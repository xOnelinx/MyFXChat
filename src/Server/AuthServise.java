package Server;

/**
 * Created by Денис on 21.03.2017.
 */
public interface AuthServise {
    void start();
    String getNick(String login,String pass);
    boolean chengeNick (ClientHandler c,String newNick);
    void stop();
}
