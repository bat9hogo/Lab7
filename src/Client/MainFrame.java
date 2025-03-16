package Client;

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import javax.swing.*;

public class MainFrame extends JFrame {
    private static final String FRAME_TITLE = "Messanger";
    private static final int FrameWidth = 400;
    private static final int FRAME_MINIMUM_HEIGHT = 200;
    private static String sender;
    private final ServerConnection serverConnection;
    private final DefaultListModel<String> dialogsListModel;
    private final JList<String> dialogsList;
    // Хранение открытых окон диалогов
    private final HashMap<String, ChatFrame> openDialogs = new HashMap<>();
    private final JTextField recipientField;

    public MainFrame(ServerConnection connection, String sender) {
        super(FRAME_TITLE);
        serverConnection = connection;
        MainFrame.sender = sender;
        setMinimumSize(new Dimension(FrameWidth, FRAME_MINIMUM_HEIGHT));
        final Toolkit kit = Toolkit.getDefaultToolkit();
        setLocation((kit.getScreenSize().width - getWidth()) / 2,
                (kit.getScreenSize().height - getHeight()) / 2);
        //Отображение списка существующих диалогов
        dialogsListModel = new DefaultListModel<>();
        dialogsList = new JList<>(dialogsListModel);

        dialogsList.setBackground(new Color(245, 245, 245)); // Светло-серый цвет
        dialogsList.setFixedCellHeight(40); // Увеличиваем высоту ячеек
        dialogsList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                // Цвет фона для выбранного элемента
                if (isSelected) {
                    label.setBackground(new Color(220, 220, 220)); // Чуть темнее серый при выборе
                } else {
                    label.setBackground(new Color(245, 245, 245));
                }

                // Добавление нижней границы (разделение чатов)
                label.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200)));

                return label;
            }
        });

        JScrollPane scrollPaneDialogs = new JScrollPane(dialogsList);
        dialogsList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedUser = dialogsList.getSelectedValue();
                if (selectedUser != null) {
                    String[] parts = selectedUser.split(" ");
                    if (parts[0] != null) {
                        try {
                            openChatWindow(parts[0], true);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
                dialogsList.clearSelection();
            }
        });

        recipientField = new JTextField();
        recipientField.setColumns(15); // Устанавливаем ширину поля
        recipientField.setForeground(Color.GRAY);
        recipientField.setText("Enter chat user name");

// Обработчики событий для placeholder
        recipientField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (recipientField.getText().equals("Enter chat user name")) {
                    recipientField.setText("");
                    recipientField.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (recipientField.getText().isEmpty()) {
                    recipientField.setForeground(Color.GRAY);
                    recipientField.setText("Enter chat user name");
                }
            }
        });

        // Кнопка "Начать диалог"
        final JButton startChatButton = new JButton("Start Dialog");
        startChatButton.addActionListener(e -> {
            try {
                startNewDialog();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        // Компоновка элементов
        final GroupLayout layout = new GroupLayout(getContentPane());
        setLayout(layout);
        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(scrollPaneDialogs)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(recipientField) // Добавляем поле ввода имени
                                .addGap(10)
                                .addComponent(startChatButton))) // Размещение кнопки
                .addContainerGap());
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollPaneDialogs)
                .addGap(10)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(recipientField)
                        .addComponent(startChatButton))
                .addContainerGap());

        requestDialogsList();
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        new Thread(new IncomingMessageListener()).start();
    }

