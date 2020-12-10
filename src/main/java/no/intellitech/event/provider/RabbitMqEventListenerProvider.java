package no.intellitech.event.provider;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.admin.AdminEvent;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.AMQP.BasicProperties.Builder;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

public class RabbitMqEventListenerProvider implements EventListenerProvider {

	private static final Logger log = LogManager.getLogger(RabbitMqEventListenerProvider.class);

	private final RabbitMqConfig cfg;
	private final ConnectionFactory factory;
	private final KeycloakSession session;

	public RabbitMqEventListenerProvider(RabbitMqConfig cfg, KeycloakSession session) {
		this.cfg = cfg;
		this.session = session;
		
		this.factory = new ConnectionFactory();

		this.factory.setUsername(cfg.getUsername());
		this.factory.setPassword(cfg.getPassword());
		this.factory.setVirtualHost(cfg.getVhost());
		this.factory.setHost(cfg.getHostUrl());
		this.factory.setPort(cfg.getPort());
	}

	@Override
	public void close() {

	}

	@Override
	public void onEvent(Event event) {
		EventClientNotificationMqMsg msg = EventClientNotificationMqMsg.create(event);
		populateMoreValues(msg);
		String routingKey = RabbitMqConfig.calculateRoutingKey(event);
		String messageString = RabbitMqConfig.writeAsJson(msg, true);
		
		BasicProperties msgProps = this.getMessageProps(EventClientNotificationMqMsg.class.getName());
		this.publishNotification(messageString, msgProps, routingKey);
	}

	private void populateMoreValues(EventClientNotificationMqMsg event) {
		String userId = event.getUserId();
		log.info("populateMoreValues userId: " + userId);
		log.info("populateMoreValues RealmId: " + event.getRealmId());
		RealmModel realm = session.realms().getRealm(event.getRealmId());
		log.info("populateMoreValues RealmModel: " + realm);
		if (userId != null) {
			UserModel user = session.users().getUserById(userId, realm);
			log.info("populateMoreValues user: " + user);
			// get all attributes
			//Map<String, List<String>> allAttributes = user.getAttributes(); // or
			// get Attribute by name
			String nin = user.getFirstAttribute("nin");
			log.info("populateMoreValues nin: " + nin);
			event.setNin(user.getFirstAttribute("nin"));
			event.setFirstName(user.getFirstName());
			event.setLastName(user.getLastName());
		}
	}

	@Override
	public void onEvent(AdminEvent event, boolean includeRepresentation) {
		EventAdminNotificationMqMsg msg = EventAdminNotificationMqMsg.create(event);
		String routingKey = RabbitMqConfig.calculateRoutingKey(event);
		String messageString = RabbitMqConfig.writeAsJson(msg, true);
		BasicProperties msgProps = this.getMessageProps(EventAdminNotificationMqMsg.class.getName());
		this.publishNotification(messageString,msgProps, routingKey);
	}
	
	private BasicProperties getMessageProps(String className) {
		
		Map<String,Object> headers = new HashMap<String,Object>();
		headers.put("__TypeId__", className);
		
		Builder propsBuilder = new AMQP.BasicProperties.Builder()
				.appId("Keycloak")
				.headers(headers)
				.contentType("application/json")
				.contentEncoding("UTF-8");
		return propsBuilder.build();
	}

	private void publishNotification(String messageString, BasicProperties props, String routingKey) {

		try {
			Connection conn = factory.newConnection();
			Channel channel = conn.createChannel();
			channel.basicPublish(cfg.getExchange(), routingKey, props, messageString.getBytes());
			log.info("keycloak-to-rabbitmq SUCCESS sending message: " + routingKey);
			channel.close();
			conn.close();

		} catch (Exception ex) {
			log.info(cfg.toString());
			// System.err.println("keycloak-to-rabbitmq ERROR sending message: " + routingKey);
			// ex.printStackTrace();
			log.error("keycloak-to-rabbitmq ERROR sending message: " + routingKey);
			log.error(ex.getMessage(), ex);
		}
	}

}
