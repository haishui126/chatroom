package client.controller;

import client.ChatStage;
import client.ClientHandler;
import client.GroupStage;
import client.util.FriendCell;
import common.model.Message;
import common.model.User;
import common.model.UserOption;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    private static String username;
    public Label usernameLabel;
    public ListView<User> friendListView;
    ClientHandler handler;
    private static List<User> userList = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        friendListView.getItems().addAll(userList);
        usernameLabel.setText(username);
        friendListView.setCellFactory(userListView -> new FriendCell());
        handler = ClientHandler.getInstance();
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

        handler.setUserListener(user -> {
            if (user.getOp() == -1) {
                friendListView.getItems().remove(user);
            } else if (user.getOp() == 0) {
                ObservableList<User> users = friendListView.getItems();
                int i = users.indexOf(user);
                users.set(i, user);
            } else {
                friendListView.getItems().add(user);
            }
        });

        handler.setUserOptionListener(userOption -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("添加好友");
            alert.setHeaderText(null);
            alert.setContentText("用户【" + userOption.getFrom() + "】请求添加您为好友");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                userOption.setOp(1);
                handler.sendUserOption(userOption);
            }
        });
    }

    public static void setUsername(String username) {
        MainController.username = username;
    }

    public static String getUsername() {
        return username;
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

    public void addFriend() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("添加好友");
        dialog.setHeaderText(null);
        dialog.setContentText("好友名称：");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> handler.sendUserOption(new UserOption(username, name, 0)));
    }

    public static void setUserList(List<User> userList) {
        MainController.userList = userList;
    }
}
