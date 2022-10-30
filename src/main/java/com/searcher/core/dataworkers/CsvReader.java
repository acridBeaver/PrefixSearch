package com.searcher.core.dataworkers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class CsvReader implements DataManager{
    private final MultiTree<String, Long> content;
    private final String filePath;
    private final int columnNumber;

    public CsvReader(String filePath, int columnNumber){
        this.filePath = filePath;
        this.columnNumber = columnNumber - 1;
        content = new MultiTree<>();
    }

    @Override
    public String[] GetData(){
        if (columnNumber < 0)
            throw new IllegalArgumentException("Номер колонки должен быть > 0");
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            var offset = 0L;
            var line = reader.readLine();

            if (columnNumber >= line.split(",(?! )").length)
                throw new IllegalArgumentException(
                        "максимальный номер колонки = " + line.split(",(?! )").length);

            while (line != null){
                String[] contentLine = line.split(",(?! )");
                content.put(contentLine[columnNumber].replace("\"", "").toLowerCase(), offset);
                offset += line.getBytes(StandardCharsets.UTF_8).length + 1;
                line = reader.readLine();
            }

            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return content.keySet().toArray(new String[0]);
    }

    @Override
    public String[] prepareData(String[] data) {
        ArrayList<String> result;
        try {
            var numericOrderTree = new TreeMap<Double, String>();
            var stringData = new ArrayList<String>();
            double numberKey;
            for (String key : data) {
                try {
                    numberKey = Double.parseDouble(key);
                    numericOrderTree.put(numberKey, key);
                } catch (NumberFormatException e) {
                    stringData.add(key);
                }
            }

            var raf = new RandomAccessFile(filePath, "r");
            result = getStringData(raf, stringData);
            result.addAll(numberOrderSort(raf, numericOrderTree));
            raf.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result.toArray(new String[0]);
    }

    private ArrayList<String> getStringData(
            RandomAccessFile raf, ArrayList<String> stringData) throws IOException {
        var result = new ArrayList<String>();
        for (String key : stringData)
            for (long offset : content.get(key)) {
                raf.seek(offset);
                var line = raf.readLine();
                result.add(String.format("%s[%s]", line.split(",(?! )")[columnNumber], line));
            }

        return result;
    }

    private ArrayList<String> numberOrderSort(
            RandomAccessFile raf, TreeMap<Double, String> numericOrderTree) throws IOException {
        var result = new ArrayList<String>();
        for (Double key : numericOrderTree.keySet()){
            var fileKey = numericOrderTree.get(key);

            for (Long offset : content.get(fileKey)) {
                    raf.seek(offset);
                    var line = raf.readLine();
                    result.add(String.format("%s[%s]", line.split(",(?! )")[columnNumber], line));
                }
        }

        return result;
    }

    public static class MultiTree<K, V> {
        private final TreeMap<K, ArrayList<V>> treeMap;

        public MultiTree() {
            this.treeMap = new TreeMap<>();
        }

        public void put(K key, V value) {
            if (!treeMap.containsKey(key))
                treeMap.put(key, new ArrayList<>());
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
