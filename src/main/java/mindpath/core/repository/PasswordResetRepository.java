package mindpath.core.repository;

import mindpath.core.domain.PasswordReset;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PasswordResetRepository  extends JpaRepository<PasswordReset,Integer> {

    @Query(value = "SELECT PR FROM PasswordReset PR WHERE PR.email = :email  AND PR.expiryAt > CURRENT_TIMESTAMP")
    Optional<PasswordReset> findByEmailAndToken(@Param("email") String email);


    @Transactional
    @Modifying
    @Query(value = "DELETE FROM PasswordReset PR WHERE PR.id = :id")
    void deleteById(@Param("id") long id);

}
