package com.github.aznamier.keycloak.event.provider;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import org.keycloak.events.Event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@JsonIgnoreProperties(ignoreUnknown = true)
@XmlRootElement
@JsonTypeInfo(use = Id.CLASS)
public class EventClientNotificationMqMsg extends Event implements Serializable {

	private static final long serialVersionUID = -2192461924304841222L;

	private String firstName;
	private String lastName;
	private String nin; // Is Signicat's new name on SSN

	public static EventClientNotificationMqMsg create(Event event) {
		EventClientNotificationMqMsg msg = new EventClientNotificationMqMsg();
		msg.setClientId(event.getClientId());
		msg.setDetails(event.getDetails());
		msg.setError(event.getError());
		msg.setIpAddress(event.getIpAddress());
		msg.setRealmId(event.getRealmId());
		msg.setSessionId(event.getSessionId());
		msg.setTime(event.getTime());
		msg.setType(event.getType());
		msg.setUserId(event.getUserId());

		return msg;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getNin() {
		return nin;
	}

	public void setNin(String nin) {
		this.nin = nin;
	}
}
