package Server;

import java.io.IOException;

class MessageConnetcion {
    public static void processMessage(String sender, String recipient, String message, DBConnector db, ClientConnection senderHandler) {
            ClientConnection recipientHandler = MessageServer.getClientHandler(recipient);
            if (recipientHandler != null) {
                try {
                    recipientHandler.sendMessage("MESSAGE " + sender + " " + message);
                    senderHandler.sendMessage("MESSAGE_DELIVERED");
                    db.saveMessage(sender, recipient, message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else {
                db.saveMessage(sender, recipient, message);

            }
    }
}