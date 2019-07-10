package com.xzj.stu.uuid.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * 优点：
 * UUID 生成方便，本地生成没有网络消耗
 *
 * 缺点：
 * 1、不易于存储：UUID太长，16字节128位，通常以36长度的字符串表示，很多场景不适用
 * 2、信息不安全：基于MAC地址生成UUID的算法可能会造成MAC地址泄露，暴露使用者的位置
 * 3、对MySQL索引不利：如果作为数据库主键，在InnoDB引擎下，UUID的无序性可能会引起数据位置频繁变动，严重影响性能，可以查阅 Mysql 索引原理 B+树的知识
 *
 * @author zhijunxie
 * @date 2019/7/8 17:54
 */
public class UidUtil {
    private static Logger logger = LoggerFactory.getLogger(UidUtil.class);

    /**
     * 版本1
     *
     * 通过当前时间，随机数，和本地Mac地址来计算出来，
     * 可以通过 org.apache.logging.log4j.core.util包中的 UidUtil.getTimeBasedUuid()来使用或者其他包中工具。
     * 由于使用了MAC地址，因此能够确保唯一性，但是同时也暴露了MAC地址，私密性不够好
     *
     * @return
     */
    public static String getTimeBasedUuid() {
        UUID timeBasedUuid = org.apache.logging.log4j.core.util.UuidUtil.getTimeBasedUuid();
        return timeBasedUuid.toString().replace("-", "");
    }

    /**
     * 版本2
     *
     * DCE（Distributed Computing Environment）安全的UUID和基于时间的UUID算法相同，但会把时间戳的前4位置换为POSIX的UID或GID。这个版本的UUID在实际中较少用到
     *
     * @return
     */
    public static String getDCEBasedUuid() {
        // TODO: 2019/7/8 待实现
        return null;
    }

    /**
     * 版本4
     *
     * 根据随机数，或者伪随机数生成UUID。
     * 这种UUID产生重复的概率是可以计算出来的，
     * 但是重复的可能性可以忽略不计，因此该版本也是被经常使用的版本。
     * JDK中使用的就是这个版本
     *
     * @return
     */
    public static String getRandomUuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 版本3
     *
     * 基于名字的UUID通过计算名字和名字空间的MD5散列值得到。
     * 这个版本的UUID保证了：相同名字空间中不同名字生成的UUID的唯一性；
     * 不同名字空间中的UUID的唯一性；
     * 相同名字空间中相同名字的UUID重复生成是相同的
     *
     * @param nameBytes
     * @return
     */
    public static String getNameUuidFromBytes(byte[] nameBytes) {
        return UUID.nameUUIDFromBytes(nameBytes).toString().replace("-", "");
    }

    /**
     * 版本5
     *
     * 和基于名字的UUID算法类似，只是散列值计算使用SHA1（Secure Hash Algorithm 1）算法
     *
     * @param nameBytes
     * @return
     */
    public static String getNameUuidWithSHA1(byte[] nameBytes) {
        return UuidWithSHA1.nameUUIDFromBytes(nameBytes).toString().replace("-", "");
    }

//    public static void main(String[] args) {
//        logger.info("md5 bduid = {}", UidUtil.getNameUuidFromBytes(new byte[]{10,20,30}));
//        logger.info("sha1 bduid = {}", UidUtil.getNameUuidWithSHA1(new byte[]{10,20,30}));
//    }

}
