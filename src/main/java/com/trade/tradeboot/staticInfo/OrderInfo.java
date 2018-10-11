package com.trade.tradeboot.staticInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderInfo {




//    public static Map<String, Double> tmp = new HashMap<String, Double>() {
//        {
//            put("eos_usdt",5.6744D);
//        }
//    };
//
//    public static Map<Long,Map<String, Double>> tmp2 = new HashMap<Long,Map<String, Double>>() {
//        {
//            put(1000000L, tmp);
//        }
//    };

    // Map<"订单号", <"时间", <"币对" ,"价格">>>
    public static Map<String, Map<Long,Map<String, Double>>> ORDER_NO = new HashMap<>();

    public static Map<String, Map<Long,Map<String, Double>>> ORDER_NO_JACKY = new HashMap<>();

}
