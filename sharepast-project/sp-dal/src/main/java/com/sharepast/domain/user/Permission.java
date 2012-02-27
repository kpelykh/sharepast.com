package com.sharepast.domain.user;

import com.sharepast.domain.IEntity;
import org.hibernate.annotations.AccessType;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * User: kpelykh
 * Date: 2/21/12
 * Time: 11:13 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity
@AccessType("field")
@Table(name = "permission")
public class Permission extends IEntity<Long> implements GrantedAuthority {

    @Column(name = "name")
    private String name;

    public Permission(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    //~ Methods ========================================================================================================

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof String) {
            return obj.equals(this.name);
        }

        if (obj instanceof GrantedAuthority) {
            GrantedAuthority attr = (GrantedAuthority) obj;

            return this.name.equals(attr.getAuthority());
        }

        return false;
    }

    public String getAuthority() {
        return this.name;
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    public String toString() {
        return this.name;
    }

    public int compareTo(IEntity<Long> entity) {
        if (entity != null && entity instanceof GrantedAuthority) {
            String rhsRole = ((GrantedAuthority) entity).getAuthority();

            if (rhsRole == null) {
                return -1;
            }

            return name.compareTo(rhsRole);
        }
        return -1;
    }

}

