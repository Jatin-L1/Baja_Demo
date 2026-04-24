package com.example.demo.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BfhlService {

    /**
     * Returns a Fibonacci series of exactly n numbers (0-indexed).
     * fibonacci(7) → [0, 1, 1, 2, 3, 5, 8]
     */
    public List<Long> calculateFibonacci(int n) {
        List<Long> fib = new ArrayList<>();
        if (n <= 0) return fib;
        fib.add(0L);
        if (n == 1) return fib;
        fib.add(1L);
        for (int i = 2; i < n; i++) {
            fib.add(fib.get(i - 1) + fib.get(i - 2));
        }
        return fib;
    }

    /**
     * Filters and returns only prime numbers from the input list.
     */
    public List<Integer> filterPrimes(List<Integer> numbers) {
        return numbers.stream()
                .filter(this::isPrime)
                .collect(Collectors.toList());
    }

    private boolean isPrime(int n) {
        if (n <= 1) return false;
        if (n <= 3) return true;
        if (n % 2 == 0 || n % 3 == 0) return false;
        for (int i = 5; (long) i * i <= n; i += 6) {
            if (n % i == 0 || n % (i + 2) == 0) return false;
        }
        return true;
    }

    /**
     * Returns the LCM of all numbers in the list.
     * Uses long to prevent integer overflow for large inputs.
     */
    public long calculateLcm(List<Integer> numbers) {
        long result = numbers.get(0);
        for (int i = 1; i < numbers.size(); i++) {
            result = lcm(result, numbers.get(i));
        }
        return result;
    }

    private long lcm(long a, long b) {
        return (a / gcd(a, b)) * b; // divide first to reduce overflow risk
    }

    /**
     * Returns the HCF (GCD) of all numbers in the list.
     */
    public long calculateHcf(List<Integer> numbers) {
        long result = numbers.get(0);
        for (int i = 1; i < numbers.size(); i++) {
            result = gcd(result, numbers.get(i));
        }
        return result;
    }

    private long gcd(long a, long b) {
        a = Math.abs(a);
        b = Math.abs(b);
        while (b != 0) {
            long temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }
}
