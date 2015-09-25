package com.alibaba.thread;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * CyclicBarrier测试 
 * 模拟20个人相约于公司门口集合，等所有人到齐后去动物园玩
 * CyclicBarrier适用于主线程需要等待所有子线程全部执行完毕再执行的情况
 * @author ahpuchunwei
 */
public class CyclicBarrierTest {
	/**总人数*/
	private static final int total = 20;
	
	public static void main(String[] args) {
		/**固定容量的线程池*/
		ExecutorService pool = Executors.newFixedThreadPool(total);
		final CyclicBarrier cyclicBarrier = new CyclicBarrier(total);
		for (int i = 0; i < total; i++) {
			final int no = i + 1;
			Runnable runnable = new Runnable() {
				public void run() {
					try {
						Thread.sleep((long) (Math.random() * 10000));
						System.out.println("编号" + no + "到达集合地点，当前已有" + (cyclicBarrier.getNumberWaiting() + 1) + "人到达，" + (cyclicBarrier.getNumberWaiting() == (total - 1) ? "都到齐了，出发" : "人还没到齐，等候......"));
						try {
							cyclicBarrier.await();
						} catch (BrokenBarrierException e) {
							e.printStackTrace();
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};
			pool.execute(runnable);
		}
		pool.shutdown();
	}
}
