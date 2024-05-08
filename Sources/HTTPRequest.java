import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HTTPRequest {
    private String method;
    private String path;
    private int contentLength;
    private boolean useChunked;
    private Map<String, String> headers;
    private Map<String, String> parameters;

    public HTTPRequest(String requestLine, BufferedReader in) throws IOException {
        headers = new HashMap<>();
        parameters = new HashMap<>();
        parseRequestLine(requestLine);
        parseHeaders(in);
    }

    private void parseRequestLine(String requestLine) throws UnsupportedEncodingException {
        String[] requestParts = requestLine.split(" ");

        if (requestParts.length == 3) {
            method = requestParts[0];
            String fullPath = cleanPath(URLDecoder.decode(requestParts[1], StandardCharsets.UTF_8.name()));

            int queryIndex = fullPath.indexOf('?');
            
            if (queryIndex != -1) {
                path = fullPath.substring(0, queryIndex);
                String queryString = fullPath.substring(queryIndex + 1);
                parseParameters(queryString);
            } else {
                path = fullPath;
            }
        }
    }

    private String cleanPath(String path) {
        // Remove any occurrences of "/../" in the path
        return path.replaceAll("/\\.\\.", "/");
    }

    private void parseHeaders(BufferedReader in) throws IOException {
        String line;
        
        while ((line = in.readLine().toLowerCase()) != null && !line.isEmpty()) {
            //Save all headers to the headers map
            String[] keyValue = line.split(":", 2);
            String key = keyValue[0].trim();
            String value = keyValue[1].trim();

            if (key.equals("content-length")) {
                contentLength = Integer.parseInt(value);
            } else if (key.equals("chunked") && value.equals("yes")) {
                useChunked = true;
            } 
            
            headers.put(key, value);
        }

        // POST request's content body parameters
        if ("POST".equals(method) && contentLength > 0) {
            StringBuilder requestBody = new StringBuilder();
            char[] buffer = new char[contentLength];
            in.read(buffer, 0, contentLength);
            requestBody.append(buffer);
            parseParameters(requestBody.toString());
        }
    }

    private void parseParameters(String requestBody) throws UnsupportedEncodingException {
        String[] paramPairs = requestBody.split("&");
    
        for (String pair : paramPairs) {
            String[] keyValue = pair.split("=");

            if (keyValue.length == 2) {
                String decodedKey = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8.name());
                String decodedValue = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8.name());
                parameters.put(decodedKey, decodedValue);
                
            }
        }
    }

    public String headersAsString() {
        //Return the headers of the request as a string.
        StringBuilder sb = new StringBuilder();

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key +": "+ value + "\r\n");
        }

        return sb.toString();
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public int getContentLength() {
        return contentLength;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public boolean isUseChunked() {
        return useChunked;
    }
}
