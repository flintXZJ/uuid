package com.xzj.stu.uuid;

import com.xzj.stu.uuid.common.util.bduid.UidGeneratorUtil;

/**
 * @author zhijunxie
 * @date 2019/7/9 21:05
 */
public class UidTest {

    public static void main(String[] args) {
        UidGeneratorUtil uidGeneratorUtil = new UidGeneratorUtil(0, 0);
        for (int i = 0; i < 10000; i++) {
            uidGeneratorUtil.getUID();
        }

    }
}
