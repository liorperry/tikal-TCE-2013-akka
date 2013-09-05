package akka.pipe;

/**
 * User: liorpe
 * Date: 9/2/13
 */

import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PipelineDemo {
    public static final int MAX_COUNT = 1000;
    private static final Logger LOGGER = Logger.getLogger(PipelineDemo.class.getName());

    static AtomicInteger counter = new AtomicInteger(0);

    private static final BlockingQueue<Integer> randomsQueue = new LinkedBlockingQueue<Integer>();
    private static final BlockingQueue<String> strQueue = new LinkedBlockingQueue<String>();

    private static class Generator extends Thread {
        @Override
        public void run() {
            Random r = new Random();
            while (true) {
                try {
                    randomsQueue.add(r.nextInt(999999));
                    randomsQueue.add(r.nextInt(19));
                    if(counter.get() > MAX_COUNT) {
                        throw new InterruptedException("completed");
                    }
                } catch (Exception ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                    stop();
                }
            }
        }
    }

    private static class Calculator extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    Integer base = randomsQueue.take();
                    Integer exp = randomsQueue.take();
                    double res = Math.pow(base, exp);
                    strQueue.put(Double.toString(res));
                    if(counter.get()> MAX_COUNT) {
                        throw new InterruptedException("completed");
                    }
                } catch (InterruptedException ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                    stop();
                }
            }
        }
    }

    private static class Filter extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    String str = strQueue.take();
                    if (str.contains("2222")) {
                        int i = counter.incrementAndGet();

                        if((i - MAX_COUNT/10)%10 == 0) {
                            System.out.print("*");
                        }
                        if(i > MAX_COUNT) {
                            throw new InterruptedException("completed");
                        }
                    }
                } catch (InterruptedException ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                    stop();
                }
            }
        }
    }

    public static void main(String[] args) {
        new Generator().start();
        new Calculator().start();
        new Filter().start();
    }
}