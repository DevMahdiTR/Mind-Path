package mindpath.core.mapper;

import mindpath.core.domain.auth.superteacher.SuperTeacher;
import mindpath.core.domain.auth.superteacher.SuperTeacherDTO;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class SuperTeacherDTOMapper implements Function<SuperTeacher , SuperTeacherDTO> {
    @Override
    public SuperTeacherDTO apply(SuperTeacher superTeacher) {
        return new SuperTeacherDTO(
                superTeacher.getId(),
                superTeacher.getFullName(),
                superTeacher.getPhoneNumber(),
                superTeacher.getEmail(),
                superTeacher.getGovernorate(),
                superTeacher.getBirthday(),
                superTeacher.getCreatedAt(),
                superTeacher.getUpdatedAt(),
                superTeacher.getRole(),
                superTeacher.isEnabled()
        );
    }
}
