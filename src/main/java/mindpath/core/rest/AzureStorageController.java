package mindpath.core.rest;


import mindpath.config.APIRouters;
import mindpath.core.service.firstbasestorage.AzureStorageService;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(APIRouters.AZURE_STORAGE_ROUTER)
public class AzureStorageController {


    private final AzureStorageService azureStorageService;

    public AzureStorageController(AzureStorageService azureStorageService) {
        this.azureStorageService = azureStorageService;
    }


    @GetMapping(value = "/stream-video")
    public ResponseEntity<Resource> streamVideo(
            @RequestParam(name = "blobName", required = true) String blobName,
            @RequestHeader(value = "Range", required = true) String httpRangeList
    ) {
        return azureStorageService.streamVideo(blobName, httpRangeList);
    }
}
