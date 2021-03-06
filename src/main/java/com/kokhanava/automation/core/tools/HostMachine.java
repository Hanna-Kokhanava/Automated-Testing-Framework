package com.kokhanava.automation.core.tools;

import com.kokhanava.automation.core.tools.commands.CommandExecutor;
import org.apache.http.client.utils.URIBuilder;

import javax.xml.bind.annotation.XmlAttribute;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created on 01.04.2018
 */
public class HostMachine {
    /**
     * [username:password@]hostname:port
     */
    private final static String URI_PATTERN = "^(?:(.+):(.+)@)?(.+):(\\d+)$";

    private String username;
    private String password;
    private String hostname;
    private String port;
    private OS os;
    private boolean isRemote;

    public HostMachine() {
    }

    /**
     * Instantiates a new hostname.
     *
     * @param driverUrl the driver url in format:<br>
     *                  <code>username:password@hostname:port</code>
     */
    public HostMachine(String driverUrl) {
        Pattern p = Pattern.compile(URI_PATTERN);
        Matcher m = p.matcher(driverUrl);
        if (m.find()) {
            setUsername(m.group(1));
            setPassword(m.group(2));
            setHostname(m.group(3));
            setPort(port = m.group(4));
        }
    }

    public String getUsername() {
        return username;
    }

    @XmlAttribute(name = "username")
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    @XmlAttribute(name = "password")
    public void setPassword(String password) {
        this.password = password;
    }

    public String getHostname() {
        return hostname;
    }

    @XmlAttribute(name = "hostname")
    public void setHostname(String hostname) {
        String hostNameOfLocalhost = CommandExecutor.getHostNameOfLocalhost();

        if (!(isRemote = !(hostname.equalsIgnoreCase("localhost")
                || hostname.equals("127.0.0.1")
                || hostname.equals("0.0.0.0")))) {
            this.hostname = hostname;
        } else {
            isRemote = !hostNameOfLocalhost.split("\\.")[0]
                    .equalsIgnoreCase(hostname.split("\\..")[0]);
            this.hostname = isRemote ? hostname : hostNameOfLocalhost;
        }
    }

    public String getPort() {
        return port;
    }

    @XmlAttribute(name = "port")
    public void setPort(String port) {
        this.port = port;
    }

    /**
     * Returns {@link OS} of host machine
     *
     * @return {@link OS} entity
     */
    public OS getOs() {
        if (Objects.isNull(os) && null == (os = CommandExecutor.getOsOfMachine(this))) {
            throw new RuntimeException("OS of the host machine was not determined");
        }
        return os;
    }

    /**
     * Checks if {@link OS} of current Host machine is the same as the following parameter
     *
     * @param os {@link OS}
     * @return true if host machine has the same OS as provided
     */
    public boolean hasOS(OS os) {
        return getOs().equals(os);
    }

    public int getPortInt() {
        return Integer.parseInt(port);
    }

    public boolean isRemote() {
        return isRemote;
    }

    public char getFileSeparator() {
        return getOs().getFileSeparator();
    }

    public URIBuilder getURIBuilder(String uriScheme) {
        URIBuilder uriBuilder = new URIBuilder().setScheme(uriScheme);
        uriBuilder.setHost(getHostname());

        return uriScheme.equals("https")
                ? uriBuilder.setUserInfo(username, password)
                : uriBuilder.setPort(getPortInt());
    }

    public URIBuilder getURIBuilder() {
        return getURIBuilder("http");
    }

    @Override
    public String toString() {
        return String.format("[username=%s, password=%s, hostname=%s, port=%s]",
                getUsername(),
                getPassword(),
                getHostname(),
                getPort()
        );
    }
}
