package jblocks;

public class Timer {
    long start;
    long target;

    public Timer() {
        start = System.currentTimeMillis();
    }

    public Timer(double seconds) {
        start = System.currentTimeMillis();
        target = start + (long) (1000 * seconds);
    }

    public Timer(long miliseconds) {
        start = System.currentTimeMillis();
        target = start + miliseconds;
    }

    public void reset() {
        start = System.currentTimeMillis();
    }

    public void start(long miliseconds) {
        start = System.currentTimeMillis();
        target = start + miliseconds;
    }

    public boolean ended() {
        return System.currentTimeMillis() >= target;
    }

    public double elapsed() {
        return (System.currentTimeMillis() - start) / 1000.0;
    }
}
