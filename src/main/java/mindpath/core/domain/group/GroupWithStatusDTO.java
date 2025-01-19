package mindpath.core.domain.group;

import mindpath.core.domain.auth.user.EducationLevel;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GroupWithStatusDTO {
    private long id;
    private String title;
    private String backgroundImageUrl;
    private String mainImageUrl;
    private boolean isActive;
    private Boolean isPublic;
    private String superTeacherFullName;
    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;
    private EducationLevel educationLevel;
}