package com.lee.pay.utils.orderUtil;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.*;

public class ThreadPoolUtils {
 
    private final ExecutorService executor;
 
    private static final ThreadPoolUtils instance = new ThreadPoolUtils();
 
    private ThreadPoolUtils() {
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("order-runner-%d").build();
        int size = Runtime.getRuntime().availableProcessors() * 2;
        this.executor = new ThreadPoolExecutor(size,size,0L,TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>(),namedThreadFactory);
    }
 
    public static ThreadPoolUtils getInstance() {
        return instance;
    }
 
    public static <T> Future<T> execute(final Callable<T> runnable) {
        return getInstance().executor.submit(runnable);
    }
 
    public static Future<?> execute(final Runnable runnable) {
        return getInstance().executor.submit(runnable);
    }
 
 
}
 