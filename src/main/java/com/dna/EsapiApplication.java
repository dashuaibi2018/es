package com.dna;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.dna.mapper")
public class EsapiApplication {

    public static void main(String[] args) {
        SpringApplication.run(EsapiApplication.class, args);
    }

}
