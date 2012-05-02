/**
 *
 */
package java.com.sharepast.jsw;

/*
 * Copyright (c) 1999, 2006 Tanuki Software Inc.
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of the Java Service Wrapper and associated
 * documentation files (the "Software"), to deal in the Software
 * without  restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sub-license,
 * and/or sell copies of the Software, and to permit persons to
 * whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 *
 * Portions of the Software have been derived from source code
 * developed by Silver Egg Technology under the following license:
 *
 * Copyright (c) 2001 Silver Egg Technology
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sub-license, and/or
 * sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 */

import org.tanukisoftware.wrapper.WrapperListener;
import org.tanukisoftware.wrapper.WrapperManager;
import org.tanukisoftware.wrapper.WrapperSystemPropertyUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * By default the WrapperStartStopApp will only wait for 2 seconds for the main
 *  method of the start class to complete.  This was done because the main
 *  methods of many applications never return.  It is possible to force the
 *  class to wait for the startup main method to complete by defining the
 *  following system property when launching the JVM (defaults to FALSE):
 *  -Dorg.tanukisoftware.wrapper.WrapperStartStopApp.waitForStartMain=TRUE
 * <p>
 * Using the waitForStartMain property will cause the startup to wait
 *  indefinitely.  This is fine if the main method will always return
 *  within a predefined period of time.  But if there is any chance that
 *  it could hang, then the maxStartMainWait property may be a better
 *  option.  It allows the 2 second wait time to be overridden. To wait
 *  for up to 5 minutes for the startup main method to complete, set
 *  the property to 300 as follows (defaults to 2 seconds):
 *  -Dorg.tanukisoftware.wrapper.WrapperStartStopApp.maxStartMainWait=300
 * <p>
 * It is possible to extend this class but make absolutely sure that any
 *  overridden methods call their super method or the class will fail to
 *  function correctly.  Most users will have no need to override this
 *  class.
 * <p>
 * NOTE - The main methods of many applications are designed not to
 *  return.  In these cases, you must either stick with the default 2 second
 *  startup timeout or specify a slightly longer timeout, using the
 *  maxStartMainWait property, to simulate the amount of time your application
 *  takes to start up.
 * <p>
 * WARNING - If the waitForStartMain is specified for an application
 *  whose start method never returns, the Wrapper will appear at first to be
 *  functioning correctly.  However the Wrapper will never enter a running
 *  state, this means that the Windows Service Manager and several of the
 *  Wrapper's error recovery mechanisms will not function correctly.
 *
 * @author Leif Mortenson <leif@tanukisoftware.com>
 */
