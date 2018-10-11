package com.trade.tradeboot.entity;


import lombok.Data;

/**
 * create by lizi
 */

@Data
public class Kline {
    private String id;
    private String symbol;
    private String anchor;
    private String openPrice;
    private String closePrice;
    private String highPrice;
    private String lowPrice;
    private String volume;
    private String startTime;
    private String receiveTime;
}
