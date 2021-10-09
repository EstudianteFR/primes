package cl.ucn.disc.hpc.primes;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;

import java.util.concurrent.TimeUnit;

@Slf4j
public class Main {

    public static void main(String[] args) {

        log.debug("Starting the program");



        final long start = 2;
        final long end = 2 * 100 * 1000;

        long time = primesBetween(start, end);


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
        log.debug("Between {} and {}, the number of primes is: {}", ini, end, counter);
        return sw.getTime(TimeUnit.MILLISECONDS);
    }

}
