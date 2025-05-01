package com.fourclover.clobee.config.dbConfig;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(basePackages = "com.fourclover.clobee.event.repository")
@MapperScan(basePackages = "com.fourclover.clobee.user.repository")
@MapperScan(basePackages = "com.fourclover.clobee.noti.repository")
@MapperScan(basePackages = "com.fourclover.clobee.card.repository")
public class MyBatisConfig {
}