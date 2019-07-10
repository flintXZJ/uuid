package com.xzj.stu.uuid;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author zhijunxie
 */
@SpringBootApplication
@EnableTransactionManagement
public class UuidApplication {

    public static void main(String[] args) {
        SpringApplication.run(UuidApplication.class, args);
    }

}
