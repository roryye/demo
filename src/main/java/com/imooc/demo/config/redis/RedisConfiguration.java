package com.imooc.demo.config.redis;

import com.imooc.demo.cache.JedisPoolWriper;
import com.imooc.demo.cache.JedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class RedisConfiguration {
    @Value("${redis.hostname}")
    private String hostname;
    @Value("${redis.port}")
    private int port;
    @Value("${redis.pool.maxActive}")
    private int maxTotal;
    @Value("${redis.pool.maxIdle}")
    private int maxIdle;
    @Value("${redis.pool.maxWait}")
    private long maxWaitMillis;
    @Value("${redis.pool.testOnBorrow}")
    private boolean testOnBorrow;

    @Autowired
    private JedisPoolConfig jedisPoolConfig;
    @Autowired
    private JedisPoolWriper jedisWritePool;
    @Autowired
    private JedisUtil jedisUtil;

    /**
     * 创建redis连接池的设置
     * @return
     */
    @Bean(name = "jedisPoolConfig")
    public JedisPoolConfig createJedisPoolConfig(){
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        //控制一个pool可分配多少个jedis实例
        jedisPoolConfig.setMaxTotal(maxTotal);
        //连接池中最多可空闲maxIdle个连接，这里取值为20,表示即使没有数据库连接时依然可以保持20空闲的连接
        jedisPoolConfig.setMaxIdle(maxIdle);
        //最大等待时间：当没有可用的连接时，连接池等待被归还的最大时间（毫秒），超出时间抛出异常
        jedisPoolConfig.setMaxWaitMillis(maxWaitMillis);
        //在获取连接时检查有效性
        jedisPoolConfig.setTestOnBorrow(testOnBorrow);
        return jedisPoolConfig;
    }

    /**
     * 创建Redis连接池，并做相关配置
     * @return
     */
    @Bean(name = "jedisWritePool")
    public JedisPoolWriper createJedisPoolWriper(){
        JedisPoolWriper jedisPoolWriper = new JedisPoolWriper(jedisPoolConfig, hostname, port);
        return jedisPoolWriper;
    }

    /**
     * 创建Redis工具类，封装好Redis的连接池以进行相关的操作
     */
    @Bean(name = "jedisUtil")
    public JedisUtil createJedisUtil(){
        JedisUtil jedisUtil = new JedisUtil();
        jedisUtil.setJedisPool(jedisWritePool);
        return jedisUtil;
    }

    /**
     * redis的key操作
     * @return
     */
    @Bean(name = "jedisKeys")
    public JedisUtil.Keys createJedisKeys(){
        JedisUtil.Keys jedisKeys = jedisUtil.new Keys();
        return jedisKeys;
    }

    @Bean(name = "jedisStrings")
    public JedisUtil.Strings createJedisStrings(){
        JedisUtil.Strings jedisStrings = jedisUtil.new Strings();
        return jedisStrings;
    }

}
