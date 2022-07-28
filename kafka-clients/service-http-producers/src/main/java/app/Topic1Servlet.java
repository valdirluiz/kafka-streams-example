package app;

import ecommerce.producers.KafkaDispatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class Topic1Servlet extends HttpServlet {

    private final KafkaDispatcher<String> dispatcher = new KafkaDispatcher<>();


    @Override
    public void destroy() {
        super.destroy();
        dispatcher.close();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {

            // we are not caring about any security issues, we are only
            // showing how to use http as a starting point
            var id = req.getParameter("id");
            var text = req.getParameter("text");
 
            dispatcher.send("streams.topic1", id, text);

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().println("Ok");

        } catch (ExecutionException e) {
            throw new ServletException(e);
        } catch (InterruptedException e) {
            throw new ServletException(e);
        }
    }
}
