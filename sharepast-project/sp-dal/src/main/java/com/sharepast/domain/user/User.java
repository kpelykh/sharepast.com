package com.sharepast.domain.user;

import com.sharepast.commons.spring.SpringConfiguration;
import com.sharepast.commons.util.UTC;
import com.sharepast.commons.util.Util;
import com.sharepast.dao.GeographicLocation;
import com.sharepast.dao.GroupManager;
import com.sharepast.domain.GeographicLocationDO;
import com.sharepast.domain.IEntity;
import com.sharepast.exception.DataConsistencyException;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.joda.time.DateMidnight;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.*;

@Entity
@org.hibernate.annotations.AccessType("field")
@Table(name = "sp_user")
@Cache(usage= CacheConcurrencyStrategy.READ_WRITE)
public class User extends IEntity<Integer> implements UserDetails {

    @Transient
    @Autowired
    private GeographicLocation geoLocation;

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

    @Column(length = 255, nullable = false)
    private String password;

    @Column(name = "username", nullable = false, unique = true, length = 255)
    private String username;

    @Column(name = "email", nullable = true, unique=true, length = 255)
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

    @Column(name = "account_non_expired", length = 1)
    @Type(type="yes_no")
    private boolean accountNonExpired;

    @Column(name = "account_non_locked", length = 1)
    @Type(type="yes_no")
    private boolean accountNonLocked;

    @Column(name = "credentials_non_expired", length = 1)
    @Type(type="yes_no")
    private boolean credentialsNonExpired;

    @Column(length = 1)
    @Type(type="yes_no")
    private boolean enabled;

