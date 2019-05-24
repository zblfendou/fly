package zbl.fly.base.quartz;

import org.springframework.context.ApplicationContext;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public abstract class TimedTask extends Task {
    private Date startTime;

    protected TimedTask(LocalDateTime startTime) {
        this();
        setStartTime(Date.from(startTime.atZone(ZoneId.systemDefault()).toInstant()));
    }

    protected TimedTask() {
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getStartTime() {
        return startTime;
    }

    public abstract String getJobName();

    public abstract String getJobGroup();

    @Override
    public void executeTask(ApplicationContext context) {
        logger.error("unknown model type:{}", TimedTask.class.getName());
    }
}
