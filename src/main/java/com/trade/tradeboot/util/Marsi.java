package com.trade.tradeboot.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * create by lizi
 */
public class Marsi {

    public static JSONArray getMarsi(JSONArray data) {
//        Map<String, String> res = HttpRequest.httpForGetRequest("https://www.okex.com/api/v1/kline.do?symbol=btc_usdt&type=2hour");
//        JSONObject jo = JSONObject.parseObject(res.toString().replace("=",":"));
//
//        JSONArray data = jo.getJSONArray("data");

        // 处理MARSI的逻辑

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
        List<Object> result = k.RSI(times,closePrice, 10, 6, 6, r1, r2, r3, ppm1, ppm2, npm1, npm2);

        List<Object> marsi = new ArrayList<>();

        List<Object> marsi2 = new ArrayList<>();

        Double sumr1 = 0.0;

        Double sumr2 = 0.0;


        long[] time_output = (long[]) result.get(0);

        double[] r1_output = (double[]) result.get(1);

        double[] r2_output = (double[]) result.get(2);

        double[] ppm1_output = (double[]) result.get(4);
        double[] npm1_output = (double[]) result.get(5);

        double[] ppm2_output = (double[]) result.get(6);
        double[] npm2_output = (double[]) result.get(7);

        for (int x=0; x < time_output.length; x ++) {

            List<Object> marsiTmp2 = new ArrayList<>();

            Double ppm2_tmp = MathCaclateUtil.divide(MathCaclateUtil.multiply(num_100, ppm2_output[x], BigDecimal.ROUND_HALF_UP),
                    MathCaclateUtil.add(ppm2_output[x], -npm2_output[x], BigDecimal.ROUND_HALF_UP), BigDecimal.ROUND_UNNECESSARY);


            if (x <= 5) {
                sumr2 = MathCaclateUtil.add(sumr2, ppm2_tmp, BigDecimal.ROUND_HALF_UP);

                marsiTmp2.add(time_output[x]);

                marsiTmp2.add(MathCaclateUtil.divide(sumr2, 6.0, BigDecimal.ROUND_HALF_UP));

                marsi2.add(marsiTmp2);
            } else {
                double last = r2_output[x - 6];

                sumr2 = MathCaclateUtil.add(sumr2, ppm2_tmp, BigDecimal.ROUND_HALF_UP);
                sumr2 = MathCaclateUtil.subtract(sumr2, last, BigDecimal.ROUND_HALF_UP);

                marsiTmp2.add(time_output[x]);

                marsiTmp2.add(MathCaclateUtil.divide(sumr2, 6.0, BigDecimal.ROUND_HALF_UP));

                marsi2.add(marsiTmp2);
            }
        }

        for (int b=0; b < time_output.length; b ++) {

            Double ppm1_tmp = MathCaclateUtil.divide(MathCaclateUtil.multiply(num_100, ppm1_output[b], BigDecimal.ROUND_HALF_UP),
                    MathCaclateUtil.add(ppm1_output[b], -npm1_output[b], BigDecimal.ROUND_HALF_UP), BigDecimal.ROUND_UNNECESSARY);

            List<Object> marsiTmp = new ArrayList<>();

            if (b <= 9) {
                sumr1 = MathCaclateUtil.add(sumr1, ppm1_tmp, BigDecimal.ROUND_HALF_UP);

                marsiTmp.add(time_output[b]);

                marsiTmp.add(MathCaclateUtil.divide(sumr1, 10.0, BigDecimal.ROUND_HALF_UP));

                marsi.add(marsiTmp);
            } else {
                double last = r1_output[b - 10];

                sumr1 = MathCaclateUtil.add(sumr1, ppm1_tmp, BigDecimal.ROUND_HALF_UP);
                sumr1 = MathCaclateUtil.subtract(sumr1, last, BigDecimal.ROUND_HALF_UP);

                marsiTmp.add(time_output[b]);

                marsiTmp.add(MathCaclateUtil.divide(sumr1, 10.0, BigDecimal.ROUND_HALF_UP));

                marsi.add(marsiTmp);
            }
        }

        // 开始没考虑好所需要的数据格式 导致这里代码很low

        List<JSONArray> json_result = new ArrayList<>();

        for  (int each = 0; each < marsi.size(); each ++) {
            List<Object> tmp = (List<Object>) marsi.get(each);

            List<Object> ppm2_tmp = (List<Object>) marsi2.get(each);

            List<Object> json_tmp = new ArrayList<>();
            json_tmp.add(tmp.get(0));
            json_tmp.add(tmp.get(1));
            json_tmp.add(ppm2_tmp.get(1));

            json_result.add(JSONArray.parseArray(json_tmp.toString()));
        }
        return JSONArray.parseArray(json_result.toString());
    }
}
