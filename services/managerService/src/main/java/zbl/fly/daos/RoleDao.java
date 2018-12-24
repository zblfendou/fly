package zbl.fly.daos;

import org.springframework.data.jpa.repository.JpaRepository;
import zbl.fly.models.Permission;
import zbl.fly.models.Role;

import java.util.List;

public interface RoleDao extends JpaRepository<Role, String> {
    Role findByRoleText(String roleText);

    List<Role> findBySuperVisorIsFalse();

    List<Role> findByPermissionsContaining(Permission permission);
}
