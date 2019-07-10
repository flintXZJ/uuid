package com.xzj.stu.uuid.controller;

import com.xzj.stu.uuid.pojo.UuidPO;
import com.xzj.stu.uuid.service.UuidService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhijunxie
 * @date 2019/7/8 17:42
 */
@RestController
@RequestMapping("/uuid")
public class UuidController {
    private static Logger logger = LoggerFactory.getLogger(UuidController.class);

    @Autowired
    private UuidService uuidService;

    @GetMapping("/1")
    public String getUuid() {
        logger.info("get bduid by db");
        UuidPO uuidPO = new UuidPO();
        uuidPO.setNoUsedCloumn(1);
        return String.format("%032d", uuidService.insert(uuidPO));
    }
}
