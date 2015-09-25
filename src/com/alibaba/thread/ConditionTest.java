package com.alibaba.thread;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Condition线程通知测试 
 * 子线程1循环10次，再子线程2循环10次，接着主线程循环100次，如此重复50次
 * Condition作用类似于Thread的wait和notify,区别在于Condition不仅仅能唤醒和阻塞自身线程，还能唤醒和阻塞其他线程
 * @author ahpuchunwei
 */
public class ConditionTest {
	/** 0表示主线程，1表示子线程1，2表示子线程2 */
	private static int state = 1;
	private static Lock lock = new ReentrantLock();

	private static Condition condition0 = lock.newCondition();
	private static Condition condition1 = lock.newCondition();
	private static Condition condition2 = lock.newCondition();

	public static void main(String[] args) {
		for (int i = 0; i < 50; i++) {
			// 子线程1
			new Thread(new Runnable() {
				@Override
				public void run() {
					lock.lock();
					try {
						while (state != 1) {
							condition1.await();
						}
						for (int t = 0; t < 10; t++) {
							System.out.println("子线程1循环第" + (t + 1) + "次");
						}
					} catch (Exception e) {
						e.printStackTrace();
					} finally {			
						// 唤醒子线程2
						state = 2;
						condition2.signal();
						lock.unlock();
					}
				}
			}).start();

			// 子线程2
			new Thread(new Runnable() {
				@Override
				public void run() {
					lock.lock();
					try {
						while (state != 2) {
							condition2.await();
						}
						for (int t = 0; t < 10; t++) {
							System.out.println("子线程2循环第" + (t + 1) + "次");
						}
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						// 唤醒主线程
						state = 0;
						condition0.signal();
						lock.unlock();
					}
				}
			}).start();

			// 主线程
			new Thread(new Runnable() {
				@Override
				public void run() {
					lock.lock();
					try {
						while (state != 0) {
							condition0.await();
						}
						for (int t = 0; t < 100; t++) {
							System.out.println("主线程循环第" + (t + 1) + "次");
						}
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						// 唤醒子线程1
						state = 1;
						condition1.signal();
						lock.unlock();
					}
				}
			}).start();
		}
	}
}
