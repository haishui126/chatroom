package common.dao;

import common.model.GroupMessage;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface GroupMessageDao {
    @Update("create table if not exists group_message(`from` varchar(50),`group` varchar(50),msg varchar(255))")
    void createTable();

    @Insert("insert into group_message(`from`, `group`, msg) values (#{from}, #{group}, #{msg})")
    void save(GroupMessage message);

    @Select("select * from group_message where (`from`=#{param1} and `group`=#{param2})")
    List<GroupMessage> getAll(String me, String friend);
}
