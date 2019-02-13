package zbl.fly;

import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class IncrTest  {


    private static void concurrenceTest() {
//        /**
//         * 模拟高并发情况代码
//         */
        final AtomicInteger atomicInteger = new AtomicInteger(10);
        final CountDownLatch countDownLatch = new CountDownLatch(10); // 相当于计数器，当所有都准备好了，再一起执行，模仿多并发，保证并发量
        final CountDownLatch countDownLatch2 = new CountDownLatch(10); // 保证所有线程执行完了再打印atomicInteger的值
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        try {
            for (int i = 0; i < 105; i++) {
                executorService.submit(() -> {
                    try {
                        countDownLatch.await(); //一直阻塞当前线程，直到计时器的值为0,保证同时并发
                        RestTemplate restTemplate = new RestTemplate();
                        String result = restTemplate.getForObject("http://localhost:8080/ajaxlogin.do?name={1}&password={2}", String.class, new String[]{"admin", "admin"});
                        System.out.println(result);
                        String resultModify = restTemplate.getForObject("http://localhost:8080/manager/modifyManagerPwd.do?id={1}&oldPwd={2}&newPwd={3}", String.class, new String[]{"1", "admin", "admin1"});
                        System.out.println("resultModify:"+resultModify);
                        String resultModify1 = restTemplate.getForObject("http://localhost:8080/manager/modifyManagerPwd.do?id={1}&oldPwd={2}&newPwd={3}", String.class, new String[]{"1", "admin1", "admin"});
                        System.out.println("resultModify1:"+resultModify1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //每个线程增加1000次，每次加1
                    for (int j = 0; j < 2; j++) {
                        atomicInteger.incrementAndGet();
                    }
                    countDownLatch2.countDown();
                });
                countDownLatch.countDown();
            }

            countDownLatch2.await();// 保证所有线程执行完
            System.out.println(atomicInteger);
            executorService.shutdown();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        concurrenceTest();
    }
}
