package com.jxnu.app;

/**
 * 消费者类
 */
public class Consumer implements Runnable {
	// 仓库
	private Storage storage;
	
	@Override
	public void run() {
		while(true) {
			try {
				storage.consume();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public Consumer(Storage storage) {
		this.storage = storage;
	}

	public Storage getStorage() {
		return storage;
	}

	public void setStorage(Storage storage) {
		this.storage = storage;
	}
}
