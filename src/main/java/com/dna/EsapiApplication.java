package com.dna;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.dna.mapper")
public class EsapiApplication {

    public static void main(String[] args) {

        /*启动类指定项目中使用的日志框架*/
        /*解决
        log4j:WARN No appenders could be found for logger (org.apache.dubbo.common.logger.LoggerFactory).
        log4j:WARN Please initialize the log4j system properly.*/
        System.setProperty("dubbo.application.logger", "log4j2");

        SpringApplication.run(EsapiApplication.class, args);
    }

}
