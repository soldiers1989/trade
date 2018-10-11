package com.trade.tradeboot.util;

import com.alibaba.fastjson.JSONArray;

import java.math.BigDecimal;
import java.util.*;

/**
 * create by lizi
 */
public class StochRSI {
    public static List<Object> ma(List<Object> records, double n) {
        double sum = 0;
        List<Object> result = new ArrayList<>();
        for (int i=0; i < records.size(); i ++) {
            if (i < n) {
                sum = MathCaclateUtil.add(sum, (double) records.get(i), BigDecimal.ROUND_HALF_UP);
                result.add(MathCaclateUtil.divide(sum, n, BigDecimal.ROUND_HALF_UP));
            } else {
                sum = MathCaclateUtil.subtract(MathCaclateUtil.add(sum, (double) records.get(i), BigDecimal.ROUND_HALF_UP),
                        (double) records.get(i - (int) n), BigDecimal.ROUND_HALF_UP);
                result.add(MathCaclateUtil.divide(sum, n, BigDecimal.ROUND_HALF_UP));
            }
        }
        return result;
    }

    public static Map<String, String> getStochRSI(JSONArray kline, List<Double> rsi, double n, double m, double p1, double p2){
        List<Object> close = new ArrayList<>();
        List<Object> time = new ArrayList<>();

        for (int i=0; i<kline.size(); i++) {
            close.add(kline.getJSONArray(i).get(4));
            time.add(kline.getJSONArray(i).get(0));
        }
        List<Object> pre_ma_1 = new ArrayList<>();
        List<Object> pre_ma_2 = new ArrayList<>();

        List<Object> start_time = time.subList(0, (int) m);

        time = time.subList((int) m, time.size());

        List<Object> k = new ArrayList<>();
        List<Object> d = new ArrayList<>();


        for (int indexx = 0; indexx < start_time.size(); indexx ++) {
            k.add(new ArrayList<>(Arrays.asList(start_time.get(indexx),0.0)));
            d.add(new ArrayList<>(Arrays.asList(start_time.get(indexx),0.0)));
        }

        for (int index=0; index < rsi.size(); index++) {
            if (index < (int) m) {
                continue;
            } else {
                pre_ma_1.add(MathCaclateUtil.subtract(rsi.get(index), Collections.min(rsi.subList(index - (int) m + 1, index + 1)),
                        BigDecimal.ROUND_HALF_UP));
                pre_ma_2.add(MathCaclateUtil.subtract(Collections.max(rsi.subList(index - (int) m + 1, index + 1)), Collections.min(rsi.subList(index - (int) m + 1, index + 1)),
                        BigDecimal.ROUND_HALF_UP));
            }
        }
        List<Object> ma1 = ma(pre_ma_1, p1);
        List<Object> ma2 = ma(pre_ma_2, p1);


        List<Object> pre_ma_K = new ArrayList<>();

        for (int index1 = 0; index1 < ma1.size(); index1 ++) {
            if (ma2.get(index1).equals(0)) {
                pre_ma_K.add(100.0);
                k.add(new ArrayList<>(Arrays.asList(time.get(index1),100.0)));
            } else {
                pre_ma_K.add(MathCaclateUtil.multiply(
                        MathCaclateUtil.divide((double) ma1.get(index1), (double) ma2.get(index1), BigDecimal.ROUND_HALF_UP),
                        100D, BigDecimal.ROUND_HALF_UP));
                k.add(new ArrayList<>(Arrays.asList(time.get(index1),MathCaclateUtil.multiply(
                        MathCaclateUtil.divide((double) ma1.get(index1), (double) ma2.get(index1), BigDecimal.ROUND_HALF_UP),
                        100D, BigDecimal.ROUND_HALF_UP))));
            }
        }
        List<Object> d_tmp = ma(pre_ma_K, p2);
        for (int index2 = 0; index2 < d_tmp.size(); index2 ++) {
            d.add(new ArrayList<>(Arrays.asList(time.get(index2), d_tmp.get(index2))));
        }


        Map<String, String> map = new HashMap<>();
        map.put("k", k.toString());
        map.put("d", d.toString());

        return map;
    }

}
