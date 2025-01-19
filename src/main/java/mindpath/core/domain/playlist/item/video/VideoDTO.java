package mindpath.core.domain.playlist.item.video;

public record VideoDTO (
        Long id,
        String title,
        String url,
        boolean isCompleted,
        boolean isFailed
){
}

