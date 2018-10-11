package com.trade.tradeboot;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.trade.tradeboot.dao")
@EnableScheduling
public class TradebootApplication {

	public static void main(String[] args) {
		SpringApplication.run(TradebootApplication.class, args);
	}
}
