package kp.app.dal.security.domain;

import kp.app.persistence.Durable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * User: Kostya
 * Date: 3/23/11
 * Time: 10:28 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity
@org.hibernate.annotations.AccessType("field")
@Table(name = "permissions")
@Cache(usage= CacheConcurrencyStrategy.READ_WRITE)
public class SecurityPermission extends Durable<Long> {

    @Column(length = 100)
    private String name;

    @Column(length = 255)
    private String description;

    public SecurityPermission() {
    }

    public SecurityPermission(String name) {

        this.name = name;
    }

    public synchronized String getName() {

        return name;
    }

    public synchronized void setName(String name) {

        this.name = name;
    }

    public synchronized String getDescription() {

        return description;
    }

    public synchronized void setDescription(String description) {

        this.description = description;
    }

}
