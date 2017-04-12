package Server;

import java.util.ArrayList;

/**
 * Created by Денис on 21.03.2017.
 */
public class BaseAuthService implements AuthServise {
    class Entry{
        private String login;
        private String pass;
        private String nick;

        public Entry(String login, String pass, String nick) {
            this.login = login;
            this.pass = pass;
            this.nick = nick;
        }
    }

    private ArrayList <Entry> entries;

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    public BaseAuthService(){
        entries = new ArrayList<>();
        entries.add(new Entry("login1","pass1","nick1"));
        entries.add(new Entry("login2","pass2","nick2"));
        entries.add(new Entry("login3","pass3","nick3"));

    }

    @Override
    public String getNick(String login, String pass) {
        for (Entry o:entries
             ) {
            if (o.login.equals(login)&& o.pass.equals(pass))return o.nick;

        }
        return null;
    }

    @Override
    public boolean chengeNick(ClientHandler c, String newNick) {
        for (Entry o: entries){
            if (o.nick.equals(newNick))return false;
        }
        for (Entry o:entries){
            if(o.nick.equals(c.getName())){
                o.nick = newNick;
                return true;
            }
        }
        return false;
    }
}
