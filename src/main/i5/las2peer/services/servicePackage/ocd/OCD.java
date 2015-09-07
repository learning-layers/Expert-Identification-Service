/**
 * 
 */
package i5.las2peer.services.servicePackage.ocd;

import i5.las2peer.services.servicePackage.parsers.xmlparser.SimpleDOMParser;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.NodeList;

/**
 * @author sathvik
 *
 */
public class OCD {
    private String graphId;
    private String coverId;

    // private static final String BASE_PATH =
    // "https://api.learning-layers.eu/ocd/";
    private static final String BASE_PATH = "http://localhost:9080/ocd/";

    private static final String UPLOAD_URL = BASE_PATH + "graphs?name=%s";
    private static final String IDENTIFY_COVERS_URL = BASE_PATH + "covers/graphs/%s/algorithms?algorithm=%s";
    private static final String GET_COVERS_URL_META = BASE_PATH + "covers/%s/graphs/%s?outputFormat=META_XML";
    private static final String GET_COVERS_URL = BASE_PATH + "covers/%s/graphs/%s";

    // Not exposed to the client.
    public static final String ALGORITHM_LABEL = "RANDOM_WALK_LABEL_PROPAGATION_ALGORITHM";

    public OCD() {

    }

    public void start(String graphContent) {
	uploadGraph("relationship_graph", graphContent);
	identifyCovers();
    }

    private String getBasicAuthEncodedString() {
	// byte[] encoding =
	// Base64.encodeBase64("Sathvik:dzJODeM4l5OmQVh".getBytes());
	byte[] encoding = Base64.encodeBase64("User:user".getBytes());
	return new String(encoding);
    }

    /**
     * 
     * Response from the service is of the format.
     *
     * @param filename
     *            String value that will be used to save the graph on the
     *            server.
     * @param graphContent
     *            String containing details about the graph, generally an xml
     *            string.
     */
    private void uploadGraph(String filename, String graphContent) {
	try {

	    HttpPost httppost = new HttpPost(String.format(UPLOAD_URL, filename));
	    httppost.addHeader("Authorization", "Basic " + getBasicAuthEncodedString());

	    // System.out.println("--------------GRAPH CONTENT-----------------------");

	    ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
	    postParameters.add(new BasicNameValuePair("name", "testing_graph_upload"));
	    httppost.setEntity(new StringEntity(graphContent));

	    CloseableHttpClient httpclient = HttpClients.createDefault();
	    CloseableHttpResponse response = httpclient.execute(httppost);
	    // System.out.println("-----------UPLOAD GRAPH-----------------------------");

	    HttpEntity entity = response.getEntity();
	    String responseString = EntityUtils.toString(entity, "UTF-8");

	    // System.out.println(responseString);

	    int statusCode = response.getStatusLine().getStatusCode();
	    EntityUtils.consume(entity);

	    // Parse the XML response to get the id of the graph.
	    if (statusCode == 200 && responseString != null && responseString.length() > 0) {
		SimpleDOMParser parser = new SimpleDOMParser(responseString);
		NodeList list = parser.getNodes("Id");
		if (list != null) {
		    for (int i = 0; i < list.getLength(); i++) {
			graphId = list.item(i).getTextContent();
		    }
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
	System.out.println("GraphId = " + graphId);

    }

    /**
     * This method executes a OCD service to identify covers in the graph.
     * 
     * Response string of the format.
     * 
     */
    private int identifyCovers() {
	int statusCode = -1;

	try {

	    String url = String.format(IDENTIFY_COVERS_URL, graphId, ALGORITHM_LABEL);

	    StringEntity strentity = new StringEntity("<?xml version=\"1.0\" encoding=\"UTF-16\"?><Parameters></Parameters>");

	    HttpPost httppost = new HttpPost(url);
	    httppost.addHeader("Authorization", "Basic " + getBasicAuthEncodedString());
	    httppost.setEntity(strentity);

	    CloseableHttpClient httpclient = HttpClients.createDefault();
	    CloseableHttpResponse response = httpclient.execute(httppost);

	    // System.out.println("------------------IDENTIFYING COVERS----------------------");
	    HttpEntity entity = response.getEntity();
	    String responseString = EntityUtils.toString(entity, "UTF-8");

	    statusCode = response.getStatusLine().getStatusCode();
	    System.out.println("Status Code::" + statusCode);
	    EntityUtils.consume(entity);
	    // <?xml version="1.0" encoding="UTF-16"?>
	    // <Cover><Id><CoverId>434</CoverId><GraphId>226</GraphId></Id></Cover>

	    // Parse the XML response to get the id of the graph.
	    if (statusCode == 200 && responseString != null && responseString.length() > 0) {
		SimpleDOMParser parser = new SimpleDOMParser(responseString);
		NodeList list = parser.getNodes("CoverId");
		if (list != null) {
		    for (int i = 0; i < list.getLength(); i++) {
			coverId = list.item(i).getTextContent();
		    }
		}
	    }

	    System.out.println("COVER ID:: " + coverId);

	} catch (Exception e) {
	    e.printStackTrace();
	}
	return statusCode;
    }

    public int getCoverCreationStatus() {
	String url = String.format(GET_COVERS_URL_META, coverId, graphId);
	String status = null;
	String responseString = getHttpResponseString(url);

	System.out.println(responseString);

	// Parse the XML response to check the status of the graph creation.
	if (responseString != null && responseString.length() > 0) {
	    SimpleDOMParser parser = new SimpleDOMParser(responseString);
	    NodeList list = parser.getNodes("Status");

	    if (list != null) {
		for (int i = 0; i < list.getLength(); i++) {
		    status = list.item(i).getTextContent();
		}
	    }
	}

	if (status.equalsIgnoreCase("error")) {
	    return -1;
	} else if (status.equalsIgnoreCase("completed")) {
	    return 1;
	} else {
	    return 0;
	}

    }

    /**
     */
    // TODO://There is a possibility of infinite loop. Have to exit after
    // certain attempts or timeout.
    public String getCovers() {
	String respStr = null;
	int isCompleted = getCoverCreationStatus();

	if (isCompleted == 1) {
	    // System.out.println("Cover creation completed..." + coverId + " "
	    // + graphId);
	    String url = String.format(GET_COVERS_URL, coverId, graphId);
	    respStr = getHttpResponseString(url);
	    // System.out.println("Resp Str " + respStr);
	} else if (isCompleted == 0) {
	    // If cover creation is still running, check the status again after
	    // sometime.
	    try {
		// System.out.println("Cover creation not yet completed...");
		Thread.sleep(2000);
		getCovers();
	    } catch (InterruptedException e) {
		e.printStackTrace();
	    }
	} else {
	    respStr = null;
	}
	return respStr;
    }

    private String getHttpResponseString(String url) {
	String responseString = null;
	try {
	    HttpGet httpget = new HttpGet(url);
	    httpget.setHeader("Authorization", "Basic " + getBasicAuthEncodedString());

	    CloseableHttpClient httpclient = HttpClients.createDefault();
	    CloseableHttpResponse response;

	    response = httpclient.execute(httpget);

	    System.out.println("-----------------COVERS--------------------");

	    HttpEntity entity = response.getEntity();
	    responseString = EntityUtils.toString(entity, "UTF-8");
	    int statusCode = response.getStatusLine().getStatusCode();

	    // if (statusCode != 200) {
	    // return null;
	    // }

	    EntityUtils.consume(entity);

	    System.out.println("Status Code :: " + statusCode);
	} catch (ClientProtocolException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}
	// System.out.println(responseString);
	return responseString;

    }
}
