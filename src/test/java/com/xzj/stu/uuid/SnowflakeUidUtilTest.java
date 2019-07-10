package com.xzj.stu.uuid;

import com.xzj.stu.uuid.common.util.SnowflakeUidUtil;

/**
 * @author zhijunxie
 * @date 2019/7/9 17:15
 */
public class SnowflakeUidUtilTest {
    public static void main(String[] args) {
        SnowflakeUidUtil snowflakeUidUtil = new SnowflakeUidUtil(1,0);

        for (int i =0; i< 100; i++) {
            System.out.println(snowflakeUidUtil.nextId());
        }
    }
}
