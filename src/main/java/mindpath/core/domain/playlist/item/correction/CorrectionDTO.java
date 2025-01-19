package mindpath.core.domain.playlist.item.correction;

public record CorrectionDTO (
        Long id,
        String title,
        String url,
        boolean isCompleted,
        boolean isFailed
){
}
