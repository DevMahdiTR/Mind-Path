package mindpath.core.domain.playlist;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UploadItemRequest {
    private String category;
    private String title;
}
