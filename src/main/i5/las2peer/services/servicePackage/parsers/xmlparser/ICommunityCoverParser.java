/**
 * 
 */
package i5.las2peer.services.servicePackage.parsers.xmlparser;

import i5.las2peer.services.servicePackage.ocd.NodeCoverParser;

import java.util.HashMap;

/**
 * Parser interface to parse the community detection covers.
 * Covers can be received in various forms, XML , matrix, edge lists.
 * Parsing different types should implement this interface.
 * 
 * @author sathvik
 *
 */
public interface ICommunityCoverParser {
    public void parse();

    public HashMap<Long, NodeCoverParser> getNodeId2CoversMap();
}

