package com.xzj.stu.uuid;

import com.xzj.stu.uuid.common.util.RedisUidUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author zhijunxie
 * @date 2019/7/9 14:46
 */
public class RedisTest extends ApplicationTests {

    @Autowired
    private RedisUidUtil redisUidUtil;

    @Test
    public void test() {
        logger.info("bduid.value = {}", redisUidUtil.getUid());
    }
}
