package com.jxnu.app;

import java.util.concurrent.atomic.AtomicInteger;


/**
 * 仓库类
 */
public class Storage {
	// 仓库最大库存量
	private final int MAX_SIZE = 10;

	// 仓库当前库存量，默认初始库存为0
	private AtomicInteger currentSize = new AtomicInteger(0);

	/**
	 * 生产产品
	 * @throws InterruptedException 
	 */
	public synchronized void produce() throws InterruptedException {
		// 如果仓库剩余容量不足
		while (currentSize.get() >= MAX_SIZE) {
			System.out.println("产品已达到仓库最大库存量【" + MAX_SIZE + "】，开始停止生产\n");
			try {
				//生产阻塞
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// 生产产品
		currentSize.getAndIncrement();
		System.out.println("【生产者】开始生产，当前产品数量:" + currentSize.get());
		
		//唤醒消费者
		notifyAll();
		System.out.println("唤醒消费者，可以开始消费产品了\n");
		
		Thread.sleep(300);	
	}

	/**
	 * 消费产品
	 * @throws InterruptedException 
	 */
	public synchronized void consume() throws InterruptedException {
		// 如果仓库库存量不足
		while (currentSize.get() <= 0) {
			System.out.println("产品没有库存了，开始停止消费.\n");
			try {
				//消费阻塞
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		currentSize.getAndDecrement();
		System.out.println("【消费者】开始消费，当前产品数量:" + currentSize.get());
		
		//唤醒生产者
		notifyAll();
		System.out.println("唤醒生产者，可以开始生产产品了\n");
		
		Thread.sleep(800);
	}
}
