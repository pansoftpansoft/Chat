import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class ChatApiHandler {

    interface Callback {

        void onAuth(boolean isSuccess, String serverError);

        void onNewMessage(String message);

        void closeSignInWindow();
    }

    //ExecutorService executor = Executors.newSingleThreadExecutor();

    Callback callback;

    DataInputStream dataInputStream;
    DataOutputStream dataOutputStream;

    ChatApiHandler(Callback callback) {
        this.callback = callback;
        try {
            Socket socket = new Socket("localhost", 8081);
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            new Thread(() -> {
                try {
                    while (true) {
                        try {
                        String newMessage = dataInputStream.readUTF();
                            if (newMessage.startsWith("/auth")) {
                                System.out.println(newMessage);
                                if (newMessage.equals("/auth ok")) {
                                    System.out.println("response success");
                                    callback.onAuth(true, null);
                                } else {
                                    callback.onAuth(false, newMessage);
                                }
                            } else if (newMessage.startsWith("/nm ") && newMessage.length() > 5) {
                                callback.onAuth(false, newMessage.substring(4));
                                callback.onNewMessage(newMessage);
                            }
                        } catch (EOFException e){
                            System.out.println("Истекло время обращения к сокету. Вас отключили.");
                            callback.closeSignInWindow();
                            return;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    synchronized void auth(String login, String password) {
        new Thread(() -> {
            try {
                String command = "/auth " + login + " " + password;
                System.out.println(command);
                dataOutputStream.writeUTF(command);
            } catch (IOException e) {
                System.out.println("response exception");
                callback.onAuth(false, null);
            }
        }).start();
    }

    synchronized void sendMessage(String text) {
        new Thread(() -> {
            try {
                dataOutputStream.writeUTF(text);
            } catch (IOException e) {
            }
        }).start();
    }
}


