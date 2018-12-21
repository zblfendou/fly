package zbl.fly.daos;

import org.springframework.data.jpa.repository.JpaRepository;
import zbl.fly.models.Manager;

public interface ManagerDao extends JpaRepository<Manager, Long> {
}
