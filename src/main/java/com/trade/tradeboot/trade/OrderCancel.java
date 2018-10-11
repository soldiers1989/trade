package com.trade.tradeboot.trade;

import com.trade.tradeboot.stock.IStockRestApi;
import com.trade.tradeboot.stock.impl.StockRestApi;
import com.trade.tradeboot.util.Config;
import org.apache.http.HttpException;
import org.apache.http.client.methods.HttpOptions;

import java.io.IOException;
import java.util.Map;

public class OrderCancel {


    public static void main(String[] args) {
        // 通用GET方法
        IStockRestApi stockGet = new StockRestApi(Config.URL_PREX);

        // 通用POST
        IStockRestApi stockPost = new StockRestApi(Config.URL_PREX, Config.API_KEY, Config.SECRET_KEY);

        // jacky 大号
        IStockRestApi stockPost_jacky = new StockRestApi(Config.URL_PREX, Config.API_KEY1, Config.SECRET_KEY1);


        try {
            String order = stockPost.order_history("ltc_usdt","0","1","200");
//            String order = stockPost.order_info("eth_usdt","1200103424");
//
            System.out.println(order);
        } catch (HttpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
