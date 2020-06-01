package client.dao;

import common.Message;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface MessageDao {
    @Update("create table IF NOT EXISTS message(`from` varchar(50),`to` varchar(50),msg varchar(255))")
    void createTable();

    @Insert("insert into message(`from`, `to`, msg) values (#{from}, #{to}, #{msg})")
    void save(Message message);

    @Select("select * from message where `to`=#{friend} or `from`=#{friend} limit 5")
    List<Message> getAll();
}
