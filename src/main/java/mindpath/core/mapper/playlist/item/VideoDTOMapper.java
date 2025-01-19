package mindpath.core.mapper.playlist.item;

import mindpath.core.domain.playlist.item.video.Video;
import mindpath.core.domain.playlist.item.video.VideoDTO;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class VideoDTOMapper implements Function<Video, VideoDTO> {
    @Override
    public VideoDTO apply(Video video) {
        return new VideoDTO(
                video.getId(),
                video.getTitle(),
                video.getUrl(),
                video.isCompleted(),
                video.isFailed()
        );
    }
}
