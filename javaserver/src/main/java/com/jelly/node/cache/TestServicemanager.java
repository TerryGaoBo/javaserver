package com.jelly.node.cache;

import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.Service;
import com.google.common.util.concurrent.ServiceManager;

public class TestServicemanager {

	public Service servcie = new Service() {

		@Override
		public Service stopAsync() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public State stopAndWait() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public ListenableFuture<State> stop() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public State state() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Service startAsync() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public State startAndWait() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public ListenableFuture<State> start() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean isRunning() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public Throwable failureCause() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void awaitTerminated(long arg0, TimeUnit arg1)
				throws TimeoutException {
			// TODO Auto-generated method stub

		}

		@Override
		public void awaitTerminated() {
			// TODO Auto-generated method stub

		}

		@Override
		public void awaitRunning(long arg0, TimeUnit arg1)
				throws TimeoutException {
			// TODO Auto-generated method stub

		}

		@Override
		public void awaitRunning() {
			// TODO Auto-generated method stub

		}

		@Override
		public void addListener(Listener arg0, Executor arg1) {
			// TODO Auto-generated method stub

		}
	};
	Map<Integer, Integer> map = Maps.newConcurrentMap();

	public synchronized void putValue(int a, int b) {
		System.out.println(a + "xxxxxxxxxxxxxxx" + b);
		map.put(a, b);
		System.out.println(a + "    " + b);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ServiceManager sm = new ServiceManager(null);
		final TestServicemanager t = new TestServicemanager();

		// 定义线程1
		Thread thread1 = new Thread() {
			public void run() {
					t.putValue(1, 1);
				System.out.println("thread1...");
			}
		};
		// 定义线程2
		Thread thread2 = new Thread() {
			public void run() {
					t.putValue(2, 2);
				System.out.println("thread2...");
			}
		};
		// 定义关闭线程
		Thread shutdownThread = new Thread() {
			public void run() {
				System.out.println("shutdownThread...");
			}
		};
		// jvm关闭的时候先执行该线程钩子
		Runtime.getRuntime().addShutdownHook(shutdownThread);
		thread1.start();
		thread2.start();
	}
}
