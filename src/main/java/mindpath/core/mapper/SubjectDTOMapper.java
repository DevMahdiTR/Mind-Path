package mindpath.core.mapper;

import mindpath.core.domain.subject.Subject;
import mindpath.core.domain.subject.SubjectDTO;
import mindpath.core.mapper.playlist.PlayListDTOMapper;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class SubjectDTOMapper implements Function<Subject, SubjectDTO> {
    @Override
    public SubjectDTO apply(Subject subject) {
        return new SubjectDTO(
                subject.getId(),
                subject.getSpeciality(),
                subject.getLevel(),
                subject.getBackgroundImageUrl(),
                subject.getMainImageUrl(),
                subject.getSections() == null ? null : subject.getSections().stream().map(new SectionDTOMapper()).toList(),
                subject.getTeacher() == null ? null : subject.getTeacher().getId(),
                subject.getPlayLists() == null ? null : subject.getPlayLists().stream().map(new PlayListDTOMapper()).toList(),
                subject.getGroup().getSuperTeacher().getFullName(),
                subject.getGroup() == null ? null : subject.getGroup().getId()
        );
    }
}
