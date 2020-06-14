package client;

import common.dao.MessageDao;
import common.model.Message;
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

public class ChatStage extends Application {
    private static final List<ChatStage> stageManager = new LinkedList<>();
    private final String me;
    private final String friend;
    private VBox vBox;
    private MessageDao messageDao;
    ClientHandler handler = ClientHandler.getInstance();
    private ListView<Message> messageListView;
    private TextArea msgArea;

    public ChatStage(String me, String friend) {
        this.me = me;
        this.friend = friend;
    }

    @Override
    public void start(Stage stage) throws Exception {
        stageManager.add(this);
        layoutChildren();
        stage.setScene(new Scene(vBox, 600, 500));
        stage.setTitle(friend);
        stage.setOnCloseRequest(windowEvent -> stageManager.remove(this));
        stage.show();
    }

    private void layoutChildren() {
        vBox = new VBox();
        vBox.setPadding(new Insets(20.0));
        vBox.setSpacing(20.0);
        messageDao = App.getDB().getDao(MessageDao.class);
        messageListView = new ListView<>();
        messageListView.setFocusTraversable(false);
        messageListView.getItems().addAll(messageDao.getAll(me, friend));
        msgArea = new TextArea();
        Button sendBtn = new Button("发送(回车)");
        vBox.getChildren().addAll(messageListView, msgArea, sendBtn);
        messageListView.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Message message, boolean empty) {
                super.updateItem(message, empty);
                if (!empty) {
                    VBox box = new VBox();
                    Label name = new Label();
                    if (message.getFrom().equals(ChatStage.this.me)) {
                        box.setAlignment(Pos.CENTER_RIGHT);
                        name.setText("我说：");
                    } else {
                        name.setText(ChatStage.this.friend + "说：");
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
        sendBtn.setOnMouseClicked(mouseEvent -> {
            sendMessage();
        });
    }

    private void sendMessage() {
        String msg = msgArea.getText();
        if (!msg.isEmpty()) {
            Message message = new Message(me, friend, msg);
            messageDao.save(message);
            handler.sendMessage(message);
            messageListView.getItems().add(message);
            messageListView.scrollTo(message);
            msgArea.setText("");
        }
    }

    public static ChatStage get(String friend) {
        for (ChatStage chatStage : stageManager) {
            if (chatStage.friend.equals(friend)) {
                return chatStage;
            }
        }
        return null;
    }

    public void addMessage(Message message) {
        messageListView.getItems().add(message);
        messageListView.scrollTo(message);
        messageDao.save(message);
    }
}
