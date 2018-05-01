package app.heroeswear.com.heroesfb;

public class MeasurementData {
    public String gcr;
    public String gcrAvg;
    public String heartRate;
    public String heartRateAvg;
    public String timestamp;


    public MeasurementData() {
    }

    public MeasurementData(String gsr,
                           String gsrAvg,
                           String heartRate,
                           String heartRateAvg,
                           String timestamp) {

        this.gcr = gsr;
        this.gcrAvg = gsrAvg;
        this.heartRate = heartRate;
        this.heartRateAvg = heartRateAvg;
        this.timestamp = timestamp;
    }
}
