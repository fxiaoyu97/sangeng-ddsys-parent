package com.sangeng.ddsys;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author: calos
 * @version: 1.0.0
 * @createTime: 2023/10/27 10:48
 **/
// 权限管理模块启动类
@SpringBootApplication
@EnableDiscoveryClient
public class ServiceAclApplication {
    public static void main(final String[] args) {
        SpringApplication.run(ServiceAclApplication.class, args);
    }
}
