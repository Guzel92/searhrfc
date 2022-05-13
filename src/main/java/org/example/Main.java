package org.example;

import jakarta.servlet.Filter;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11NioProtocol;
import org.apache.coyote.http2.Http2Protocol;
import org.apache.tomcat.util.descriptor.web.ContextResource;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import org.apache.tomcat.util.net.SSLHostConfig;
import org.apache.tomcat.util.net.SSLHostConfigCertificate;
import org.example.filter.AnonymousAuthenticationFilter;
import org.example.filter.BearerAuthenticationFilter;
import org.example.filter.X509AuthenticationFilter;
import org.example.listener.ContextLoadDestroyListener;
import org.example.server.Server;
import org.example.servlet.DemoServlet;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;

public class Main {
    public static void main(String[] args) throws LifecycleException, IOException, ExecutionException, InterruptedException {
        final Server server = new Server();

        server.setPort(9999);

        final Http11NioProtocol protocol = new Http11NioProtocol();
        final Connector connector = new Connector(protocol);
        connector.setPort(9999);

        protocol.setMaxThreads(150);
        protocol.setSSLEnabled(true);
        connector.addUpgradeProtocol(new Http2Protocol());

        final SSLHostConfig sslHostConfig = new SSLHostConfig();
        sslHostConfig.setProtocols("TLSv1.3");
        sslHostConfig.setCertificateVerificationAsString("optional");
        sslHostConfig.setTruststoreFile("truststore.jks");
        sslHostConfig.setTruststorePassword("passphrase");
        connector.addSslHostConfig(sslHostConfig);

        final SSLHostConfigCertificate certificate = new SSLHostConfigCertificate(sslHostConfig, SSLHostConfigCertificate.Type.RSA);
        certificate.setCertificateKeystoreFile("server.jks");
        certificate.setCertificateKeystorePassword("passphrase");
        sslHostConfig.addCertificate(certificate);

        server.setConnector(connector);

        final Context context = server.createContext("",
                Files.createDirectories(Paths.get("static"))
                        .toFile()
                        .getAbsolutePath()
        );

        final ContextResource db = new ContextResource();
        db.setName("jdbc/db");
        db.setAuth("Container");
        db.setType(DataSource.class.getName());
        db.setProperty("url", "jdbc:postgresql://localhost:5432/db?user=app&password=pass");
        db.setProperty("maxTotal", "20");
        db.setProperty("maxIdle", "10");
        db.setCloseMethod("close");
        context.getNamingResources().addResource(db);

        context.addServletContainerInitializer(
                (c, ctx) -> {
                    ctx.setInitParameter("basePackage", "org.example");
                    ctx.addListener(new ContextLoadDestroyListener());
                },
                null
        );

        // TODO: добавьте сюда ваши фильтры
        registerFilter(context, new X509AuthenticationFilter(), "x509");
        registerFilter(context, new BearerAuthenticationFilter(), "bearer");
        //registerFilter(context, new BearerAuthenticationFilter(), "basic");
        registerFilter(context, new AnonymousAuthenticationFilter(), "anon");

        // регистрация сервлета
        final Wrapper wrapper = context.createWrapper();
        wrapper.setServlet(new DemoServlet());
        wrapper.setName("front");
        wrapper.setLoadOnStartup(1);

        context.addChild(wrapper);
        context.addServletMappingDecoded("/", wrapper.getName());

        server.start();
    }

    private static void registerFilter(Context context, Filter filter, String name) {
        final FilterDef filterDef = new FilterDef();
        filterDef.setFilter(filter);
        filterDef.setFilterName(name);
        context.addFilterDef(filterDef);

        final FilterMap filterMap = new FilterMap();
        filterMap.setFilterName(filterDef.getFilterName());
        filterMap.addURLPatternDecoded("/*");
        context.addFilterMap(filterMap);
    }

    //final ExecutorService fixedThreadPool = Executors.newFixedThreadPool(64);
//Files.walkFileTree(Paths.get("RFC"),new FilesVisitResults)


}
