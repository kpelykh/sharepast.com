package com.sharepast.service;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;


/**
 * Business manager interface used for sample application.
 *
 * @since 0.1
 */
public interface SampleService {

    /**
     * Returns the value stored in the user's session.
     *
     * @return the value.
     */
    String getValue();


    /**
     * Sets a value to be stored in the user's session.
     *
     * @param newValue the new value to store in the user's session.
     */
    void setValue(String newValue);

    /**
     * Method that requires <tt>role1</tt> in order to be invoked.
     */
    @RequiresRoles("ADMIN_ROLE")
    void secureMethod1();

    /**
     * Method that requires <tt>role2</tt> in order to be invoked.
     */
    @RequiresRoles("USER_ROLE")
    void secureMethod2();

    /**
     * Method that requires <tt>permission1</tt> in order to be invoked.
     */
    @RequiresPermissions("permission2")
    void secureMethod3();
}
