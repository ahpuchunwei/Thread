package com.jxnu.app;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 生产者和消费者测试
 * 经典的生产者-消费者问题，生产者不停的生产产品，消费者则不停的消费生产者生产的产品
 * 1.当产品数量达到最大库存量时，生产者开始暂停生产，并通知消费者开始消费
 * 2.当产品数量为0时，消费者开始暂停消费，并通知生产者开始生产
 * 3.两个线程并行执行且在特定条件下可以自动暂停以及互相通知
 * @author  ahpuchunwei
 */
public class ProducerAndConsumerTest {
	//线程池
	private static ExecutorService pool = Executors.newFixedThreadPool(2);
	
	
	
	public static void main(String[] args) {
		// 仓库对象  
        Storage storage = new Storage();
        
        //生产者
        Producer producer = new Producer(storage);
        
        //消费者
        Consumer consumer = new Consumer(storage);
        
        pool.execute(producer);
        pool.execute(consumer);
        
        pool.shutdown();
	}
}
