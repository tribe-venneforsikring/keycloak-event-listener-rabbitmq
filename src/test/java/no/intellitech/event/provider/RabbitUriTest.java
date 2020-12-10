package no.intellitech.event.provider;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RabbitUriTest {

    @Test
    public void testParse_herokuUri() {
        // note: not real data
        String rabbitUrl = "amqps://imk345dk:aHpq90Z435309fsdsdgf5KM6Cx5Ra0ovpE@crow.rmq.cloudamqp.com/imk345dk";

        RabbitUri uri = new RabbitUri(rabbitUrl);

        assertEquals("amqps", uri.getScheme());
        assertEquals("crow.rmq.cloudamqp.com", uri.getHost());
        assertEquals("imk345dk", uri.getPath());

        assertEquals("imk345dk", uri.getUser());
        assertEquals("aHpq90Z435309fsdsdgf5KM6Cx5Ra0ovpE", uri.getPwd());
        assertEquals(5672, uri.getPort());
    }

    @Test
    public void testParse_dockerUri() {
        // note: not real data
        String rabbitUrl = "amqps://guest:guest@wtc.rabbitmq/";

        RabbitUri uri = new RabbitUri(rabbitUrl);

        assertEquals("amqps", uri.getScheme());
        assertEquals("wtc.rabbitmq", uri.getHost());
        assertEquals("", uri.getPath());

        assertEquals("guest", uri.getUser());
        assertEquals("guest", uri.getPwd());
        assertEquals(5672, uri.getPort());
    }

    @Test
    public void testParse_realHerokuUri() {
        // note: not real data
        String rabbitUrl = "amqps://imk345dk:aHpq90Z435309fsdsdgf5KM6Cx5Ra0ovpE@crow.rmq.cloudamqp.com/imk345dk";

        RabbitUri uri = new RabbitUri(rabbitUrl);

        assertEquals("amqps", uri.getScheme());
        assertEquals("crow.rmq.cloudamqp.com", uri.getHost());
        assertEquals("imk345dk", uri.getPath());

        assertEquals("imk345dk", uri.getUser());
        assertEquals("aHpq90Z435309fsdsdgf5KM6Cx5Ra0ovpE", uri.getPwd());
        assertEquals(5672, uri.getPort());
    }

    @Test
    public void testParseRabbitUri() throws URISyntaxException {

        // note: not real data
        String rabbitUrl = "amqps://imk345dk:aHpq90Z435309fsdsdgf5KM6Cx5Ra0ovpE@crow.rmq.cloudamqp.com/imk345dk";

        URI uri = new URI(rabbitUrl);

        assertEquals("amqps", uri.getScheme());
        assertEquals("imk345dk:aHpq90Z435309fsdsdgf5KM6Cx5Ra0ovpE", uri.getUserInfo());
        assertEquals("crow.rmq.cloudamqp.com", uri.getHost());
        assertEquals("/imk345dk", uri.getPath());
        assertEquals("imk345dk:aHpq90Z435309fsdsdgf5KM6Cx5Ra0ovpE@crow.rmq.cloudamqp.com", uri.getAuthority());
        String[] userInfo = uri.getUserInfo().split(":");
        assertEquals("imk345dk", userInfo[0]);
        assertEquals("aHpq90Z435309fsdsdgf5KM6Cx5Ra0ovpE", userInfo[1]);


    }
}
