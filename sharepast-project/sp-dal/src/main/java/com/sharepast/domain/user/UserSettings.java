package com.sharepast.domain.user;

import javax.persistence.Embeddable;

@Embeddable
public class UserSettings extends FlagCompactor<UserSettingFlags> {

    public UserSettings(String settings) {
        super(settings);
    }

    public UserSettings() {
        setShowWelcomePage('1');
    }

    public UserSettingFlags[] getFlags() {
        return UserSettingFlags.values();
    }

    public boolean isShowWelcomePage() {
        return isSetTo(UserSettingFlags.SHOW_WELCOME_PAGE, '1');
    }

    public void setShowWelcomePage(char value) {
        setFlag(UserSettingFlags.SHOW_WELCOME_PAGE, value);
    }

    public UserSettingFlags getFlagByKey(String key) {
        UserSettingFlags[] allFlags = getFlags();
        for (UserSettingFlags flag : allFlags) {
            if (flag.getSettingKey().equals(key)) {
                return flag;
            }
        }

        return null;
    }
}

