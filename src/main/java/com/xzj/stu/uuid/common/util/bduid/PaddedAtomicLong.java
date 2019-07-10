package com.xzj.stu.uuid.common.util.bduid;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 解决cpu缓存行伪共享问题
 *
 * Represents a padded {@link AtomicLong} to prevent the FalseSharing problem<p>
 *
 * The CPU cache line commonly be 64 bytes, here is a sample of cache line after padding:<br>
 * 64 bytes = 8 bytes (object reference) + 6 * 8 bytes (padded long) + 8 bytes (a long value)
 *
 * @author zhijunxie
 * @date 2019/7/9 20:34
 */
public class PaddedAtomicLong extends AtomicLong {
    /** Padded 6 long (48 bytes) */
    public volatile long p1, p2, p3, p4, p5, p6 = 7L;

    /**
     * Constructors from {@link AtomicLong}
     */
    public PaddedAtomicLong() {
        super();
    }

    public PaddedAtomicLong(long initialValue) {
        super(initialValue);
    }

    /**
     * To prevent GC optimizations for cleaning unused padded references
     */
    public long sumPaddingToPreventOptimization() {
        return p1 + p2 + p3 + p4 + p5 + p6;
    }
}
