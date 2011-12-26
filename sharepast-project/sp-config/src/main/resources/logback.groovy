import static ch.qos.logback.classic.Level.TRACE;
import static ch.qos.logback.classic.Level.DEBUG;
import static ch.qos.logback.classic.Level.INFO;
import static ch.qos.logback.classic.Level.WARN;
import static ch.qos.logback.classic.Level.ERROR;
import static ch.qos.logback.classic.Level.ALL;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.filter.ThresholdFilter;
import ch.qos.logback.classic.html.HTMLLayout
import ch.qos.logback.core.encoder.LayoutWrappingEncoder
import ch.qos.logback.core.ConsoleAppender;

import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy

def userHome = System.getProperty( 'user.home' )

def env      = System.getProperty( 'app.env', 'de' )
def logDir   = System.getProperty( 'log.dir.rel', '/app/sharepast.com/logs' )

def pool     = System.getProperty( AppConstants.SYSTEM_PROPERTY_POOL , 'app' )
def server   = System.getProperty( AppConstants.SYSTEM_PROPERTY_SERVER_ID, '001' )

def logLevel = 'log.properties'

def logLevelLocations = [ "$logLevel", "$env/$logLevel", "$env/$pool/$logLevel", "$env/$pool/$server/$logLevel", "$userHome/.m2/$logLevel" ]

def filePattern    = '%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n'
def htmlPattern    = '%d{HH:mm:ss.SSS}%thread%mdc%level%logger%msg'
def errorPattern   = '%d{HH:mm:ss.SSS} [%thread] %-5level %class{40}:%line - %msg%n%throwable'
def consolePattern = '%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n'
def defaultAppenders = ['all','err','errh']
def defaultRootAppenders = ['console','all','err','errh']

//  ---  files  -----------
appender( 'all', RollingFileAppender )
{
  file = "${logDir}/all-${pool}-${server}.txt"
  rollingPolicy(TimeBasedRollingPolicy) { fileNamePattern = "${logDir}/all-${pool}-${server}-%d{yyyy-MM-dd}.txt" }
  encoder(PatternLayoutEncoder) { pattern = filePattern }
}

appender( "err", RollingFileAppender )
{
  filter(ThresholdFilter) { level = ERROR }
  file = "${logDir}/err-${pool}-${server}.txt"
  rollingPolicy(TimeBasedRollingPolicy) { fileNamePattern = "${logDir}/err-${pool}-${server}-%d{yyyy-MM-dd}.txt" }
  encoder(PatternLayoutEncoder) { pattern = errorPattern }
}

appender( "errh", RollingFileAppender )
{
  filter(ThresholdFilter) { level = WARN }
  file = "${logDir}/err-${pool}-${server}.html"
  rollingPolicy(TimeBasedRollingPolicy) { fileNamePattern = "${logDir}/err-${pool}-${server}-%d{yyyy-MM-dd}.html" }
  encoder(LayoutWrappingEncoder) { layout(HTMLLayout) {pattern = htmlPattern}}
}

appender( "activemq", RollingFileAppender )
{
  filter(ThresholdFilter) { level = WARN }
  file = "${logDir}/activemq-${pool}-${server}.txt"
  rollingPolicy(TimeBasedRollingPolicy) { fileNamePattern = "${logDir}/activemq-${pool}-${server}-%d{yyyy-MM-dd}.txt" }
  encoder(PatternLayoutEncoder) { pattern = errorPattern }
}

appender( "email", RollingFileAppender )
{
  file = "${logDir}/email-${pool}-${server}.txt"
  rollingPolicy(TimeBasedRollingPolicy) { fileNamePattern = "${logDir}/email-${pool}-${server}-%d{yyyy-MM-dd}.txt" }
  encoder(PatternLayoutEncoder) { pattern = filePattern }
}

appender( "console", ConsoleAppender )
{
  encoder(PatternLayoutEncoder) { pattern = consolePattern }
}

ClassLoader cl = getClass().getClassLoader()

// load system-wide default logging levels
Properties levels = new Properties()

println '\n============== Logging configuration =============='

levels = [:]

def verb = 'reading'
def aj   = 'from'

logLevelLocations.each
{ loc ->
    InputStream is = cl.getResourceAsStream(loc)

    if( is )
    {
      println "$verb configuration $aj ${cl.getResource(loc)} "

      Properties envLevels = new Properties()
      envLevels.load( is )

      levels.putAll(envLevels)

      verb = 'overriding'
      aj   = 'with'
    }
}

println '-------------------- final log levels --------------------'

boolean rootNotFound = true

StringBuffer julHacks = new StringBuffer( 256 )

// create loggers
levels.each
{ k,v ->

  if( !k || k.length() < 1 )
    return

  def appenders = []
  String [] parts = v.split()
  def lev = Level.toLevel( parts[0] )

  if( parts.length > 1 )
    for( int i=1; i<parts.length; i++ )
      appenders += parts[i]
  else
    appenders = defaultAppenders

  if( 'root' == k )
  {
    def finalAppenders = parts.length == 1 ? defaultRootAppenders : appenders
    root( lev, finalAppenders )
    println "===============> root logger ==> $v $finalAppenders"

    rootNotFound = false
  }
  else
  {
    logger( k, lev, appenders )
    println "$k --> $lev $appenders"
  }

  String julLev = 'ERROR'

  switch( lev )
  {
    case TRACE:
      julLev = 'FINEST'
      break;

    case DEBUG:
      julLev = 'FINE'
      break;

    case INFO:
      julLev = 'INFO'
      break;

    case WARN:
      julLev = 'WARNING'
      break;

    case ERROR:
      julLev = 'SEVERE'
      break;

    case ALL:
      julLev = 'ALL'
      break;
  }
  julHacks.append( "$k=$julLev\n" as String )
}

if( rootNotFound )
{
  // create default root logger in case we don't find anything
  Level rootLevel = 'pr' == env ? WARN : DEBUG
  root( rootLevel, defaultRootAppenders )
  println "root logger ==> $rootLevel $defaultRootAppenders"
}

def julConfig =
"""
# Specify the handlers to create in the root logger
handlers = java.util.logging.ConsoleHandler
#, java.util.logging.FileHandler

# Set the default logging level for the root logger
.level = SEVERE

# Set the default logging level for new ConsoleHandler instances
java.util.logging.ConsoleHandler.level = SEVERE

# Set the default logging level for new FileHandler instances
java.util.logging.FileHandler.level = SEVERE

# Set the default formatter for new ConsoleHandler instances
java.util.logging.ConsoleHandler.formatter = java.util.logging.SimpleFormatter

# Set the default logging level for the loggers
$julHacks
"""

java.util.logging.LogManager.getLogManager().readConfiguration( new java.io.ByteArrayInputStream( (julConfig as String).bytes ) )

println "\n-------> java.util.logging configured as below:\n$julHacks <-------- jul"

println '===================== logging configured =====================\n'

