package zbl.fly.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.ToString;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

import static org.springframework.util.StringUtils.hasText;

@Entity
@Setter
@Getter
@Cacheable
@ToString
public class Manager extends BaseModel {
    private static final char[] DIGITS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    public static final Charset UTF_8 = StandardCharsets.UTF_8;
    /**
     * 名称
     */
    private String userName;
    /**
     * 铭文密码
     */
    @JsonIgnore
    private String password;
    /**
     * 密文密码
     */
    @JsonIgnore
    private String security;
    @Column(updatable = false)
    private String salt = UUID.randomUUID().toString();
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "MANAGERROLE",
            joinColumns = @JoinColumn(name = "managerID", foreignKey = @ForeignKey(name = "FK_MANAGERROLE_MANAGER")),
            inverseJoinColumns = @JoinColumn(name = "roleName", foreignKey = @ForeignKey(name = "FK_MANAGERROLE_ROLE"))
    )
    private Set<Role> roles;
    /**
     * 状态
     */
    @Column(length = 10)
    @Enumerated(EnumType.STRING)
    private Status status = Status.NEW;
    private String phoneNum;
    private String email;
    private String name;
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime = new Date();

    public Manager() {
    }

    @SneakyThrows
    public static String encrypt(String source, String salt) {
        assert hasText(source);
        assert hasText(salt);
        byte[] sourceBytes = source.getBytes(UTF_8);
        byte[] saltBytes = salt.getBytes(UTF_8);
        MessageDigest digest = MessageDigest.getInstance("MD5");
        digest.reset();
        digest.update(saltBytes);
        byte[] hashed = digest.digest(sourceBytes);

        digest.reset();
        hashed = digest.digest(hashed);
        int l = hashed.length;
        char[] out = new char[l << 1];
        int i = 0;

        for (int var4 = 0; i < l; ++i) {
            out[var4++] = DIGITS[(240 & hashed[i]) >>> 4];
            out[var4++] = DIGITS[15 & hashed[i]];
        }

        return new String(out);
    }

    public void setPassword(String password) {
        this.password = password;
        if (hasText(password)) this.security = encrypt(password, salt);
    }

    public enum Status {
        NEW("待激活"), ACTIVED("正常"), STOPED("禁用");

        Status(@SuppressWarnings("unused") String desc) {
            //noop
        }
    }
}
