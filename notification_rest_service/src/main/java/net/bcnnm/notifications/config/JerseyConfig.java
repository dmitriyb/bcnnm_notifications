package net.bcnnm.notifications.config;

import net.bcnnm.notifications.MainController;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

@Component
public class JerseyConfig extends ResourceConfig {
    public JerseyConfig() {
        register(MainController.class);
    }
}
