package com.searcher.core.dataworkers;

import com.google.common.collect.TreeMultimap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;

@Component
@Scope("prototype")
@PropertySource("classpath:application.yml")
public class CsvReader implements DataManager{
    private final MultiTree<String, Integer> content;
    private final String filePath;
    private Boolean isString;

    public CsvReader(@Value("${app.filename}") String filePath){
        this.filePath = filePath;
        content = new MultiTree<>();
        isString = false;
    }

    @Override
    public String[] GetData(int columnNumber){
        if (columnNumber < 1)
            throw new IllegalArgumentException("Номер колонки должен быть > 0");
        columnNumber--;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            int offset = 0;
            var str = reader.readLine();
            if (columnNumber >= split(str).length)
                throw new IllegalArgumentException("максимальный номер колонки = " + split(str).length);
            if (split(str)[columnNumber].startsWith("\""))
                isString = true;
            while (str != null){
                String[] line = split(str);
                content.put(line[columnNumber], offset);
                offset += str.getBytes(StandardCharsets.UTF_8).length + 1;
                str = reader.readLine();

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return content.keySet().toArray(new String[0]);
    }

    private String[] split(String str){
        var result = new ArrayList<String>();
        var charArr = str.toCharArray();
        var firstChar = 0;
        var lastChar = 0;
        for (lastChar = 1; lastChar < str.length(); lastChar++) {
            if ((charArr[lastChar] == ',' && charArr[lastChar + 1] != ' ')) {
                result.add(str.substring(firstChar, lastChar));
                firstChar = lastChar + 1;
            }
        }
        result.add(str.substring(firstChar, lastChar));
        return result.toArray(new String[0]);
    }

    @Override
    public String[] prepareData(String[] data) {
        String[] result = new String[0];
        try {
            result = isString ? stringOrderSort(data) : numberOrderSort(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private String[] stringOrderSort(String[] data) throws IOException {
        var raf = new RandomAccessFile(filePath, "r");
        var result = new ArrayList<String>();
        for (String key : data)
            for (long offset : content.get(key)) {
                raf.seek(offset);
                var line = raf.readLine();
                result.add(String.format("%s[%s]", key, line));
            }
        return result.toArray(new String[0]);
    }

    private String[] numberOrderSort(String[] data) throws IOException {
        var raf = new RandomAccessFile(filePath, "r");
        var result = new ArrayList<String>();
        TreeMultimap<Double, String> tree = TreeMultimap.create();
        for (String key : data) {
            Double numberKey = Double.valueOf(key);
            for (long offset : content.get(key)) {
                raf.seek(offset);
                var line = raf.readLine();
                tree.put(numberKey, line);
            }
        }

        DecimalFormat df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
        df.setMaximumFractionDigits(40);
        for (Double key : tree.keySet()){
            for (String line : tree.get(key)) {
                result.add(String.format("%s[%s]", df.format(key), line));
            }
        }
        return result.toArray(new String[0]);
    }

    public Boolean getIsString() {
        return isString;
    }

    public static class MultiTree<K, V> {
        private final TreeMap<K, ArrayList<V>> treeMap;

        public MultiTree() {
            this.treeMap = new TreeMap<>();
        }

        public void put(K key, V value) {
            if (!treeMap.containsKey(key))
                treeMap.put(key, new ArrayList<V>());
            treeMap.get(key).add(value);
        }

        public ArrayList<V> get(K key) {
            return treeMap.get(key);
        }

        public Set<K> keySet(){
            return treeMap.keySet();
        }
    }
}
