package mindpath.core.service.superteacher;

import mindpath.core.domain.auth.student.StudentDTO;
import mindpath.core.domain.auth.teacher.TeacherDTO;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.UUID;

public interface SuperTeacherService {
    List<TeacherDTO> getAllTeacher(final UserDetails userDetails);
    List<StudentDTO> getAllStudent(final UserDetails userDetails);
    String addTeacherToSuperTeacher(UserDetails userDetails,final UUID teacherId);
    String removeTeacherFromSuperTeacher(UserDetails userDetails,final UUID teacherId);
}
