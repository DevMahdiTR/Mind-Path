package mindpath.core.domain.subject;

import mindpath.core.domain.playlist.PlayListDTO;
import mindpath.core.domain.subject.section.SectionDTO;

import java.util.List;
import java.util.UUID;

public record SubjectDTO (
        Long id,
        String speciality,
        String level,
        String backgroundImageUrl,
        String mainImageUrl,
        List<SectionDTO> sections,
        UUID teacherId,
        List<PlayListDTO> playLists,
        String superTeacherFullName,
        Long groupId
) {
}
