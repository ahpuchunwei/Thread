package com.alibaba.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
/**
 * Future测试
 * 对于你想异步获取多线程执行结果时，这个例子很有用
 * @author  ahpuchunwei
 */
public class ExecutorTest {
	public static void main(String[] args) throws InterruptedException,
			ExecutionException {
		//创建一个含缓冲区的线程池，线程池容量可动态调整
		ExecutorService pool = Executors.newCachedThreadPool();
		/**Future怎么理解呢，你可以这样理解：
		 * 由于是多线程执行，所以多线程什么时候执行完不可预知，那么高效的执行方式时，线程执行完后主动通知我：它执行完了，并告诉我执行结果放在哪儿了，然后
		 * 我接到通知主动去指定的地方去取结果，Future的作用就是代替你去傻傻等待线程执行完成，然后帮你拿到执行结果放到指定位置，并通知你，Future就充当这样
		 * 一个角色，由于有了Future的存在，你就腾出了傻傻等待线程执行完毕的等待时间，腾出的时间你可以去干别的事情，你不用关心线程什么时候执行完，反正Future
		 * 会通知你，有点明星找经纪人的感觉，明星什么时候在哪儿出席什么商业活动都是经纪人出面接洽的，明星自己闲暇就去泡妹纸/游玩是一个道理,哈哈！！！
		 * */
		List<Future<String>> futures = new ArrayList<Future<String>>(10);

		//创建10个线程放入线程池，并交给Future管理
		for (int i = 0; i < 10; i++) {
			final int seq = i;
			futures.add(pool.submit(new Callable<String>() {
				@Override
				public String call() throws Exception {
					Thread.sleep(new Random().nextInt(5000));
					return "Hello " + seq;
				}
			}));
		}

		//通过Future异步获取线程执行后返回的结果
		for (Future<String> future : futures) {
			String result = future.get();
			System.out.println("return result:" + result);
		}
		//释放线程池资源
		pool.shutdown();
	}
}
