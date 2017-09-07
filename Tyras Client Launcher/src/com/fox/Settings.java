package com.fox;

import java.awt.Color;
import java.awt.Dimension;
import java.io.File;

public class Settings {

	public static final String SERVER_NAME = "Tyras - The Place To Be";
	public static final String DOWNLOAD_URL = "http://www.tyrasps.com/Tyras_Client/Tyras_Client.jar";
	
	public static final String SAVE_NAME = "Tyras_Client.jar";
	public static final String SAVE_DIR = System.getProperty("user.home") + File.separator;
	
	public static final String SERVER_IP = "198.12.13.49";
	public static final int SERVER_PORT = 43595;
	
	public static final boolean enableMusicPlayer = true;
	
	// Frame Settings
	public static final Dimension frameSize = new Dimension(600, 350);
	public static final Color borderColor = new Color(0, 0, 0);
	public static final Color backgroundColor = new Color(30, 30, 30);
	public static final Color primaryColor = new Color(59, 166, 226); // 226, 166, 59
	public static final Color iconShadow = new Color(0, 0, 0);
	public static final Color buttonDefaultColor = new Color(255, 255, 255);
	
	// link settings
	public static final String youtube = "";
	public static final String twitter = "";
	public static final String facebook = "";
	
	public static final String community = "https://discord.gg/GBWEYRG";
	public static final String leaders = "https://discord.gg/GBWEYRG";
	public static final String store = "https://discord.gg/GBWEYRG";
	public static final String vote = "https://discord.gg/GBWEYRG";
	public static final String bugs = "https://discord.gg/GBWEYRG";
	
}
