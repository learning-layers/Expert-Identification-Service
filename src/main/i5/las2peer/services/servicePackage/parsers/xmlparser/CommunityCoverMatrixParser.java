/**
 * 
 */
package i5.las2peer.services.servicePackage.parsers.xmlparser;

import i5.las2peer.services.servicePackage.ocd.NodeCoverParser;

import java.util.HashMap;
import java.util.Scanner;

/**
 * Parses for the covers when covers are available as matrix.
 * 
 * @author sathvik
 *
 */
public class CommunityCoverMatrixParser implements ICommunityCoverParser {
    // String filename;
    String coverMatrix;
    HashMap<Long, NodeCoverParser> nodeId2Covers;

    public CommunityCoverMatrixParser(String covermatrix) {
	// this.filename = filename;
	this.coverMatrix = covermatrix;
	nodeId2Covers = new HashMap<Long, NodeCoverParser>();
    }

    /*
     * (non-Javadoc)
     * 
     * @see ICommunityCoverParser#parse()
     */
    @Override
    public void parse() {
	NodeCoverParser ncoverManager;
	HashMap<Long, Double> coverId2memVal;
//System.out.println(coverMatrix);
	Scanner scanner = new Scanner(coverMatrix);
	while (scanner.hasNextLine()) {
	    String line = scanner.nextLine();
	    String values[] = line.split("\\s+");
	    if (values != null && values.length > 0) {
	    	
		long nodeId = Long.parseLong(values[0].replace(",", "."));
		coverId2memVal = new HashMap<Long, Double>();
		if (values.length > 1) {
		    for (int i = 1; i < values.length; i++) {
		    //System.out.println("rrr"+values[i]);
			double memValue = Double.parseDouble(values[i].replace(",", "."));
			if (memValue != 0) {
			    coverId2memVal.put((long) (i - 1), memValue);
			}
		    }
		}
		//System.out.print("NODE ID::" + nodeId);
		ncoverManager = new NodeCoverParser(nodeId, coverId2memVal);
		nodeId2Covers.put(nodeId, ncoverManager);
	    }
	}
	scanner.close();

	// NodeCoverManager ncoverManager;
	// HashMap<Long, Double> coverId2memVal;
	// try (BufferedReader br = new BufferedReader(new
	// FileReader(filename))) {
	// for (String line; (line = br.readLine()) != null;) {
	// String values[] = line.split("\\s+");
	// if (values != null && values.length > 0) {
	// long nodeId = Long.parseLong(values[0]);
	// coverId2memVal = new HashMap<Long, Double>();
	// if (values.length > 1) {
	// for (int i = 1; i < values.length; i++) {
	// double memValue = Double.parseDouble(values[i]);
	// if (memValue != 0) {
	// coverId2memVal.put((long) (i - 1), memValue);
	// }
	// }
	// }
	// System.out.print("NODE ID::" + nodeId);
	// ncoverManager = new NodeCoverManager(nodeId, coverId2memVal);
	// nodeId2Covers.put(nodeId, ncoverManager);
	// }
	// }
	// // line is not visible here.
	// } catch (FileNotFoundException e) {
	// e.printStackTrace();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }

    }

    /*
     * (non-Javadoc)
     * 
     * @see ICommunityCoverParser#getNodeId2CoversMap()
     */
    @Override
    public HashMap<Long, NodeCoverParser> getNodeId2CoversMap() {
	return nodeId2Covers;
    }

}
