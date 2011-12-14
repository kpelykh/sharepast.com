package kp.app.security;

import org.restlet.data.Method;

/**
 * Created by IntelliJ IDEA.
 * User: Kostya
 * Date: 8/31/11
 * Time: 12:47 AM
 * To change this template use File | Settings | File Templates.
 */
public enum AppResourceActionEnum {
    READ(Method.GET),
    CREATE(Method.PUT),
    UPDATE(Method.POST),
    DELETE(Method.DELETE),
    EXISTS(Method.HEAD),
    DESCRIBE(Method.OPTIONS);

    Method val;

    private AppResourceActionEnum(Method val) {
        this.val = val;
    }

    Method getValue() {
        return val;
    }

}

