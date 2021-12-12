package com.example.wifiswitch;


import static com.example.wifiswitch.HttpResponseParser.*;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.Callable;


public class HttpGetTask implements Callable<HttpGetResult> {

    private static final String TAG = HttpGetTask.class.getSimpleName();

    private final URL url;

    public HttpGetTask(URL url) {
        this.url = url;
    }

    @Override
    public HttpGetResult call() throws IOException {

        String host = url.getHost();
        String path = url.getPath();
        String message = "GET " + path + " HTTP/1.1\r\n\r\n";

        String response = sendAndReceive(host, message);

        return parseResponse(response);
    }

    private HttpGetResult parseResponse(String response) throws MalformedHttpResponseException {
        HttpResponseParser parser = new HttpResponseParser(response);

        int code = Integer.parseInt(parser.getHttpCode());

        if (parser.getBody() != null) {
            try {
                JsonElement jsonElement = JsonParser.parseString(parser.getBody());
                if (jsonElement.isJsonObject())
                    return new HttpGetResult(code, jsonElement.getAsJsonObject());
            } catch (JsonSyntaxException ignored) {
            }
        }

        return new HttpGetResult(code, null);
    }


    private native String sendAndReceive(String host, String message) throws IOException;
}
