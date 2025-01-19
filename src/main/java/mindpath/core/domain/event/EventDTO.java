package mindpath.core.domain.event;

import java.time.LocalDateTime;

public record EventDTO (
        Long id,
        String title,
        LocalDateTime startTime,
        LocalDateTime endTime,
        String backgroundColor,
        Long groupId
){
}
