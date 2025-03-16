package Client;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import javax.swing.*;

@SuppressWarnings("serial")
public class LoginFrame extends JFrame {
    private static final String FrameTitle = "Sign in";
    private static final int FrameWidth = 375;
    private static final int FrameHeight = 225;
    private final JTextField TextFieldLogin;
    private final JTextField TextFieldPassword;

    public LoginFrame() throws IOException {
        super(FrameTitle);
        setMinimumSize(new Dimension(FrameWidth, FrameHeight)); // Центрирование окна
        final Toolkit kit = Toolkit.getDefaultToolkit();
        setLocation((kit.getScreenSize().width - getWidth()) / 2, (kit.getScreenSize().height - getHeight()) / 2);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        final JButton loginButton = new JButton("Sign in");
        Properties properties = new Properties();

        try (InputStream in = Files.newInputStream(Paths.get("src\\server.properties"))) {
            properties.load(in);
        }

        // Получаем URL, имя пользователя и пароль
        String IP = properties.getProperty("IP");
        String port = properties.getProperty("port");

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!TextFieldPassword.getText().isEmpty() && !TextFieldLogin.getText().isEmpty()) {
                    InetAddress localHost = null;
                    try {
                        localHost = InetAddress.getLocalHost();
                    } catch (UnknownHostException ex) {
                        throw new RuntimeException(ex);
                    }
                    String ipAddress = localHost.getHostAddress();
                    String request = "LOGIN " + TextFieldLogin.getText() + " " + TextFieldPassword.getText() + " " + ipAddress;
                    try {
                        ServerConnection serverConnection = new ServerConnection(IP, Integer.parseInt(port));
                        serverConnection.sendMessage(request);
                        String response = serverConnection.receiveMessage();
                        System.out.println("Received response: " + response);
                        if (response.equals("LOGIN_SUCCESS")) {
                            dispose();
                            final MainFrame mainFrame = new MainFrame(serverConnection, TextFieldLogin.getText());
                        } else {
                            JOptionPane.showMessageDialog(LoginFrame.this, "Invalid login or password.");
                        }
                    } catch (IOException ex) {
                       throw new RuntimeException(ex);
                    }
                } else if (TextFieldPassword.getText().isEmpty() && TextFieldLogin.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(LoginFrame.this, "Enter your login and password.");
                } else if (TextFieldPassword.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(LoginFrame.this, "Enter your password.");
                } else if (TextFieldLogin.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(LoginFrame.this, "Enter your login.");
                }
            }
        });

        // Кнопка для перехода в меню регистрации
        final JButton registrationButton = new JButton("Sign up");
        registrationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Закрываем окно входа
                try {
                    final RegistrationFrame registrationFrame = new RegistrationFrame(); // Открываем окно регистрации
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        final JLabel labelForLogin = new JLabel("Login");
        final JLabel labelForPassword = new JLabel("Password");
        final JLabel instructionLabel = new JLabel("If you are not registered, press the button.");
        instructionLabel.setHorizontalAlignment(SwingConstants.CENTER); // Центрируем текст

        TextFieldLogin = new JTextField(30);
        TextFieldPassword = new JTextField(30);

        JPanel panel = new JPanel();
        GroupLayout layout = new GroupLayout(panel);
        panel.setLayout(layout);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Отступы от краев

        layout.setAutoCreateGaps(true); // Автоматические отступы между элементами
        layout.setAutoCreateContainerGaps(true); // Автоматические отступы от краев контейнера

        // Выравнивание элементов
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(labelForLogin)
                                        .addComponent(labelForPassword))
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(TextFieldLogin)
                                        .addComponent(TextFieldPassword)))
                        .addComponent(loginButton)
                        .addComponent(instructionLabel)
                        .addComponent(registrationButton)
        );

        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(labelForLogin)
                                .addComponent(TextFieldLogin))
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(labelForPassword)
                                .addComponent(TextFieldPassword))
                        .addGap(20)
                        .addComponent(loginButton)
                        .addComponent(instructionLabel)
                        .addComponent(registrationButton)
        );

        add(panel);
        setVisible(true);
    }

    public LoginFrame(ServerConnection serverConnection) {
        super(FrameTitle);
        setMinimumSize(new Dimension(FrameWidth, FrameHeight-25)); // Центрирование окна
        final Toolkit kit = Toolkit.getDefaultToolkit();
        setLocation((kit.getScreenSize().width - getWidth()) / 2, (kit.getScreenSize().height - getHeight()) / 2);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Кнопка для входа
        final JButton loginButton = new JButton("Sign in");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!TextFieldPassword.getText().isEmpty() && !TextFieldLogin.getText().isEmpty()) {
                    InetAddress localHost = null;
                    try {
                        localHost = InetAddress.getLocalHost();
                    } catch (UnknownHostException ex) {
                        throw new RuntimeException(ex);
                    }
                    String ipAddress = localHost.getHostAddress();
                    String request = "LOGIN " + TextFieldLogin.getText() + " " + TextFieldPassword.getText() + " " + ipAddress;
                    serverConnection.sendMessage(request);
                    String response = null;
                    try {
                        response = serverConnection.receiveMessage();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                    if (response.equals("LOGIN_SUCCESS")) {
                        dispose();
                        final MainFrame mainFrame = new MainFrame(serverConnection, TextFieldLogin.getText());
                    } else {
                        JOptionPane.showMessageDialog(LoginFrame.this, "Invalid login or password.");
                    }
                } else if (TextFieldPassword.getText().isEmpty() && TextFieldLogin.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(LoginFrame.this, "Enter your login and password.");
                } else if (TextFieldPassword.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(LoginFrame.this, "Enter your password.");
                } else if (TextFieldLogin.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(LoginFrame.this, "Enter your login.");
                }
            }
        });

        final JLabel labelForLogin = new JLabel("Login");
        final JLabel labelForPassword = new JLabel("Password");
        // Текст с инструкцией

        TextFieldLogin = new JTextField(30);
        TextFieldPassword = new JTextField(30);

        JPanel panel = new JPanel();
        GroupLayout layout = new GroupLayout(panel);
        panel.setLayout(layout);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Отступы от краев

        layout.setAutoCreateGaps(true); // Автоматические отступы между элементами
        layout.setAutoCreateContainerGaps(true); // Автоматические отступы от краев контейнера

        // Выравнивание элементов
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(labelForLogin)
                                        .addComponent(labelForPassword))
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(TextFieldLogin)
                                        .addComponent(TextFieldPassword)))
                        .addComponent(loginButton)
        );

        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(labelForLogin)
                                .addComponent(TextFieldLogin))
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(labelForPassword)
                                .addComponent(TextFieldPassword))
                        .addGap(20)
                        .addComponent(loginButton)
        );

        add(panel);
        setVisible(true);
    }
}
