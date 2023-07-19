package vladislav;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.File;


public class Main {
    public static void main(String[] args) throws Exception {
        BooleanSearchEngine engine = new BooleanSearchEngine(new File("pdfs"));
        System.out.println(engine.search("бизнес"));

        while (true) {
            try (ServerSocket serverSocket = new ServerSocket(8989);
                 Socket clientSocket = serverSocket.accept();
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
                final String word = in.readLine();

                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();

                out.println(gson.toJson(engine.search(word)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}