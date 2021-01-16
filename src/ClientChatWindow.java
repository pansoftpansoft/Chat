import javax.swing.*;
import java.awt.*;

public class ClientChatWindow extends JFrame {

    final JTextArea messagesList;

    interface Callback {

        void sendMessage(String text);
    }

    ClientChatWindow(Callback callback) {
        setBounds(500, 500, 600, 500);
        setLayout(new GridLayout(2, 1));
        // Список сообщений
        messagesList = new JTextArea();
        messagesList.setEditable(false);
        add(messagesList);
        // Панель новго сообщения
        JPanel sendMassagePanel = new JPanel();
        sendMassagePanel.setLayout(new BoxLayout(sendMassagePanel, BoxLayout.X_AXIS));
        sendMassagePanel.setSize(600, 50);
        JTextField messageField = new JTextField();
        JButton sendButton = new JButton("Отправить");
        sendMassagePanel.add(messageField);
        sendMassagePanel.add(sendButton);
        add(sendMassagePanel);
        sendButton.addActionListener((e) -> {
            callback.sendMessage(messageField.getText());
        });
    }

    public void onNewMessage(String message) {
        messagesList.setText(messagesList.getText() + "\n" + message);
    }
}
