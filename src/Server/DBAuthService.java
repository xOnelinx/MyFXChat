package Server;

import java.sql.*;

/**
 * Created by Денис on 21.03.2017.
 */
public class DBAuthService implements AuthServise{
    private Connection connection;
    private PreparedStatement ps;

    public void start(){
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:ChatBase.db");
            ps = connection.prepareStatement("SELECT Nick FROM entrys WHERE Login = ? AND Pass = ?;");
        } catch (Exception e) {
            e.printStackTrace();
        }
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
