package com.sharepast.service.impl;

/**
 * Created by IntelliJ IDEA.
 * User: kpelykh
 * Date: 1/23/12
 * Time: 12:15 PM
 * To change this template use File | Settings | File Templates.
 */

import com.sharepast.dao.UserDAO;
import com.sharepast.domain.user.StaticGroups;
import com.sharepast.domain.user.User;
import com.sharepast.exception.UsernameExistsException;
import com.sharepast.genericdao.hibernate.GenericDAO;
import com.sharepast.service.AbstractService;
import com.sharepast.service.IUserService;
import com.sharepast.service.Subject;
import com.sharepast.service.exception.ServiceException;
import com.sharepast.util.security.PasswordHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * A custom service for retrieving users from a custom datasource, such as a database.
 * <p/>
 * This custom service must implement Spring's {@link org.springframework.security.core.userdetails.UserDetailsService}
 */
@Service
public class UserService extends AbstractService<User> implements IUserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserDAO userDAO;


    @Autowired
    @Qualifier("org.springframework.security.authenticationManager")
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordHelper passwordHelper;

    @Override
    @Transactional(readOnly = true)
    public User findUserByUsername(String username) {
        return userDAO.findByUsername(username);
    }

    public User createUser(User user) throws UsernameExistsException {
        Assert.notNull(user);

        User userExist = userDAO.findByUsername(user.getUsername());

        if (userExist != null) {
            throw new UsernameExistsException(String.format("Username %s exists. Please select another username.", user.getUsername()));
        }

        if ( user.getGroups().size() == 0 ) {
            user.getGroups().add(StaticGroups.ROLE_USER.getGroup());
        }

        // default attributes
        //user.getAttributes().clear();
        user.setAccountNonLocked(true);
        user.setAccountNonExpired(true);
        user.setEnabled(true);
        user.setCredentialsNonExpired(true);
        user.setPassword(passwordHelper.encode(user.getPassword(), user));

        userDAO.save(user);

        return user;

    }

    public void updateUser(UserDetails user) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void deleteUser(String username) {
        System.out.println(username);
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void changePassword(String username, String newPassword, boolean otheruser) throws AuthenticationException, ServiceException {

        if (Subject.getCurrentUser() == null ) {
            // This would indicate bad coding somewhere
            throw new AccessDeniedException("Can't change password as no Authentication object found in context " +
                    "for current user.");
        }

        if (Subject.hasAdminRole()) {
            User user = userDAO.findByUsername(username);
            if (user == null) {
                throw new ServiceException(String.format("User %s doesn't exist in database", username));
            }

            LOG.info("Changing password for user '" + username + "'");

            String newPasswordHash = passwordHelper.encode(newPassword);
            user.setPassword(newPasswordHash);
            update(user);
            LOG.info("Password successfully updated for for user '" + username + "'");
        } else {
            throw new AccessDeniedException("User has to be in admin role to change other user's password");
        }


    }

    public void changePassword(String oldPassword, String newPassword) throws AuthenticationException, ServiceException {

        if (Subject.getCurrentUser() == null) {
            // This would indicate bad coding somewhere
            throw new AccessDeniedException("Can't change password as no Authentication object found in context " +
                    "for current user.");
        }
        User currentUser = Subject.getCurrentUser();

        if (currentUser.getPassword().equals(passwordHelper.encode(oldPassword))) {
            String username = currentUser.getUsername();

            LOG.info("Changing password for user '" + username + "'");

            String newPasswordhash = passwordHelper.encode(newPassword);

            User user = userDAO.findByUsername(username);
            user.setPassword(newPasswordhash);
            update(user);

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            createNewAuthentication(authentication);
        } else {
            throw new BadCredentialsException("Old password is incorrect.");
        }
    }

    public void changeUsername(String oldUsername, String newUsername) throws AuthenticationException, ServiceException {
        Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();

        if (!userDAO.isUsernameAvailable(newUsername)) {
            throw new UsernameExistsException("Username \"" + newUsername + "\" already registered. Please select another username.");
        }

        //In case of ReflectionSaltSource, password also needs to be reset, otherwise user will not be able to login next time.

        LOG.info("Changing username for user '" + oldUsername + "'");

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        user.setUsername(newUsername);
        update(user);

        createNewAuthentication(currentUser);

    }


    protected void createNewAuthentication(Authentication currentAuth) {
        UserDetails user = loadUserByUsername(currentAuth.getName());

        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
        token.setDetails(currentAuth.getDetails());

        Authentication newAuthentication = authenticationManager.authenticate(token);

        if(newAuthentication.getPrincipal() instanceof User) {
            SecurityContextHolder.getContext().setAuthentication(newAuthentication);
        }
    }


    // -- UserDetailsService implementation

    /**
     * Returns a populated {@link org.springframework.security.core.userdetails.UserDetails} object.
     * The username is first retrieved from the database and then mapped to
     * a {@link org.springframework.security.core.userdetails.UserDetails} object.
     */
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException, DataAccessException {

            User domainUser = userDAO.findByUsername(username);
            if (domainUser == null) {
                throw new UsernameNotFoundException(String.format("Can't find user '%s'", username));
            }

            boolean enabled = true;
            boolean accountNonExpired = true;
            boolean credentialsNonExpired = true;
            boolean accountNonLocked = true;

            return new org.springframework.security.core.userdetails.User(
                    domainUser.getUsername(),
                    domainUser.getPassword().toLowerCase(),
                    enabled,
                    accountNonExpired,
                    credentialsNonExpired,
                    accountNonLocked,
                    domainUser.getAuthorities());

    }

    @Override
    protected GenericDAO<User, Integer> getDao() {
        return userDAO;
    }
}
