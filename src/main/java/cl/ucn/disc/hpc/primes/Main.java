package cl.ucn.disc.hpc.primes;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
public class Main {

    public static void main(String[] args) {

        log.debug("Starting the program");



        final long start = 2;
        final long end = 2 * 100 * 1000;

        int N = 5;
        runningAverageTime(N, start, end);
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

        for (long i= 2; i < n; i++) {

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
}
