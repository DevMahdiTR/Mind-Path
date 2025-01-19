package mindpath.core.domain.playlist.item.exercice;

public record ExerciceDTO (
        Long id,
        String title,
        String url,
        boolean isCompleted,
        boolean isFailed
){
}