    @Column(name = "created")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreated = UTC.now();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_group",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id")
    )
    private Set<Group> groups = new HashSet<Group>(0);


    /*@ElementCollection
    @javax.persistence.MapKeyColumn(name="attribute_name")
    @Column(name="user_attributes")
    @CollectionTable(name="attribute_mapping")
    private Map<String, String> attributes = new HashMap<String, String>();*/


    //~ Constructors ===================================================================================================

    public User() {
    }


    /**
     * Calls the more complex constructor with all boolean arguments set to {@code true}.
     */
    public User(String username, String password) {
        this(username, password, true, true, true, true);
    }

    /**
     * Construct the <code>User</code> with the details required by
     * {@link org.springframework.security.authentication.dao.DaoAuthenticationProvider}.
     *
     * @param username the username presented to the
     *        <code>DaoAuthenticationProvider</code>
     * @param password the password that should be presented to the
     *        <code>DaoAuthenticationProvider</code>
     * @param enabled set to <code>true</code> if the user is enabled
     * @param accountNonExpired set to <code>true</code> if the account has not
     *        expired
     * @param credentialsNonExpired set to <code>true</code> if the credentials
     *        have not expired
     * @param accountNonLocked set to <code>true</code> if the account is not
     *        locked
     *
     * @throws IllegalArgumentException if a <code>null</code> value was passed
     *         either as a parameter or as an element in the
     *         <code>GrantedAuthority</code> collection
     */
    public User(String username, String password, boolean enabled, boolean accountNonExpired,
                boolean credentialsNonExpired, boolean accountNonLocked) {

        if (((username == null) || "".equals(username)) || (password == null)) {
            throw new IllegalArgumentException("Cannot pass null or empty values to constructor");
        }

        location = new Location();
        status = UserStatus.ACTIVE;
        privacyPreferences = new UserPrivacyPreferences();
        notificationPreferences = new UserNotificationPreferences();
        userSettings = new UserSettings();

        this.username = username;
        this.password = password;
        this.enabled = enabled;
        this.accountNonExpired = accountNonExpired;
        this.credentialsNonExpired = credentialsNonExpired;
        this.accountNonLocked = accountNonLocked;
    }

    //~ Methods ========================================================================================================


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        if (!super.equals(o)) return false;

        User user = (User) o;

        if (accountNonExpired != user.accountNonExpired) return false;
        if (accountNonLocked != user.accountNonLocked) return false;
        if (credentialsNonExpired != user.credentialsNonExpired) return false;
        if (enabled != user.enabled) return false;
        if (email != null ? !email.equals(user.email) : user.email != null) return false;
        if (firstName != null ? !firstName.equals(user.firstName) : user.firstName != null) return false;
        if (lastName != null ? !lastName.equals(user.lastName) : user.lastName != null) return false;
        if (password != null ? !password.equals(user.password) : user.password != null) return false;
        if (groups != null ? !groups.equals(user.groups) : user.groups != null) return false;
        if (!username.equals(user.username)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + username.hashCode();
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + (accountNonExpired ? 1 : 0);
        result = 31 * result + (accountNonLocked ? 1 : 0);
        result = 31 * result + (credentialsNonExpired ? 1 : 0);
        result = 31 * result + (enabled ? 1 : 0);
        result = 31 * result + (groups != null ? groups.hashCode() : 0);
        return result;
    }

    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    public void setAccountNonExpired(boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }


    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    public void setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    public synchronized Date getDateCreated() {
        return dateCreated;
    }

    public synchronized void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    //This is BAD naming. This method should be called getGroups, but since we're using
    //RoleHierarchyVoter, which takes only GrantedAuthority
    //we have to make this ugly trick and wrap group name with SimpleGrantedAuthority
    public List<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();

        for (Group group : groups) {
            GrantedAuthority auth = new SimpleGrantedAuthority(group.getName());
            authorities.add(auth);
        }

        return authorities;
    }

    public List<GrantedAuthority> getPermissions() {
        Set<GrantedAuthority> permissions = new HashSet<GrantedAuthority>();

        Collection<? extends GrantedAuthority> auths = SpringConfiguration.getInstance().getBean(RoleHierarchy.class).getReachableGrantedAuthorities(getAuthorities());

        GroupManager groupManager = SpringConfiguration.getInstance().getBean(GroupManager.class);

        for (GrantedAuthority authority : auths) {
            if (!authority.getAuthority().equals("ROLE_ANONYMOUS"))
                permissions.addAll(groupManager.findGroupAuthorities(authority.getAuthority()));
        }

        return new ArrayList<GrantedAuthority>(permissions);
    }

    /*public boolean isAllowUsersSeeHistory() {
        return isAttribute(UserAttributes.ALLOW_USERS_SEE_HISTORY);
    }

    public void setAllowUsersSeeHistory(boolean value) {
        attributes.put(UserAttributes.ALLOW_USERS_SEE_HISTORY, Boolean.toString(value));
    }*/

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString()).append(": ");
        sb.append("Username: ").append(this.username).append("; ");
        sb.append("Password: [PROTECTED]; ");
        sb.append("Enabled: ").append(this.enabled).append("; ");
        sb.append("AccountNonExpired: ").append(this.accountNonExpired).append("; ");
        sb.append("credentialsNonExpired: ").append(this.credentialsNonExpired).append("; ");
        sb.append("AccountNonLocked: ").append(this.accountNonLocked).append("; ");

        if (this.getAuthorities() != null) {
            sb.append("Granted Authorities: \n");
            sb.append(this.getAuthorities().toString());
        } else {
            sb.append("Not granted any authorities");
        }

        return sb.toString();
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

    public Set<Group> getGroups() {
        return groups;
    }

    public GeographicLocation getGeoLocation() {
        return geoLocation;
    }

    public void setGeoLocation(GeographicLocation geoLocation) {
        this.geoLocation = geoLocation;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public UserPrivacyPreferences getPrivacyPreferences() {
        return privacyPreferences;
    }

    public void setPrivacyPreferences(UserPrivacyPreferences privacyPreferences) {
        this.privacyPreferences = privacyPreferences;
    }

    public UserNotificationPreferences getNotificationPreferences() {
        return notificationPreferences;
    }

    public void setNotificationPreferences(UserNotificationPreferences notificationPreferences) {
        this.notificationPreferences = notificationPreferences;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Min(value = 1900, message = "Birthday year is not valid")
    public synchronized Integer getBirthdayYear() {
        return birthdayYear;
    }

    @Min(value = 1, message = "Birthday day value must be between 1 and 31")
    @Max(value = 31, message = "Birthday day value must be between 1 and 31")
    public synchronized Integer getBirthdayDay() {
        return birthdayDay;
    }

    @Min(value = 0, message = "Birthday month value must be between 0 and 11")
    @Max(value = 11, message = "Birthday month value must be between 0 and 11")
    public synchronized Integer getBirthdayMonth() {
        return birthdayMonth;
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
