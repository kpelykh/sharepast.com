package com.sharepast.commons.spring.web;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

/**
 * Created by IntelliJ IDEA.
 * User: kpelykh
 * Date: 2/15/12
 * Time: 11:47 PM
 * To change this template use File | Settings | File Templates.
 */
@Component
public class HttpConfigs implements InitializingBean {

    private @Value("${http.host}") String httpHost;
    private @Value("${http.port}") Integer httpPort;
    private @Value("${https.port}") Integer sslport;

    private @Value("${keystore.pass:}") String keyStorePassword;
    private @Value("${keystore.manager.pass:}") String keyStoreManagerPassword;
    private @Value("${keystore.path:file:}") Resource keyStoreResource;

    private @Value("${truststore.pass:}") String trustStorePassword;
    private @Value("${truststore.path:file:}") Resource trustStoreResource;

    private @Value("${container.jar.pattern:.*/.*jsp-api-[^/]*\\.jar$|.*/.*jsp-[^/]*\\.jar$|.*/.*taglibs[^/]*\\.jar|.*zts.*$}") String containerJarPattern;

    private KeyStore keystore;
    private KeyStore trustStore;

    private @Value("${ssl.enabled:false}") Boolean enableSSL;

    private @Value("${jetty.resource.base:}") Resource resourceBase;
    private @Value("${jetty.web.default:org/eclipse/jetty/webapp/webdefault.xml}") FileSystemResource webDefault;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (enableSSL) {
            try {
                keystore = KeyStore.getInstance(KeyStore.getDefaultType());
                keystore.load(keyStoreResource.getInputStream(), keyStorePassword.toCharArray());

                trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
                trustStore.load(trustStoreResource.getInputStream(), trustStorePassword.toCharArray());
            } catch (KeyStoreException e) {
                throw new IllegalStateException(e);
            } catch (CertificateException e) {
                throw new IllegalStateException(e);
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalStateException(e);
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }

        validate();
    }


    // Getters & Setters

    public String getHttpHost() {
        return httpHost;
    }

    public Integer getHttpPort() {
        return httpPort;
    }

    public Integer getSslport() {
        return sslport;
    }

    public KeyStore getKeystore() {
        return keystore;
    }

    public KeyStore getTrustStore() {
        return trustStore;
    }

    public Boolean isSSLEnabled() {
        return enableSSL;
    }

    public String getTrustStorePassword() {
        return trustStorePassword;
    }

    public String getContainerJarPattern() {
        return containerJarPattern;
    }

    public String getKeyStorePassword() {
        return keyStorePassword;
    }

    public String getKeyStoreManagerPassword() {
        return keyStoreManagerPassword;
    }

    public String getResourceBase() {
        try {
            return resourceBase.getURL().toString();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public String getWebDefault() {
        try {
            return webDefault.getFile().getCanonicalPath();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public void validate() {
        if (getHttpPort() == null && getSslport() == null) {
            throw new IllegalStateException(
                    "you need to provide property 'http.port' and/or 'https.port'");
        }

        if (!available(httpPort)) {
            throw new IllegalStateException("httpPort :" + httpPort
                    + " already in use!");
        }

        if (isSSLEnabled() && getSslport() != null) {
            if (!available(sslport)) {
                throw new IllegalStateException("SSL httpPort :" + sslport
                        + " already in use!");
            }
        }
    }

    private static boolean available(int port) {
        if (port <= 0) {
            throw new IllegalArgumentException("Invalid start httpPort: " + port);
        }

        ServerSocket ss = null;
        DatagramSocket ds = null;
        try {
            ss = new ServerSocket(port);
            ss.setReuseAddress(true);
            ds = new DatagramSocket(port);
            ds.setReuseAddress(true);
            return true;
        } catch (IOException e) {
        } finally {
            if (ds != null) {
                ds.close();
            }

            if (ss != null) {
                try {
                    ss.close();
                } catch (IOException e) {
                    /* should not be thrown */
                }
            }
        }

        return false;
    }

}
