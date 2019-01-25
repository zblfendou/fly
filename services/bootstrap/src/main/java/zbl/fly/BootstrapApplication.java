package zbl.fly;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.ImportResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import zbl.fly.config.DubboDeployer;

import java.util.concurrent.CountDownLatch;

import static org.springframework.boot.Banner.Mode.OFF;

@SpringBootApplication
@EnableJpaRepositories("zbl.fly.**.daos")
@EntityScan("zbl.fly.**.models")
@EnableTransactionManagement(order = 3)
@EnableAspectJAutoProxy(exposeProxy = true)
@ImportResource({"classpath*:/dubbo/base-provider.xml"})
@Slf4j
public class BootstrapApplication {

    public static void main(String[] args) throws InterruptedException {
        ConfigurableApplicationContext context = new SpringApplicationBuilder()
                .sources(BootstrapApplication.class)
                .web(false)
                .run(args);
        log.debug("Service started");
        new SpringApplicationBuilder().parent(context).web(false).bannerMode(OFF)
                .sources(DubboDeployer.class).run(args);
        log.debug("Deployed Services to Dubbo");
        CountDownLatch closeLatch = context.getBean(CountDownLatch.class);
        closeLatch.await();
    }

    @Bean
    public CountDownLatch closeLatch() {
        return new CountDownLatch(1);
    }
}
