public class ClientApp implements
        ClientSignInWindow.Callback,
        ClientChatWindow.Callback,
        ChatApiHandler.Callback {

    final ChatApiHandler api;
    final ClientSignInWindow clientSignInWindow;
    final ClientChatWindow clientChatWindow;

    ClientApp() {
        api = new ChatApiHandler(this);
        clientSignInWindow = new ClientSignInWindow(this);
        clientChatWindow = new ClientChatWindow(this);
        showSignInWindow();
    }

    public static void main(String[] args) {
        new ClientApp();
    }

    @Override
    public void onLoginClick(String login, String password) {
        api.auth(login, password);
    }

    @Override
    public synchronized void onAuth(boolean isSuccess, String serverError) {
        System.out.println("login: " + isSuccess);
        if (isSuccess) {
            hideSignInWindow();
            showChatWindow();
            // Открываем окно чата
        } else {
            clientSignInWindow.showError(serverError);
        }
    }

    @Override
    public synchronized void closeSignInWindow() {
        System.out.println("login: " + false);
        System.out.println("Закрывем окно ввода пароля и приложение." + false);
        //Закрываем окно ввода пароля
        hideSignInWindow();
        System.exit(0);
    }


    @Override
    public void sendMessage(String text) {
        api.sendMessage(text);
    }

    @Override
    public void onNewMessage(String message) {
        synchronized (clientChatWindow) {
            clientChatWindow.onNewMessage(message);
        }
    }

    private void showSignInWindow() {
        clientSignInWindow.setVisible(true);
    }

    private void hideSignInWindow() {
        clientSignInWindow.setVisible(false);
    }

    private void showChatWindow() {
        clientChatWindow.setVisible(true);
    }

    private void hideChatWindow() {
        clientChatWindow.setVisible(false);
    }
}
