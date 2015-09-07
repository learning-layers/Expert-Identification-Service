/**
 * 
 */
package i5.las2peer.services.servicePackage.metrics;

/**
 * @author sathvik
 *
 */
public interface IEvaluator<E> {
    /**
     * Actual evaluation algorithm implementation is computed in this method.
     */
    public void compute();

    /**
     * 
     * @return E, value or list of values corresponding to the particular
     *         evaluation method.
     */
    public E getValue();
}
