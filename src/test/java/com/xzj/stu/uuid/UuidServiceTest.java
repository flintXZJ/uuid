package com.xzj.stu.uuid;

import com.xzj.stu.uuid.pojo.UuidPO;
import com.xzj.stu.uuid.service.UuidService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author zhijunxie
 * @date 2019/7/9 10:42
 */
public class UuidServiceTest extends ApplicationTests {

    @Autowired
    private UuidService uuidService;


    @Test
    public void test() {
        UuidPO uuidPO = new UuidPO();
        uuidPO.setNoUsedCloumn(1);
        logger.info("bduid = {}", uuidService.insert(uuidPO));
    }
}
