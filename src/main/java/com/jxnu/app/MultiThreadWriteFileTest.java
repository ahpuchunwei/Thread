package com.jxnu.app;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 多线程写文件测试<br/>
 *  启动4个线程，向4个文件A，B，C，D里写入数据，每个线程只能写一个值。 <br/>
          线程1：只写1 <br/>
          线程2：只写2 <br/>
	线程3：只写3 <br/>
	线程4：只写4 <br/>
    4个文件A，B，C，D。 <br/>
          程序运行起来，4个文件的写入结果如下： <br/>
    A：12341234... <br/>
    B：23412341... <br/>
    C：34123412... <br/>
    D：41234123...<br/>
 * @author  ahpuchunwei
 */
public class MultiThreadWriteFileTest {
	private static final String BASE_PATH = "E:/test/";
	
	private static int state = 1;
	/**写10次*/
	private static int count = 10;
	
	/**各线程文件写入顺序*/
	private static int[][] writeOrders = {{0,3,2,1},{1,0,3,2},{2,1,0,3},{3,2,1,0}};
	
	private static Lock lock = new ReentrantLock();

	private static Condition condition1 = lock.newCondition();
	private static Condition condition2 = lock.newCondition();
	private static Condition condition3 = lock.newCondition();
	private static Condition condition4 = lock.newCondition();
	
	//3个维度依次是线程，文件，写入索引
	private static int[][][] writeIndex = new int[4][4][count];
	/**写入内容*/
	private static String[][] writeContent = new String[4][4*count];
	
	private static String[] fileNames = {"A","B","C","D"};
	/**文件写入编码*/
	private static final String CHARSET = "UTF-8";
	
	//线程池
	private static ExecutorService pool = Executors.newCachedThreadPool();
	//计数器
	private static CountDownLatch countDownLatch = new CountDownLatch(4*count);
	static {
		//初始化根目录
		File baseFolder = new File(BASE_PATH);
		if(!baseFolder.exists()) {
			baseFolder.mkdir();
		}
		//创建A,B,C,D 4个记事本文件
		for(int i = 65; i < 69; i++) {
			String filePath = BASE_PATH + (char)i + ".txt";
			File file = new File(filePath);
			if(!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e) {
					System.out.println("创建文件:" + (char)i + ".txt失败");
				}
			}
		}
		
		//线程
		for(int i = 0; i < 4; i++) {
			//文件
			for(int j = 0; j < 4; j++) {
				int order = writeOrders[i][j];
				//写入索引
				for(int k = 0; k < count; k++) {
					writeIndex[i][j][k] = 4*k + order;
				}
			}
		}
	}
	
	public static void main(String[] args) throws Exception {
		for(int i = 0; i < count; i++) {
			//线程1
			Runnable runnable1 = new Runnable() {	
				@Override
				public void run() {
					lock.lock();
					try {
						while (state != 1) {
							condition1.await();
						}
						fillArray(0, "1");
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						// 唤醒线程2
						state = 2;
						condition2.signal();
						countDownLatch.countDown();
						lock.unlock();
					}
				}
			};
			
			//线程2
			Runnable runnable2 = new Runnable() {	
				@Override
				public void run() {
					lock.lock();
					try {
						while (state != 2) {
							condition2.await();
						}
						fillArray(1, "2");
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						// 唤醒线程3
						state = 3;
						condition3.signal();
						countDownLatch.countDown();
						lock.unlock();
					}
				}
			};
			
			//线程3
			Runnable runnable3 = new Runnable() {	
				@Override
				public void run() {
					lock.lock();
					try {
						while (state != 3) {
							condition3.await();
						}
						fillArray(2, "3");
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						// 唤醒线程4
						state = 4;
						condition4.signal();
						countDownLatch.countDown();
						lock.unlock();
					}
				}
			};
			
			//线程4
			Runnable runnable4 = new Runnable() {	
				@Override
				public void run() {
					lock.lock();
					try {
						while (state != 4) {
							condition4.await();
						}
						fillArray(3, "4");
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						// 唤醒线程1
						state = 1;
						condition1.signal();
						countDownLatch.countDown();
						lock.unlock();
					}
				}
			};
			pool.execute(runnable1);
			pool.execute(runnable2);
			pool.execute(runnable3);
			pool.execute(runnable4);
		}
		//等待所有线程执行完毕
		countDownLatch.await();
		//写入文件
		write();
		//释放线程池资源
		pool.shutdown();
	}
	
	/**
	 * 4个线程执行完毕后把数组写入文件
	 */
	private static void write() {
		for(int i = 0; i < 4; i++) {
			String fileName = fileNames[i] + ".txt";
			for(int j = 0; j < 4*count; j++) {
				writeFile(writeContent[i][j], BASE_PATH + fileName, CHARSET, true);
			}
		}
	}
	
	/**
	 * 填充写入内容数组
	 * @param threadIndex  线程索引
	 * @param content      写入内容
	 */
	private static void fillArray(int threadIndex,String content) {
		int[][] indexs = writeIndex[threadIndex];
		for(int i = 0; i < 4; i++) {
			for(int j = 0; j < count; j++) {
				int index = indexs[i][j];
				writeContent[i][index] = content;
			}
		}
	}
	
	
	/**
	 * 把字符串以指定编码写入文件，<br/>可以指定写入方式：追加/覆盖
	 * @param content    写入的字符串
	 * @param filePath   文件保存路径
	 * @param charset    写入编码
	 * @param append     是否追加
	 */
	public static void writeFile(String content,String filePath,String charset,boolean append) {
		BufferedWriter writer = null;
		OutputStream os = null; 
		OutputStreamWriter osw = null; 
		try { 
	        os = new FileOutputStream(filePath,append); 
	        osw = new OutputStreamWriter(os, charset); 
	        writer = new BufferedWriter(osw);
	        writer.write(content); 
	        writer.flush();
	    } catch (Exception e) { 
	        e.printStackTrace(); 
	    } finally {
	    	try {
	    		os.close();
	    		osw.close();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }
	}
}
