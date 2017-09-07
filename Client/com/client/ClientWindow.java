package com.client;

import java.net.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowListener;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.*;

public class ClientWindow extends Client implements ActionListener, WindowListener {

	private static final long serialVersionUID = -6978617783576386732L;

	public void initUI() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			JPopupMenu.setDefaultLightWeightPopupEnabled(false);
			frame = new JFrame("Dbz is a big noob ;)");
			frame.setLayout(new BorderLayout());
			setFocusTraversalKeysEnabled(false);
			frame.setResizable(true);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			JPanel gamePanel = new JPanel();
			gamePanel.setLayout(new BorderLayout());
			gamePanel.add(this);
			gamePanel.setPreferredSize(new Dimension(765, 503));
			frame.getContentPane().add(gamePanel, BorderLayout.CENTER);
			frame.pack();
			URL url2 = null;
	        @SuppressWarnings("unused")
			boolean updateIcon = true;
	        try {
	            url2 = new URL("https://www.dropbox.com/s/kes389xmmm44yiy/Tyras_T_for_icon.png");
	        } catch (MalformedURLException ex) {
	            updateIcon = true;
	            ex.printStackTrace();
	        }
	        if (imageUpdate(null, 0, 0, 0, 0, 0)) {
	            System.out.println("Icon updated.");
	            Image bimg = Toolkit.getDefaultToolkit().getImage(url2);
	            this.setIconImage(bimg);
	        }
			insets = frame.getInsets();
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
			init();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setIconImage(Image bimg) {
		// TODO Auto-generated method stub
		
	}

	public ClientWindow(String args[]) {
		super();
		try {
			com.client.sign.Signlink.startpriv(InetAddress.getByName(server));
			initUI();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public URL getCodeBase() {
		try {
			return new URL("http://" + server + "/cache");
		} catch (Exception e) {
			return super.getCodeBase();
		}
	}

	@Override
	public URL getDocumentBase() {
		return getCodeBase();
	}

	public void loadError(String s) {
		System.out.println("loadError: " + s);
	}

	@Override
	public String getParameter(String key) {
		return "";
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		
	}
	
	private static JFrame frame;
	
	public static JFrame getFrame() {
		return frame;
	}
	
	private static Insets insets;
	
	public static Insets getInset() {
		return insets;
	}

}