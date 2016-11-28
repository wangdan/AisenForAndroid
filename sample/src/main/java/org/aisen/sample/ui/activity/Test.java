package org.aisen.sample.ui.activity;

import android.os.Handler;

import org.aisen.android.common.utils.Logger;
import org.aisen.android.network.task.TaskException;
import org.aisen.android.network.task.WorkTask;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by wangdan on 16/11/28.
 */
public class Test {

    static class MyThread extends Thread {

        private String name;

        MyThread(Runnable target, String name) {
            super(target, name);

            this.name = name;
        }

    }


    public static void test() {
        final ThreadPoolExecutor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(1, 128, 100, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(10), new ThreadFactory() {
            private final AtomicInteger mCount = new AtomicInteger(1);

            public Thread newThread(Runnable r) {
                int count = mCount.getAndIncrement();

                Logger.w("WorkTask", "Thread count = " + count);

                return new MyThread(r, "WorkTask # " + count) {

                    @Override
                    public void run() {
                        Logger.w("WorkTask", "begin run()");

                        super.run();

                        Logger.w("WorkTask", "end run()");
                    }

                    @Override
                    protected void finalize() throws Throwable {
                        super.finalize();

                        Logger.w("WorkTask", "finalize()");
                    }

                };
            }
        });
        THREAD_POOL_EXECUTOR.allowCoreThreadTimeOut(true);
//        final ExecutorService THREAD_POOL_EXECUTOR = Executors.newSingleThreadExecutor(new ThreadFactory() {
//            private final AtomicInteger mCount = new AtomicInteger(1);
//
//            public Thread newThread(Runnable r) {
//                int count = mCount.getAndIncrement();
//
//                Logger.w("WorkTask", "Thread count = " + count);
//
//                return new Thread(r, "WorkTask # " + count) {
//
//                    @Override
//                    protected void finalize() throws Throwable {
//                        super.finalize();
//
//                        Logger.w("WorkTask", "finalize()");
//                    }
//
//                };
//            }
//        });

//        final WorkTask<Void, Void, Boolean> workTask = new WorkTask<Void, Void, Boolean>("Test", null) {
//
//            @Override
//            public Boolean workInBackground(Void... params) throws TaskException {
//
//                try {
//                    Thread.sleep(1000);
//                } catch (Exception e) {
//                    e.printStackTrace();
//
//                    Logger.w("WorkTask", e + "");
//                }
//
//                return true;
//            }
//
//        }.executeOnExecutor(THREAD_POOL_EXECUTOR);
//        new Handler().postDelayed(new Runnable() {
//
//            @Override
//            public void run() {
//                final WorkTask<Void, Void, Boolean> workTask = new WorkTask<Void, Void, Boolean>("Test", null) {
//
//                    @Override
//                    public Boolean workInBackground(Void... params) throws TaskException {
//
//                        try {
//                            Thread.sleep(1000);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//
//                            Logger.w("WorkTask", e + "");
//                        }
//
//                        return true;
//                    }
//
//                }.executeOnExecutor(THREAD_POOL_EXECUTOR);
//
//                new Handler().postDelayed(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        Logger.w("WorkTask", "shutdownNow()");
//                        THREAD_POOL_EXECUTOR.shutdownNow();
//                    }
//
//                }, 3000);
//            }
//
//        }, 1000 * new Random().nextInt(3) + 5000);

//        final List<WorkTask> workTasks = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            final WorkTask<Void, Void, Boolean> workTask = new WorkTask<Void, Void, Boolean>("Test" + i, null) {

                @Override
                public Boolean workInBackground(Void... params) throws TaskException {

                    try {
                        Thread.sleep(2000);
                    } catch (Exception e) {
                        e.printStackTrace();

                        Logger.w("WorkTask", e + "");
                    }

                    return true;
                }

            }.executeOnExecutor(THREAD_POOL_EXECUTOR);

//            workTasks.add(workTask);
        }
//        THREAD_POOL_EXECUTOR.shutdown();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                for (int i = 0; i < 20; i++) {
                    final WorkTask<Void, Void, Boolean> workTask = new WorkTask<Void, Void, Boolean>("Test" + i, null) {

                        @Override
                        public Boolean workInBackground(Void... params) throws TaskException {

                            try {
                                Thread.sleep(2000);
                            } catch (Exception e) {
                                e.printStackTrace();

                                Logger.w("WorkTask", e + "");
                            }

                            return true;
                        }

                    }.executeOnExecutor(THREAD_POOL_EXECUTOR);

//            workTasks.add(workTask);
                }
            }

        }, 10 * 1000);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                Logger.w("WorkTask", "gc");

                System.gc();
            }

        }, 20 * 1000);

//        new Handler().postDelayed(new Runnable() {
//
//            @Override
//            public void run() {
//                for (int i = 0; i < workTasks.size(); i++) {
//                    workTasks.get(i).cancel(true);
//                }
//            }
//
//        }, 1000);


    }

}
