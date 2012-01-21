package com.sharepast.security;

import com.sharepast.constants.AppConstants;
import com.sharepast.dal.domain.user.User;
import com.sharepast.dal.domain.AppSecurityContextNameEnum;
import com.sharepast.dal.domain.SecurityPermission;
import com.sharepast.dal.domain.SecurityRole;
import org.apache.shiro.authc.SimpleAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Kostya
 * Date: 9/1/11
 * Time: 1:34 AM
 * To change this template use File | Settings | File Templates.
 */
public class AppAvatar extends SimpleAccount {
    private static final Logger LOG = LoggerFactory.getLogger(AppAvatar.class);

    public AppAvatar(User user) {
        super(user.getEmail(), user.getPassword(), AppConstants.REALM_NAME);

        addObjectPermission(AppSecurityUtil.createContextPermission(AppSecurityContextNameEnum.ME, user.getId()));

        processFixedRoles(user);
    }

    private void processFixedRoles(User user) {
        for (SecurityRole role : user.getRoles()) {
            addRole(role.getName());
            List<String> permissions = new ArrayList<String>();
            for (SecurityPermission permission : role.getPermissions()) {
                permissions.add(permission.getName());
            }
            addStringPermissions(permissions);
        }
    }
}
