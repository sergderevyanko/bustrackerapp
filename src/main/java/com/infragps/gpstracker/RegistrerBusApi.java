package com.infragps.gpstracker;

import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.infragps.gpstracker.client.ApiException;
import com.infragps.gpstracker.client.ApiInvoker;
import com.infragps.gpstracker.client.Pair;
import com.infragps.gpstracker.client.model.Tracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * Created by sergey.derevyanko on 08.12.16.
 */
public class RegistrerBusApi {
    String basePath = "http://bustracking.com.ua";
    ApiInvoker apiInvoker = ApiInvoker.getInstance();

    public void addHeader(String key, String value) {
        getInvoker().addDefaultHeader(key, value);
    }

    public ApiInvoker getInvoker() {
        return apiInvoker;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public String getBasePath() {
        return basePath;
    }

    public Tracker registerBusPost(Tracker body) throws TimeoutException, ExecutionException, InterruptedException, ApiException {
        Object postBody = body;
        String path = "/bus/register";

        List<Pair> queryParams = new ArrayList<Pair>();
        Map<String, String> headerParams = new HashMap<String, String>();
        Map<String, String> formParams = new HashMap<String, String>();

        String[] contentTypes = {};
        String contentType = contentTypes.length > 0 ? contentTypes[0] : "application/json";

        String[] authNames = new String[] {  };

        try {
            String localVarResponse = apiInvoker.invokeAPI (basePath, path, "POST", queryParams, postBody, headerParams, formParams, contentType, authNames);
            if(localVarResponse != null){
                return (Tracker) ApiInvoker.deserialize(localVarResponse, "object", Tracker.class);
            } else {
                return null;
            }
        } catch (ApiException ex) {
            throw ex;
        } catch (InterruptedException ex) {
            throw ex;
        } catch (ExecutionException ex) {
            if(ex.getCause() instanceof VolleyError) {
                VolleyError volleyError = (VolleyError)ex.getCause();
                if (volleyError.networkResponse != null) {
                    throw new ApiException(volleyError.networkResponse.statusCode, volleyError.getMessage());
                }
            }
            throw ex;
        } catch (TimeoutException ex) {
            throw ex;
        }
    }

    public Tracker registerBusPostAsync(Tracker body) throws TimeoutException, ExecutionException, InterruptedException, ApiException {
        Object postBody = body;
        String path = "/bus/register";

        List<Pair> queryParams = new ArrayList<Pair>();
        Map<String, String> headerParams = new HashMap<String, String>();
        Map<String, String> formParams = new HashMap<String, String>();

        String[] contentTypes = {};
        String contentType = contentTypes.length > 0 ? contentTypes[0] : "application/json";

        String[] authNames = new String[] {  };

        try {
            apiInvoker.invokeAPI (basePath, path, "POST", queryParams, postBody, headerParams, formParams, contentType, authNames,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.i("RESPONSE", "GOT RESPONSE "+response);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.i("RESPONSE", "GOT RESPONSE "+error.toString());
                        }
                    });
        } catch (ApiException ex) {
            throw ex;
        }
        return new Tracker();
    }


//    public Tracker registerBusAsync(Tracker body){
//        httpAsyncTask.execute(body);
//        try {
//            Tracker tracker = (Tracker) httpAsyncTask.get();
//            Log.i(RegistrerBusApi.class.getName(), "Tracker id is "+tracker.getId());
//            return tracker;
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }
//        return new Tracker();
//    }


}
