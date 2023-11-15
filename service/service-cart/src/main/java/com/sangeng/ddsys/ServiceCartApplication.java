package com.sangeng.ddsys;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author: calos
 * @version: 1.0.0
 * @createTime: 2023/11/15 21:36
 **/
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class) // 取消数据源自动配置
@EnableDiscoveryClient
@EnableFeignClients
public class ServiceCartApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceCartApplication.class, args);
    }
}
