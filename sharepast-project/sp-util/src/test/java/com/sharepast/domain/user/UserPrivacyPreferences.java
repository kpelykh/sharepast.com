package java.com.sharepast.domain.user;

import javax.persistence.Embeddable;

@Embeddable
public class UserPrivacyPreferences extends FlagCompactor<PrivacyPreferenceFlags> {

    public UserPrivacyPreferences() {
    }

    public UserPrivacyPreferences(String flags) {
        super(flags);
    }

    public PrivacyPreferenceFlags[] getFlags() {

        return PrivacyPreferenceFlags.values();
    }

    public boolean isHideProfilePicture() {

        return !isSetTo(PrivacyPreferenceFlags.HIDE_PROFILE_PICTURE, '0');
    }

    public void setHideProfilePicture(char value) {

        setFlag(PrivacyPreferenceFlags.HIDE_PROFILE_PICTURE, value);
    }

    public boolean isHidePublicName() {

        return !isSetTo(PrivacyPreferenceFlags.HIDE_PUBLIC_NAME, '0');
    }

    public void setHidePublicName(char value) {

        setFlag(PrivacyPreferenceFlags.HIDE_PUBLIC_NAME, value);
    }

    public boolean isHideLocation() {

        return !isSetTo(PrivacyPreferenceFlags.HIDE_LOCATION, '0');
    }

    public void setHideLocation(char value) {

        setFlag(PrivacyPreferenceFlags.HIDE_LOCATION, value);
    }

    public boolean isHideBirthday() {

        return !isSetTo(PrivacyPreferenceFlags.HIDE_BIRTHDAY, '0');
    }

    public void setHideBirthday(char value) {

        setFlag(PrivacyPreferenceFlags.HIDE_BIRTHDAY, value);
    }

}
