package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MessageServer {
    private static final int PORT = 6789;
    private static final ExecutorService threadPool = Executors.newFixedThreadPool(10);

    // Храним активные подключения
    private static final Map<String, ClientConnection> activeClients = new ConcurrentHashMap<>();
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is running on port " + PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientConnection clientconnection = new ClientConnection(clientSocket);
                threadPool.execute(clientconnection);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

     //Метод для добавления клиента в список активных
    public static void addClient(String username, ClientConnection handler) {
        activeClients.put(username, handler);
    }

    // Метод для удаления клиента при отключении
    public static void removeClient(String username) {
        activeClients.remove(username);
    }

    // Метод для получения ClientHandler по имени пользователя
    public static ClientConnection getClientHandler(String username) {
        return activeClients.get(username);
    }
}