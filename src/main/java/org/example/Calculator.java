package org.example;

import java.util.ArrayList;
import java.util.List;

public class Calculator {
    private H2DB h2DB = new H2DB();

    @Cachable(H2DB.class)
    public List<Integer> fibonacci(int n) {
        // проверяем наличие результата в кэше
        List<Integer> cashedResult = h2DB.getCachedResult("fibonacci", n);
        if (cashedResult != null) {
            System.out.println("Результат получен из кэша.");
            return cashedResult;    
        }

        System.out.println("Результат не получен из кэша, проводим вычисление.");
        List<Integer> result = calculateFibonacci(n);

        //Записываем результат в кэш
        h2DB.cacheResault("fibonacci", n, result);
        return result;
    }

    private List<Integer> calculateFibonacci(int n) {
        List<Integer> result = new ArrayList<>();
        int a = 0, b = 1;
        for (int i = 0; i < n; i++) {
            result.add(i);
            int next = a + b;
            a = b;
            b = next;
        }
        return result;
    }
}