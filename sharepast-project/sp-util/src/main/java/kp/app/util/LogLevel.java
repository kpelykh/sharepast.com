package kp.app.util;

/**
 * Created by IntelliJ IDEA.
 * User: Kostya
 * Date: 7/18/11
 * Time: 8:57 PM
 * To change this template use File | Settings | File Templates.
 */
public enum LogLevel {
    DEBUG(LogLevelType.DEBUG,false), DEBUG_WITH_TRACE(LogLevelType.DEBUG,true)
    , INFO(LogLevelType.INFO,false), INFO_WITH_TRACE(LogLevelType.INFO,true)
    , WARN(LogLevelType.WARN,false), WARN_WITH_TRACE(LogLevelType.WARN,true)
    , ERROR(LogLevelType.ERROR,false), ERROR_WITH_TRACE(LogLevelType.ERROR,true)
  ;

    boolean showTrace = false;
    LogLevelType level;

    private LogLevel( LogLevelType level, boolean showTrace )
    {
      this.showTrace = showTrace;
      this.level = level;
    }

    public boolean isShowTrace()
    {
      return showTrace;
    }

    public LogLevelType getLevel()
    {
      return level;
    }
}


