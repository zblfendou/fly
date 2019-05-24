package zbl.fly.quartz;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import zbl.fly.base.quartz.CronTask;
import zbl.fly.base.quartz.TimedTask;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;

@Named
@Setter
@Getter
@Slf4j
public class MyJobDetail implements Job {
    @Inject
    private ObjectMapper objectMapper;
    @Inject
    private TaskExecutor taskExcutor;

    private String timedTask;
    private String cronTask;
    private String classname;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        try {
            if (timedTask != null)
                taskExcutor.executeTask((TimedTask) objectMapper.readValue(timedTask, Class.forName(classname)));
            else if (cronTask != null)
                taskExcutor.executeTask((CronTask) objectMapper.readValue(cronTask, Class.forName(classname)));
            else log.error("Unknown model type");
        } catch (IOException | ClassNotFoundException ex) {
            log.error("Job execute failed.", ex);
            throw new JobExecutionException(ex);
        }
    }
}
