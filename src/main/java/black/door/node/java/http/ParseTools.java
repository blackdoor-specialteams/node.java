package black.door.node.java.http;

import black.door.node.java.exception.HttpParsingException;
import black.door.struct.ByteQueue;
import black.door.util.DBP;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.ClosedChannelException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by nfischer on 6/10/15.
 */
public abstract class ParseTools {

    public static Map<String, String> parseHeaders(InputStream is) throws IOException {
        Map<String, String> headers = new HashMap<>();

        for(String next = nextLine(is); !next.isEmpty(); next = nextLine(is)){
            String[] header = next.split("\\s*:\\s*", 2);
            headers.put(header[0], header[1]);
        }
        return headers;
    }

    public static byte[] getBody(InputStream is, Map<String, String> headers, int maxBodySize) throws IOException, HttpParsingException {
        maxBodySize = maxBodySize == -1 ? Integer.MAX_VALUE : maxBodySize;
        byte[] body;
        String transferEncoding = headers.get("Transfer-Encoding");

        if(transferEncoding != null && !Objects.equals(transferEncoding, "identity")){
            ByteQueue bq = new ByteQueue();
            for(String next = nextLine(is); !next.isEmpty(); next = nextLine(is)) {
                int size = Integer.valueOf(next, 16);
                if(size == 0)
                    break;
                if(bq.filled() + size > maxBodySize)
                    throw new HttpParsingException("Request body size exceeds max allowed body size ("+ maxBodySize +").");

                byte[] chunk = new byte[size];
                read(is, chunk);
                bq.enQueue(chunk);
                nextLine(is);
            }
            body = bq.deQueue(bq.filled());

        }else{
            String contentLengthString = headers.get("Content-Length");
            if(contentLengthString != null){
                int contentLength = Integer.valueOf(contentLengthString);
                if(contentLength > maxBodySize)
                    throw new HttpParsingException("Request body size exceeds max allowed body size ("+ maxBodySize +").");

                body = new byte[contentLength];
                read(is, body);
            }else{
                body = null;
            }
        }
        return body;
    }

    private static byte[] read(InputStream is, byte[] bytes) throws IOException, HttpParsingException {
        int bytesRead = 0;
        for (int r = 0; bytesRead < bytes.length; r = is.read(bytes, bytesRead, bytes.length - bytesRead)){
            if(r == -1)
                throw new HttpParsingException("Expected to be able to read more bytes from socket.");
            bytesRead += r;
        }
        return bytes;
    }

    private static final char CR = 13;
    private static final char LF = 10;

    /**
     *
     * @param stream
     * @return the next line from the stream
     * @throws IOException
     */
    public static String nextLine(InputStream stream) throws IOException {
        StringBuilder sb = new StringBuilder();
        boolean cr = false;
        for(int read = stream.read();; read = stream.read()){
            if(read == -1) {
                if(sb.length() == 0)
                    throw new ClosedChannelException();
                throw new EOFException("EOF reached before line ended.");
            }

            char current = (char) read;

            if(current == CR){
                cr = true;
                continue;
            }
            if(cr){
                if(current == LF)
                    break;
                sb.append(CR);
                sb.append(current);
                cr = false;
                continue;
            }else{
                if(current == LF)
                    break;
                sb.append(current);
            }
        }
        return sb.toString();
    }

    private static Pattern theRegex = Pattern.compile("((?<field>[\\w\\-\\.~%!$'\\(\\)*+,/?:@]+)=?(?<value>[\\w\\-\\.~%!$'\\(\\)*+,/?:@]*))");

    public static Map<String, String> parseQueries(URL url){
        try {
            return parseQueries(url.getQuery());
        } catch (MalformedURLException e) {
            black.door.dbp.DBP.error().log(e.toString() + (e.getMessage() == null
                            ? ""
                            : ": " + e.getMessage()),
                    e.getStackTrace());
            throw new RuntimeException(e.getMessage());
        }
    }

    public static Map<String, String> parseQueries(String query) throws MalformedURLException {
        Map<String, String> queries = new HashMap<>();
        if (query == null)
            return queries;

        Pattern pattern = theRegex;
        Matcher matcher = pattern.matcher(query);

        try {
            while (matcher.find()) {
                String field = matcher.group("field");
                String value = matcher.group("value");
                queries.put(field, value);
            }
        }catch (IllegalArgumentException e){
            DBP.printException(e);
            throw new MalformedURLException();
        }
        return queries;
    }

}
