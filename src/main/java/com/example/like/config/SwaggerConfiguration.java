package com.example.like.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class SwaggerConfiguration implements ApplicationRunner {

    @Value("${server.port:8080}")
    private String serverPort;
    @Value("${server.servlet.context-path:}")
    private String contextPath;

    /**
     * 自定义 openAPI 个性化信息
     */
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info() // 基本信息配置
                        .title("高并发点赞系统") // 标题
                        .description("原子操作，实时统计，状态查询，数据持久") // 描述 Api 接口文档的基本信息
                        .version("v1.0.0") // 版本
                        // 设置 OpenAPI 文档的联系信息
                        .contact(new Contact().name("lin.chen").email("19970955759@163.com"))
                );
    }

    /**
     * 方便大家启动项目后可以直接点击链接跳转，而不用自己到浏览器输入路径
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("API Document: http://127.0.0.1:{}{}/doc.html", serverPort, contextPath);
    }
}