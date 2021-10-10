package cl.ucn.disc.hpc.primes;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class Main {

    private static final AtomicInteger counter = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException {

        log.debug("Starting the program");
        parallel();

        /*
        final long start = 2;
        final long end = 5 * 10 * 100 * 1000;

        int N = 12;
        runningAverageTime(N, start, end);*/
    }

    /**
     * Detect if a number is prime.
     * @param n to test.
     * @return true is n is prime
     */
    private static boolean isPrime (final long n) {

        if (n <= 0) {
            throw new IllegalArgumentException("Error in n: can't process negative numbers");
        }

        if (n == 1) {
            return false;
        }

        if (n==2) {
            return true;
        }

        if (n % 2 == 0) {
            return false;
        }

        for (long i= 3; i*i <= n; i += 2) {

            if (n % i == 0 ) {
                return false;
            }
        }

        return true;
    }

    /**
     * Compute the number of primes between two numbers
     * @param ini bound
     * @param end bound
     * @return running time
     */
    private static long primesBetween(long ini, long end) {

        //Stopwatch
        StopWatch sw = StopWatch.createStarted();

        long counter = 0;
        for (long n = ini; n <= end; n++) {
            if (isPrime(n)){
                counter++;
            }
        }
        long time = sw.getTime(TimeUnit.MILLISECONDS);
        log.debug("Between {} and {}, the number of primes is: {}", ini, end, counter);
        log.debug("Running time: {} milliseconds", time);
        return time;
    }

    /**
     * 
     * @param N of computations of primesBetween
     * @param start bound
     * @param end bound
     * @return average time
     */
    private static double runningAverageTime(int N, long start, long end) {
        List<Long> times = new ArrayList<>(N);

        for (int i = 0; i < N; i++) {
            long time = primesBetween(start, end);
            times.add(time);

        }

        // Remove the min
        long min = Collections.min(times);
        times.remove(min);

        // Remove the max
        long max = Collections.max(times);
        times.remove(max);

        var average = times.stream().mapToLong(n -> n).average().getAsDouble();
        log.debug("The average time is: {} milliseconds", average);
        return average;
    }

    public static void parallel() throws InterruptedException {

        StopWatch sw = StopWatch.createStarted();

        final int start = 2;
        final int end = 5 * 10 * 100 * 1000;

        final int maxCores = Runtime.getRuntime().availableProcessors();
        log.debug("The max cores: {}", maxCores);
        log.info("Finding primes from {} to {} using {} cores.", start, end, maxCores);

        final ExecutorService executor = Executors.newFixedThreadPool(maxCores);

        for (int i = start; i <= end; i++) {
            final int n = i;
            executor.submit( () -> {
                if (isPrime(n)) {
                    counter.getAndIncrement();
                }
            } );
        }


        executor.shutdown();
        int maxTime = 5;

        if (executor.awaitTermination(5, TimeUnit.MINUTES)) {
            log.debug("Founded {} primes in {} milliseconds", counter, sw.getTime(TimeUnit.MILLISECONDS));
        } else {
            log.warn("The executor has not finished in {} minutes", maxTime);
        }


    }
}
