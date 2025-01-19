package mindpath.core.service.user;

import mindpath.core.domain.auth.student.Student;
import mindpath.core.domain.auth.superteacher.SuperTeacher;
import mindpath.core.domain.auth.teacher.Teacher;
import mindpath.core.domain.auth.user.UserDTO;
import mindpath.core.domain.auth.user.UserEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.UUID;

public interface UserEntityService {

    UserDTO getCurrentUser(final UserDetails userDetails);
    UserDTO getUserByUUID(final UUID userId);
    String enabledOrDisabledUser(final Authentication authentication, final UUID userId, final boolean isEnabled);
    UserDTO mapper(final UserEntity userEntity);
    List<UserDTO> mapper(final List<UserEntity> userEntities);
    UserEntity save(final UserEntity userEntity);
    UserEntity saveAndFlush(final UserEntity userEntity);
    UserEntity fetchUserByUUID(final UUID userId);
    UserEntity fetchUserByEmail(final String email);
    Boolean isEmailRegistered(final String email);
    Boolean isPhoneNumberRegistered(final String phoneNumber);
    List<UserDTO> filterUser(
            final String fullName,
            final String email,
            final String role,
            final String phoneNumber,
            final Boolean isEnabled
    );

    List<UserEntity> filterUsers(
            String fullName,
            String email,
            String role,
            String phoneNumber,
            Boolean isEnabled
    );


    SuperTeacher validateSuperTeacher(final UserEntity userEntity);
    Teacher validateTeacher(final UserEntity userEntity);
    Student validateStudent(final UserEntity userEntity);





}
