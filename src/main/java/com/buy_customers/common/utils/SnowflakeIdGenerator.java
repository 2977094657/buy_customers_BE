package com.buy_customers.common.utils;

public class SnowflakeIdGenerator {
    // 开始时间戳，一般取当前时间的起始时间戳
    private final static long START_TIMESTAMP = 1621864800000L;

    // 机器ID所占的位数
    private final static long WORKER_ID_BITS = 5L;

    // 数据中心ID所占的位数
    private final static long DATA_CENTER_ID_BITS = 5L;

    // 序列号所占的位数
    private final static long SEQUENCE_BITS = 12L;

    // 最大机器ID
    private final static long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);

    // 最大数据中心ID
    private final static long MAX_DATA_CENTER_ID = ~(-1L << DATA_CENTER_ID_BITS);

    // 机器ID向左移的位数
    private final static long WORKER_ID_SHIFT = SEQUENCE_BITS;

    // 数据中心ID向左移的位数
    private final static long DATA_CENTER_ID_SHIFT = WORKER_ID_BITS + SEQUENCE_BITS;

    // 时间戳向左移的位数
    private final static long TIMESTAMP_SHIFT = DATA_CENTER_ID_BITS + WORKER_ID_BITS + SEQUENCE_BITS;

    // 序列号的最大值
    private final static long MAX_SEQUENCE = ~(-1L << SEQUENCE_BITS);

    // 当前序列号
    private long sequence = 0L;

    // 上次生成ID的时间戳
    private long lastTimestamp = -1L;

    // 机器ID
    private final long workerId;

    // 数据中心ID
    private final long dataCenterId;

    /**
     * 构造函数
     *
     * @param workerId     机器ID
     * @param dataCenterId 数据中心ID
     */
    public SnowflakeIdGenerator(long workerId, long dataCenterId) {
        if (workerId > MAX_WORKER_ID || workerId < 0) {
            throw new IllegalArgumentException("workerId can't be greater than " + MAX_WORKER_ID + " or less than 0");
        }

        if (dataCenterId > MAX_DATA_CENTER_ID || dataCenterId < 0) {
            throw new IllegalArgumentException("dataCenterId can't be greater than " + MAX_DATA_CENTER_ID + " or less than 0");
        }

        this.workerId = workerId;
        this.dataCenterId = dataCenterId;
    }

    /**
     * 生成下一个全局唯一ID
     *
     * @return 全局唯一ID
     */
    public synchronized long nextId() {
        long timestamp = timeGen();

        if (timestamp < lastTimestamp) {
            throw new RuntimeException("Clock moved backwards. Refusing to generate id");
        }

        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;

            if        (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        return ((timestamp - START_TIMESTAMP) << TIMESTAMP_SHIFT) |
                (dataCenterId << DATA_CENTER_ID_SHIFT) |
                (workerId << WORKER_ID_SHIFT) |
                sequence;
    }

    /**
     * 获取下一个时间戳，直到大于当前时间戳
     *
     * @param lastTimestamp 上次生成ID的时间戳
     * @return 下一个时间戳
     */
    private long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();

        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }

        return timestamp;
    }

    /**
     * 获取当前时间戳
     *
     * @return 当前时间戳
     */
    private long timeGen() {
        return System.currentTimeMillis();
    }
}
