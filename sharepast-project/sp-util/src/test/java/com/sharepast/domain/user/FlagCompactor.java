package java.com.sharepast.domain.user;

import com.sharepast.persistence.Flags;
import org.hibernate.annotations.AccessType;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.io.Serializable;

@MappedSuperclass
@AccessType("property")
public abstract class FlagCompactor<F extends Enum & Flags> implements Serializable
{

  private StringBuilder compaction;

  public FlagCompactor()
  {
    init();
  }

	public FlagCompactor(String flags) {
		setCompaction(flags);
	}

  private void init()
  {
    F[] flags = getFlags();

    compaction = new StringBuilder( flags.length );
    for( Flags flag : flags )
      compaction.append( flag.getDefaultCode() );
  }

  @Transient
  public abstract F[] getFlags();

  @Column
  public synchronized String getCompaction()
  {
    return compaction.toString();
  }

  public synchronized void setCompaction( String compaction )
  {
    if( compaction == null || compaction.length() < 1 )
    {
      init();
      return;
    }

    F[] flags = getFlags();
    int len = compaction.length();
    int flen = flags.length;

    this.compaction.delete( 0, this.compaction.length() );
    this.compaction.append( flen < len ? compaction.substring( 0, flen ) : compaction );

    if( len < flen )
    {
      this.compaction.ensureCapacity( flen );

      for ( int i = len; i < flen; i++ )
        this.compaction.append( flags[i].getDefaultCode() );
    }

  }

  public boolean isSetTo( F flag, char singleChar )
  {
    return getFlag( flag ) == singleChar;
  }

  public synchronized char getFlag( F flag )
  {
    if( flag.ordinal() < compaction.length() )
      return compaction.charAt( flag.ordinal() );

    return flag.getDefaultCode();

  }

  public synchronized void setFlag( F flag, char singleChar )
  {
    if ( flag.ordinal() >= compaction.length() )
      setCompaction( compaction.toString() );

    compaction.setCharAt( flag.ordinal(), singleChar );
  }

  @Override
  public synchronized String toString()
  {
    StringBuilder sb = new StringBuilder( 100 );
    F[] flags = getFlags();
    int len = flags.length;
    String comma = "";

    sb.append( getClass().getName()+" [ " );
    for( int i=0; i<len; i++ )
    {
      sb.append( String.format( "%s%d: %s", comma, i, flags[i].name() ) );
      comma = ", ";
    }
    sb.append( " ]" );

    return sb.toString();
  }

}
