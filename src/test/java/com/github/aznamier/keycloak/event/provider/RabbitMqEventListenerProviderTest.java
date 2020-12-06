package com.github.aznamier.keycloak.event.provider;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.keycloak.events.Event;
import org.keycloak.events.EventType;

public class RabbitMqEventListenerProviderTest {

    //private static final Logger log = LogManager.getLogger(RabbitMqEventListenerProviderTest.class);

    public void test() {

    }

    //@Disabled
    //@Test
    /*public void testOnEvent() {

        RabbitMqEventListenerProvider provider = new RabbitMqEventListenerProvider(buildConfig(), null);
        provider.onEvent(buildEvent());

    }*/


    /*static RabbitMqConfig buildConfig() {
        // hostUrl: rabbitmq, port: 6672, username: guest, password: guest, vhost: /, exchange: amq.topic
        RabbitMqConfig config = new RabbitMqConfig();
        config.setHostUrl("localhost");
        config.setPort(6672);
        config.setExchange("amq.topic");
        config.setVhost("/");
        config.setUsername("guest");
        config.setPassword("guest");

        log.debug(config.toString());

        return config;
    }*/

    /*
    	public static String calculateRoutingKey(Event event) {
		// KK.EVENT.CLIENT.<REALM>.<RESULT>.<CLIENT>.<EVENT_TYPE>
		String routingKey = ROUTING_KEY_PREFIX
					+ ".CLIENT"
					+ "." + event.getRealmId()
					+ "." + (event.getError() != null ? "ERROR" : "SUCCESS")
					+ "." + event.getClientId()
					+ "." + event.getType();

            KK.EVENT.CLIENT.watercircles.SUCCESS.client-app.REGISTER
     */
    static Event buildEvent() {
        Event event = new Event();
        event.setRealmId("watercircles");
        event.setError(null);
        event.setClientId("client-app");
        event.setType(EventType.REGISTER);
        return event;
    }
}
