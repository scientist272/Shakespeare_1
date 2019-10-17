package com.company;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FileOperator {
    public static String readFile(String path){
        StringBuilder res = new StringBuilder();
        try(InputStreamReader read = new InputStreamReader(new FileInputStream(path));
            BufferedReader reader = new BufferedReader(read)){
            while(true){
                String line = reader.readLine();
                if(line==null)
                    break;
                res.append(line).append(" ");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res.toString();
    }

    public static String outputResultToFile(List<Map.Entry<String,Integer>> sortedReduce){
        File writeName = new File("./output.txt");
        try {
            if(writeName.exists()){
                if(!writeName.delete())
                    throw new IOException("File already exist and can't be deleted");
            }
            if(!writeName.createNewFile())
                throw new IOException("Failed in creating file");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try(BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(writeName)))) {
            for(Map.Entry<String,Integer> entry:sortedReduce){
                out.write(entry.getKey()+","+entry.getValue());
                out.newLine();
            }
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Succeed in computing to file, path: "+writeName;
    }
    public static List<Map.Entry<String,Integer>> sort(Map<String,Integer> reduce){
        List<Map.Entry<String, Integer>> sortedReduce = new ArrayList<>(reduce.entrySet());
            sortedReduce.sort((e1, e2) -> -(e1.getValue() - e2.getValue()));
            return sortedReduce;
    }
}
