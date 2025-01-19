package mindpath.core.service.role;


import mindpath.core.domain.role.Role;

public interface RoleService {

    Role fetchRoleByName(final String name);

    Role deleteRoleById(final Long id);

    Role updateRoleById(final Long id, final Role role);

    Role save(final Role role);

    Role getRoleById(final Long id);
}
