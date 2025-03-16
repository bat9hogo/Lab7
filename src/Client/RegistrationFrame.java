package Client;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Properties;
import javax.swing.*;

public class RegistrationFrame extends JFrame {
    private static final String FrameTitle = "Registration";
    private static final int FrameWidth = 375;
    private static final int FrameHeight = 200;
    private final JTextField TextFieldLogin;
    private final JTextField TextFieldPassword1;
    private final JTextField TextFieldPassword2;

    public  RegistrationFrame() throws IOException {
        super(FrameTitle);
        setMinimumSize(new Dimension(FrameWidth, FrameHeight));// Центрирование окна
        final Toolkit kit = Toolkit.getDefaultToolkit();
        setLocation((kit.getScreenSize().width - getWidth()) / 2,
                (kit.getScreenSize().height - getHeight()) / 2);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        final JButton registrationButton = new JButton("Registration");
        Properties properties = new Properties();

        try (InputStream in = Files.newInputStream(Paths.get("src\\server.properties"))) {
            properties.load(in);
        }

        String IP = properties.getProperty("IP");
        String port = properties.getProperty("port");
        registrationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!TextFieldLogin.getText().isEmpty() && !TextFieldPassword1.getText().isEmpty() && Objects.equals(TextFieldPassword1.getText(), TextFieldPassword2.getText()))
                {
                    InetAddress localHost;
                    try {
                        localHost = InetAddress.getLocalHost();
                    } catch (UnknownHostException ex) {
                        throw new RuntimeException(ex);
                    }

                    String ipAddress = localHost.getHostAddress();
                    String request = "REGISTER " + TextFieldLogin.getText() + " " + TextFieldPassword1.getText() + " " + ipAddress;
                    try {
                        ServerConnection serverConnection = new ServerConnection(IP,Integer.parseInt(port));
                        serverConnection.sendMessage(request);
                        String response = serverConnection.receiveMessage();
                        if (response.equals("REGISTER_SUCCESS")) {
                            JOptionPane.showMessageDialog(RegistrationFrame.this, "Registration was successful. Log in to your account.");
                            dispose();
                            final LoginFrame loginframe = new LoginFrame(serverConnection);
                        } else {
                            JOptionPane.showMessageDialog(RegistrationFrame.this, "The username is taken.");
                        }
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }

                }else if (TextFieldPassword1.getText().isEmpty() && TextFieldLogin.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(RegistrationFrame.this, "Enter your login and password.");
                }
                else if (TextFieldLogin.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(RegistrationFrame.this, "Enter your login");
                }
                else {
                    JOptionPane.showMessageDialog(RegistrationFrame.this, "The passwords don't match.");
                }
            }
        });
        // Подписи полей
        final JLabel labelForName = new JLabel("Login");
        final JLabel labelForPassword1 = new JLabel("Password");
        final JLabel labelForPassword2 = new JLabel("Repeat password");
        TextFieldLogin = new JTextField(30);
        TextFieldPassword1 = new JTextField(30);
        TextFieldPassword2 = new JTextField(30);

        // Настройка панели с использованием GroupLayout
        JPanel panel = new JPanel();
        GroupLayout layout = new GroupLayout(panel);
        panel.setLayout(layout);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Отступы от краев

        layout.setAutoCreateGaps(true); // Автоматические отступы между элементами
        layout.setAutoCreateContainerGaps(true); // Автоматические отступы от краев контейнера

        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.CENTER) // Центрируем все элементы
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(labelForName)
                                        .addComponent(labelForPassword1)
                                        .addComponent(labelForPassword2))
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(TextFieldLogin)
                                        .addComponent(TextFieldPassword1)
                                        .addComponent(TextFieldPassword2)))
                        .addComponent(registrationButton) // Центрируем кнопку регистрации
        );

        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(labelForName)
                                .addComponent(TextFieldLogin))
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(labelForPassword1)
                                .addComponent(TextFieldPassword1))
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(labelForPassword2)
                                .addComponent(TextFieldPassword2))
                        .addGap(20)
                        .addComponent(registrationButton) // Центрируем кнопку
        );
        add(panel);
        setVisible(true);
    }
}