package app;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class ProducerService {

    public static void main(String[] args) throws Exception {
        var server = new Server(8080);
        var context = new ServletContextHandler();
        context.setContextPath("/");
        context.addServlet(new ServletHolder(new Topic1Servlet()), "/new-topic1");
        context.addServlet(new ServletHolder(new Topic2Servlet()), "/new-topic2");
        server.setHandler(context);
        server.start();
        server.join();
    }
}
