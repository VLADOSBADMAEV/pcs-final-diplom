import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import vladislav.PageEntry;
import com.google.gson.reflect.TypeToken;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.lang.reflect.Type;


public class Main {
    final static String PATH = "pdfs";
    public static void main(String[] args) {
        int port = 8989;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                try (Socket connection = serverSocket.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                     PrintWriter out = new PrintWriter(connection.getOutputStream(), true)) {

                    String word = in.readLine();
                    BooleanSearchEngine engine = new BooleanSearchEngine(new File(PATH));
                    List<PageEntry> pageEntryList = engine.search(word);

                    Type type = new TypeToken<List<PageEntry>>() {}.getType();
                    Gson gson = new GsonBuilder().create();
                    out.println(gson.toJson(pageEntryList, type));
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}