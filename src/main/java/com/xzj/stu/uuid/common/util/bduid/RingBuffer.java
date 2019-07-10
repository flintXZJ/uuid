package com.xzj.stu.uuid.common.util.bduid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author zhijunxie
 * @date 2019/7/9 20:22
 */
public class RingBuffer {

    private static final Logger LOGGER = LoggerFactory.getLogger(RingBuffer.class);

    private final int bufferSize;

    private final long indexMask;

    /**
     * Threshold for trigger padding buffer
     */
    private final int paddingThreshold;

    /**
     * 存放预设置的uid
     */
    private final long[] slots;

    private static final long CAN_PUT_FLAG = 0L;
    private static final long CAN_TAKE_FLAG = 1L;
    /**
     * 存储Uid状态(是否可填充、是否可消费) 0:可填充 1:可消费
     */
    private final PaddedAtomicLong[] flags;

    private static final int START_POINT = -1;
    /** Tail: last position sequence to produce */
    private final AtomicLong tail = new PaddedAtomicLong(START_POINT);

    /** Cursor: current position sequence to consume */
    private final AtomicLong cursor = new PaddedAtomicLong(START_POINT);

    private BufferPaddingExecutor bufferPaddingExecutor;

    public RingBuffer(int bufferSize, int paddingFactor) {
        Assert.isTrue(bufferSize > 0L, "RingBuffer size must be positive");
        Assert.isTrue(Integer.bitCount(bufferSize) == 1, "RingBuffer size must be a power of 2");
        Assert.isTrue(paddingFactor > 0 && paddingFactor < 100, "RingBuffer size must be positive");

        this.bufferSize = bufferSize;
        this.indexMask = bufferSize - 1;
        this.paddingThreshold = bufferSize * paddingFactor / 100;
        this.slots = new long[bufferSize];
        this.flags = initFlags(bufferSize);
        LOGGER.info("bufferSize = {}, paddingThreshold = {}", bufferSize, paddingThreshold);
    }

    /**
     * 初始存储uid环形数组状态
     *
     * @param bufferSize
     * @return
     */
    private PaddedAtomicLong[] initFlags(int bufferSize) {
        PaddedAtomicLong[] flags = new PaddedAtomicLong[bufferSize];
        for (int i = 0; i < bufferSize; i++) {
            flags[i] = new PaddedAtomicLong(CAN_PUT_FLAG);
        }
        return flags;
    }

    public synchronized boolean put(long uid) {
        long currentTail = tail.get();
        long currentCursor = cursor.get();

        long distance = currentTail - (currentCursor == START_POINT ? 0 : currentCursor);
        if (distance == bufferSize - 1) {
            return false;
        }

        // 1. pre-check whether the flag is CAN_PUT_FLAG
        int nextTailIndex = calSlotIndex(currentTail + 1);
        if (flags[nextTailIndex].get() != CAN_PUT_FLAG) {
            return false;
        }

        // 2. put UID in the next slot
        // 3. update next slot' flag to CAN_TAKE_FLAG
        // 4. publish tail with sequence increase by one
        slots[nextTailIndex] = uid;
        slots[nextTailIndex] = uid;
        flags[nextTailIndex].set(CAN_TAKE_FLAG);
        tail.incrementAndGet();

        return true;
    }

    public long take() throws Exception {
        // spin get next available cursor
        long currentCursor = cursor.get();
        long nextCursor = cursor.updateAndGet(old -> old == tail.get() ? old : old + 1);

        // check for safety consideration, it never occurs
        Assert.isTrue(nextCursor >= currentCursor, "Curosr can't move back");

        // trigger padding in an async-mode if reach the threshold
        long currentTail = tail.get();
        if (currentTail - nextCursor < paddingThreshold) {
            LOGGER.info("trigger padding in an async-mode if reach the threshold");
            bufferPaddingExecutor.asyncPadding();
        }

        // cursor catch the tail, means that there is no more available UID to take
        if (nextCursor == currentCursor) {
            throw new Exception("无可用uuid");
        }

        // 1. check next slot flag is CAN_TAKE_FLAG
        int nextCursorIndex = calSlotIndex(nextCursor);
        Assert.isTrue(flags[nextCursorIndex].get() == CAN_TAKE_FLAG, "Curosr not in can take status");

        // 2. get UID from next slot
        // 3. set next slot flag as CAN_PUT_FLAG.
        long uid = slots[nextCursorIndex];
        flags[nextCursorIndex].set(CAN_PUT_FLAG);

        return uid;
    }

    protected int calSlotIndex(long sequence) {
        return (int) (sequence & indexMask);
    }

    public void setBufferPaddingExecutor(BufferPaddingExecutor bufferPaddingExecutor) {
        this.bufferPaddingExecutor = bufferPaddingExecutor;
    }
}
