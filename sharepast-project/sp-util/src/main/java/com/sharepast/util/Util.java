package com.sharepast.util;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.net.URLCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.security.SecureRandom;
import java.text.Normalizer;
import java.util.Collection;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Created by IntelliJ IDEA.
 * User: Kostya
 * Date: 1/14/11
 * Time: 2:00 AM
 * To change this template use File | Settings | File Templates.
 */
public class Util {

    private static final Logger LOG = LoggerFactory.getLogger(Util.class);

    private static final int MIN_PORT_NUMBER = 1024;
    private static final int MAX_PORT_NUMBER = 65535;

    /**
     * a very useful secure codec
     */
    private static URLCodec URL_CODEC = new URLCodec();

    public static boolean isEmpty(Collection o) {
        return o == null || o.isEmpty();
    }

    public static final boolean isEmpty(String o) {
        return o == null || o.length() < 1;
    }

    public static final boolean isEmpty(File o) {
        return o == null || !o.exists() || o.length() < 1L;
    }

    public static final boolean isEmpty(Object[] o) {
        return o == null || o.length < 1;
    }

    @SuppressWarnings("unchecked")
    public static final boolean isEmpty(Map o) {
        return o == null || o.isEmpty();
    }

    public static final boolean isEmpty(Object o) {
        return o == null;
    }

    /**
     * find ramdom free port in 1024 attempts
     */
    public static int findFreeRandomPort()
            throws IOException {
        return findFreeRandomPort(1024);
    }

    public static int findFreeRandomPort(int nAttempts)
            throws IOException {
        int port;
        int maxPort = 65537 - 1025;
        SecureRandom sr = new SecureRandom();
        if (!OSEnum.Linux.equals(getOS()))
            sr.setSeed(sr.generateSeed(128));
        ServerSocket socket = new ServerSocket();
        InetSocketAddress sa;
        for (int i = 0; i < nAttempts; i++) {
            port = sr.nextInt(maxPort) + 1024;
            sa = new InetSocketAddress(port);
            try {
                socket.bind(sa);
                if (socket.isBound())
                    socket.close();
                return port;
            } catch (Exception e) {
            }
        }
        throw new IllegalStateException("No valid open port could be located");
    }

    public static OSEnum getOS() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.startsWith("linux"))
            return OSEnum.Linux;
        if (os.startsWith("mac"))
            return OSEnum.OSX;
        if (os.startsWith("windows"))
            return OSEnum.Windows;
        return null;
    }

    public static final String slugify(String s) {
        if (s == null)
            return null;

        if (s.isEmpty())
            return s;

        String result = Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
        result = result.replace(" ", "-");
        result = result.toLowerCase();

        return result;
    }

    /**
     * encode string to safely use in HTTP GET
     */
    public static final String encodeForUrl(String s) {
        if (isEmpty(s))
            return s;

        try {
            return URL_CODEC.encode(s);
        } catch (EncoderException e) {
            String msg = String.format("cannot decode ", s, e.getMessage());
            LOG.error(msg, e);
            throw new RuntimeException(msg);
        }
    }

    /**
     * encode string to safely use in HTTP GET
     */
    public static final String decodeFromUrl(String s) {
        if (isEmpty(s))
            return s;

        try {
            return URL_CODEC.decode(s);
        } catch (DecoderException e) {
            String msg = String.format("cannot decode ", s, e.getMessage());
            LOG.error(msg, e);
            throw new RuntimeException(msg);
        }
    }

    /**
     * split string into a string array based on supplied delimiters
     */
    public static String[] chopString(final String str, final String delims) {
        if (isEmpty(str))
            return null;
        StringTokenizer st = new StringTokenizer(str, delims);
        int count = st.countTokens();
        String[] to = new String[count];
        for (int i = 0; st.hasMoreTokens(); i++)
            to[i] = st.nextToken();
        return to;
    }

    public static long resourceToFile( String resource, String fileName, boolean deleteOnExit )
      throws IOException
      {
        long sz = -1L;

        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream( resource );
        if( is == null )
        {
          LOG.warn( String.format("cannot open resource %s", resource) );
          return sz;
        }

        File fout = new File( fileName );

        FileOutputStream fos = new FileOutputStream( fout );

        byte [] buf = new byte[10240];
        int n = -1;
        while( (n = is.read( buf ) ) > 0 )
        {
          fos.write( buf, 0, n );
          sz += n;
        }

        fos.flush();
        fos.close();

        is.close();

        if( deleteOnExit )
          fout.deleteOnExit();

        LOG.warn( String.format( "copied %s to %s", resource, fout.getAbsolutePath() ) );

        return sz;
      }

    /**
     * Checks to see if a specific port is available.
     *
     * @param port the port to check for availability
     */
    public static boolean isPortAvailable(int port) {
        if (port < MIN_PORT_NUMBER || port > MAX_PORT_NUMBER) {
            throw new IllegalArgumentException("Invalid start port: " + port);
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
