module chatroom.client {
    requires io.netty.all;
    requires chatroom.common;
    requires javafx.controls;
    requires javafx.fxml;
    requires org.mybatis;

    opens client.controller to javafx.fxml;
    exports client;
}