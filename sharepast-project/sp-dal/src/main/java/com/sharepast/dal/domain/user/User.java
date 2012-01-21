package com.sharepast.dal.domain.user;

import com.sharepast.constants.LogonConstants;
import com.sharepast.dal.dao.GeographicLocationDAO;
import com.sharepast.dal.domain.AppTimeZone;
import com.sharepast.dal.domain.GeographicLocationDO;
import com.sharepast.dal.domain.SecurityRole;
import com.sharepast.dal.exceptions.BadPasswordException;
import com.sharepast.dal.exceptions.DataConsistencyException;
import com.sharepast.persistence.orm.type.UTC;
import com.sharepast.util.Util;
import com.sharepast.persistence.Durable;
import org.apache.commons.lang.RandomStringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.joda.time.DateMidnight;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@org.hibernate.annotations.AccessType("field")
@Table(name = "users")
@Cache(usage= CacheConcurrencyStrategy.READ_WRITE)
public class User extends Durable<Long> {

    private static final Logger LOG = LoggerFactory.getLogger(User.class);

    @Transient
    @Autowired
    private GeographicLocationDAO geoLocation;

    @Embedded
    @AttributeOverrides( {
            @AttributeOverride(name="latitude", column = @Column(name="latitude") ),
            @AttributeOverride(name="longitude", column = @Column(name="longitude") ),
            @AttributeOverride(name="altitude", column = @Column(name="altitude") )
    } )
    private Location location;

    @Embedded
    @AttributeOverride( name="compaction", column = @Column(name="privacy_preferences", nullable = false) )
    private UserPrivacyPreferences privacyPreferences;

    @Embedded
    @AttributeOverride( name="compaction", column = @Column(name="notification_preferences", nullable = false) )
    private UserNotificationPreferences notificationPreferences;

    @Embedded
    @AttributeOverride( name="compaction", column = @Column(name="user_settings") )
    private UserSettings userSettings;

    @Column(name = "dob")
    @Temporal(TemporalType.DATE)
    private Date dateOfBirth;

