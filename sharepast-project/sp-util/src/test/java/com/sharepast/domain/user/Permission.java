package java.com.sharepast.domain.user;

import com.sharepast.domain.IEntity;
import org.hibernate.annotations.AccessType;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: kpelykh
 * Date: 2/21/12
 * Time: 11:13 PM
 * To change this template use File | Settings | File Templates.
 */
//There is some confusion on what constitutes an Authority as it is interchangeably called Group or Permission throughout the documentation, but essentially they are the same thing.
//A Group/Authority/Permission is a granular definition of a business use case. For example: ACCESS_ACCOUNT, DELETE_INVOICE, TRANSFER_BALANCE etc could be all treated as business use cases.
//A Group, can have multiple Authorities and typically represents what is commonly called outside the Spring Security world, a ‘role’. For example a business department or function like ACCOUNTS, SALES, ADMIN etc could be Group.

@Entity
@AccessType("field")
@Table(name = "permission")
public class Permission extends IEntity<Integer> implements GrantedAuthority {

    private static final long serialVersionUID = 1L;

    @Column(name = "name")
    private String name;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name="group_permission",
            joinColumns=@JoinColumn(name="permission_id"),
            inverseJoinColumns=@JoinColumn(name="group_id")
    )
    private Set<Group> groups = new HashSet<Group>(0);


    public Permission() {
    }

    public Permission(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Set<Group> getGroups() {
        return groups;
    }

    public void setGroups(Set<Group> groups) {
        this.groups = groups;
    }

    public int compareTo(IEntity<Integer> entity) {
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

