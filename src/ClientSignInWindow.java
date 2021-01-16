import javax.swing.*;

public class ClientSignInWindow extends JFrame {

    private final JLabel error;

    interface Callback {
        void onLoginClick(String login, String password);
    }

    ClientSignInWindow(Callback callback) {
        setBounds(500, 500, 300, 120);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        add(new JLabel("Пожалуйста авторизайтесь"));
        JTextField loginField = new JTextField();
        loginField.setName("Логин");
        JTextField passwordField = new JTextField();
        passwordField.setName("Пароль");
        JButton signInButton = new JButton("Войти");
        signInButton.addActionListener((e) -> {
            callback.onLoginClick(loginField.getText(), passwordField.getText());
        });
        error = new JLabel();
        add(loginField);
        add(passwordField);
        add(signInButton);
        add(error);
    }

    void showError(String errorText) {
        error.setText(errorText);
    }
}
