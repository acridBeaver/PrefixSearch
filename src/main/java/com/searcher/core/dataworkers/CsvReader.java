package com.searcher.core.dataworkers;

import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;
import org.springframework.beans.factory.annotation.Value;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class CsvReader implements DataManager{
    private final Multimap<String, Long> content;
    private final String filePath;
    private final int columnNumber;

    public CsvReader(@Value("${app.filename}") String filePath, @Value("${app.column-number") int columnNumber){
        this.filePath = filePath;
        if (columnNumber < 1)
            throw new IllegalArgumentException("Номер колонки должен быть > 0");
        this.columnNumber = columnNumber - 1;
        content = TreeMultimap.create();
    }
    @Override
    public String[] GetData(){
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            long offset = 0;
            String str;
            str = reader.readLine();
            while (str != null){
                String[] line = split(str);
                if (columnNumber >= line.length)
                    continue;
                content.put(line[columnNumber], offset);
                offset += str.getBytes(StandardCharsets.UTF_8).length + 1;
                str = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
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

    public String[] prepareData(String[] data) {
        var result = new ArrayList<String>();
        try {
            var raf = new RandomAccessFile(filePath, "r");

            for (String key : data)
                for (long offset : content.get(key)) {
                    raf.seek(offset);
                    var line = raf.readLine();
                    result.add(key + line);
                }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.toArray(new String[0]);
    }
}
