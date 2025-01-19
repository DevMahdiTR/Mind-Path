package mindpath.core.domain.auth.user;

import mindpath.core.domain.role.Role;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public interface UserDTO {
    UUID getId();
    String getFullName();
    String getPhoneNumber();
    String getEmail();
    TunisianGovernorate getGovernorate();
    LocalDate getBirthday();
    LocalDateTime getCreatedAt();
    LocalDateTime getUpdatedAt();
    boolean isEnabled();
    Role getRole();
}
