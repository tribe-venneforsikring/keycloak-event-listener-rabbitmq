package no.intellitech.event.provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.keycloak.Config.Scope;
import org.keycloak.events.Event;
import org.keycloak.events.admin.AdminEvent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Optional;

import static no.intellitech.event.provider.RabbitUri.CLOUDAMQP_URL;

public class RabbitMqConfig {

	private static final Logger log = LoggerFactory.getLogger(RabbitMqConfig.class);

	public static final ObjectMapper rabbitMqObjectMapper = new ObjectMapper();
	public static String ROUTING_KEY_PREFIX = "KK.EVENT";

	private String hostUrl;
	private Integer port;
	private String username;
	private String password;
	private String vhost;
	
	private String exchange;
	
	public static String calculateRoutingKey(AdminEvent adminEvent) {
		//KK.EVENT.ADMIN.<REALM>.<RESULT>.<RESOURCE_TYPE>.<OPERATION>
		String routingKey = ROUTING_KEY_PREFIX
				+ ".ADMIN"
				+ "." + adminEvent.getRealmId()
				+ "." + (adminEvent.getError() != null ? "ERROR" : "SUCCESS")
				+ "." + adminEvent.getResourceTypeAsString()
				+ "." + adminEvent.getOperationType().toString()
				
				;
		return normalizeKey(routingKey);
	}
	
	public static String calculateRoutingKey(Event event) {
		// KK.EVENT.CLIENT.<REALM>.<RESULT>.<CLIENT>.<EVENT_TYPE>
		String routingKey = ROUTING_KEY_PREFIX
					+ ".CLIENT"
					+ "." + event.getRealmId()
					+ "." + (event.getError() != null ? "ERROR" : "SUCCESS")
					+ "." + event.getClientId()
					+ "." + event.getType();
		
		return normalizeKey(routingKey);
	}
	
	// Remove all characters apart a-z, A-Z, 0-9, space, underscore, eplace all spaces and hyphens with underscore
	public static String normalizeKey(String stringToNormalize) {
		return stringToNormalize.replaceAll("[^\\*#a-zA-Z0-9 _.-]", "").
				replaceAll(" ", "_");
	}
	
	public static String writeAsJson(Object object, boolean isPretty) {
		String messageAsJson = "unparsable";
		try {
			if(isPretty) {
				messageAsJson = RabbitMqConfig.rabbitMqObjectMapper
						.writerWithDefaultPrettyPrinter().writeValueAsString(object);
			} else {
				messageAsJson = RabbitMqConfig.rabbitMqObjectMapper.writeValueAsString(object);
			}
			
		} catch (JsonProcessingException e) {
			log.error(e.getMessage(), e);
		}
		return messageAsJson;
	}

	public static RabbitMqConfig createFromScope(Scope config) {
		RabbitMqConfig cfg = new RabbitMqConfig();

		Optional<String> cloudAmqpUrlOpt = RabbitUri.getCloudAmqpUrlFromEnv();
		if (cloudAmqpUrlOpt.isPresent()) {
			log.info("Found " + CLOUDAMQP_URL + ". Uri is: " + cloudAmqpUrlOpt.get());
			RabbitUri uri = new RabbitUri(cloudAmqpUrlOpt.get());
			cfg.hostUrl = uri.getHost();
			cfg.port = uri.getPort();
			cfg.username = uri.getUser();
			cfg.password = uri.getPwd();
			cfg.vhost = uri.getPath();
		}
		else {
			log.info("Didn't find " +  CLOUDAMQP_URL + " env variable. Reading KK_TO_RMQ_* env variables instead.");
			cfg.hostUrl = resolveConfigVar(config, "url", "localhost");
			cfg.port = Integer.valueOf(resolveConfigVar(config, "port", "5672"));
			cfg.username = resolveConfigVar(config, "username", "admin");
			cfg.password = resolveConfigVar(config, "password", "admin");
			cfg.vhost = resolveConfigVar(config, "vhost", "");
		}
		cfg.exchange = resolveConfigVar(config, "exchange", "amq.topic");

		log.info("hostUrl: {}, port: {}, username: {}, password: {}, vhost: {}, exchange: {}",
				cfg.hostUrl, cfg.port, cfg.username, cfg.password, cfg.vhost, cfg.exchange);

		return cfg;
	}

	private static String resolveConfigVar(Scope config, String variableName, String defaultValue) {
		
		String value = defaultValue;
		if(config != null && config.get(variableName) != null) {
			value = config.get(variableName);
		} else {
			// try from env variables eg: KK_TO_RMQ_URL:
			String envVariableName = "KK_TO_RMQ_" + variableName.toUpperCase();
			if(System.getenv(envVariableName) != null) {
				value = System.getenv(envVariableName);
			}
		}
		log.info("keycloak-to-rabbitmq configuration: " + variableName + "=" + value);
		return value;
	}

	@Override
	public String toString() {
		return String.format("hostUrl: %s, port: %s, username: %s, password: %s, vhost: %s, exchange: %s",
				hostUrl, port, username, password, vhost, exchange);
	}
	
	public String getHostUrl() {
		return hostUrl;
	}
	public void setHostUrl(String hostUrl) {
		this.hostUrl = hostUrl;
	}
	public Integer getPort() {
		return port;
	}
	public void setPort(Integer port) {
		this.port = port;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getVhost() {
		return vhost;
	}
	public void setVhost(String vhost) {
		this.vhost = vhost;
	}
	public String getExchange() {
		return exchange;
	}
	public void setExchange(String exchange) {
		this.exchange = exchange;
	}

}
