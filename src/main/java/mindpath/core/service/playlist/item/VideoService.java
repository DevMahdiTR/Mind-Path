package mindpath.core.service.playlist.item;

import mindpath.core.domain.playlist.PlayList;
import mindpath.core.domain.playlist.item.video.Video;
import mindpath.core.repository.playlist.item.VideoRepository;
import org.springframework.stereotype.Service;

@Service
public class VideoService implements ItemService<Video> {

    private final VideoRepository videoRepository;

    public VideoService(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
    }

    @Override
    public Video addItemToPlayList(Video video, PlayList playList) {
        video.setPlayList(playList);
        return videoRepository.save(video);
    }

    @Override
    public Video deleteItemFromPlayList(Video item) {
        videoRepository.deleteById(item.getId());
        return item;
    }

    @Override
    public Video updateItem(Video item) {
        return videoRepository.save(item);
    }

    @Override
    public Video getItemById(Long itemId) {
        return videoRepository.findVideoById(itemId).orElseThrow(() -> new IllegalArgumentException("La vidéo n'existe pas"));
    }
}
