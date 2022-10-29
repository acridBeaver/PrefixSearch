package com.searcher.core;

import com.searcher.core.dataworkers.DataManager;
import com.searcher.core.searchers.Searcher;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Scope("prototype")
public class SearchEngine {
    private String[] data;
    private final DataManager dataManager;
    private final Searcher searcher;
    private final StopWatch timer;

    public SearchEngine(DataManager dataManager, Searcher searcher) {
        this.dataManager = dataManager;
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

    public void setColumn(int columnNumber) {
        data = dataManager.GetData(columnNumber);
        searcher.setCompareMode(dataManager.getIsString());
    }

    public long getElapsedTime() {
        return timer.getTime(TimeUnit.MILLISECONDS);
    }

}
