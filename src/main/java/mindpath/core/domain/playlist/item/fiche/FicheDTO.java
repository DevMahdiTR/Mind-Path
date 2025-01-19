package mindpath.core.domain.playlist.item.fiche;

public record FicheDTO (
        Long id,
        String title,
        String url,
        boolean isCompleted,
        boolean isFailed
){
}
