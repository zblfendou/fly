package zbl.fly.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
@Cacheable
public class PermGroup implements Serializable {

    @Id
    @Column(columnDefinition = "varchar(191) not null")
    private String groupName;
    private String groupText;
    @ManyToOne
    @JoinColumn(name = "parent", foreignKey = @ForeignKey(name = "FK_PERMGROUP_PARENT"))
    @JsonIgnore
    private PermGroup parent;
    @OneToMany(mappedBy = "parent", fetch = FetchType.EAGER)
    @OrderBy("order asc ")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<PermGroup> subGroups = new ArrayList<>();
    @OneToMany(mappedBy = "permGroup", fetch = FetchType.EAGER)
    @OrderBy("order asc ")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)

    private List<Permission> permissions = new ArrayList<>();
    @JsonIgnore
    @Column(name = "o")
    private int order;

    public PermGroup(String groupName, String groupText, int order) {
        this.groupName = groupName;
        this.groupText = groupText;
        this.order = order;
    }

    public PermGroup() {
    }

    public PermGroup(String groupName, String groupText, PermGroup parent, int order) {
        this.groupName = groupName;
        this.groupText = groupText;
        this.parent = parent;
        this.order = order;
    }


    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupText() {
        return groupText;
    }

    public void setGroupText(String groupText) {
        this.groupText = groupText;
    }

    public PermGroup getParent() {
        return parent;
    }

    public void setParent(PermGroup parent) {
        this.parent = parent;
    }

    public List<PermGroup> getSubGroups() {
        return subGroups;
    }

    public void setSubGroups(List<PermGroup> subGroups) {
        this.subGroups = subGroups;
    }

    public List<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }

    @Transient
    @JsonIgnore
    public String getFullName() {
        return (parent == null ? "" : parent.getFullName() + ":") + groupName;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
