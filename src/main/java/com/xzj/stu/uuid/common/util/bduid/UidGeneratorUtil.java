package com.xzj.stu.uuid.common.util.bduid;

import java.util.ArrayList;
import java.util.List;

/**
 * 百度
 * https://juejin.im/post/5d22972f6fb9a07edd2a34cc
 * https://github.com/baidu/uid-generator/blob/master/README.zh_cn.md
 * <p>
 * DefaultUidGenerator 实现方式和雪花算法相似，只是64位结构分配不同
 * <p>
 * CachedUidGenerator
 *
 * 实现逻辑：
 *  1、创建环形数组，提前在数组中存储uid
 *  2、Tail为创建的uid最大序号，Cursor为消费过的uid序号
 *  3、Tail不能超过Cursor，即生产者不能覆盖未消费的slot；Cursor不能超过Tail，即不能消费未生产的slot
 *  4、当消费超过阈值时，往环形数组中插入新uid
 *  5、也可定时向环形数组插入数据
 *
 *  该实现为简略版，具体参照上文github连接百度
 *
 * @author zhijunxie
 * @date 2019/7/8 18:53
 */
public class UidGeneratorUtil {
    // ==============================Fields===========================================
    /**
     * 开始时间截 (2019-07-01)
     */
    private final long twepoch = 1561910400000L;

    /**
     * 机器id所占的位数
     */
    private final long workerIdBits = 5L;

    /**
     * 数据中心id所占的位数
     */
    private final long datacenterIdBits = 5L;

    /**
     * 支持的最大机器id，结果是31 (这个移位算法可以很快的计算出几位二进制数所能表示的最大十进制数)
     */
    private final long maxWorkerId = -1L ^ (-1L << workerIdBits);

    /**
     * 支持的最大数据中心id，结果是31
     */
    private final long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);

    /**
     * 序列在id中占的位数
     */
    private final long sequenceBits = 12L;

    /**
     * 机器ID向左移12位
     */
    private final long workerIdShift = sequenceBits;

    /**
     * 数据中心id向左移17位(12+5)
     */
    private final long datacenterIdShift = sequenceBits + workerIdBits;

    /**
     * 时间截向左移22位(5+5+12)
     */
    private final long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;

    /**
     * 生成序列的掩码，这里为4095 (0b111111111111=0xfff=4095)
     */
    private final long sequenceMask = -1L ^ (-1L << sequenceBits);

    /**
     * 工作机器ID(0~31)
     */
    private long workerId;

    /**
     * 数据中心ID(0~31)
     */
    private long datacenterId;

    /**
     * 毫秒内序列(0~4095)
     */
    private long sequence = 0L;

    private final long maxSequence = sequenceMask;

    /**
     * ringbuffer size = (maxSequence+1) >> boostPower
     */
    private int boostPower = 1;

    private RingBuffer ringBuffer;
    public RingBuffer getRingBuffer() {
        return ringBuffer;
    }
    private static final int paddingFactor = 50;

    //==============================Constructors=====================================

    /**
     * 构造函数
     *
     * @param workerId     工作ID (0~31)
     * @param datacenterId 数据中心ID (0~31)
     */
    public UidGeneratorUtil(long workerId, long datacenterId) {
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
        }
        if (datacenterId > maxDatacenterId || datacenterId < 0) {
            throw new IllegalArgumentException(String.format("datacenter Id can't be greater than %d or less than 0", maxDatacenterId));
        }
        this.workerId = workerId;
        this.datacenterId = datacenterId;

        this.initRingBuffer();
    }

    // ==============================Methods==========================================

    private void initRingBuffer() {
        int bufferSize = ((int) maxSequence + 1) << boostPower;
        this.ringBuffer = new RingBuffer(bufferSize, paddingFactor);

        BufferPaddingExecutor bufferPaddingExecutor = new BufferPaddingExecutor(ringBuffer, this::nextIdsForOneSecond);
        ringBuffer.setBufferPaddingExecutor(bufferPaddingExecutor);
        bufferPaddingExecutor.paddingBuffer();
    }


    /**
     * 获取下一秒uuid
     *
     * @param currentTimestamp
     * @return
     */
    private List<Long> nextIdsForOneSecond(long currentTimestamp) {
        // Initialize result list size of (max sequence + 1)
        int listSize = (int) maxSequence + 1;
        List<Long> uidList = new ArrayList<>(listSize);

        // Allocate the first sequence of the second, the others can be calculated with the offset
        long firstSeqUid = getCurrentFirstSeqUid(currentTimestamp*1000);
        for (int offset = 0; offset < listSize; offset++) {
            uidList.add(firstSeqUid + offset);
        }

        return uidList;
    }

    /**
     * 获得下一毫秒初始ID (该方法是线程安全的)
     *
     * @return SnowflakeId
     */
    private long getCurrentFirstSeqUid(long currentTimestamp) {
        sequence = 0L;
        //移位并通过或运算拼到一起组成64位的ID
        return ((currentTimestamp - twepoch) << timestampLeftShift)
                | (datacenterId << datacenterIdShift)
                | (workerId << workerIdShift)
                | sequence;
    }

    public long getUID() {
        try {
            return ringBuffer.take();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

}
