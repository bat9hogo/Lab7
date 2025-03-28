package Client;

import java.io.*;
import java.net.*;

public class ServerConnection {
    private final Socket socket;
    private final PrintWriter out;
    private final BufferedReader in;

    public ServerConnection(String serverAddress, int port) throws IOException {
        this.socket = new Socket(serverAddress, port);
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    // Отправка данных на сервер
    public void sendMessage(String message) {
        out.println(message);
        out.flush();
    }

    // Получение данных с сервера
    public String receiveMessage() throws IOException {
        return in.readLine();
    }

    // Закрытие соединения
    public void closeConnection() throws IOException {
        if (in != null) in.close();
        if (out != null) out.close();
        if (socket != null) socket.close();
    }
}