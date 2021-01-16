import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Timer;
import java.util.TimerTask;

class ClientHandler {
    AuthService authService;
    Server server;
    Socket socket;
    DataOutputStream dataOutputStream;
    DataInputStream dataInputStream;
    Client client;
    private TimerTaskCloseSocket myTimerTask;


    ClientHandler(AuthService authService, Server server, Socket socket) {
        this.authService = authService;
        this.server = server;
        this.socket = socket;
        try {
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataInputStream = new DataInputStream(socket.getInputStream());

            Timer timer = new Timer();
            myTimerTask = new TimerTaskCloseSocket();
            timer.schedule(myTimerTask, 120000);
            if (!auth(dataInputStream, dataOutputStream)) {
                // Удаляемся из сервера
                closeNetAll();
                server.onClientDisconnected(this);
                return;
            }
            server.onNewClient(this);
            messageListener(dataInputStream);
        } catch (IOException e) {
            // Удаляемся из сервера
            try {
                dataInputStream.close();
                dataOutputStream.close();
                socket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            server.onClientDisconnected(this);
            e.printStackTrace();
        }
    }

    void sendMessage(Client client, String text) {
        try {
            dataOutputStream.writeUTF("/nm " + client.name + ": " + text);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean auth(DataInputStream dataInputStream, DataOutputStream dataOutputStream) throws IOException {
        // Цикл ожидания авторизации клиентов
        int tryCount = 0;
        int maxTryCount = 5;
        while (true) {
            // Читаем комманду от клиента
            try {
                String newMessage = dataInputStream.readUTF();
                // Разбиваем сообщение на состовляющие комманды
                String[] messageData = newMessage.split("\\s");
                // Проверяем соответсует ли комманда комманде авторизации
                if (messageData.length == 3 && messageData[0].equals("/auth")) {
                    tryCount++;
                    String login = messageData[1];
                    String password = messageData[2];
                    // Зарегистрирован ли данных пользователь
                    client = authService.auth(login, password);
                    if (client != null) {
                        // Если получилось авторизоваться то выходим из цикла
                        dataOutputStream.writeUTF("/auth ok");
                        System.out.println("Login success");
                        break;
                    } else {
                        dataOutputStream.writeUTF("Неправильные логин и пароль!");
                    }
                } else {
                    dataOutputStream.writeUTF("Ошибка авторизации!");
                }
                if (tryCount == maxTryCount) {
                    dataOutputStream.writeUTF("Первышен лимит попыток!");
                    closeNetAll();
                    return false;
                }
            } catch (SocketException e) {
                return false;
            }
        }
        return true;
    }

    private void messageListener(DataInputStream dataInputStream) throws IOException {
        while (true) {
            String newMessage = dataInputStream.readUTF();
            if (newMessage.equals("/exit")) {
                dataOutputStream.writeUTF("/exit ok");
                closeNetAll();
            } else if (newMessage.startsWith("/w ")) {
                String messageWithoutCommand = newMessage.substring(3);
                int messageIndex = messageWithoutCommand.indexOf(" ");
                String nick = messageWithoutCommand.substring(0, messageIndex);
                String message = messageWithoutCommand.substring(messageIndex);
                dataOutputStream.writeUTF("/w ok");
                server.sendMessageTo(client, nick, message);
            } else {
                dataOutputStream.writeUTF("/b ok");
                server.sendBroadCastMessage(client, newMessage);
            }
        }
    }

    private void closeNetAll() {
        try {
            dataInputStream.close();
            dataOutputStream.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    class TimerTaskCloseSocket extends TimerTask {
        @Override
        public void run() {
            if (client != null) {
                System.out.println("Пользователь " + client.login + " работает на сокете : " + socket.toString());
            } else {
                System.out.println("Закроем сокет: " + socket.toString());
                closeNetAll();
            }
        }
    }
}