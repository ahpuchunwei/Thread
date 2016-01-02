package com.jxnu.app;

public class InterviewTest {
	final Businese b = new Businese();
	public static void main(String[] args) {
		final InterviewTest t = new InterviewTest();
		for(int i = 0; i < 100; i++) {
			//子线程
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						t.b.sub();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}).start();
			
			//主线程
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						t.b.main();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}).start();
		}
	}
	
	class Businese {
		private boolean isSubRun = true;
		public synchronized void sub() throws InterruptedException {
			while (!isSubRun) {
				this.wait();
			}
			System.out.println("轮到子线程执行了");
			for(int i = 0; i < 100; i++) {
				System.out.println("子线程第" + (i+1) + "次循环");
			}
			isSubRun = false;
			this.notify();
			System.out.println("子线程执行完毕，唤醒主线程，轮到主线程执行");
		}
		
		public synchronized void main() throws InterruptedException{
			while (isSubRun) {
				this.wait();
			}
			System.out.println("轮到主线程执行了");
			for(int i = 0; i < 10; i++) {
				System.out.println("主线程第" + (i+1) + "次循环");
			}
			isSubRun = true;
			this.notify();
			System.out.println("主线程执行完毕，唤醒子线程，轮到子线程执行");
		}
	}
}
