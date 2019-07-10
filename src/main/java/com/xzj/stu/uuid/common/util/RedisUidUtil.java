package com.xzj.stu.uuid.common.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 基于redis的uid生成方案
 * 由于Redis自身的单线程的特点所以能保证生成的 ID 肯定是唯一有序的
 *
 * 单机存在性能瓶颈，无法满足高并发的业务需求，所以可以采用集群的方式来实现，
 * 集群的方式又会涉及到和数据库集群同样的问题，所以也需要设置分段和步长来实现。
 *
 * 为了避免长期自增后数字过大可以通过与当前时间戳组合起来使用，另外为了保证并发和业务多线程的问题可以采用 Redis + Lua的方式进行编码，保证安全
 *
 *
 * @author zhijunxie
 * @date 2019/7/8 18:52
 */
@Component
public class RedisUidUtil {
    private static final String UUID_KEY = "xzj-bduid-key-str";

    @Autowired
    private RedisUtil redisUtil;

    public String getUid() {
        long incr = redisUtil.incr(UUID_KEY, 1L);
        return String.format("%032d", incr);
    }
}
