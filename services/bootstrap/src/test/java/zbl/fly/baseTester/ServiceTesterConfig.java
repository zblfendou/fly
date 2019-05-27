package zbl.fly.baseTester;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAutoConfiguration
@ComponentScan("zbl.fly")
@EntityScan("zbl.fly.**.models")
@EnableAspectJAutoProxy(exposeProxy = true)
public class ServiceTesterConfig {
}
