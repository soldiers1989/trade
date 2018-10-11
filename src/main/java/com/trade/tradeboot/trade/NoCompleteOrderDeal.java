package com.trade.tradeboot.trade;


import com.alibaba.fastjson.JSONObject;
import com.trade.tradeboot.stock.IStockRestApi;
import com.trade.tradeboot.stock.impl.StockRestApi;
import com.trade.tradeboot.util.Config;
import org.apache.http.HttpException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;

import static com.trade.tradeboot.staticInfo.OrderInfo.ORDER_NO;
import static com.trade.tradeboot.staticInfo.OrderInfo.ORDER_NO_JACKY;

//@Service
public class NoCompleteOrderDeal {


    @Scheduled(cron="0/5 * *  * * ? ")
    public void checkAndDealOrder() {


        IStockRestApi stockGet = new StockRestApi(Config.URL_PREX);

        // 通用POST
        IStockRestApi stockPost = new StockRestApi(Config.URL_PREX, Config.API_KEY, Config.SECRET_KEY);

        // jacky 大号
        IStockRestApi stockPost_jacky = new StockRestApi(Config.URL_PREX, Config.API_KEY1, Config.SECRET_KEY1);

        synchronized (this) {
            if (!ORDER_NO.isEmpty()) {
                for (Map.Entry<String, Map<Long,Map<String, Double>>> entry:ORDER_NO.entrySet()) {
                    Long currentTime = System.currentTimeMillis();
                    String order_id = entry.getKey();

                    Long lastTime = (Long) entry.getValue().keySet().toArray()[0];

                    String symbol = (String) ((Map<String, Double>) entry.getValue().values().toArray()[0]).keySet().toArray()[0];

                    Double price = (Double) ((Map<String, Double>) entry.getValue().values().toArray()[0]).values().toArray()[0];

                    System.out.println(symbol + "__" + order_id + "__" + price + "__" + lastTime);

                    try {
                        String order_info = stockPost.order_info(symbol, order_id);

                        Integer status = JSONObject.parseObject(order_info).getJSONArray("orders").getJSONObject(0).getInteger("status");

                        Double amout = JSONObject.parseObject(order_info).getJSONArray("orders").getJSONObject(0).getDouble("amount");

                        System.out.println("status::::"+status);

                        // 为0 就撤销 重新下单
                        if (status == 0 && currentTime - lastTime > 10 * 1000) {

                            String currentPrice = JSONObject.parseObject(stockGet.ticker(symbol)).getJSONObject("ticker").getString("last");

                            String res_cancel_order = stockPost.cancel_order(symbol,order_id);

                            System.out.println("res_cancel_order::"+res_cancel_order);
                            System.out.println((Double.parseDouble(currentPrice) - price) / price);
                            System.out.println(currentTime - lastTime);

                            if (!JSONObject.parseObject(res_cancel_order).isEmpty() && (Double.parseDouble(currentPrice) - price) / price < 0.015D ) {
                                String res_trade = stockPost.trade(symbol,"buy", currentPrice, amout.toString());

                                System.out.println("res_trade:"+res_trade);
                                if (JSONObject.parseObject(res_trade).getBoolean("result") == true) {

                                    Map<String, Double> tmp = new HashMap<String, Double>() {
                                        {
                                            put(symbol, Double.parseDouble(currentPrice));
                                        }
                                    };

                                    Map<Long,Map<String, Double>> tmp2 = new HashMap<Long,Map<String, Double>>() {
                                        {
                                            put(System.currentTimeMillis(), tmp);
                                        }
                                    };

                                    ORDER_NO.put(JSONObject.parseObject(res_trade).getInteger("order_id").toString(), tmp2);
                                } else {
                                    System.out.println("补下单失败。。。。。");
                                }
                            } else {
                                // 说明已经不符合买入条件， 删除map中的key
                                ORDER_NO.remove(order_id);
                            }
                        } else {
                            // 不为0 暂时做法为删除map中的key
                            ORDER_NO.remove(order_id);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        }

        synchronized (this) {
            if (!ORDER_NO_JACKY.isEmpty()) {
                for (Map.Entry<String, Map<Long,Map<String, Double>>> entry:ORDER_NO_JACKY.entrySet()) {
                    Long currentTime_jacky = System.currentTimeMillis();
                    String order_id_jacky = entry.getKey();

                    Long lastTime_jacky = (Long) entry.getValue().keySet().toArray()[0];

                    String symbol_jacky = (String) ((Map<String, Double>) entry.getValue().values().toArray()[0]).keySet().toArray()[0];

                    Double price_jacky = (Double) ((Map<String, Double>) entry.getValue().values().toArray()[0]).values().toArray()[0];


                    System.out.println(symbol_jacky + "__" + order_id_jacky + "__" + price_jacky + "__" + lastTime_jacky);

                    try {
                        String order_info_jacky = stockPost_jacky.order_info(symbol_jacky, order_id_jacky);

                        Integer status_jacky = JSONObject.parseObject(order_info_jacky).getJSONArray("orders").getJSONObject(0).getInteger("status");

                        Double amout_jacky = JSONObject.parseObject(order_info_jacky).getJSONArray("orders").getJSONObject(0).getDouble("amount");



                        System.out.println("status_jacky::::"+status_jacky);



                        // 为0 就撤销 重新下单
                        if (status_jacky == 0 && currentTime_jacky - lastTime_jacky > 10 * 1000) {


                            String currentPrice_jacky = JSONObject.parseObject(stockGet.ticker(symbol_jacky)).getJSONObject("ticker").getString("last");

                            String res_cancel_order_jacky = stockPost_jacky.cancel_order(symbol_jacky,order_id_jacky);

                            System.out.println("res_cancel_order::"+res_cancel_order_jacky);
                            System.out.println((Double.parseDouble(currentPrice_jacky) - price_jacky) / price_jacky);
                            System.out.println(currentTime_jacky - lastTime_jacky);


                            if (!JSONObject.parseObject(res_cancel_order_jacky).isEmpty() && (Double.parseDouble(currentPrice_jacky) - price_jacky) / price_jacky < 0.015D ) {
                                String res_trade_jacky = stockPost_jacky.trade(symbol_jacky,"buy", currentPrice_jacky, amout_jacky.toString());

                                System.out.println("res_trade_jacky:"+res_trade_jacky);

                                if (JSONObject.parseObject(res_trade_jacky).getBoolean("result") == true) {

                                    Map<String, Double> tmp3 = new HashMap<String, Double>() {
                                        {
                                            put(symbol_jacky, Double.parseDouble(currentPrice_jacky));
                                        }
                                    };

                                    Map<Long,Map<String, Double>> tmp4 = new HashMap<Long,Map<String, Double>>() {
                                        {
                                            put(System.currentTimeMillis(), tmp3);
                                        }
                                    };

                                    ORDER_NO_JACKY.put(JSONObject.parseObject(res_trade_jacky).get("order_id").toString(), tmp4);
                                } else {
                                    System.out.println("补下单失败。。。。。");
                                }
                            } else {
                                // 说明已经不符合买入条件， 删除map中的key
                                ORDER_NO_JACKY.remove(order_id_jacky);
                            }
                        } else {
                            // 不为0 暂时做法为删除map中的key
                            ORDER_NO_JACKY.remove(order_id_jacky);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        }

    }
//    public static void main(String[] args) {
////        Map<String, Long> map = new HashMap<String, Long>() {
////            {
////                put("1002",1000000L);
////                put("1003",212313L);
////            }
////        };
//
//
//
//
//    }

}




