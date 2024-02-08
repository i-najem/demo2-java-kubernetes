import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import java.net.InetAddress;
import java.net.UnknownHostException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class Main {
    public static long startTime = System.currentTimeMillis();
    public static long failedTime = getRandomNumberUsingInts(Integer.parseInt(System.getenv("MIN_SEC")),Integer.parseInt(System.getenv("MAX_SEC")));
    
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/", new HomeHandler());
        server.createContext("/health", new HealthHandler());
        server.start();
    }

    public static long getRandomNumberUsingInts(long min, long max) {
        Random random = new Random();
        return random.longs(min, max+1)
        .findFirst()
        .getAsLong();
    }
}



class HomeHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        String query = exchange.getRequestURI().getQuery();

        long finish = System.currentTimeMillis();
        long timeElapsed = finish - Main.startTime;
        String time = String.format("%02d min, %02d sec", 
            TimeUnit.MILLISECONDS.toMinutes(timeElapsed),
            TimeUnit.MILLISECONDS.toSeconds(timeElapsed) - 
            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeElapsed))
        );
        long secondes = TimeUnit.MILLISECONDS.toSeconds(timeElapsed);

        try {  
            String hostname = InetAddress.getLocalHost().getHostName();
            String response = "This server "+hostname.toString()+" is running since "+time+" -> health="+((secondes < Main.failedTime) ? "OK": "ERROR")+"\n";
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        } catch (UnknownHostException e) {
        }  
    }

}

class HealthHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();

        String query = exchange.getRequestURI().getQuery();
        
        long finish = System.currentTimeMillis();
        long timeElapsed = finish - Main.startTime;
        long secondes = TimeUnit.MILLISECONDS.toSeconds(timeElapsed);
        String response = "";
        if(secondes<Main.failedTime){
            response = "OK";
            exchange.sendResponseHeaders(200, response.length());
        } else {
            response = "ERROR";
            exchange.sendResponseHeaders(500, response.length());
        }

        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
