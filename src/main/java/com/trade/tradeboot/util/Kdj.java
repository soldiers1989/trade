package com.trade.tradeboot.util;

import com.alibaba.fastjson.JSONArray;


/**
 * create by lizi
 */
public class Kdj {
    public static JSONArray getKdj(JSONArray data) {
//        Map<String, String> res = HttpRequest.httpForGetRequest("https://www.okex.com/api/v1/kline.do?symbol=btc_usdt&type=2hour&size=100");
//        JSONObject jo = JSONObject.parseObject(res.toString().replace("=",":"));
//
//        JSONArray data = jo.getJSONArray("data");

        KlineAnalysis kl = new KlineAnalysis();

        double[] maxPrice = new double[data.size()];

        double[] minPrice = new double[data.size()];

        double[] closePrice = new double[data.size()];

        int fastK = 9; // K

        int slowK = 3; // D

        int slowD = 3; // J

        double[] K_R = new double[data.size()];

        double[] D_R =  new double[data.size()];

        double[] J_R = new double[data.size()];

        long[] times = new long[data.size()];

        for (int i=0; i<data.size(); i ++) {
            JSONArray array = data.getJSONArray(i);
            times[i] = array.getLong(0);
            maxPrice[i] = Double.parseDouble(array.get(2).toString());
            minPrice[i] = Double.parseDouble(array.get(3).toString());
            closePrice[i] = Double.parseDouble(array.get(4).toString());
        }
        JSONArray result = kl.KDJ(times, maxPrice, minPrice, closePrice, fastK, slowK, slowD, K_R, D_R, J_R);
        return result;
    }

//    public static void main(String[] args) {
//        Kdj k = new Kdj();
//        System.out.println(k.getKdj());
//    }
}
