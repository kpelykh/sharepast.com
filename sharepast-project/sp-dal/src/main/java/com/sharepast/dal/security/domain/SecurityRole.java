package com.sharepast.dal.security.domain;

import com.sharepast.persistence.Durable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.Set;

/**
 * Model object that represents a security role.
 */
@Entity
@org.hibernate.annotations.AccessType("field")
@Table(name="roles")
@Cache(usage= CacheConcurrencyStrategy.READ_WRITE)
public class SecurityRole extends Durable<Long> {

    @Basic(optional=false)
    @Column(length=100, name = "role_name")
    private String name;

    @Basic(optional=false)
    @Column(length=255)
    private String description;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name="role_permissions",
            joinColumns = @JoinColumn( name="role_id"),
            inverseJoinColumns = @JoinColumn( name="permission_id")
    )
    public Set<SecurityPermission> permissions;

    public SecurityRole() {
    }

    public SecurityRole(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<SecurityPermission> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<SecurityPermission> permissions) {
        this.permissions = permissions;
    }
}


