package black.door.node.java.benchmark;


import org.eclipse.jetty.server.Server;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

/**
 * Created by cjbur on 11/15/2015.
 */
public class JavaRSBenchmark {
    public static int port = 8080;

    public static void main(String[] args) throws Exception {
        URI baseUri = UriBuilder.fromUri("http://localhost/").port(port).build();
        ResourceConfig config = new ResourceConfig(new BenchTestJerseyResourceConfig());
        Server server = JettyHttpContainerFactory.createServer(baseUri, config);
        server.setAttribute("minThreads", 10);
        server.setAttribute("maxThreads", 200);
        server.start();
    }

    public static class BenchTestJerseyResourceConfig extends ResourceConfig {
        public BenchTestJerseyResourceConfig() {
            packages("black.door.node.java");
            register(BenchTestJersey.class);
        }
    }
}