    @Column(name="user_status")
    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @Column(name="gender")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "username", nullable = false, unique = true, length = 100)
    private String username;

    @Column
    private String email;

    @Column(name = "first_name", length = 50)
    private String firstName;

    @Column(name = "last_name", length = 50)
    private String lastName;

    @Column(name="dob_month")
    private Integer birthdayMonth;

    @Column(name="dob_day")
    private Integer birthdayDay;

    @Column(name="dob_year")
    private Integer birthdayYear;


    @Column(length = 255, nullable = false)
    private String password;

    /**
     * if not null - used to permutate the password hash
     */
    @Column(length = 20)
    private String salt;

    @Column(name = "created")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreated = UTC.now();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name="user_roles",
        joinColumns=@JoinColumn(name="user_id"),
        inverseJoinColumns=@JoinColumn(name="role_id")
    )
    @org.hibernate.annotations.Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
    private Set<SecurityRole> roles = new HashSet<SecurityRole>();

    public User() throws BadPasswordException {

        location = new Location();
        status = UserStatus.ACTIVE;
        privacyPreferences = new UserPrivacyPreferences();
        notificationPreferences = new UserNotificationPreferences();
        userSettings = new UserSettings();
        setNewPassword(RandomStringUtils.randomAlphanumeric(LogonConstants.MIN_PASS_LENGTH));
    }


    public User(String username, String password, String firstName, String lastName, Gender gender, Date dateOfBirth, String country, String language, String postalCode, String timeZone, double latitude, double longitude, float altitude)
            throws BadPasswordException {

        this(username, password, firstName, lastName, UserStatus.ACTIVE, gender, dateOfBirth, country, language, postalCode, timeZone, latitude, longitude, altitude);
    }

    public User(String username, String password, String firstName, String lastName, UserStatus status, Gender gender, Date dateOfBirth)
            throws BadPasswordException {
        location = new Location();
        setUsername(username);
        setFirstName(firstName);
        setLastName(lastName);
        setGender(gender);
        setDateOfBirth(UTC.then(dateOfBirth));
        setStatus(status);
        setNewPassword(password);
        privacyPreferences = new UserPrivacyPreferences();
        notificationPreferences = new UserNotificationPreferences();
        userSettings = new UserSettings();
    }

    public User(String username, String password, String firstName, String lastName, UserStatus status, Gender gender, Date dateOfBirth, String country, String language, String postalCode, String timeZone, double latitude, double longitude, float altitude)
            throws BadPasswordException {

        setUsername(username);
        setFirstName(firstName);
        setLastName(lastName);
        setGender(gender);
        setDateOfBirth(UTC.then(dateOfBirth));
        setStatus(status);
        setNewPassword(password);
        setLocation(new Location(country, language, postalCode, AppTimeZone.valueOf(timeZone), latitude, longitude, altitude));
        privacyPreferences = new UserPrivacyPreferences();
        notificationPreferences = new UserNotificationPreferences();
        userSettings = new UserSettings();
    }

    public User(User user) {

        location = user.getLocation();
        dateOfBirth = user.getDateOfBirth();
        status = user.getStatus();
        gender = user.getGender();
        username = user.getUsername();
        firstName = user.getFirstName();
        lastName = user.getLastName();
        birthdayMonth = user.getBirthdayMonth();
        birthdayDay = user.getBirthdayDay();
        birthdayYear = user.getBirthdayYear();
        password = user.getPassword();
        salt = user.getSalt();
        privacyPreferences = user.getPrivacyPreferences();
        notificationPreferences = new UserNotificationPreferences();
        userSettings = user.getUserSettings();
    }

    public Set<SecurityRole> getRoles() {
        return roles;
    }

    public void setRoles(Set<SecurityRole> roles) {
        this.roles = roles;
    }

    public synchronized Date getDateCreated() {
        return dateCreated;
    }

    public synchronized void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public synchronized String getUsername() {
        return username;
    }

    public synchronized void setUsername(String username) {
        this.username = username;
    }

    public synchronized String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public synchronized void setPassword(String password)
            throws BadPasswordException {

        this.password = Util.hashPassword(password, getSalt());
    }

    /**
     * this method generates salt and then sets the password
     */

    public synchronized void setNewPassword(String password)
            throws BadPasswordException {
        if (Util.isEmpty(password) || password.length() < LogonConstants.MIN_PASS_LENGTH || password.length() > LogonConstants.MAX_PASS_LENGTH)
            throw new BadPasswordException("password size should be between %d and %d characters", LogonConstants.MIN_PASS_LENGTH, LogonConstants.MAX_PASS_LENGTH);

        this.salt = RandomStringUtils.randomAlphanumeric(LogonConstants.MAX_SALT_LENGTH);
        this.password = Util.hashPassword(password, salt);
    }

    /**
     * this method generates salt and then sets the password
     */

    public synchronized void setNewPassword(String oldPassword, String password)
            throws BadPasswordException {
        String oldHash = Util.hashPassword(oldPassword, salt);

        if (oldHash.equals(this.password))
            setNewPassword(password);
        else
            throw new BadPasswordException("Password you entered does not match one we have on record.");
    }

    public synchronized String getSalt() {
        return salt == null ? "" : salt;
    }


    public synchronized void setSalt(String salt) {
        this.salt = salt;
    }

    public synchronized String getHashedPassword() {
        return password;
    }

    public synchronized void setHashedPassword(String hashedPassword) {
        this.password = hashedPassword;
    }

    public synchronized String getFirstName() {
        return firstName;
    }


    public synchronized void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public synchronized String getLastName() {
        return lastName;
    }


    public synchronized void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        String fullName = "";

        if (getFirstName() != null)
            fullName += (getFirstName());

        if (getFirstName() != null && getLastName() != null)
            fullName += " ";

        if (getLastName() != null)
            fullName += getLastName();

        if (fullName.isEmpty())
            fullName = "No Name";

        return fullName;
    }

    public synchronized Gender getGender() {
        return gender;
    }


    public synchronized void setGender(Gender gender) {
        this.gender = gender;
    }

    public synchronized Date getDateOfBirth() {
        return dateOfBirth;
    }


    public synchronized void setDateOfBirth(Date dateOfBirth) {
        if (dateOfBirth != null) {
            this.dateOfBirth = UTC.then(dateOfBirth);
        }
    }

    @Min(value = 1900, message = "Birthday year is not valid")
    public synchronized Integer getBirthdayYear() {
        return birthdayYear;
    }


    public synchronized void setBirthdayYear(Integer birthdayYear) {
        this.birthdayYear = birthdayYear;
    }

    @Min(value = 1, message = "Birthday day value must be between 1 and 31")
    @Max(value = 31, message = "Birthday day value must be between 1 and 31")
    public synchronized Integer getBirthdayDay() {
        return birthdayDay;
    }

    public synchronized void setBirthdayDay(Integer birthdayDay) {
        this.birthdayDay = birthdayDay;
    }

    @Min(value = 0, message = "Birthday month value must be between 0 and 11")
    @Max(value = 11, message = "Birthday month value must be between 0 and 11")
    public synchronized Integer getBirthdayMonth() {
        return birthdayMonth;
    }


    public synchronized void setBirthdayMonth(Integer birthdayMonth) {
        this.birthdayMonth = birthdayMonth;
    }

    public synchronized Date getBirthday() {
        // This method is called by the business upcoming birthdays module.

        if (getBirthdayMonth() == null || getBirthdayDay() == null)
            return null;

        // Birth year is irrelevant
        DateMidnight birthday = new DateMidnight().withMonthOfYear(getBirthdayMonth() + 1).withDayOfMonth(getBirthdayDay());
        if (birthday.isBefore(new DateMidnight())) {
            // Show UPCOMING birthday always - this is really for trapping the case when the range of upcoming birthdays
            // spans two years (e.g., 2009/12/28 - 2010/01/08)
            birthday.plusYears(1);
        }

        return birthday.toDate();
    }

    public synchronized Location getLocation() {
        return location;
    }

    public synchronized void setLocation(Location location) {
        this.location = (location == null) ? new Location() : location;
    }

    public synchronized UserStatus getStatus() {
        return status;
    }


    public synchronized void setStatus(UserStatus status) {

        this.status = status;
    }

    public synchronized UserPrivacyPreferences getPrivacyPreferences() {
        return privacyPreferences;
    }


    public synchronized void setPrivacyPreferences(UserPrivacyPreferences privacyPreferences) {
        this.privacyPreferences = privacyPreferences;
    }

    public synchronized UserNotificationPreferences getNotificationPreferences() {
        return notificationPreferences;
    }


    public synchronized void setNotificationPreferences(UserNotificationPreferences notificationPreferences) {
        this.notificationPreferences = notificationPreferences;
    }

    public synchronized UserSettings getUserSettings() {
        return (userSettings == null) ? new UserSettings() : userSettings;
    }

    public synchronized void setUserSettings(UserSettings userSettings) {
        if (userSettings == null) {
            userSettings = new UserSettings();
        }
        this.userSettings = userSettings;
    }

    public void createAlias() {
        String alias = getFirstName();
        if (!Util.isEmpty(alias)) {
            String lastName = getLastName();
            if (!Util.isEmpty(lastName)) {
                alias += " " + Character.toUpperCase(lastName.charAt(0)) + ".";
            }
        }
    }

    public GeographicLocationDO getGeoLocationInformation() {

        GeographicLocationDO location;

        if ((location = geoLocation.getByPostalCode(getLocation().getPostalCode())) == null) {
            throw new DataConsistencyException("No location information could be found for postal code (%s)", getLocation().getPostalCode());
        }

        return location;
    }

}
