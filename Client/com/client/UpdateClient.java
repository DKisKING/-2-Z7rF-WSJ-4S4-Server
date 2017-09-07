package com.client;

import java.awt.Desktop;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;

public class UpdateClient extends Thread {

	static final String URL_ADDRESS = "https://www.dropbox.com/s/w7fagwslnb4bg6j/OSVeldahar.jar?raw=1"; //"https://www.os-v.org/cache/OSVeldahar.jar";
	
	private static String getApplicationPath() throws URISyntaxException {
		CodeSource codeSource = Client.class.getProtectionDomain().getCodeSource();
		return new File(codeSource.getLocation().toURI().getPath()).getAbsolutePath();
	}
	
	@Override
	public void run() {
		try {
			URL applicationURL = new URL(URL_ADDRESS);
			StringBuilder sb = new StringBuilder(getApplicationPath());
			int indexOfExtension = sb.indexOf(".jar");
			
			if (!sb.toString().contains("Updated")) {
				sb.insert(indexOfExtension, " Updated");
			}
			
			File applicationFile = new File(sb.toString());
			
			File oldApplicationFile = new File(getApplicationPath());
			
			if(oldApplicationFile.exists()) {
				oldApplicationFile.delete();
			}
			
			applicationFile.createNewFile();
			DataInputStream input = new DataInputStream(applicationURL.openStream());
			FileOutputStream output = new FileOutputStream(applicationFile);
			byte[] data = new byte[1024];
			int read = 0;
			
			while((read = input.read(data, 0, data.length - 1)) != -1) {
				output.write(data, 0, read);
			}
			output.close();
			
			if(Desktop.isDesktopSupported()) {
				Desktop.getDesktop().open(applicationFile);
			} else {
				javax.swing.JOptionPane.showMessageDialog(null, 
						  "Your Operating System does not support\n"
						+ "the resources needed to relaunch the new client.\n"
						+ "Please access the file directly at the following path;\n\n"
						+ applicationFile.getAbsolutePath());
			}
			System.exit(0);
		} catch (Exception e) {
			javax.swing.JOptionPane.showMessageDialog(null,
					  "We were unable to download the file from the site.\n"
					+ "Please visit the forums at http://os-v.org/\n");
			e.printStackTrace();
		}
	}
}
