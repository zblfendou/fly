package zbl.fly.api.remote;

import zbl.fly.base.utils.QueryResult;
import zbl.fly.models.Manager;
import zbl.fly.models.PermGroup;
import zbl.fly.models.Role;

import java.util.List;

public interface ManagerService {
    Manager getManagerByUserName(String userName);

    Manager createManager(String username, String name, String phoneNum, String email, String roleName, String pwd);

    void activeManager(long managerId);

    void stopManager(long managerId);

    Manager getManager(Long managerId);

    Role getRole(String roleName);

    void deleteRole(String roleName);

    void modifyRole(String roleName, String roleText, String[] perms);

    Role getRoleByText(String roleText);

    void createRole(String roleText, String[] perms);

    List<PermGroup> getPermsTree();

    void modifyOtherManagerPwd(long id, String newPwd);

    void modifyManagerPwd(long id, String oldPwd, String newPwd);

    void deleteManagerById(long id);

    QueryResult<Role> queryGeneralRoles(final int pageSize, int page);

    QueryResult<Manager> getOtherManagers(String userName, final int pageSize, int page);

    void modifyManagerRole(long managerID, String roleName);


    void modifyManagerInfo(long id, String name, String phoneNum, String email);

    List<Role> queryGeneralRoles();
}
