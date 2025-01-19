package mindpath.core.repository;

import mindpath.core.domain.role.Role;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    @Query(value = "SELECT R FROM Role R WHERE R.id = :id")
    Optional<Role> fetchRoleById(@Param("id") final Long id);

    @Query(value = "SELECT R FROM Role R WHERE R.name = :name")
    Optional<Role> fetchRoleByName(@Param("name") final String name);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM Role R WHERE R.id = :id")
    void removeRoleById(@Param("id") final Long id);

}
