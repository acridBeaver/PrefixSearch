package com.searcher;

import com.google.common.primitives.Ints;
import com.searcher.core.SearchEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

import java.util.Scanner;

@SpringBootApplication
@PropertySource("classpath:application.yml")
public class MainApplication implements CommandLineRunner {
    private SearchEngine searchEngine;

    public static void main(String [] args) {
        if (args.length > 0 && Ints.tryParse(args[0]) != null)
            SpringApplication.run(MainApplication.class, args);
        else
            System.out.println("Необходимо ввести  аргумент: целое число номер колонки для поиска");
    }

    @Autowired
    public void engine(SearchEngine engine) {
        searchEngine = engine;
    }

    @Override
    public void run(String... args) {
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