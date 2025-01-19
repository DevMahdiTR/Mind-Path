package mindpath.core.service.role;

import mindpath.core.domain.role.Role;
import mindpath.core.exceptions.custom.DatabaseException;
import mindpath.core.exceptions.custom.ResourceNotFoundException;
import mindpath.core.repository.RoleRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Role fetchRoleByName(String name) {
        log.debug("Fetching role by name: {}", name);
        return roleRepository.fetchRoleByName(name).orElseThrow(
                () -> new ResourceNotFoundException("Le rôle avec le nom %s n'a pas pu être trouvé.".formatted(name))
        );
    }

    @Transactional
    @Override
    public Role deleteRoleById(Long id) {
        log.debug("Deleting role by ID: {}", id);
        Role existingRole = getRoleById(id);
        roleRepository.removeRoleById(id);
        log.info("Role deleted successfully: {}", existingRole);
        return existingRole;
    }

    @Override
    public Role updateRoleById(Long id, @NotNull Role role) {
        log.debug("Updating role by ID: {} with data: {}", id, role);
        Role existingRole = getRoleById(id);
        role.setId(existingRole.getId());
        Role updatedRole = this.save(role);
        log.info("Role updated successfully: {}", updatedRole);
        return updatedRole;
    }

    @Override
    public Role save(Role role) {
        try {
            log.debug("Saving role: {}", role);
            Role savedRole = roleRepository.save(role);
            log.info("Role saved successfully: {}", savedRole);
            return savedRole;
        } catch (Exception e) {
            log.error("Error saving role: {}", role, e);
            throw new DatabaseException("Erreur lors de l'exécution de la requête de base de données.");
        }
    }

    @Override
    public Role getRoleById(Long id) {
        log.debug("Fetching role by ID: {}", id);
        return roleRepository.fetchRoleById(id).orElseThrow(
                () -> new ResourceNotFoundException("Le rôle avec le ID %d n'a pas pu être trouvé.".formatted(id))
        );
    }
}
