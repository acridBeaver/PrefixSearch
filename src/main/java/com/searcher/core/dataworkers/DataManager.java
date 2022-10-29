package com.searcher.core.dataworkers;

public interface DataManager {
    String[] GetData(int columnNumber);
    String[] prepareData(String[] data);
    Boolean getIsString();
}
