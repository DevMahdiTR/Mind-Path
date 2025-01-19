package mindpath.core.rest;

import mindpath.config.APIRouters;
import mindpath.core.domain.playlist.PlayListDTO;
import mindpath.core.domain.playlist.item.ItemDTO;
import mindpath.core.service.playlist.PlayListService;
import mindpath.core.utility.CustomerResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@RestController
@RequestMapping(APIRouters.PLAY_LIST_ROUTER)
@Slf4j
public class PlayListController {

    private final PlayListService playListService;

    public PlayListController(PlayListService playListService) {
        this.playListService = playListService;
    }

    @PostMapping()
    public CustomerResponse<String> addPlayListToSubject(
            @AuthenticationPrincipal final UserDetails userDetails,
            @RequestParam(value = "subjectId", required = true) final Long subjectId,
            @RequestBody final PlayListDTO playListDTO
    ){
        return new CustomerResponse<>(playListService.addPlayListToSubject(userDetails,subjectId,playListDTO), HttpStatus.OK);
    }

    @PutMapping("/{playListId}")
    public CustomerResponse<String> editPlayList(
            @AuthenticationPrincipal final UserDetails userDetails,
            @PathVariable(value = "playListId") final Long playListId,
            @RequestParam(value = "subjectId", required = true) final Long subjectId,
            @RequestBody final PlayListDTO playListDTO
    ){
        return new CustomerResponse<>(playListService.editPlayList(userDetails,playListId,subjectId,playListDTO), HttpStatus.OK);
    }

    @PutMapping("/{playListId}/upload")
    public CustomerResponse<ItemDTO> uploadItemToPlayList(
            @AuthenticationPrincipal final UserDetails userDetails,
            @PathVariable(value = "playListId") final Long playListId,
            @RequestParam(value = "subjectId") final Long subjectId,
            @RequestParam(value = "uploadItemRequestJson") final String uploadItemRequestJson,
            @RequestParam(value = "file") final MultipartFile file
    ) throws IOException {

        return new CustomerResponse<>(playListService.uploadItemToPlayList(userDetails,playListId,subjectId,uploadItemRequestJson,file),HttpStatus.OK);
    }

    @PutMapping("/url")
    public CustomerResponse<String> updatedItemUrl(
            @AuthenticationPrincipal final UserDetails userDetails,
            @RequestParam(value = "subjectId") final Long subjectId,
            @RequestParam(value = "itemId") final Long itemId,
            @RequestParam(value = "url") final String url,
            @RequestParam(value = "category") final String category
    ){
        playListService.updatedItemUrl(userDetails,subjectId,itemId,url,category);
        return new CustomerResponse<>("Item url updated successfully",HttpStatus.OK);
    }

    @GetMapping("/{playListId}")
    public CustomerResponse<PlayListDTO> getPlayListById(
            @PathVariable(value = "playListId") final Long playListId
    ){
        return new CustomerResponse<>(playListService.fetchPlayListById(playListId),HttpStatus.OK);
    }

    @DeleteMapping("/{playListId}")
    public CustomerResponse<String> deletePlayListById(
            @AuthenticationPrincipal final UserDetails userDetails,
            @PathVariable(value = "playListId") final Long playListId
    ){
        playListService.deletePlayList(userDetails,playListId);
        return new CustomerResponse<>("PlayList deleted successfully",HttpStatus.OK);
    }

    @DeleteMapping("/{playListId}/item/{itemId}")
    public CustomerResponse<String> deleteItemFromPlayList(
            @AuthenticationPrincipal final UserDetails userDetails,
            @PathVariable(value = "playListId") final Long playListId,
            @PathVariable(value = "itemId") final Long itemId
    ){
        playListService.deleteItemFromPlayList(userDetails,playListId,itemId);
        return new CustomerResponse<>("Item deleted successfully",HttpStatus.OK);
    }
}
