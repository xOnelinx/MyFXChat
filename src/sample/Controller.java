package sample;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
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

    public ListView clientList;

    public TextField loginField;
    public PasswordField passField;

    public HBox upperPanel;
    public AnchorPane bottomPanel;

    private boolean authorized;
    private String myNick = "";

    public void setAuthorized(boolean authorized) {
        this.authorized = authorized;
        if (!this.authorized){
            upperPanel.setManaged(true);
            upperPanel.setVisible(true);
            bottomPanel.setManaged(false);
            bottomPanel.setVisible(false);
            clientList.setManaged(false);
            clientList.setVisible(false);
        } else {
            upperPanel.setManaged(false);
            upperPanel.setVisible(false);
            bottomPanel.setManaged(true);
            bottomPanel.setVisible(true);
            clientList.setManaged(true);
            clientList.setVisible(true);
            textArea.clear();
            Platform.runLater(() -> textField.requestFocus());
        }

    }


    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    @Override
    public void initialize(URL location, ResourceBundle resources) { setAuthorized(false);



    }

    void start() {
        try {
            setAuthorized(false);
            socket = new Socket("localhost", 8187);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());



            Thread t = new Thread(()->{
                try{
                    //цикл авторизации
                    while (true){
                        String str = in.readUTF();
                        if (str.startsWith("/authok")){
                            setAuthorized(true);
                            myNick = str.split("\\s")[1];
                            Platform.runLater(() -> Main.mainStage.setTitle("JavaFX Client: " + myNick));
                            break;
                        }
                        textArea.appendText(str + "\n");
                    }
                    //цикл беседы
                    while (true){
                        String str = in.readUTF();
                        if (str.startsWith("/")){
                            if (str.equals("/end")){
                            break;
                            }
                            if (str.startsWith("/clients ")){
                                String[] s = str.split(" ");
                                Platform.runLater(() -> {
                                    clientList.getItems().clear();
                                        for (int i = 1; i <s.length ; i++) {
                                            clientList.getItems().add(s[i]);
                                        }
                                    }
                                );
                            }
                            if (str.startsWith("/yournickis ")){
                                myNick = str.split(" ")[1];
                            }
                        }else {
                           textArea.appendText(str + "\n");
                        }
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
            t.start();
        } catch (IOException e){
            showAlert("Не удалось подключится к серверу");
            Platform.exit();
        }
    }

    public void onSendMsg(){
        try {
            if (textField.getText().equals("/help")){
                textArea.appendText("HELP:\n\\w - шепнуть\\changenick - сменить имя\\" +
                        "\\end - выйти");
                return;
            }
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
        if (socket == null || socket.isClosed())
            start();
        try {

            out.writeUTF("/auth "+ loginField.getText() + " " + passField.getText());
            loginField.clear();
            passField.clear();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            textField.requestFocus();
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
    public void listClick (MouseEvent mouseEvent){
        if (mouseEvent.getClickCount()==2){
            if (clientList.getSelectionModel().getSelectedItem().toString().equals(myNick)){
                textField.setText("/changenick ");
            }else {
                textField.setText("/w "
                        + clientList.getSelectionModel().getSelectedItem().toString() + " ");
                textField.requestFocus();
            }
        }
    }
}
