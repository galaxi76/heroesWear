package common;

import java.util.ArrayList;
import java.util.List;

public class MeasurementClass {
    private double samples_length ;

    private int index_minor = 0 ;
    private double base_ts = 0;
    private float m_sum = 0;
    private float current_mean = 0;
    private float current_samples = 10;

    private List<Float> list = new ArrayList<Float>();


    public MeasurementClass(double mean_interval, int c_samps)
    {
        samples_length = mean_interval;
        current_samples = c_samps;
    }

    public void add_mes(float smp,double ts)
    {
        current_mean = (current_mean*(current_samples-1) + smp)/current_samples;
        if (index_minor==0)
        {
            base_ts = ts;
        }
        m_sum += smp;
        index_minor++;
        if (( ts - base_ts ) > samples_length ){
            list.add ( m_sum/index_minor);
            index_minor = 0;
            m_sum = 0;
        }
    }

    public String get_current ()
    {
        return String.valueOf(current_mean);
    }

    public List<Float> get_last_samples ()
    {
        if (list.size() > 5)
            list.subList(0, list.size()).clear();
        return list;
    }

    public String get_last_samples_avg ()
    {
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