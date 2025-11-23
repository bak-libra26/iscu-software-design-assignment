package kr.co.iscu.assignment.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("kr.co.iscu.assignment.repository")
public class MyBatisConfig {

}
