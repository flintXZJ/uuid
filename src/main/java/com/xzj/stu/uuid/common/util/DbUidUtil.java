package com.xzj.stu.uuid.common.util;

import com.xzj.stu.uuid.pojo.UuidPO;
import com.xzj.stu.uuid.service.UuidService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 基于数据库的uid生成方案
 *
 * 分布式数据库:
 * 将分布式系统中数据库的同一个业务表的自增ID设计成不一样的起始值，然后设置固定的步长，步长的值即为分库的数量或分表的数量
 *  auto_increment_offset：表示自增长字段从那个数开始，他的取值范围是1 .. 65535
 *  auto_increment_increment：表示自增长字段每次递增的量，其默认值是1，取值范围是1 .. 65535
 *
 * 优点：
 *  是依赖于数据库自身不需要其他资源，并且ID号单调自增，可以实现一些对ID有特殊要求的业务
 *
 * 缺点：
 *  强依赖DB，当DB异常时整个系统不可用
 *  配置主从复制可以尽可能的增加可用性，但是数据一致性在特殊情况下难以保证
 *  主从切换时的不一致可能会导致重复发号
 *  ID发号性能瓶颈限制在单台MySQL的读写性能
 *  无法水平扩展
 *
 * @author zhijunxie
 * @date 2019/7/9 15:11
 */
@Component
public class DbUidUtil {

    @Autowired
    private UuidService uuidService;

    public String getUid() {
        UuidPO uuidPO = new UuidPO();
        uuidPO.setNoUsedCloumn(1);
        Long uid = uuidService.insert(uuidPO);
        return String.format("%032d", uid);
    }
}
