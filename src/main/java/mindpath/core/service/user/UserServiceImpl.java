package mindpath.core.service.user;

import mindpath.core.domain.auth.student.Student;
import mindpath.core.domain.auth.superteacher.SuperTeacher;
import mindpath.core.domain.auth.teacher.Teacher;
import mindpath.core.domain.auth.user.UserDTO;
import mindpath.core.domain.auth.user.UserEntity;
import mindpath.core.exceptions.custom.DatabaseException;
import mindpath.core.exceptions.custom.ResourceNotFoundException;
import mindpath.core.mapper.StudentDTOMapper;
import mindpath.core.mapper.SuperTeacherDTOMapper;
import mindpath.core.mapper.TeacherDTOMapper;
import mindpath.core.mapper.UserEntityDTOMapper;
import mindpath.core.repository.UserEntityRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Service
@Slf4j
public class UserServiceImpl implements UserEntityService {

    private final UserEntityRepository userEntityRepository;
    private final UserEntityDTOMapper userEntityDTOMapper;
    private final SuperTeacherDTOMapper superTeacherDTOMapper;
    private final TeacherDTOMapper teacherDTOMapper;
    private final StudentDTOMapper studentDTOMapper;


    public UserServiceImpl(UserEntityRepository userEntityRepository, UserEntityDTOMapper userEntityDTOMapper, SuperTeacherDTOMapper superTeacherDTOMapper, TeacherDTOMapper teacherDTOMapper, StudentDTOMapper studentDTOMapper) {
        this.userEntityRepository = userEntityRepository;
        this.userEntityDTOMapper = userEntityDTOMapper;
        this.superTeacherDTOMapper = superTeacherDTOMapper;
        this.teacherDTOMapper = teacherDTOMapper;
        this.studentDTOMapper = studentDTOMapper;
    }

    @Override
    public UserDTO getCurrentUser(UserDetails userDetails) {
        UserEntity currentUser = fetchUserByEmail(userDetails.getUsername());
        return mapper(currentUser);
    }

    @Override
    public UserDTO getUserByUUID(UUID userId) {
        UserEntity currentUser = fetchUserByUUID(userId);
        return mapper(currentUser);
    }

    @Override
    public String enabledOrDisabledUser(Authentication authentication, UUID userId, boolean isEnabled) {
        if (authentication == null) {
            log.error("Unauthorized action: User is not authenticated");
            throw new ResourceNotFoundException("Action non autorisée : L'utilisateur n'est pas authentifié.");
        }
        String currentRole = authentication.getAuthorities().iterator().next().getAuthority();
        final UserEntity currentUser = this.fetchUserByUUID(userId);
//        if (!RoleCapabilities.canCreateRole(currentRole, currentUser.getRole().getName())) {
//            throw new UnauthorizedActionException("Unauthorized action");
//        }

        currentUser.setEnabled(isEnabled);
        save(currentUser);

        return "Utilisateur %s avec succès".formatted(isEnabled ? "activé" : "désactivé");

    }


    @Override
    public UserDTO mapper(UserEntity userEntity) {

        return switch (userEntity.getClass().getSimpleName()) {
            case "SuperTeacher" -> superTeacherDTOMapper.apply((SuperTeacher) userEntity);
            case "Teacher" -> teacherDTOMapper.apply((Teacher) userEntity);
            case "Student" -> studentDTOMapper.apply((Student) userEntity);
            default -> userEntityDTOMapper.apply(userEntity);
        };
    }

    @Override
    public List<UserDTO> mapper(List<UserEntity> userEntities) {
        return userEntities.stream().map(this::mapper).toList();
    }

    @Override
    public UserEntity save(UserEntity userEntity) {
        try {
            log.info("Database Request to save User: {}", userEntity);
            final UserEntity savedUser = userEntityRepository.save(userEntity);
            log.info("{} saved successfully", savedUser);
            return savedUser;
        } catch (Exception e) {
            log.error("Error saving User: {}", e.getMessage(), e);
            throw new DatabaseException("Erreur lors de l'exécution de la requête de base de données.");
        }
    }

    @Override
    public UserEntity saveAndFlush(UserEntity userEntity) {
        try {
            log.info("Database Request to save User: {}", userEntity);
            final UserEntity savedUser = userEntityRepository.saveAndFlush(userEntity);
            log.info("{} saved successfully", savedUser);
            return savedUser;
        } catch (Exception e) {
            log.error("Error saving User: {}", e.getMessage(), e);
            throw new DatabaseException("Erreur lors de l'exécution de la requête de base de données.");
        }
    }

    public UserEntity fetchUserByUUID(final UUID userId) {
        return userEntityRepository.fetchUserById(userId).orElseThrow(
                () -> new ResourceNotFoundException("L'utilisateur avec l'ID %s n'a pas pu être trouvé dans notre système.".formatted(userId))
        );
    }


    @Override
    public UserEntity fetchUserByEmail(String email) {
        return userEntityRepository.fetchUserByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException("L'utilisateur avec l'EMAIL %s n'a pas pu être trouvé dans notre système.".formatted(email))
        );
    }


    @Override
    public Boolean isEmailRegistered(final String email) {
        return userEntityRepository.isEmailRegistered(email);
    }

    @Override
    public Boolean isPhoneNumberRegistered(String phoneNumber) {
        return userEntityRepository.isPhoneNumberRegistered(phoneNumber);
    }

    @Override
    public List<UserDTO> filterUser(String fullName, String email, String role, String phoneNumber, Boolean isEnabled) {
        List<UserEntity> users = userEntityRepository.findUsers(fullName, email, role, phoneNumber, isEnabled);
        return mapper(users);
    }

    @Override
    public List<UserEntity> filterUsers(String fullName, String email, String role, String phoneNumber, Boolean isEnabled) {
      return userEntityRepository.findUsers(fullName, email, role, phoneNumber, isEnabled);

    }
    @Override
    public SuperTeacher validateSuperTeacher(UserEntity userEntity){
        if (userEntity instanceof SuperTeacher superTeacher) {
            return superTeacher;
        }
        throw new ResourceNotFoundException("L'utilisateur n'est pas un Enseignant Responsable.");
    }

    @Override
    public Teacher validateTeacher(UserEntity userEntity) {
        if (userEntity instanceof Teacher teacher) {
            return teacher;
        }
        throw new ResourceNotFoundException("L'utilisateur n'est pas un Enseignant.");
    }

    @Override
    public Student validateStudent(UserEntity userEntity) {
        if (userEntity instanceof Student student) {
            return student;
        }
        throw new ResourceNotFoundException("L'utilisateur n'est pas un Super Etudiant.");
    }


}
