package com.point.console;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;

/**
 * console app
 *
 */
public class App 
{
    public static void main( String[] args ) {
        int max = 1000;
        int min = 0;
        LocalDate startDate = LocalDate.of(2018, 1, 1); //start date
        long start = startDate.toEpochDay();
        LocalDate endDate = LocalDate.of(2018, 12, 31); //end date
        long end = endDate.toEpochDay();
        try {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("Stopping the process");
            }));
            int i = 0;
            while(true) {
                CloseableHttpClient httpClient = HttpClientBuilder.create().build();
                HttpPost post = new HttpPost("http://localhost:8080/api/v1/points");
                JSONObject json = new JSONObject();
                long randomEpochDay = ThreadLocalRandom.current().nextLong(start,end);
                json.put("measurementDay", LocalDate.ofEpochDay(randomEpochDay).toString());
                json.put("location", LocationEnum.getRandom());
                json.put("value", (Math.random()*((max-min)+1)) + min);
                StringEntity input = new StringEntity( json.toString());

                post.setEntity(input);
                post.setHeader("Content-type", "application/json");
                HttpResponse response = httpClient.execute(post);
                i++;
                System.out.println("point created successfully" );
                /*BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                String line = "";
                while ((line = rd.readLine()) != null) {
                    System.out.println(line);
                }*/
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
