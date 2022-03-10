package com.sun.demo2.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * @author Harper
 * @date 2022/2/23
 * note:线程的开启方式
 * 1、集成Thread
 * 2、实现Runnable接口
 * 3、实现Callable接口（java 8才有），可以获取返回值
 */
public class ThreadTest {

    private void startThread() throws ExecutionException, InterruptedException {
        Thread t1 = new Thread(new MyRunnable());
        t1.start();

        FutureTask futureTask = new FutureTask(new MyCallable());
        Thread t2 = new Thread(futureTask);
        t2.start();
        //拿到子线程的返回值，这个过程是耗时的
        Object a = futureTask.get();
    }

    class MyRunnable implements Runnable {

        @Override
        public void run() {

        }
    }

    class MyCallable implements Callable {

        @Override
        public Object call() throws Exception {
            int a = 100;
            int b = 100;
            return a + b;
        }
    }

    private void test() {
        List<String> list = new ArrayList<>();
        Thread t1 = new Thread(new Producer(list));
        Thread t2 = new Thread(new Consumer(list));
        t1.setName("生产者");
        t2.setName("消费者");
        t1.start();
        t2.start();
    }

    class Producer implements Runnable {

        private List<String> list;

        public Producer(List<String> list) {
            this.list = list;
        }

        @Override
        public void run() {
            while (true) {
                synchronized (list) {
                    if (list.size() > 0) {
                        try {
                            list.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    list.add("牛奶");
                    list.notify();
                }
            }
        }
    }

    class Consumer implements Runnable {

        private List<String> list;

        public Consumer(List<String> list) {
            this.list = list;
        }

        @Override
        public void run() {
            while (true){
                synchronized (list){
                    if (list.size() == 0){
                        try {
                            list.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    list.remove(0);
                    list.notify();
                }
            }
        }
    }
}
