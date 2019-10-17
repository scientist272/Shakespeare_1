package com.company;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

public class MapFunc {
    private static final Set<String> stopWords;
    //阻塞队列(缓冲区),每个任务处理完后放入缓冲区让reduce处理
    private static final BlockingDeque<Map<String,List<Integer>>> blockingDeque =Transfer.buffer;
    //管道


    static {
        stopWords = new HashSet<>();
        String path1 = "./stopwords1.txt";
        String path2 = "./stopwords2.txt";
        getStopWords(path1);
        getStopWords(path2);
    }

    private static void getStopWords(String path) {
        try(InputStreamReader read = new InputStreamReader(new FileInputStream(path));
            BufferedReader reader = new BufferedReader(read)){
            while(true){
                String line = reader.readLine();
                if(line==null)
                    break;
                stopWords.add(line.toLowerCase());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void map(String string){
        String[] temp = generateUnfilteredWords(string);
        //TODO 多线程处理
        ExecutorService executorService =  Executors.newCachedThreadPool();
        final int tasks = 8;//处理map的任务数
        final CountDownLatch countDownLatch = new CountDownLatch(tasks);//所有线程处理完毕后关闭线程池
        for (int i = 0; i < tasks ; i++) {
            int finalI = i;
            int purSize = temp.length/tasks;
            executorService.execute(()->{
                Map<String,List<Integer>> keyValues = new ConcurrentHashMap<>();
                int index = finalI*purSize;
                    for (int j = index; j < index+ purSize; j++) {
                        String element = temp[j].toLowerCase();
                        if (!element.equals("")&&!stopWords.contains(element)) {
                            if(keyValues.get(element)==null){
                                //list应该保证线程安全
                                List<Integer> list = new CopyOnWriteArrayList<>();
                                list.add(1);
                                keyValues.put(element,list);
                                continue;
                            }

                            keyValues.get(element).add(1);
                        }
                    }
                try {
                    blockingDeque.put(keyValues);
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

    }

    public static String[] generateUnfilteredWords(String string){
        char[] chars = string.toCharArray();
        for (int i = 0; i <chars.length ; i++) {
            char c = chars[i];
            if(!isCharacter(c,chars,i)){
                chars[i] = ' ';
            }
        }
        return String.valueOf(chars).split(" ");
    }

    private static boolean isCharacter(char c,char[] chars,int index){
            return Character.isUpperCase(c) || Character.isLowerCase(c) ||
                    (index > 0 && index < chars.length - 1
                            && c == '\'' && chars[index + 1] != ' ' && chars[index - 1] != ' ');
        }
}

