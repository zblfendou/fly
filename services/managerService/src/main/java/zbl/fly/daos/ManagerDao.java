package zbl.fly.daos;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import zbl.fly.models.Manager;
import zbl.fly.models.Role;

public interface ManagerDao extends JpaRepository<Manager, Long>, JpaSpecificationExecutor<Manager> {


    Manager findByUserName(String userName);

    int countByRolesContains(Role role);

    Page<Manager> findByUserNameNot(String userName, Pageable pageRequest);
}
