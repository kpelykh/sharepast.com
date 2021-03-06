#!/usr/bin/env groovy

import java.text.DateFormat
import java.text.SimpleDateFormat

/**
 * this script prepares current folder for running Bizzy Apps. Presuming that jsw is a subfolder of current
 **
 */
class Prep
{
  static String rtConfig = 'runtime-config'

  static String keyEnv = 'pr'
  static String targetEnv = 'pr'

  static String env = System.getenv('APP_ENV') ? System.getenv('APP_ENV') : targetEnv

  static String tcHome

  static String versionHome
  static String optSearch

  static String optShare
  static String optVersion
  static String optArea

  static DateFormat FMT_IN = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
  static DateFormat FMT_OUT = new SimpleDateFormat("yyyyMMddHHmmss");

  static main( String [] args )
  {
    if( !args || args.length < 3 )
      throw new Exception("usage: Prep.groovy SHARE_LOCATION CURRENT_VERSION ENV, instead got ${args}")

    optShare    = args[0]
    optVersion  = args[1]
    optArea     = args[2]

    versionHome = "${optShare}/code/${optVersion}"

    def rep = [
      'set\\.default\\.APP_VERSION=.*':"set.default.APP_VERSION=${optVersion}"
    , 'set\\.APP_VERSION=.*':"set.default.APP_VERSION=${optVersion}"
    , 'set\\.default\\.APP_VERSION_DIR=.*':"set.default.APP_VERSION_DIR=${versionHome}"
    , 'set\\.APP_VERSION_DIR=.*':"set.default.APP_VERSION_DIR=${versionHome}"
    , 'set\\.default\\.APP_LOG_DIR=.*':"set.default.APP_LOG_DIR=${optShare}/logs"
    , 'set\\.APP_LOG_DIR=.*':"set.default.APP_LOG_DIR=${optShare}/logs"
    , 'set\\.default\\.APP_SHARE=.*':"set.default.APP_SHARE=${optShare}"
    , 'set\\.APP_SHARE=.*':"set.APP_SHARE=${optShare}"
    , 'set\\.default\\.APP_ENV=.*':"set.default.APP_ENV=${optArea}"
    , 'set\\.APP_ENV=.*':"set.APP_ENV=${optArea}"
                    ]

    //['app','scheduler','search'].each
    "ln -s jsw/linux-x86-64/${it} ${it}".execute().waitForProcessOutput()
    replace( new File(versionHome, "config/${it}.conf"), rep )
    println "linked and reconfigured ${it} ..."


    configure()

    println "Done"
  }

  static void backupVersion()
  {
    def ant = new AntBuilder()

    ant.copy( todir:new File(System.getProperty('user.home'),'bz'), overwrite:true )
    {
      fileset( dir:new File( "${versionHome}" ) )
      {
        include( name:'**' )
      }
    }
  }

  static void configure()
  {
    // delete current keyfile
    File keyFile = new File( versionHome, "config/${keyEnv}/keys.jks" )

    if( keyFile.exists() )
      keyFile.delete()

    keyFile = new File( "${optShare}/code", "${rtConfig}/keys.jks" )
    if( !keyFile.exists() )
    {
      println "cannot find ${keyFile.absolutePath}, please contact release engineering group"
      return
    }

    def ant = new AntBuilder()

    // copy real keyfile into the folder
    //"cp ${keyFile.absolutePath} ${new File('config/'+keyEnv).absolutePath}".execute().waitForProcessOutput()
    ant.copy( file:keyFile, todir:new File( versionHome, 'config/'+keyEnv) )
    println "copied ${keyFile}"

    // configure keystore password
    File ef = new File(versionHome, "classes/${targetEnv}/environment.properties")

    if( !ef.exists() )
    {
      println "cannot find ${ef.absolutePath}, please run from the version folder"
      return
    }

    File cf = new File( "${optShare}/code", "${rtConfig}/config.xml" )

    def config = new XmlSlurper().parseText( cf.text )

    replace( ef, [
                    '${keystore.password}':config.keystorePassword.text()
                  , '${key.password}':config.keyPassword.text()
                  ,'${jdbc.password}':config.jdbcPassword.text()
                  ] )

    println "Configured keystore and database passwords, adjust JSW startup configurations .."
  }

  static void replace( File f, map )
  {
    if( !f || !f.exists() )
      throw Exception("cannot replace in file ${f}")

    String text = f.text
    map.each { text = text.replace( it.key, it.value ); println "setting ${it.key} in ${f.absolutePath}" }
    f.withWriter { wr -> wr.write(text) }
  }

  static String getBuildId( versionFolder )
  {
    File propFile = new File(versionFolder, 'classes/META-INF/maven/com.bizzy/bz-config/pom.properties' )
    String text = propFile.text
    /*
     #Generated by Maven
     #Mon Oct 05 19:59:00 PDT 2009
     version=1.0-alpha-2-SNAPSHOT
     groupId=com.bizzy
     artifactId=bz-assembly
     */
    String ts = text.readLines().find { it.startsWith('#') && it.indexOf('Maven') == -1  }

    if( ts )
      return FMT_OUT.format(FMT_IN.parse(ts.substring(1)))

    throw new Exception("cannot find build timestamp in ${propFile.absolutePath}")
  }
}