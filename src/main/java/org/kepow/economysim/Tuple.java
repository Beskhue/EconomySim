package org.kepow.economysim;

/**
 * Class representing a generic tuple.
 * @author Thomas Churchman
 *
 * @param <T1> Type of first object contained in the tuple.
 * @param <T2> Type of second object contained in the tuple.
 */
public class Tuple<T1, T2> 
{
    public final T1 t1;
    public final T2 t2;

    /**
     * Constructor.
     * @param t1 First object contained in the tuple.
     * @param t2 Second object contained in the tuple.
     */
    public Tuple(T1 t1, T2 t2)
    {
        this.t1 = t1;
        this.t2 = t2;
    }
}
