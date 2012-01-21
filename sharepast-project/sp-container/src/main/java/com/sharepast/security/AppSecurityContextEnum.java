package com.sharepast.security;

import com.sharepast.constants.ParamNameEnum;
import com.sharepast.dal.domain.AppSecurityContextNameEnum;

/**
 * Created by IntelliJ IDEA.
 * User: Kostya
 * Date: 8/31/11
 * Time: 12:10 AM
 * To change this template use File | Settings | File Templates.
 */
public enum AppSecurityContextEnum {
    MY_USER(AppSecurityContextNameEnum.ME, ParamNameEnum.USER_ID);

    private AppSecurityContextNameEnum permissionName;
    private String paramName;

    private AppSecurityContextEnum(AppSecurityContextNameEnum permissionName, ParamNameEnum paramName) {
        this.permissionName = permissionName;
        this.paramName = paramName.getKey();
    }

    public AppSecurityContextNameEnum getContextName() {
        return permissionName;
    }

    public String getPermissionName() {
        return permissionName.name();
    }

    public String getParamName() {
        return paramName;
    }

}