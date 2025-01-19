package mindpath.core.mapper;


import mindpath.core.domain.auth.user.UserEntity;
import mindpath.core.domain.auth.user.UserEntityDTO;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;


import java.util.function.Function;

@Service
public class UserEntityDTOMapper implements Function<UserEntity, UserEntityDTO> {
    @Override
    public UserEntityDTO apply(@NotNull UserEntity userEntity) {
        return new UserEntityDTO(
                userEntity.getId(),
                userEntity.getFullName(),
                userEntity.getPhoneNumber(),
                userEntity.getEmail(),
                userEntity.getGovernorate(),
                userEntity.getBirthday(),
                userEntity.getCreatedAt(),
                userEntity.getUpdatedAt(),
                userEntity.getRole(),
                userEntity.isEnabled()
        );
    }
}
