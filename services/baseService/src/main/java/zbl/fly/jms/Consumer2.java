package zbl.fly.jms;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;

import javax.inject.Named;

/**
 * 描述:
 *
 * @author: 张彬雷 [zhangbinlei@xinnet.com]
 * @since: 2019/5/30
 * @update:[变更日期YYYY-MM-DD][更改人姓名][变更描述]
 */
@Named
@Slf4j
public class Consumer2 {
    //接收消息
    @JmsListener(destination = "mytest.queue")
    public void receiveQueue(String text) {
        log.debug("消费者2收到消息：{}", text);
    }
}
