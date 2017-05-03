package com.infragps.gpstracker.client;

import android.os.AsyncTask;

import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ResponseDelivery;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.NoCache;
import com.android.volley.toolbox.RequestFuture;
import com.google.gson.JsonParseException;
import com.infragps.gpstracker.client.model.Tracker;
import com.infragps.gpstracker.client.request.DeleteRequest;
import com.infragps.gpstracker.client.request.GetRequest;
import com.infragps.gpstracker.client.request.PatchRequest;
import com.infragps.gpstracker.client.request.PostRequest;
import com.infragps.gpstracker.client.request.PutRequest;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by sergey.derevyanko on 25.11.16.
 */
public class ApiInvoker {
    private static ApiInvoker INSTANCE;
    private Map<String, String> defaultHeaderMap = new HashMap<String, String>();

    private RequestQueue mRequestQueue;

//    private Map<String, Authentication> authentications;

    private int connectionTimeout;

    /** Content type "text/plain" with UTF-8 encoding. */
    public static final ContentType TEXT_PLAIN_UTF8 = ContentType.create("text/plain", Consts.UTF_8);

    private Cache cache;

    /**
     * ISO 8601 date time format.
     * @see https://en.wikipedia.org/wiki/ISO_8601
     */
    public static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    /**
     * ISO 8601 date format.
     * @see https://en.wikipedia.org/wiki/ISO_8601
     */
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    static {
        // Use UTC as the default time zone.
        DATE_TIME_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
        DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public static void setUserAgent(String userAgent) {
        INSTANCE.addDefaultHeader("User-Agent", userAgent);
    }

    public static Date parseDateTime(String str) {
        try {
            return DATE_TIME_FORMAT.parse(str);
        } catch (java.text.ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static Date parseDate(String str) {
        try {
            return DATE_FORMAT.parse(str);
        } catch (java.text.ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static String formatDateTime(Date datetime) {
        return DATE_TIME_FORMAT.format(datetime);
    }

    public static String formatDate(Date date) {
        return DATE_FORMAT.format(date);
    }

    public static String parameterToString(Object param) {
        if (param == null) {
            return "";
        } else if (param instanceof Date) {
            return formatDateTime((Date) param);
        } else if (param instanceof Collection) {
            StringBuilder b = new StringBuilder();
            for(Object o : (Collection)param) {
                if(b.length() > 0) {
                    b.append(",");
                }
                b.append(String.valueOf(o));
            }
            return b.toString();
        } else {
            return String.valueOf(param);
        }
    }

    /*
      Format to {@code Pair} objects.
    */
    public static List<Pair> parameterToPairs(String collectionFormat, String name, Object value){
        List<Pair> params = new ArrayList<Pair>();

        // preconditions
        if (name == null || name.isEmpty() || value == null) return params;

        Collection valueCollection = null;
        if (value instanceof Collection) {
            valueCollection = (Collection) value;
        } else {
            params.add(new Pair(name, parameterToString(value)));
            return params;
        }

        if (valueCollection.isEmpty()){
            return params;
        }

        // get the collection format
        collectionFormat = (collectionFormat == null || collectionFormat.isEmpty() ? "csv" : collectionFormat); // default: csv

        // create the params based on the collection format
        if (collectionFormat.equals("multi")) {
            for (Object item : valueCollection) {
                params.add(new Pair(name, parameterToString(item)));
            }

            return params;
        }

        String delimiter = ",";

        if (collectionFormat.equals("csv")) {
            delimiter = ",";
        } else if (collectionFormat.equals("ssv")) {
            delimiter = " ";
        } else if (collectionFormat.equals("tsv")) {
            delimiter = "\t";
        } else if (collectionFormat.equals("pipes")) {
            delimiter = "|";
        }

        StringBuilder sb = new StringBuilder() ;
        for (Object item : valueCollection) {
            sb.append(delimiter);
            sb.append(parameterToString(item));
        }

        params.add(new Pair(name, sb.substring(1)));

        return params;
    }


    public static void initializeInstance() {
        initializeInstance(null, 0, null, 10);
    }

    public static void initializeInstance(Network network, int threadPoolSize, ResponseDelivery delivery, int connectionTimeout) {
        INSTANCE = new ApiInvoker(network, threadPoolSize, delivery, connectionTimeout);
        setUserAgent("Swagger-Codegen/1.0.0/android");
    }

    private ApiInvoker(Network network, int threadPoolSize, ResponseDelivery delivery, int connectionTimeout) {
        cache = new NoCache();
        if(network == null) {
            HttpStack stack = new HurlStack();
            network = new BasicNetwork(stack);
        }

        if(delivery == null) {
            initConnectionRequest(network);
        } else {
            initConnectionRequest(network, threadPoolSize, delivery);
        }
        this.connectionTimeout = connectionTimeout;
    }

    public static ApiInvoker getInstance() {
        if (INSTANCE == null) initializeInstance();
        return INSTANCE;
    }

    public void addDefaultHeader(String key, String value) {
        defaultHeaderMap.put(key, value);
    }

    public String escapeString(String str) {
        try {
            return URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return str;
        }
    }

    public static Object deserialize(String json, String containerType, Class cls) throws ApiException {
        try{
            if("list".equalsIgnoreCase(containerType) || "array".equalsIgnoreCase(containerType)) {
                return JsonUtil.deserializeToList(json, cls);
            }
            else if(String.class.equals(cls)) {
                if(json != null && json.startsWith("\"") && json.endsWith("\"") && json.length() > 1)
                    return json.substring(1, json.length() - 1);
                else
                    return json;
            }
            else {
                return JsonUtil.deserializeToObject(json, cls);
            }
        }
        catch (JsonParseException e) {
            throw new ApiException(500, e.getMessage());
        }
    }

    public static String serialize(Object obj) throws ApiException {
        try {
            if (obj != null)
                return JsonUtil.serialize(obj);
            else
                return null;
        }
        catch (Exception e) {
            throw new ApiException(500, e.getMessage());
        }
    }


    public void setConnectionTimeout(int connectionTimeout){
        this.connectionTimeout = connectionTimeout;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }



    public String invokeAPI(String host, String path, String method, List<Pair> queryParams, Object body, Map<String, String> headerParams, Map<String, String> formParams, String contentType, String[] authNames) throws ApiException, InterruptedException, ExecutionException, TimeoutException {
        final RequestFuture<String> future = RequestFuture.newFuture();
        Request request = createRequest(host, path, method, queryParams, body, headerParams, formParams, contentType, new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                return;
            }
        }, future);

        if(request != null) {
            mRequestQueue.add(request);
//            httpAsyncTask.execute(future);
//            return httpAsyncTask.get(connectionTimeout, TimeUnit.SECONDS);
            return future.get(connectionTimeout, TimeUnit.SECONDS);
        } else return "no data";
    }

    public void invokeAPI(String host, String path, String method, List<Pair> queryParams, Object body, Map<String, String> headerParams, Map<String, String> formParams, String contentType, String[] authNames, Response.Listener<String> stringRequest, Response.ErrorListener errorListener) throws ApiException {
        Request request = createRequest(host, path, method, queryParams, body, headerParams, formParams, contentType, stringRequest, errorListener);
        if (request != null) mRequestQueue.add(request);
    }

    public Request<String> createRequest(String host, String path, String method, List<Pair> queryParams, Object body, Map<String, String> headerParams, Map<String, String> formParams, String contentType, Response.Listener<String> stringRequest, Response.ErrorListener errorListener) throws ApiException {
        StringBuilder b = new StringBuilder();
        b.append("?");

        if (queryParams != null){
            for (Pair queryParam : queryParams){
                if (!queryParam.getName().isEmpty()) {
                    b.append(escapeString(queryParam.getName()));
                    b.append("=");
                    b.append(escapeString(queryParam.getValue()));
                    b.append("&");
                }
            }
        }

        String querystring = b.substring(0, b.length() - 1);
        String url = host + path + querystring;

        HashMap<String, String> headers = new HashMap<String, String>();

        for(String key : headerParams.keySet()) {
            headers.put(key, headerParams.get(key));
        }

        for(String key : defaultHeaderMap.keySet()) {
            if(!headerParams.containsKey(key)) {
                headers.put(key, defaultHeaderMap.get(key));
            }
        }
        headers.put("Accept", "application/json");

        // URL encoded string from form parameters
        String formParamStr = null;

        // for form data
        if ("application/x-www-form-urlencoded".equals(contentType)) {
            StringBuilder formParamBuilder = new StringBuilder();

            // encode the form params
            for (String key : formParams.keySet()) {
                String value = formParams.get(key);
                if (value != null && !"".equals(value.trim())) {
                    if (formParamBuilder.length() > 0) {
                        formParamBuilder.append("&");
                    }
                    try {
                        formParamBuilder.append(URLEncoder.encode(key, "utf8")).append("=").append(URLEncoder.encode(value, "utf8"));
                    }
                    catch (Exception e) {
                        // move on to next
                    }
                }
            }
            formParamStr = formParamBuilder.toString();
        }
        Request request = null;

        if ("GET".equals(method)) {
            request = new GetRequest(url, headers, null, stringRequest, errorListener);
        }
        else if ("POST".equals(method)) {
            request = null;
            if (formParamStr != null) {
                request = new PostRequest(url, headers, contentType, new StringEntity(formParamStr, "UTF-8"), stringRequest, errorListener);
            } else if (body != null) {
                if (body instanceof HttpEntity) {
                    request = new PostRequest(url, headers, null, (HttpEntity) body, stringRequest, errorListener);
                } else {
                    request = new PostRequest(url, headers, contentType, new StringEntity(serialize(body), "UTF-8"), stringRequest, errorListener);
                }
            } else {
                request = new PostRequest(url, headers, null, null, stringRequest, errorListener);
            }
        }
        else if ("PUT".equals(method)) {
            request = null;
            if (formParamStr != null) {
                request = new PutRequest(url, headers, contentType, new StringEntity(formParamStr, "UTF-8"), stringRequest, errorListener);
            } else if (body != null) {
                if (body instanceof HttpEntity) {
                    request = new PutRequest(url, headers, null, (HttpEntity) body, stringRequest, errorListener);
                } else {
                    request = new PutRequest(url, headers, contentType, new StringEntity(serialize(body), "UTF-8"), stringRequest, errorListener);
                }
            } else {
                request = new PutRequest(url, headers, null, null, stringRequest, errorListener);
            }
        }
        else if ("DELETE".equals(method)) {
            request = null;
            if (formParamStr != null) {
                request = new DeleteRequest(url, headers, contentType, new StringEntity(formParamStr, "UTF-8"), stringRequest, errorListener);
            } else if (body != null) {
                if (body instanceof HttpEntity) {
                    request = new DeleteRequest(url, headers, null, (HttpEntity) body, stringRequest, errorListener);
                } else {
                    request = new DeleteRequest(url, headers, contentType, new StringEntity(serialize(body), "UTF-8"), stringRequest, errorListener);
                }
            } else {
                request = new DeleteRequest(url, headers, null, null, stringRequest, errorListener);
            }
        }
        else if ("PATCH".equals(method)) {
            request = null;
            if (formParamStr != null) {
                request = new PatchRequest(url, headers, contentType, new StringEntity(formParamStr, "UTF-8"), stringRequest, errorListener);
            } else if (body != null) {
                if (body instanceof HttpEntity) {
                    request = new PatchRequest(url, headers, null, (HttpEntity) body, stringRequest, errorListener);
                } else {
                    request = new PatchRequest(url, headers, contentType, new StringEntity(serialize(body), "UTF-8"), stringRequest, errorListener);
                }
            } else {
                request = new PatchRequest(url, headers, null, null, stringRequest, errorListener);
            }
        }

        if (request != null) {
            request.setRetryPolicy(new DefaultRetryPolicy((int)TimeUnit.SECONDS.toMillis(this.connectionTimeout), DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        }

        return request;
    }

    private void initConnectionRequest(Network network) {
        mRequestQueue = new RequestQueue(cache, network);
        mRequestQueue.start();
    }

    private void initConnectionRequest(Network network, int threadPoolSize, ResponseDelivery delivery) {
        mRequestQueue = new RequestQueue(cache, network, threadPoolSize, delivery);
        mRequestQueue.start();
    }

    public void stopQueue() {
        mRequestQueue.stop();
    }

    AsyncTask<RequestFuture, Void, String> httpAsyncTask = new AsyncTask<RequestFuture, Void, String>() {
        @Override
        protected String doInBackground(RequestFuture... futures) {
            try {
                return (String) futures[0].get(getConnectionTimeout(), TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return  "";
        }
    };
}

