package tsukasa.finatic.schooltracking.Model;

public class MyLocation {
    private int accuracy,altitude,bearing,bearingAccuracyDefrees,speed,speedAcccuracyMeterPerSecond,verticalAccuracyMeters;
    private boolean complete,fromMockProvider;
    private String provider;
    private long time,elapsedRealtimeNanos;
    private double latitude, longitude;

    public MyLocation(){

    }

    public int getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(int accuracy) {
        this.accuracy = accuracy;
    }

    public int getAltitude() {
        return altitude;
    }

    public void setAltitude(int altitude) {
        this.altitude = altitude;
    }

    public int getBearing() {
        return bearing;
    }

    public void setBearing(int bearing) {
        this.bearing = bearing;
    }

    public int getBearingAccuracyDefrees() {
        return bearingAccuracyDefrees;
    }

    public void setBearingAccuracyDefrees(int bearingAccuracyDefrees) {
        this.bearingAccuracyDefrees = bearingAccuracyDefrees;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getSpeedAcccuracyMeterPerSecond() {
        return speedAcccuracyMeterPerSecond;
    }

    public void setSpeedAcccuracyMeterPerSecond(int speedAcccuracyMeterPerSecond) {
        this.speedAcccuracyMeterPerSecond = speedAcccuracyMeterPerSecond;
    }

    public int getVerticalAccuracyMeters() {
        return verticalAccuracyMeters;
    }

    public void setVerticalAccuracyMeters(int verticalAccuracyMeters) {
        this.verticalAccuracyMeters = verticalAccuracyMeters;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public boolean isFromMockProvider() {
        return fromMockProvider;
    }

    public void setFromMockProvider(boolean fromMockProvider) {
        this.fromMockProvider = fromMockProvider;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getElapsedRealtimeNanos() {
        return elapsedRealtimeNanos;
    }

    public void setElapsedRealtimeNanos(long elapsedRealtimeNanos) {
        this.elapsedRealtimeNanos = elapsedRealtimeNanos;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

}
