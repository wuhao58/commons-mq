package com.leo.commons.mian;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author: LEO
 * @Date: 2020/9/11 11:26
 * @Description
 */
@SpringBootApplication(scanBasePackages = {"com.leo.commons"})
public class CommonApplication {

    public static void main(String[] args) {
        SpringApplication.run(CommonApplication.class, args);
    }

}

