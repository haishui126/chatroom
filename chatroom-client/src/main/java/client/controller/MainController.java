package client.controller;

import client.ChatStage;
import client.ClientHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    private static String username;
    public Label usernameLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        usernameLabel.setText(username);
        ClientHandler handler = ClientHandler.getInstance();
        handler.setMessageListener(message -> {
            ChatStage stage = ChatStage.get(message.getFrom());
            if (stage == null) {
                stage = new ChatStage(username, message.getFrom());
                try {
                    stage.start(new Stage());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            stage.addMessage(message);
        });
    }

    public void chat() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("发起聊天");
        dialog.setHeaderText(null);
        dialog.setContentText("好友的昵称：");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            try {
                new ChatStage(username, name).start(new Stage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void setUsername(String username) {
        MainController.username = username;
    }
}
