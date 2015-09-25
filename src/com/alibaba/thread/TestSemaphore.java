package com.alibaba.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * 信号灯测试
 * 模拟20个人排队上厕所，总共只有5个茅坑
 * 
 * @author ahpuchunwei	
 */
public class TestSemaphore {
	public static void main(String[] args) {
		// 线程池
		ExecutorService exec = Executors.newCachedThreadPool();
		// 只能5个线程同时访问
		final Semaphore semp = new Semaphore(5);
		// 模拟20个客户端访问
		for (int index = 0; index < 20; index++) {
			final int NO = index;
			Runnable run = new Runnable() {
				public void run() {
					try {
						// 获取锁，好比你进去之前，把门上锁
						semp.acquire();
						System.out.println("编号" + NO + "使用中...当前还剩" + semp.availablePermits() + "个可占用，" + semp.getQueueLength() + "个排队等待中");
						System.out.println();
						Thread.sleep((long) (Math.random() * 10000));
						// 访问完后，释放锁，好比你上完厕所，开门走人
						semp.release();
						System.out.println("编号" + NO + "使用完毕...,当前还剩" + semp.availablePermits() + "个可占用，" + semp.getQueueLength() + "个排队等待中");
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};
			exec.execute(run);
		}
		// 线程池资源释放
		exec.shutdown();
	}
}
