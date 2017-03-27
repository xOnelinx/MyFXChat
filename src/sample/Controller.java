package sample;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;


public class Controller implements Initializable{

    @FXML
    public TextArea textArea;
    @FXML
    public TextField textField;

    public TextField loginField;
    public PasswordField passField;

    public HBox upperPanel;
    public AnchorPane bottomPanel;

    private boolean authorized;

    public void setAuthorized(boolean authorized) {
        this.authorized = authorized;
        if (!this.authorized){
            upperPanel.setManaged(true);
            upperPanel.setVisible(true);
            bottomPanel.setManaged(false);
            bottomPanel.setVisible(false);
        } else {
            upperPanel.setManaged(false);
            upperPanel.setVisible(false);
            bottomPanel.setManaged(true);
            bottomPanel.setVisible(true);
            textArea.clear();
        }

    }


    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        try {
            socket = new Socket("localhost", 8187);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            setAuthorized(false);
            Thread t = new Thread(()->{
                try{
                    //цикл авторизации
                    while (true){
                        String str = in.readUTF();
                        if (str.startsWith("/authok")){
                           setAuthorized(true);
                            break;
                        }
                        textArea.appendText(str + "\n");
                    }
                    //цикл беседы
                    while (true){
                        String str = in.readUTF();
                        if (str.equals("/end")){
                            break;
                        }
                        textArea.appendText(str+"\n");
                    }
                }catch(IOException e){
                    e.printStackTrace();
                }finally {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    setAuthorized(false);
                }

            });
       //     t.setDaemon(true);
            t.start();
        } catch (IOException e){
            showAlert("Не удалось подключится к серверу");
            Platform.exit();
        }

    }

    public void onSendMsg(){
        try {
            out.writeUTF(textField.getText());
            if (textField.getText().equals("/ebd")){
                socket.close();
            }
            textField.clear();
            textField.requestFocus();
        }catch (IOException e){
            showAlert("Проверьте сетевое соединение...");
            e.printStackTrace();
        }
    }
    //TODO че за авторизация???
    public void onAuthClick() {
        try {
            out.writeUTF("/auth "+ loginField.getText() + " " + passField.getText());
            loginField.clear();
            passField.clear();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void showAlert(String msg){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Возникли проблемы");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
    public void closeConnection() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
