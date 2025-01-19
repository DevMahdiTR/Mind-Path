package mindpath.core.service.auth.regiter;

import mindpath.config.AuthenticationRoles;
import mindpath.core.domain.auth.register.RegisterStudentDTO;
import mindpath.core.domain.auth.register.RegisterUserDTO;
import mindpath.core.domain.auth.student.Student;
import org.springframework.stereotype.Service;

@Service
public class StudentRegistration extends UserRegistration<Student>{
    @Override
    protected Student createUser(RegisterUserDTO registerUserDTO) {
        final RegisterStudentDTO studentDTO = (RegisterStudentDTO) registerUserDTO;
        return Student.builder()
                .fullName(registerUserDTO.getFullName())
                .phoneNumber(registerUserDTO.getPhoneNumber())
                .email(registerUserDTO.getEmail())
                .birthday(registerUserDTO.getBirthday())
                .governorate(registerUserDTO.getGovernorate())
                .educationLevel(studentDTO.getEducationLevel())
                .build();
    }

    @Override
    protected String getRole() {
        return AuthenticationRoles.ROLE_STUDENT;
    }
}
