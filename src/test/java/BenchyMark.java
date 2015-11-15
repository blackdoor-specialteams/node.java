import java.net.*;
import java.io.*;

/**
 * Created by cjbur on 11/15/2015.
 */
public class BenchyMark {

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        getNFibs(39);
        long end = System.currentTimeMillis();
        System.out.println("Time = " + (end - start) + " ms");
    }

    public static void getNFibs(int n) throws IOException {
        URL local = new URL("http://localhost:8080/fib/" + n);
        URLConnection yc = local.openConnection();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        yc.getInputStream()));
        String inputLine;

        while ((inputLine = in.readLine()) != null)
            System.out.println(inputLine);
        in.close();
    }
}
