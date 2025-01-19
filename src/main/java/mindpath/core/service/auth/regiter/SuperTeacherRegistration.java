package mindpath.core.service.auth.regiter;

import mindpath.config.AuthenticationRoles;
import mindpath.core.domain.auth.register.RegisterUserDTO;
import mindpath.core.domain.auth.superteacher.SuperTeacher;
import org.springframework.stereotype.Service;

@Service
public class SuperTeacherRegistration extends UserRegistration<SuperTeacher> {
    @Override
    protected SuperTeacher createUser(RegisterUserDTO registerUserDTO) {
        return SuperTeacher.builder()
                .fullName(registerUserDTO.getFullName())
                .phoneNumber(registerUserDTO.getPhoneNumber())
                .email(registerUserDTO.getEmail())
                .birthday(registerUserDTO.getBirthday())
                .governorate(registerUserDTO.getGovernorate())
                .build();
    }

    @Override
    protected String getRole() {
        return AuthenticationRoles.ROLE_SUPER_TEACHER;
    }
}
