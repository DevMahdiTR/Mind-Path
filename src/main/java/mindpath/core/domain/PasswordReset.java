package mindpath.core.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "password_reset")
public class PasswordReset {

    @SequenceGenerator(
            name = "password_reset_sequence",
            sequenceName = "password_reset_sequence",
            allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "password_reset_sequence")
    @Id
    private long id;

    @Column(name = "email",nullable = false)
    private String email;

    @Column(name = "token",nullable = false)
    private String token;

    @Column(name = "created_at",nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "expiry_at",nullable = false)
    private LocalDateTime expiryAt;

}
