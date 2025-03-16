package Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Map;


public class ChatFrame extends JFrame {
    private final JTextArea textAreaChat;
    private final JTextField textFieldMessage;
    private final ServerConnection serverConnection;
    private final String recipient;
    private final String sender;

    public ChatFrame(ServerConnection serverConnection, String recipient, String sender,Map<String, ChatFrame> openChats) {
        super("Dialog " + sender + " with " + recipient);
        this.serverConnection = serverConnection;
        this.recipient = recipient;
        this.sender = sender;
        setMinimumSize(new Dimension(300, 200));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                openChats.remove(recipient); // Удаляем окно из мапы
            }
        });
        // Текстовая область для сообщений
        textAreaChat = new JTextArea(15, 0);
        textAreaChat.setEditable(false);
        final JScrollPane scrollPaneChat = new JScrollPane(textAreaChat);

        // Поле ввода сообщения
        textFieldMessage = new JTextField();
        // Placeholder для поля ввода сообщения
        textFieldMessage.setForeground(Color.GRAY);
        textFieldMessage.setText("Enter a message");

// Обработчики событий для placeholder
        textFieldMessage.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (textFieldMessage.getText().equals("Enter a message")) {
                    textFieldMessage.setText("");
                    textFieldMessage.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (textFieldMessage.getText().isEmpty()) {
                    textFieldMessage.setForeground(Color.GRAY);
                    textFieldMessage.setText("Enter a message");
                }
            }
        });
        textFieldMessage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        final JButton sendButton = new JButton("Send");

        // Обработчик отправки сообщения
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        // Компоновка элементов
        final GroupLayout layout = new GroupLayout(getContentPane());
        setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup()
                .addComponent(scrollPaneChat)
                .addGroup(layout.createSequentialGroup()
                        .addComponent(textFieldMessage)
                        .addComponent(sendButton)));
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addComponent(scrollPaneChat)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(textFieldMessage)
                        .addComponent(sendButton)));
        pack();
    }

    // Отправка сообщения
    private void sendMessage() {
        String message = textFieldMessage.getText().trim();
        if (!message.isEmpty()) {
            String formattedMessage = "MESSAGE " + sender + " " + recipient + " " + message;
            serverConnection.sendMessage(formattedMessage);
            appendMessage("Me (" + sender + ")", message);
            textFieldMessage.setText("");
        }
        textFieldMessage.requestFocus();
    }

    public void appendMessage(String sender, String message) {
        textAreaChat.append(sender + " -> " + message + "\n");
    }

}
