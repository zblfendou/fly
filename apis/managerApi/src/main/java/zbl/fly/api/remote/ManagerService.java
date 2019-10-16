package zbl.fly.api.remote;

import zbl.fly.base.utils.QueryResult;
import zbl.fly.models.Manager;
import zbl.fly.models.PermGroup;
import zbl.fly.models.Role;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
@Path("manager")
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public interface ManagerService {
//    @GET
//    @Path("get/{userName}")
    Manager getManagerByUserName(/*@PathParam("userName")*/ String userName);

    Manager createManager(String username, String name, String phoneNum, String email, String roleName, String pwd);
    @GET
    @Path("active")
    boolean activeManager(@QueryParam("managerId") Long managerId);

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

    void modifyManagerRole(long managerId, String roleName);


    void modifyManagerInfo(long id, String name, String phoneNum, String email);

    List<Role> queryGeneralRoles();
}
