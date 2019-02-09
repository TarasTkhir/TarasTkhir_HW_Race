package com.basecamp.service.impl;

import com.basecamp.exception.InternalException;
import com.basecamp.exception.InvalidDataException;
import com.basecamp.service.ProductService;
import com.basecamp.wire.GetHandleProductIdsResponse;
import com.basecamp.wire.GetProductInfoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static java.lang.Thread.sleep;

@Service
@RequiredArgsConstructor
@Log4j2
public class ProductServiceImpl implements ProductService {

    private final int CAPACITY = 20;
    private final int POOL = 5;
    private Map<String, String> concurrentMup = new ConcurrentHashMap<>(CAPACITY);

    private ExecutorService service = Executors.newFixedThreadPool(POOL);

    private Random rand = new Random();

    private final ConcurrentTaskService taskService;

    public GetProductInfoResponse getProductInfo(String productId) {

        validateId(productId);

        log.info("Product id {} was successfully validated.", productId);

        return callToDbAnotherServiceETC(productId);
    }

    public GetHandleProductIdsResponse handleProducts(List<String> productIds) {
        Map<String, Future<String>> handledTasks = new HashMap<>();
        productIds.forEach(productId ->
                handledTasks.put(
                        productId,
                        taskService.handleProductIdByExecutor(productId)));

        List<String> handledIds = handledTasks.entrySet().stream().map(stringFutureEntry -> {
            try {
                return stringFutureEntry.getValue().get(3, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                log.error(stringFutureEntry.getKey() + " execution error!");
            }

            return stringFutureEntry.getKey() + " is not handled!";
        }).collect(Collectors.toList());

        return GetHandleProductIdsResponse.builder()
                .productIds(handledIds)
                .build();
    }

    public void stopProductExecutor() {
        log.warn("Calling to stop product executor...");

        taskService.stopExecutorService();

        log.info("Product executor stopped.");
    }


    private void validateId(String id) {

        if (StringUtils.isEmpty(id)) {
            // all messages could be moved to messages properties file (resources)
            String msg = "ProductId is not set.";
            log.error(msg);
            throw new InvalidDataException(msg);
        }

        try {
            Integer.valueOf(id);
        } catch (NumberFormatException e) {
            String msg = String.format("ProductId %s is not a number.", id);
            log.error(msg);
            throw new InvalidDataException(msg);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new InternalException(e.getMessage());
        }
    }

    private GetProductInfoResponse callToDbAnotherServiceETC(String productId) {
        return GetProductInfoResponse.builder()
                .id(productId)
                .name("ProductName")
                .status("ProductStatus")
                .build();
    }

    @Override
    public void Race() {

        System.out.println(Thread.getAllStackTraces().keySet().size());
        for (int i = 0; i < POOL; i++) {
            service.submit(new Runnable() {
                public void run() {

                    StringBuilder bilder = new StringBuilder("");

                    System.out.println(Thread.currentThread().getName());

                    int result = 0;

                    for (int i = 0; i >= 0; i++) {

                        result += rand.nextInt(10);

                        if (result >= 100) {

                            System.out.format("... %s finished:  " + LocalDateTime.now(), Thread.currentThread().getName());
                            break;
                        }

                        for (int k = result; k > 0; k--) {

                            bilder.append(".");
                        }
                        if (concurrentMup.containsKey(Thread.currentThread().getName())) {

                            concurrentMup.remove(Thread.currentThread().getName());
                            concurrentMup.put(Thread.currentThread().getName(), bilder.toString() + "Ж=");
                        } else {

                            concurrentMup.put(Thread.currentThread().getName(), bilder.toString() + "Ж=");
                        }

                        try {
                            sleep(1000);
                        } catch (InterruptedException e) {

                            log.warn("racer is interrupted.");
                        }
                    }


                }
            });

        }
        service.shutdown();
//        try {
//            service.shutdown();
//            service.awaitTermination(15, TimeUnit.SECONDS);
//        } catch (InterruptedException e) {
//            log.warn("Stop executor interrupted.");
//        }


        for (int i = 20; i > 0; i--) {

            for (Map.Entry<String, String> pair : concurrentMup.entrySet()) {

                System.out.println(pair.getKey() + " " + pair.getValue());
            }

            try {

                sleep(1500);

            } catch (InterruptedException e) {

                e.printStackTrace();
            }

            System.out.println("\n");
            System.out.println("\n");
            System.out.println("\n");

        }

    }
}