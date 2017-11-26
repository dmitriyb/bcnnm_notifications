package net.bcnnm.notifications.fcc;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        NotificationServer notificationServer = new NotificationServer();

        ExecutorService executorService = Executors.newFixedThreadPool(5);
        executorService.submit(notificationServer::run);

//        executorService.submit(FccControlCenterStub::main);

        Thread.sleep(5000);

//        notificationServer.askFccForStatus(session, event);
    }
}
