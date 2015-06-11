package com.meishi.rest;

import android.os.AsyncTask;
import android.util.Log;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * Created by Aaron on 2015/6/11.
 */
public class HttpPostJsonXmlActivity extends AbstractAsyncActivity {

    protected static final String TAG = HttpPostJsonXmlActivity.class.getSimpleName();


    // ***************************************
    // Private classes
    // ***************************************
    private class PostMessageTask extends AsyncTask<MediaType, Void, String> {

        private Message message;

        @Override
        protected void onPreExecute() {
            showLoadingProgressDialog();

        }

        @Override
        protected String doInBackground(MediaType... params) {
            try {
                if (params.length <= 0) {
                    return null;
                }

                MediaType mediaType = params[0];

                // The URL for making the POST request
                final String url = "";

                HttpHeaders requestHeaders = new HttpHeaders();

                // Sending a JSON or XML object i.e. "application/json" or "application/xml"
                requestHeaders.setContentType(mediaType);

                // Populate the Message object to serialize and headers in an
                // HttpEntity object to use for the request
                HttpEntity<Message> requestEntity = new HttpEntity<Message>(message, requestHeaders);

                // Create a new RestTemplate instance
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());


                // Make the network request, posting the message, expecting a String in response from the server
                ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity,
                        String.class);

                // Return the response body to display to the user
                return response.getBody();
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            dismissProgressDialog();

            //TODO deal with result
        }

    }

}
