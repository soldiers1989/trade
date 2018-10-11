package com.trade.tradeboot.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/**
 *
 * @author Sailing
 *
 */
public class TestThreadPool{
    class myThread implements Runnable{
        private Integer type;
        public myThread() {
            super();
        }
        public myThread(Integer type) {
            super();
            this.type = type;
        }
        @Override
        public void run() {
            System.out.println("type："+type);
        }
    }

    public void doUpdate(){
        ExecutorService es = Executors.newCachedThreadPool();
        System.out.println("开始");
        List<Integer> typeList = new ArrayList<>();
        typeList.add(1);
        typeList.add(2);
        typeList.add(3);
        typeList.add(4);
        typeList.add(5);
        typeList.add(6);
        typeList.add(7);
        typeList.add(8);
        typeList.add(9);
        typeList.add(10);
        for(int i=0;i<typeList.size();i++){
            Integer adjustType = typeList.get(i);
            Runnable myRunnable = new myThread(adjustType);
            es.execute(myRunnable);
        }
        /*
         * es.shutdown(); 		阻止新任务的提交，但是原本已经提交的，不会受到影响，当已提交的任务全部完成后，中断闲置的线程
         * es.shutdownNow();	阻止新任务的提交，且已提交的任务也会受到影响，不等已提交的任务完成，就会中断所有的线程。
         */
        es.shutdown();
        /*
         * es.isTerminated();	调用shutdown()方法后，且已提交的任务完成后，才会返回true
         * es.isShutdown();		调用shutdown()方法后，立马返回true
         */
        if(es.isTerminated()){
            System.out.println("结束");
        }
    }
    public static void main(String[] args) {
        new TestThreadPool().doUpdate();
    }
}
