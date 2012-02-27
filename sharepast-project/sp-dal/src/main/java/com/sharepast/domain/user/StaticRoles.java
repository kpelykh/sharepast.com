package com.sharepast.domain.user;

import com.sharepast.dao.IGroupManager;
import com.sharepast.util.spring.SpringConfigurator;
import org.springframework.util.Assert;

/**
 * Created by IntelliJ IDEA.
 * User: kpelykh
 * Date: 2/21/12
 * Time: 11:11 PM
 * To change this template use File | Settings | File Templates.
 */
public enum StaticRoles {

    ROLE_ADMIN,
    ROLE_USER;

    private Role role;

    StaticRoles() {
        IGroupManager rolesManager = SpringConfigurator.getInstance().getBean(IGroupManager.class);
        Assert.notNull(rolesManager, "error getting rolesManager");
        role = rolesManager.findByName(this.name());
    }

    public Role getRole() {
        return role;
    }

    @Override
    public String toString() {
        return role.getName();
    }

}