//    private void updateDialogList(String newDialogUser) {
//        SwingUtilities.invokeLater(() -> {
//            String[] parts = newDialogUser.split(" ");
//            if (!dialogsListModel.contains(parts[0])) {
//                dialogsListModel.addElement(parts[0]);
//            }
//        });
//    }
private void updateDialogList(String newDialogUser) {
    SwingUtilities.invokeLater(() -> {
        String[] parts = newDialogUser.split(" ");
        if (!dialogsListModel.contains(parts[0]+" (online)") && !dialogsListModel.contains(parts[0]+" (offline)") && !dialogsListModel.contains(parts[0])) {
            dialogsListModel.addElement(parts[0]);
        }
    });
}

    private void startNewDialog() throws IOException {
        String recipient = recipientField.getText().trim();
        if (!recipient.isEmpty() && !Objects.equals(sender, recipient)) {
            checkUserExist(recipient);
        } else if (Objects.equals(recipient, sender)) {
            JOptionPane.showMessageDialog(MainFrame.this, "You can't start a dialog with yourself", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

     //Метод для открытия окна чата с конкретным пользователем
    private void openChatWindow(String recipient, boolean isUserExist) throws IOException {

        if (!openDialogs.containsKey(recipient) && isUserExist) {
            isUserExist = false;
            updateDialogList(recipient);
            ChatFrame chatFrame = new ChatFrame(serverConnection, recipient, sender,openDialogs);
            openDialogs.put(recipient, chatFrame);
            getMessagesFromServer(recipient);
            chatFrame.setVisible(true);
        } else if(openDialogs.containsKey(recipient)){
            JOptionPane.showMessageDialog(this, "A dialog with this user has already been opened.");
        }
        else if(!isUserExist){
            JOptionPane.showMessageDialog(this, "A user name " + recipient + " does not exist");
        }
        else{
            JOptionPane.showMessageDialog(MainFrame.this,
                    "ERROR!",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void getMessagesFromServer(String recipient){
        String formattedMessage = "GET_MESSAGES " + recipient + " " + sender;
        serverConnection.sendMessage(formattedMessage);
    }
    private void checkUserExist(String recipient) throws IOException {
        String formattedMessage = "CHECK_USER_EXIST " + recipient;
        serverConnection.sendMessage(formattedMessage);
    }
    private void requestDialogsList() {
        serverConnection.sendMessage("GET_DIALOGS " + sender);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    final LoginFrame frame = new LoginFrame();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private class IncomingMessageListener implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    String message = serverConnection.receiveMessage();
                    //System.out.println(message);
                    if (message != null && message.startsWith("MESSAGE")) {
                        // Формат сообщения: MESSAGE <отправитель> <сообщение>
                        String[] parts = message.split(" ", 3);
                        if (parts.length == 3) {
                            String sender = parts[1];

                            // Если окно диалога с отправителем ещё не открыто, открываем его
                            SwingUtilities.invokeLater(() -> {
                                if (!openDialogs.containsKey(sender)) {
                                    try {
                                        updateDialogList(sender);
                                        openChatWindow(sender,true);
                                        synchronized (this) {
                                            try {
                                                wait(500); // Ждем немного, пока сервер ответит
                                            } catch (InterruptedException e) {
                                                Thread.currentThread().interrupt();
                                            }
                                        }
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                }else{
                                    openDialogs.get(sender).appendMessage(sender,parts[2]);
                                }

                            });
                        }
                    }
                    else if(message!= null && message.startsWith("USER_EXIST"))
                    {
                        String[] parts = message.split(" ", 3);
                        openChatWindow(parts[2],Objects.equals(parts[1], "TRUE"));
                    }
                    else if (message!= null && message.startsWith("DIALOGS_LIST")) {
                        String[] parts = message.split(" ");
                        if(parts.length >= 2) {
                            SwingUtilities.invokeLater(() -> {
                                dialogsListModel.clear();
                                for (int i = 1; i < parts.length; i++) {
                                    dialogsListModel.addElement(parts[i]);
                                }
                            });
                        }
                    }
                    else if(message!= null && message.startsWith("GETTING_MESSAGES"))
                    {
                        String recipient = message.split(" ")[1];
                        String cleanedMessage = message.replace("GETTING_MESSAGES " + recipient + " ", "").trim();
                        String[] messages = cleanedMessage.split("AbCd1234@xYz");
                        for(int i = 0; i < messages.length-1; i+=2){
                            if(Objects.equals(messages[i], sender)) {openDialogs.get(recipient).appendMessage("Я", messages[i+1]);}
                            else {openDialogs.get(recipient).appendMessage(messages[i], messages[i+1]);}
                        }

                    }
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(MainFrame.this,
                            "Server communication error: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                    break;
                }
            }
        }
    }
}