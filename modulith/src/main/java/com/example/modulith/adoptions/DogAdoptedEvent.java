package com.example.modulith.adoptions;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.core.GenericHandler;
import org.springframework.integration.dsl.DirectChannelSpec;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.file.dsl.Files;
import org.springframework.messaging.MessageChannel;
import org.springframework.modulith.events.Externalized;

import java.io.File;

// kafka, rabbitmq, spring framework message channel
@Externalized(OutboundMessageChannelConfiguration.OUTBOUND_MESSAGE_CHANNEL)
public record DogAdoptedEvent(int dogId) {
}

@Configuration
class OutboundMessageChannelConfiguration {


    static final String OUTBOUND_MESSAGE_CHANNEL = "adoptionsChannel";

    @Bean(OUTBOUND_MESSAGE_CHANNEL)
    DirectChannelSpec directChannelSpec() {
        return MessageChannels.direct(OUTBOUND_MESSAGE_CHANNEL);
    }

    //
    @Bean
    IntegrationFlow integrationFlow(
            @Value("file://${HOME}/Desktop/out") File dir,
            @Qualifier(OUTBOUND_MESSAGE_CHANNEL) MessageChannel inbound) {
        var files = Files.outboundAdapter(dir).autoCreateDirectory(true);
        return IntegrationFlow
                .from(inbound)
                .handle((GenericHandler<DogAdoptedEvent>) (payload, headers) -> {
                    IO.println("got an event " + payload + "!");
                    headers.forEach((k, v) -> IO.println(k + " -> " + v));
                    return payload;
                })
                .transform(DogAdoptedEvent::dogId)
                .transform(String::valueOf)
                .handle(files)
                .get();
    }
}


// cat foo.txt | grep the | sort | uniq | wc -l > count.xt
// producers |  consumers

// 4. messaging