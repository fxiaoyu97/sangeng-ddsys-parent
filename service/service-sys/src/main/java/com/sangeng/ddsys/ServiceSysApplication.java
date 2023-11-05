package com.sangeng.ddsys;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author: calos
 * @version: 1.0.0
 * @createTime: 2023/11/3 16:40
 **/
@SpringBootApplication
@EnableDiscoveryClient
public class ServiceSysApplication {
    public static void main(final String[] args) {
        SpringApplication.run(ServiceSysApplication.class, args);
    }
}
