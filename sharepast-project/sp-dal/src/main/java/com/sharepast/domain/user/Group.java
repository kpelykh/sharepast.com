package com.sharepast.domain.user;

import com.sharepast.domain.IEntity;
import org.hibernate.annotations.*;
import org.hibernate.annotations.AccessType;
import org.hibernate.annotations.CascadeType;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: kpelykh
 * Date: 2/21/12
 * Time: 11:11 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity
@AccessType("field")
@Table(name = "groups")
public class Group extends IEntity<Integer> {

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "group_permission",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    @Cascade(value = {CascadeType.SAVE_UPDATE})
    private Set<Permission> permissions = new HashSet<Permission>(0);

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_group",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> users = new HashSet<User>(0);

    @Column(name = "name")
    private String name;

    public Group() {
    }

    public Group(String name, Set<Permission> permissions) {
        this.name = name;
        this.permissions = permissions;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Group)) return false;
        if (!super.equals(o)) return false;

        Group group = (Group) o;

        if (name != null ? !name.equals(group.name) : group.name != null) return false;
        if (permissions != null ? !permissions.equals(group.permissions) : group.permissions != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (permissions != null ? permissions.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}