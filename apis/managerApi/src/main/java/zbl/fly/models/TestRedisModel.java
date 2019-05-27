package zbl.fly.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;

/**
 * 描述:
 *
 * @author: 张彬雷 [zhangbinlei@xinnet.com]
 * @since: 2019/5/27
 * @update:[变更日期YYYY-MM-DD][更改人姓名][变更描述]
 */
@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TestRedisModel extends BaseModel {
    private String name;
    private int age;
    private String address;
}
