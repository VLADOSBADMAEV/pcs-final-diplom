package vladislav;

import com.google.gson.Gson;
import org.json.simple.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {

    public static void main(String[] args) {
        String host = "localhost";
        int port = 8989;

        try (Socket clientSocket = new Socket(host, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            out.println("развитие");
            String response = in.readLine();
            Gson gson = new Gson();
            JSONArray jsonArray = gson.fromJson(response, JSONArray.class);
            for (Object jsonObject : jsonArray) {
                System.out.println(jsonObject);
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}