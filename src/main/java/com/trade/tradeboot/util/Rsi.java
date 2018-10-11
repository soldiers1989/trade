package com.trade.tradeboot.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * @author lizi
 */

public class Rsi {
    public static JSONArray getRsi(JSONArray data) {

        double num_100 = 100D;

        long[] times = new long[data.size()];

        double[] closePrice = new double[data.size()];

        double[] r1 = new double[data.size()];

        double[] r2 = new double[data.size()];

        double[] r3 = new double[data.size()];

        double[] ppm1 = new double[data.size()];

        double[] ppm2 = new double[data.size()];

        double[] npm1 = new double[data.size()];

        double[] npm2 = new double[data.size()];

        for (int i = 0; i< data.size(); i ++ ) {
            JSONArray array = data.getJSONArray(i);
            times[i] = array.getLong(0);
            closePrice[i] = Double.parseDouble(array.get(4).toString());
        }
        KlineAnalysis k = new KlineAnalysis();
        List<Object> result = k.RSI(times,closePrice, 12, 6, 6, r1, r2, r3, ppm1, ppm2, npm1, npm2);

        List<Object> tmp = new ArrayList<>();

        for (int x = 0; x < ((double[]) result.get(1)).length; x++) {
            tmp.add(((double[]) result.get(1))[x]);
        }
        return JSON.parseArray(tmp.toString());
    }
}
