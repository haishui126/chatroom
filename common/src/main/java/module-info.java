module chatroom.common {
    requires org.mybatis;
    requires java.sql;

    exports common.dao;
    exports common.model;
    exports common;
}