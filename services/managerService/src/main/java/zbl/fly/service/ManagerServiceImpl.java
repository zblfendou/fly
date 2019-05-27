package zbl.fly.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import zbl.fly.api.remote.ManagerService;
import zbl.fly.base.utils.QueryResult;
import zbl.fly.base.utils.RedisConstant;
import zbl.fly.commons.redis.RedisClient;
import zbl.fly.daos.*;
import zbl.fly.models.Manager;
import zbl.fly.models.PermGroup;
import zbl.fly.models.Role;
import zbl.fly.models.TestRedisModel;
import zbl.fly.quartz.SchedulingUtils;

import javax.inject.Inject;
import javax.inject.Named;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.util.StringUtils.hasText;

@Named("managerService")
public class ManagerServiceImpl implements ManagerService {

    private final static SecureRandom random = new SecureRandom();
    @Inject
    private ManagerDao dao;
    @Inject
    private RoleDao roleDao;
    @Inject
    private PermDao permDao;
    @Inject
    private PermGroupDao permGroupDao;
    @Inject
    private SchedulingUtils schedulingUtils;
    @Inject
    private RedisClient redisClient;
    @Inject
    private TestRedisModelDao testRedisModelDao;

    @Override
    public Manager getManagerByUserName(String userName) {
        //测试redis
        TestRedisModel testRedisModel = redisClient.get(RedisConstant.REDIS_TEST_NAME, userName);
        if (testRedisModel == null) {
            testRedisModel = testRedisModelDao.findByName(userName);
            if (testRedisModel != null) {
                redisClient.set(RedisConstant.REDIS_TEST_NAME, userName, testRedisModel,10, TimeUnit.SECONDS);
            }
        }
        return dao.findByUserName(userName);
    }


    @Override
    @Transactional
    public Manager createManager(String username, String name, String phoneNum, String email, String roleName, String pwd) {
        assert hasText(username);
        assert hasText(name);

        assert getManagerByUserName(username) == null;
        Manager manager = new Manager();
        manager.setUserName(username);
        manager.setPassword(pwd);
        manager.setName(name);
        manager.setStatus(Manager.Status.ACTIVED);
        manager.setEmail(email);
        manager.setPhoneNum(phoneNum);
        Set<Role> roles = new HashSet<>();
        Role role = getRole(roleName);
        roles.add(role);
        manager.setRoles(roles);
        return dao.save(manager);
    }

    @Override
    @Transactional
    public void activeManager(long managerId) {
        Manager manager = dao.findOne(managerId);
        assert manager != null;
        assert manager.getStatus() == Manager.Status.NEW;
        manager.setStatus(Manager.Status.ACTIVED);
        dao.save(manager);
    }

    @Override
    @Transactional
    public void stopManager(long managerId) {
        Manager manager = dao.findOne(managerId);
        assert manager != null;
        assert manager.getStatus() != Manager.Status.STOPED;
        manager.setStatus(Manager.Status.STOPED);
        dao.save(manager);
    }

    @Override
    public Manager getManager(Long managerId) {
        if (managerId == null) return null;
        return dao.findOne(managerId);
    }

    @Override
    @Transactional
    public void modifyManagerInfo(long id, String name, String phoneNum, String email) {
        Manager manager = dao.findOne(id);
        assert manager != null;
        manager.setName(name);
        manager.setPhoneNum(phoneNum);
        manager.setEmail(email);
        dao.save(manager);
    }

    @Override
    public List<Role> queryGeneralRoles() {
        return roleDao.findBySuperVisorIsFalse();
    }

    @Override
    public Role getRole(String roleName) {
        return roleDao.findOne(roleName);
    }

    @Override
    @Transactional
    public void deleteRole(String roleName) {
        if (roleDao.exists(roleName)) roleDao.delete(roleName);

    }

    @Override
    @Transactional
    public void modifyRole(String roleName, String roleText, String[] perms) {
        assert perms != null && perms.length > 0;
        Role role = getRole(roleName);
        assert role != null;
        role.setPermissions(Arrays.stream(perms).map(permDao::findOne).filter(Objects::nonNull).collect(Collectors.toSet()));
        role.setRoleText(roleText);
        roleDao.save(role);

    }

    @Override
    public Role getRoleByText(String roleText) {
        return roleDao.findByRoleText(roleText);
    }

    @Override
    @Transactional
    public void createRole(String roleText, String[] perms) {
        assert getRoleByText(roleText) == null;
        assert perms != null && perms.length > 0;
        Role role = new Role();
        role.setPermissions(Arrays.stream(perms).map(permDao::findOne).filter(Objects::nonNull).collect(Collectors.toSet()));
        role.setRoleText(roleText);
        role.setRoleName(generateRoleName());
        roleDao.save(role);
    }

    private String generateRoleName() {
        String result = String.format("ROLE_%4d", random.nextInt(10000));
        return roleDao.findOne(result) == null ? result : generateRoleName();
    }

    @Override
    public List<PermGroup> getPermsTree() {
        return permGroupDao.findAllByParentIsNullOrderByOrder();
    }

    @Override
    @Transactional
    public void modifyOtherManagerPwd(long id, String newPwd) {
        Manager manager = dao.findOne(id);
        assert manager != null;
        manager.setPassword(newPwd);
        dao.save(manager);
    }

    @Override
    @Transactional
    public void modifyManagerPwd(long id, String oldPwd, String newPwd) {
        Manager manager = dao.findOne(id);
        assert manager != null;
        assert Manager.encrypt(oldPwd, manager.getSalt()).equals(manager.getSecurity()) : "旧密码错误";
        manager.setPassword(newPwd);
        dao.save(manager);
    }

    @Override
    @Transactional
    public void deleteManagerById(long id) {
        if (dao.exists(id)) dao.delete(id);
    }

    @Override
    public QueryResult<Role> queryGeneralRoles(final int pageSize, int page) {
        Pageable pagerequest = new PageRequest(page - 1, pageSize, new Sort(ASC, "createTime").and(new Sort(Sort.Direction.DESC, "createTime")));
        Page<Role> pageResult = roleDao.findAll(pagerequest);
        List<Role> list = pageResult.getContent();
        list.forEach(role -> role.setManagerCount(getOperatorCount(role)));

        return QueryResult.build(pageResult.getTotalElements(), pageResult.getTotalPages(), pageResult.getNumber(), list);
    }

    private int getOperatorCount(Role role) {
        return dao.countByRolesContains(role);
    }

    @Override
    public QueryResult<Manager> getOtherManagers(String userName, final int pageSize, int page) {
        Pageable pageRequest = new PageRequest(page - 1, pageSize, new Sort(Sort.Direction.DESC, "createTime"));
        Page<Manager> p = dao.findByUserNameNot(userName, pageRequest);
        return QueryResult.build(p.getTotalElements(), p.getTotalPages(), p.getNumber(), p.getContent());
    }

    @Override
    @Transactional
    public void modifyManagerRole(long managerID, String roleName) {
        Manager manager = dao.findOne(managerID);
        assert manager != null;
        Role role = roleDao.findOne(roleName);
        assert role != null;
        manager.getRoles().clear();
        manager.getRoles().add(role);
        dao.save(manager);

    }
}
