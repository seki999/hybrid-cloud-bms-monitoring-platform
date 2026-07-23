package com.example.bms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * BMS 监视平台的组合根。
 *
 * <p>应用采用一个可执行制品配合 {@code bms.component} 运行模式。开发环境可在一个进程中启用全部组件，
 * Kubernetes 则通过不同 Deployment 选择 Web、协议接收和 Worker 职责，从而独立扩缩容。</p>
 */
@SpringBootApplication
@EnableScheduling
public class BmsApplication {

    /**
     * 启动 Spring 容器。
     *
     * @param args Spring Boot 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(BmsApplication.class, args);
    }
}

