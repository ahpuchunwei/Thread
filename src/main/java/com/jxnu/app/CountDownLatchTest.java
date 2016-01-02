package com.jxnu.app;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * CountDownLatch测试
 * 跟CyclicBarrier作用类似，主要适用于一个线程执行完后通知多个线程，比如起跑发令枪控制多个运动员，发令枪没响，运动员都在等着，
 * 颁奖需要等多个运动员都到达后才能开始即多个线程执行完通知一个线程的情况
 * 模拟百米赛跑
 * @author  ahpuchunwei
 */
public class CountDownLatchTest {
	private static final int total = 5;
	public static void main(String[] args) throws Exception {
		/**创建固定容量的线程池*/
		ExecutorService pool = Executors.newCachedThreadPool();
		/**发令枪*/
		final CountDownLatch countDownLatch1 = new CountDownLatch(1);
		/**所有运动员比赛完毕，才能开始统计比赛结果，颁奖*/
		final CountDownLatch countDownLatch2 = new CountDownLatch(total);
		
		for(int i = 0; i < total; i++) {
			final int no = i + 1;
			//每个运动员即为一个子线程
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					try {
						Thread.sleep((long) (Math.random() * 500));
						countDownLatch1.await();
						System.out.println(no + "号运动员起跑");
						Thread.sleep((long) (Math.random() * 10000));
					} catch (InterruptedException e) {
						e.printStackTrace();
					} finally {
						countDownLatch2.countDown();
						System.out.println(no + "号运动员到达终点...");
					}
				}
			};
			//子线程交给线程池管理
			pool.execute(runnable);
		}
		//比赛正式开始
		countDownLatch1.countDown();
		System.out.println("预备，GO!!!");
		//等所有运动员比赛都完成后
		countDownLatch2.await();
		Thread.sleep((long)(Math.random()*10000));
		//比赛结束
		System.out.println("所有运动员比赛完毕，开始统计比赛结果，颁奖");
		//释放线程池资源
		pool.shutdown();
	}
}