package com.searcher.core;

import com.searcher.core.dataworkers.DataManager;
import com.searcher.core.searchers.Searcher;
import org.apache.commons.lang3.time.StopWatch;

import java.util.concurrent.TimeUnit;

public class SearchEngine {
    private final String[] data;
    private final DataManager dataManager;
    private final Searcher searcher;
    private final StopWatch timer;

    public SearchEngine(DataManager dataManager, Searcher searcher) {
        this.dataManager = dataManager;
        data = dataManager.GetData();
        this.searcher = searcher;
        this.timer = new StopWatch();
    }

    public String[] getOccurrences (String prefix){
        timer.reset();
        timer.start();
        var occurrences = searcher.search(data, prefix);
        var result = dataManager.prepareData(occurrences);
        timer.stop();
        return result;
    }

    public long getElapsedTime() {
        return timer.getTime(TimeUnit.MILLISECONDS);
    }

}
