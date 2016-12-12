package com.infragps.gpstracker;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.infragps.gpstracker.client.ApiException;
import com.infragps.gpstracker.client.ApiInvoker;
import com.infragps.gpstracker.client.Pair;
import com.infragps.gpstracker.client.model.Geopoint;

import org.apache.http.HttpEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * Created by sergey.derevyanko on 25.11.16.
 */
public class GeopointApi {
    String basePath = "http://bustracking.com.ua/";
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

    /**
     * Geopoints of buses
     * The Products endpoint returns list of buses on the specified route. The response includes the route number, bus number, and coordinates.
     * @param routeNumber Route number of queried buses.
     * @return List<Bus>
     */
    public List<Geopoint> busesOnRouteGet(String routeNumber) throws TimeoutException, ExecutionException, InterruptedException, ApiException {
        Object postBody = null;

        if (routeNumber == null) {
            VolleyError error = new VolleyError("Missing the required parameter 'routeNumber' when calling busesOnRouteGet",
                    new ApiException(400, "Missing the required parameter 'routeNumber' when calling busesOnRouteGet"));
        }


        // create path and map variables
        String path = "/buses/{routeNumber}".replaceAll("\\{format\\}","json").replaceAll("\\{" + "routeNumber" + "\\}", apiInvoker.escapeString(routeNumber.toString()));

        // query params
        List<Pair> queryParams = new ArrayList<Pair>();
        // header params
        Map<String, String> headerParams = new HashMap<String, String>();
        // form params
        Map<String, String> formParams = new HashMap<String, String>();



        String[] contentTypes = {

        };
        String contentType = contentTypes.length > 0 ? contentTypes[0] : "application/json";

        if (contentType.startsWith("multipart/form-data")) {
            // file uploading
            MultipartEntityBuilder localVarBuilder = MultipartEntityBuilder.create();


            HttpEntity httpEntity = localVarBuilder.build();
            postBody = httpEntity;
        } else {
            // normal form params
        }

        String[] authNames = new String[] {  };

        try {
            String localVarResponse = apiInvoker.invokeAPI (basePath, path, "GET", queryParams, postBody, headerParams, formParams, contentType, authNames);
            if(localVarResponse != null){
                return (List<Geopoint>) ApiInvoker.deserialize(localVarResponse, "array", Geopoint.class);
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

    /**
     * Geopoints of buses
     * The Products endpoint returns list of buses on the specified route. The response includes the route number, bus number, and coordinates.
     * @param routeNumber Route number of queried buses.
     */
    public void busesOnRouteGet(String routeNumber, final Response.Listener<List<Geopoint>> responseListener, final Response.ErrorListener errorListener) {
        Object postBody = null;


        // verify the required parameter 'routeNumber' is set
        if (routeNumber == null) {
            VolleyError error = new VolleyError("Missing the required parameter 'routeNumber' when calling busesOnRouteGet",
                    new ApiException(400, "Missing the required parameter 'routeNumber' when calling busesOnRouteGet"));
        }


        // create path and map variables
        String path = "/buses/{routeNumber}".replaceAll("\\{format\\}","json").replaceAll("\\{" + "routeNumber" + "\\}", apiInvoker.escapeString(routeNumber.toString()));

        // query params
        List<Pair> queryParams = new ArrayList<Pair>();
        // header params
        Map<String, String> headerParams = new HashMap<String, String>();
        // form params
        Map<String, String> formParams = new HashMap<String, String>();



        String[] contentTypes = {

        };
        String contentType = contentTypes.length > 0 ? contentTypes[0] : "application/json";

        if (contentType.startsWith("multipart/form-data")) {
            // file uploading
            MultipartEntityBuilder localVarBuilder = MultipartEntityBuilder.create();


            HttpEntity httpEntity = localVarBuilder.build();
            postBody = httpEntity;
        } else {
            // normal form params
        }

        String[] authNames = new String[] {  };

        try {
            apiInvoker.invokeAPI(basePath, path, "GET", queryParams, postBody, headerParams, formParams, contentType, authNames,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String localVarResponse) {
                            try {
                                responseListener.onResponse((List<Geopoint>) ApiInvoker.deserialize(localVarResponse,  "array", Geopoint.class));
                            } catch (ApiException exception) {
                                errorListener.onErrorResponse(new VolleyError(exception));
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            errorListener.onErrorResponse(error);
                        }
                    });
        } catch (ApiException ex) {
            errorListener.onErrorResponse(new VolleyError(ex));
        }
    }

}