public class Main
    implements WrapperListener, Runnable
{
    /**
     * Application's start main method
     */
    private Method m_startMainMethod;

    /**
     * Command line arguments to be passed on to the start main method
     */
    private String[] m_startMainArgs;

    /**
     * Application's stop main method
     */
    private Method _stopMainMethod;

    /**
     * Should the stop process force the JVM to exit, or wait for all threads
     *  to die on their own.
     */
    private boolean _stopWait;

    /**
     * Command line arguments to be passed on to the stop main method
     */
    private String[] _stopMainArgs;

    /**
     * Gets set to true when the thread used to launch the application
     *  actuially starts.
     */
    private boolean _mainStarted;

    /**
     * Gets set to true when the thread used to launch the application
     *  completes.
     */
    private boolean _mainComplete;

    /**
     * Exit code to be returned if the application fails to start.
     */
    private Integer _mainExitCode;

    /**
     * Flag used to signify that the start method has completed.
     */
    private boolean _startComplete;

    private static final String me = Main.class.getName();

    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    protected Main( String args[] )
    {

        // Initialize the WrapperManager class on startup by referencing it.
        Class wmClass = WrapperManager.class;

        // Get the class name of the application
        if ( args.length < 5 )
        {
            System.out.println( me+": Not enough argments.  Minimum 5 required." );
            showUsage();
            WrapperManager.stop( 1 );
            return;  // Will not get here
        }


        // Look for the start main method.
        m_startMainMethod = getMainMethod( args[0] );
        // Get the start arguments
        String[] startArgs = getArgs( args, 1 );


        // Where do the stop arguments start
        int stopArgBase = 2 + startArgs.length;
        if ( args.length < stopArgBase + 3 )
        {
            System.out.println( me+": Not enough argments. Minimum 3 after start "
                + "arguments." );
            showUsage();
            WrapperManager.stop( 1 );
            return;  // Will not get here
        }

        // Look for the stop main method.
        _stopMainMethod = getMainMethod( args[stopArgBase] );
        // Get the stopWait flag
        if ( args[stopArgBase + 1].equalsIgnoreCase( "true" ) )
        {
            _stopWait = true;
        }
        else if ( args[stopArgBase + 1].equalsIgnoreCase( "false" ) )
        {
            _stopWait = false;
        }
        else
        {
            System.out.println( me+": The stop_wait argument must be either true or false." );
            showUsage();
            WrapperManager.stop( 1 );
            return;  // Will not get here
        }
        // Get the start arguments
        _stopMainArgs = getArgs( args, stopArgBase + 2 );

        // Start the application.  If the JVM was launched from the native
        //  Wrapper then the application will wait for the native Wrapper to
        //  call the application's start method.  Otherwise the start method
        //  will be called immediately.
        WrapperManager.start( this, startArgs );

        // This thread ends, the WrapperManager will start the application after the Wrapper has
        //  been propperly initialized by calling the start method above.
    }


    protected Main( Method startMainMethod,
                   Method stopMainMethod,
                   boolean stopWait,
                   String[] stopMainArgs
                  )
    {
        m_startMainMethod = startMainMethod;
        _stopMainMethod = stopMainMethod;
        _stopWait = stopWait;
        _stopMainArgs = stopMainArgs;
    }

    /*---------------------------------------------------------------
     * Runnable Methods
     *-------------------------------------------------------------*/
    /**
     * Used to launch the application in a separate thread.
     */
    public void run()
    {
        // Notify the start method that the thread has been started by the JVM.
        synchronized( this )
        {
            _mainStarted = true;
            notifyAll();
        }

        Throwable t = null;
        try
        {
            if ( WrapperManager.isDebugEnabled() )
            {
                System.out.println( me+": invoking start main method" );
            }
            m_startMainMethod.invoke( null, new Object[] { m_startMainArgs } );
            if ( WrapperManager.isDebugEnabled() )
            {
                System.out.println( me+": start main method completed" );
            }

            synchronized(this)
            {
                // Let the start() method know that the main method returned, in case it is
                //  still waiting.
                _mainComplete = true;
                this.notifyAll();
            }

            return;
        }
        catch ( IllegalAccessException e )
        {
            t = e;
        }
        catch ( IllegalArgumentException e )
        {
            t = e;
        }
        catch ( InvocationTargetException e )
        {
            t = e.getTargetException();
            if ( t == null )
            {
                t = e;
            }
        }

        // If we get here, then an error was thrown.  If this happened quickly
        // enough, the start method should be allowed to shut things down.
        System.out.println();
        System.out.println( me+": Encountered an error running start main: " + t );

        // We should print a stack trace here, because in the case of an
        // InvocationTargetException, the user needs to know what exception
        // their app threw.
        t.printStackTrace();

        synchronized(this)
        {
            if ( _startComplete )
            {
                // Shut down here.
                WrapperManager.stop( 1 );
                return; // Will not get here.
            }
            else
            {
                // Let start method handle shutdown.
                _mainComplete = true;
                _mainExitCode = new Integer( 1 );
                this.notifyAll();
                return;
            }
        }
    }

    /*---------------------------------------------------------------
     * WrapperListener Methods
     *-------------------------------------------------------------*/
    /**
     * The start method is called when the WrapperManager is signalled by the
     *  native wrapper code that it can start its application.  This
     *  method call is expected to return, so a new thread should be launched
     *  if necessary.
     * If there are any problems, then an Integer should be returned, set to
     *  the desired exit code.  If the application should continue,
     *  return null.
     */
    public Integer start( String[] args )
    {
        // Decide whether or not to wait for the start main method to complete before returning.
        boolean waitForStartMain = WrapperSystemPropertyUtil.getBooleanProperty(
            Main.class.getName() + ".waitForStartMain", false );
        int maxStartMainWait = WrapperSystemPropertyUtil.getIntProperty(
            Main.class.getName() + ".maxStartMainWait", 2 );
        maxStartMainWait = Math.max( 1, maxStartMainWait );

        // Decide the maximum number of times to loop waiting for the main start method.
        int maxLoops;
        if ( waitForStartMain )
        {
            maxLoops = Integer.MAX_VALUE;
            if ( WrapperManager.isDebugEnabled() )
            {
                System.out.println( me+": start(args) Will wait indefinitely "
                    + "for the main method to complete." );
            }
        }
        else
        {
            maxLoops = maxStartMainWait; // 1s loops.
            if ( WrapperManager.isDebugEnabled() )
            {
                System.out.println( me+": start(args) Will wait up to " + maxLoops
                    + " seconds for the main method to complete." );
            }
        }

        Thread mainThread = new Thread( this, me+"Main" );
        synchronized(this)
        {
            m_startMainArgs = args;
            mainThread.start();

            // To avoid problems with the main thread starting slowly on heavily loaded systems,
            //  do not continue until the thread has actually started.
            while ( !_mainStarted )
            {
                try
                {
                    this.wait( 1000 );
                }
                catch ( InterruptedException e )
                {
                    // Continue.
                }
            }

            // Wait for startup main method to complete.
            int loops = 0;
            while ( ( loops < maxLoops ) && ( !_mainComplete ) )
            {
                try
                {
                    this.wait( 1000 );
                }
                catch ( InterruptedException e )
                {
                    // Continue.
                }

                if ( !_mainComplete )
                {
                    // If maxLoops is large then this could take a while.  Notify the
                    //  WrapperManager that we are still starting so it doesn't give up.
                    WrapperManager.signalStarting( 5000 );
                }

                loops++;
            }

            // Always set the flag stating that the start method completed.  This is needed
            //  so the run method can decide whether or not it needs to be responsible for
            //  shutting down the JVM in the event of an exception thrown by the start main
            //  method.
            _startComplete = true;

            // The main exit code will be null unless an error was thrown by the start
            //  main method.
            if ( WrapperManager.isDebugEnabled() )
            {
                System.out.println( me+": start(args) end.  Main Completed="
                    + _mainComplete + ", exitCode=" + _mainExitCode );
            }
            return _mainExitCode;
        }
    }

    /**
     * Called when the application is shutting down.
     */
    public int stop( int exitCode )
    {
        if ( WrapperManager.isDebugEnabled() )
        {
            System.out.println( me+": stop(" + exitCode + ")" );
        }

        // Execute the main method in the stop class
        Throwable t = null;
        try
        {
            if ( WrapperManager.isDebugEnabled() )
            {
                System.out.println( me+": invoking stop main method" );
            }

            _stopMainMethod.invoke( null, new Object[] { _stopMainArgs } );

            if ( WrapperManager.isDebugEnabled() )
            {
                System.out.println( me+": stop main method completed" );
            }

            if ( _stopWait )
            {
                // This feature exists to make sure the stop process waits for the main
                //  application to fully shutdown.  This can only be done by looking for
                //  and counting the number of non-daemon threads still running in the
                //  system.

                int systemThreadCount = WrapperSystemPropertyUtil.getIntProperty(
                    Main.class.getName() + ".systemThreadCount", 1 );
                systemThreadCount = Math.max( 0, systemThreadCount );

                int threadCnt;
                while( ( threadCnt = getNonDaemonThreadCount() ) > systemThreadCount )
                {
                    if ( WrapperManager.isDebugEnabled() )
                    {
                        System.out.println( me+": stopping.  Waiting for "
                            + ( threadCnt - systemThreadCount ) + " threads to complete." );
                    }
                    try
                    {
                        Thread.sleep( 1000 );
                    }
                    catch ( InterruptedException e )
                    {
                    }
                }
            }

            // Success
            return exitCode;
        }
        catch ( IllegalAccessException e )
        {
            t = e;
        }
        catch ( IllegalArgumentException e )
        {
            t = e;
        }
        catch ( InvocationTargetException e )
        {
            t = e;
        }

        // If we get here, then an error was thrown.
        System.out.println( "Encountered an error running stop main: " + t );

        // We should print a stack trace here, because in the case of an
        // InvocationTargetException, the user needs to know what exception
        // their app threw.
        t.printStackTrace();

        // Return a failure exit code
        return 1;
    }

    /**
     * Called whenever the native wrapper code traps a system control signal
     *  against the Java process.  It is up to the callback to take any actions
     *  necessary.  Possible values are: WrapperManager.WRAPPER_CTRL_C_EVENT,
     *    WRAPPER_CTRL_CLOSE_EVENT, WRAPPER_CTRL_LOGOFF_EVENT, or
     *    WRAPPER_CTRL_SHUTDOWN_EVENT
     */
    public void controlEvent( int event )
    {
        if ( ( event == WrapperManager.WRAPPER_CTRL_LOGOFF_EVENT )
            && WrapperManager.isLaunchedAsService() )
        {
            // Ignore
            if ( WrapperManager.isDebugEnabled() )
            {
                System.out.println( me+": controlEvent(" + event + ") Ignored" );
            }
        }
        else
        {
            if ( WrapperManager.isDebugEnabled() )
            {
                System.out.println( me+": controlEvent(" + event + ") Stopping" );
            }
            WrapperManager.stop( 0 );
            // Will not get here.
        }
    }

    /*---------------------------------------------------------------
     * Methods
     *-------------------------------------------------------------*/
    /**
     * Returns a count of all non-daemon threads in the JVM, starting with the top
     *  thread group.
     *
     * @return Number of non-daemon threads.
     */
    private int getNonDaemonThreadCount()
    {
        // Locate the top thread group.
        ThreadGroup topGroup = Thread.currentThread().getThreadGroup();
        while ( topGroup.getParent() != null )
        {
            topGroup = topGroup.getParent();
        }

        // Get a list of all threads.  Use an array that is twice the total number of
        //  threads as the number of running threads may be increasing as this runs.
        Thread[] threads = new Thread[topGroup.activeCount() * 2];
        topGroup.enumerate( threads, true );

        // Only count any non daemon threads which are
        //  still alive other than this thread.
        int liveCount = 0;
        for ( int i = 0; i < threads.length; i++ )
        {
            /*
            if ( threads[i] != null )
            {
                System.out.println( "Check " + threads[i].getName() + " daemon="
                    + threads[i].isDaemon() + " alive=" + threads[i].isAlive() );
            }
            */
            if ( ( threads[i] != null ) && threads[i].isAlive() )
            {
                // Do not count this thread.
                if ( ( Thread.currentThread() != threads[i] ) && ( !threads[i].isDaemon() ) )
                {
                    // Non-Daemon living thread
                    liveCount++;
                    //System.out.println( "  -> Non-Daemon" );
                }
            }
        }
        //System.out.println( "  => liveCount = " + liveCount );

        return liveCount;
    }

    /**
     * Returns the main method of the specified class.  If there are any problems,
     *  an error message will be displayed and the Wrapper will be stopped.  This
     *  method will only return if it has a valid method.
     */
    private Method getMainMethod( String className )
    {
        // Look for the start class by name
        Class mainClass;
        try
        {
            mainClass = Class.forName( className );
        }
        catch ( ClassNotFoundException e )
        {
            System.out.println( me+": Unable to locate the class " + className
                + ": " + e );
            showUsage();
            WrapperManager.stop( 1 );
            return null;  // Will not get here
        }
        catch ( LinkageError e )
        {
            System.out.println( me+": Unable to locate the class " + className
                + ": " + e );
            showUsage();
            WrapperManager.stop( 1 );
            return null;  // Will not get here
        }

        // Look for the start method
        Method mainMethod;
        try
        {
            // getDeclaredMethod will return any method named main in the specified class,
            //  while getMethod will only return public methods, but it will search up the
            //  inheritance path.
            mainMethod = mainClass.getMethod( "main", new Class[] { String[].class } );
        }
        catch ( NoSuchMethodException e )
        {
            System.out.println(
                me+": Unable to locate a public static main method in "
                + "class " + className + ": " + e );
            showUsage();
            WrapperManager.stop( 1 );
            return null;  // Will not get here
        }
        catch ( SecurityException e )
        {
            System.out.println(
                me+": Unable to locate a public static main method in "
                + "class " + className + ": " + e );
            showUsage();
            WrapperManager.stop( 1 );
            return null;  // Will not get here
        }

        // Make sure that the method is public and static
        int modifiers = mainMethod.getModifiers();
        if ( !( Modifier.isPublic( modifiers ) && Modifier.isStatic( modifiers ) ) )
        {
            System.out.println( me+": The main method in class " + className
                + " must be declared public and static." );
            showUsage();
            WrapperManager.stop( 1 );
            return null;  // Will not get here
        }

        return mainMethod;
    }

    private String[] getArgs( String[] args, int argBase )
    {
        // The arg at the arg base should be a count of the number of available arguments.
        int argCount;
        try
        {
            argCount = Integer.parseInt( args[argBase] );
        }
        catch ( NumberFormatException e )
        {
            System.out.println( me+": Illegal argument count: " + args[argBase] );
            showUsage();
            WrapperManager.stop( 1 );
            return null;  // Will not get here
        }
        if ( argCount < 0 )
        {
            System.out.println( me+": Illegal argument count: " + args[argBase] );
            showUsage();
            WrapperManager.stop( 1 );
            return null;  // Will not get here
        }

        // Make sure that there are enough arguments in the array.
        if ( args.length < argBase + 1 + argCount )
        {
            System.out.println( me+": Not enough argments.  Argument count of "
                + argCount + " was specified." );
            showUsage();
            WrapperManager.stop( 1 );
            return null;  // Will not get here
        }

        // Create the argument array
        String[] mainArgs = new String[argCount];
        System.arraycopy( args, argBase + 1, mainArgs, 0, argCount );

        return mainArgs;
    }

    /**
     * Displays application usage
     */
    protected void showUsage()
    {
        System.out.println();
        System.out.println(
            me+" Usage:" );
        System.out.println(
            "  java org.tanukisoftware.wrapper.WrapperStartStopApp {start_class} {start_arg_count} "
            + "[start_arguments] {stop_class} {stop_wait} {stop_arg_count} [stop_arguments]" );
        System.out.println();
        System.out.println(
            "Where:" );
        System.out.println(
            "  start_class:     The fully qualified class name to run to start the " );
        System.out.println(
            "                   application." );
        System.out.println(
            "  start_arg_count: The number of arguments to be passed to the start class's " );
        System.out.println(
            "                   main method." );
        System.out.println(
            "  start_arguments: The arguments that would normally be passed to the start " );
        System.out.println(
            "                   class application." );
        System.out.println(
            "  stop_class:      The fully qualified class name to run to stop the " );
        System.out.println(
            "                   application." );
        System.out.println(
            "  stop_wait:       When stopping, should the Wrapper wait for all threads to " );
        System.out.println(
            "                   complete before exiting (true/false)." );
        System.out.println(
            "  stop_arg_count:  The number of arguments to be passed to the stop class's " );
        System.out.println(
            "                   main method." );
        System.out.println(
            "  stop_arguments:  The arguments that would normally be passed to the stop " );
        System.out.println(
            "                   class application." );
    }

    /*---------------------------------------------------------------
     * Main Method
     *-------------------------------------------------------------*/
    /**
     * Used to Wrapper enable a standard Java application.  This main
     *  expects the first argument to be the class name of the application
     *  to launch.  All remaining arguments will be wrapped into a new
     *  argument list and passed to the main method of the specified
     *  application.
     */
    public static void main( String args[] )
    {
        new Main( args );
    }
}

