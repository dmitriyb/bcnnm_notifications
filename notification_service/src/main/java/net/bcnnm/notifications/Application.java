package net.bcnnm.notifications;

import net.bcnnm.notifications.fcc.NotificationServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication(scanBasePackages = {"me.ramswaroop.jbot", "net.bcnnm.notifications"})
public class Application implements CommandLineRunner {

    @Autowired
    private ApplicationContext appContext;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) {

        NotificationServer notificationServer = appContext.getBean(NotificationServer.class);
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        executorService.submit(notificationServer::run);
    }
}
