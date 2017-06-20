package devicerocks.accelerate;

/**
 * Created by plasmashadow on 6/20/17.
 */

public class SpeedData {

    private long timestamp;
    private double x;

    public SpeedData(long timestamp, double speed) {
        this.timestamp = timestamp;
        this.x = speed * 3.6f;
    }

    public double getX(){
        return this.x;
    }

    public long getTimestamp(){
        return this.timestamp;
    }
}