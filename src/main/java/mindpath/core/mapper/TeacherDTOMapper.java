package mindpath.core.mapper;

import mindpath.core.domain.auth.teacher.Teacher;
import mindpath.core.domain.auth.teacher.TeacherDTO;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class TeacherDTOMapper implements Function<Teacher, TeacherDTO> {
    @Override
    public TeacherDTO apply(Teacher teacher) {
        return new TeacherDTO(
                teacher.getId(),
                teacher.getFullName(),
                teacher.getPhoneNumber(),
                teacher.getEmail(),
                teacher.getGovernorate(),
                teacher.getBirthday(),
                teacher.getCreatedAt(),
                teacher.getUpdatedAt(),
                teacher.getRole(),
                teacher.isEnabled()
        );
    }
}
