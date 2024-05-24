package com.pdiff.core;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NumberedThreadFactory implements ThreadFactory {
	private final AtomicInteger threadNumber = new AtomicInteger(0);

	@Override
	public Thread newThread(Runnable r) {
		Thread thread = new Thread(r);
		thread.setName("Worker-" + threadNumber.getAndIncrement());
		return thread;
	}
}