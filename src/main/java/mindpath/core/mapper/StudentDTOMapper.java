package mindpath.core.mapper;

import mindpath.core.domain.auth.student.Student;
import mindpath.core.domain.auth.student.StudentDTO;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class StudentDTOMapper implements Function<Student , StudentDTO> {
    @Override
    public StudentDTO apply(Student student) {
        return new StudentDTO(
                student.getId(),
                student.getFullName(),
                student.getPhoneNumber(),
                student.getEmail(),
                student.getGovernorate(),
                student.getBirthday(),
                student.getCreatedAt(),
                student.getUpdatedAt(),
                student.getRole(),
                student.getEducationLevel(),
                student.isEnabled(),
                student.getGroups() == null ? null : student.getGroups().stream().map(new GroupDTOMapper()).toList()

        );
    }
}
