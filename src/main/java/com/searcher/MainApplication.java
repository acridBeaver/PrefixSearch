package com.searcher;

import com.searcher.core.SearchEngine;
import com.searcher.core.dataworkers.CsvReader;
import com.searcher.core.dataworkers.DataManager;
import com.searcher.core.searchers.PrefixSearcher;
import com.searcher.core.searchers.Searcher;

import java.util.Scanner;

public class MainApplication {
    private static SearchEngine searchEngine;

    public static void main(String [] args) {
        if (args.length > 0 && args[0].matches("\\d+")) {
            engine(new CsvReader("./src/main/resources/airports.dat"),
                    new PrefixSearcher());
            run(args);
        }
        else
            System.out.println("Необходимо ввести  аргумент: целое число номер колонки для поиска");
    }

    public static void engine(DataManager dataManager, Searcher searcher) {
        searchEngine = new SearchEngine(dataManager, searcher);
    }

    public static void run(String... args) {
        searchEngine.setColumn(Integer.parseInt(args[0]));
        var inputScanner = new Scanner(System.in);
        System.out.print("Введите строку: ");
        var prefix = inputScanner.nextLine();
        while (!prefix.equals("!quit")) {
            String[] result = searchEngine.getOccurrences(prefix);
            for (var str : result)
                System.out.println(str);

            System.out.printf("\nКоличество найденных строк: %d\t", result.length);
            System.out.printf("Время, затраченное на поиск: %d мс\n", searchEngine.getElapsedTime());

            System.out.print("Введите строку: ");
            prefix = inputScanner.nextLine();
        }

        System.out.println("Завершение программы...");
    }
}