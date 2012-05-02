package java.com.sharepast.domain;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: kpelykh
 * Date: 2/21/12
 * Time: 11:14 PM
 * To change this template use File | Settings | File Templates.
 */
@MappedSuperclass
public abstract class IEntity<I extends Comparable<I>> implements Serializable, Comparable<IEntity<I>> {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="id")
    @Type(type="int")
    private I id;

    public IEntity() {
    }

    public IEntity(IEntity<I> IEntity) {

        id = IEntity.getId();
    }

    public synchronized I getId () {

        return id;
    }

    public synchronized void setId (I id) {

        this.id = id;
    }

    public int compareTo (IEntity<I> IEntity) {

        if (getId() == null) {
            if (IEntity.getId() == null) {

                return 0;
            }
            else {

                return -1;
            }
        }

        if (IEntity.getId() == null) {

            return 1;
        }

        return IEntity.getId().compareTo(getId());
    }

    @Override
    public synchronized int hashCode () {

        if (id == null) {

            return super.hashCode();
        }

        int h = id.hashCode();

        h ^= (h >>> 20) ^ (h >>> 12);

        return h ^ (h >>> 7) ^ (h >>> 4);
    }

    @Override
    public synchronized boolean equals (Object obj) {

        if (obj instanceof IEntity) {
            if ((((IEntity)obj).getId() == null) || (id == null)) {
                return super.equals(obj);
            }
            else {
                return ((IEntity)obj).getId().equals(id);
            }
        }

        return false;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) + ": id=" + id;
    }
}
