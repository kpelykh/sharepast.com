package kp.app.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Konstantin
 * Date: Nov 19, 2008
 * Time: 2:21:37 PM
 *
 * 26-character Base32 Guid (Globally Unique IDentifier) implementation.
 */
public final class Uuid
{
// what's this for?
	public static final String CLASS_NAME = Uuid.class.getName();

	private static final Object g_mutex = new Object();
	private static final Map g_uuids = new java.util.HashMap(8096);

	private static long g_lastTimeMillis = System.currentTimeMillis();
	private static int g_lastSequence = 0;

	private static final int g_localHostAddress;
	static
	{
		try
		{
			InetAddress localHost = InetAddress.getLocalHost();
			byte addressBytes[]   = localHost.getAddress();

			int addressInt  = addressBytes[ 3 ] & 0xFF;
			addressInt |= ( ( addressBytes[ 2 ] << 8 ) & 0xFF00 );
			addressInt |= ( ( addressBytes[ 1 ] << 16 ) & 0xFF0000 );
			addressInt |= ( ( addressBytes[ 0 ] << 24 ) & 0xFF000000 );

			g_localHostAddress = addressInt;
		}
		catch( UnknownHostException e )
		{
			throw new ExceptionInInitializerError( e );
		}
	}

    public static String create() {
        return toLowerCaseString(nextGuidString());
    }

	/**
	 * @return cached Uuid count
	 */
	public static int count()
	{
		return g_uuids.size();
	}

	/**
	 * checks if given Uuid is valid, trying to extract Sequence, Mills, AddressBytes from this Uuid
	 * Invalid Uuid can appear when it was built using Uuid.get(id) with invalid 'id' string
	 * e.g. Uuid.get("_new_pid_") returns invalid Uuid
	 * @param uuid
	 * @return true/false if Uuid is Valid/Invalid
	 */
	public static boolean isValid(Uuid uuid)
	{
		try {
			uuid.getSequence();
			uuid.getMillis();
			uuid.getAddressBytes();
			return true;
		} catch (IllegalStateException e){}
		return false;
	}

	private static String nextGuidString()
	{
		long now      = System.currentTimeMillis();
		int  sequence = 0;

		synchronized( g_mutex )
		{
			if( now == g_lastTimeMillis )
				g_lastSequence++;
			else
			{
				g_lastTimeMillis = now;
				g_lastSequence   = 0;
			}

			now      = g_lastTimeMillis;
			sequence = g_lastSequence;
		}

		return toBase32String( g_localHostAddress, now, sequence );
	}

	private static String toBase32String( int address, long millis, int sequence )
	{
		final char[] c = new char[ 26 ];

		c[ 6 ]  = StringUtilities.toBase32Char( ( ( address & 0x3 ) << 3 ) | (int) ( ( millis >> 61 ) & 0x7 ) );
		c[ 19 ] = StringUtilities.toBase32Char( ( ( (int) ( millis & 0x1 ) ) << 4 ) | ( ( sequence >> 28 ) & 0xF ) );
		c[ 25 ] = StringUtilities.toBase32Char( ( sequence & 0x7 ) << 2 );

		address = address >> 2;
		for( int i = 5; i > -1; i-- )
		{
			c[ i ] = StringUtilities.toBase32Char( address & 0x1F );
			address = address >> 5;
		}

		millis = millis >> 1;
		for( int i = 18; i > 6; i-- )
		{
			c[ i ] = StringUtilities.toBase32Char((int) (millis & 0x1F));
			millis = millis >> 5;
		}

		sequence = sequence >> 3;
		for( int i = 24; i > 19; i-- )
		{
			c[ i ] = StringUtilities.toBase32Char( sequence & 0x1F );
			sequence = sequence >> 5;
		}

		return new String( c );
	}

   
	/**
	 * Returns the IP adrress of this Uuid.
	 */
	public byte[] getAddressBytes()
	{
		final String value = toString();
		if( value.length() == 22 )
		{
			byte[] bytes = new byte[ 4 ];

			String base64 = value.substring( 0, 6 );

			bytes[ 3 ] = StringUtilities.BYTE_FROM_URL_SAFE_BASE64_CHAR[ base64.charAt( 5 ) ];
			bytes[ 3 ] |= StringUtilities.BYTE_FROM_URL_SAFE_BASE64_CHAR[ base64.charAt( 4 ) ] << 6;

			bytes[ 2 ] = (byte) ( StringUtilities.BYTE_FROM_URL_SAFE_BASE64_CHAR[ base64.charAt( 4 ) ] >> 2 );
			bytes[ 2 ] |= StringUtilities.BYTE_FROM_URL_SAFE_BASE64_CHAR[ base64.charAt( 3 ) ] << 4;

			bytes[ 1 ] = (byte) (StringUtilities.BYTE_FROM_URL_SAFE_BASE64_CHAR[ base64.charAt( 3 ) ] >> 4 );
			bytes[ 1 ] |= StringUtilities.BYTE_FROM_URL_SAFE_BASE64_CHAR[ base64.charAt( 2 ) ] << 2;

			bytes[ 0 ] = StringUtilities.BYTE_FROM_URL_SAFE_BASE64_CHAR[ base64.charAt( 1 ) ];
			bytes[ 0 ] |= StringUtilities.BYTE_FROM_URL_SAFE_BASE64_CHAR[ base64.charAt( 2 ) ] << 6;

			return bytes;
		}
		else if( value.length() == 26 )
		{
			final String s = value.substring( 0, 7 );

			final byte[] b = new byte[ 4 ];

			b[ 0 ]  = (byte) ( StringUtilities.byteFromBase32Char( s.charAt( 0 ) ) << 3 );
			b[ 0 ] |= StringUtilities.byteFromBase32Char( s.charAt( 1 ) ) >> 2;

			b[ 1 ]  = (byte) ( ( StringUtilities.byteFromBase32Char( s.charAt( 1 ) ) & 0x3 ) << 6 );
			b[ 1 ] |= StringUtilities.byteFromBase32Char( s.charAt( 2 ) ) << 1;
			b[ 1 ] |= ( StringUtilities.byteFromBase32Char( s.charAt( 3 ) ) >> 4 ) & 0x1;

			b[ 2 ]  = (byte) ( ( StringUtilities.byteFromBase32Char( s.charAt( 3 ) ) & 0xF ) << 4 );
			b[ 2 ] |= StringUtilities.byteFromBase32Char( s.charAt( 4 ) ) >> 1;

			b[ 3 ]  = (byte) ( ( StringUtilities.byteFromBase32Char( s.charAt( 4 ) ) & 0x1 ) << 7 );
			b[ 3 ] |= StringUtilities.byteFromBase32Char( s.charAt( 5 ) ) << 2;
			b[ 3 ] |= ( StringUtilities.byteFromBase32Char( s.charAt( 6 ) ) >> 3 ) & 0x3;

			return b;
		}
		else
			throw new IllegalStateException( "Uuid::getAddressBytes>Urecognized encoding for Uuid: '" + value + "'." );
	}

