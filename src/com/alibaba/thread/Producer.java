package com.alibaba.thread;

/**
 * 生产者类
 * 
 */
public class Producer implements Runnable {
	// 仓库
	private Storage storage;
	
	@Override
	public void run() {
		while(true) {
			try {
				storage.produce();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public Producer(Storage storage) {
		this.storage = storage;
	}

	public Storage getStorage() {
		return storage;
	}

	public void setStorage(Storage storage) {
		this.storage = storage;
	}
}
