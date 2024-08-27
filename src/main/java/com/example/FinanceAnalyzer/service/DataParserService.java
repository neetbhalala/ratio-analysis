package com.example.FinanceAnalyzer.service;

import okhttp3.*;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class DataParserService {

    private final OkHttpClient client = new OkHttpClient();
//    private final String apiUrl = "https://app.nocodb.com/api/v2/tables/m66k7ovrz10rgv1/records";
    private final String apiToken = "rtllgXG97qx80EhrRcFS3-KJtHdyA3P-CCz02OIV";

    public String duplicateBase(String companyName) throws IOException {
        String baseId = "pns9r6cu7ddye3e"; // The original base ID

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = formatter.format(new Date());

        // Prepare JSON payload to duplicate the base with a new name
        JSONObject payload = new JSONObject();
        payload.put("excludeData", false);
        payload.put("excludeViews", true);
        payload.put("excludeHooks", true);

        // Create request body
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, payload.toString());

        // Build request
        Request request = new Request.Builder()
                .url("https://app.nocodb.com/api/v2/meta/duplicate/" + baseId)
                .method("POST", body)
                .addHeader("xc-token", apiToken)
                .addHeader("Content-Type", "application/json")
                .build();

        // Execute request and parse response
        try (Response response = client.newCall(request).execute()) {
            String responseBodyString = response.body().string();
            System.out.println("Response Code: " + response.code());
            System.out.println("Response Body: " + responseBodyString);

            if (response.isSuccessful()) {
                JSONObject responseBody = new JSONObject(responseBodyString);

                // Check if 'base_id' exists, if not, look for 'id' or print all keys
                if (responseBody.has("base_id")) {
                    String newBaseId = responseBody.getString("base_id");
                    System.out.println("Base successfully duplicated with ID: " + newBaseId);
                    return newBaseId;
                } else if (responseBody.has("id")) {
                    String newBaseId = responseBody.getString("id");
                    System.out.println("Base successfully duplicated with ID: " + newBaseId);
                    return newBaseId;
                } else {
                    System.out.println("Neither 'base_id' nor 'id' found in response. Available keys:");
                    for (String key : responseBody.keySet()) {
                        System.out.println(key);
                    }
                    throw new IOException("Unable to find base ID in response");
                }
            } else {
                throw new IOException("Failed to duplicate base. Response code: " + response.code());
            }
        }
    }

    public void storeData(JSONObject jsonObject, String baseId) throws IOException {
        String balanceSheetApiUrl = "https://app.nocodb.com/api/v2/tables/" + baseId + "/BalanceSheet/records";

        for (String category : jsonObject.keySet()) {
            Object categoryData = jsonObject.get(category);

            if (categoryData instanceof JSONObject) {
                // If the category data is a JSONObject, iterate through its keys
                JSONObject categoryObject = (JSONObject) categoryData;
                for (String key : categoryObject.keySet()) {
                    Object value = categoryObject.get(key);
                    // If value is a nested object, iterate again (if necessary)
                    if (value instanceof JSONObject) {
                        JSONObject nestedObject = (JSONObject) value;
                        for (String nestedKey : nestedObject.keySet()) {
                            Object nestedValue = nestedObject.get(nestedKey);
                            sendToNocoDB(category, nestedKey, nestedValue, balanceSheetApiUrl);
                        }
                    } else {
                        sendToNocoDB(category, key, value, balanceSheetApiUrl);
                    }
                }
            } else {
                // For simple key-value pairs at the top level
                sendToNocoDB(category, "total", categoryData, balanceSheetApiUrl);
            }
        }
    }

    private void sendToNocoDB(String category, String key, Object value, String apiUrl) throws IOException {
        // Prepare JSON payload
        JSONObject payload = new JSONObject();
        payload.put("Category", category);
        payload.put("Key", key);
        payload.put("Value", value);

        // Create request body
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, payload.toString());

        // Build request
        Request request = new Request.Builder()
                .url(apiUrl)
                .post(body)
                .addHeader("xc-token", apiToken)
                .addHeader("Content-Type", "application/json")
                .build();

        // Execute request and handle response
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                System.out.println("Data successfully stored in NocoDB: " + response.body().string());
            } else {
                System.err.println("Failed to store data. Response code: " + response.code());
                System.err.println("Response body: " + response.body().string());
            }
        }
    }




//    private void storeData(JSONObject jsonObject) throws IOException {
//        OkHttpClient client = new OkHttpClient();
//        MediaType mediaType = MediaType.parse("application/json");
//        RequestBody body = RequestBody.create(mediaType, String.valueOf(jsonObject));
//        Request request = new Request.Builder()
//                .url("https://app.nocodb.com/api/v2/tables/m66k7ovrz10rgv1/records")
//                .method("POST", body)
//                .addHeader("xc-token", "7UKKVAcXGDGTjgXE_PCLPRo32juVtpw_itA6BJ6E")
//                .addHeader("Content-Type", "application/json")
//                .build();
//        Response response = client.newCall(request).execute();
//    }

}
