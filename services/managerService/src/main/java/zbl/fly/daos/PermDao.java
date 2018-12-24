package zbl.fly.daos;

import org.springframework.data.jpa.repository.JpaRepository;
import zbl.fly.models.Permission;

public interface PermDao extends JpaRepository<Permission, String> {
}