	/**
	 * Returns this Uuid's creation time in milliseconds.
	 */
	public long getMillis()
	{
		long l = 0;

		final String value = toString();
		if( value.length() == 22 )
		{
			String s = value.substring( 6, 17 );
			for( int i = 0; i < 10; i++ )
			{
				l |= StringUtilities.BYTE_FROM_URL_SAFE_BASE64_CHAR[ s.charAt( i ) ];
				l = l << 6;
			}

			l = l >> 2;
			l |= StringUtilities.BYTE_FROM_URL_SAFE_BASE64_CHAR[ s.charAt( 10 ) ] & 0x3C;
		}
		else if( value.length() == 26 )
		{
			l = (long) ( StringUtilities.byteFromBase32Char( value.charAt( 6 ) ) & 0x7 );

			for( int i = 7; i < 19; i++ )
			{
				l  = l << 5;
				l |= StringUtilities.byteFromBase32Char( value.charAt( i ) );
			}

			l  = l << 1;
			l |= ( StringUtilities.byteFromBase32Char( value.charAt( 19 ) ) >> 4 ) & 0x1;
		}
		else
			throw new IllegalStateException( "Uuid::getMillis>Unrecognized encoding for Uuid: '" + value + "'." );

		return l;
	}

	/**
	 * Returns this Uuid's sequence number.
	 */
	public int getSequence()
	{
		int seq = 0;

		final String value = toString();
		if( value.length() == 22 )
		{
			String s = value.substring( 16 );
			for( int i = 0; i < 5; i++ )
			{
				seq |= StringUtilities.BYTE_FROM_URL_SAFE_BASE64_CHAR[ s.charAt( i ) ];
				seq = seq << 6;
			}

			seq = seq >> 4;
			seq |= StringUtilities.BYTE_FROM_URL_SAFE_BASE64_CHAR[ s.charAt( 5 ) ] & 0x03;
		}
		else if( value.length() == 26 )
		{
			seq = StringUtilities.byteFromBase32Char( value.charAt( 19 ) ) & 0xF;
			for( int i = 20; i < 25; i++ )
			{
				seq  = seq << 5;
				seq |= StringUtilities.byteFromBase32Char( value.charAt( i ) );
			}

			seq  = seq << 3;
			seq |= StringUtilities.byteFromBase32Char( value.charAt( 25 ) ) >> 2;
		}
		else
			throw new IllegalStateException( "Uuid::getSequence>Unrecognized encoding for Uuid: '" + value + "'." );

		return seq;
	}

	/**
	 * Returns a lower-case representation of this Uuid
	 * where uppercase letters [A-Z] are lowercased and
	 * prefixed with a '-'.
	 * @return NOT NULL	
	 */
	public static String toLowerCaseString(String str)
	{
		final char   escape = '-';
		final String value  = str;

		final StringBuffer buffer = new StringBuffer( 31 );
		for( int i = 0; i < value.length(); i++ )
		{
			char c = value.charAt( i );
			if( Character.isUpperCase( c ) )
			{
				buffer.append( escape );
				buffer.append( Character.toLowerCase( c ) );
			}
			else
				buffer.append( c );
		}
		return buffer.toString();
	}

    public static void main(String args[]) {
        System.out.println(Uuid.create()); 
    }

}
