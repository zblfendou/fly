package zbl.fly.activemq;

import org.apache.activemq.command.ActiveMQQueue;
import org.junit.Test;
import zbl.fly.baseTester.AbstractTester;
import zbl.fly.jms.Producer;

import javax.inject.Inject;
import javax.jms.Destination;

/**
 * 描述:
 *
 * @author: 张彬雷 [zhangbinlei@xinnet.com]
 * @since: 2019/5/30
 * @update:[变更日期YYYY-MM-DD][更改人姓名][变更描述]
 */
public class ActivemqTest extends AbstractTester {
    @Inject
    private Producer producer;

    @Test
    public void testProducer() {
        Destination destination = new ActiveMQQueue("mytest.queue");
        for (int i = 0; i < 200; i++)
            producer.sendMessage(destination, "my name is avticemq " + i);
    }
}
