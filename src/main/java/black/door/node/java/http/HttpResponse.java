package black.door.node.java.http;

import black.door.net.http.tools.ParseTools;
import black.door.node.java.Conf;
import black.door.node.java.loops.ResponseLoop;
import static com.google.common.net.HttpHeaders.*;
import org.apache.commons.httpclient.HttpStatus;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;


import static black.door.net.http.tools.ParseTools.nextLine;
import static black.door.net.http.tools.ParseTools.parseHeaders;

/**
 * Created by nfischer on 6/10/15.
 */
public class HttpResponse {
    private Map<String, String> headers;
    private int statusCode;
    private String statusMessage;
    private String version;
    private byte[] body;
    HttpContext context;

    public HttpResponse(String version, int statusCode, String statusMessage){
        this.version = version;
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.headers = new HashMap<>();
        body = new byte[0];
    }

    public HttpResponse(byte[] response) throws IOException {
        this(new ByteArrayInputStream(response));
    }

    public HttpResponse(InputStream stream) throws IOException {
        String firstLine = nextLine(stream);

        String[] split = firstLine.split("\\s+");
        version = split[0];
        statusCode = Integer.valueOf(split[1]);
        statusMessage = split[2];

        this.headers = parseHeaders(stream);

        this.body = ParseTools.getBody(stream, headers, -1);
    }

    public Future send(){
        return ResponseLoop.sendResponse(this, context.getSock());
    }

    HttpContext getContext(){
        return context;
    }

    void setContext(HttpContext context){
        this.context = context;
    }

    public HttpResponse putHeader(String key, String value){
        headers.put(key, value);
        return this;
    }

    public String getHeader(String headerName) {
        return headers.get(headerName);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Sets the status code and message for this response, unless the status
     * code is not recognized, then only the code is set, then the message is unchanged
     * @param statusCode
     * @return this
     */
    public HttpResponse setStatusCode(int statusCode) {
        this.statusCode = statusCode;
        String message = HttpStatus.getStatusText(statusCode);
        if(message != null)
            this.statusMessage = message;
        return this;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
        putHeader(CONTENT_LENGTH, String.valueOf(body.length));
    }

    public void setBody(String body){
        putHeader(CONTENT_TYPE, "text/plain; charset=UTF-8");
        setBody(body.getBytes(StandardCharsets.UTF_8));
    }

    private String getLine1(){
        StringBuilder sb = new StringBuilder();
        sb.append(version);
        sb.append(" ");
        sb.append(statusCode);
        sb.append(" ");
        sb.append(statusMessage);
        sb.append('\n');
        return sb.toString();
    }

    private String getHeaderString(){
        StringBuilder sb = new StringBuilder();
        for(Map.Entry<String, String> header : headers.entrySet()){
            sb.append(header.getKey());
            sb.append(": ");
            sb.append(header.getValue());
            sb.append('\n');
        }
        sb.append('\n');
        return sb.toString();
    }

    public byte[] serialize(){
        finalize();
        byte[] top = (getLine1() + getHeaderString()).getBytes(
                StandardCharsets.ISO_8859_1);
        if(body != null){
            byte[] serial = Arrays.copyOf(top, top.length + body.length);
            System.arraycopy(body, 0, serial, top.length, body.length);
            return serial;
        }else{
            return top;
        }
    }

    public void finalize(){
        if(!headers.containsKey(SERVER))
            putHeader(SERVER, Conf.get().getString("nodejava.serverName") +" ("
                + System.getProperty("os.name") +')');


        if(body == null){
            putHeader(CONTENT_LENGTH, "0");
        }else{
            if(!headers.containsKey(CONTENT_LENGTH))
                putHeader(CONTENT_LENGTH, String.valueOf(body.length));
        }
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(getLine1());

        sb.append(getHeaderString());

        if(body != null)
            sb.append(new String(body, StandardCharsets.ISO_8859_1));

        return sb.toString();
    }
}
