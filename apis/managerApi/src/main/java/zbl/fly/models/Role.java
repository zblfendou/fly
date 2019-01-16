package zbl.fly.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Setter
@Getter
@Cacheable
@Table(uniqueConstraints = @UniqueConstraint(name = "UK_ROLE_TEXT", columnNames = "roleText"))
public class Role implements Serializable {
    public final static Role SUPERVISOR;

    static {
        SUPERVISOR = new Role();
        SUPERVISOR.setRoleName("SuperVisor");
        SUPERVISOR.setRoleText("超级管理员");
        SUPERVISOR.setSuperVisor(true);
        Set<Permission> permissionSet = new HashSet<>();
        permissionSet.add(Permission.SUPERVISOR);
        SUPERVISOR.setPermissions(permissionSet);

    }

    private boolean superVisor;
    @JsonIgnore
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime = new Date();
    @Id
    @Column(columnDefinition = "varchar(191) not null")
    private String roleName;
    @Basic
    private String roleText;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "ROLEPERM",
            joinColumns = @JoinColumn(name = "role", foreignKey = @ForeignKey(name = "FK_ROLEPERM_ROLE")),
            inverseJoinColumns = @JoinColumn(name = "perm", foreignKey = @ForeignKey(name = "FK_ROLEPERM_PERM"))
    )
    @JsonIgnore
    private Set<Permission> permissions = new HashSet<>();
    @Transient
    private int managerCount;

    @Transient
    public Set<String> getPermNames() {
        return permissions.stream().map(Permission::getPerm).collect(Collectors.toSet());
    }

}
