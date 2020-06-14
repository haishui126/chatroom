package client.controller;

import client.ChatStage;
import client.ClientHandler;
import client.GroupStage;
import common.model.Message;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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

        handler.setFileListener(uploadFile -> {
            File file = new File("./fileRecv/" + uploadFile.getFilename());
            if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
            try (FileOutputStream outputStream = new FileOutputStream(file)) {
                outputStream.write(uploadFile.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
            ChatStage stage = ChatStage.get(uploadFile.getFrom());
            if (stage == null) {
                stage = new ChatStage(username, uploadFile.getFrom());
                try {
                    stage.start(new Stage());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Message message = new Message(uploadFile.getFrom(), uploadFile.getTo(), "接收到对方发来的文件【" + uploadFile.getFilename() + "】，已保存在软件fileRecv文件夹下");
            stage.addMessage(message);
        });

        handler.setGroupMessageListener(message -> {
            GroupStage stage = GroupStage.get(message.getGroup());
            if (stage != null) {
                stage.addMessage(message);
            }
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

    public void groupChat() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("加入群聊");
        dialog.setHeaderText(null);
        dialog.setContentText("群名称：");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            try {
                new GroupStage(username, name).start(new Stage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
