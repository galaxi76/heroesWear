package common;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MeasurementClass {
    private double samples_length ;

    private int index_minor = 0 ;
    private double base_ts = 0;
    private float m_sum = 0;
    private float current_mean = 0;
    private float current_samples = 10;
    private float current_bvp = 0;
    private float prev_bvp = 0;
    private boolean initialized = false;
    private double last_peak = 0;
    private int last_direction = 0;

    private List<Float> list = new ArrayList<Float>();
    private List<Long> timeList = new ArrayList<Long>();


    public MeasurementClass(double mean_interval, int c_samps)
    {
        samples_length = mean_interval;
        current_samples = c_samps;
    }

    public void add_mes_bvp(float smp, double ts){
        if (initialized) {
            int direction = 0;
            current_bvp = smp;

            if(current_bvp > prev_bvp)
                direction = 1;
            else
                direction = -1;

            if ((last_direction == 1) && (direction == -1)) {

                if (last_peak != 0) {
                    double ibi =  ts - last_peak;
                    double paulse = ((60 / (ibi)));
                    Log.e("EmpaE4","paulse_IBI: " + ibi);
                    Log.e("EmpaE4","paulse: " + paulse);
                }

                last_peak = ts;
            }
            last_direction = direction;
        }

        initialized = true;
        prev_bvp = smp;
    }
    public void add_mes(float smp,double ts)
    {
//        current_mean = (current_mean*(current_samples-1) + smp)/current_samples;
        current_mean = smp;
        if (index_minor==0)
        {
            base_ts = ts;
        }
        m_sum += smp;
        index_minor++;
        if (( ts - base_ts ) > samples_length ){
            list.add ( m_sum/index_minor);
            timeList.add(System.currentTimeMillis());
            index_minor = 0;
            m_sum = 0;
        }
    }

    public String get_current ()
    {
        float sum = 0.0f;
        int count = 0;
        long currentTime = System.currentTimeMillis();
        for (int index = (timeList.size()-1); index >= 0; index--) {
            long t = timeList.get(index);

            if (currentTime - t < (1000*10)) {
                break;
            } else {
                count++;
                sum += list.get(index);
            }
        }
        if (count == 0)
        {
            return String.valueOf((current_mean));
        }
       return String.valueOf((sum / count));
    }

    public List<Float> get_last_samples ()
    {
        if (list.size() > 5)
            list.subList(0, list.size()).clear();
        return list;
    }

    public String get_last_samples_avg ()
    {
        int index = 0;

        long currentTime = System.currentTimeMillis();

        for (index = 0; index < timeList.size(); index++) {
            long t = timeList.get(index);

            if (currentTime - t < (1000*60*5)) {
                break;
            } else {
                list.remove(index);
                timeList.remove(index);
                index--;
            }
        }

        int counter = 0;
        float sum = 0;

        for( float f : list)
        {
            sum += f;
            counter += 1;
        }

        if (counter == 0)
            return "0";

        return String.valueOf( sum / counter );
    }
}