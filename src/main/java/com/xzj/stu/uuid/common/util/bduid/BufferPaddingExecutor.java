package com.xzj.stu.uuid.common.util.bduid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author zhijunxie
 * @date 2019/7/9 20:54
 */
public class BufferPaddingExecutor {
    private static final Logger LOGGER = LoggerFactory.getLogger(BufferPaddingExecutor.class);

    private final PaddedAtomicLong lastSecond;
    private final RingBuffer ringBuffer;
    private final BufferedUidProvider uidProvider;

    /** Padding immediately by the thread pool */
    private final ExecutorService bufferPadExecutors;

    /** Whether buffer padding is running */
    private final AtomicBoolean running;

    public BufferPaddingExecutor(RingBuffer ringBuffer, BufferedUidProvider uidProvider) {
        int cores = Runtime.getRuntime().availableProcessors();

        bufferPadExecutors  = new ThreadPoolExecutor(10, 10,
                60L, TimeUnit.SECONDS, new ArrayBlockingQueue(10), new ThreadPoolExecutor.DiscardPolicy());

        this.running = new AtomicBoolean(false);
        lastSecond = new PaddedAtomicLong(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
        this.ringBuffer = ringBuffer;
        this.uidProvider = uidProvider;
    }

    /**
     * Padding buffer in the thread pool
     */
    public void asyncPadding() {
        bufferPadExecutors.submit(this::paddingBuffer);
    }


    public void paddingBuffer() {
        LOGGER.info("Ready to padding buffer lastSecond:{}. {}", lastSecond.get(), ringBuffer);

        // is still running
        if (!running.compareAndSet(false, true)) {
            LOGGER.info("Padding buffer is still running. {}", ringBuffer);
            return;
        }

        // fill the rest slots until to catch the cursor
        boolean isFullRingBuffer = false;
        while (!isFullRingBuffer) {
            List<Long> uidList = uidProvider.provide(lastSecond.incrementAndGet());
            for (Long uid : uidList) {
                isFullRingBuffer = !ringBuffer.put(uid);
                if (isFullRingBuffer) {
                    LOGGER.info("slots is full");
                    break;
                }
            }
        }

        // not running now
        running.compareAndSet(true, false);
    }
}
