package zbl.fly.task;

import org.springframework.context.ApplicationContext;
import zbl.fly.base.quartz.TimedTask;

import java.time.LocalDateTime;

/**
 * 描述:
 *
 * @author: 张彬雷 [zhangbinlei@xinnet.com]
 * @since: 2019/5/24
 * @update:[变更日期YYYY-MM-DD][更改人姓名][变更描述]
 */
public class TestTask extends TimedTask {

    private String msg;

    public String getMsg() {
        return msg;
    }

    public TestTask(String msg, LocalDateTime startTime) {
        super(startTime);
        this.msg = msg;
    }

    public TestTask() {
    }

    @Override
    public String getJobName() {
        return "AAAA";
    }

    @Override
    public String getJobGroup() {
        return "BBB";
    }

    @Override
    public void executeTask(ApplicationContext context) {
        System.out.println(msg);
    }
}
