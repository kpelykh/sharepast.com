package java.com.sharepast.exception;

import com.sharepast.util.lang.FormattedRuntimeException;

/**
 * Created with IntelliJ IDEA.
 * User: kpelykh
 * Date: 5/2/12
 * Time: 12:07 AM
 * To change this template use File | Settings | File Templates.
 */

public class DataConsistencyException extends FormattedRuntimeException {

    public DataConsistencyException () {

        super();
    }

    public DataConsistencyException (String message, Object... args) {

        super(message, args);
    }

    public DataConsistencyException (Throwable throwable, String message, Object... args) {

        super(throwable, message, args);
    }

    public DataConsistencyException (Throwable throwable) {

        super(throwable);
    }
}
