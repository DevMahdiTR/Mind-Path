package mindpath.core.service.subject;

import mindpath.core.domain.subject.Subject;
import mindpath.core.domain.subject.SubjectDTO;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface SubjectService {

    SubjectDTO createSubject(
            @NotNull UserDetails userDetails,
            @NotNull Long groupId,
            @NotNull String subjectDTOJson,
            @NotNull MultipartFile mainImage,
            @NotNull MultipartFile backgroundImage
    ) throws IOException;

    SubjectDTO updateSubject(
            @NotNull UserDetails userDetails,
            @NotNull Long subjectId,
            @NotNull String subjectDTOJson,
            MultipartFile mainImage,
            MultipartFile backgroundImage
    ) throws IOException;

    SubjectDTO getSubjectById(UserDetails userDetails,Long subjectId);

    List<SubjectDTO> getSubjectsByGroupId(UserDetails userDetails, Long groupId);

    List<SubjectDTO> allSubjects(@NotNull UserDetails userDetails);
    List<SubjectDTO> findAllSubjects();

    Subject fetchSubjectById(Long subjectId);

    void deleteSubject(UserDetails userDetails, Long subjectId);
    void deleteSubject(Subject subject);
    void deleteSubjectAsync(Subject subject);

}
