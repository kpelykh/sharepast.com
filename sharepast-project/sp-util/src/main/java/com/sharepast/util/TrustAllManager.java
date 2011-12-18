package com.sharepast.util;

import org.slf4j.Logger;

import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

/**
 * Created by IntelliJ IDEA.
 * User: Kostya
 * Date: 4/19/11
 * Time: 1:36 AM
 * To change this template use File | Settings | File Templates.
 */
public final class TrustAllManager
  implements X509TrustManager
{
  private static final Logger LOG = org.slf4j.LoggerFactory.getLogger( TrustAllManager.class );

  public TrustAllManager()
  {
  }

  /**
   * Method called on the server-side for establishing trust with a client. See API documentation for
   * javax.net.ssl.X509TrustManager.
   */
  public void checkClientTrusted( X509Certificate[] chain, String authType )
    throws java.security.cert.CertificateException
  {
    for ( int j = 0; j < chain.length; j++ )
    {
      System.out.println( "Client certificate information:" );
      System.out.println( "  Subject DN: " + chain[j].getSubjectDN() );
      System.out.println( "  Issuer DN: " + chain[j].getIssuerDN() );
      System.out.println( "  Serial number: " + chain[j].getSerialNumber() );
      System.out.println( "" );
    }

  }

  /**
   * Method called on the client-side for establishing trust with a server. See API documentation for
   * javax.net.ssl.X509TrustManager.
   */
  public void checkServerTrusted( X509Certificate[] chain, String authType )
    throws java.security.cert.CertificateException
  {
    for ( int j = 0; j < chain.length; j++ )
    {
      System.out.println( "Server certificate information:" );
      System.out.println( "  Subject DN: " + chain[j].getSubjectDN() );
      System.out.println( "  Issuer DN: " + chain[j].getIssuerDN() );
      System.out.println( "  Serial number: " + chain[j].getSerialNumber() );
      System.out.println( "" );
    }
  }

  public X509Certificate[] getAcceptedIssuers()
  {
    return null;
  }


}