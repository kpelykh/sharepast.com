package com.sharepast.persistence.hibernate;

import org.hibernate.ScrollableResults;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ScrollIterator<T> implements Iterator<T>, Iterable<T> {

    private ScrollableResults scrollableResults;

    private Class<T> managedClass;

    private boolean more;

    public ScrollIterator (ScrollableResults scrollableResults, Class<T> managedClass) {

        this.scrollableResults = scrollableResults;
        this.managedClass = managedClass;

        more = scrollableResults.first();
    }

    public Iterator<T> iterator () {

        return this;
    }

    public boolean hasNext () {

        return more;
    }

    public T next () {

        if (!more) {
            throw new NoSuchElementException();
        }

        try {
            return managedClass.cast(scrollableResults.get(0));
        }
        finally {
            more = scrollableResults.next();
        }
    }

    public void remove () {

        throw new UnsupportedOperationException();
    }
}