package mindpath.core.domain.auth.superteacher;


import mindpath.core.domain.auth.user.TunisianGovernorate;
import mindpath.core.domain.auth.user.UserDTO;
import mindpath.core.domain.role.Role;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record SuperTeacherDTO (
        UUID id,
        String fullName,
        String phoneNumber,
        String email,
        TunisianGovernorate governorate,
        LocalDate birthday,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Role role,
        boolean isEnabled
) implements UserDTO {

    @Override
    public UUID getId() {
        return this.id;
    }

    @Override
    public String getFullName() {
        return this.fullName;
    }

    @Override
    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    @Override
    public String getEmail() {
        return this.email;
    }

    @Override
    public TunisianGovernorate getGovernorate() {
        return this.governorate;
    }

    @Override
    public LocalDate getBirthday() {
        return this.birthday;
    }

    @Override
    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    @Override
    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    @Override
    public Role getRole() {
        return this.role;
    }
}
