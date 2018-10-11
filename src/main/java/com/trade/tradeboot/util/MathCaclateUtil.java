package com.trade.tradeboot.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author: lizi
 * @source: github
 */

import org.apache.commons.lang3.StringUtils;


    /***
     *
     * 金额
     *
     * 如果需要精确计算，必须用String来够造BigDecimal！ ！！
     *
     * Java里面的商业计算，不能用float和double，因为他们无法 进行精确计算。
     * 但是Java的设计者给编程人员提供了一个很有用的类BigDecimal， 他可以完善float和double类无法进行精确计算的缺憾。
     * BigDecimal类位于java.maths类包下。 它的构造函数很多，最常用的: BigDecimal(double val)
     * BigDecimal(String str) BigDecimal(BigInteger val) BigDecimal(BigInteger
     * unscaledVal, int scale)
     */


public class MathCaclateUtil {


    /***
     * 保留2位小数 四舍五入
     *
     * @param
     *
     * @return 返回一个double类型的2位小数
     */
    public static double get2Double(Double doubleVal, int scale) {
        if (null == doubleVal) {
            doubleVal = new Double(0);
        }
        return new BigDecimal(doubleVal).setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }


    public static double get2DoubleForNowprice(Double doubleVal, int scale) {
        if (null == doubleVal) {
            doubleVal = new Double(0);
        }
        return new BigDecimal(doubleVal).setScale(scale, BigDecimal.ROUND_HALF_DOWN).doubleValue();
    }


    /***
     * 格式化Double类型并保留scale位小数 四舍五入
     *
     * @param doubleVal
     * @param scale
     *            scale必须为大于0的正整数，不能等于0
     * @return
     */
    public static String formatBy2Scale(Double doubleVal, int scale) {
        if (null == doubleVal) {
            doubleVal = new Double(0);
        }
        StringBuffer sbStr = new StringBuffer("0.");
        for (int i = 0; i < scale; i++) {
            sbStr.append("0");
        }
        DecimalFormat myformat = new DecimalFormat(sbStr.toString());
        return myformat.format(doubleVal);
    }


    /***
     * Double类型相加 <font color="red">+</font><br/>
     * ROUND_HALF_UP <font color="red">四舍五入</font><br/>
     *
     * @param val1
     *
     * @param val2
     *
     * @param scale
     *            <font color="red">保留scale位小数</font><br/>
     * @return
     */
    public static double add(Double val1, Double val2, int scale) {
        if (null == val1) {
            val1 = new Double(0);
        }
        if (null == val2) {
            val2 = new Double(0);
        }
        return new BigDecimal(Double.toString(val1)).add(new BigDecimal(Double.toString(val2)))
                .setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }


    /***
     * Double类型相减 <font color="red">—</font><br/>
     * ROUND_HALF_UP <font color="red">四舍五入</font><br/>
     *
     * @param val1
     *
     * @param val2
     *
     * @param scale
     *            <font color="red">保留scale位小数</font><br/>
     * @return
     */
    public static double subtract(Double val1, Double val2, int scale) {
        if (null == val1) {
            val1 = new Double(0);
        }
        if (null == val2) {
            val2 = new Double(0);
        }
        return new BigDecimal(Double.toString(val1)).subtract(new BigDecimal(Double.toString(val2)))
                .setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }


    /***
     * Double类型相乘 <font color="red">*</font><br/>
     * ROUND_HALF_UP <font color="red">四舍五入</font><br/>
     *
     * @param val1
     *
     * @param val2
     *
     * @param scale
     *            <font color="red">保留scale位小数</font><br/>
     * @return
     */
    public static double multiply(Double val1, Double val2, int scale) {
        if (null == val1) {
            val1 = new Double(0);
        }
        if (null == val2) {
            val2 = new Double(0);
        }
        return new BigDecimal(Double.toString(val1)).multiply(new BigDecimal(Double.toString(val2)))
                .setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }


