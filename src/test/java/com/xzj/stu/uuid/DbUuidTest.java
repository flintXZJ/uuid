package com.xzj.stu.uuid;

import com.xzj.stu.uuid.common.util.DbUidUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author zhijunxie
 * @date 2019/7/9 15:13
 */
public class DbUuidTest extends ApplicationTests {

    @Autowired
    private DbUidUtil dbUidUtil;

    @Test
    public void test() {
        logger.info("bduid = {}", dbUidUtil.getUid());
    }
}
