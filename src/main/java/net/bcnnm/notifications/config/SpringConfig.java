package net.bcnnm.notifications.config;

import net.bcnnm.notifications.slack.SlackChannelWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfig {
    private final String MODELING_STATUS_TEST = "modeling_status_test";

    @Bean
    public SlackChannelWriter slack() {
        return new SlackChannelWriter(MODELING_STATUS_TEST);
    }
}
