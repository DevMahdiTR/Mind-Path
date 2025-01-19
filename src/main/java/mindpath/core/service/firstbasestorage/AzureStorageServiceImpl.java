package mindpath.core.service.firstbasestorage;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobRange;
import com.azure.storage.blob.models.BlobStorageException;
import com.azure.storage.blob.options.BlobInputStreamOptions;
import com.azure.storage.blob.sas.BlobSasPermission;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;
import com.azure.storage.blob.specialized.BlobInputStream;
import com.azure.storage.common.sas.SasProtocol;
import com.google.cloud.storage.*;
import mindpath.core.domain.playlist.item.Item;
import mindpath.core.service.playlist.item.ItemService;
import mindpath.core.service.playlist.item.ItemServiceFactory;
import mindpath.security.utility.FirebaseStorageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
public class AzureStorageServiceImpl implements AzureStorageService {


    @Value("${spring.cloud.azure.storage.blob.container-name}")
    private String containerName;

    @Value("${spring.cloud.azure.storage.blob.connection-string}")
    private String connectionString;

    private final ItemServiceFactory itemServiceFactory;

    private BlobServiceClient blobServiceClient;

    public AzureStorageServiceImpl(ItemServiceFactory itemServiceFactory) {
        this.itemServiceFactory = itemServiceFactory;
    }

    @PostConstruct
    public void init() {
        blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(connectionString)
                .buildClient();
    }


    @Override
    public String uploadFile(MultipartFile file, String directory) throws IOException {
        log.debug("Uploading file: {}", file.getOriginalFilename());
        if (file.isEmpty() || directory == null || directory.trim().isEmpty()) {
            throw new IllegalArgumentException("Le fichier ou le répertoire ne peut pas être nul ou vide");
        }
        String blobName = directory + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();
        BlobClient blobClient = blobServiceClient
                .getBlobContainerClient(containerName)
                .getBlobClient(blobName);
        blobClient.upload(file.getInputStream(), file.getSize(), true);
        log.debug("File uploaded successfully: {}", file.getOriginalFilename());
        return blobClient.getBlobUrl();
    }

    private BlobClient getBlobClient(String blobName) {
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(connectionString)
                .buildClient();
        return blobServiceClient.getBlobContainerClient(containerName).getBlobClient(blobName);
    }

