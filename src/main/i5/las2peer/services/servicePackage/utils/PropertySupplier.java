package i5.las2peer.services.servicePackage.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertySupplier {
	private static Properties sysProperties;

	public static String getProperty(String property) {
		if (sysProperties == null) {
			InputStream input = null;
			sysProperties = new Properties();
			try {
				input = new FileInputStream("etc/system.ers.properties");
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				sysProperties.load(input);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		String value = null;
		if (sysProperties.getProperty(property) != null)
			value = sysProperties.get(property).toString();
		return value;
	}

	public static Properties getSysProperties() {
		return sysProperties;
	}

	public static void setSysProperties(Properties sysProps) {
		sysProperties = sysProps;
	}

}
