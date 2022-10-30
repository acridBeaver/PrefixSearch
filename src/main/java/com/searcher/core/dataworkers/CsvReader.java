package com.searcher.core.dataworkers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;

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
            var line = reader.readLine();
            if (columnNumber >= line.split(",(?! )").length)
                throw new IllegalArgumentException("максимальный номер колонки = " + line.split(",(?! )").length);
            if (line.split(",(?! )")[columnNumber].startsWith("\""))
                isString = true;
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
                result.add(line.split(",(?! )")[columnNumber]);
                result.add(line);
            }

        raf.close();

        return result.toArray(new String[0]);
    }

    private String[] numberOrderSort(String[] data) throws IOException {
        var result = new ArrayList<String>();
        var attitude = new HashMap<Double, String>();
        var attitudeKey = -3000d;
        var numericOrderTree = new TreeMap<Double, String>();
        double numberKey;
        for (String key : data) {
            try {
                numberKey = Double.parseDouble(key);
            } catch (NumberFormatException e) {
                attitude.put(attitudeKey, key);
                numberKey = attitudeKey;
                attitudeKey--;
            }
            numericOrderTree.put(numberKey, key);
        }

        var raf = new RandomAccessFile(filePath, "r");
        for (Double key : numericOrderTree.keySet()){
            String fileKey;
            if (key <= -3000d)
                fileKey = attitude.get(key);
            else
                fileKey = numericOrderTree.get(key);

            for (Long offset : content.get(fileKey)) {
                    raf.seek(offset);
                    var line = raf.readLine();
                    result.add(line.split(",(?! )")[columnNumber]);
                    result.add(line);
                }
        }
        raf.close();
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
