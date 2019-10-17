package com.company;

import java.util.Map;

public class TestMain {
    public static void main(String[] args) {
        String res = FileOperator.readFile("./shakespeare.txt");
        MapFunc mapper = new MapFunc();
        ReduceFunc reducer = new ReduceFunc();
        long begin = System.currentTimeMillis();
        new Thread(()-> mapper.map(res)).start();
        Map<String,Integer> reduce = reducer.reduce();
        System.out.println("processing takes "+String.valueOf(((double) System.currentTimeMillis()-begin)/1000)+"s");
        System.out.println(FileOperator.outputResultToFile(FileOperator.sort(reduce)));

    }
}
