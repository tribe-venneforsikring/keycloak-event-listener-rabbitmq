package no.intellitech.event.provider;

import org.checkerframework.checker.nullness.Opt;

import javax.swing.text.html.Option;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

public class RabbitUri {

    public static final String CLOUDAMQP_URL = "CLOUDAMQP_URL";

    private final String scheme;
    private final String user;
    private final String pwd;
    private final String host;
    private final Integer port;
    private final String path; // the virtual host

    public RabbitUri(String rabbitUri) {
        URI uri;
        try {
            uri = new URI(rabbitUri);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Rabbit URI not valid: " + rabbitUri);
        }
        this.scheme = uri.getScheme();
        String[] userInfo = uri.getUserInfo().split(":");
        this.user = userInfo[0];
        this.pwd = userInfo[1];
        this.host = uri.getHost();
        this.port = uri.getPort();
        this.path = uri.getPath();
    }

    public static Optional<String> getCloudAmqpUrlFromEnv() {
        if(System.getenv(CLOUDAMQP_URL) != null) {
            return Optional.of(System.getenv(CLOUDAMQP_URL));
        }
        return Optional.empty();
    }


    public String getScheme() {
        return scheme;
    }

    public String getUser() {
        return user;
    }

    public String getPwd() {
        return pwd;
    }

    public String getHost() {
        return host;
    }

    public Integer getPort() {
        if (port == -1) {
            return 5672;
        }
        return port;
    }

    public String getPath() {
        return path;
    }
}
