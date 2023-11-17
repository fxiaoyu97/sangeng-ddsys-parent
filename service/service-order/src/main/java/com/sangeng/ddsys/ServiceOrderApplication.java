package com.sangeng.ddsys;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author: calos
 * @create: 2023-11-17 10:38
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class ServiceOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceOrderApplication.class, args);
    }

}
