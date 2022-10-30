package com.searcher;

import com.searcher.core.SearchEngine;
import com.searcher.core.dataworkers.CsvReader;
import com.searcher.core.dataworkers.DataManager;
import com.searcher.core.searchers.PrefixSearcher;
import com.searcher.core.searchers.Searcher;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;

public class MainApplication {
    private static SearchEngine searchEngine;

    public static void main(String [] args) {
        if (args.length > 0 && args[0].matches("\\d+")) {
            var property = new Properties();
            try {
                var fis = new FileInputStream("src/main/resources/config.properties");
                property.load(fis);
                fis.close();
            } catch (IOException e) {
                System.out.println("Файл config.properties отсутствует");
            }
            engine(new CsvReader(property.getProperty("file.path"), Integer.parseInt(args[0])),
                    new PrefixSearcher());
            run();
        }
        else
            System.out.println("Необходимо ввести  аргумент: целое число номер колонки для поиска");
    }

    public static void engine(DataManager dataManager, Searcher searcher) {
        searchEngine = new SearchEngine(dataManager, searcher);
    }

    public static void run() {
        var inputScanner = new Scanner(System.in);
        System.out.print("Введите строку: ");
        var prefix = inputScanner.nextLine();
        while (!prefix.equals("!quit")) {
            String[] result = searchEngine.getOccurrences(prefix);
            for (String line : result)
                System.out.println(line);

            System.out.printf("\nКоличество найденных строк: %d\t", result.length);
            System.out.printf("Время, затраченное на поиск: %d мс\n", searchEngine.getElapsedTime());

            System.out.print("Введите строку: ");
            prefix = inputScanner.nextLine();
        }

        System.out.println("Завершение программы");
    }
}