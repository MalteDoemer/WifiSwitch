package com.example.wifiswitch;

import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpResponseParser {

    public String getHttpVersion() {
        return httpVersion;
    }

    public String getHttpCode() {
        return httpCode;
    }

    public Header[] getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public String getDescription() {
        return description;
    }

    static class Header {
        @NonNull
        @Override
        public String toString() {
            return "Header{" +
                    "key='" + key + '\'' +
                    ", value='" + value + '\'' +
                    '}';
        }

        public Header(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String key;
        public String value;
    }

    static class MalformedHttpResponseException extends IOException {
        public MalformedHttpResponseException() {
        }

        public MalformedHttpResponseException(String message) {
            super(message);
        }

        public MalformedHttpResponseException(String message, Throwable cause) {
            super(message, cause);
        }

        public MalformedHttpResponseException(Throwable cause) {
            super(cause);
        }
    }


    private String httpVersion;
    private String httpCode;
    private String description;
    private Header[] headers;
    private String body;


    public HttpResponseParser(String response) throws MalformedHttpResponseException {

        if (response == null)
            throw new MalformedHttpResponseException("response was null");

        String[] lines = response.split("\\r\\n");

        if (lines.length == 0)
            throw new MalformedHttpResponseException("empty response");

        String status_line = lines[0];

        Pattern status_line_pattern = Pattern.compile("\\s*(HTTP/\\d+.\\d+)\\s+(\\d{3})\\s+(.*)");
        Matcher status_line_matcher = status_line_pattern.matcher(status_line);

        if (!status_line_matcher.find()) {
            throw new MalformedHttpResponseException("status line missing or incorrect");
        }

        httpVersion = status_line_matcher.group(1);
        httpCode = status_line_matcher.group(2);
        description = status_line_matcher.group(3);

        Pattern header_pattern = Pattern.compile("\\s*(.+):\\s*(.*)");
        ArrayList<Header> header_list = new ArrayList<>();

        int i = 1;
        for (; i < lines.length; i++) {

            Matcher header_matcher = header_pattern.matcher(lines[i]);
            if (!header_matcher.find())
                break;
            String key = header_matcher.group(1);
            String value = header_matcher.group(2);
            header_list.add(new Header(key, value));
        }

        headers = new Header[header_list.size()];
        headers = header_list.toArray(headers);

        StringBuilder builder = new StringBuilder();

        for (; i < lines.length; i++) {
            builder.append(lines[i]);
            builder.append("\n");
        }

        body = builder.toString();

    }
}
