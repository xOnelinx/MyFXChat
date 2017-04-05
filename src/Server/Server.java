package Server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Calendar;
import java.util.Vector;


/**
 *
 */
public class Server {

    private ServerSocket serv;
    private Vector<ClientHandler> clients;
    private AuthServise authServise;
    private PrintWriter out;
    public AuthServise getAuthServise() {
        return authServise;
    }

    private final int PORT = 8187;

    public Server() {
        try  {
            out = new PrintWriter("localHistory.txt");
            serv = new ServerSocket(PORT);
            Socket socket = null;
            authServise = new DBAuthService();
            authServise.start();
            clients = new Vector<>();
            while (true) {
                System.out.println("Сервер запущен, ожидаем подключения");
                socket = serv.accept();
                System.out.println("Клиент подключился");
                new ClientHandler(socket,this);
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Ошибка подключения сервера");
        }finally {
            try {
                serv.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            authServise.stop();
        }

    }

    public synchronized void sendMsgToClient(ClientHandler from, String nickTo , String msg){
        from.sendMsg("to " + nickTo + " :" + msg);
        for (ClientHandler o: clients){
            if (o.getName().equals(nickTo)){
                o.sendMsg("from "+from.getName()+" :" +msg);
                break;
            }
        }
    }

    public synchronized boolean isNickBusy (String nick){
        for (ClientHandler o:clients
             ) {
            if (o.getName().equals(nick)) return true;
        }
        return false;
    }
    public synchronized void brodcastClientsList (){
        StringBuilder sb = new StringBuilder("/clients ");
        for (ClientHandler o:clients) {
            sb.append(o.getName() + " ");
        }
        broadcastMsg(sb.toString());

    }

    public synchronized void broadcastMsg(String msg){
        for (ClientHandler c:clients
             ) {
            c.sendMsg(msg);
        }
    }

    public void hist(String msg){
        out.println(Calendar.getInstance().getTime().toString() +" "+ msg);
        out.flush();
    }

    public synchronized void unsubscribe(ClientHandler o){
        clients.remove(o);
        brodcastClientsList();
    }
    public synchronized void subscribe(ClientHandler o){
        clients.add(o);
        brodcastClientsList();
    }

}
