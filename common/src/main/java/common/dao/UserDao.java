package common.dao;

import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserDao {
    @Update("create table if not exists user(user1 varchar(100),user2 varchar(100))")
    void createTable();

    @Select("select user2 from user where user1=#{param1}")
    List<String> getFriends(String username);

    @Insert("insert into user(user1, user2) values (#{param1}, #{param2})")
    void save(String user1, String user2);

    @Delete("delete from user where user1 = #{param1} and user2=#{param2}")
    void delete(String user1, String user2);
}
