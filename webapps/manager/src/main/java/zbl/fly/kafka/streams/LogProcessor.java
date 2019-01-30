package zbl.fly.kafka.streams;

import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;

public class LogProcessor implements Processor<byte[], byte[]> {


    private ProcessorContext context;

    public void init(ProcessorContext context) {
        this.context=context;
    }

    public void process(byte[] key, byte[] value) {
        System.out.println("Processor 了");
        String input = new String(value);
        if (input.contains(">>>")){
            input=input.split(">>>")[1].trim();
            context.forward("logProcessor".getBytes(),input.getBytes());
        }else {
            context.forward("logProcessor".getBytes(),input.getBytes());
        }

    }

    public void punctuate(long timestamp) {

    }

    public void close() {

    }
}
