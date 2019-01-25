package zbl.fly.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import zbl.fly.commons.IDGenerator;

@Configuration
public class IDGeneratorConfig {
    @Bean
    public IDGenerator idGenerator() {
        //此处可以读取分布式配置信息
        return new IDGenerator(1L, 1L);
    }
}
