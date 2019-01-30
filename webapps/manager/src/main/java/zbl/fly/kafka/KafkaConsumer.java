package zbl.fly.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {
    @KafkaListener(topics = "app_log")
    public void receive(String message) {
        System.out.println("app_log收到通过kafka发送的消息:" + message);
    }
}
