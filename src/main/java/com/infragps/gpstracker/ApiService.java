package com.infragps.gpstracker;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.infragps.gpstracker.client.ApiException;
import com.infragps.gpstracker.client.model.Tracker;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * Created by sergey.derevyanko on 05.01.17.
 */
public class ApiService extends Service {

    private final RegistrerBusApi registrerBusApi = new RegistrerBusApi();;
    private final IBinder mBinder = new LocalBinder();


    public class LocalBinder extends Binder {
        ApiService getService() {
            Log.i(ApiService.class.getName(), "getService() is called with service "+this.toString());
            return ApiService.this;
        }
    }

    public Tracker registerBusPost(Tracker body){
        try {
            return registrerBusApi.registerBusPost(body);
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ApiException e) {
            e.printStackTrace();
        }
        return new Tracker();
    }

    public Tracker registerBusPostAsync(Tracker body){
        try {
            return registrerBusApi.registerBusPost(body);
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ApiException e) {
            e.printStackTrace();
        }
        return new Tracker();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(ApiService.class.getName(), "ApiService has been binded 2");
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

}
