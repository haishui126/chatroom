package client;

import common.dao.GroupMessageDao;
import common.model.GroupMessage;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.LinkedList;
import java.util.List;

public class GroupStage extends Application {
    private static final List<GroupStage> stageManager = new LinkedList<>();
    private final String me;
    private final String group;
    private VBox vBox;
    private GroupMessageDao groupMessageDao;
    ClientHandler handler;
    private ListView<GroupMessage> messageListView;
    private TextArea msgArea;

    public GroupStage(String me, String group) {
        this.me = me;
        this.group = group;
    }

    @Override
    public void start(Stage stage) throws Exception {
        handler = ClientHandler.getInstance();
        GroupMessage groupMessage = new GroupMessage(me, group, null);
        groupMessage.setFlag(1);
        handler.sendGroupMessage(groupMessage);
        stageManager.add(this);
        layoutChildren();
        stage.setScene(new Scene(vBox, 600, 500));
        stage.setTitle(group);
        stage.setOnCloseRequest(windowEvent -> {
            stageManager.remove(this);
            GroupMessage message = new GroupMessage(me, group, null);
            groupMessage.setFlag(-1);
            handler.sendGroupMessage(message);
        });
        stage.show();
    }

    private void layoutChildren() {
        vBox = new VBox();
        vBox.setPadding(new Insets(20.0));
        vBox.setSpacing(20.0);
        groupMessageDao = App.getDB().getDao(GroupMessageDao.class);
        messageListView = new ListView<>();
        messageListView.setFocusTraversable(false);
        messageListView.getItems().addAll(groupMessageDao.getAll(me, group));
        msgArea = new TextArea();
        Button sendBtn = new Button("发送(回车)");
        vBox.getChildren().addAll(messageListView, msgArea, sendBtn);
        messageListView.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(GroupMessage message, boolean empty) {
                super.updateItem(message, empty);
                if (!empty) {
                    VBox box = new VBox();
                    Label name = new Label();
                    if (message.getFrom().equals(GroupStage.this.me)) {
                        box.setAlignment(Pos.CENTER_RIGHT);
                        name.setText("我说：");
                    } else {
                        name.setText(message.getFrom() + "说：");
                    }
                    name.setFont(Font.font(11));
                    name.setTextFill(Paint.valueOf("#999999"));
                    Label msg = new Label(message.getMsg());
                    msg.setFont(Font.font(14));
                    msg.setPadding(new Insets(0, 0, 0, 10));
                    box.getChildren().addAll(name, msg);
                    this.setGraphic(box);
                } else {
                    setText(null);
                    setGraphic(null);
                }
            }
        });

        msgArea.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                sendMessage();
            }
        });
        sendBtn.setOnMouseClicked(mouseEvent -> sendMessage());
    }

    private void sendMessage() {
        String msg = msgArea.getText();
        if (!msg.isEmpty()) {
            GroupMessage message = new GroupMessage(me, group, msg);
            groupMessageDao.save(message);
            handler.sendGroupMessage(message);
            messageListView.getItems().add(message);
            messageListView.scrollTo(message);
            msgArea.setText("");
        }
    }

    public static GroupStage get(String group) {
        for (GroupStage groupStage : stageManager) {
            if (groupStage.group.equals(group)) {
                return groupStage;
            }
        }
        return null;
    }

    public void addMessage(GroupMessage message) {
        messageListView.getItems().add(message);
        messageListView.scrollTo(message);
        groupMessageDao.save(message);
    }
}
