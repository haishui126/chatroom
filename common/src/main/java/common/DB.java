package common;

import common.dao.GroupMessageDao;
import common.dao.MessageDao;
import common.model.GroupMessage;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

public class DB {
    private final SqlSession session;

    public DB(String name) {
        PooledDataSource dataSource = new PooledDataSource("org.h2.Driver", "jdbc:h2:./db/" + name, "root", "root");
        Environment environment = new Environment("1", new JdbcTransactionFactory(), dataSource);
        Configuration configuration = new Configuration(environment);
        configuration.addMapper(MessageDao.class);
        configuration.addMapper(GroupMessageDao.class);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
        session = sqlSessionFactory.openSession(true);
        session.getMapper(MessageDao.class).createTable();
        session.getMapper(GroupMessageDao.class).createTable();
    }

    public <T> T getDao(Class<T> tClass) {
        return session.getMapper(tClass);
    }
}
