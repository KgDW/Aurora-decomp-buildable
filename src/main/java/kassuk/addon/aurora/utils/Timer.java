package kassuk.addon.aurora.utils;

public class Timer
{
    private final long current = -1L;
    private long time;
    
    public Timer() {
        this.time = -1L;
    }
    
    public boolean passedS(final double s) {
        return this.getMs(System.nanoTime() - this.time) >= (long)(s * 1000.0);
    }
    
    public boolean passedM(final double m) {
        return this.getMs(System.nanoTime() - this.time) >= (long)(m * 1000.0 * 60.0);
    }
    
    public boolean passedDms(final double dms) {
        return this.getMs(System.nanoTime() - this.time) >= (long)(dms * 10.0);
    }
    
    public boolean passedDs(final double ds) {
        return this.getMs(System.nanoTime() - this.time) >= (long)(ds * 100.0);
    }
    
    public boolean passedMs(final long ms) {
        return this.getMs(System.nanoTime() - this.time) >= ms;
    }
    
    public boolean passedNS(final long ns) {
        return System.nanoTime() - this.time >= ns;
    }
    
    public void setMs(final long ms) {
        this.time = System.nanoTime() - ms * 1000000L;
    }
    
    public long getPassedTimeMs() {
        return this.getMs(System.nanoTime() - this.time);
    }
    
    public Timer reset() {
        this.time = System.nanoTime();
        return this;
    }
    
    public long getMs(final long time) {
        return time / 1000000L;
    }
    
    public boolean sleep(final long time) {
        if (System.nanoTime() / 1000000L - time >= time) {
            this.reset();
            return true;
        }
        return false;
    }
    
    public boolean hasReached(final long delay) {
        final long currentTimeMillis = System.currentTimeMillis();
        this.getClass();
        return currentTimeMillis + 1L >= delay;
    }
    
    public boolean hasReached(final long delay, final boolean reset) {
        if (reset) {
            this.reset();
        }
        final long currentTimeMillis = System.currentTimeMillis();
        this.getClass();
        return currentTimeMillis + 1L >= delay;
    }
}
