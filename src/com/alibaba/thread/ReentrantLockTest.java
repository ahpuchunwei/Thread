package com.alibaba.thread;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 互斥锁测试
 * 即A线程执行时其他线程就给我等着，等我执行完再轮到你
 * 模拟举重比赛运动员按抽签顺序轮流上场
 * @author  ahpuchunwei
 */
public class ReentrantLockTest {
	/**5名选手*/
	private static final int total = 5;
	/**5个选手的编号*/
	private static String[] players = {"1","2","3","4","5"};
	/**线程池*/
	private static ExecutorService pool = Executors.newFixedThreadPool(players.length);
	/**计数器*/
	private static final CountDownLatch countDownLatch = new CountDownLatch(total);
	/**互斥锁*/
	private static Lock lock = new ReentrantLock();
	
	/**抓举成绩*/
	private static int[] zhuajus = new int[5];
	/**挺举成绩*/
	private static int[] tingjus = new int[5];
	
	/**抓举最高成绩*/
	private static int zhuajuMax = 0;
	/**挺举最高成绩*/
	private static int tingjuMax = 0;
	/**总成绩最高值*/
	private static int totalMax = 0;
	
	public static void main(String[] args) throws Exception {
		System.out.println("开始抽签");
		//随机打乱数组，模拟抽签效果
		List<String> list = Arrays.asList(players);
		Collections.shuffle(list);
		players = list.toArray(new String[]{});
		System.out.println("抽签完毕，比赛开始");
		
		
		for(int i = 0; i < players.length; i++) {
			final String no = players[i];
			final int index = i;
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					try {
						//上锁
						lock.lock();
						System.out.println(no + "号选手上场");
						System.out.println(no + "号选手正在抓举");
						Thread.sleep((long) (Math.random() * 10000));
						int zhuaju = randomNumber(100, 200);
						if(zhuaju > zhuajuMax) {
							zhuajuMax = zhuaju;
						}
						zhuajus[index] = zhuaju;
						System.out.println(no + "号选手抓举成绩是：" + zhuaju + " KG");
						System.out.println(no + "号选手正在挺举");
						Thread.sleep((long) (Math.random() * 10000));
						int tingju = randomNumber(160, 380);
						if(tingju > tingjuMax) {
							tingjuMax = tingju;
						}
						tingjus[index] = tingju;
						System.out.println(no + "号选手挺举成绩是：" + tingju + " KG");
						System.out.println("\n");
					} catch (InterruptedException e) {
						e.printStackTrace();
					} finally {
						countDownLatch.countDown();
						//释放锁
						lock.unlock();
					}
				}
			};
			pool.execute(runnable);
		}
		//等待比赛结束
		countDownLatch.await();
		System.out.println("比赛结束，开始统计比赛成绩");
		for(int i = 0; i < zhuajus.length; i++) {
			if(zhuajus[i] == zhuajuMax) {
				System.out.println("抓举冠军是：" + players[i] + "号选手，抓举成绩是：" + zhuajuMax + " KG.");
				break;
			}
		}
		for(int i = 0; i < tingjus.length; i++) {
			if(tingjus[i] == tingjuMax) {
				System.out.println("挺举冠军是：" + players[i] + "号选手，挺举成绩是：" + tingjuMax + " KG.");
				break;
			}
		}
		int index = 0;
		for(int i = 0; i < total; i++) {
			int zhuaju = zhuajus[i];
			int tingju = tingjus[i];
			int total = zhuaju + tingju;
			if(total > totalMax) {
				totalMax = total;
				index = i;
			}
		}
		System.out.println("总冠军是：" + players[index] + "号选手，总成绩是：" + totalMax + " KG.");
		System.out.println("开始颁奖，哈哈!!!!!");
		
		//最后释放线程池资源
		pool.shutdown();
	}
	
	/**
	 * 生成指定区间[min-max)之间的随机数
	 * 
	 * @param max
	 * @param min
	 * @return
	 */
	public static int randomNumber(int max, int min) {
		return new Random().nextInt(max) % (max - min + 1) + min;
	}
}
