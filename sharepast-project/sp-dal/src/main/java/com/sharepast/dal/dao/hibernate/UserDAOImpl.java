package com.sharepast.dal.dao.hibernate;

import com.sharepast.dal.dao.UserDAO;
import com.sharepast.dal.domain.user.User;
import com.sharepast.persistence.orm.hibernate.CriteriaDetails;
import com.sharepast.persistence.orm.hibernate.HibernateDao;
import com.sharepast.persistence.orm.hibernate.QueryDetails;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("userDao")
public class UserDAOImpl extends HibernateDao<User> implements UserDAO {

    private static final Logger LOG = LoggerFactory.getLogger(UserDAOImpl.class);


    public UserDAOImpl() {
    }

    public Class<User> getManagedClass() {

        return User.class;
    }

    public User getUser(Long userId) {
        return (User) getSession().get(User.class, userId);
    }

    public User findUser(String username) {
        String query = "from User u where u.username = :username";
        return (User) getSession().createQuery(query).setString("username", username).uniqueResult();
    }

    public void createUser(User user) {
        getSession().save(user);
    }

    public List<User> getAllUsers() {
        return getSession().createQuery("from User order by username").list();
    }

    public void deleteUser(Long userId) {
        User user = getUser(userId);
        if (user != null) {
            getSession().delete(user);
        }
    }

    public void updateUser(User user) {
        getSession().update(user);
    }


    /**
     * find accounts matching email regex
     */
    public List<User> findByEmailRegEx(final String email) {
        return listByQuery(new QueryDetails() {
            public String getQueryString() {
                return "from User where lower(email) like :email";
            }

            public Query completeQuery(Query query) {
                return query.setString("email", email.toLowerCase());
            }
        });
    }

    public User findByEmail(final String email) {

        return findByCriteria(new CriteriaDetails() {

            public Criteria completeCriteria(Criteria criteria) {

                return criteria.add(Restrictions.eq("email", email));
            }
        });
    }

}