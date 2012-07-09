package com.sharepast.commons.spring;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: kpelykh
 * Date: 6/17/12
 * Time: 1:56 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class ProfileConfigurator {

    public abstract Map<String, Class[]> getProfiles();

    public boolean supportsProfile(String profileId) {
        return getProfiles().containsKey(profileId);
    }

    public void configure( String profileId ) {
        Class[] resources = getProfiles().get(profileId);

        if ( resources == null )
            throw new IllegalArgumentException( String.format("cannot find profile with id %s", profileId) );

        SpringConfiguration.getInstance().configure(resources);
    }

}
