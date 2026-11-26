
package com.feedback;

import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;

public class Main {

    public static void main(String[] args) throws Exception {

        HttpServer server = HttpServer.create(
                new InetSocketAddress(8085), 0);

        server.createContext("/", exchange -> {

            if (exchange.getRequestMethod().equals("GET")) {

                InputStream input = Main.class.getResourceAsStream("/static/index.html");

                if (input == null) {
                    exchange.sendResponseHeaders(404, -1);
                    return;
                }

                byte[] response = input.readAllBytes();
                input.close();

                exchange.getResponseHeaders().set(
                        "Content-Type", "text/html; charset=UTF-8");

                exchange.sendResponseHeaders(200, response.length);

                OutputStream output = exchange.getResponseBody();
                output.write(response);
                output.close();

            } else if (exchange.getRequestMethod().equals("POST")) {

                String body = new String(
                        exchange.getRequestBody().readAllBytes(),
                        StandardCharsets.UTF_8);

                String name = "Student";

                for (String field : body.split("&")) {

                    String[] pair = field.split("=", 2);

                    if (pair.length == 2 && pair[0].equals("name")) {
                        name = URLDecoder.decode(
                                pair[1], StandardCharsets.UTF_8);
                    }
                }

                String html = "<html><body style='font-family:Arial'>"
                        + "<h1>Feedback Submitted Successfully!</h1>"
                        + "<h2>Thank you, " + escape(name) + "!</h2>"
                        + "<a href='/'>Go Back</a></body></html>";

                byte[] response = html.getBytes(StandardCharsets.UTF_8);

                exchange.getResponseHeaders().set(
                        "Content-Type", "text/html; charset=UTF-8");

                exchange.sendResponseHeaders(200, response.length);

                OutputStream output = exchange.getResponseBody();
                output.write(response);
                output.close();

            } else {
                exchange.sendResponseHeaders(405, -1);
            }
        });

        server.start();

        System.out.println("Feedback Application Started!");
        System.out.println("Open http://localhost:8085");

    }

    private static String escape(String value) {
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
