package com.sun.demo2.model;

/**
 * @author Harper
 * @date 2022/2/23
 * note:死锁产生的原因
 * 1、互斥使用，即当资源被一个线程使用(占有)时，别的线程不能使用
 * 2、不可抢占，资源请求者不能强制从资源占有者手中夺取资源，资源只能由资源占有者主动释放。
 * 3、请求和保持，即当资源请求者在请求其他的资源的同时保持对原有资源的占有。
 * 4、循环等待，即存在一个等待队列：P1占有P2的资源，P2占有P3的资源，P3占有P1的资源。这样就形成了一个等待环路。
 */
public class DeadLock {

    private void startDeadLock() {
        Object ob1 = new Object();
        Object ob2 = new Object();
        Thread t1 = new MyThread1(ob1, ob2);
        Thread t2 = new MyThread2(ob1, ob2);
        t1.start();
        t2.start();
    }

    class MyThread1 extends Thread {
        Object ob1;
        Object ob2;

        public MyThread1(Object ob1, Object ob2) {
            this.ob1 = ob1;
            this.ob2 = ob2;
        }

        @Override
        public void run() {
            synchronized (ob1) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (ob2) {

                }
            }
        }
    }

    class MyThread2 extends Thread {
        Object ob1;
        Object ob2;

        public MyThread2(Object ob1, Object ob2) {
            this.ob1 = ob1;
            this.ob2 = ob2;
        }

        @Override
        public void run() {
            synchronized (ob2) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (ob1) {

                }
            }
        }
    }
}
