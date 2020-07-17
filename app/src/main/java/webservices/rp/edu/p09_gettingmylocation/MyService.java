package webservices.rp.edu.p09_gettingmylocation;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class MyService extends Service {
    public MyService() {
    }

    boolean started;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public void onCreate() {
        Log.d("Service", "Service created");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (started == false){
            started = true;
            Log.d("Service", "Service started");
            Toast.makeText(getApplicationContext(),"Service started",Toast.LENGTH_SHORT).show();

        } else {
            Log.d("Service", "Service is still running");
            Toast.makeText(getApplicationContext(),"Service is still running",Toast.LENGTH_SHORT).show();

        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d("Service", "Service exited");
        super.onDestroy();
        Toast.makeText(getApplicationContext(),"Service is exited",Toast.LENGTH_SHORT).show();

    }

}

