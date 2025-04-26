package com.fourclover.clobee;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.fourclover.clobee.mapper")
@SpringBootApplication
public class ClobeeApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClobeeApplication.class, args);
    }

}
