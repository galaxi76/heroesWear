package common;

import android.app.IntentService;
import android.content.Intent;
import android.media.MediaExtractor;
import android.util.Log;

import com.empatica.empalink.ConnectionNotAllowedException;
import com.empatica.empalink.EmpaDeviceManager;
import com.empatica.empalink.EmpaticaDevice;
import com.empatica.empalink.config.EmpaSensorStatus;
import com.empatica.empalink.config.EmpaSensorType;
import com.empatica.empalink.config.EmpaStatus;
import com.empatica.empalink.delegate.EmpaDataDelegate;
import com.empatica.empalink.delegate.EmpaStatusDelegate;
import app.heroeswear.com.heroesfb.FirebaseManager;
import app.heroeswear.com.heroesfb.MeasurementData;

import java.security.Timestamp;
import java.util.Timer;
import java.util.TimerTask;


public class EmpaE4 extends IntentService implements EmpaDataDelegate, EmpaStatusDelegate {

    private static final String EMPATICA_API_KEY = "b1e5dc72e0d64626ba5138285a78480e"; //ADD API KEY
    private static final long START_DELAY_MS = (1000*60*6);
    //private static final long START_DELAY_MS = (1000*60);
    private static final long EMPA_TIMER_PERIOD_MS = (1000*60);
    //private static final long EMPA_TIMER_PERIOD_MS = (1000*60);
    private EmpaDeviceManager deviceManager = null;
    private MeasurementClass measure_gsr = new MeasurementClass(60,10);
    private MeasurementClass measure_heartRate = new MeasurementClass(60,10);
    private Timer timer = new Timer();
    private FirebaseManager fbManager;

    /**
     * A constructor is required, and must call the super IntentService(String)
     * constructor with a name for the worker thread.
     */
    public EmpaE4() {
        super("EmpaE4");
    }

    /**
     * The IntentService calls this method from the default worker thread with
     * the intent that started the service. When this method returns, IntentService
     * stops the service, as appropriate.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        initEmpaticaDeviceManager();

        fbManager = FirebaseManager.Companion.newInstance();

        timer = new Timer();
        timer.scheduleAtFixedRate(new empaTimer(),START_DELAY_MS , EMPA_TIMER_PERIOD_MS);
    }

    private void initEmpaticaDeviceManager() {
        // Create a new EmpaDeviceManager. MainActivity is both its data and status delegate.
        deviceManager = new EmpaDeviceManager(getApplicationContext(), this, this);

        // Initialize the Device Manager using your API key. You need to have Internet access at this point.
        deviceManager.authenticateWithAPIKey(EMPATICA_API_KEY);
    }

    /*
        @Override
        protected void onPause() {
            super.onPause();
            if (deviceManager != null) {
                deviceManager.stopScanning();
            }
        }
*/
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        if (deviceManager != null) {
//            deviceManager.cleanUp();
//        }
//    }

    @Override
    public void didDiscoverDevice(EmpaticaDevice bluetoothDevice, String deviceName, int rssi, boolean allowed) {
        // Check if the discovered device can be used with your API key. If allowed is always false,
        // the device is not linked with your API key. Please check your developer area at
        // https://www.empatica.com/connect/developer.php
        if (allowed) {
            // Stop scanning. The first allowed device will do.
            deviceManager.stopScanning();
            try {
                // Connect to the device
                deviceManager.connectDevice(bluetoothDevice);

            } catch (ConnectionNotAllowedException e) {
                // This should happen only if you try to connect when allowed == false.
            }
        }
    }

    @Override
    public void didRequestEnableBluetooth() {
        // Request the user to enable Bluetooth
        //Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        //startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    }

    /*
        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            // The user chose not to enable Bluetooth
            if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
                // You should deal with this
                return;
            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    */
    @Override
    public void didUpdateSensorStatus(@EmpaSensorStatus int status, EmpaSensorType type) {

        didUpdateOnWristStatus(status);
    }

    @Override
    public void didUpdateStatus(EmpaStatus status) {
        // The device manager is ready for use
        Log.e("moshe", "status is " + status);
        if (status == EmpaStatus.READY) {
            // Start scanning
            deviceManager.startScanning();
            Log.e("moshe", "starting to scan for wrist bands");

        } else if (status == EmpaStatus.CONNECTED) {
            Log.e("moshe", "connected to wrist band");

        } else if (status == EmpaStatus.DISCONNECTED) {
            Log.e("moshe", "disconnected from wrist band");
        }
    }

    @Override
    public void didReceiveAcceleration(int x, int y, int z, double timestamp) {
        Log.e("moshe",   timestamp + " acc " + x + " " + y + " " + z);
    }

    @Override
    public void didReceiveBVP(float bvp, double timestamp) {
        Log.e("moshe", timestamp + " bvp " + bvp);
    }

    @Override
    public void didReceiveBatteryLevel(float battery, double timestamp) {
        Log.e("moshe", timestamp + " battery " + String.format("%.0f %%", battery * 100));
    }

    @Override
    public void didReceiveGSR(float gsr, double timestamp) {
        Log.e("moshe", timestamp + " gsr" + gsr);
        measure_gsr.add_mes(gsr,timestamp);
        Log.e("david", "get_current(): " + measure_gsr.get_current());
    }

    @Override
    public void didReceiveIBI(float ibi, double timestamp) {
        Log.e("moshe", timestamp + " ibi" + 60 / ibi);
        measure_heartRate.add_mes((60 / ibi),timestamp);
        Log.e("david", "get_current(): " + measure_gsr.get_current());
    }

    @Override
    public void didReceiveTemperature(float temp, double timestamp) {
        Log.e("moshe", timestamp + " temp " + temp);
    }

    @Override
    public void didReceiveTag(double timestamp) {
    }

    @Override
    public void didEstablishConnection() {

        // show();
    }

    @Override
    public void didUpdateOnWristStatus(@EmpaSensorStatus final int status) {

//        runOnUiThread(new Runnable() {
//
//            @Override
//            public void run() {
//
//                if (status == EmpaSensorStatus.ON_WRIST) {
//
//                    //           ((TextView) findViewById(R.id.wrist_status_label)).setText("ON WRIST");
//                }
//                else {
//
//                    //((TextView) findViewById(R.id.wrist_status_label)).setText("NOT ON WRIST");
//                }
//            }
//        });
    }

    class empaTimer extends TimerTask {

        @Override
        public void run() {
            long timestamp = System.currentTimeMillis();

            Log.e("david_TIMER","TIMER_TICK");
            Log.e("david_TIMER","heartrate:" + measure_heartRate.get_current());
            Log.e("david_TIMER","heartrateAVG:" + measure_heartRate.get_last_samples_avg());
            Log.e("david_TIMER","gsr:" + measure_gsr.get_current());
            Log.e("david_TIMER","gsrAVG:" + measure_gsr.get_last_samples_avg());
            Log.e("david_TIMER","timestamp:" + String.valueOf(timestamp));

            MeasurementData data = new MeasurementData(measure_gsr.get_current(),
                    measure_gsr.get_last_samples_avg(),
                    measure_heartRate.get_current(),
                    measure_heartRate.get_last_samples_avg(),
                    String.valueOf(timestamp));

            fbManager.addHRMeasurementToken(data);
        }
    };
}

