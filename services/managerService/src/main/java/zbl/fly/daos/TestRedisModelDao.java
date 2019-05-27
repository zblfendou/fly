package zbl.fly.daos;

import org.springframework.data.jpa.repository.JpaRepository;
import zbl.fly.models.TestRedisModel;

/**
 * 描述:
 *
 * @author: 张彬雷 [zhangbinlei@xinnet.com]
 * @since: 2019/5/27
 * @update:[变更日期YYYY-MM-DD][更改人姓名][变更描述]
 */
public interface TestRedisModelDao extends JpaRepository<TestRedisModel, Long> {
    TestRedisModel findByName(String name);
}
