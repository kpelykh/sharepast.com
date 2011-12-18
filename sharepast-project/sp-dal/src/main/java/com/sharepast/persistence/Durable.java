package com.sharepast.persistence;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@MappedSuperclass
public abstract class Durable<I extends Comparable<I>> implements Serializable, Comparable<Durable<I>> {

  @Id
  @GeneratedValue(generator = "app_generator")
  @GenericGenerator(
          name = "app_generator",
          strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
          parameters = {
            @Parameter(name = "initial_value", value = "3"),
            @Parameter(name = "increment_size", value = "10"),
            @Parameter(name = "sequence_name", value = "hibernate_sequence"),
            @Parameter(name = "optimizer", value = "pooled")
          })
  @Column(name="id")
  @Type(type="long")
  private I id;

  public Durable () {
  }

  public Durable (Durable<I> durable) {

    id = durable.getId();
  }

  @NotNull
  public synchronized I getId () {

    return id;
  }

  public synchronized void setId (I id) {

    this.id = id;
  }

  public int compareTo (Durable<I> durable) {

    if (getId() == null) {
      if (durable.getId() == null) {

        return 0;
      }
      else {

        return -1;
      }
    }

    if (durable.getId() == null) {

      return 1;
    }

    return durable.getId().compareTo(getId());
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

    if (obj instanceof Durable) {
      if ((((Durable)obj).getId() == null) || (id == null)) {
        return super.equals(obj);
      }
      else {
        return ((Durable)obj).getId().equals(id);
      }
    }

    return false;
  }

	@Override
	public String toString() {
		return getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) + ": id=" + id;
	}
}
