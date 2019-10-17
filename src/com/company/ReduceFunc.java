package com.company;

import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class ReduceFunc {
    public static final Map<String,Integer> reduceResult = new ConcurrentHashMap<>();
    //缓冲区，接收map任务处理完后的结果
    private static final BlockingDeque<Map<String,List<Integer>>> blockingDeque = Transfer.buffer;


    public Map<String,Integer> reduce(){
        //线程池
        ExecutorService executorService = Executors.newCachedThreadPool();
        //线程数
        int tasks = 8;
        CountDownLatch countDownLatch = new CountDownLatch(tasks);
        for (int i = 0; i <tasks ; i++) {
            //reduce任务
            executorService.execute(()->{
                try {
                    //从缓冲区拿map任务的结果，如果还没有就阻塞
                    Map<String,List<Integer>> map1 = blockingDeque.take();
                    for(Map.Entry<String,List<Integer>> entry:map1.entrySet()){
                        int sum = 0;
                        try {
                            for(Integer integer:entry.getValue()){

                                sum+=integer;
                            }
                            //将本任务的处理结果加入最终结果中
                            reduceResult.put(entry.getKey(),reduceResult.getOrDefault(entry.getKey(),0)+sum);
                        }
                        catch (NullPointerException e){
                            System.out.println(entry);
                            e.printStackTrace();
                        }

                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                countDownLatch.countDown();
            });
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executorService.shutdown();

        return reduceResult;
    }


}
