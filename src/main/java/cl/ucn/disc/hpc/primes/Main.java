/*
 * MIT License
 *
 * Copyright (c) 2021 Fabian Rojas
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package cl.ucn.disc.hpc.primes;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class Main {

    // Counter with lock capacity
    private static final AtomicInteger counter = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException {

        log.debug("Starting the program");
        parallel();

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
     * For each core, compute the time that took it to count prime numbers between 2 and 5000000.
     * @throws InterruptedException
     */
    public static void parallel() throws InterruptedException {
        
        final int start = 2;
        final int end = 5 * 10 * 100 * 1000;

        // Max cores plus 1
        final int maxCores = Runtime.getRuntime().availableProcessors() * 2;
        log.debug("The max cores: {}", maxCores);
        log.info("Finding primes from {} to {} using {} cores.", start, end, maxCores);


        for (int j = 1; j <= maxCores; j++) {
            List<Long> times = new ArrayList<>(10);
            for (int i = 0; i < 20; i++) {

                counter.set(0);
                long time = findPrimesParallel(start, end, j);
                times.add(time);
                //log.info("Time with {} cores: {} milliseconds", j, time);

            }
            // Remove the min
            long min = Collections.min(times);
            times.remove(min);

            // Remove the max
            long max = Collections.max(times);
            times.remove(max);

            var average = times.stream().mapToLong(n -> n).average().getAsDouble();
            log.debug("The average time with {} cores is: {} milliseconds", j, average);
        }

    }

    /**
     * Count prime numbers between 2 numbers, with n amount of cores
     * @param ini - lower bound
     * @param end - higher bound
     * @param cores - cores to use
     * @return time spent
     * @throws InterruptedException if the job is in loop
     */
    private static long findPrimesParallel(long ini, long end, int cores) throws InterruptedException {
        final ExecutorService executor = Executors.newFixedThreadPool(cores);
        StopWatch sw = StopWatch.createStarted();
        
        for (long i = ini; i <= end; i++) {
            final long n = i;
            executor.submit( () -> {
                if (isPrime(n)) {
                    counter.getAndIncrement();
                }
            } );
        }


        executor.shutdown();
        int maxTime = 5;

        if (executor.awaitTermination(5, TimeUnit.MINUTES)) {
            long time = sw.getTime(TimeUnit.MILLISECONDS);
            //log.debug("Founded {} primes in {} milliseconds", counter, time);
            return time;
        } else {
            log.warn("The executor has not finished in {} minutes", maxTime);
        }
        
        throw new RuntimeException("The computation didn't finish");
    }
}
