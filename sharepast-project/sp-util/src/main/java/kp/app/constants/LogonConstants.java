package kp.app.constants;

/**
 * Logon related constants
 *
 *
 */
public abstract class LogonConstants
{
  /** form field names to be filled in for logon  */
  public static final String LOGON_TARGET_URI_NAME = "targetUri";
  public static final String LOGON_TARGET_QUERY_NAME = "targetQuery";
  public static final String REMEMBER_ME = "remember_me";

  public static final String LOGON_USER_NAME = "login";
  public static final String LOGON_PASS_NAME = "password";

  /** maximum allowed email length */
  public static final int MAX_EMAIL_LENGTH = 80;

  /** maximum allowed salt length */
  public static final int MAX_SALT_LENGTH = 16;

  /** minimal allowed password length */
  public static final int MIN_PASS_LENGTH = 6;

  /** maximum allowed password length */
  public static final int MAX_PASS_LENGTH = 200;

}
