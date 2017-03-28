package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by Денис on 19.03.2017.
 */
public class ClientHandler {
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private Server server;
    private String name;

    public String getName() {
        return name;
    }

    public ClientHandler(Socket socket, Server server) {
        try {
            this.server = server;
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            new Thread(()->{
                try {
                    while (true){
                        String str = in.readUTF();
                        if (str.startsWith("/auth")){
                            String[] parts = str.split("\\s");
                            String nick = server.getAuthServise().getNick(parts[1],parts[2]);
                            if (nick != null){
                                if (!server.isNickBusy(nick)) { //если ник не занят то..
                                    sendMsg("/authok " + nick);
                                    name = nick;
                                    server.broadcastMsg(name+" зашел в чат");
                                    server.subscribe(this);
                                    break;
                                }else sendMsg("Учетная запись занята");
                            }else {
                                sendMsg("Неверный логин/пароль");
                            }
                        }
                    }
                    while (true) {

                        String str = in.readUTF();
                        System.out.println("from client: " + str);
                        if (str.startsWith("/")) {
                            if (str.equals("/end")) break;
                            if (str.startsWith("/w")){
                                String[] tokens = str.split("\\s");
                                String nick = tokens[1];
                                String msg = str.substring(4 + nick.length());
                                server.sendMsgToClient(this,nick,msg);
                            }
                        }else {
                            server.broadcastMsg(name + " :" + str);
                        }
                    }
                }catch (IOException e){
                    e.printStackTrace();
                }finally {
                    server.unsubscribe(this);
                    server.broadcastMsg(name+" вышел из чата");
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        } catch (Exception e) {
            throw new RuntimeException("Проблемы при создании обработчика клиента");
        }
    }
    public void sendMsg(String msg){
        try {
            out.writeUTF(msg);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
