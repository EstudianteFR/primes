package cl.ucn.disc.hpc.primes;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;

import java.util.concurrent.TimeUnit;

@Slf4j
public class Main {

    public static void main(String[] args) {

        //Stopwatch
        StopWatch sw = StopWatch.createStarted();
        log.debug("Starting the program");
        long n = 87178291199L;
        log.debug("{} isPrime: {}", n, isPrime(n));

        log.info("Done in {}.", sw.getTime(TimeUnit.MILLISECONDS));

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

}
