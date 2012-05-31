package com.sharepast.domain.user;

import com.sharepast.dao.GroupManager;
import com.sharepast.commons.spring.SpringConfiguration;
import org.springframework.util.Assert;

/**
 * Created by IntelliJ IDEA.
 * User: kpelykh
 * Date: 2/21/12
 * Time: 11:11 PM
 * To change this template use File | Settings | File Templates.
 */
public enum StaticGroups {

    ROLE_ADMIN,
    ROLE_USER;

    private Group group;

    StaticGroups() {
        GroupManager rolesManager = SpringConfiguration.getInstance().getBean(GroupManager.class);
        Assert.notNull(rolesManager, "error getting rolesManager");
        group = rolesManager.findByName(this.name());
    }

    public Group getGroup() {
        return group;
    }

    @Override
    public String toString() {
        return group.getName();
    }

}
