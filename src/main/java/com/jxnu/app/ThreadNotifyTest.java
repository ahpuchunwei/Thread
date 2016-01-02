package com.jxnu.app;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 线程通知测试
 * @author  puchunwei
 */
public class ThreadNotifyTest {
	//线程池
	private static ExecutorService pool = Executors.newCachedThreadPool();
	
	private static Lock lock = new ReentrantLock();

	private static Condition condition1 = lock.newCondition();
	private static Condition condition2 = lock.newCondition();
	private static Condition condition3 = lock.newCondition();
	
	private static String[] words = {"A","B","C"};
	
	private static int state = 1;
	
	/**循环次数*/
	private static final int loopCount = 10;
	
	public static void main(String[] args) {
		for(int i=0; i < loopCount; i++) {
			Runnable runnable1 = new Runnable() {
				@Override
				public void run() {
					lock.lock();
					try {
						while (state != 1) {
							condition1.await();
						}
						System.out.print(words[0]);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						// 唤醒下一个线程
						state = 2;
						condition2.signal();
						lock.unlock();
					}
				}
			};
			Runnable runnable2 = new Runnable() {
				@Override
				public void run() {
					lock.lock();
					try {
						while (state != 2) {
							condition2.await();
						}
						System.out.print(words[1]);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						// 唤醒下一个线程
						state = 3;
						condition3.signal();
						lock.unlock();
					}
				}
			};
			Runnable runnable3 = new Runnable() {
				@Override
				public void run() {
					lock.lock();
					try {
						while (state != 3) {
							condition3.await();
						}
						System.out.print(words[2]);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						// 唤醒下一个线程
						state = 1;
						condition1.signal();
						lock.unlock();
					}
				}
			};
			pool.execute(runnable1);
			pool.execute(runnable2);
			pool.execute(runnable3);
		}
		//释放线程池资源
		pool.shutdown();
	}
}
