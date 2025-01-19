package mindpath.core.domain.group;

import mindpath.core.domain.auth.user.EducationLevel;
import mindpath.core.domain.subject.SubjectDTO;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record GroupDTO(
        long id,
        @Size(min = 3, max = 50, message = "Le titre doit comporter entre 3 et 50 caractères")
        String title,
        String backgroundImageUrl,
        String mainImageUrl,
        EducationLevel educationLevel,
        Boolean isPublic,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<UUID> studentsIds,
        UUID superTeacherId,
        String superTeacherFullName,
        List<SubjectDTO> subjects
){
}