    /***
     * Double类型相除 <font color="red">/</font><br/>
     * ROUND_HALF_UP <font color="red">四舍五入</font><br/>
     *
     * @param val1
     *
     * @param val2
     *
     * @param scale
     *            <font color="red">保留scale位小数</font><br/>
     * @return
     */
    public static double divide(Double val1, Double val2, int scale) {
        if (null == val1) {
            val1 = new Double(0);
        }
        if (null == val2 || val2 == 0) {
            val2 = new Double(1);
        }
// new BigDecimal(Double.toString(val1)).divide(new
// BigDecimal(Double.toString(val2)))
// .setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue()
        return new BigDecimal(Double.toString(val1))
                .divide(new BigDecimal(Double.toString(val2)), scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }


    /***
     * Double类型取余 <font color="red">%</font><br/>
     * ROUND_HALF_UP <font color="red">四舍五入</font><br/>
     *
     * @param val1
     *
     * @param val2
     *
     * @param scale
     *            <font color="red">保留scale位小数</font><br/>
     * @return
     */
    public static int divideAndRemainder(Double val1, Double val2, int scale) {
        if (null == val1) {
            val1 = new Double(0);
        }
        if (null == val2 || val2 == 0) {
            val2 = new Double(1);
        }
        return new BigDecimal(Double.toString(val1)).divideAndRemainder(new BigDecimal(Double.toString(val2)))[1]
                .setScale(scale, BigDecimal.ROUND_HALF_UP).intValue();
    }


    /***
     * 格式化Double类型数据
     *
     * @param val
     * @param fmt
     *            NumberFormat currency = NumberFormat.getCurrencyInstance();
     *            //建立货币格式化引用 NumberFormat percent =
     *            NumberFormat.getPercentInstance(); //建立百分比格式化引用
     * @param maximumFractionDigits
     *            如果是百分比 设置小数位数（四舍五入）
     * @return
     */
    public static String formatByNumberFormat(Double val, NumberFormat fmt, int maximumFractionDigits) {
        if (fmt.equals(NumberFormat.getPercentInstance())) {
            fmt.setMaximumFractionDigits(maximumFractionDigits); // 百分比小数点最多3位
        }
        return fmt.format(val);


    }


    /***
     * 比较大小 -1 <、0 =、1 >，即左边比右边数大，返回1，相等返回0，比右边小返回-1。
     *
     * @param
     * @return
     */
    public static int compareTo(Double val1, Double val2) {
        if (null == val1) {
            val1 = new Double(0);
        }
        if (null == val2) {
            val2 = new Double(0);
        }
        return new BigDecimal(val1).compareTo(new BigDecimal(val2));
    }


    public static BigDecimal decimalpoint(String str) {
        BigDecimal bigDecimal = null;
        if (StringUtils.isEmpty(str)) {
            return new BigDecimal("0");
        }
        bigDecimal = new BigDecimal(str);


        return bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP);// 默认四舍五入
    }


    public static BigDecimal decimalpoint(BigDecimal bigDecimal) {
        if (bigDecimal == null) {
            return bigDecimal;
        }
        return bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP);// 默认四舍五入
    }

    /**
     * 格式化百分比
     *
     * @param fractionDigit
     *            保留位数
     * @param bigDecimal
     *            格式化数据
     * @return
     */
    public static String unmberFormat(int fractionDigit, BigDecimal bigDecimal) {
        NumberFormat nt = NumberFormat.getPercentInstance();// 设置百分数精确度2即保留两位小数
        nt.setMinimumFractionDigits(fractionDigit);


        bigDecimal = bigDecimal == null ? new BigDecimal("0") : bigDecimal;


        return nt.format(bigDecimal);
    }

    public static double Variance(double[] x) {
        int m=x.length;
        double sum=0;
        for(int i=0;i<m;i++){//求和
            sum+=x[i];
        }
        double dAve=sum/m;//求平均值
        double dVar=0;
        for(int i=0;i<m;i++){//求方差
            dVar+=(x[i]-dAve)*(x[i]-dAve);
        }
        return dVar/m;
    }

    public static String unmberFormat(String num) {
        return num + "%";
    }

    public static String formalDate(long time) {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//这个是你要转成后的时间的格式
        String sd = sdf.format(new Date(time));   // 时间戳转换成时间
        return sd;
    }

    /**
     * 获取指定日期所在月份开始的时间戳
     * @param date 指定日期
     * @return
     */
    public static Long getMonthBegin(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        //设置为1号,当前日期既为本月第一天
        c.set(Calendar.DAY_OF_MONTH, 1);
        //将小时至0
        c.set(Calendar.HOUR_OF_DAY, 0);
        //将分钟至0
        c.set(Calendar.MINUTE, 0);
        //将秒至0
        c.set(Calendar.SECOND,0);
        //将毫秒至0
        c.set(Calendar.MILLISECOND, 0);
        // 获取本月第一天的时间戳
        return c.getTimeInMillis();
    }

    /**
     * 获取指定日期所在月份结束的时间戳
     * @param date 指定日期
     * @return
     */
    public static Long getMonthEnd(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        //设置为当月最后一天
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
        //将小时至23
        c.set(Calendar.HOUR_OF_DAY, 23);
        //将分钟至59
        c.set(Calendar.MINUTE, 59);
        //将秒至59
        c.set(Calendar.SECOND,59);
        //将毫秒至999
        c.set(Calendar.MILLISECOND, 999);
        // 获取本月最后一天的时间戳
        return c.getTimeInMillis();
    }
}
