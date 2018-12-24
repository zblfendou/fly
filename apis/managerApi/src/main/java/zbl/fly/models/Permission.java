package zbl.fly.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Setter
@Getter
public class Permission implements Serializable {
    public static final Permission SUPERVISOR = new Permission("SuperVisor", "超级管理员", null, 0);
    public static final String PERM_SUPERVISOR = "SuperVisor:*";
    public static final String PERM_WAITERS_CREATE_ORDER = "waiters:order:createOrder";
    @Id
    @Column(columnDefinition = "varchar(191) not null")
    private String perm;
    private String name;
    @ManyToOne
    @JoinColumn(name = "permGroup", foreignKey = @ForeignKey(name = "FK_PERM_GROUP"))
    @JsonIgnore
    private PermGroup permGroup;
    @JsonIgnore
    @Column(name = "o")
    private int order;

    public Permission() {
    }

    public Permission(String shortName, String name, PermGroup permGroup, int order) {
        this.name = name;
        this.permGroup = permGroup;
        this.perm = permGroup == null ? shortName : permGroup.getFullName() + ":" + shortName;
        this.order = order;
    }

}
