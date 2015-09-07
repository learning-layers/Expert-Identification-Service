/**
 * 
 */
package i5.las2peer.services.servicePackage.parsers;


/**
 * @author sathvik
 *
 */
public interface IParser<T, U> {
    public T getPosts();

    public U getUsers();
}

