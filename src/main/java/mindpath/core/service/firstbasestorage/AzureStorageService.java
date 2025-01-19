package mindpath.core.service.firstbasestorage;

import mindpath.core.domain.playlist.item.Item;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface AzureStorageService {
    String generateSasToken(String blobName);
    String uploadFile(MultipartFile file, String directory) throws IOException;
    ResponseEntity<Resource> streamVideo(String blobName, String httpRangeList);
    void uploadLargeFile(MultipartFile file,long fileSize, String directory, Item item) throws IOException;
    void deleteFile(String fileUrl);

}

