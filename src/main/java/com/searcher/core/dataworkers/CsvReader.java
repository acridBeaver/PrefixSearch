package com.searcher.core.dataworkers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;

public class CsvReader implements DataManager{
    private final MultiTree<String, Long> content;
    private final String filePath;
    private final int columnNumber;
    private Boolean isString;

    public CsvReader(String filePath, int columnNumber){
        this.filePath = filePath;
        this.columnNumber = columnNumber - 1;
        content = new MultiTree<>();
        isString = false;
    }

    @Override
    public String[] GetData(){
        if (columnNumber < 0)
            throw new IllegalArgumentException("Номер колонки должен быть > 0");
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            var offset = 0L;
            var str = reader.readLine();
            if (columnNumber >= str.split(",(?! )").length)
                throw new IllegalArgumentException("максимальный номер колонки = " + str.split(",(?! )").length);
            if (str.split(",(?! )")[columnNumber].startsWith("\""))
                isString = true;
            while (str != null){
                String[] line = str.split(",(?! )");
                content.put(line[columnNumber], offset);
                offset += str.getBytes(StandardCharsets.UTF_8).length + 1;
                str = reader.readLine();
            }

            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return content.keySet().toArray(new String[0]);
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
                result.add(key);
                result.add(line);
            }

        raf.close();

        return result.toArray(new String[0]);
    }

    private String[] numberOrderSort(String[] data) throws IOException {
        var raf = new RandomAccessFile(filePath, "r");
        var result = new ArrayList<String>();
        MultiTree<Double, String> tree = new MultiTree<>();
        for (String key : data) {
            Double numberKey = Double.valueOf(key);
            for (long offset : content.get(key)) {
                raf.seek(offset);
                var line = raf.readLine();
                tree.put(numberKey, line);
            }
        }

        raf.close();

        DecimalFormat df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
        df.setMaximumFractionDigits(15);
        for (Double key : tree.keySet()){
            for (String line : tree.get(key)) {
                result.add(df.format(key));
                result.add(line);
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
