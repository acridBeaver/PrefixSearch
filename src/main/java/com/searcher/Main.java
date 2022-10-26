package com.searcher;

import com.searcher.core.dataworkers.CsvReader;
import com.searcher.core.searchers.PrefixSearcher;
import org.apache.commons.lang3.time.StopWatch;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String [] args) throws IOException {
        var csvManager = new CsvReader("./src/main/resources/airports.csv", 2);
        //String str = "5675,\"São Filipe Airport\",\"Sao Filipe, Fogo Island\",\"Cape Verde\",\"SFL\",\"GVSF\",14.8850002289,-24.4799995422,617,-1,\"U\",\"Atlantic/Cape_Verde\",\"airport\",\"OurAirports\"";
        //var str1 = "676,\"Szczecin-Goleniów \\\"Solidarność\\\" Airport\",\"Szczecin\",\"Poland\",\"SZZ\",\"EPSC\",53.584701538100006,14.902199745199999,154,1,\"E\",\"Europe/Warsaw\",\"airport\",\"OurAirports\"";
        var prefixSearch = new PrefixSearcher();
        var ar = csvManager.GetData();
        var timer = new StopWatch();
        timer.reset();
        timer.start();
        var searched = prefixSearch.Search(ar, "\"am");
        var result = csvManager.prepareData(searched);
        timer.stop();
        for (var str : result)
            System.out.println(str);
        System.out.println(result.length);
        System.out.println(timer.getTime(TimeUnit.MILLISECONDS));
    }
}
