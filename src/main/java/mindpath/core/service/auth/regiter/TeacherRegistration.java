package mindpath.core.service.auth.regiter;

import mindpath.config.AuthenticationRoles;
import mindpath.core.domain.auth.register.RegisterUserDTO;
import mindpath.core.domain.auth.teacher.Teacher;
import org.springframework.stereotype.Service;

@Service
public class TeacherRegistration extends UserRegistration<Teacher>{
    @Override
    protected Teacher createUser(RegisterUserDTO registerUserDTO) {
        return Teacher.builder()
                .fullName(registerUserDTO.getFullName())
                .phoneNumber(registerUserDTO.getPhoneNumber())
                .email(registerUserDTO.getEmail())
                .birthday(registerUserDTO.getBirthday())
                .governorate(registerUserDTO.getGovernorate())
                .build();

    }

    @Override
    protected String getRole() {
        return AuthenticationRoles.ROLE_TEACHER;
    }
}
