package mindpath.core.domain.playlist.item;

public record ItemDTO (
    Long id,
    String title,
    String url,
    boolean isCompleted,
    boolean isFailed
){

}
