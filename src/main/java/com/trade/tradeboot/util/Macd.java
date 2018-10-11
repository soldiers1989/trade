package com.trade.tradeboot.util;

import com.alibaba.fastjson.JSONArray;

import java.util.List;


/**
 * create by lizi
 */
public class Macd {
    public static JSONArray getMacd(JSONArray data) {
//        Map<String, String> res = HttpRequest.httpForGetRequest("https://www.okex.com/api/v1/kline.do?symbol=btc_usdt&type=2hour&size=100");
//        JSONObject jo = JSONObject.parseObject(res.toString().replace("=",":"));
//
//        JSONArray data = jo.getJSONArray("data");

        KlineAnalysis kl = new KlineAnalysis();

        long[] times = new long[data.size()];

        double[] closePrice = new double[data.size()];

        int fast = 12;

        int slow = 26;

        int signal = 9;

        double[] macd = new double[data.size()];

        double[] dea = new double[data.size()];

        double[] diff = new double[data.size()];

        for (int i=0; i<data.size(); i ++) {
            JSONArray array = data.getJSONArray(i);
            times[i] = array.getLong(0);
            closePrice[i] = Double.parseDouble(array.get(4).toString());
        }
        JSONArray result = kl.MACD(times, closePrice, fast, slow ,signal, macd, dea, diff);

        return result;
    }

//    public static void main(String[] args) {
//        Macd m = new Macd();
//        m.getMacd();
//    }
}
