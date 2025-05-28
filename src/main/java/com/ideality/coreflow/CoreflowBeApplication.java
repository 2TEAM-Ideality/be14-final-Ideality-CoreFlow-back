package com.ideality.coreflow;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
public class CoreflowBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoreflowBeApplication.class, args);
    }

}
