package com.trade.tradeboot.util;

import com.alibaba.fastjson.JSONArray;

import java.math.BigInteger;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


/**
 * create by lizi
 */
public class Level2DataUtil {
    public JSONArray getLevel2(String symbol, String anchor, String type, long startTime, long endTime) {
        //声明Connection对象
        Connection con;
        //驱动程序名
        String driver = "com.mysql.jdbc.Driver";
        //URL指向要访问的数据库名mydata
        String url = "jdbc:mysql://rm-t4n133u864ocqyai0io.mysql.singapore.rds.aliyuncs.com:3306/kline?useUnicode=true&characterEncoding=UTF-8&useSSL=false";
        //MySQL配置时的用户名
        String user = "pgrab";
        //MySQL配置时的密码
        String password = "Pgrab123";
        //遍历查询结果集
        try {
            //加载驱动程序
            Class.forName(driver);
            //1.getConnection()方法，连接MySQL数据库！！
            con = DriverManager.getConnection(url,user,password);
            if(!con.isClosed())
                System.out.println("Succeeded connecting to the Database!");
            //2.创建statement类对象，用来执行SQL语句！！
            Statement statement = con.createStatement();
            //要执行的SQL语句
            String sql = "select * from okex_" + symbol + "_" + anchor + "_201807 where period=" + "'" + type + "' and receiveTime BETWEEN  " + startTime + " AND " + endTime;
            System.out.println(sql);
            //3.ResultSet类，用来存放获取的结果集！！
            ResultSet rs = statement.executeQuery(sql);
            System.out.println("-----------------");
            System.out.println("执行结果如下所示:");
            System.out.println("-----------------");

            String openPrice = null;
            String highPrice = null;
            String lowPrice = null;
            String closePrice = null;
            String volume = null;
            String time = null;
            List<List<Object>> list = new ArrayList<>();
            while(rs.next()){
                time = rs.getString("receiveTime");
                openPrice = rs.getString("openPrice");
                highPrice = rs.getString("highPrice");
                lowPrice = rs.getString("lowPrice");
                closePrice = rs.getString("closePrice");
                volume = rs.getString("volume");
                List<Object> tmp = new ArrayList<>();
                tmp.add(Long.parseLong(time));
                tmp.add(Double.parseDouble(openPrice));
                tmp.add(Double.parseDouble(highPrice));
                tmp.add(Double.parseDouble(lowPrice));
                tmp.add(Double.parseDouble(closePrice));
                tmp.add(Double.parseDouble(volume));
                list.add(tmp);
                //输出结果
//                System.out.println(time + "\t" + openPrice+ "\t" + highPrice+ "\t" + lowPrice+ "\t" + closePrice+ "\t" + volume);
            }
            rs.close();
            con.close();
            return JSONArray.parseArray(list.toString());
        } catch(ClassNotFoundException e) {
            //数据库驱动类异常处理
            System.out.println("Sorry,can`t find the Driver!");
            e.printStackTrace();
            JSONArray result = new JSONArray();
            return result;
        } catch(SQLException e) {
            //数据库连接失败异常处理
            e.printStackTrace();
            JSONArray result = new JSONArray();
            return result;
        }catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            JSONArray result = new JSONArray();
            return result;
        }finally{
            System.out.println("数据库数据成功获取！！");
        }
    }
}
