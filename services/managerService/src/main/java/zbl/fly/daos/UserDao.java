package zbl.fly.daos;

import org.springframework.data.jpa.repository.JpaRepository;
import zbl.fly.models.User;

public interface UserDao extends JpaRepository<User, Long> {
}
