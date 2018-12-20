package zbl.fly.config;

import org.springframework.context.annotation.ImportResource;

@ImportResource({"classpath*:/dubbo/*-dubbo.xml"})
public class DubboDeployer {
}
