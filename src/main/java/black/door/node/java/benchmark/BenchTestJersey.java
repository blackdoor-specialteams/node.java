package black.door.node.java.benchmark;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

/**
 * Created by cjbur on 11/15/2015.
 */
@Path("/")
public class BenchTestJersey {

    @GET
    @Path("/fib/{param}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response fib(@PathParam("param") int n) {
        if (n > 0) {
            try {
                List<Long> fibs = new LinkedList<>();
                IntStream.range(0, n).forEach(i -> fibs.add(fibN(i)));
                File f = new File("outs/" + UUID.randomUUID().toString());
                f.createNewFile();
                StringBuilder sb = new StringBuilder();
                try (PrintStream os = new PrintStream(
                        new BufferedOutputStream(
                                new FileOutputStream(f)))) {
                    fibs.forEach(l -> {
                        os.println(l);
                        sb.append(l).append('\n');
                    });
                }
                return Response.status(200).entity(sb.toString()).build();
            } catch (IOException e) {
                return Response.status(500).entity("oh noes!\n" + e.toString()).build();
            }
        } else
            return Response.status(404).entity("no. just no.").build();
    }

    public long fibN(int n) {
        if (n <= 1)
            return n;
        return fibN(n - 2) + fibN(n - 1);
    }
}