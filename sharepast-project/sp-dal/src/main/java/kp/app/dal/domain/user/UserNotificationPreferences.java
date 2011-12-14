package kp.app.dal.domain.user;

import javax.persistence.Embeddable;

@Embeddable
public class UserNotificationPreferences extends FlagCompactor<UserNotificationFlags> {

    public UserNotificationPreferences(String flags) {
		super(flags);
	}

	public UserNotificationPreferences() {
		
	}

  public UserNotificationFlags[] getFlags () {

    return UserNotificationFlags.values();
  }

  public boolean isReceivePersonalMessageNotifications () {

    return isSetTo(UserNotificationFlags.RECEIVE_PERSONAL_MESSAGES_NOTIFICATIONS, '1');
  }

  public void setReceivePersonalMessageNotifications (char value) {

    setFlag(UserNotificationFlags.RECEIVE_PERSONAL_MESSAGES_NOTIFICATIONS, value);
  }

  public boolean isReceiveSummaryNotifications () {

    return isSetTo(UserNotificationFlags.RECEIVE_SUMMARY_NOTIFICATIONS, '1');
  }

  public void setReceiveSummaryNotifications (char value) {

    setFlag(UserNotificationFlags.RECEIVE_SUMMARY_NOTIFICATIONS, value);
  }

  public boolean isReceiveWhatsHappeningNewsletter() {
    return isSetTo(UserNotificationFlags.RECEIVE_WHATS_HAPPENING_NEWSLETTER, '1');
  }

  public void setReceiveWhatsHappeningNewsletter(char value) {
    setFlag(UserNotificationFlags.RECEIVE_WHATS_HAPPENING_NEWSLETTER, value);
  }

  public UserNotificationFlags getFlagByKey (String key) {
    UserNotificationFlags[] allFlags = getFlags();
    for (UserNotificationFlags flag : allFlags) {
      if (flag.getNotificationKey().equals(key)) {
        return flag;
      }
    }

    return null;
  }
}
