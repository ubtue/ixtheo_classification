package de.tuebingen.uni.ub.hc;


import java.io.Serializable;


public class Counter implements Serializable {
    private long count;

    public Counter() {
        this(0);
    }

    public Counter(long initialValue) {
        this.count = initialValue;
    }

    public void increase() {
        ++this.count;
    }

    public long get() {
        return count;
    }

    @Override
    public String toString() {
        return Long.toString(count);
    }
}
