package mindpath.core.domain.playlist.item.qcm;

public record QcmDTO  (
        Long id,
        String title,
        String url,
        boolean isCompleted,
        boolean isFailed
){
}

