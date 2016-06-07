package userinterface.graph;

/**
 * This is a dummy class used to be able to index Series in this graph.
 * Previously this was done using integers, which was unsafe if removeSeries
 * was used. The hashcode() and equals() implementation of Object (based on
 * object identity) are sufficient to use this as the key of a HashMap.
 * In addition, we add a 'next' field, to allow the same class to be used
 * to store (linked) lists of keys.
 */

public class SeriesKey 
{
	public SeriesKey next = null;
}