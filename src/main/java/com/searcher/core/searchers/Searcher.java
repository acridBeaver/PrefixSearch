package com.searcher.core.searchers;

public interface Searcher {
    String[] search(String[] target, String prefix);
    void setCompareMode(Boolean isString);
}
