package org.example.server;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;

public class Server {
    private final Tomcat tomcat = new Tomcat();

    public Server() {
        tomcat.enableNaming(); // включает поддержку JNDI (по умолчанию отключено)
    }

    public void setPort(final int port) {
        tomcat.setPort(port);
    }

    public void setConnector(final Connector connector) {
        tomcat.setConnector(connector);
    }

    public Context createContext(final String prefix, final String path) {
        return tomcat.addContext(prefix, path);
    }

    public void start() throws LifecycleException {
        tomcat.start();
    }
}
