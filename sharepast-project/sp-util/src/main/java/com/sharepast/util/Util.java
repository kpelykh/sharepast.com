package com.sharepast.util;

import com.sharepast.constants.LogonConstants;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.net.URLCodec;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.restlet.Request;
import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

    public static final String UTF8 = "UTF8";

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

    /**
     * hash provided array with SHA-256
     */
    public static String hashPassword(char[] password) {
        if (password == null || password.length < 1)
            return null;
        return hashPassword(new String(password));
    }

    /**
     * hash provided string with SHA-256
     */
    public static String hashPassword(String password) {
        if (password == null)
            return null;
        int len = password.length();
        StringBuilder sb = new StringBuilder(len + 16);
        sb.append(password);

        Sha256Hash hash = new Sha256Hash(sb.toString());
        return hash.toBase64();
    }

    /**
     * hash provided string with SHA-256
     */
    public static String hashPassword(String password, String salt) {
        if (password == null)
            return null;
        int len = password.length();
        StringBuilder sb = new StringBuilder(len + 16);
        sb.append(password);

        Sha256Hash hash = new Sha256Hash(sb.toString(), salt);
        return hash.toBase64();
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


    public static Reference form2reference(Request request, Form form) {
        String targetUri = form.getFirstValue(LogonConstants.LOGON_TARGET_URI_NAME, false);
        targetUri = Util.decodeFromUrl(targetUri);
        Reference ref = new Reference(request.getResourceRef(), targetUri);
        String origQuery = form.getFirstValue(LogonConstants.LOGON_TARGET_QUERY_NAME, false);
        if (!Util.isEmpty(origQuery)) {
            origQuery = Util.decodeFromUrl(origQuery);

            String[] qs = Util.chopString(origQuery, "&");
            if (!Util.isEmpty(qs))
                for (String q : qs) {
                    String[] qqs = Util.chopString(q, "=");
                    if (Util.isEmpty(qqs[0]))
                        continue;

                    ref.addQueryParameter(qqs[0], qqs.length > 1 ? Util.encodeForUrl(qqs[1]) : null);
                }
        }
        return ref;
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
}