    @Override
    public ResponseEntity<Resource> streamVideo(String blobName, String httpRangeList) {
        try {
            BlobClient blobClient = getBlobClient(blobName);
            long fileSize = blobClient.getProperties().getBlobSize();
            long start = 0;
            long length = fileSize;

            // Parse Range header if it exists
            if (httpRangeList != null) {
                List<HttpRange> httpRanges = HttpRange.parseRanges(httpRangeList);
                if (!httpRanges.isEmpty()) {
                    HttpRange range = httpRanges.get(0);
                    start = range.getRangeStart(fileSize);
                    long end = range.getRangeEnd(fileSize);
                    length = end - start + 1;
                }
            }

            BlobInputStreamOptions options = new BlobInputStreamOptions()
                    .setRange(new BlobRange(start, length));
            BlobInputStream inputStream = blobClient.openInputStream(options);
            InputStreamResource inputStreamResource = new InputStreamResource(inputStream);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Range", "bytes " + start + "-" + (start + length - 1) + "/" + fileSize);
            headers.add("Accept-Ranges", "bytes");
            headers.setContentLength(length);
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            return new ResponseEntity<>(inputStreamResource, headers, HttpStatus.PARTIAL_CONTENT);

        } catch (BlobStorageException e) {
            log.error("BlobStorageException occurred: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            log.error("General exception occurred: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



    @Override
    @Async("uploadTaskExecutor")
    public void uploadLargeFile(MultipartFile file, long fileSize, String directory, Item item) {
        log.debug("Starting upload for file: {}", file.getOriginalFilename());

        validateFileAndDirectory(file, directory);

        String blobName = generateBlobName(directory, Objects.requireNonNull(file.getOriginalFilename()));
        BlobClient blobClient = createBlobClient(blobName);

        try (InputStream inputStream = file.getInputStream()) {
            uploadToBlob(blobClient, inputStream, fileSize);
            if(directory.equals("video")) {
                handleSuccess(item, "https://devfoknje7ik.com:8081/api/v1/azure-storage/stream-video?blobName=" + blobName);
            }
            else {
                handleSuccess(item, blobClient.getBlobUrl());
            }
        } catch (IOException | BlobStorageException e) {
            handleFailure(item, e, file.getOriginalFilename());
        }
    }


    @Override
    public void deleteFile(String fileUrl) {
        log.debug("Deleting file: {}", fileUrl);
        if (fileUrl == null || fileUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("File URL cannot be null or empty");
        }

        try {
            String filePath = FirebaseStorageUtils.extractPathFromUrl(fileUrl);
            log.error("file path after :  " + filePath);
            if (filePath == null || filePath.trim().isEmpty()) {
                throw new IllegalArgumentException("Invalid file URL: " + fileUrl);
            }
            BlobClient blobClient = blobServiceClient
                    .getBlobContainerClient(containerName)
                    .getBlobClient(filePath);

            if (blobClient.exists()) {
                blobClient.delete();
                log.debug("File deleted successfully: {}", fileUrl);
                return;
            }

            throw new RuntimeException("Failed to delete file: " + filePath);

        } catch (MalformedURLException e) {
            System.err.println("MalformedURLException occurred while parsing URL: " + e.getMessage());
            throw new RuntimeException("Invalid file URL format: " + fileUrl, e);
        } catch (StorageException e) {
            System.err.println("StorageException occurred while deleting file: " + e.getMessage());
            throw new RuntimeException("Failed to delete file due to storage issue: " + fileUrl, e);
        } catch (Exception e) {
            System.err.println("Exception occurred while deleting file: " + e.getMessage());
            throw new RuntimeException("Unexpected error occurred while deleting file: " + fileUrl, e);
        }
    }

    @Override
    public String generateSasToken(String blobName) {
        log.debug("Generating SAS token for blob: {}", blobName);
        var blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(connectionString)
                .buildClient();

        var containerClient = blobServiceClient.getBlobContainerClient(containerName);

        BlobSasPermission permissions = new BlobSasPermission()
                .setReadPermission(true)
                .setWritePermission(true)
                .setDeletePermission(true);

        OffsetDateTime expiryTime = OffsetDateTime.now().plusHours(1);

        BlobServiceSasSignatureValues sasValues = new BlobServiceSasSignatureValues(expiryTime, permissions)
                .setProtocol(SasProtocol.HTTPS_ONLY);

        log.debug("SAS token generated successfully for blob: {}", blobName);
        return containerClient.getBlobClient(blobName).generateSas(sasValues);
    }


    // Helper method to validate file and directory
    private void validateFileAndDirectory(MultipartFile file, String directory) {
        if (file.isEmpty() || directory == null || directory.trim().isEmpty()) {
            throw new IllegalArgumentException("The file or directory cannot be null or empty");
        }
    }

    // Helper method to generate a unique blob name
    private String generateBlobName(String directory, String originalFilename) {
        return directory + "/" + UUID.randomUUID() + "_" + originalFilename.replaceAll("[^a-zA-Z0-9.]", "");
    }

    // Helper method to create a BlobClient instance
    private BlobClient createBlobClient(String blobName) {
        return blobServiceClient.getBlobContainerClient(containerName).getBlobClient(blobName);
    }

    // Method to upload file content to blob
    private void uploadToBlob(BlobClient blobClient, InputStream inputStream, long fileSize) throws IOException {
        blobClient.upload(inputStream, fileSize, true);
        log.debug("File uploaded to blob: {}", blobClient.getBlobUrl());
    }

    // Success handler to update the item and log success
    private void handleSuccess(Item item, String fileUrl) {
        item.setUrl(fileUrl);
        item.setCompleted(true);
        updateItem(item);
        log.debug("Upload completed for item with URL: {}", fileUrl);
    }

    // Failure handler to log the error and mark the item as failed
    private void handleFailure(Item item, Exception e, String originalFilename) {
        log.error("Failed to upload file: {}", originalFilename, e);
        item.setFailed(true);
        updateItem(item);
    }

    // Method to update the item using the item service
    private void updateItem(Item item) {
        ItemService<Item> itemService = (ItemService<Item>) itemServiceFactory.getItemService(item);
        itemService.updateItem(item);
    }

}
