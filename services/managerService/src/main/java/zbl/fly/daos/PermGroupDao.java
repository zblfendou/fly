package zbl.fly.daos;

import org.springframework.data.jpa.repository.JpaRepository;
import zbl.fly.models.PermGroup;

import java.util.List;

public interface PermGroupDao extends JpaRepository<PermGroup, String> {

    List<PermGroup> findAllByParentIsNullOrderByOrder();
}
