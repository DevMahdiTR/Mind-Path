package mindpath.core.service.playlist;

import mindpath.core.domain.playlist.PlayList;
import mindpath.core.domain.playlist.PlayListDTO;
import mindpath.core.domain.playlist.item.ItemDTO;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface PlayListService {

    String addPlayListToSubject(
            @NotNull UserDetails userDetails,
            @NotNull Long subjectId,
            @NotNull PlayListDTO playListDTO
    );

    String editPlayList(
            @NotNull UserDetails userDetails,
            @NotNull Long playListId,
            @NotNull Long subjectId,
            @NotNull PlayListDTO playListDTO
    );

    ItemDTO uploadItemToPlayList(
            @NotNull UserDetails userDetails,
            @NotNull Long playListId,
            @NotNull Long subjectId,
            @NotNull String uploadItemRequestJson,
            @NotNull MultipartFile file
    ) throws IOException;

    void updatedItemUrl(@NotNull UserDetails userDetails, @NotNull Long subjectId, @NotNull Long itemId, @NotNull String url, @NotNull String category);

    PlayListDTO fetchPlayListById(@NotNull Long playListId);

    PlayList getPlayListById(@NotNull Long playListId);

    void deleteItemFromPlayList(@NotNull UserDetails userDetails, @NotNull Long playListId, @NotNull Long itemId);

    void deletePlayList(@NotNull UserDetails userDetails, @NotNull Long playListId);
    void deletePlayListAsync(@NotNull PlayList playList);
    void deletePlayList(PlayList playList);

}
