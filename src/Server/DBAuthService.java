package Server;




import org.sqlite.ExtendedCommand;

import java.sql.*;

/**
 * Created by Денис on 21.03.2017.
 */
public class DBAuthService implements AuthServise{
    private Connection connection;
    private PreparedStatement ps;
    private PreparedStatement psChangeNick;
    private PreparedStatement psCheckNick;
    public void start(){
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:ChatBase.db");
            ps = connection.prepareStatement("SELECT Nick FROM entrys WHERE Login = ? AND Pass = ?;");
            psChangeNick = connection.prepareStatement("UPDATE entrys SET Nick = ? WHERE Nick = ?;");
            psCheckNick = connection.prepareStatement("SELECT COUNT(*) FROM entrys WHERE Nick = ?");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean chengeNick(ClientHandler c, String newNick) {
        try {
            psCheckNick.setString(1,newNick);
            ResultSet rs = psCheckNick.executeQuery();
            rs.next();
            if (rs.getInt(1)==0) {
                psChangeNick.setString(1, c.getName());
                psChangeNick.setString(2, newNick);
                psChangeNick.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            return  false;
        }
        return false;
    }

    @Override
    public String getNick(String login, String pass) {

        try {
            ps.setString(1, login);
            ps.setString(2, pass);
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                return rs.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void stop(){
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
