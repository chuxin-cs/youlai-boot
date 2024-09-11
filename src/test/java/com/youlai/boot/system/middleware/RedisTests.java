package com.youlai.boot.system.middleware;

import com.youlai.boot.system.model.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * Redis 单元测试
 *
 * @author haoxr
 * @since 2023/02/17
 */
@SpringBootTest
@Slf4j
public class RedisTests {

    @Autowired
    private  RedisTemplate redisTemplate;

    /**
     * Redis 序列化测试
     */
    @Test
    public void testRedisSerializer() {
        User user = new User();
        user.setId(1l);
        user.setNickname("张三");
        // 写
        redisTemplate.opsForValue().set("user", user);

        // 读
        User userCache = (User)redisTemplate.opsForValue().get("user");
        log.info("userCache:{}", userCache);

    }

}
