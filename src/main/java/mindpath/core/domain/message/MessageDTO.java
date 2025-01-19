package mindpath.core.domain.message;

import java.time.LocalDateTime;

public record MessageDTO(
        Long id,
        String content,
        String sender,
        String receiver,
        LocalDateTime timestamp
) {
}
