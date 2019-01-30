package zbl.fly.kafka.streams;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.processor.ProcessorSupplier;
import org.apache.kafka.streams.processor.TopologyBuilder;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;
@Configuration
public class Application {

    static {

        //定义输入的topic
        String from = "app_log";
        String to = "second";

        //设置参数
        Properties settings = new Properties();
        settings.put(StreamsConfig.APPLICATION_ID_CONFIG,"consumer-1");
        settings.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG,"192.168.253.128:9092");

        StreamsConfig config = new StreamsConfig(settings);

        //创建拓图
        TopologyBuilder builder = new TopologyBuilder();

        // 具体分析处理
        builder.addSource("SOURCE", from)
                .addProcessor("PROCESS", (ProcessorSupplier<byte[], byte[]>) LogProcessor::new, "SOURCE")
                .addSink("SINK", to, "PROCESS");
        //创建kafka stream
        KafkaStreams stream = new KafkaStreams(builder,config);
        stream.start();
    }
}
