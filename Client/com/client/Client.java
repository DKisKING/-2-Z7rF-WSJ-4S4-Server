package com.client;

import java.applet.AppletContext;
import java.awt.AWTException;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Insets;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFrame;

import org.apache.commons.lang3.SystemUtils;

import com.client.definitions.AnimationDefinition;
import com.client.definitions.FloorOverlayDefinition;
import com.client.definitions.FloorUnderlayDefinition;
import com.client.definitions.GraphicsDefinition;
import com.client.definitions.ItemDefinition;
import com.client.definitions.NpcDefinition;
import com.client.definitions.ObjectDefinition;
import com.client.definitions.VarBit;
import com.client.features.EntityTarget;
import com.client.features.gametimers.GameTimer;
import com.client.features.gametimers.GameTimerHandler;
import com.client.gameframe.ScreenMode;
import com.client.graphics.BlackFadingScreen;
import com.client.graphics.FadingScreen;
import com.client.graphics.FadingScreenImage;
import com.client.interfaces.RSInterface;
import com.client.interfaces.impl.GrandExchange;
import com.client.sign.Signlink;

public class Client extends RSApplet {

	public static Sprite[] fadingScreenImages = new Sprite[8];

	private Pattern pattern;

	private Matcher matcher;

	private boolean placeHolders = true;
	
	public boolean debugModels = false;

	public boolean worldSelect = false;
	
	public static int currentWorld = 0;

	private FadingScreen fadingScreen;

	private EntityTarget entityTarget;

	private InformationFile informationFile = new InformationFile();

	public static boolean snowVisible = Configuration.CHRISTMAS ? true : false;

	public static int[][] runePouch = new int[][] { { -1, -1 }, { -1, -1 }, { -1, -1 } };
	
	public static int itemX = 0, itemY = 0;
	
	private static boolean shiftDrop = true;
	
	public static boolean shiftDown;
		
	private static boolean removeShiftDropOnMenuOpen;
	
//    @Override
//    public void keyPressed(KeyEvent event) {
//    	super.keyPressed(event);
//    	if(loggedIn) {
//    	stream.createFrame(186);
//    	stream.writeWord(event.getKeyCode());
//    	}
//    }

	/**
	 * The maximum boundary of the screen as specified by the
	 * {@link GraphicsEnvironment} utility class.
	 */
	public static final Rectangle MAXIMUM_SCREEN_BOUNDS = GraphicsEnvironment.getLocalGraphicsEnvironment()
			.getMaximumWindowBounds();

	public static ScreenMode currentScreenMode = ScreenMode.FIXED;

	
	
	public static int currentGameWidth = currentScreenMode.equals(ScreenMode.FIXED) ? 765 : 0;
	public static int currentGameHeight = currentScreenMode.equals(ScreenMode.FIXED) ? 503 : 0;

	/**
	 * Handles rune pouch input
	 * 
	 * @author Sky
	 * @param text
	 * @param frame
	 * @return
	 */
	public static boolean handleRunePouch(String text, int frame) {
		if (!(text.startsWith("#") && text.endsWith("$") && frame == 49999)) {
			return false;
		}
		try {
		//System.out.println("got here");
		text = text.replace("#", "");
		text = text.replace("$", "");
		String[] runes = text.split("-");
		for (int index = 0; index < runes.length; index++) {
			String[] args = runes[index].split(":");
			int id = Integer.parseInt(args[0]);
			int amt = Integer.parseInt(args[1]);
			runePouch[index][0] = id;
			runePouch[index][1] = amt;
		}
		return true;
		} catch(Exception e) {
			return true;
		}
	}

	private void setGameMode(ScreenMode mode) {
		if (currentScreenMode == mode)
			return;

		JFrame component = ClientWindow.getFrame();

		component.dispose();
		currentScreenMode = mode;
		if (mode.isUndecorated()) {
			component.setUndecorated(true);
			component.setLocation(0, 0);
			component.setVisible(true);
		} else {
			if (component.isUndecorated()) {
				component.setUndecorated(false);
				component.setVisible(true);
			}
		}
		component.setMinimumSize(new Dimension(765, mode == ScreenMode.FIXED ? 503 : 610));
		component.setResizable(mode.isResizable());
		Insets insets = ClientWindow.getInset();

		int clientWidth = mode.getWidth();
		int clientHeight = mode.getHeight();

		final int xInsetOffset = insets.left + insets.right;
		final int yInsetOffset = insets.top + insets.bottom;

		int totalClientWidth = clientWidth;
		int totalClientHeight = clientHeight;

		if (mode == ScreenMode.FULLSCREEN) {
			if (SystemUtils.IS_OS_MAC)
				component.setLocation(0, insets.top);
			totalClientWidth = (int) MAXIMUM_SCREEN_BOUNDS.getWidth();
			totalClientHeight = (int) MAXIMUM_SCREEN_BOUNDS.getHeight();
		} else {
			totalClientWidth += xInsetOffset;
			totalClientHeight += yInsetOffset;
		}

		component.setSize(totalClientWidth, totalClientHeight);

		currentGameWidth = totalClientWidth;
		currentGameHeight = totalClientHeight;

		if (mode != ScreenMode.FULLSCREEN)
			component.setLocationRelativeTo(null);

		component.setVisible(true);

		graphics = super.getGameComponent().getGraphics();

		updateGameScreen();
	}

	public void updateGameScreen() {
		Texture.method365(currentScreenMode == ScreenMode.FIXED ? 519 : currentGameWidth,
				currentScreenMode == ScreenMode.FIXED ? 165 : currentGameHeight);
		anIntArray1180 = Texture.anIntArray1472;
		Texture.method365(currentScreenMode == ScreenMode.FIXED ? 246 : currentGameWidth,
				currentScreenMode == ScreenMode.FIXED ? 335 : currentGameHeight);
		anIntArray1181 = Texture.anIntArray1472;
		Texture.method365(currentScreenMode == ScreenMode.FIXED ? 519 : currentGameWidth,
				currentScreenMode == ScreenMode.FIXED ? 338 : currentGameHeight);
		anIntArray1182 = Texture.anIntArray1472;
		int ai[] = new int[9];
		for (int i8 = 0; i8 < 9; i8++) {
			int k8 = 128 + i8 * 32 + 15;
			int l8 = 600 + k8 * 3;
			int i9 = Texture.anIntArray1470[k8];
			ai[i8] = l8 * i9 >> 16;
		}
		WorldController.method310(500, 800, currentScreenMode == ScreenMode.FIXED ? 519 : currentGameWidth, currentScreenMode == ScreenMode.FIXED ? 338 : currentGameHeight, ai);
		mainGameGraphicsBuffer = new RSImageProducer(currentScreenMode == ScreenMode.FIXED ? 519 : currentGameWidth, currentScreenMode == ScreenMode.FIXED ? 338 : currentGameHeight, getGameComponent());
		viewDistance = currentScreenMode == ScreenMode.FIXED ? 9 : 10;
	}

	private long experienceCounter;

	private Sprite[] minimapSprites = new Sprite[14];

	private Sprite mapBack;

	// private Background[] mapbackSprites = new Background[3];

	private Sprite[] smallXpSprites = new Sprite[22];

	private static final long serialVersionUID = 1L;
	private Sprite[] inputSprites = new Sprite[7];

	private int modifiableXValue = 1; // u dont care if it starts at 1? Can't see a real problem with it :P kk
	private int achievementCutoff = 100;
	private Sprite[] minimapIcons = new Sprite[2];
	public int port = Configuration.PORT; // ? 5555 : worldSelect == true ? 5555 : 43594;
	private String macAddress;

	public static void dumpModels() {
		int id = 6610;
		NpcDefinition npc = NpcDefinition.forID(id);
		System.out.println(npc.name);
		if (npc.models != null) {
			for (int modelid = 0; modelid < npc.models.length; modelid++) {
				System.out.println(npc.models[modelid]);
			}
		}
	}

	private static String intToKOrMilLongName(int i) {
		String s = String.valueOf(i);
		for (int k = s.length() - 3; k > 0; k -= 3)
			s = s.substring(0, k) + "," + s.substring(k);
		if (s.length() > 8)
			s = "@gre@" + s.substring(0, s.length() - 8) + " million @whi@(" + s + ")";
		else if (s.length() > 4)
			s = "@cya@" + s.substring(0, s.length() - 4) + "K @whi@(" + s + ")";
		return " " + s;
	}

	private static String intToKOrMil(int j) {
		if (j < 0x186a0)
			return String.valueOf(j);
		if (j < 0x989680)
			return j / 1000 + "K";
		else
			return j / 0xf4240 + "M";
	}

	public void stopMidi() {
		Signlink.midifade = 0;
		Signlink.midi = "stop";
	}

	public Sprite[] chatImages = new Sprite[2];
	public static int spellID = 0;
	public int MapX, MapY;

	public static void setTab(int id) {
		needDrawTabArea = true;
		tabID = id;
		tabAreaAltered = true;
	}

	public void setSidebarInterface(int sidebarID, int interfaceID) {
		tabInterfaceIDs[sidebarID] = interfaceID;
		tabID = sidebarID;
		needDrawTabArea = true;
		tabAreaAltered = true;
	}

	private boolean menuHasAddFriend(int j) {
		if (j < 0)
			return false;
		int k = menuActionID[j];
		if (k >= 2000)
			k -= 2000;
		return k == 337;
	}

	private Sprite channelButtons;

	private void drawChannelButtons() {
		String text[] = { "On", "Friends", "Off", "Hide" };
		int textColor[] = { 65280, 0xffff00, 0xff0000, 65535 };
		int yOffset = currentScreenMode == ScreenMode.FIXED ? 0 : currentGameHeight - 165;

		if (hideChatArea) {
			channelButtons.drawSprite(0, currentGameHeight - 23);
		}

		switch (channelButtonClickPosition) {
		case 0:
			chatButtons[1].drawSprite(5, 142 + yOffset);
			break;
		case 1:
			chatButtons[1].drawSprite(71, 142 + yOffset);
			break;
		case 2:
			chatButtons[1].drawSprite(137, 142 + yOffset);
			break;
		case 3:
			chatButtons[1].drawSprite(203, 142 + yOffset);
			break;
		case 4:
			chatButtons[1].drawSprite(269, 142 + yOffset);
			break;
		case 5:
			chatButtons[1].drawSprite(335, 142 + yOffset);
			break;
		}
		if (channelButtonHoverPosition == channelButtonClickPosition) {
			switch (channelButtonHoverPosition) {
			case 0:
				chatButtons[2].drawSprite(5, 142 + yOffset);
				break;
			case 1:
				chatButtons[2].drawSprite(71, 142 + yOffset);
				break;
			case 2:
				chatButtons[2].drawSprite(137, 142 + yOffset);
				break;
			case 3:
				chatButtons[2].drawSprite(203, 142 + yOffset);
				break;
			case 4:
				chatButtons[2].drawSprite(269, 142 + yOffset);
				break;
			case 5:
				chatButtons[2].drawSprite(335, 142 + yOffset);
				break;
			case 6:
				chatButtons[3].drawSprite(404, 142 + yOffset);
				break;
			}
		} else {
			switch (channelButtonHoverPosition) {
			case 0:
				chatButtons[0].drawSprite(5, 142 + yOffset);
				break;
			case 1:
				chatButtons[0].drawSprite(71, 142 + yOffset);
				break;
			case 2:
				chatButtons[0].drawSprite(137, 142 + yOffset);
				break;
			case 3:
				chatButtons[0].drawSprite(203, 142 + yOffset);
				break;
			case 4:
				chatButtons[0].drawSprite(269, 142 + yOffset);
				break;
			case 5:
				chatButtons[0].drawSprite(335, 142 + yOffset);
				break;
			case 6:
				chatButtons[3].drawSprite(404, 142 + yOffset);
				break;
			}
		}
		Date date = new Date();
		DateFormat dateNow = new SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH);
		DateFormat time = new SimpleDateFormat("h:mm a", Locale.ENGLISH);
		time.setTimeZone(TimeZone.getTimeZone("GMT+5"));
		smallText.method389(true, 422, 0xffffff, "" + dateNow.format(date), 152 + yOffset);
		smallText.method389(true, 424, 0xffffff, "Time: " + time.format(date), 162 + yOffset);
		smallText.method389(true, 26, 0xffffff, "All", 157 + yOffset);
		smallText.method389(true, 86, 0xffffff, "Game", 157 + yOffset);
		smallText.method389(true, 150, 0xffffff, "Public", 152 + yOffset);
		smallText.method389(true, 212, 0xffffff, "Private", 152 + yOffset);
		smallText.method389(true, 286, 0xffffff, "Clan", 152 + yOffset);
		smallText.method389(true, 349, 0xffffff, "Trade", 152 + yOffset);
		smallText.method382(textColor[publicChatMode], 164, text[publicChatMode], 163 + yOffset, true);
		smallText.method382(textColor[privateChatMode], 230, text[privateChatMode], 163 + yOffset, true);
		smallText.method382(textColor[clanChatMode], 296, text[clanChatMode], 163 + yOffset, true);
		smallText.method382(textColor[tradeMode], 362, text[tradeMode], 163 + yOffset, true);
	}

	ItemSearch grandExchangeItemSearch = null;

	public static int grandExchangeSearchScrollPostion;

	private static void handleGEItemSearchClick(int itemId) {
		// TODO Send Item Packet
		System.out.println("Clicking Item: " + itemId);
	}

	private void drawChatArea() {
		if (currentScreenMode == ScreenMode.FIXED)
			hideChatArea = false;

		if (currentScreenMode == ScreenMode.FIXED && loginScreenGraphicsBuffer == null)
			chatAreaGraphicsBuffer.initDrawingArea();

		final int yOffset = currentScreenMode == ScreenMode.FIXED ? 0 : currentGameHeight - 165;
		final int yOffset2 = currentScreenMode == ScreenMode.FIXED ? 338 : 0;

		Texture.anIntArray1472 = anIntArray1180;

		if (hideChatArea)
			drawChannelButtons();

		if (!hideChatArea) {
			chatArea.drawSprite(0, yOffset);
			drawChannelButtons();

			TextDrawingArea textDrawingArea = aTextDrawingArea_1271;
			if (super.saveClickX >= 0 && super.saveClickX <= 518) {
				if (super.saveClickY >= (currentScreenMode == ScreenMode.FIXED ? 343 : currentGameHeight - 164)
						&& super.saveClickY <= (currentScreenMode == ScreenMode.FIXED ? 484 : currentGameHeight - 30)) {
					if (this.isFieldInFocus()) {
						Client.inputString = "";
						this.resetInputFieldFocus();
					}
				}
			}
			if (messagePromptRaised) {
				newBoldFont.drawCenteredString(aString1121, 259, 60 + yOffset, 0, -1);
				newBoldFont.drawCenteredString(promptInput + "*", 259, 80 + yOffset, 128, -1);
			} else if (inputDialogState == 1) {
				newBoldFont.drawCenteredString("Enter amount:", 259, 60 + yOffset, 0, -1);
				newBoldFont.drawCenteredString(amountOrNameInput + "*", 259, 80 + yOffset, 128, -1);
			} else if (inputDialogState == 2) {
				newBoldFont.drawCenteredString("Enter name:", 259, 60 + yOffset, 0, -1);
				newBoldFont.drawCenteredString(amountOrNameInput + "*", 259, 80 + yOffset, 128, -1);
			} else if (inputDialogState == 3) {
				DrawingArea.fillPixels(8, 505, 108, 0x463214, 28 + yOffset);
				DrawingArea.drawAlphaBox(8, 28 + yOffset, 505, 108, 0x746346, 75);

				newBoldFont.drawCenteredString("@bla@What would you like to buy? @blu@" + amountOrNameInput + "*", 259,
						20 + yOffset, 128, -1);
				if (amountOrNameInput != "") {
					grandExchangeItemSearch = new ItemSearch(amountOrNameInput, 100, true);

					final int xPosition = 15;
					final int yPosition = 32 + yOffset - grandExchangeSearchScrollPostion;
					int rowCountX = 0;
					int rowCountY = 0;

					int itemAmount = grandExchangeItemSearch.getItemSearchResultAmount();

					if (amountOrNameInput.length() == 0) {
						newRegularFont.drawCenteredString("Start typing the name of an item to search for it.", 259,
								70 + yOffset, 0, -1);
					} else if (itemAmount == 0) {
						newRegularFont.drawCenteredString("No matching items found!", 259, 70 + yOffset, 0, -1);
					} else {
						DrawingArea.setDrawingArea(134 + yOffset, 8, 497, 29 + yOffset);
						for (int itemId = 0; itemId < itemAmount; itemId++) {
							int itemResults[] = grandExchangeItemSearch.getItemSearchResults();

							if (itemResults[itemId] != -1) {
								final int startX = xPosition + rowCountX * 160;
								final int startY = yPosition + rowCountY * 35;
								Sprite itemSprite = ItemDefinition.getSprite(itemResults[itemId], 1, 0);
								if (itemSprite != null)
									itemSprite.drawSprite(startX, startY);

								ItemDefinition itemDef = ItemDefinition.forID(itemResults[itemId]);
								newRegularFont.drawBasicString(itemDef.name, startX + 40, startY + 14, 0, -1);

								if (super.mouseX >= startX && super.mouseX <= startX + 160) {
									if (super.mouseY >= (startY + yOffset2)
											&& super.mouseY <= (startY + yOffset2) + 35) {
										DrawingArea.drawAlphaBox(startX, startY, 160, 35, 0xFFFFFF, 120);

										if (super.clickMode3 == 1)
											handleGEItemSearchClick(itemDef.id);
									}
								}
							}

							rowCountX++;

							if (rowCountX > 2) {
								rowCountY++;
								rowCountX = 0;
							}
						}
						DrawingArea.defaultDrawingAreaSize();
					}

					int maxScrollPosition = itemAmount / 3 * 35;
					if (itemAmount > 9)
						drawScrollbar(106, grandExchangeSearchScrollPostion > maxScrollPosition ? 0
								: grandExchangeSearchScrollPostion, 29 + yOffset, 496, itemAmount / 3 * 35);

				}
			} else if (aString844 != null) {
				newBoldFont.drawCenteredString(aString844, 259, 60 + yOffset, 0, -1);
				newBoldFont.drawCenteredString("Click to continue", 259, 80 + yOffset, 128, -1);
			} else if (backDialogID != -1) {
				drawInterface(0, 20, RSInterface.interfaceCache[backDialogID], 20 + yOffset);
			} else if (dialogID != -1) {
				drawInterface(0, 20, RSInterface.interfaceCache[dialogID], 20 + yOffset);
			} else {
				int j77 = -3;
				int j = 0;
				DrawingArea.setDrawingArea(122 + yOffset, 8, 497, 7 + yOffset);
				for (int k = 0; k < 500; k++)
					if (chatMessages[k] != null) {
						// System.out.println(chatMessages[k]);
						int chatType = chatTypes[k];
						int yPos = (70 - j77 * 14) + anInt1089 + 5;
						String s1 = chatNames[k];
						byte byte0 = 0;
						if (s1.startsWith("@cr")) {
							String s2 = s1.substring(3, s1.length());
							int index = s2.indexOf("@");
							if (index != -1) {
								s2 = s2.substring(0, index);
								byte0 = Byte.parseByte(s2);
								s1 = s1.substring(4 + s2.length());
							}
						}
						if (chatType == 0) {
							if (chatTypeView == 5 || chatTypeView == 0) {
								if (yPos > 0 && yPos < 210) {
									newRegularFont.drawBasicString(chatMessages[k], 11, yPos - 1 + yOffset, 0, -1);
								}
								j++;
								j77++;
							}
						}
						if ((chatType == 1 || chatType == 2) && (chatType == 1 || publicChatMode == 0
								|| publicChatMode == 1 && isFriendOrSelf(s1))) {
							if (chatTypeView == 1 || chatTypeView == 0) {
								if (yPos > 0 && yPos < 210) {
									int xPos = 11;
									if (byte0 > 0) {
										for (int right = 0; right < modIcons.length; right++) {
											if (right == (byte0 - 1) && modIcons[right] != null) {
												modIcons[right].drawAdvancedSprite(xPos,
														yPos + yOffset - modIcons[right].myHeight);
												xPos += modIcons[right].myWidth;
												break;
											}
										}
									}
									newRegularFont.drawBasicString(s1 + ":", xPos, yPos - 1 + yOffset, 0, -1);
									xPos += newRegularFont.getTextWidth(s1) + 5;
									newRegularFont.drawBasicString(chatMessages[k], xPos, yPos - 1 + yOffset, 255, -1);
								}
								j++;
								j77++;
							}
						}
						if ((chatType == 3 || chatType == 7) && (!splitPrivateChat || chatTypeView == 2)
								&& (chatType == 7 || privateChatMode == 0
										|| privateChatMode == 1 && isFriendOrSelf(s1))) {
							if (chatTypeView == 2 || chatTypeView == 0) {
								if (yPos > 0 && yPos < 210) {
									int k1 = 11;
									newRegularFont.drawBasicString("From", k1, yPos - 1 + yOffset, 0, -1);
									k1 += textDrawingArea.getTextWidth("From ");
									if (byte0 > 0) {
										for (int right = 0; right < modIcons.length; right++) {
											if (right == (byte0 - 1) && modIcons[right] != null) {
												modIcons[right].drawAdvancedSprite(k1,
														yPos + yOffset - modIcons[right].myHeight);
												k1 += modIcons[right].myWidth;
												break;
											}
										}
									}
									newRegularFont.drawBasicString(s1 + ":", k1, yPos - 1 + yOffset, 0, -1);
									k1 += newRegularFont.getTextWidth(s1) + 8;
									newRegularFont.drawBasicString(chatMessages[k], k1, yPos - 1 + yOffset, 0x800000, -1);
								}
								j++;
								j77++;
							}
						}
						if (chatType == 4 && (tradeMode == 0 || tradeMode == 1 && isFriendOrSelf(s1))) {
							if (chatTypeView == 3 || chatTypeView == 0) {
								if (yPos > 0 && yPos < 210)
									newRegularFont.drawBasicString(s1 + " " + chatMessages[k], 11, yPos - 1 + yOffset,
											0x800080, -1);
								j++;
								j77++;
							}
						}
						if (chatType == 5 && !splitPrivateChat && privateChatMode < 2) {
							if (chatTypeView == 2 || chatTypeView == 0) {
								if (yPos > 0 && yPos < 210)
									newRegularFont.drawBasicString(chatMessages[k], 11, yPos - 1 + yOffset, 0x800000,
											-1);
								j++;
								j77++;
							}
						}
						if (chatType == 6 && (!splitPrivateChat || chatTypeView == 2) && privateChatMode < 2) {
							if (chatTypeView == 2 || chatTypeView == 0) {
								if (yPos > 0 && yPos < 210) {
									int k1 = 15;
									k1 += textDrawingArea.getTextWidth("To " + s1);
									newRegularFont.drawBasicString("To " + s1 + ":", 11, yPos - 1 + yOffset, 0, -1);
									//newRegularFont.drawBasicString(chatMessages[k], 15 + newRegularFont.getTextWidth("To :" + s1), yPos, 0x800000, -1);
									newRegularFont.drawBasicString(chatMessages[k], k1, yPos - 1 + yOffset, 0x800000, -1);
								}
								j++;
								j77++;
							}
						}
						if (chatType == 11 && (clanChatMode == 0)) {
							if (chatTypeView == 11 || chatTypeView == 0) {
								if (yPos > 0 && yPos < 210)
									newRegularFont.drawBasicString(s1 + " " + chatMessages[k], 8, yPos - 1 + yOffset,
											0x7e3200, -1);
								j++;
								j77++;
							}
						}
						if (chatType == 13 && chatTypeView == 12) {
							if (yPos > 0 && yPos < 210)
								newRegularFont.drawBasicString(s1 + " " + chatMessages[k], 8, yPos - 1 + yOffset,
										0x7e3200, -1);
							j++;
							j77++;
						}
						if (chatType == 8 && (tradeMode == 0 || tradeMode == 1 && isFriendOrSelf(s1))) {
							if (chatTypeView == 3 || chatTypeView == 0) {
								if (yPos > 0 && yPos < 210)
									newRegularFont.drawBasicString(s1 + " " + chatMessages[k], 11, yPos - 1 + yOffset,
											0x7e3200, -1);
								j++;
								j77++;
							}
							if (chatType == 11 && (clanChatMode == 0)) {
								if (chatTypeView == 11) {
									if (yPos > 0 && yPos < 110)
										newRegularFont.drawBasicString(s1 + " " + chatMessages[k], 19,
												yPos - 1 + yOffset, 0x7e3200, -1);
									j++;
									j77++;
								}
							}
							if (chatType == 12) {
								if (chatTypeView == 11 || chatTypeView == 0) {
									if (yPos > 3 && yPos < 130) {
										String title = "<col=0000FF>" + clanTitles[k] + "</col>";
										String username = (chatRights[k] > 0 ? "<img=" + (chatRights[k] - 1) + ">" : "")
												+ TextClass.fixName(chatNames[k]);
										String message = "<col=800000>" + chatMessages[k] + "</col>";
										newRegularFont.drawBasicString("[" + title + "] " + username + ": " + message,
												11, yPos - 1 + yOffset, 0, -1);
									}
									j++;
									j77++;
								}
							}
						}
						if (chatType == 16) {
							int j2 = 40 + 11;
							String clanname = clanList[k];
							int clanNameWidth = textDrawingArea.getTextWidth(clanname);
							if (chatTypeView == 11 || chatTypeView == 0) {
								if (yPos > 0 && yPos < 110)
									switch (chatRights[k]) {
									case 1:
										j2 += clanNameWidth;
										modIcons[0].drawSprite(j2 - 18, yPos + yOffset);
										j2 += 15;
										break;
									case 2:
										j2 += clanNameWidth;
										modIcons[2].drawSprite(j2 - 18, yPos + yOffset);
										j2 += 15;
										break;
									case 3:
										j2 += clanNameWidth;
										modIcons[1].drawSprite(j2 - 18, yPos + yOffset);
										j2 += 15;
										break;
									case 4:
										j2 += clanNameWidth;
										modIcons[3].drawSprite(j2 - 18, yPos + yOffset);
										j2 += 15;
										break;

									default:
										j2 += clanNameWidth;
										break;
									}
								newRegularFont.drawBasicString("[", 19, yPos - 1 + yOffset, 0, -1);
								newRegularFont.drawBasicString("]", clanNameWidth + 16 + 11, yPos - 1 + yOffset, 0, -1);
								newRegularFont.drawBasicString("" + capitalize(clanname) + "", 25, yPos - 1 + yOffset,
										255, -1);
								newRegularFont.drawBasicString(chatNames[k] + ":", j2 - 17, yPos);
								j2 += newRegularFont.getTextWidth(chatNames[k]) + 7;
								newRegularFont.drawBasicString(capitalize(chatMessages[k]), j2 - 16, yPos - 1 + yOffset,
										0x800000, -1);

								j++;
								j77++;
							}
						}
					}
				DrawingArea.defaultDrawingAreaSize();
				chatAreaScrollLength = j * 14 + 7 + 5;
				if (chatAreaScrollLength < 111)
					chatAreaScrollLength = 111;
				drawScrollbar(114, chatAreaScrollLength - anInt1089 - 113, 7 + yOffset, 496, chatAreaScrollLength);
				String fixedString;
				if (myPlayer != null && myPlayer.name != null) {
					if (myPlayer.title.length() > 0) {
						fixedString = "<col=" + myPlayer.titleColor + ">" + myPlayer.title + "</col>" + " "
								+ myPlayer.name;
					} else {
						fixedString = myPlayer.name;
					}
				} else {
					fixedString = TextClass.fixName(capitalize(myUsername));
				}
				String s;
				if (myPlayer != null && myPlayer.name != null && myPlayer.title.length() > 1)
					s = myPlayer.title + " " + myPlayer.name;
				else
					s = TextClass.fixName(capitalize(myUsername));
				int xOffset = 10;
				if (myPlayer.getRights() > 0) {
					modIcons[myPlayer.getRights() - 1].drawSprite(10, 122 + yOffset);
					newRegularFont.drawBasicString(fixedString + "", 23, 133 + yOffset, 0, -1);
					xOffset += modIcons[myPlayer.getRights() - 1].myWidth;
				} else {
					xOffset += 1;
					newRegularFont.drawBasicString(fixedString + "", 11, 133 + yOffset, 0, -1);
				}
				textDrawingArea.method385(0, ": ", 133 + yOffset, (xOffset + textDrawingArea.getTextWidth(s)));
				if (!isFieldInFocus())
					newRegularFont.drawBasicString(inputString + ((loopCycle % 40 < 20) ? "|" : ""),
							xOffset + textDrawingArea.getTextWidth(s + ": "), 133 + yOffset, 255, -1);
				DrawingArea.method339(121 + yOffset, 0x807660, 506, 7);
			}

		}

		if (menuOpen && menuScreenArea == 2)
			drawMenu();

		if (currentScreenMode == ScreenMode.FIXED)
			chatAreaGraphicsBuffer.drawGraphics(0, 338, super.graphics);

		mainGameGraphicsBuffer.initDrawingArea();
		Texture.anIntArray1472 = anIntArray1182;
	}

	private String clanUsername;
	private String clanMessage;
	private String clanTitle;
	private final String[] clanTitles;
	private int channelRights;

	@Override
	public void init() {
		try {
			nodeID = 1;
			portOff = 0;
			setHighMem();
			isMembers = true;
			Signlink.storeid = 32;
			Signlink.startpriv(InetAddress.getLocalHost());
			initClientFrame(currentScreenMode.getWidth(), currentScreenMode.getHeight());
			instance = this;
			GroundItemColors.loadGroundItems();
		} catch (Exception exception) {
			return;
		}
	}

	@Override
	public void startRunnable(Runnable runnable, int i) {
		if (i > 10)
			i = 10;
		if (Signlink.mainapp != null) {
			Signlink.startthread(runnable, i);
		} else {
			super.startRunnable(runnable, i);
		}
	}

	public Socket openSocket(int port) throws IOException {
		return new Socket(InetAddress.getByName(server), port);
	}

	public boolean processMenuClick() {
		if (activeInterfaceType != 0)
			return false;
		int j = super.clickMode3;
		if (spellSelected == 1 && super.saveClickX >= 516 && super.saveClickY >= 160 && super.saveClickX <= 765
				&& super.saveClickY <= 205)
			j = 0;
		if (menuOpen) {
			if (j != 1) {
				int k = super.mouseX;
				int j1 = super.mouseY;
				if (menuScreenArea == 0) {
					k -= 0;
					j1 -= 0;
				}
				if (menuScreenArea == 1) {
					k -= 519;
					j1 -= 168;
				}
				if (menuScreenArea == 2) {
					k -= 17;
					j1 -= 338;
				}
				if (menuScreenArea == 3) {
					k -= 519;
					j1 -= 0;
				}
				if (k < menuOffsetX - 10 || k > menuOffsetX + menuWidth + 10 || j1 < menuOffsetY - 10
						|| j1 > menuOffsetY + menuHeight + 10) {
					menuOpen = false;
					if (menuScreenArea == 1)
						needDrawTabArea = true;
					if (menuScreenArea == 2)
						inputTaken = true;
				}
			}
			if (j == 1) {
				int l = menuOffsetX;
				int k1 = menuOffsetY;
				int i2 = menuWidth;
				int k2 = super.saveClickX;
				int l2 = super.saveClickY;
				if (menuScreenArea == 0) {
					k2 -= 0;
					l2 -= 0;
				}
				if (menuScreenArea == 1) {
					k2 -= 519;
					l2 -= 168;
				}
				if (menuScreenArea == 2) {
					k2 -= 17;
					l2 -= 338;
				}
				if (menuScreenArea == 3) {
					k2 -= 519;
					l2 -= 0;
				}
				int i3 = -1;
				for (int j3 = 0; j3 < menuActionRow; j3++) {
					int k3 = k1 + 31 + (menuActionRow - 1 - j3) * 15;
					if (k2 > l && k2 < l + i2 && l2 > k3 - 13 && l2 < k3 + 3)
						i3 = j3;
				}
				if (i3 != -1)
					doAction(i3);
				menuOpen = false;
				if (menuScreenArea == 1)
					needDrawTabArea = true;
				if (menuScreenArea == 2) {
					inputTaken = true;
				}
			}
			return true;
		} else {
			if (j == 1 && menuActionRow > 0) {
				int i1 = menuActionID[menuActionRow - 1];
				if (i1 == 632 || i1 == 78 || i1 == 867 || i1 == 431 || i1 == 53 || i1 == 74 || i1 == 454 || i1 == 539
						|| i1 == 493 || i1 == 847 || i1 == 447 || i1 == 1125) {
					int l1 = menuActionCmd2[menuActionRow - 1];
					int j2 = menuActionCmd3[menuActionRow - 1];
					RSInterface class9 = RSInterface.interfaceCache[j2];
					if (class9.aBoolean259 || class9.aBoolean235) {
						aBoolean1242 = false;
						anInt989 = 0;
						anInt1084 = j2;
						anInt1085 = l1;
						activeInterfaceType = 2;
						anInt1087 = super.saveClickX;
						anInt1088 = super.saveClickY;
						if (RSInterface.interfaceCache[j2].parentID == openInterfaceID)
							activeInterfaceType = 1;
						if (RSInterface.interfaceCache[j2].parentID == backDialogID)
							activeInterfaceType = 3;
						return true;
					}
				}
			}
			if (j == 1 && (anInt1253 == 1 || menuHasAddFriend(menuActionRow - 1)) && menuActionRow > 2)
				j = 2;
			if (j == 1 && menuActionRow > 0)
				doAction(menuActionRow - 1);
			if (j == 2 && menuActionRow > 0)
				determineMenuSize();
				minimapHovers();
			return false;
		}
	}
	
	private final void minimapHovers() {
 		final boolean fixed = currentScreenMode == ScreenMode.FIXED;
		prayHover = fixed ? super.mouseX >= 545 && super.mouseX <= 569 && super.mouseY >= 90 && super.mouseY < 114 : super.mouseX >= currentGameWidth - 207 && super.mouseX <= currentGameWidth - 151 && super.mouseY >= 105 && super.mouseY < 139;
		counterHover = fixed ? super.mouseX >= 522 && super.mouseX <= 544 && super.mouseY >= 20 && super.mouseY <= 47 : super.mouseX >= currentGameWidth - 205 && super.mouseX <= currentGameWidth - 184 && super.mouseY >= 27 && super.mouseY <= 44;
		worldHover = fixed ? super.mouseX >= 718 && super.mouseX <= 748 && super.mouseY >= 22 && super.mouseY <= 50 : super.mouseX >= currentGameWidth - 117 && super.mouseX <= currentGameWidth - 86 && super.mouseY >= 153 && super.mouseY <= 186;
	}
	
	private boolean prayHover, prayClicked, counterOn, counterHover, worldHover;

	public static int totalRead = 0;

	public static String getFileNameWithoutExtension(String fileName) {
		File tmpFile = new File(fileName);
		tmpFile.getName();
		int whereDot = tmpFile.getName().lastIndexOf('.');
		if (0 < whereDot && whereDot <= tmpFile.getName().length() - 2) {
			return tmpFile.getName().substring(0, whereDot);
		}
		return "";
	}

	public String indexLocation(int cacheIndex, int index) {
		return Signlink.findCacheDir() + "index" + cacheIndex + "/" + (index != -1 ? index + ".gz" : "");
	}

	/**
	 * Packing new data to the cache
	 * #1 - Models
	 * #2 - Animations
	 * #4 - Maps
	 * @param cacheIndex
	 */
	public void repackCacheIndex(int cacheIndex) {
		System.out.println("Started repacking index " + cacheIndex + ".");
		int indexLength = new File(indexLocation(cacheIndex, -1)).listFiles().length;
		File[] file = new File(indexLocation(cacheIndex, -1)).listFiles();
		try {
			for (int index = 0; index < indexLength; index++) {
				int fileIndex = Integer.parseInt(getFileNameWithoutExtension(file[index].toString()));
				byte[] data = fileToByteArray(cacheIndex, fileIndex);
				if(data != null && data.length > 0) {
					decompressors[cacheIndex].method234(data.length, data, fileIndex);
					System.out.println("Repacked " + fileIndex + ".");
				} else {
					System.out.println("Unable to locate index " + fileIndex + ".");
				}
			}
		} catch(Exception e) {
			System.out.println("Error packing cache index " + cacheIndex + ".");
		}
		System.out.println("Finished repacking " + cacheIndex + ".");
	}

	public byte[] fileToByteArray(int cacheIndex, int index) {
		try {
			if (indexLocation(cacheIndex, index).length() <= 0 || indexLocation(cacheIndex, index) == null) {
				return null;
			}
			File file = new File(indexLocation(cacheIndex, index));
			byte[] fileData = new byte[(int)file.length()];
			FileInputStream fis = new FileInputStream(file);
			fis.read(fileData);
			fis.close();
			return fileData;
		} catch(Exception e) {
			return null;
		}
	}

	public void preloadModels() {
		File models = new File(Signlink.findCacheDir() + "/Raw/");
		File[] modelList = models.listFiles();
		for (int model = 0; model < modelList.length; model++) {
			String modelID = modelList[model].getName();
			byte[] modelData = ReadFile(Signlink.findCacheDir() + "/Raw/" + modelID);
			Model.method460(modelData, Integer.parseInt(getFileNameWithoutExtension(modelID)));
		}
	}

	public static final byte[] ReadFile(String s) {
		try {
			byte abyte0[];
			File file = new File(s);
			int i = (int) file.length();
			abyte0 = new byte[i];
			DataInputStream datainputstream = new DataInputStream(new BufferedInputStream(new FileInputStream(s)));
			datainputstream.readFully(abyte0, 0, i);
			datainputstream.close();
			totalRead++;
			return abyte0;
		} catch (Exception e) {
			System.out.println((new StringBuilder()).append("Read Error: ").append(s).toString());
			return null;
		}
	}

	public void addModels() {
		for (int ModelIndex = 0; ModelIndex < 50000; ModelIndex++) {
			byte[] abyte0 = getModel(ModelIndex);
			if (abyte0 != null && abyte0.length > 0) {
				decompressors[1].method234(abyte0.length, abyte0, ModelIndex);
			}
		}
	}

	public byte[] getModel(int Index) {
		try {
			File Model = new File(Signlink.findCacheDir() + "./pModels/" + Index + ".gz");
			byte[] aByte = new byte[(int) Model.length()];
			FileInputStream fis = new FileInputStream(Model);
			fis.read(aByte);
			System.out.println("" + Index + " aByte = [" + aByte + "]!");
			fis.close();
			return aByte;
		} catch (Exception e) {
			return null;
		}
	}

	public void addMaps() {
		for (int MapIndex = 0; MapIndex < 3536; MapIndex++) {
			byte[] abyte0 = getMaps(MapIndex);
			if (abyte0 != null && abyte0.length > 0) {
				decompressors[4].method234(abyte0.length, abyte0, MapIndex);
				System.out.println("Maps Added");
			}
		}
	}

	public byte[] getMaps(int Index) {
		try {
			File Map = new File(Signlink.findCacheDir() + "./maps/" + Index + ".gz");
			byte[] aByte = new byte[(int) Map.length()];
			FileInputStream fis = new FileInputStream(Map);
			fis.read(aByte);
			pushMessage("aByte = [" + aByte + "]!", 0, "");
			fis.close();
			return aByte;
		} catch (Exception e) {
			return null;
		}
	}

	public void saveMidi(boolean flag, byte abyte0[]) {
		Signlink.midifade = flag ? 1 : 0;
		Signlink.midisave(abyte0, abyte0.length);
	}

	public int positions[] = new int[2000];
	public int landScapes[] = new int[2000];
	public int objects[] = new int[2000];

	public final void method22() {
		try {
			anInt985 = -1;
			aClass19_1056.removeAll();
			aClass19_1013.removeAll();
			Texture.method366();
			unlinkMRUNodes();
			worldController.initToNull();
			System.gc();
			load_holiday_objects();
			load_objects();
			for (int i = 0; i < 4; i++)
				aClass11Array1230[i].method210();

			for (int l = 0; l < 4; l++) {
				for (int k1 = 0; k1 < 104; k1++) {
					for (int j2 = 0; j2 < 104; j2++)
						byteGroundArray[l][k1][j2] = 0;
				}
			}
			ObjectManager objectManager = new ObjectManager(byteGroundArray, intGroundArray);

			int k2 = aByteArrayArray1183.length;

			/*
			 * int k18 = 62; for (int A = 0; A < k2; A++) for (int B = 0; B <
			 * 2000; B++) if (anIntArray1234[A] == positions[B]) {
			 * anIntArray1235[A] = landScapes[B]; anIntArray1236[A] =
			 * objects[B]; }
			 */

			stream.createFrame(0);

			if (!aBoolean1159) {
				for (int i3 = 0; i3 < k2; i3++) {
					int i4 = (anIntArray1234[i3] >> 8) * 64 - baseX;
					int k5 = (anIntArray1234[i3] & 0xff) * 64 - baseY;

					byte abyte0[] = aByteArrayArray1183[i3];

					if (abyte0 != null)
						objectManager.method180(abyte0, k5, i4, (mapRegionsX - 6) * 8, (mapRegionsY - 6) * 8,
								aClass11Array1230);
				}

				for (int j4 = 0; j4 < k2; j4++) {
					int l5 = (anIntArray1234[j4] >> 8) * 64 - baseX;
					int k7 = (anIntArray1234[j4] & 0xff) * 64 - baseY;
					byte abyte2[] = aByteArrayArray1183[j4];
					if (abyte2 == null && mapRegionsY < 800)
						objectManager.method174(k7, 64, 64, l5);
				}

				anInt1097++;
				if (anInt1097 > 160) {
					anInt1097 = 0;
					stream.createFrame(238);
					stream.writeWordBigEndian(96);

				}
				stream.createFrame(0);

				for (int i6 = 0; i6 < k2; i6++) {
					byte abyte1[] = aByteArrayArray1247[i6];
					if (abyte1 != null) {
						int l8 = (anIntArray1234[i6] >> 8) * 64 - baseX;
						int k9 = (anIntArray1234[i6] & 0xff) * 64 - baseY;
						objectManager.method190(l8, aClass11Array1230, k9, worldController, abyte1);
					}
				}

			}
			if (aBoolean1159) {
				for (int j3 = 0; j3 < 4; j3++) {
					for (int k4 = 0; k4 < 13; k4++) {
						for (int j6 = 0; j6 < 13; j6++) {
							int l7 = anIntArrayArrayArray1129[j3][k4][j6];
							if (l7 != -1) {
								int i9 = l7 >> 24 & 3;
								int l9 = l7 >> 1 & 3;
								int j10 = l7 >> 14 & 0x3ff;
								int l10 = l7 >> 3 & 0x7ff;
								int j11 = (j10 / 8 << 8) + l10 / 8;
								for (int l11 = 0; l11 < anIntArray1234.length; l11++) {
									if (anIntArray1234[l11] != j11 || aByteArrayArray1183[l11] == null)
										continue;
									objectManager.method179(i9, l9, aClass11Array1230, k4 * 8, (j10 & 7) * 8,
											aByteArrayArray1183[l11], (l10 & 7) * 8, j3, j6 * 8);
									break;
								}

							}
						}

					}

				}

				for (int l4 = 0; l4 < 13; l4++) {
					for (int k6 = 0; k6 < 13; k6++) {
						int i8 = anIntArrayArrayArray1129[0][l4][k6];
						if (i8 == -1)
							objectManager.method174(k6 * 8, 8, 8, l4 * 8);
					}

				}

				stream.createFrame(0);

				for (int l6 = 0; l6 < 4; l6++) {
					for (int j8 = 0; j8 < 13; j8++) {
						for (int j9 = 0; j9 < 13; j9++) {
							int i10 = anIntArrayArrayArray1129[l6][j8][j9];
							if (i10 != -1) {
								int k10 = i10 >> 24 & 3;
								int i11 = i10 >> 1 & 3;
								int k11 = i10 >> 14 & 0x3ff;
								int i12 = i10 >> 3 & 0x7ff;
								int j12 = (k11 / 8 << 8) + i12 / 8;
								for (int k12 = 0; k12 < anIntArray1234.length; k12++) {
									if (anIntArray1234[k12] != j12 || aByteArrayArray1247[k12] == null)
										continue;
									objectManager.method183(aClass11Array1230, worldController, k10, j8 * 8,
											(i12 & 7) * 8, l6, aByteArrayArray1247[k12], (k11 & 7) * 8, i11, j9 * 8);
									break;
								}

							}
						}

					}

				}

			}
			stream.createFrame(0);
			objectManager.method171(aClass11Array1230, worldController);
			mainGameGraphicsBuffer.initDrawingArea();
			stream.createFrame(0);

			int k3 = ObjectManager.anInt145;
			if (k3 > plane)
				k3 = plane;

			if (k3 < plane - 1)
				k3 = plane - 1;
			if (lowMem)

				worldController.method275(ObjectManager.anInt145);
			else
				worldController.method275(0);
			for (int i5 = 0; i5 < 104; i5++) {
				for (int i7 = 0; i7 < 104; i7++) {
					spawnGroundItem(i5, i7);
				}
			}

			anInt1051++;
			if (anInt1051 > 98) {
				anInt1051 = 0;
				stream.createFrame(150);

			}
			method63();
		} catch (Exception e) {
			e.printStackTrace();
		}
		ObjectDefinition.mruNodes1.unlinkAll();
		if (lowMem && Signlink.cache_dat != null) {
			int j = onDemandFetcher.getVersionCount(0);
			for (int i1 = 0; i1 < j; i1++) {
				int l1 = onDemandFetcher.getModelIndex(i1);
				if ((l1 & 0x79) == 0)
					Model.method461(i1);
			}

		}
		stream.createFrame(210);
		stream.writeDWord(0x3f008edd);
		System.gc();
		Texture.method367();
		onDemandFetcher.method566();

		int k = (mapRegionsX - 6) / 8 - 1;
		int j1 = (mapRegionsX + 6) / 8 + 1;
		int i2 = (mapRegionsY - 6) / 8 - 1;
		int l2 = (mapRegionsY + 6) / 8 + 1;
		if (aBoolean1141) {
			k = 49;
			j1 = 50;
			i2 = 49;
			l2 = 50;
		}
		for (int l3 = k; l3 <= j1; l3++) {
			for (int j5 = i2; j5 <= l2; j5++)
				if (l3 == k || l3 == j1 || j5 == i2 || j5 == l2) {
					int j7 = onDemandFetcher.method562(0, j5, l3);
					if (j7 != -1)
						onDemandFetcher.method560(j7, 3);
					int k8 = onDemandFetcher.method562(1, j5, l3);
					if (k8 != -1)
						onDemandFetcher.method560(k8, 3);
				}

		}

	}

	public void unlinkMRUNodes() {
		ObjectDefinition.mruNodes1.unlinkAll();
		ObjectDefinition.mruNodes2.unlinkAll();
		NpcDefinition.mruNodes.unlinkAll();
		ItemDefinition.mruNodes2.unlinkAll();
		ItemDefinition.mruNodes1.unlinkAll();
		Player.mruNodes.unlinkAll();
		GraphicsDefinition.aMRUNodes_415.unlinkAll();
	}

	public void method24(int i) {
		int ai[] = minimapImage.myPixels;
		int j = ai.length;
		for (int k = 0; k < j; k++)
			ai[k] = 0;

		for (int l = 1; l < 103; l++) {
			int i1 = 24628 + (103 - l) * 512 * 4;
			for (int k1 = 1; k1 < 103; k1++) {
				if ((byteGroundArray[i][k1][l] & 0x18) == 0)
					worldController.method309(ai, i1, i, k1, l);
				if (i < 3 && (byteGroundArray[i + 1][k1][l] & 8) != 0)
					worldController.method309(ai, i1, i + 1, k1, l);
				i1 += 4;
			}

		}

		int j1 = ((238 + (int) (Math.random() * 20D)) - 10 << 16) + ((238 + (int) (Math.random() * 20D)) - 10 << 8)
				+ ((238 + (int) (Math.random() * 20D)) - 10);
		int l1 = (238 + (int) (Math.random() * 20D)) - 10 << 16;
		minimapImage.method343();
		for (int i2 = 1; i2 < 103; i2++) {
			for (int j2 = 1; j2 < 103; j2++) {
				if ((byteGroundArray[i][j2][i2] & 0x18) == 0)
					method50(i2, j1, j2, l1, i);
				if (i < 3 && (byteGroundArray[i + 1][j2][i2] & 8) != 0)
					method50(i2, j1, j2, l1, i + 1);
			}

		}

		mainGameGraphicsBuffer.initDrawingArea();
		mapIconAmount = 0;
		for (int k2 = 0; k2 < 104; k2++) {
			for (int l2 = 0; l2 < 104; l2++) {
				int i3 = worldController.method303(plane, k2, l2);
				if (i3 != 0) {
					i3 = i3 >> 14 & 0x7fff;
					int j3 = ObjectDefinition.forID(i3).anInt746;
					if (j3 >= 0) {
						int k3 = k2;
						int l3 = l2;
						if (j3 != 22 && j3 != 29 && j3 != 34 && j3 != 36 && j3 != 46 && j3 != 47 && j3 != 48) {
							// byte byte0 = 104;
							// byte byte1 = 104;
							// int ai1[][] =
							// aClass11Array1230[plane].anIntArrayArray294;
							// for (int i4 = 0; i4 < 10; i4++) {
							// int j4 = (int) (Math.random() * 4D);
							// if (j4 == 0 && k3 > 0 && k3 > k2 - 3
							// && (ai1[k3 - 1][l3] & 0x1280108) == 0)
							// k3--;
							// if (j4 == 1 && k3 < byte0 - 1 && k3 < k2 + 3
							// && (ai1[k3 + 1][l3] & 0x1280180) == 0)
							// k3++;
							// if (j4 == 2 && l3 > 0 && l3 > l2 - 3
							// && (ai1[k3][l3 - 1] & 0x1280102) == 0)
							// l3--;
							// if (j4 == 3 && l3 < byte1 - 1 && l3 < l2 + 3
							// && (ai1[k3][l3 + 1] & 0x1280120) == 0)
							// l3++;
							// }

						}
						mapIconSprite[mapIconAmount] = mapFunctions[j3];
						anIntArray1072[mapIconAmount] = k3;
						anIntArray1073[mapIconAmount] = l3;
						mapIconAmount++;
					}
				}
			}

		}

	}

	public void spawnGroundItem(int i, int j) {
		NodeList class19 = groundArray[plane][i][j];
		if (class19 == null) {
			worldController.method295(plane, i, j);
			return;
		}
		int k = 0xfa0a1f01;
		Object obj = null;
		for (Item item = (Item) class19.reverseGetFirst(); item != null; item = (Item) class19.reverseGetNext()) {
			ItemDefinition itemDef = ItemDefinition.forID(item.ID);

			int l = itemDef.value;
			if (itemDef.stackable)
				l *= item.anInt1559 + 1;
			if (l > k) {
				k = l;
				obj = item;
			}
			obj = item;
		}

		class19.insertTail(((Node) (obj)));
		Object obj1 = null;
		Object obj2 = null;
		for (Item class30_sub2_sub4_sub2_1 = (Item) class19
				.reverseGetFirst(); class30_sub2_sub4_sub2_1 != null; class30_sub2_sub4_sub2_1 = (Item) class19
						.reverseGetNext()) {
			if (class30_sub2_sub4_sub2_1.ID != ((Item) (obj)).ID && obj1 == null)
				obj1 = class30_sub2_sub4_sub2_1;
			if (class30_sub2_sub4_sub2_1.ID != ((Item) (obj)).ID && class30_sub2_sub4_sub2_1.ID != ((Item) (obj1)).ID
					&& obj2 == null)
				obj2 = class30_sub2_sub4_sub2_1;
		}

		int i1 = i + (j << 7) + 0x60000000;
		worldController.method281(i, i1, ((Animable) (obj1)), method42(plane, j * 128 + 64, i * 128 + 64),
				((Animable) (obj2)), ((Animable) (obj)), plane, j);
	}

	public void method26(boolean flag) {
		for (int j = 0; j < npcCount; j++) {
			NPC npc = npcArray[npcIndices[j]];
			int k = 0x20000000 + (npcIndices[j] << 14);
			if (npc == null || !npc.isVisible() || npc.desc.aBoolean93 != flag)
				continue;
			int l = npc.x >> 7;
			int i1 = npc.y >> 7;
			if (l < 0 || l >= 104 || i1 < 0 || i1 >= 104)
				continue;
			if (npc.anInt1540 == 1 && (npc.x & 0x7f) == 64 && (npc.y & 0x7f) == 64) {
				if (anIntArrayArray929[l][i1] == anInt1265)
					continue;
				anIntArrayArray929[l][i1] = anInt1265;
			}
			if (!npc.desc.aBoolean84)
				k += 0x80000000;
			worldController.method285(plane, npc.anInt1552, method42(plane, npc.y, npc.x), k, npc.y,
					(npc.anInt1540 - 1) * 64 + 60, npc.x, npc, npc.aBoolean1541);
		}
	}

	private boolean replayWave() {
		return Signlink.wavereplay();
	}

	public void loadError() {
		String s = "ondemand";// was a constant parameter
		System.out.println(s);
		try {
			getAppletContext().showDocument(new URL(getCodeBase(), "loaderror_" + s + ".html"));
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		do
			try {
				Thread.sleep(1000L);
			} catch (Exception _ex) {
			}
		while (true);
	}

	private void buildInterfaceMenu(int i, RSInterface class9, int k, int l, int i1, int j1) {
		if (class9 == null) {
			return;
		}
		if (class9.type != 0 || class9.children == null || class9.isMouseoverTriggered)
			return;
		if (k < i || i1 < l || k > i + class9.width || i1 > l + class9.height)
			return;
		int k1 = class9.children.length;
		for (int l1 = 0; l1 < k1; l1++) {
			int i2 = class9.childX[l1] + i;
			int j2 = (class9.childY[l1] + l) - j1;
			RSInterface class9_1 = RSInterface.interfaceCache[class9.children[l1]];
			if (class9_1 == null) {
				break;
			}
			i2 += class9_1.anInt263;
			j2 += class9_1.anInt265;
			if ((class9_1.mOverInterToTrigger >= 0 || class9_1.anInt216 != 0) && k >= i2 && i1 >= j2
					&& k < i2 + class9_1.width && i1 < j2 + class9_1.height) {
				if (class9_1.mOverInterToTrigger >= 0)
					anInt886 = class9_1.mOverInterToTrigger;
				else
					anInt886 = class9_1.id;
			}	
//			if (class9_1.type == 22) {
//				RSInterface class9_2;
//				int slot = class9_1.grandExchangeSlot;
//				if (grandExchangeInformation[slot][0] == -1)
//					class9_2 = RSInterface.interfaceCache[GrandExchange.grandExchangeBuyAndSellBoxIds[slot]];
//				else
//					class9_2 = RSInterface.interfaceCache[GrandExchange.grandExchangeItemBoxIds[slot]];
//				buildInterfaceMenu(i2, class9_2, k, j2, i1, j1);
//			}
//			if (class9_1.type == 9 && k >= i2 && i1 >= j2 && k < i2 + class9_1.width && i1 < j2 + class9_1.height) {
//				anInt1315 = class9_1.id;
//			}
			if (class9_1.type == 0) {
				buildInterfaceMenu(i2, class9_1, k, j2, i1, class9_1.scrollPosition);
				if (class9_1.scrollMax > class9_1.height) {
					if (scrollbarVisible(class9_1)) {
						method65(i2 + class9_1.width, class9_1.height, k, i1, class9_1, j2, true, class9_1.scrollMax);
					}
				}
			} else {
				if (class9_1.atActionType == 1 && k >= i2 && i1 >= j2 && k < i2 + class9_1.width
						&& i1 < j2 + class9_1.height) {
					boolean flag = false;
					if (class9_1.contentType != 0)
						flag = buildFriendsListMenu(class9_1);
					if (!flag) {
						menuActionName[menuActionRow] = class9_1.tooltip;
						menuActionID[menuActionRow] = 315;
						menuActionCmd3[menuActionRow] = class9_1.id;
						menuActionRow++;
					}
				}
				if (class9_1.atActionType == 2 && spellSelected == 0 && k >= i2 && i1 >= j2 && k < i2 + class9_1.width
						&& i1 < j2 + class9_1.height) {
					String s = class9_1.selectedActionName;
					if (s.indexOf(" ") != -1)
						s = s.substring(0, s.indexOf(" "));
					menuActionName[menuActionRow] = "Autocast" + "@gre@ " + class9_1.spellName;
					menuActionID[menuActionRow] = 104;// autocast
					menuActionCmd3[menuActionRow] = class9_1.id;
					menuActionRow++;
					menuActionName[menuActionRow] = s + " @gre@" + class9_1.spellName;
					menuActionID[menuActionRow] = 626;
					menuActionCmd3[menuActionRow] = class9_1.id;
					menuActionRow++;
				}
				if (class9_1.atActionType == 3 && k >= i2 && i1 >= j2 && k < i2 + class9_1.width
						&& i1 < j2 + class9_1.height) {
					menuActionName[menuActionRow] = "Close";
					menuActionID[menuActionRow] = 200;
					menuActionCmd3[menuActionRow] = class9_1.id;
					menuActionRow++;
				}
				if (class9_1.atActionType == 4 && k >= i2 && i1 >= j2 && k < i2 + class9_1.width
						&& i1 < j2 + class9_1.height) {
					menuActionName[menuActionRow] = class9_1.tooltip;
					menuActionID[menuActionRow] = 169;
					menuActionCmd3[menuActionRow] = class9_1.id;
					menuActionRow++;
				}
				if (class9_1.atActionType == 5 && k >= i2 && i1 >= j2 && k < i2 + class9_1.width
						&& i1 < j2 + class9_1.height) {
					menuActionName[menuActionRow] = class9_1.tooltip;
					menuActionID[menuActionRow] = 646;
					menuActionCmd3[menuActionRow] = class9_1.id;
					menuActionRow++;
				}
				if (class9_1.atActionType == 6 && !aBoolean1149 && k >= i2 && i1 >= j2 && k < i2 + class9_1.width
						&& i1 < j2 + class9_1.height) {
					menuActionName[menuActionRow] = class9_1.tooltip;
					menuActionID[menuActionRow] = 679;
					menuActionCmd3[menuActionRow] = class9_1.id;
					menuActionRow++;
				}
				if (class9_1.type == 8 && k >= i2 && i1 >= j2 && k < i2 + class9_1.width && i1 < j2 + class9_1.height) {
					anInt1315 = class9_1.id;
				}
				if (class9_1.atActionType == 8 && !aBoolean1149 && k >= i2 && i1 >= j2 && k < i2 + class9_1.width
						&& i1 < j2 + class9_1.height) {
					for (int s1 = 0; s1 < class9_1.tooltips.length; s1++) {
						if (!RSInterface.interfaceCache[32007].isMouseoverTriggered) {
							if (class9_1.id > 32016) {
								continue;
							}
						}
						menuActionName[menuActionRow] = class9_1.tooltips[s1];
						menuActionID[menuActionRow] = 1700 + s1;
						menuActionCmd3[menuActionRow] = class9_1.id;
						menuActionRow++;
					}
				}
				if (class9_1.atActionType == 9 && !aBoolean1149 && k >= i2 && i1 >= j2 && k < i2 + class9_1.width
						&& i1 < j2 + class9_1.height) {
					menuActionName[menuActionRow] = class9_1.tooltip;
					menuActionID[menuActionRow] = 1100;
					menuActionCmd3[menuActionRow] = class9_1.id;
					menuActionRow++;
				}
				if (class9_1.atActionType == 10 && !aBoolean1149 && k >= i2 && i1 >= j2 && k < i2 + class9_1.width
						&& i1 < j2 + class9_1.height) {
					menuActionName[menuActionRow] = class9_1.getMenuItem().getText();
					menuActionID[menuActionRow] = 1200;
					menuActionCmd3[menuActionRow] = class9_1.id;
					menuActionRow++;
				}
				if (class9_1.atActionType == 11 && k >= i2 && i1 >= j2 && k < i2 + class9_1.width
						&& i1 < j2 + class9_1.height) {
					menuActionName[menuActionRow] = class9_1.tooltip;
					menuActionID[menuActionRow] = 201;
					menuActionCmd3[menuActionRow] = class9_1.id;
					menuActionRow++;
				}
				if (k >= i2 && i1 >= j2 && k < i2 + (class9_1.type == 4 ? 100 : class9_1.width)
						&& i1 < j2 + class9_1.height) {
					
					
					if (class9_1.actions != null) {
						if ((class9_1.type == 4 && class9_1.message.length() > 0) || class9_1.type == 5) {
							for (int action = class9_1.actions.length - 1; action >= 0; action--) {
								if (class9_1.actions[action] != null) {
									menuActionName[menuActionRow] = class9_1.actions[action] + (class9_1.type == 4 ? " " + class9_1.message : "");
									menuActionID[menuActionRow] = 647;
									menuActionCmd2[menuActionRow] = action;
									menuActionCmd3[menuActionRow] = class9_1.id;
									menuActionRow++;
								}
							}
						}
					}
					
					
				}
				if (class9_1.type == 2) {
					int k2 = 0;
					for (int l2 = 0; l2 < class9_1.height; l2++) {
						for (int i3 = 0; i3 < class9_1.width; i3++) {
							boolean smallSprite = openInterfaceID == 26000
									&& GrandExchange.isSmallItemSprite(class9_1.id);
							int size = smallSprite ? 18 : 32;
							int j3 = i2 + i3 * (size + class9_1.invSpritePadX);
							int k3 = j2 + l2 * (size + class9_1.invSpritePadY);
							if (k2 < 20) {
								j3 += class9_1.spritesX[k2];
								k3 += class9_1.spritesY[k2];
							}
							if (k >= j3 && i1 >= k3 && k < j3 + size && i1 < k3 + size) {
								mouseInvInterfaceIndex = k2;
								lastActiveInvInterface = class9_1.id;
								if (class9_1.inv[k2] > 0) {
									ItemDefinition itemDef = ItemDefinition.forID(class9_1.inv[k2] - 1);
									boolean hasDestroyOption = false;
									if (itemSelected == 1 && class9_1.isInventoryInterface) {
										if (class9_1.id != anInt1284 || k2 != anInt1283) {
											menuActionName[menuActionRow] = "Use " + selectedItemName + " with @lre@"
													+ itemDef.name;
											menuActionID[menuActionRow] = 870;
											menuActionCmd1[menuActionRow] = itemDef.id;
											menuActionCmd2[menuActionRow] = k2;
											menuActionCmd3[menuActionRow] = class9_1.id;
											menuActionRow++;
										}
									} else if (spellSelected == 1 && class9_1.isInventoryInterface) {
										if ((spellUsableOn & 0x10) == 16) {
											menuActionName[menuActionRow] = spellTooltip + " @lre@" + itemDef.name;
											menuActionID[menuActionRow] = 543;
											menuActionCmd1[menuActionRow] = itemDef.id;
											menuActionCmd2[menuActionRow] = k2;
											menuActionCmd3[menuActionRow] = class9_1.id;
											menuActionRow++;
										}
									} else {
										if (class9_1.isInventoryInterface) {
											for (int l3 = 4; l3 >= 3; l3--)
												if (itemDef.inventoryOptions != null && itemDef.inventoryOptions[l3] != null) {
													menuActionName[menuActionRow] = itemDef.inventoryOptions[l3] + " @lre@"
															+ itemDef.name;
													if (l3 == 3)
														menuActionID[menuActionRow] = 493;
													if (l3 == 4)
														menuActionID[menuActionRow] = 847;
													   	hasDestroyOption = itemDef.inventoryOptions[l3].contains("Destroy");
														menuActionCmd1[menuActionRow] = itemDef.id;
														menuActionCmd2[menuActionRow] = k2;
														menuActionCmd3[menuActionRow] = class9_1.id;
														menuActionRow++;
												} else if (l3 == 4) {
													menuActionName[menuActionRow] = "Drop @lre@" + itemDef.name;
													menuActionID[menuActionRow] = 847;
													menuActionCmd1[menuActionRow] = itemDef.id;
													menuActionCmd2[menuActionRow] = k2;
													menuActionCmd3[menuActionRow] = class9_1.id;
													menuActionRow++;
												}

										}
										if (class9_1.usableItemInterface) {
											menuActionName[menuActionRow] = "Use @lre@" + itemDef.name;
											menuActionID[menuActionRow] = 447;
											menuActionCmd1[menuActionRow] = itemDef.id;
											// k2 = inventory spot
											// System.out.println(k2);
											menuActionCmd2[menuActionRow] = k2;
											menuActionCmd3[menuActionRow] = class9_1.id;
											menuActionRow++;
											if (!hasDestroyOption && !menuOpen && shiftDrop && shiftDown) {
												menuActionsRow("Drop @lre@"+ itemDef.name, 1, 847, 2);
												removeShiftDropOnMenuOpen = true;
											}
										}
										if (class9_1.isInventoryInterface && itemDef.inventoryOptions != null) {
											for (int i4 = 2; i4 >= 0; i4--)
												if (itemDef.inventoryOptions[i4] != null) {
													
													menuActionName[menuActionRow] = itemDef.inventoryOptions[i4] + " @lre@"
															+ itemDef.name;
													if (i4 == 0)
														menuActionID[menuActionRow] = 74;
													if (i4 == 1)
														menuActionID[menuActionRow] = 454;
													if (i4 == 2)
														menuActionID[menuActionRow] = 539;
													menuActionCmd1[menuActionRow] = itemDef.id;
													menuActionCmd2[menuActionRow] = k2;
													menuActionCmd3[menuActionRow] = class9_1.id;
													menuActionRow++;
													if (!hasDestroyOption && !menuOpen && shiftDrop && shiftDown) {
														menuActionsRow("Drop @lre@"+ itemDef.name, 1, 847, 2);
														removeShiftDropOnMenuOpen = true;
													}
												}

										}
										if (class9_1.actions != null) {
											for (int j4 = 8; j4 >= 0; j4--) {
												if (j4 > class9_1.actions.length - 1)
													continue;
												if (class9_1.actions[j4] != null) {
													if (class9_1.parentID == 5292) {
														if (class9_1.id == 5382 && placeHolders && class9_1.invStackSizes[k2] <= 0) {
															//menuActionName[menuActionRow] = "Release " + " @lre@" +itemDef.name;
															class9_1.actions = new String[8];
															class9_1.actions[1] = "Release";
														} else {
															if (modifiableXValue > 0) {//so issue is when x = 0
																if (class9_1.actions.length < 9) {
																	class9_1.actions = new String[] { "Withdraw 1",
																			"Withdraw 5", "Withdraw 10", "Withdraw All",
																			"Withdraw X", "Withdraw " + modifiableXValue,
																			"Withdraw All but one", "Placeholder" };
																}
																class9_1.actions[5] = "Withdraw " + modifiableXValue;
															}
														} 
													}
													menuActionName[menuActionRow] = class9_1.actions[j4] + " @lre@"
															+ itemDef.name;
													if (class9_1.id != 1688) {
														if (j4 == 0)
															menuActionID[menuActionRow] = 632;
														if (j4 == 1)
															menuActionID[menuActionRow] = 78;
														if (j4 == 2)
															menuActionID[menuActionRow] = 867;
														if (j4 == 3)
															menuActionID[menuActionRow] = 431;
														if (j4 == 4)
															menuActionID[menuActionRow] = 53;//can u pull up commands?
														if (j4 == 7)
															menuActionID[menuActionRow] = 1337;//placeholders
													} else {
															if (itemDef.equipActions[j4] == null) {
																menuActionName[3] = "Operate @lre@" + itemDef.name;
																menuActionName[4] = "Remove @lre@" + itemDef.name;
															} else {
																class9_1.actions = itemDef.equipActions;
																menuActionName[menuActionRow] = itemDef.equipActions[j4] + " @lre@" + itemDef.name;
															}
															if (j4 == 0)
															    menuActionID[menuActionRow] = 632; // remove
															if (j4 == 1)
															    menuActionID[menuActionRow] = 661; // operate 1
														        if (j4 == 2)
															    menuActionID[menuActionRow] = 662; // operate 2
															if (j4 == 3)
															    menuActionID[menuActionRow] = 663; //operate 3
														        if (j4 == 4)
															    menuActionID[menuActionRow] = 664; //operate 4
													}
													if (class9_1.parentID == 5292) {
														if (class9_1.actions.length < 8) {
															if (j4 == 5)
																menuActionID[menuActionRow] = 291;
														} else {
															if (j4 == 5)
																menuActionID[menuActionRow] = 300;
															if (j4 == 6)
																menuActionID[menuActionRow] = 291;
														}
													}
													menuActionCmd1[menuActionRow] = itemDef.id;
													menuActionCmd2[menuActionRow] = k2;
													menuActionCmd3[menuActionRow] = class9_1.id;
													menuActionRow++;
												}
											}
										}
									}
										if (class9_1.parentID >= 58040 && class9_1.parentID <= 58048
												|| class9_1.parentID >= 32100 && class9_1.parentID <= 32156
												|| class9_1.parentID >= 32200 && class9_1.parentID <= 32222) {
											return;
										}
										if (class9_1.isItemSearchComponent) {
											menuActionName[menuActionRow] = "Select @lre@" + itemDef.name;
											menuActionID[menuActionRow] = 1130;
											menuActionCmd1[menuActionRow] = itemDef.id;
											menuActionCmd2[menuActionRow] = k2;
											menuActionCmd3[menuActionRow] = class9_1.id;
											menuActionRow++;
										} else {
											if (!RSInterface.interfaceCache[32007].isMouseoverTriggered) {
												if (class9_1.id > 32016) {
													continue;
												}
											}
											menuActionName[menuActionRow] = "Examine @lre@" + itemDef.name;

											if (debugModels == true) {
												if (System.currentTimeMillis() - debugDelay > 1000) {
													debugDelay = System.currentTimeMillis();
													pushMessage("<col=255>" + itemDef.name + ":</col> Male: <col=255>"
															+ itemDef.maleModel + "</col> Female: <col=255>"
															+ itemDef.femaleModel + "</col> Model id: <col=255>" + itemDef.modelId, 0, "");
													pushMessage("Zoom: <col=255>" + itemDef.modelZoom
															+ "</col> Rotation: <col=255>x" + itemDef.modelRotation1 + " y"
															+ itemDef.modelRotation2 + "", 0, "");
													pushMessage("Offset: <col=255>x" + itemDef.modelOffset1 + " y"
															+ itemDef.modelOffset2, 0, "");
													menuActionName[menuActionRow] = "#2 (@whi@"
															+ Arrays.toString(itemDef.originalModelColors) + ")@gre@)(@whi@"
															+ Arrays.toString(itemDef.modifiedModelColors) + ")@gre@)";
												}
											}
											menuActionID[menuActionRow] = 1125;
											menuActionCmd1[menuActionRow] = itemDef.id;
											menuActionCmd2[menuActionRow] = k2;
											menuActionCmd3[menuActionRow] = class9_1.id;
											menuActionRow++;
										}
								}
							}
							k2++;
						}

					}

				}
			}
		}
	}

	private void menuActionsRow(String action, int index, int actionID, int row) {
		   if (menuOpen)
			return;
		   menuActionName[index] = action;
		   menuActionID[index] = actionID;
		   menuActionRow = row;
	}

	public void drawScrollbar(int height, int scrollPosition, int yPosition, int xPosition, int scrollMax) {
		scrollBar1.drawSprite(xPosition, yPosition);
		scrollBar2.drawSprite(xPosition, (yPosition + height) - 16);
		DrawingArea.drawPixels(height - 32, yPosition + 16, xPosition, 0x000001, 16);
		DrawingArea.drawPixels(height - 32, yPosition + 16, xPosition, 0x3d3426, 15);
		DrawingArea.drawPixels(height - 32, yPosition + 16, xPosition, 0x342d21, 13);
		DrawingArea.drawPixels(height - 32, yPosition + 16, xPosition, 0x2e281d, 11);
		DrawingArea.drawPixels(height - 32, yPosition + 16, xPosition, 0x29241b, 10);
		DrawingArea.drawPixels(height - 32, yPosition + 16, xPosition, 0x252019, 9);
		DrawingArea.drawPixels(height - 32, yPosition + 16, xPosition, 0x000001, 1);
		int k1 = ((height - 32) * height) / scrollMax;
		if (k1 < 8)
			k1 = 8;
		int l1 = ((height - 32 - k1) * scrollPosition) / (scrollMax - height);
		DrawingArea.drawPixels(k1, yPosition + 16 + l1, xPosition, barFillColor, 16);
		DrawingArea.method341(yPosition + 16 + l1, 0x000001, k1, xPosition);
		DrawingArea.method341(yPosition + 16 + l1, 0x817051, k1, xPosition + 1);
		DrawingArea.method341(yPosition + 16 + l1, 0x73654a, k1, xPosition + 2);
		DrawingArea.method341(yPosition + 16 + l1, 0x6a5c43, k1, xPosition + 3);
		DrawingArea.method341(yPosition + 16 + l1, 0x6a5c43, k1, xPosition + 4);
		DrawingArea.method341(yPosition + 16 + l1, 0x655841, k1, xPosition + 5);
		DrawingArea.method341(yPosition + 16 + l1, 0x655841, k1, xPosition + 6);
		DrawingArea.method341(yPosition + 16 + l1, 0x61553e, k1, xPosition + 7);
		DrawingArea.method341(yPosition + 16 + l1, 0x61553e, k1, xPosition + 8);
		DrawingArea.method341(yPosition + 16 + l1, 0x5d513c, k1, xPosition + 9);
		DrawingArea.method341(yPosition + 16 + l1, 0x5d513c, k1, xPosition + 10);
		DrawingArea.method341(yPosition + 16 + l1, 0x594e3a, k1, xPosition + 11);
		DrawingArea.method341(yPosition + 16 + l1, 0x594e3a, k1, xPosition + 12);
		DrawingArea.method341(yPosition + 16 + l1, 0x514635, k1, xPosition + 13);
		DrawingArea.method341(yPosition + 16 + l1, 0x4b4131, k1, xPosition + 14);
		DrawingArea.method339(yPosition + 16 + l1, 0x000001, 15, xPosition);
		DrawingArea.method339(yPosition + 17 + l1, 0x000001, 15, xPosition);
		DrawingArea.method339(yPosition + 17 + l1, 0x655841, 14, xPosition);
		DrawingArea.method339(yPosition + 17 + l1, 0x6a5c43, 13, xPosition);
		DrawingArea.method339(yPosition + 17 + l1, 0x6d5f48, 11, xPosition);
		DrawingArea.method339(yPosition + 17 + l1, 0x73654a, 10, xPosition);
		DrawingArea.method339(yPosition + 17 + l1, 0x76684b, 7, xPosition);
		DrawingArea.method339(yPosition + 17 + l1, 0x7b6a4d, 5, xPosition);
		DrawingArea.method339(yPosition + 17 + l1, 0x7e6e50, 4, xPosition);
		DrawingArea.method339(yPosition + 17 + l1, 0x817051, 3, xPosition);
		DrawingArea.method339(yPosition + 17 + l1, 0x000001, 2, xPosition);
		DrawingArea.method339(yPosition + 18 + l1, 0x000001, 16, xPosition);
		DrawingArea.method339(yPosition + 18 + l1, 0x564b38, 15, xPosition);
		DrawingArea.method339(yPosition + 18 + l1, 0x5d513c, 14, xPosition);
		DrawingArea.method339(yPosition + 18 + l1, 0x625640, 11, xPosition);
		DrawingArea.method339(yPosition + 18 + l1, 0x655841, 10, xPosition);
		DrawingArea.method339(yPosition + 18 + l1, 0x6a5c43, 7, xPosition);
		DrawingArea.method339(yPosition + 18 + l1, 0x6e6046, 5, xPosition);
		DrawingArea.method339(yPosition + 18 + l1, 0x716247, 4, xPosition);
		DrawingArea.method339(yPosition + 18 + l1, 0x7b6a4d, 3, xPosition);
		DrawingArea.method339(yPosition + 18 + l1, 0x817051, 2, xPosition);
		DrawingArea.method339(yPosition + 18 + l1, 0x000001, 1, xPosition);
		DrawingArea.method339(yPosition + 19 + l1, 0x000001, 16, xPosition);
		DrawingArea.method339(yPosition + 19 + l1, 0x514635, 15, xPosition);
		DrawingArea.method339(yPosition + 19 + l1, 0x564b38, 14, xPosition);
		DrawingArea.method339(yPosition + 19 + l1, 0x5d513c, 11, xPosition);
		DrawingArea.method339(yPosition + 19 + l1, 0x61553e, 9, xPosition);
		DrawingArea.method339(yPosition + 19 + l1, 0x655841, 7, xPosition);
		DrawingArea.method339(yPosition + 19 + l1, 0x6a5c43, 5, xPosition);
		DrawingArea.method339(yPosition + 19 + l1, 0x6e6046, 4, xPosition);
		DrawingArea.method339(yPosition + 19 + l1, 0x73654a, 3, xPosition);
		DrawingArea.method339(yPosition + 19 + l1, 0x817051, 2, xPosition);
		DrawingArea.method339(yPosition + 19 + l1, 0x000001, 1, xPosition);
		DrawingArea.method339(yPosition + 20 + l1, 0x000001, 16, xPosition);
		DrawingArea.method339(yPosition + 20 + l1, 0x4b4131, 15, xPosition);
		DrawingArea.method339(yPosition + 20 + l1, 0x544936, 14, xPosition);
		DrawingArea.method339(yPosition + 20 + l1, 0x594e3a, 13, xPosition);
		DrawingArea.method339(yPosition + 20 + l1, 0x5d513c, 10, xPosition);
		DrawingArea.method339(yPosition + 20 + l1, 0x61553e, 8, xPosition);
		DrawingArea.method339(yPosition + 20 + l1, 0x655841, 6, xPosition);
		DrawingArea.method339(yPosition + 20 + l1, 0x6a5c43, 4, xPosition);
		DrawingArea.method339(yPosition + 20 + l1, 0x73654a, 3, xPosition);
		DrawingArea.method339(yPosition + 20 + l1, 0x817051, 2, xPosition);
		DrawingArea.method339(yPosition + 20 + l1, 0x000001, 1, xPosition);
		DrawingArea.method341(yPosition + 16 + l1, 0x000001, k1, xPosition + 15);
		DrawingArea.method339(yPosition + 15 + l1 + k1, 0x000001, 16, xPosition);
		DrawingArea.method339(yPosition + 14 + l1 + k1, 0x000001, 15, xPosition);
		DrawingArea.method339(yPosition + 14 + l1 + k1, 0x3f372a, 14, xPosition);
		DrawingArea.method339(yPosition + 14 + l1 + k1, 0x443c2d, 10, xPosition);
		DrawingArea.method339(yPosition + 14 + l1 + k1, 0x483e2f, 9, xPosition);
		DrawingArea.method339(yPosition + 14 + l1 + k1, 0x4a402f, 7, xPosition);
		DrawingArea.method339(yPosition + 14 + l1 + k1, 0x4b4131, 4, xPosition);
		DrawingArea.method339(yPosition + 14 + l1 + k1, 0x564b38, 3, xPosition);
		DrawingArea.method339(yPosition + 14 + l1 + k1, 0x000001, 2, xPosition);
		DrawingArea.method339(yPosition + 13 + l1 + k1, 0x000001, 16, xPosition);
		DrawingArea.method339(yPosition + 13 + l1 + k1, 0x443c2d, 15, xPosition);
		DrawingArea.method339(yPosition + 13 + l1 + k1, 0x4b4131, 11, xPosition);
		DrawingArea.method339(yPosition + 13 + l1 + k1, 0x514635, 9, xPosition);
		DrawingArea.method339(yPosition + 13 + l1 + k1, 0x544936, 7, xPosition);
		DrawingArea.method339(yPosition + 13 + l1 + k1, 0x564b38, 6, xPosition);
		DrawingArea.method339(yPosition + 13 + l1 + k1, 0x594e3a, 4, xPosition);
		DrawingArea.method339(yPosition + 13 + l1 + k1, 0x625640, 3, xPosition);
		DrawingArea.method339(yPosition + 13 + l1 + k1, 0x6a5c43, 2, xPosition);
		DrawingArea.method339(yPosition + 13 + l1 + k1, 0x000001, 1, xPosition);
		DrawingArea.method339(yPosition + 12 + l1 + k1, 0x000001, 16, xPosition);
		DrawingArea.method339(yPosition + 12 + l1 + k1, 0x443c2d, 15, xPosition);
		DrawingArea.method339(yPosition + 12 + l1 + k1, 0x4b4131, 14, xPosition);
		DrawingArea.method339(yPosition + 12 + l1 + k1, 0x544936, 12, xPosition);
		DrawingArea.method339(yPosition + 12 + l1 + k1, 0x564b38, 11, xPosition);
		DrawingArea.method339(yPosition + 12 + l1 + k1, 0x594e3a, 10, xPosition);
		DrawingArea.method339(yPosition + 12 + l1 + k1, 0x5d513c, 7, xPosition);
		DrawingArea.method339(yPosition + 12 + l1 + k1, 0x61553e, 4, xPosition);
		DrawingArea.method339(yPosition + 12 + l1 + k1, 0x6e6046, 3, xPosition);
		DrawingArea.method339(yPosition + 12 + l1 + k1, 0x7b6a4d, 2, xPosition);
		DrawingArea.method339(yPosition + 12 + l1 + k1, 0x000001, 1, xPosition);
		DrawingArea.method339(yPosition + 11 + l1 + k1, 0x000001, 16, xPosition);
		DrawingArea.method339(yPosition + 11 + l1 + k1, 0x4b4131, 15, xPosition);
		DrawingArea.method339(yPosition + 11 + l1 + k1, 0x514635, 14, xPosition);
		DrawingArea.method339(yPosition + 11 + l1 + k1, 0x564b38, 13, xPosition);
		DrawingArea.method339(yPosition + 11 + l1 + k1, 0x594e3a, 11, xPosition);
		DrawingArea.method339(yPosition + 11 + l1 + k1, 0x5d513c, 9, xPosition);
		DrawingArea.method339(yPosition + 11 + l1 + k1, 0x61553e, 7, xPosition);
		DrawingArea.method339(yPosition + 11 + l1 + k1, 0x655841, 5, xPosition);
		DrawingArea.method339(yPosition + 11 + l1 + k1, 0x6a5c43, 4, xPosition);
		DrawingArea.method339(yPosition + 11 + l1 + k1, 0x73654a, 3, xPosition);
		DrawingArea.method339(yPosition + 11 + l1 + k1, 0x7b6a4d, 2, xPosition);
		DrawingArea.method339(yPosition + 11 + l1 + k1, 0x000001, 1, xPosition);
	}

	public void updateNPCs(Stream stream, int i) {
		anInt839 = 0;
		anInt893 = 0;
		method139(stream);
		method46(i, stream);
		method86(stream);
		for (int k = 0; k < anInt839; k++) {
			int l = anIntArray840[k];
			if (npcArray[l].anInt1537 != loopCycle) {
				npcArray[l].desc = null;
				npcArray[l] = null;
			}
		}

		if (stream.currentOffset != i) {
			Signlink.reporterror(
					myUsername + " size mismatch in getnpcpos - pos:" + stream.currentOffset + " psize:" + i);
			throw new RuntimeException("eek");
		}
		for (int i1 = 0; i1 < npcCount; i1++)
			if (npcArray[npcIndices[i1]] == null) {
				Signlink.reporterror(myUsername + " null entry in npc list - pos:" + i1 + " size:" + npcCount);
				throw new RuntimeException("eek");
			}

	}

	private int channelButtonHoverPosition;
	private int channelButtonClickPosition;

	public void processChatModeClick() {
		if (super.mouseY >= currentGameHeight - 22 && super.mouseY <= currentGameHeight) {
			if (super.mouseX >= 5 && super.mouseX <= 61) {
				channelButtonHoverPosition = 0;
				inputTaken = true;
			} else if (super.mouseX >= 71 && super.mouseX <= 127) {
				channelButtonHoverPosition = 1;
				inputTaken = true;
			} else if (super.mouseX >= 137 && super.mouseX <= 193) {
				channelButtonHoverPosition = 2;
				inputTaken = true;
			} else if (super.mouseX >= 203 && super.mouseX <= 259) {
				channelButtonHoverPosition = 3;
				inputTaken = true;
			} else if (super.mouseX >= 269 && super.mouseX <= 325) {
				channelButtonHoverPosition = 4;
				inputTaken = true;
			} else if (super.mouseX >= 335 && super.mouseX <= 391) {
				channelButtonHoverPosition = 5;
				inputTaken = true;
			} else if (super.mouseX >= 404 && super.mouseX <= 515) {
				channelButtonHoverPosition = 6;
				inputTaken = true;
			}
		} else {
			channelButtonHoverPosition = -1;
			inputTaken = true;
		}
		if (super.clickMode3 == 1) {
			if (super.saveClickY >= currentGameHeight - 22 && super.saveClickY <= currentGameHeight) {
				if (super.saveClickX >= 5 && super.saveClickX <= 61) {
					if (channelButtonClickPosition == 0)
						toggleHideChatArea();
					channelButtonClickPosition = 0;
					chatTypeView = 0;
					inputTaken = true;
				} else if (super.saveClickX >= 71 && super.saveClickX <= 127) {
					if (channelButtonClickPosition == 1)
						toggleHideChatArea();
					channelButtonClickPosition = 1;
					chatTypeView = 5;
					inputTaken = true;
				} else if (super.saveClickX >= 137 && super.saveClickX <= 193) {
					if (channelButtonClickPosition == 2)
						toggleHideChatArea();
					channelButtonClickPosition = 2;
					chatTypeView = 1;
					inputTaken = true;
				} else if (super.saveClickX >= 203 && super.saveClickX <= 259) {
					if (channelButtonClickPosition == 3)
						toggleHideChatArea();
					channelButtonClickPosition = 3;
					chatTypeView = 2;
					inputTaken = true;
				} else if (super.saveClickX >= 269 && super.saveClickX <= 325) {
					if (channelButtonClickPosition == 4)
						toggleHideChatArea();
					channelButtonClickPosition = 4;
					chatTypeView = 11;
					inputTaken = true;
				} else if (super.saveClickX >= 335 && super.saveClickX <= 391) {
					if (channelButtonClickPosition == 5)
						toggleHideChatArea();
					channelButtonClickPosition = 5;
					chatTypeView = 3;
					inputTaken = true;
				}
			}
		}
	}

	private static boolean hideChatArea = false;

	private static void toggleHideChatArea() {
		if (currentScreenMode == ScreenMode.FIXED)
			return;
		hideChatArea = !hideChatArea;
	}

	public void method33(int i) {
		if (i > Varp.cacheSize)
			return;

		if (Varp.cache[i] == null)
			return;

		int j = Varp.cache[i].anInt709;
		if (j == 0)
			return;
		int k = variousSettings[i];
		if (j == 1) {
			if (k == 1)
				Texture.method372(0.90000000000000002D);
			if (k == 2)
				Texture.method372(0.80000000000000004D);
			if (k == 3)
				Texture.method372(0.69999999999999996D);
			if (k == 4)
				Texture.method372(0.59999999999999998D);
			ItemDefinition.mruNodes1.unlinkAll();
			welcomeScreenRaised = true;
		}
		if (j == 3) {
			boolean flag1 = musicEnabled;
			if (k == 0) {
				adjustVolume(musicEnabled, 0);
				musicEnabled = true;
			}
			if (k == 1) {
				adjustVolume(musicEnabled, -400);
				musicEnabled = true;
			}
			if (k == 2) {
				adjustVolume(musicEnabled, -800);
				musicEnabled = true;
			}
			if (k == 3) {
				adjustVolume(musicEnabled, -1200);
				musicEnabled = true;
			}
			if (k == 4)
				musicEnabled = false;
			if (musicEnabled != flag1 && !lowMem) {
				if (musicEnabled) {
					nextSong = currentSong;
					songChanging = true;
					onDemandFetcher.method558(2, nextSong);
				} else {
					stopMidi();
				}
				prevSong = 0;
			}
		}
		if (j == 4) {
			if (k == 0) {
				aBoolean848 = true;
				setWaveVolume(0);
			}
			if (k == 1) {
				aBoolean848 = true;
				setWaveVolume(-400);
			}
			if (k == 2) {
				aBoolean848 = true;
				setWaveVolume(-800);
			}
			if (k == 3) {
				aBoolean848 = true;
				setWaveVolume(-1200);
			}
			if (k == 4)
				aBoolean848 = false;
		}
		if (j == 5)
			anInt1253 = k;
		if (j == 6)
			anInt1249 = k;
		if (j == 9)
			anInt913 = k;
	}

	public void updateEntities() {
		try {
			
			int anInt974 = 0;
			for (int j = -1; j < playerCount + npcCount; j++) {
				Object obj;
				if (j == -1)
					obj = myPlayer;
				else if (j < playerCount)
					obj = playerArray[playerIndices[j]];
				else
					obj = npcArray[npcIndices[j - playerCount]];
				if (obj == null || !((Entity) (obj)).isVisible())
					continue;
				if (obj instanceof NPC) {
					NpcDefinition entityDef = ((NPC) obj).desc;
					if (entityDef.childrenIDs != null)
						entityDef = entityDef.method161();
					if (entityDef == null)
						continue;
				}
				/**
				 * Players
				 */
				if (j < playerCount) {
					int l = 30;
					Player player = (Player) obj;
					if (player.headIcon >= 0) {
						npcScreenPos(((Entity) (obj)), ((Entity) (obj)).height + 15);
						if (spriteDrawX > -1) {
							if (player.skullIcon < 2) {
								skullIcons[player.skullIcon].drawSprite(spriteDrawX - 12, spriteDrawY - l);
								l += 25;
							}
							if (player.headIcon < 7) {
								headIcons[player.headIcon].drawSprite(spriteDrawX - 12, spriteDrawY - l);
								l += 18;
							}
						}
					}
					if (j >= 0 && anInt855 == 10 && anInt933 == playerIndices[j]) {
						npcScreenPos(((Entity) (obj)), ((Entity) (obj)).height + 15);
						
						if (spriteDrawX > -1)
							headIconsHint[player.hintIcon].drawSprite(spriteDrawX - 12, spriteDrawY - l);
					}
				} else {
					NpcDefinition entityDef_1 = ((NPC) obj).desc;
					if (entityDef_1.anInt75 >= 0 && entityDef_1.anInt75 < headIcons.length) {
						npcScreenPos(((Entity) (obj)), ((Entity) (obj)).height + 15);
						if (spriteDrawX > -1)
							headIcons[entityDef_1.anInt75].drawSprite(spriteDrawX - 12, spriteDrawY - 30);
					}
					if (anInt855 == 1 && anInt1222 == npcIndices[j - playerCount] && loopCycle % 20 < 10) {
						npcScreenPos(((Entity) (obj)), ((Entity) (obj)).height + 15);
						if (spriteDrawX > -1)
							headIconsHint[0].drawSprite(spriteDrawX - 12, spriteDrawY - 28);
					}
				}
				if (((Entity) (obj)).textSpoken != null && (j >= playerCount || publicChatMode == 0
						|| publicChatMode == 3 || publicChatMode == 1 && isFriendOrSelf(((Player) obj).name))) {
					npcScreenPos(((Entity) (obj)), ((Entity) (obj)).height);
					if (spriteDrawX > -1 && anInt974 < anInt975) {
						anIntArray979[anInt974] = chatTextDrawingArea.method384(((Entity) (obj)).textSpoken) / 2;
						anIntArray978[anInt974] = chatTextDrawingArea.anInt1497;
						anIntArray976[anInt974] = spriteDrawX;
						anIntArray977[anInt974] = spriteDrawY;
						anIntArray980[anInt974] = ((Entity) (obj)).anInt1513;
						anIntArray981[anInt974] = ((Entity) (obj)).anInt1531;
						anIntArray982[anInt974] = ((Entity) (obj)).textCycle;
						aStringArray983[anInt974++] = ((Entity) (obj)).textSpoken;
						if (anInt1249 == 0 && ((Entity) (obj)).anInt1531 >= 1 && ((Entity) (obj)).anInt1531 <= 3) {
							anIntArray978[anInt974] += 10;
							anIntArray977[anInt974] += 5;
						}
						if (anInt1249 == 0 && ((Entity) (obj)).anInt1531 == 4)
							anIntArray979[anInt974] = 60;
						if (anInt1249 == 0 && ((Entity) (obj)).anInt1531 == 5)
							anIntArray978[anInt974] += 5;
					}
				}
				if (((Entity) (obj)).loopCycleStatus > loopCycle) {
					try {
						npcScreenPos(((Entity) (obj)), ((Entity) (obj)).height + 15);
						if (spriteDrawX > -1) {
							int i1 = (((Entity) (obj)).currentHealth * 30) / ((Entity) (obj)).maxHealth;
							if (i1 > 30)
								i1 = 30;
							DrawingArea.drawPixels(5, spriteDrawY - 3, spriteDrawX - 15, 65280, i1);
							DrawingArea.drawPixels(5, spriteDrawY - 3, (spriteDrawX - 15) + i1, 0xff0000, 30 - i1);
						}
					} catch (Exception e) {
					}
				}
				for (int j1 = 0; j1 < 4; j1++)
					if (((Entity) (obj)).hitsLoopCycle[j1] > loopCycle) {
						npcScreenPos(((Entity) (obj)), ((Entity) (obj)).height / 2);
						
						if (spriteDrawX > -1) {
							if (j1 == 1)
								spriteDrawY -= 20;
							if (j1 == 2) {
								spriteDrawX -= 15;
								spriteDrawY -= 10;
							}
							if (j1 == 3) {
								spriteDrawX += 15;
								spriteDrawY -= 10;
							}
							hitMarks[((Entity) (obj)).hitMarkTypes[j1]].drawSprite(spriteDrawX - 12, spriteDrawY - 12);
							smallText.drawText(0, String.valueOf(((Entity) (obj)).hitArray[j1]), spriteDrawY + 4,
									spriteDrawX);
							smallText.drawText(0xffffff, String.valueOf(((Entity) (obj)).hitArray[j1]), spriteDrawY + 3,
									spriteDrawX - 1);
						}
					}
			}
			for (int k = 0; k < anInt974; k++) {
				int k1 = anIntArray976[k];
				int l1 = anIntArray977[k];
				int j2 = anIntArray979[k];
				int k2 = anIntArray978[k];
				boolean flag = true;
				while (flag) {
					flag = false;
					for (int l2 = 0; l2 < k; l2++)
						if (l1 + 2 > anIntArray977[l2] - anIntArray978[l2] && l1 - k2 < anIntArray977[l2] + 2
								&& k1 - j2 < anIntArray976[l2] + anIntArray979[l2]
								&& k1 + j2 > anIntArray976[l2] - anIntArray979[l2]
								&& anIntArray977[l2] - anIntArray978[l2] < l1) {
							l1 = anIntArray977[l2] - anIntArray978[l2];
							flag = true;
						}

				}
				spriteDrawX = anIntArray976[k];
				spriteDrawY = anIntArray977[k] = l1;
				String s = aStringArray983[k];
				if (anInt1249 == 0) {
					int i3 = 0xffff00;
					if (anIntArray980[k] < 6)
						i3 = anIntArray965[anIntArray980[k]];
					if (anIntArray980[k] == 6)
						i3 = anInt1265 % 20 >= 10 ? 0xffff00 : 0xff0000;
					if (anIntArray980[k] == 7)
						i3 = anInt1265 % 20 >= 10 ? 65535 : 255;
					if (anIntArray980[k] == 8)
						i3 = anInt1265 % 20 >= 10 ? 0x80ff80 : 45056;
					if (anIntArray980[k] == 9) {
						int j3 = 150 - anIntArray982[k];
						if (j3 < 50)
							i3 = 0xff0000 + 1280 * j3;
						else if (j3 < 100)
							i3 = 0xffff00 - 0x50000 * (j3 - 50);
						else if (j3 < 150)
							i3 = 65280 + 5 * (j3 - 100);
					}
					if (anIntArray980[k] == 10) {
						int k3 = 150 - anIntArray982[k];
						if (k3 < 50)
							i3 = 0xff0000 + 5 * k3;
						else if (k3 < 100)
							i3 = 0xff00ff - 0x50000 * (k3 - 50);
						else if (k3 < 150)
							i3 = (255 + 0x50000 * (k3 - 100)) - 5 * (k3 - 100);
					}
					if (anIntArray980[k] == 11) {
						int l3 = 150 - anIntArray982[k];
						if (l3 < 50)
							i3 = 0xffffff - 0x50005 * l3;
						else if (l3 < 100)
							i3 = 65280 + 0x50005 * (l3 - 50);
						else if (l3 < 150)
							i3 = 0xffffff - 0x50000 * (l3 - 100);
					}
					/**
					 * Entity chat
					 */
					if (anIntArray981[k] == 0) {
						chatTextDrawingArea.drawText(0, s, spriteDrawY + 1, spriteDrawX);
						chatTextDrawingArea.drawText(i3, s, spriteDrawY, spriteDrawX);
					}
					if (anIntArray981[k] == 1) {
						chatTextDrawingArea.method386(0, s, spriteDrawX, anInt1265, spriteDrawY + 1);
						chatTextDrawingArea.method386(i3, s, spriteDrawX, anInt1265, spriteDrawY);
					}
					if (anIntArray981[k] == 2) {
						chatTextDrawingArea.method387(spriteDrawX, s, anInt1265, spriteDrawY + 1, 0);
						chatTextDrawingArea.method387(spriteDrawX, s, anInt1265, spriteDrawY, i3);
					}
					if (anIntArray981[k] == 3) {
						chatTextDrawingArea.method388(150 - anIntArray982[k], s, anInt1265, spriteDrawY + 1,
								spriteDrawX, 0);
						chatTextDrawingArea.method388(150 - anIntArray982[k], s, anInt1265, spriteDrawY, spriteDrawX,
								i3);
					}
					if (anIntArray981[k] == 4) {
						int i4 = chatTextDrawingArea.method384(s);
						int k4 = ((150 - anIntArray982[k]) * (i4 + 100)) / 150;
						DrawingArea.setDrawingArea(334, spriteDrawX - 50, spriteDrawX + 50, 0);
						chatTextDrawingArea.method385(0, s, spriteDrawY + 1, (spriteDrawX + 50) - k4);
						chatTextDrawingArea.method385(i3, s, spriteDrawY, (spriteDrawX + 50) - k4);
						DrawingArea.defaultDrawingAreaSize();
					}
					if (anIntArray981[k] == 5) {
						int j4 = 150 - anIntArray982[k];
						int l4 = 0;
						if (j4 < 25)
							l4 = j4 - 25;
						else if (j4 > 125)
							l4 = j4 - 125;
						DrawingArea.setDrawingArea(spriteDrawY + 5, 0, 512,
								spriteDrawY - chatTextDrawingArea.anInt1497 - 1);
						chatTextDrawingArea.drawText(0, s, spriteDrawY + 1 + l4, spriteDrawX);
						chatTextDrawingArea.drawText(i3, s, spriteDrawY + l4, spriteDrawX);
						DrawingArea.defaultDrawingAreaSize();
					}
				} else {
					chatTextDrawingArea.drawText(0, s, spriteDrawY + 1, spriteDrawX);
					chatTextDrawingArea.drawText(0xffff00, s, spriteDrawY, spriteDrawX);
				}
			}
		} catch (Exception e) {
		}
	}

	private void delFriend(long l) {
		try {
			if (l == 0L)
				return;
			for (int i = 0; i < friendsCount; i++) {
				if (friendsListAsLongs[i] != l)
					continue;
				friendsCount--;
				needDrawTabArea = true;
				for (int j = i; j < friendsCount; j++) {
					friendsList[j] = friendsList[j + 1];
					friendsNodeIDs[j] = friendsNodeIDs[j + 1];
					friendsListAsLongs[j] = friendsListAsLongs[j + 1];
				}

				stream.createFrame(215);
				stream.writeQWord(l);
				break;
			}
		} catch (RuntimeException runtimeexception) {
			Signlink.reporterror("18622, " + false + ", " + l + ", " + runtimeexception.toString());
			throw new RuntimeException();
		}
	}

	Sprite tabAreaFixed;
	Sprite[] tabAreaResizable = new Sprite[3];

	private void drawTabArea() {
		if (currentScreenMode == ScreenMode.FIXED && loginScreenGraphicsBuffer == null && tabAreaGraphicsBuffer != null)
			tabAreaGraphicsBuffer.initDrawingArea();

		Texture.anIntArray1472 = anIntArray1181;
		if (currentScreenMode == ScreenMode.FIXED) {
			tabAreaFixed.drawSprite(0, 0);
			if (invOverlayInterfaceID == -1)
				drawTabs();

		} else {
			(stackTabs() ? tabAreaResizable[1] : tabAreaResizable[2]).drawSpriteWithOpacity(
					Client.currentGameWidth - (stackTabs() ? 231 : 462),
					Client.currentGameHeight - (stackTabs() ? 73 : 37), 220);

			tabAreaResizable[0].drawSpriteWithOpacity(Client.currentGameWidth - 204,
					Client.currentGameHeight - 275 - (stackTabs() ? 73 : 37), 220);

			if (invOverlayInterfaceID == -1)
				drawTabs();
		}
		int y = stackTabs() ? 73 : 37;
		if (invOverlayInterfaceID != -1) {
			drawInterface(0, currentScreenMode == ScreenMode.FIXED ? 28 : currentGameWidth - 197,
					RSInterface.interfaceCache[invOverlayInterfaceID],
					currentScreenMode == ScreenMode.FIXED ? 37 : currentGameHeight - 275 - y + 10);
		} else if (Client.tabInterfaceIDs[Client.tabID] != -1) {
			drawInterface(0, currentScreenMode == ScreenMode.FIXED ? 28 : currentGameWidth - 197,
					RSInterface.interfaceCache[Client.tabInterfaceIDs[Client.tabID]],
					currentScreenMode == ScreenMode.FIXED ? 37 : currentGameHeight - 275 - y + 10);
		}
		if (currentScreenMode == ScreenMode.FIXED) {
			if (menuOpen && menuScreenArea == 1)
				drawMenu();
		}

		if (currentScreenMode == ScreenMode.FIXED && loginScreenGraphicsBuffer == null && tabAreaGraphicsBuffer != null)
			tabAreaGraphicsBuffer.drawGraphics(519, 168, super.graphics);

		mainGameGraphicsBuffer.initDrawingArea();
		Texture.anIntArray1472 = anIntArray1182;
	}

	Sprite[] sideIcons = new Sprite[14];
	Sprite[] redStones = new Sprite[5];

	private void drawTabs() {
		if (currentScreenMode == ScreenMode.FIXED) {
			final int[][] sideIconCoordinates = new int[][] { { 14, 8 }, { 46, 7 }, { 80, 7 }, { 110, 5 }, { 143, 2 },
					{ 177, 3 }, { 210, 6 }, { 12, 302 }, { 46, 304 }, { 79, 304 }, { 113, 302 }, { 145, 304 },
					{ 180, 303 }, { 211, 303 } };
			if (Client.tabInterfaceIDs[Client.tabID] != -1) {
				if (Client.tabID == 0)
					redStones[0].drawSprite(3, 0);
				if (Client.tabID == 1)
					redStones[4].drawSprite(41, 0);
				if (Client.tabID == 2)
					redStones[4].drawSprite(74, 0);
				if (Client.tabID == 3)
					redStones[4].drawSprite(107, 0);
				if (Client.tabID == 4)
					redStones[4].drawSprite(140, 0);
				if (Client.tabID == 5)
					redStones[4].drawSprite(173, 0);
				if (Client.tabID == 6)
					redStones[1].drawSprite(206, 0);
				if (Client.tabID == 7)
					redStones[2].drawSprite(3, 298);
				if (Client.tabID == 8)
					redStones[4].drawSprite(41, 298);
				if (Client.tabID == 9)
					redStones[4].drawSprite(74, 298);
				if (Client.tabID == 10)
					redStones[4].drawSprite(107, 298);
				if (Client.tabID == 11)
					redStones[4].drawSprite(140, 298);
				if (Client.tabID == 12)
					redStones[4].drawSprite(173, 298);
				if (Client.tabID == 13)
					redStones[3].drawSprite(206, 298);
			}
			for (int index = 0; index <= 13; index++) {
				if (Client.tabInterfaceIDs[index] != -1 && anInt1054 == index) {
					if (Client.loopCycle % 20 >= 10)
						;
				}
				sideIcons[index].drawSprite(sideIconCoordinates[index][0], sideIconCoordinates[index][1]);
			}
		} else {
			final int[][] sideIconOffsets = new int[][] { { 7, 8 }, { 4, 6 }, { 6, 7 }, { 3, 4 }, { 3, 2 }, { 4, 3 },
					{ 4, 6 }, { 5, 5 }, { 5, 6 }, { 5, 6 }, { 6, 3 }, { 5, 5 }, { 6, 4 }, { 5, 5 } };
			int x = Client.currentGameWidth - (stackTabs() ? 231 : 462);
			int y = Client.currentGameHeight - (stackTabs() ? 73 : 37);
			for (int tabIndex = 0; tabIndex < 14; tabIndex++) {
				if (Client.tabID == tabIndex) {
					redStones[4].drawSprite(x, y);
				}
				if (stackTabs()) {
					if (tabIndex != 6) {
						x += 33;
					} else if (tabIndex == 6) {
						y += 36;
						x = Client.currentGameWidth - 231;
					}
				} else {
					x += 33;
				}
			}
			x = Client.currentGameWidth - (stackTabs() ? 231 : 462);
			y = Client.currentGameHeight - (stackTabs() ? 73 : 37);
			for (int index = 0; index < 14; index++) {
				if (Client.tabInterfaceIDs[index] != -1 && anInt1054 == index) {
					if (Client.loopCycle % 20 >= 10)
						;
				}
				sideIcons[index].drawSprite(x + sideIconOffsets[index][0], y + sideIconOffsets[index][1]);
				if (stackTabs()) {
					if (index != 6) {
						x += 33;
					} else if (index == 6) {
						y += 36;
						x = Client.currentGameWidth - 231;
					}
				} else {
					x += 33;
				}
			}
		}
	}

	public void method37(int j) {
		if (!lowMem) {
			if (Texture.anIntArray1480[17] >= j) {
				Background background = Texture.aBackgroundArray1474s[17];
				int k = background.anInt1452 * background.anInt1453 - 1;
				int j1 = background.anInt1452 * anInt945 * 2;
				byte abyte0[] = background.aByteArray1450;
				byte abyte3[] = aByteArray912;
				for (int i2 = 0; i2 <= k; i2++)
					abyte3[i2] = abyte0[i2 - j1 & k];

				background.aByteArray1450 = abyte3;
				aByteArray912 = abyte0;
				Texture.method370(17);
				anInt854++;
				if (anInt854 > 1235) {
					anInt854 = 0;
					stream.createFrame(226);
					stream.writeWordBigEndian(0);
					int l2 = stream.currentOffset;
					stream.writeWord(58722);
					stream.writeWordBigEndian(240);
					stream.writeWord((int) (Math.random() * 65536D));
					stream.writeWordBigEndian((int) (Math.random() * 256D));
					if ((int) (Math.random() * 2D) == 0)
						stream.writeWord(51825);
					stream.writeWordBigEndian((int) (Math.random() * 256D));
					stream.writeWord((int) (Math.random() * 65536D));
					stream.writeWord(7130);
					stream.writeWord((int) (Math.random() * 65536D));
					stream.writeWord(61657);
					stream.writeBytes(stream.currentOffset - l2);
				}
			}
			if (Texture.anIntArray1480[24] >= j) {
				Background background_1 = Texture.aBackgroundArray1474s[24];
				int l = background_1.anInt1452 * background_1.anInt1453 - 1;
				int k1 = background_1.anInt1452 * anInt945 * 2;
				byte abyte1[] = background_1.aByteArray1450;
				byte abyte4[] = aByteArray912;
				for (int j2 = 0; j2 <= l; j2++)
					abyte4[j2] = abyte1[j2 - k1 & l];

				background_1.aByteArray1450 = abyte4;
				aByteArray912 = abyte1;
				Texture.method370(24);
			}
			if (Texture.anIntArray1480[34] >= j) {
				Background background_2 = Texture.aBackgroundArray1474s[34];
				int i1 = background_2.anInt1452 * background_2.anInt1453 - 1;
				int l1 = background_2.anInt1452 * anInt945 * 2;
				byte abyte2[] = background_2.aByteArray1450;
				byte abyte5[] = aByteArray912;
				for (int k2 = 0; k2 <= i1; k2++)
					abyte5[k2] = abyte2[k2 - l1 & i1];

				background_2.aByteArray1450 = abyte5;
				aByteArray912 = abyte2;
				Texture.method370(34);
			}
			if (Texture.anIntArray1480[40] >= j) {
				Background background_2 = Texture.aBackgroundArray1474s[40];
				int i1 = background_2.anInt1452 * background_2.anInt1453 - 1;
				int l1 = background_2.anInt1452 * anInt945 * 2;
				byte abyte2[] = background_2.aByteArray1450;
				byte abyte5[] = aByteArray912;
				for (int k2 = 0; k2 <= i1; k2++)
					abyte5[k2] = abyte2[k2 - l1 & i1];
				background_2.aByteArray1450 = abyte5;
				aByteArray912 = abyte2;
				Texture.method370(40);
			}
		}
	}

	public void method38() {
		for (int i = -1; i < playerCount; i++) {
			int j;
			if (i == -1)
				j = myPlayerIndex;
			else
				j = playerIndices[i];
			Player player = playerArray[j];
			if (player != null && player.textCycle > 0) {
				player.textCycle--;
				if (player.textCycle == 0)
					player.textSpoken = null;
			}
		}
		for (int k = 0; k < npcCount; k++) {
			int l = npcIndices[k];
			NPC npc = npcArray[l];
			if (npc != null && npc.textCycle > 0) {
				npc.textCycle--;
				if (npc.textCycle == 0)
					npc.textSpoken = null;
			}
		}
	}

	public void calcCameraPos() {
		int i = anInt1098 * 128 + 64;
		int j = anInt1099 * 128 + 64;
		int k = method42(plane, j, i) - anInt1100;
		if (xCameraPos < i) {
			xCameraPos += anInt1101 + ((i - xCameraPos) * anInt1102) / 1000;
			if (xCameraPos > i)
				xCameraPos = i;
		}
		if (xCameraPos > i) {
			xCameraPos -= anInt1101 + ((xCameraPos - i) * anInt1102) / 1000;
			if (xCameraPos < i)
				xCameraPos = i;
		}
		if (zCameraPos < k) {
			zCameraPos += anInt1101 + ((k - zCameraPos) * anInt1102) / 1000;
			if (zCameraPos > k)
				zCameraPos = k;
		}
		if (zCameraPos > k) {
			zCameraPos -= anInt1101 + ((zCameraPos - k) * anInt1102) / 1000;
			if (zCameraPos < k)
				zCameraPos = k;
		}
		if (yCameraPos < j) {
			yCameraPos += anInt1101 + ((j - yCameraPos) * anInt1102) / 1000;
			if (yCameraPos > j)
				yCameraPos = j;
		}
		if (yCameraPos > j) {
			yCameraPos -= anInt1101 + ((yCameraPos - j) * anInt1102) / 1000;
			if (yCameraPos < j)
				yCameraPos = j;
		}
		i = anInt995 * 128 + 64;
		j = anInt996 * 128 + 64;
		k = method42(plane, j, i) - anInt997;
		int l = i - xCameraPos;
		int i1 = k - zCameraPos;
		int j1 = j - yCameraPos;
		int k1 = (int) Math.sqrt(l * l + j1 * j1);
		int l1 = (int) (Math.atan2(i1, k1) * 325.94900000000001D) & 0x7ff;
		int i2 = (int) (Math.atan2(l, j1) * -325.94900000000001D) & 0x7ff;
		if (l1 < 128)
			l1 = 128;
		if (l1 > 383)
			l1 = 383;
		if (yCameraCurve < l1) {
			yCameraCurve += anInt998 + ((l1 - yCameraCurve) * anInt999) / 1000;
			if (yCameraCurve > l1)
				yCameraCurve = l1;
		}
		if (yCameraCurve > l1) {
			yCameraCurve -= anInt998 + ((yCameraCurve - l1) * anInt999) / 1000;
			if (yCameraCurve < l1)
				yCameraCurve = l1;
		}
		int j2 = i2 - xCameraCurve;
		if (j2 > 1024)
			j2 -= 2048;
		if (j2 < -1024)
			j2 += 2048;
		if (j2 > 0) {
			xCameraCurve += anInt998 + (j2 * anInt999) / 1000;
			xCameraCurve &= 0x7ff;
		}
		if (j2 < 0) {
			xCameraCurve -= anInt998 + (-j2 * anInt999) / 1000;
			xCameraCurve &= 0x7ff;
		}
		int k2 = i2 - xCameraCurve;
		if (k2 > 1024)
			k2 -= 2048;
		if (k2 < -1024)
			k2 += 2048;
		if (k2 < 0 && j2 > 0 || k2 > 0 && j2 < 0)
			xCameraCurve = i2;
	}

	public void drawMenu() {
		int i = menuOffsetX;
		int j = menuOffsetY;
		int k = menuWidth;
		int l = menuHeight + 1;
		int i1 = 0x5d5447;
		DrawingArea.drawPixels(l, j, i, i1, k);
		DrawingArea.drawPixels(16, j + 1, i + 1, 0, k - 2);
		DrawingArea.fillPixels(i + 1, k - 2, l - 19, 0, j + 18);
		chatTextDrawingArea.method385(i1, "Choose Option", j + 14, i + 3);
		int j1 = super.mouseX;
		int k1 = super.mouseY;
		if (menuScreenArea == 0) {
			j1 -= 4;
			k1 -= 4;
		}
		if (menuScreenArea == 1) {
			j1 -= 519;
			k1 -= 168;
		}
		if (menuScreenArea == 2) {
			j1 -= 17;
			k1 -= 338;
		}
		if (menuScreenArea == 3) {
			j1 -= 519;
			k1 -= 0;
		}
		for (int l1 = 0; l1 < menuActionRow; l1++) {
			int i2 = j + 31 + (menuActionRow - 1 - l1) * 15;
			int j2 = 0xffffff;
			if (j1 > i && j1 < i + k && k1 > i2 - 13 && k1 < i2 + 3)
				j2 = 0xffff00;
			newBoldFont.drawBasicString(menuActionName[l1], i + 3, i2, j2, 0);
		}
	}

	private void addFriend(long l) {
		try {
			if (l == 0L)
				return;
			if (friendsCount >= 100 && anInt1046 != 1) {
				pushMessage("Your friendlist is full. Max of 100 for free users, and 200 for members", 0, "");
				return;
			}
			if (friendsCount >= 200) {
				pushMessage("Your friendlist is full. Max of 100 for free users, and 200 for members", 0, "");
				return;
			}
			String s = TextClass.fixName(TextClass.nameForLong(l));
			for (int i = 0; i < friendsCount; i++)
				if (friendsListAsLongs[i] == l) {
					pushMessage(s + " is already on your friend list", 0, "");
					return;
				}
			for (int j = 0; j < ignoreCount; j++)
				if (ignoreListAsLongs[j] == l) {
					pushMessage("Please remove " + s + " from your ignore list first", 0, "");
					return;
				}

			if (s.equals(myPlayer.name)) {
				return;
			} else {
				friendsList[friendsCount] = s;
				friendsListAsLongs[friendsCount] = l;
				friendsNodeIDs[friendsCount] = 0;
				friendsCount++;
				needDrawTabArea = true;
				stream.createFrame(188);
				stream.writeQWord(l);
				return;
			}
		} catch (RuntimeException runtimeexception) {
			Signlink.reporterror("15283, " + (byte) 68 + ", " + l + ", " + runtimeexception.toString());
		}
		throw new RuntimeException();
	}

	private int method42(int i, int j, int k) {
		int l = k >> 7;
		int i1 = j >> 7;
		if (l < 0 || i1 < 0 || l > 103 || i1 > 103)
			return 0;
		int j1 = i;
		if (j1 < 3 && (byteGroundArray[1][l][i1] & 2) == 2)
			j1++;
		int k1 = k & 0x7f;
		int l1 = j & 0x7f;
		int i2 = intGroundArray[j1][l][i1] * (128 - k1) + intGroundArray[j1][l + 1][i1] * k1 >> 7;
		int j2 = intGroundArray[j1][l][i1 + 1] * (128 - k1) + intGroundArray[j1][l + 1][i1 + 1] * k1 >> 7;
		return i2 * (128 - l1) + j2 * l1 >> 7;
	}

	public void resetLogout() {
		try {
			if (socketStream != null)
				socketStream.close();
		} catch (Exception _ex) {
		}
		socketStream = null;
		loggedIn = false;
		prayClicked = false;
		loginScreenState = 0;
		fadingScreen.stop();
		fadingScreen = new FadingScreenImage(newBoldFont, "Thank you for playing OS-Veldahar.", (byte) -1, (byte) 5,
				765 / 2 - 125, 350, 250);
		if (entityTarget != null) {
			entityTarget.stop();
		}
		GameTimerHandler.getSingleton().stopAll();

		// myUsername = "";
		// myPassword = "";
		unlinkMRUNodes();
		worldController.initToNull();
		for (int i = 0; i < 4; i++)
			aClass11Array1230[i].method210();
		System.gc();
		stopMidi();
		currentSong = -1;
		nextSong = -1;
		prevSong = 0;
		experienceCounter = 0;
		setGameMode(ScreenMode.FIXED);
	}

	public void method45() {
		aBoolean1031 = true;
		for (int j = 0; j < 7; j++) {
			anIntArray1065[j] = -1;
			for (int k = 0; k < IDK.length; k++) {
				if (IDK.cache[k].aBoolean662 || IDK.cache[k].anInt657 != j + (aBoolean1047 ? 0 : 7))
					continue;
				anIntArray1065[j] = k;
				break;
			}
		}
	}

	private void method46(int i, Stream stream) {
		while (stream.bitPosition + 21 < i * 8) {
			int k = stream.readBits(14);
			if (k == 16383)
				break;
			if (npcArray[k] == null)
				npcArray[k] = new NPC();
			NPC npc = npcArray[k];
			npcIndices[npcCount++] = k;
			npc.anInt1537 = loopCycle;
			int l = stream.readBits(5);
			if (l > 15)
				l -= 32;
			int i1 = stream.readBits(5);
			if (i1 > 15)
				i1 -= 32;
			int j1 = stream.readBits(1);
			npc.desc = NpcDefinition.forID(stream.readBits(14));
			int k1 = stream.readBits(1);
			if (k1 == 1)
				anIntArray894[anInt893++] = k;
			npc.anInt1540 = npc.desc.boundDim;
			npc.anInt1504 = npc.desc.getDegreesToTurn;
			npc.anInt1554 = npc.desc.walkAnim;
			npc.anInt1555 = npc.desc.anInt58;
			npc.anInt1556 = npc.desc.anInt83;
			npc.anInt1557 = npc.desc.anInt55;
			npc.anInt1511 = npc.desc.standAnim;
			npc.setPos(myPlayer.smallX[0] + i1, myPlayer.smallY[0] + l, j1 == 1);
		}
		stream.finishBitAccess();
	}

	@Override
	public void processGameLoop() {
		if (rsAlreadyLoaded || loadingError || genericLoadingError)
			return;
		loopCycle++;

		if (!loggedIn)
			processLoginScreenInput();
		else
			mainGameProcessor();

		processOnDemandQueue();
	}

	public void method47(boolean flag) {
		if (myPlayer.x >> 7 == destX && myPlayer.y >> 7 == destY)
			destX = 0;
		int j = playerCount;
		if (flag)
			j = 1;
		for (int l = 0; l < j; l++) {
			Player player;
			int i1;
			if (flag) {
				player = myPlayer;
				i1 = myPlayerIndex << 14;
			} else {
				player = playerArray[playerIndices[l]];
				i1 = playerIndices[l] << 14;
			}
			if (player == null || !player.isVisible())
				continue;
			player.aBoolean1699 = (lowMem && playerCount > 50 || playerCount > 200) && !flag
					&& player.anInt1517 == player.anInt1511;
			int j1 = player.x >> 7;
			int k1 = player.y >> 7;
			if (j1 < 0 || j1 >= 104 || k1 < 0 || k1 >= 104)
				continue;
			if (player.aModel_1714 != null && loopCycle >= player.anInt1707 && loopCycle < player.anInt1708) {
				player.aBoolean1699 = false;
				player.anInt1709 = method42(plane, player.y, player.x);
				worldController.method286(plane, player.y, player, player.anInt1552, player.anInt1722, player.x,
						player.anInt1709, player.anInt1719, player.anInt1721, i1, player.anInt1720);
				continue;
			}
			if ((player.x & 0x7f) == 64 && (player.y & 0x7f) == 64) {
				if (anIntArrayArray929[j1][k1] == anInt1265)
					continue;
				anIntArrayArray929[j1][k1] = anInt1265;
			}
			player.anInt1709 = method42(plane, player.y, player.x);
			worldController.method285(plane, player.anInt1552, player.anInt1709, i1, player.y, 60, player.x, player,
					player.aBoolean1541);
		}
	}

	private boolean promptUserForInput(RSInterface class9) {
		int j = class9.contentType;
		if (anInt900 == 2) {
			if (j == 201) {
				inputTaken = true;
				inputDialogState = 0;
				messagePromptRaised = true;
				promptInput = "";
				friendsListAction = 1;
				aString1121 = "Enter name of friend to add to list";
			}
			if (j == 202) {
				inputTaken = true;
				inputDialogState = 0;
				messagePromptRaised = true;
				promptInput = "";
				friendsListAction = 2;
				aString1121 = "Enter name of friend to delete from list";
			}
		}
		if (j == 205) {
			anInt1011 = 250;
			return true;
		}
		if (j == 501) {
			inputTaken = true;
			inputDialogState = 0;
			messagePromptRaised = true;
			promptInput = "";
			friendsListAction = 4;
			aString1121 = "Enter name of player to add to list";
		}
		if (j == 502) {
			inputTaken = true;
			inputDialogState = 0;
			messagePromptRaised = true;
			promptInput = "";
			friendsListAction = 5;
			aString1121 = "Enter name of player to delete from list";
		}
		if (j == 550) {
			inputTaken = true;
			inputDialogState = 0;
			messagePromptRaised = true;
			promptInput = "";
			friendsListAction = 6;
			aString1121 = "Enter the name of the chat you wish to join";
		}
		if (j >= 300 && j <= 313) {
			int k = (j - 300) / 2;
			int j1 = j & 1;
			int i2 = anIntArray1065[k];
			if (i2 != -1) {
				do {
					if (j1 == 0 && --i2 < 0)
						i2 = IDK.length - 1;
					if (j1 == 1 && ++i2 >= IDK.length)
						i2 = 0;
				} while (IDK.cache[i2].aBoolean662 || IDK.cache[i2].anInt657 != k + (aBoolean1047 ? 0 : 7));
				anIntArray1065[k] = i2;
				aBoolean1031 = true;
			}
		}
		if (j >= 314 && j <= 323) {
			int l = (j - 314) / 2;
			int k1 = j & 1;
			int j2 = anIntArray990[l];
			if (k1 == 0 && --j2 < 0)
				j2 = anIntArrayArray1003[l].length - 1;
			if (k1 == 1 && ++j2 >= anIntArrayArray1003[l].length)
				j2 = 0;
			anIntArray990[l] = j2;
			aBoolean1031 = true;
		}
		if (j == 324 && !aBoolean1047) {
			aBoolean1047 = true;
			method45();
		}
		if (j == 325 && aBoolean1047) {
			aBoolean1047 = false;
			method45();
		}
		if (j == 326) {
			stream.createFrame(101);
			stream.writeWordBigEndian(aBoolean1047 ? 0 : 1);
			for (int i1 = 0; i1 < 7; i1++)
				stream.writeWordBigEndian(anIntArray1065[i1]);

			for (int l1 = 0; l1 < 5; l1++)
				stream.writeWordBigEndian(anIntArray990[l1]);

			return true;
		}
		if (j == 613)
			canMute = !canMute;
		if (j >= 601 && j <= 612) {
			clearTopInterfaces();
			if (reportAbuseInput.length() > 0) {
				stream.createFrame(218);
				stream.writeQWord(TextClass.longForName(reportAbuseInput));
				stream.writeWordBigEndian(j - 601);
				stream.writeWordBigEndian(canMute ? 1 : 0);
			}
		}
		return false;
	}

	public void method49(Stream stream) {
		for (int j = 0; j < anInt893; j++) {
			int k = anIntArray894[j];
			Player player = playerArray[k];
			int l = stream.readUnsignedByte();
			if ((l & 0x40) != 0)
				l += stream.readUnsignedByte() << 8;
			method107(l, k, stream, player);
		}
	}

	public void method50(int i, int k, int l, int i1, int j1) {
		int k1 = worldController.method300(j1, l, i);
		if (k1 != 0) {
			int l1 = worldController.method304(j1, l, i, k1);
			int k2 = l1 >> 6 & 3;
			int i3 = l1 & 0x1f;
			int k3 = k;
			if (k1 > 0)
				k3 = i1;
			int ai[] = minimapImage.myPixels;
			int k4 = 24624 + l * 4 + (103 - i) * 512 * 4;
			int i5 = k1 >> 14 & 0x7fff;
			ObjectDefinition class46_2 = ObjectDefinition.forID(i5);
			if (class46_2.anInt758 != -1) {
				Background background_2 = mapScenes[class46_2.anInt758];
				if (background_2 != null) {
					int i6 = (class46_2.anInt744 * 4 - background_2.anInt1452) / 2;
					int j6 = (class46_2.anInt761 * 4 - background_2.anInt1453) / 2;
					background_2.drawBackground(48 + l * 4 + i6, 48 + (104 - i - class46_2.anInt761) * 4 + j6);
				}
			} else {
				if (i3 == 0 || i3 == 2)
					if (k2 == 0) {
						ai[k4] = k3;
						ai[k4 + 512] = k3;
						ai[k4 + 1024] = k3;
						ai[k4 + 1536] = k3;
					} else if (k2 == 1) {
						ai[k4] = k3;
						ai[k4 + 1] = k3;
						ai[k4 + 2] = k3;
						ai[k4 + 3] = k3;
					} else if (k2 == 2) {
						ai[k4 + 3] = k3;
						ai[k4 + 3 + 512] = k3;
						ai[k4 + 3 + 1024] = k3;
						ai[k4 + 3 + 1536] = k3;
					} else if (k2 == 3) {
						ai[k4 + 1536] = k3;
						ai[k4 + 1536 + 1] = k3;
						ai[k4 + 1536 + 2] = k3;
						ai[k4 + 1536 + 3] = k3;
					}
				if (i3 == 3)
					if (k2 == 0)
						ai[k4] = k3;
					else if (k2 == 1)
						ai[k4 + 3] = k3;
					else if (k2 == 2)
						ai[k4 + 3 + 1536] = k3;
					else if (k2 == 3)
						ai[k4 + 1536] = k3;
				if (i3 == 2)
					if (k2 == 3) {
						ai[k4] = k3;
						ai[k4 + 512] = k3;
						ai[k4 + 1024] = k3;
						ai[k4 + 1536] = k3;
					} else if (k2 == 0) {
						ai[k4] = k3;
						ai[k4 + 1] = k3;
						ai[k4 + 2] = k3;
						ai[k4 + 3] = k3;
					} else if (k2 == 1) {
						ai[k4 + 3] = k3;
						ai[k4 + 3 + 512] = k3;
						ai[k4 + 3 + 1024] = k3;
						ai[k4 + 3 + 1536] = k3;
					} else if (k2 == 2) {
						ai[k4 + 1536] = k3;
						ai[k4 + 1536 + 1] = k3;
						ai[k4 + 1536 + 2] = k3;
						ai[k4 + 1536 + 3] = k3;
					}
			}
		}
		k1 = worldController.method302(j1, l, i);
		if (k1 != 0) {
			int i2 = worldController.method304(j1, l, i, k1);
			int l2 = i2 >> 6 & 3;
			int j3 = i2 & 0x1f;
			int l3 = k1 >> 14 & 0x7fff;
			ObjectDefinition class46_1 = ObjectDefinition.forID(l3);
			if (class46_1.anInt758 != -1) {
				Background background_1 = mapScenes[class46_1.anInt758];
				if (background_1 != null) {
					int j5 = (class46_1.anInt744 * 4 - background_1.anInt1452) / 2;
					int k5 = (class46_1.anInt761 * 4 - background_1.anInt1453) / 2;
					background_1.drawBackground(48 + l * 4 + j5, 48 + (104 - i - class46_1.anInt761) * 4 + k5);
				}
			} else if (j3 == 9) {
				int l4 = 0xeeeeee;
				if (k1 > 0)
					l4 = 0xee0000;
				int ai1[] = minimapImage.myPixels;
				int l5 = 24624 + l * 4 + (103 - i) * 512 * 4;
				if (l2 == 0 || l2 == 2) {
					ai1[l5 + 1536] = l4;
					ai1[l5 + 1024 + 1] = l4;
					ai1[l5 + 512 + 2] = l4;
					ai1[l5 + 3] = l4;
				} else {
					ai1[l5] = l4;
					ai1[l5 + 512 + 1] = l4;
					ai1[l5 + 1024 + 2] = l4;
					ai1[l5 + 1536 + 3] = l4;
				}
			}
		}
		k1 = worldController.method303(j1, l, i);
		if (k1 != 0) {
			int j2 = k1 >> 14 & 0x7fff;
			ObjectDefinition class46 = ObjectDefinition.forID(j2);
			if (class46.anInt758 != -1) {
				Background background = mapScenes[class46.anInt758];
				if (background != null) {
					int i4 = (class46.anInt744 * 4 - background.anInt1452) / 2;
					int j4 = (class46.anInt761 * 4 - background.anInt1453) / 2;
					background.drawBackground(48 + l * 4 + i4, 48 + (104 - i - class46.anInt761) * 4 + j4);
				}
			}
		}
	}

	private Sprite aSprite_1201;
	private Sprite aSprite_1202;

	private void loadTitleScreen() {
		if (titleBox == null) {
			titleBox = new Background(titleStreamLoader, "titlebox", 0);
		}
		if (boxHover == null) {
			boxHover = new Sprite(titleStreamLoader, "titlebox", 1);
		}
		if (titleButton == null) {
			titleButton = new Background(titleStreamLoader, "titlebutton", 0);
		}
		if (loginHover == null) {
			loginHover = new Sprite(titleStreamLoader, "titlebutton", 1);
		}
		aBackgroundArray1152s = new Background[12];
		int j = 0;
		try {
			j = Integer.parseInt(getParameter("fl_icon"));
		} catch (Exception _ex) {
		}
		if (j == 0) {
			for (int k = 0; k < 12; k++)
				aBackgroundArray1152s[k] = new Background(titleStreamLoader, "runes", k);

		} else {
			for (int l = 0; l < 12; l++)
				aBackgroundArray1152s[l] = new Background(titleStreamLoader, "runes", 12 + (l & 3));

		}
		aSprite_1201 = new Sprite(128, 265);
		aSprite_1202 = new Sprite(128, 265);
		// aSprite_1201 = new Sprite(0, 266);
		// aSprite_1202 = new Sprite(0, 266);
		System.arraycopy(leftSideFlame.canvasRaster, 0, aSprite_1201.myPixels, 0, 33920);

		System.arraycopy(rightSideFlame.canvasRaster, 0, aSprite_1202.myPixels, 0, 33920);

		anIntArray851 = new int[256];
		for (int k1 = 0; k1 < 64; k1++)
			anIntArray851[k1] = k1 * 0x40000;

		for (int l1 = 0; l1 < 64; l1++)
			anIntArray851[l1 + 64] = 0xff0000 + 1024 * l1;

		for (int i2 = 0; i2 < 64; i2++)
			anIntArray851[i2 + 128] = 0xffff00 + 4 * i2;

		for (int j2 = 0; j2 < 64; j2++)
			anIntArray851[j2 + 192] = 0xffffff;

		anIntArray852 = new int[256];
		for (int k2 = 0; k2 < 64; k2++)
			anIntArray852[k2] = k2 * 1024;

		for (int l2 = 0; l2 < 64; l2++)
			anIntArray852[l2 + 64] = 65280 + 4 * l2;

		for (int i3 = 0; i3 < 64; i3++)
			anIntArray852[i3 + 128] = 65535 + 0x40000 * i3;

		for (int j3 = 0; j3 < 64; j3++)
			anIntArray852[j3 + 192] = 0xffffff;

		anIntArray853 = new int[256];
		for (int k3 = 0; k3 < 64; k3++)
			anIntArray853[k3] = k3 * 4;

		for (int l3 = 0; l3 < 64; l3++)
			anIntArray853[l3 + 64] = 255 + 0x40000 * l3;

		for (int i4 = 0; i4 < 64; i4++)
			anIntArray853[i4 + 128] = 0xff00ff + 1024 * i4;

		for (int j4 = 0; j4 < 64; j4++)
			anIntArray853[j4 + 192] = 0xffffff;

		anIntArray850 = new int[256];
		anIntArray1190 = new int[32768];
		anIntArray1191 = new int[32768];
		randomizeBackground(null);
		anIntArray828 = new int[32768];
		anIntArray829 = new int[32768];
		if (!aBoolean831) {
			drawFlames = true;
			aBoolean831 = true;
			startRunnable(this, 2);
		}
	}

	private static void setHighMem() {
		WorldController.lowMem = false;
		Texture.lowMem = false;
		lowMem = false;
		ObjectManager.lowMem = false;
		ObjectDefinition.lowMem = false;
	}

	public static void main(String args[]) {
		try {
			nodeID = 1;
			portOff = 0;
			setHighMem();
			isMembers = true;
			Signlink.storeid = 32;
			Signlink.startpriv(InetAddress.getLocalHost());
			instance = new ClientWindow(args);
		} catch (Exception exception) {
		}
	}

	private static Client instance;

	public static Client getInstance() {
		return instance;
	}

	public void loadingStages() {
		if (lowMem && loadingStage == 2 && ObjectManager.anInt131 != plane) {
			mainGameGraphicsBuffer.initDrawingArea();
			aTextDrawingArea_1271.drawText(0, "Loading - please wait.", 151, 257);
			aTextDrawingArea_1271.drawText(0xffffff, "Loading - please wait.", 150, 256);
			mainGameGraphicsBuffer.drawGraphics(0, 0, super.graphics);
			loadingStage = 1;
			aLong824 = System.currentTimeMillis();
		}
		if (loadingStage == 1) {
			int j = method54();
			if (j != 0 && System.currentTimeMillis() - aLong824 > 0x57e40L) {
				Signlink.reporterror(
						myUsername + " glcfb " + aLong1215 + "," + j + "," + lowMem + "," + decompressors[0] + ","
								+ onDemandFetcher.getNodeCount() + "," + plane + "," + mapRegionsX + "," + mapRegionsY);
				aLong824 = System.currentTimeMillis();
			}
		}
		if (loadingStage == 2 && plane != anInt985) {
			anInt985 = plane;
			method24(plane);
			stream.createFrame(121);
		}
	}

	private int method54() {
		for (int i = 0; i < aByteArrayArray1183.length; i++) {
			if (aByteArrayArray1183[i] == null && anIntArray1235[i] != -1)
				return -1;
			if (aByteArrayArray1247[i] == null && anIntArray1236[i] != -1)
				return -2;
		}
		boolean flag = true;
		for (int j = 0; j < aByteArrayArray1183.length; j++) {
			byte abyte0[] = aByteArrayArray1247[j];
			if (abyte0 != null) {
				int k = (anIntArray1234[j] >> 8) * 64 - baseX;
				int l = (anIntArray1234[j] & 0xff) * 64 - baseY;
				if (aBoolean1159) {
					k = 10;
					l = 10;
				}
				flag &= ObjectManager.method189(k, abyte0, l);
			}
		}
		if (!flag)
			return -3;// couldn't parse all landscapes
		if (aBoolean1080) {
			return -4;
		} else {
			loadingStage = 2;
			ObjectManager.anInt131 = plane;
			method22();
			stream.createFrame(121);
			return 0;
		}
	}

	public void method55() {
		for (Animable_Sub4 class30_sub2_sub4_sub4 = (Animable_Sub4) aClass19_1013
				.reverseGetFirst(); class30_sub2_sub4_sub4 != null; class30_sub2_sub4_sub4 = (Animable_Sub4) aClass19_1013
						.reverseGetNext())
			if (class30_sub2_sub4_sub4.anInt1597 != plane || loopCycle > class30_sub2_sub4_sub4.anInt1572)
				class30_sub2_sub4_sub4.unlink();
			else if (loopCycle >= class30_sub2_sub4_sub4.anInt1571) {
				if (class30_sub2_sub4_sub4.anInt1590 > 0) {
					NPC npc = npcArray[class30_sub2_sub4_sub4.anInt1590 - 1];
					if (npc != null && npc.x >= 0 && npc.x < 13312 && npc.y >= 0 && npc.y < 13312)
						class30_sub2_sub4_sub4.method455(loopCycle, npc.y,
								method42(class30_sub2_sub4_sub4.anInt1597, npc.y, npc.x)
										- class30_sub2_sub4_sub4.anInt1583,
								npc.x);
				}
				if (class30_sub2_sub4_sub4.anInt1590 < 0) {
					int j = -class30_sub2_sub4_sub4.anInt1590 - 1;
					Player player;
					if (j == unknownInt10)
						player = myPlayer;
					else
						player = playerArray[j];
					if (player != null && player.x >= 0 && player.x < 13312 && player.y >= 0 && player.y < 13312)
						class30_sub2_sub4_sub4.method455(loopCycle, player.y,
								method42(class30_sub2_sub4_sub4.anInt1597, player.y, player.x)
										- class30_sub2_sub4_sub4.anInt1583,
								player.x);
				}
				class30_sub2_sub4_sub4.method456(anInt945);
				worldController.method285(plane, class30_sub2_sub4_sub4.anInt1595,
						(int) class30_sub2_sub4_sub4.aDouble1587, -1, (int) class30_sub2_sub4_sub4.aDouble1586, 60,
						(int) class30_sub2_sub4_sub4.aDouble1585, class30_sub2_sub4_sub4, false);
			}

	}

	@Override
	public AppletContext getAppletContext() {
		if (Signlink.mainapp != null)
			return Signlink.mainapp.getAppletContext();
		else
			return super.getAppletContext();
	}

	public static String capitalize(String s) {
		for (int i = 0; i < s.length(); i++) {
			if (i == 0) {
				s = String.format("%s%s", Character.toUpperCase(s.charAt(0)), s.substring(1));
			}
			if (!Character.isLetterOrDigit(s.charAt(i))) {
				if (i + 1 < s.length()) {
					s = String.format("%s%s%s", s.subSequence(0, i + 1), Character.toUpperCase(s.charAt(i + 1)),
							s.substring(i + 2));
				}
			}
		}
		return s;
	}

	@SuppressWarnings("unused")
	private static void writeFile(byte[] fileData, String fileName) throws IOException {
		OutputStream out = new FileOutputStream(fileName);
		out.write(fileData);
		out.close();
	}

	private void processOnDemandQueue() {
		do {
			OnDemandData onDemandData;
			do {
				onDemandData = onDemandFetcher.getNextNode();
				if (onDemandData == null)
					return;
				if (onDemandData.dataType == 0) {
					Model.method460(onDemandData.buffer, onDemandData.ID);
					needDrawTabArea = true;
					if (backDialogID != -1)
						inputTaken = true;
				}
				if (onDemandData.dataType == 1 && onDemandData.buffer != null)
					Class36.load(onDemandData.ID, onDemandData.buffer);
				if (onDemandData.dataType == 2 && onDemandData.ID == nextSong && onDemandData.buffer != null)
					saveMidi(songChanging, onDemandData.buffer);
				if (onDemandData.dataType == 3 && loadingStage == 1) {
					for (int i = 0; i < aByteArrayArray1183.length; i++) {
						if (anIntArray1235[i] == onDemandData.ID) {
							if (aByteArrayArray1183[i] == null)
								aByteArrayArray1183[i] = onDemandData.buffer;
							if (onDemandData.buffer == null)
								anIntArray1235[i] = -1;
							break;
						}
						if (anIntArray1236[i] != onDemandData.ID)
							continue;
						if (aByteArrayArray1247[i] == null)
							aByteArrayArray1247[i] = onDemandData.buffer;
						if (onDemandData.buffer == null)
							anIntArray1236[i] = -1;
						break;
					}

				}
			} while (onDemandData.dataType != 93 || !onDemandFetcher.method564(onDemandData.ID));
			ObjectManager.method173(new Stream(onDemandData.buffer), onDemandFetcher);
		} while (true);
	}

	public void calcFlamesPosition() {
		char c = '\u0100';
		for (int j = 10; j < 117; j++) {
			int k = (int) (Math.random() * 100D);
			if (k < 50)
				anIntArray828[j + (c - 2 << 7)] = 255;
		}
		for (int l = 0; l < 100; l++) {
			int i1 = (int) (Math.random() * 124D) + 2;
			int k1 = (int) (Math.random() * 128D) + 128;
			int k2 = i1 + (k1 << 7);
			anIntArray828[k2] = 192;
		}

		for (int j1 = 1; j1 < c - 1; j1++) {
			for (int l1 = 1; l1 < 127; l1++) {
				int l2 = l1 + (j1 << 7);
				anIntArray829[l2] = (anIntArray828[l2 - 1] + anIntArray828[l2 + 1] + anIntArray828[l2 - 128]
						+ anIntArray828[l2 + 128]) / 4;
			}

		}

		anInt1275 += 128;
		if (anInt1275 > anIntArray1190.length) {
			anInt1275 -= anIntArray1190.length;
			int i2 = (int) (Math.random() * 12D);
			randomizeBackground(aBackgroundArray1152s[i2]);
		}
		for (int j2 = 1; j2 < c - 1; j2++) {
			for (int i3 = 1; i3 < 127; i3++) {
				int k3 = i3 + (j2 << 7);
				int i4 = anIntArray829[k3 + 128] - anIntArray1190[k3 + anInt1275 & anIntArray1190.length - 1] / 5;
				if (i4 < 0)
					i4 = 0;
				anIntArray828[k3] = i4;
			}

		}

		System.arraycopy(anIntArray969, 1, anIntArray969, 0, c - 1);

		anIntArray969[c - 1] = (int) (Math.sin(loopCycle / 14D) * 16D + Math.sin(loopCycle / 15D) * 14D
				+ Math.sin(loopCycle / 16D) * 12D);
		if (anInt1040 > 0)
			anInt1040 -= 4;
		if (anInt1041 > 0)
			anInt1041 -= 4;
		if (anInt1040 == 0 && anInt1041 == 0) {
			int l3 = (int) (Math.random() * 2000D);
			if (l3 == 0)
				anInt1040 = 1024;
			if (l3 == 1)
				anInt1041 = 1024;
		}
	}

	private boolean saveWave(byte abyte0[], int i) {
		return abyte0 == null || Signlink.wavesave(abyte0, i);
	}

	public void method60(int i) {
		RSInterface class9 = RSInterface.interfaceCache[i];
		for (int j = 0; j < class9.children.length; j++) {
			if (class9.children[j] == -1)
				break;
			RSInterface class9_1 = RSInterface.interfaceCache[class9.children[j]];
			if (class9_1.type == 1)
				method60(class9_1.id);
			class9_1.anInt246 = 0;
			class9_1.anInt208 = 0;
		}
	}

	public void drawHeadIcon() {
		if (anInt855 != 2)
			return;
		calcEntityScreenPos((anInt934 - baseX << 7) + anInt937, anInt936 * 2, (anInt935 - baseY << 7) + anInt938);
		if (spriteDrawX > -1 && loopCycle % 20 < 10)
			headIconsHint[0].drawSprite(spriteDrawX - 12, spriteDrawY - 28);
	}

	private GameTimer gameTimer;

	private void mainGameProcessor() {
		if (gameTimer != null) {
			if (gameTimer.isCompleted()) {
				System.out.println("Timer has finished!");
				gameTimer.stop();
			}
		}

		if (currentScreenMode != ScreenMode.FIXED && currentScreenMode != ScreenMode.FULLSCREEN) {
			if (currentGameWidth != getGameComponent().getWidth()) {
				currentGameWidth = getGameComponent().getWidth();
				graphics = super.getGameComponent().getGraphics();
				updateGameScreen();
			}
			if (currentGameHeight != getGameComponent().getHeight()) {
				currentGameHeight = getGameComponent().getHeight();
				graphics = super.getGameComponent().getGraphics();
				updateGameScreen();
			}
		}

		if (anInt1104 > 1)
			anInt1104--;
		if (anInt1011 > 0)
			anInt1011--;
		for (int j = 0; j < 5; j++)
			if (!parsePacket())
				break;

		if (!loggedIn)
			return;
		synchronized (mouseDetection.syncObject) {
			if (flagged) {
				if (super.clickMode3 != 0 || mouseDetection.coordsIndex >= 40) {
					stream.createFrame(45);
					stream.writeWordBigEndian(0);
					int j2 = stream.currentOffset;
					int j3 = 0;
					for (int j4 = 0; j4 < mouseDetection.coordsIndex; j4++) {
						if (j2 - stream.currentOffset >= 240)
							break;
						j3++;
						int l4 = mouseDetection.coordsY[j4];
						if (l4 < 0)
							l4 = 0;
						else if (l4 > 502)
							l4 = 502;
						int k5 = mouseDetection.coordsX[j4];
						if (k5 < 0)
							k5 = 0;
						else if (k5 > 764)
							k5 = 764;
						int i6 = l4 * 765 + k5;
						if (mouseDetection.coordsY[j4] == -1 && mouseDetection.coordsX[j4] == -1) {
							k5 = -1;
							l4 = -1;
							i6 = 0x7ffff;
						}
						if (k5 == anInt1237 && l4 == anInt1238) {
							if (anInt1022 < 2047)
								anInt1022++;
						} else {
							int j6 = k5 - anInt1237;
							anInt1237 = k5;
							int k6 = l4 - anInt1238;
							anInt1238 = l4;
							if (anInt1022 < 8 && j6 >= -32 && j6 <= 31 && k6 >= -32 && k6 <= 31) {
								j6 += 32;
								k6 += 32;
								stream.writeWord((anInt1022 << 12) + (j6 << 6) + k6);
								anInt1022 = 0;
							} else if (anInt1022 < 8) {
								stream.writeDWordBigEndian(0x800000 + (anInt1022 << 19) + i6);
								anInt1022 = 0;
							} else {
								stream.writeDWord(0xc0000000 + (anInt1022 << 19) + i6);
								anInt1022 = 0;
							}
						}
					}

					stream.writeBytes(stream.currentOffset - j2);
					if (j3 >= mouseDetection.coordsIndex) {
						mouseDetection.coordsIndex = 0;
					} else {
						mouseDetection.coordsIndex -= j3;
						for (int i5 = 0; i5 < mouseDetection.coordsIndex; i5++) {
							mouseDetection.coordsX[i5] = mouseDetection.coordsX[i5 + j3];
							mouseDetection.coordsY[i5] = mouseDetection.coordsY[i5 + j3];
						}

					}
				}
			} else {
				mouseDetection.coordsIndex = 0;
			}
		}
		if (super.clickMode3 != 0) {
			long l = (super.aLong29 - aLong1220) / 50L;
			if (l > 4095L)
				l = 4095L;
			aLong1220 = super.aLong29;
			int k2 = super.saveClickY;
			if (k2 < 0)
				k2 = 0;
			else if (k2 > 502)
				k2 = 502;
			int k3 = super.saveClickX;
			if (k3 < 0)
				k3 = 0;
			else if (k3 > 764)
				k3 = 764;
			int k4 = k2 * 765 + k3;
			int j5 = 0;
			if (super.clickMode3 == 2)
				j5 = 1;
			int l5 = (int) l;
			stream.createFrame(241);
			stream.writeDWord((l5 << 20) + (j5 << 19) + k4);
		}
		if (anInt1016 > 0)
			anInt1016--;
		if (super.keyArray[1] == 1 || super.keyArray[2] == 1 || super.keyArray[3] == 1 || super.keyArray[4] == 1)
			aBoolean1017 = true;
		if (aBoolean1017 && anInt1016 <= 0) {
			anInt1016 = 20;
			aBoolean1017 = false;
			stream.createFrame(86);
			stream.writeWord(anInt1184);
			stream.method432(viewRotation);
		}
		if (super.awtFocus && !aBoolean954) {
			aBoolean954 = true;
			stream.createFrame(3);
			stream.writeWordBigEndian(1);
		}
		if (!super.awtFocus && aBoolean954) {
			aBoolean954 = false;
			stream.createFrame(3);
			stream.writeWordBigEndian(0);
		}
		loadingStages();
		method115();
		method90();
		anInt1009++;
		if (anInt1009 > 750)
			dropClient();
		method114();
		method95();
		method38();
		anInt945++;
		if (crossType != 0) {
			crossIndex += 20;
			if (crossIndex >= 400)
				crossType = 0;
		}
		if (atInventoryInterfaceType != 0) {
			atInventoryLoopCycle++;
			if (atInventoryLoopCycle >= 15) {
				if (atInventoryInterfaceType == 2)
					needDrawTabArea = true;
				if (atInventoryInterfaceType == 3)
					inputTaken = true;
				atInventoryInterfaceType = 0;
			}
		}
		if (activeInterfaceType != 0) {
			anInt989++;
			if (super.mouseX > anInt1087 + 5 || super.mouseX < anInt1087 - 5 || super.mouseY > anInt1088 + 5
					|| super.mouseY < anInt1088 - 5)
				aBoolean1242 = true;
			if (super.clickMode2 == 0) {
				if (activeInterfaceType == 2)
					needDrawTabArea = true;
				if (activeInterfaceType == 3)
					inputTaken = true;
				activeInterfaceType = 0;
				if (aBoolean1242 && anInt989 >= 12) {
					lastActiveInvInterface = -1;
					processRightClick();
					if (anInt1084 == 5382) {
						Point southWest, northEast;
						int xOffset = currentScreenMode == ScreenMode.FIXED ? 0
								: (centerInterface() ? (currentGameWidth / 2) - 256 : 0);
						int yOffset = currentScreenMode == ScreenMode.FIXED ? 0
								: (centerInterface() ? (currentGameHeight / 2) - 167 : 0);
						southWest = new Point(68 + xOffset, 75 + yOffset);
						northEast = new Point(457 + xOffset, 41 + yOffset);
						int[] slots = new int[9];
						for (int i = 0; i < slots.length; i++)
							slots[i] = i == 0 ? (int) southWest.getX() : (41 * i) + (int) southWest.getX();
						for (int i = 0; i < slots.length; i++) {
							if (super.mouseX >= slots[i] && super.mouseX <= slots[i] + 42
									&& super.mouseY >= northEast.getY() && super.mouseY <= southWest.getY()) {
								RSInterface rsi = RSInterface.interfaceCache[58050 + i];
								if (rsi.isMouseoverTriggered) {
									continue;
								}
								stream.createFrame(214);
								stream.method433(anInt1084);
								stream.method424(0);
								stream.method433(anInt1085);
								stream.method431(1000 + i);
								return;
							}
						}
					}
					if (lastActiveInvInterface == anInt1084 && mouseInvInterfaceIndex != anInt1085) {
						RSInterface class9 = RSInterface.interfaceCache[anInt1084];
						int j1 = 0;
						if (anInt913 == 1 && class9.contentType == 206)
							j1 = 1;
						if (class9.inv[mouseInvInterfaceIndex] <= 0)
							j1 = 0;
						if (class9.aBoolean235) {
							int l2 = anInt1085;
							int l3 = mouseInvInterfaceIndex;
							class9.inv[l3] = class9.inv[l2];
							class9.invStackSizes[l3] = class9.invStackSizes[l2];
							class9.inv[l2] = -1;
							class9.invStackSizes[l2] = 0;
						} else if (j1 == 1) {
							int i3 = anInt1085;
							for (int i4 = mouseInvInterfaceIndex; i3 != i4;)
								if (i3 > i4) {
									class9.swapInventoryItems(i3, i3 - 1);
									i3--;
								} else if (i3 < i4) {
									class9.swapInventoryItems(i3, i3 + 1);
									i3++;
								}

						} else {
							class9.swapInventoryItems(anInt1085, mouseInvInterfaceIndex);
						}
						stream.createFrame(214);
						stream.method433(anInt1084);
						stream.method424(j1);
						stream.method433(anInt1085);
						stream.method431(mouseInvInterfaceIndex);
					}
				} else if ((anInt1253 == 1 || menuHasAddFriend(menuActionRow - 1)) && menuActionRow > 2)
					determineMenuSize();
				else if (menuActionRow > 0)
					doAction(menuActionRow - 1);
				atInventoryLoopCycle = 10;
				super.clickMode3 = 0;
			}
		}
		if (WorldController.anInt470 != -1) {
			int k = WorldController.anInt470;
			int k1 = WorldController.anInt471;
			boolean flag = false;
			if ((myPlayer.getRights() == 3  && controlIsDown) || (myPlayer.getRights() == 16  && controlIsDown)) {
				teleport(baseX + k, baseY + k1);
			} else {
				flag = doWalkTo(0, 0, 0, 0, myPlayer.smallY[0], 0, 0, k1, myPlayer.smallX[0], true, k);
			}
			WorldController.anInt470 = -1;
			if (flag) {
				crossX = super.saveClickX;
				crossY = super.saveClickY;
				crossType = 1;
				crossIndex = 0;
			}
		}
		if (super.clickMode3 == 1 && aString844 != null) {
			aString844 = null;
			inputTaken = true;
			super.clickMode3 = 0;
		}
		if (!processMenuClick()) {
			processTabClick();
			processMainScreenClick();

			// processTabClick2();
			processChatModeClick();
		}

		if (super.clickMode2 == 1 || super.clickMode3 == 1)
			anInt1213++;
		if (anInt1500 != 0 || anInt1044 != 0 || anInt1129 != 0) {
			if (anInt1501 < 50 && !menuOpen) {
				anInt1501++;
				if (anInt1501 == 50) {
					if (anInt1500 != 0) {
						inputTaken = true;
					}
					if (anInt1044 != 0) {
						needDrawTabArea = true;
					}
				}
			}
		} else if (anInt1501 > 0) {
			anInt1501--;
		}
		if (loadingStage == 2)
			method108();
		if (loadingStage == 2 && aBoolean1160)
			calcCameraPos();
		for (int i1 = 0; i1 < 5; i1++)
			anIntArray1030[i1]++;
			method73();
			super.idleTime++;
		if (super.idleTime > 22500) {
			anInt1011 = 250;
			super.idleTime -= 500;
			stream.createFrame(202);
		}
		anInt1010++;
		if (anInt1010 > 50)
			stream.createFrame(0);
		try {
			if (socketStream != null && stream.currentOffset > 0) {
				socketStream.queueBytes(stream.currentOffset, stream.buffer);
				stream.currentOffset = 0;
				anInt1010 = 0;
			}
		} catch (IOException _ex) {
			dropClient();
		} catch (Exception exception) {
			resetLogout();
		}
	}

	public void method63() {
		Class30_Sub1 class30_sub1 = (Class30_Sub1) aClass19_1179.reverseGetFirst();
		for (; class30_sub1 != null; class30_sub1 = (Class30_Sub1) aClass19_1179.reverseGetNext())
			if (class30_sub1.anInt1294 == -1) {
				class30_sub1.anInt1302 = 0;
				method89(class30_sub1);
			} else {
				class30_sub1.unlink();
			}

	}

	private void resetImageProducers() {
		if (loginScreenGraphicsBuffer != null)
			return;
		super.fullGameScreen = null;
		chatAreaGraphicsBuffer = null;

		tabAreaGraphicsBuffer = null;
		mainGameGraphicsBuffer = null;
		mapAreaGraphicsBuffer = null;
		leftSideFlame = new RSImageProducer(128, 265, getGameComponent());
		DrawingArea.setAllPixelsToZero();
		rightSideFlame = new RSImageProducer(128, 265, getGameComponent());
		DrawingArea.setAllPixelsToZero();
		loginScreenGraphicsBuffer = new RSImageProducer(currentGameWidth, currentGameHeight, getGameComponent());
		DrawingArea.setAllPixelsToZero();
		if (titleStreamLoader != null)
			welcomeScreenRaised = true;
	}

	public int rememberMe = 0;
	public int loginButtonint;
	public int rememberMehover;
	public int textbox;
	public int textbox1;

	void drawLoadingText(int percentage, String s, int downloadSpeed, int secondsRemaining) {
		anInt1079 = percentage;
		aString1049 = s;
		resetImageProducers();

		if (titleStreamLoader == null) {
			super.drawLoadingText(percentage, s, downloadSpeed, secondsRemaining);
			return;
		}

		loginScreenGraphicsBuffer.initDrawingArea();
		Sprite background = new Sprite("loginscreen/2015/background_old");
		background.drawAdvancedSprite(0, 0);
		int x = 765 / 2 - 543 / 2;
		int y = 475 - 20 + 8;
		int width = 540;
		int height = 32;
		double offset = 5.43;
		new Sprite("loginscreen/general/emptybar").drawAdvancedSprite(765 / 2 - width / 2, y + 11 - height / 2);
		new Sprite("loginscreen/general/fullbar").drawAdvancedSprite(765 / 2 - width / 2, y + 11 - height / 2);
		new DrawingArea().drawAlphaGradient(x + ((int) Math.round(percentage * offset) / 2), y,
				width - ((int) Math.round(percentage * offset) / 2), height, 0x000000, 0x000000, 200);
		if (percentage >= 198) {
			newBoldFont.drawCenteredString("Finished loading Tyras", (765 / 2), y + height / 2, 0xffffff, 1);
		} else {
			newBoldFont.drawCenteredString("Loading Tyras - Please wait - " + (percentage / 2) + "%", (765 / 2),
					y + height / 2, 0xffffff, 1);
		}
		loginScreenGraphicsBuffer.drawGraphics(0, 0, super.graphics);
	}

	public void method65(int i, int j, int k, int l, RSInterface class9, int i1, boolean flag, int j1) {
		int anInt992;
		if (aBoolean972)
			anInt992 = 32;
		else
			anInt992 = 0;
		aBoolean972 = false;
		if (k >= i && k < i + 16 && l >= i1 && l < i1 + 16) {
			class9.scrollPosition -= anInt1213 * 4;
			if (flag) {
				needDrawTabArea = true;
			}
		} else if (k >= i && k < i + 16 && l >= (i1 + j) - 16 && l < i1 + j) {
			class9.scrollPosition += anInt1213 * 4;
			if (flag) {
				needDrawTabArea = true;
			}
		} else if (k >= i - anInt992 && k < i + 16 + anInt992 && l >= i1 + 16 && l < (i1 + j) - 16 && anInt1213 > 0) {
			int l1 = ((j - 32) * j) / j1;
			if (l1 < 8)
				l1 = 8;
			int i2 = l - i1 - 16 - l1 / 2;
			int j2 = j - 32 - l1;
			class9.scrollPosition = ((j1 - j) * i2) / j2;
			if (flag)
				needDrawTabArea = true;
			aBoolean972 = true;
		}
	}

	private boolean method66(int i, int j, int k) {
		int i1 = i >> 14 & 0x7fff;
		int j1 = worldController.method304(plane, k, j, i);
		if (j1 == -1)
			return false;
		int k1 = j1 & 0x1f;
		int l1 = j1 >> 6 & 3;
		if (k1 == 10 || k1 == 11 || k1 == 22) {
			ObjectDefinition class46 = ObjectDefinition.forID(i1);
			int i2;
			int j2;
			if (l1 == 0 || l1 == 2) {
				i2 = class46.anInt744;
				j2 = class46.anInt761;
			} else {
				i2 = class46.anInt761;
				j2 = class46.anInt744;
			}
			int k2 = class46.anInt768;
			if (l1 != 0)
				k2 = (k2 << l1 & 0xf) + (k2 >> 4 - l1);
			doWalkTo(2, 0, j2, 0, myPlayer.smallY[0], i2, k2, j, myPlayer.smallX[0], false, k);
		} else {
			doWalkTo(2, l1, 0, k1 + 1, myPlayer.smallY[0], 0, 0, j, myPlayer.smallX[0], false, k);
		}
		crossX = super.saveClickX;
		crossY = super.saveClickY;
		crossType = 2;
		crossIndex = 0;
		return true;
	}

	private StreamLoader streamLoaderForName(int i, String s, String s1, int j, int k) {
		byte abyte0[] = null;
		try {
			if (decompressors[0] != null) {
				abyte0 = decompressors[0].decompress(i);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (abyte0 != null) {
			StreamLoader streamLoader = new StreamLoader(abyte0, s);
			return streamLoader;
		}
		return null;
	}

	public void sendStringAsLong(String string) {
		stream.createFrame(60);
		stream.writeQWord(TextClass.longForName(string));
	}

	public void sendString(int identifier, String text) {
		text = identifier + "," + text;
		stream.createFrame(127);
		stream.writeWordBigEndian(text.length() + 1);
		stream.writeString(text);
	}

	public void dropClient() {
		if (anInt1011 > 0) {
			resetLogout();
			return;
		}
		mainGameGraphicsBuffer.initDrawingArea();
		DrawingArea.fillPixels(2, 229, 39, 0xffffff, 2);
		DrawingArea.drawPixels(37, 3, 3, 0, 227);
		aTextDrawingArea_1271.drawText(0, "Connection Lost", 19, 120);
		aTextDrawingArea_1271.drawText(0xffffff, "Connection Lost", 18, 119);
		aTextDrawingArea_1271.drawText(0, "Please Wait - Reconnecting", 34, 117);
		aTextDrawingArea_1271.drawText(0xffffff, "Please Wait - Reconnecting", 34, 116);
		mainGameGraphicsBuffer.drawGraphics(0, 0, super.graphics);
		minimapState = 0;
		destX = 0;
		RSSocket rsSocket = socketStream;
		loggedIn = false;
		loginFailures = 0;
		login(myUsername, myPassword, true);
		if (!loggedIn)
			resetLogout();
		try {
			rsSocket.close();
		} catch (Exception _ex) {
		}
	}
	
    public static String formatNumber(double number) {
        return NumberFormat.getInstance().format(number);
    }

	private void doAction(int i) {
		if (i < 0)
			return;
		if (inputDialogState != 0 && inputDialogState != 3) {
			inputDialogState = 0;
			inputTaken = true;
		}
		int j = menuActionCmd2[i];
		int k = menuActionCmd3[i];
		int l = menuActionID[i];
		int i1 = menuActionCmd1[i];
		// System.out.println("Click: " + i + ", "+ j + ", " + k + ", " + k + ", " + i1);
		//System.out.println("Click button: l: " + l + ", k: " + k);
		if (l >= 2000)
			l -= 2000;
		if (l == 1100) {
			RSInterface button = RSInterface.interfaceCache[k];
			button.setMenuVisible(button.isMenuVisible() ? false : true);
		}
		if (l == 474) {
			drawExperienceCounter = !drawExperienceCounter;
		}
		if (l == 475) {
			stream.createFrame(185);
			stream.writeWord(-1);
			experienceCounter = 0L;
		}
		if (l == 850) {
			teleportInterface();
		}
		
		if (l == 661) { // intid, slot, itemid;
			stream.createFrame(232);
			stream.method432(1);
			stream.method432(i1);
		}
		if (l == 662) {
			stream.createFrame(232);
			stream.method432(2);
			stream.method432(i1);
		}
		if (l == 663) {
			stream.createFrame(232);
			stream.method432(3);
			stream.method432(i1);
		}
		if (l == 664) {
			stream.createFrame(232);
			stream.method432(4);
			stream.method432(i1);
		}
		
		switch (l) {
		case 1500: //Toggle quick prayers
			prayClicked = !prayClicked;
			stream.createFrame(185); 
			stream.writeWord(5000);
		break;
		
		case 1506: //Select quick prayers
			stream.createFrame(185); 
			stream.writeWord(5001);
			setTab(5);
		break;
		}
		if (l == 1200) {
			stream.createFrame(185);
			stream.writeWord(k);
			RSInterface item = RSInterface.interfaceCache[k];
			RSInterface menu = RSInterface.interfaceCache[item.mOverInterToTrigger];
			menu.setMenuItem(item.getMenuItem());
			menu.setMenuVisible(false);
		}
		if (l >= 1700 && l <= 1710) {
			stream.createFrame(185);
			int offset = k + (k - 58030) * 10 + (l - 1700);
			stream.writeWord(offset);
		}
		if (l == 300) {
			stream.createFrame(141);
			stream.method432(j);
			stream.writeWord(k);
			stream.method432(i1);
			stream.writeDWord(modifiableXValue);
		}
		if (l == 291) {
			stream.createFrame(140);
			stream.method433(k);
			stream.method433(i1);
			stream.method431(j);
			atInventoryLoopCycle = 0;
			atInventoryInterface = k;
			atInventoryIndex = j;
			atInventoryInterfaceType = 2;
			if (RSInterface.interfaceCache[k].parentID == openInterfaceID)
				atInventoryInterfaceType = 1;
			if (RSInterface.interfaceCache[k].parentID == backDialogID)
				atInventoryInterfaceType = 3;
		}
		if (l == 582) {
			NPC npc = npcArray[i1];
			if (npc != null) {
				doWalkTo(2, 0, 1, 0, myPlayer.smallY[0], 1, 0, npc.smallY[0], myPlayer.smallX[0], false, npc.smallX[0]);
				crossX = super.saveClickX;
				crossY = super.saveClickY;
				crossType = 2;
				crossIndex = 0;
				stream.createFrame(57);
				stream.method432(anInt1285);
				stream.method432(i1);
				stream.method431(anInt1283);
				stream.method432(anInt1284);
			}
		}
		if (l == 234) {
			boolean flag1 = doWalkTo(2, 0, 0, 0, myPlayer.smallY[0], 0, 0, k, myPlayer.smallX[0], false, j);
			if (!flag1)
				flag1 = doWalkTo(2, 0, 1, 0, myPlayer.smallY[0], 1, 0, k, myPlayer.smallX[0], false, j);
			crossX = super.saveClickX;
			crossY = super.saveClickY;
			crossType = 2;
			crossIndex = 0;
			stream.createFrame(236);
			stream.method431(k + baseY);
			stream.writeWord(i1);
			stream.method431(j + baseX);
		}
		if (l == 62 && method66(i1, k, j)) {
			stream.createFrame(192);
			stream.writeWord(anInt1284);
			stream.method431(i1 >> 14 & 0x7fff);
			stream.method433(k + baseY);
			stream.method431(anInt1283);
			stream.method433(j + baseX);
			stream.writeWord(anInt1285);
		}
		if (l == 511) {
			boolean flag2 = doWalkTo(2, 0, 0, 0, myPlayer.smallY[0], 0, 0, k, myPlayer.smallX[0], false, j);
			if (!flag2)
				flag2 = doWalkTo(2, 0, 1, 0, myPlayer.smallY[0], 1, 0, k, myPlayer.smallX[0], false, j);
			crossX = super.saveClickX;
			crossY = super.saveClickY;
			crossType = 2;
			crossIndex = 0;
			stream.createFrame(25);
			stream.method431(anInt1284);
			stream.method432(anInt1285);
			stream.writeWord(i1);
			stream.method432(k + baseY);
			stream.method433(anInt1283);
			stream.writeWord(j + baseX);
		}
		if (l == 74) {
			stream.createFrame(122);
			stream.method433(k);
			stream.method432(j);
			stream.method431(i1);
			atInventoryLoopCycle = 0;
			atInventoryInterface = k;
			atInventoryIndex = j;
			atInventoryInterfaceType = 2;
			if (RSInterface.interfaceCache[k].parentID == openInterfaceID)
				atInventoryInterfaceType = 1;
			if (RSInterface.interfaceCache[k].parentID == backDialogID)
				atInventoryInterfaceType = 3;
		}
		if (l == 315) {
			RSInterface class9 = RSInterface.interfaceCache[k];
			boolean flag8 = true;
			if (class9.contentType > 0)
				flag8 = promptUserForInput(class9);
			if (flag8) {

				switch (k) {
				
				case 37708:
					if (coloredItemColor != 0xffffff) {
					inputTaken = true;
 					inputDialogState = 0;
 					messagePromptRaised = true;
 					promptInput = "";
 					friendsListAction = 25;//25 = enter item name
 					aString1121 = "Enter an item name to set this color to. (Name must match exact)";
					} else {
						pushMessage("You must select a color first...", 0, "");
					}
					break;
				case 37710:
					inputTaken = true;
 					inputDialogState = 0;
 					messagePromptRaised = true;
 					promptInput = "";
 					friendsListAction = 26;//25 = enter item name
 					aString1121 = "Enter a minimum item value to display on the ground.";
					break;
				case 37712:
					groundItemColors.clear();
					pushMessage("You have successfully cleared all the set item colors.", 0, "");
					break;
				case 37706:
					Robot robot2;
					PointerInfo pointer2;
					pointer2 = MouseInfo.getPointerInfo();
					Point coord2 = pointer2.getLocation();
				      try {
							robot2 = new Robot();
							coord2 = MouseInfo.getPointerInfo().getLocation();       
				            Color color = robot2.getPixelColor((int)coord2.getX(), (int)coord2.getY());
				            String hex2 = String.format("%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue());
				            int hex3 = Integer.parseInt(hex2, 16);
							//pushMessage("<shad="+hex3+">Yell Color set.</shad>", 0, "");
							coloredItemColor = hex3;
							RSInterface.interfaceCache[37707].message = "Current color chosen!";
							RSInterface.interfaceCache[37707].textColor = coloredItemColor;
				        } catch (AWTException e) {
				        	e.printStackTrace();
				        }
					break;
				
				case 62013:
					RSInterface input = RSInterface.interfaceCache[RSInterface.selectedItemInterfaceId + 1];
					RSInterface itemContainer = RSInterface.interfaceCache[RSInterface.selectedItemInterfaceId];
					if (RSInterface.selectedItemInterfaceId <= 0) {
						return;
					}
					if (input != null && itemContainer != null) {
						int amount = -1;
						try {
							amount = Integer.parseInt(input.message);
						} catch (NumberFormatException nfe) {
							pushMessage("The amount must be a non-negative numerical value.", 0, "");
							break;
						}
						if (itemContainer.itemSearchSelectedId < 0) {
							itemContainer.itemSearchSelectedId = 0;
						}
						if (itemContainer.itemSearchSelectedSlot < 0) {
							itemContainer.itemSearchSelectedSlot = 0;
						}
						stream.createFrame(124);
						stream.writeDWord(RSInterface.selectedItemInterfaceId);
						stream.writeDWord(itemContainer.itemSearchSelectedSlot);
						stream.writeDWord(itemContainer.itemSearchSelectedId - 1);
						stream.writeDWord(amount);
					}
					break;
				
				case 32013:
					RSInterface input2 = RSInterface.interfaceCache[RSInterface.selectedItemInterfaceId + 1];
					RSInterface itemContainer2 = RSInterface.interfaceCache[RSInterface.selectedItemInterfaceId];
					if (RSInterface.selectedItemInterfaceId <= 0) {
						return;
					}
					if (input2 != null && itemContainer2 != null) {
						int amount = -1;
						try {
							amount = Integer.parseInt(input2.message);
						} catch (NumberFormatException nfe) {
							pushMessage("The amount must be a non-negative numerical value.", 0, "");
							break;
						}
						if (itemContainer2.itemSearchSelectedId < 0) {
							itemContainer2.itemSearchSelectedId = 0;
						}
						if (itemContainer2.itemSearchSelectedSlot < 0) {
							itemContainer2.itemSearchSelectedSlot = 0;
						}
						stream.createFrame(124);
						stream.writeDWord(RSInterface.selectedItemInterfaceId);
						stream.writeDWord(itemContainer2.itemSearchSelectedSlot);
						stream.writeDWord(itemContainer2.itemSearchSelectedId - 1);
						stream.writeDWord(amount);
					}
					break;
				case 19144:
					sendFrame248(15106, 3213);
					method60(15106);
					inputTaken = true;
					break;
				default:
					stream.createFrame(185);
					stream.writeWord(k);
					
					if (k >= 61101 && k <= 61200) {
						int selected = k - 61101;
						for (int ii = 0, slot = -1; ii < ItemDefinition.totalItems && slot < 100; ii++) {
							ItemDefinition def = ItemDefinition.forID(ii);

							if (def.name == null || def.certTemplateID == ii - 1 || def.certID == ii - 1 || RSInterface.interfaceCache[61254].message.length() == 0) {
								continue;
							}

							if (def.name.toLowerCase().contains(RSInterface.interfaceCache[61254].message.toLowerCase())) {
								slot++;
							}

							if (slot != selected) {
								continue;
							}

							int id = def.id;
							long num = Long.valueOf(RSInterface.interfaceCache[61255].message.replaceAll(",", ""));

							if (num > Integer.MAX_VALUE) {
								num = Integer.MAX_VALUE;
							}

							stream.createFrame(149);
							stream.writeWord(id);
							stream.writeDWord((int) num);
							stream.writeWordBigEndian(variousSettings[1075]);
							break;
						}
					}
					
					break;

				}
			}
		}
		if (l == 561) {
			Player player = playerArray[i1];
			if (player != null) {
				doWalkTo(2, 0, 1, 0, myPlayer.smallY[0], 1, 0, player.smallY[0], myPlayer.smallX[0], false,
						player.smallX[0]);
				crossX = super.saveClickX;
				crossY = super.saveClickY;
				crossType = 2;
				crossIndex = 0;
				anInt1188 += i1;
				if (anInt1188 >= 90) {
					stream.createFrame(136);
					anInt1188 = 0;
				}
				stream.createFrame(128);
				stream.writeWord(i1);
			}
		}
		if (l == 745) {
			stream.createFrame(8);
			stream.writeDWord(i1);
		}
		if (l == 20) {
			NPC class30_sub2_sub4_sub1_sub1_1 = npcArray[i1];
			if (class30_sub2_sub4_sub1_sub1_1 != null) {
				doWalkTo(2, 0, 1, 0, myPlayer.smallY[0], 1, 0, class30_sub2_sub4_sub1_sub1_1.smallY[0],
						myPlayer.smallX[0], false, class30_sub2_sub4_sub1_sub1_1.smallX[0]);
				crossX = super.saveClickX;
				crossY = super.saveClickY;
				crossType = 2;
				crossIndex = 0;
				stream.createFrame(155);
				stream.method431(i1);
			}
		}
		if (l == 779) {
			Player class30_sub2_sub4_sub1_sub2_1 = playerArray[i1];
			if (class30_sub2_sub4_sub1_sub2_1 != null) {
				doWalkTo(2, 0, 1, 0, myPlayer.smallY[0], 1, 0, class30_sub2_sub4_sub1_sub2_1.smallY[0],
						myPlayer.smallX[0], false, class30_sub2_sub4_sub1_sub2_1.smallX[0]);
				crossX = super.saveClickX;
				crossY = super.saveClickY;
				crossType = 2;
				crossIndex = 0;
				stream.createFrame(153);
				stream.method431(i1);
			}
		}
		if (l == 516)
			if (!menuOpen)
				worldController.method312(super.saveClickY - 4, super.saveClickX - 4);
			else
				worldController.method312(k - 4, j - 4);
		if (l == 1062) {
			anInt924 += baseX;
			if (anInt924 >= 113) {
				stream.createFrame(183);
				stream.writeDWordBigEndian(0xe63271);
				anInt924 = 0;
			}
			method66(i1, k, j);
			stream.createFrame(228);
			stream.method432(i1 >> 14 & 0x7fff);
			stream.method432(k + baseY);
			stream.writeWord(j + baseX);
		}
		if (l == 679 && !aBoolean1149) {
			stream.createFrame(40);
			stream.writeWord(k);
			aBoolean1149 = true;
		}
		if (l == 431) {
			stream.createFrame(129);
			stream.method432(j);
			stream.writeWord(k);
			stream.method432(i1);
			atInventoryLoopCycle = 0;
			atInventoryInterface = k;
			atInventoryIndex = j;
			atInventoryInterfaceType = 2;
			if (RSInterface.interfaceCache[k].parentID == openInterfaceID)
				atInventoryInterfaceType = 1;
			if (RSInterface.interfaceCache[k].parentID == backDialogID)
				atInventoryInterfaceType = 3;
		}
		if (l == 337 || l == 42 || l == 792 || l == 322) {
			String s = menuActionName[i];
			int k1 = s.indexOf("@whi@");
			if (k1 != -1) {
				long l3 = TextClass.longForName(s.substring(k1 + 5).trim());
				if (l == 337)
					addFriend(l3);
				if (l == 42)
					addIgnore(l3);
				if (l == 792)
					delFriend(l3);
				if (l == 322)
					delIgnore(l3);
			}
		}
		if (l == 1337) { //Placeholders
			 inputString = "::placeholder-"+j+"-"+i1;
	            stream.createFrame(103);
				stream.writeWordBigEndian(inputString.length() - 1);
				stream.writeString(inputString.substring(2));
				inputString = "";
		}
		if (l == 53) {
			stream.createFrame(135);
			stream.method431(j);
			stream.method432(k);
			stream.method431(i1);
			atInventoryLoopCycle = 0;
			atInventoryInterface = k;
			atInventoryIndex = j;
			atInventoryInterfaceType = 2;
			if (RSInterface.interfaceCache[k].parentID == openInterfaceID)
				atInventoryInterfaceType = 1;
			if (RSInterface.interfaceCache[k].parentID == backDialogID)
				atInventoryInterfaceType = 3;
		}
		if (l == 539) {
			stream.createFrame(16);
			stream.method432(i1);
			stream.method433(j);
			stream.method433(k);
			atInventoryLoopCycle = 0;
			atInventoryInterface = k;
			atInventoryIndex = j;
			atInventoryInterfaceType = 2;
			if (RSInterface.interfaceCache[k].parentID == openInterfaceID)
				atInventoryInterfaceType = 1;
			if (RSInterface.interfaceCache[k].parentID == backDialogID)
				atInventoryInterfaceType = 3;
		}
		if (l == 484 || l == 6) {
			String s1 = menuActionName[i];
			int l1 = s1.indexOf("@whi@");
			if (l1 != -1) {
				s1 = s1.substring(l1 + 5).trim();
				String s7 = TextClass.fixName(TextClass.nameForLong(TextClass.longForName(s1)));
				boolean flag9 = false;
				for (int j3 = 0; j3 < playerCount; j3++) {
					Player class30_sub2_sub4_sub1_sub2_7 = playerArray[playerIndices[j3]];
					if (class30_sub2_sub4_sub1_sub2_7 == null || class30_sub2_sub4_sub1_sub2_7.name == null
							|| !class30_sub2_sub4_sub1_sub2_7.name.equalsIgnoreCase(s7))
						continue;
					doWalkTo(2, 0, 1, 0, myPlayer.smallY[0], 1, 0, class30_sub2_sub4_sub1_sub2_7.smallY[0],
							myPlayer.smallX[0], false, class30_sub2_sub4_sub1_sub2_7.smallX[0]);
					if (l == 484) {
						stream.createFrame(39);
						stream.method431(playerIndices[j3]);
					}
					if (l == 6) {
						anInt1188 += i1;
						if (anInt1188 >= 90) {
							stream.createFrame(136);
							anInt1188 = 0;
						}
						stream.createFrame(128);
						stream.writeWord(playerIndices[j3]);
					}
					flag9 = true;
					break;
				}

				if (!flag9)
					pushMessage("Unable to find " + s7, 0, "");
			}
		}
		if (l == 870) {
			stream.createFrame(53);
			stream.writeWord(j);
			stream.method432(anInt1283);
			stream.method433(i1);
			stream.writeWord(anInt1284);
			stream.method431(anInt1285);
			stream.writeWord(k);
			atInventoryLoopCycle = 0;
			atInventoryInterface = k;
			atInventoryIndex = j;
			atInventoryInterfaceType = 2;
			if (RSInterface.interfaceCache[k].parentID == openInterfaceID)
				atInventoryInterfaceType = 1;
			if (RSInterface.interfaceCache[k].parentID == backDialogID)
				atInventoryInterfaceType = 3;
		}
		if (l == 847) {
			stream.createFrame(87);
			stream.method432(i1);
			stream.writeWord(k);
			stream.method432(j);
			atInventoryLoopCycle = 0;
			atInventoryInterface = k;
			atInventoryIndex = j;
			atInventoryInterfaceType = 2;
			if (RSInterface.interfaceCache[k].parentID == openInterfaceID)
				atInventoryInterfaceType = 1;
			if (RSInterface.interfaceCache[k].parentID == backDialogID)
				atInventoryInterfaceType = 3;
		}
		if (l == 626) {
			RSInterface class9_1 = RSInterface.interfaceCache[k];
			spellSelected = 1;
			spellID = class9_1.id;
			anInt1137 = k;
			spellUsableOn = class9_1.spellUsableOn;
			itemSelected = 0;
			needDrawTabArea = true;
			spellID = class9_1.id;
			String s4 = class9_1.selectedActionName;
			if (s4.indexOf(" ") != -1)
				s4 = s4.substring(0, s4.indexOf(" "));
			String s8 = class9_1.selectedActionName;
			if (s8.indexOf(" ") != -1)
				s8 = s8.substring(s8.indexOf(" ") + 1);
			spellTooltip = s4 + " " + class9_1.spellName + " " + s8;
			// class9_1.sprite1.drawSprite(class9_1.anInt263, class9_1.anInt265,
			// 0xffffff);
			// class9_1.sprite1.drawSprite(200,200);
			// System.out.println("Sprite: " + class9_1.sprite1.toString());
			if (spellUsableOn == 16) {
				needDrawTabArea = true;
				tabID = 3;
				tabAreaAltered = true;
			}
			return;
		}
		if (l == 104) {
			RSInterface class9_1 = RSInterface.interfaceCache[k];
			spellID = class9_1.id;
			if (!autocast) {
				autocast = true;
				autocastId = class9_1.id;
				stream.createFrame(185);
				stream.writeWord(class9_1.id);
			} else if (autocastId == class9_1.id) {
				autocast = false;
				autocastId = 0;
				stream.createFrame(185);
				stream.writeWord(6666); // reset server side
			} else if (autocastId != class9_1.id) {
				autocast = true;
				autocastId = class9_1.id;
				stream.createFrame(185);
				stream.writeWord(class9_1.id);
			}
		}
		if (l == 78) {
			stream.createFrame(117);
			stream.method433(k);
			stream.method433(i1);
			stream.method431(j);
			atInventoryLoopCycle = 0;
			atInventoryInterface = k;
			atInventoryIndex = j;
			atInventoryInterfaceType = 2;
			if (RSInterface.interfaceCache[k].parentID == openInterfaceID)
				atInventoryInterfaceType = 1;
			if (RSInterface.interfaceCache[k].parentID == backDialogID)
				atInventoryInterfaceType = 3;
		}
		if (l == 27) {
			Player class30_sub2_sub4_sub1_sub2_2 = playerArray[i1];
			if (class30_sub2_sub4_sub1_sub2_2 != null) {
				doWalkTo(2, 0, 1, 0, myPlayer.smallY[0], 1, 0, class30_sub2_sub4_sub1_sub2_2.smallY[0],
						myPlayer.smallX[0], false, class30_sub2_sub4_sub1_sub2_2.smallX[0]);
				crossX = super.saveClickX;
				crossY = super.saveClickY;
				crossType = 2;
				crossIndex = 0;
				anInt986 += i1;
				if (anInt986 >= 54) {
					stream.createFrame(189);
					stream.writeWordBigEndian(234);
					anInt986 = 0;
				}
				stream.createFrame(73);
				stream.method431(i1);
			}
		}
		if (l == 213) {
			boolean flag3 = doWalkTo(2, 0, 0, 0, myPlayer.smallY[0], 0, 0, k, myPlayer.smallX[0], false, j);
			if (!flag3)
				flag3 = doWalkTo(2, 0, 1, 0, myPlayer.smallY[0], 1, 0, k, myPlayer.smallX[0], false, j);
			crossX = super.saveClickX;
			crossY = super.saveClickY;
			crossType = 2;
			crossIndex = 0;
			stream.createFrame(79);
			stream.method431(k + baseY);
			stream.writeWord(i1);
			stream.method432(j + baseX);
		}
		if (l == 632) {
			stream.createFrame(145);
			stream.method432(k);
			stream.method432(j);
			stream.method432(i1);
			atInventoryLoopCycle = 0;
			atInventoryInterface = k;
			atInventoryIndex = j;
			atInventoryInterfaceType = 2;
			if (RSInterface.interfaceCache[k].parentID == openInterfaceID)
				atInventoryInterfaceType = 1;
			if (RSInterface.interfaceCache[k].parentID == backDialogID)
				atInventoryInterfaceType = 3;
		}
		if (l == 1050) {
			if (!runClicked) {
				runClicked = true;
				stream.createFrame(185);
				stream.writeWord(152);
			} else {
				runClicked = false;
				stream.createFrame(185);
				stream.writeWord(152);
			}
		}
		if (l == 1004) {
			if (tabInterfaceIDs[10] != -1) {
				needDrawTabArea = true;
				setSidebarInterface(14, 2449);
				tabID = 10;
				tabAreaAltered = true;
			}
		}
		if (l == 1003) {
			clanChatMode = 2;
			inputTaken = true;
		}
		if (l == 1002) {
			clanChatMode = 1;
			inputTaken = true;
		}
		if (l == 1001) {
			clanChatMode = 0;
			inputTaken = true;
		}
		if (l == 1000) {
			// chatButtonClickPosition = 4;
			chatTypeView = 11;
			inputTaken = true;
		}
		if (l == 999) {
			// chatButtonClickPosition = 0;
			chatTypeView = 0;
			inputTaken = true;
		}
		if (l == 998) {
			// chatButtonClickPosition = 1;
			chatTypeView = 5;
			inputTaken = true;
		}
		if (l == 1005) {
			// chatButtonClickPosition = 1;
			chatTypeView = 12;
			inputTaken = true;
		}
		if (l == 997) {
			publicChatMode = 3;
			stream.createFrame(95);
			stream.writeWordBigEndian(publicChatMode);
			stream.writeWordBigEndian(privateChatMode);
			stream.writeWordBigEndian(tradeMode);
			inputTaken = true;
		}
		if (l == 996) {
			publicChatMode = 2;
			stream.createFrame(95);
			stream.writeWordBigEndian(publicChatMode);
			stream.writeWordBigEndian(privateChatMode);
			stream.writeWordBigEndian(tradeMode);
			inputTaken = true;
		}
		if (l == 995) {
			publicChatMode = 1;
			stream.createFrame(95);
			stream.writeWordBigEndian(publicChatMode);
			stream.writeWordBigEndian(privateChatMode);
			stream.writeWordBigEndian(tradeMode);
			inputTaken = true;
		}
		if (l == 994) {
			publicChatMode = 0;
			inputTaken = true;
		}
		if (l == 993) {
			// chatButtonClickPosition = 2;
			chatTypeView = 1;
			inputTaken = true;
		}
		if (l == 992) {
			privateChatMode = 2;
			stream.createFrame(95);
			stream.writeWordBigEndian(publicChatMode);
			stream.writeWordBigEndian(privateChatMode);
			stream.writeWordBigEndian(tradeMode);
			inputTaken = true;
		}
		if (l == 991) {
			privateChatMode = 1;
			stream.createFrame(95);
			stream.writeWordBigEndian(publicChatMode);
			stream.writeWordBigEndian(privateChatMode);
			stream.writeWordBigEndian(tradeMode);
			inputTaken = true;
		}
		if (l == 990) {
			privateChatMode = 0;
			stream.createFrame(95);
			stream.writeWordBigEndian(publicChatMode);
			stream.writeWordBigEndian(privateChatMode);
			stream.writeWordBigEndian(tradeMode);
			inputTaken = true;
		}
		if (l == 989) {
			// chatButtonClickPosition = 3;
			chatTypeView = 2;
			inputTaken = true;
		}
		if (l == 987) {
			tradeMode = 2;
			stream.createFrame(95);
			stream.writeWordBigEndian(publicChatMode);
			stream.writeWordBigEndian(privateChatMode);
			stream.writeWordBigEndian(tradeMode);
			inputTaken = true;
		}
		if (l == 986) {
			tradeMode = 1;
			stream.createFrame(95);
			stream.writeWordBigEndian(publicChatMode);
			stream.writeWordBigEndian(privateChatMode);
			stream.writeWordBigEndian(tradeMode);
			inputTaken = true;
		}
		if (l == 985) {
			tradeMode = 0;
			stream.createFrame(95);
			stream.writeWordBigEndian(publicChatMode);
			stream.writeWordBigEndian(privateChatMode);
			stream.writeWordBigEndian(tradeMode);
			inputTaken = true;
		}
		if (l == 984) {
			// chatButtonClickPosition = 5;
			chatTypeView = 3;
			inputTaken = true;
		}
		if (l == 983) {
			duelMode = 2;
			inputTaken = true;
		}
		if (l == 982) {
			duelMode = 1;
			inputTaken = true;
		}
		if (l == 981) {
			duelMode = 0;
			inputTaken = true;
		}
		if (l == 980) {
			// chatButtonClickPosition = 6;
			chatTypeView = 4;
			inputTaken = true;
		}
		if (l == 493) {
			stream.createFrame(75);
			stream.method433(k);
			stream.method431(j);
			stream.method432(i1);
			atInventoryLoopCycle = 0;
			atInventoryInterface = k;
			atInventoryIndex = j;
			atInventoryInterfaceType = 2;
			if (RSInterface.interfaceCache[k].parentID == openInterfaceID)
				atInventoryInterfaceType = 1;
			if (RSInterface.interfaceCache[k].parentID == backDialogID)
				atInventoryInterfaceType = 3;
		}
		if (l == 652) {
			boolean flag4 = doWalkTo(2, 0, 0, 0, myPlayer.smallY[0], 0, 0, k, myPlayer.smallX[0], false, j);
			if (!flag4)
				flag4 = doWalkTo(2, 0, 1, 0, myPlayer.smallY[0], 1, 0, k, myPlayer.smallX[0], false, j);
			crossX = super.saveClickX;
			crossY = super.saveClickY;
			crossType = 2;
			crossIndex = 0;
			stream.createFrame(156);
			stream.method432(j + baseX);
			stream.method431(k + baseY);
			stream.method433(i1);
		}
		if (l == 94) {
			boolean flag5 = doWalkTo(2, 0, 0, 0, myPlayer.smallY[0], 0, 0, k, myPlayer.smallX[0], false, j);
			if (!flag5)
				flag5 = doWalkTo(2, 0, 1, 0, myPlayer.smallY[0], 1, 0, k, myPlayer.smallX[0], false, j);
			crossX = super.saveClickX;
			crossY = super.saveClickY;
			crossType = 2;
			crossIndex = 0;
			stream.createFrame(181);
			stream.method431(k + baseY);
			stream.writeWord(i1);
			stream.method431(j + baseX);
			stream.method432(anInt1137);
		}
		// clan chat
		if (l == 647) {
			stream.createFrame(213);
			stream.writeWord(k);
			stream.writeWord(j);
			switch (k) {
			case 18304:
				if (j == 0) {
					inputTaken = true;
					inputDialogState = 0;
					messagePromptRaised = true;
					promptInput = "";
					friendsListAction = 8;
					aString1121 = "Enter your clan chat title";
				}
				break;
			}
		}
		if (l == 646) { /** TODO **/
			stream.createFrame(185);
			stream.writeWord(k);
			if (!clickConfigButton(k)) {
				RSInterface class9_2 = RSInterface.interfaceCache[k];
				if (class9_2.valueIndexArray != null && class9_2.valueIndexArray[0][0] == 5) {
					int i2 = class9_2.valueIndexArray[0][1];
					if (variousSettings[i2] != class9_2.anIntArray212[0]) {
						variousSettings[i2] = class9_2.anIntArray212[0];
						method33(i2);
						needDrawTabArea = true;
					}

					System.out.println(class9_2.id + ", " + i2 + ", " + variousSettings[i2] + ", " + class9_2.anIntArray212[0]);
				}
				switch (k) {
				// clan chat
				case 18129:
					if (RSInterface.interfaceCache[18135].message.toLowerCase().contains("join")) {
						inputTaken = true;
						inputDialogState = 0;
						messagePromptRaised = true;
						promptInput = "";
						friendsListAction = 6;
						aString1121 = "Enter the name of the chat you wish to join";
					} else {
						sendString(0, "");
					}
					break;

				case 18132:
					openInterfaceID = 18300;
					break;

				case 18526:
					inputTaken = true;
					inputDialogState = 0;
					messagePromptRaised = true;
					promptInput = "";
					friendsListAction = 9;
					aString1121 = "Enter a name to add";
					break;

				case 18527:
					inputTaken = true;
					inputDialogState = 0;
					messagePromptRaised = true;
					promptInput = "";
					friendsListAction = 10;
					aString1121 = "Enter a name to add";
					break;
				}
			}
		}
		if (l == 225) {
			NPC class30_sub2_sub4_sub1_sub1_2 = npcArray[i1];
			if (class30_sub2_sub4_sub1_sub1_2 != null) {
				doWalkTo(2, 0, 1, 0, myPlayer.smallY[0], 1, 0, class30_sub2_sub4_sub1_sub1_2.smallY[0],
						myPlayer.smallX[0], false, class30_sub2_sub4_sub1_sub1_2.smallX[0]);
				crossX = super.saveClickX;
				crossY = super.saveClickY;
				crossType = 2;
				crossIndex = 0;
				anInt1226 += i1;
				if (anInt1226 >= 85) {
					stream.createFrame(230);
					stream.writeWordBigEndian(239);
					anInt1226 = 0;
				}
				stream.createFrame(17);
				stream.method433(i1);
			}
		}
		if (l == 965) {
			NPC class30_sub2_sub4_sub1_sub1_3 = npcArray[i1];
			if (class30_sub2_sub4_sub1_sub1_3 != null) {
				doWalkTo(2, 0, 1, 0, myPlayer.smallY[0], 1, 0, class30_sub2_sub4_sub1_sub1_3.smallY[0],
						myPlayer.smallX[0], false, class30_sub2_sub4_sub1_sub1_3.smallX[0]);
				crossX = super.saveClickX;
				crossY = super.saveClickY;
				crossType = 2;
				crossIndex = 0;
				anInt1134++;
				if (anInt1134 >= 96) {
					stream.createFrame(152);
					stream.writeWordBigEndian(88);
					anInt1134 = 0;
				}
				stream.createFrame(21);
				stream.writeWord(i1);
			}
		}
		if (l == 413) {
			NPC class30_sub2_sub4_sub1_sub1_4 = npcArray[i1];
			if (class30_sub2_sub4_sub1_sub1_4 != null) {
				doWalkTo(2, 0, 1, 0, myPlayer.smallY[0], 1, 0, class30_sub2_sub4_sub1_sub1_4.smallY[0],
						myPlayer.smallX[0], false, class30_sub2_sub4_sub1_sub1_4.smallX[0]);
				crossX = super.saveClickX;
				crossY = super.saveClickY;
				crossType = 2;
				crossIndex = 0;
				stream.createFrame(131);
				stream.method433(i1);
				stream.method432(anInt1137);
			}
		}
		if (l == 200)
			clearTopInterfaces();

		if (l == 201) {
			for (int index = 0; index < GrandExchange.grandExchangeItemBoxIds.length; index++)
				if (k == GrandExchange.grandExchangeItemBoxIds[index] + 1)
					openInterfaceID = GrandExchange.grandExchangeOfferStatusInterfaceIds[index];

			switch (k) {

			case 25705:
			case 25557:
				openInterfaceID = 25000;
				break;

			}
		}

		if (l == 1025) {
			NPC class30_sub2_sub4_sub1_sub1_5 = npcArray[i1];
			if (class30_sub2_sub4_sub1_sub1_5 != null) {
				NpcDefinition entityDef = class30_sub2_sub4_sub1_sub1_5.desc;
				if (entityDef.childrenIDs != null)
					entityDef = entityDef.method161();
				if (entityDef != null) {
					String s9;
					if (entityDef.description != null)
						s9 = new String(entityDef.description);
					else
						s9 = "It's a " + entityDef.name + ".";
					pushMessage(s9, 0, "");
				}
			}
		}
		if (l == 900) {
			method66(i1, k, j);
			stream.createFrame(252);
			stream.method433(i1 >> 14 & 0x7fff);
			stream.method431(k + baseY);
			stream.method432(j + baseX);
		}
		if (l == 412) {
			NPC class30_sub2_sub4_sub1_sub1_6 = npcArray[i1];
			if (class30_sub2_sub4_sub1_sub1_6 != null) {
				doWalkTo(2, 0, 1, 0, myPlayer.smallY[0], 1, 0, class30_sub2_sub4_sub1_sub1_6.smallY[0],
						myPlayer.smallX[0], false, class30_sub2_sub4_sub1_sub1_6.smallX[0]);
				crossX = super.saveClickX;
				crossY = super.saveClickY;
				crossType = 2;
				crossIndex = 0;
				stream.createFrame(72);
				stream.method432(i1);
			}
		}
		if (l == 365) {
			Player class30_sub2_sub4_sub1_sub2_3 = playerArray[i1];
			if (class30_sub2_sub4_sub1_sub2_3 != null) {
				doWalkTo(2, 0, 1, 0, myPlayer.smallY[0], 1, 0, class30_sub2_sub4_sub1_sub2_3.smallY[0],
						myPlayer.smallX[0], false, class30_sub2_sub4_sub1_sub2_3.smallX[0]);
				crossX = super.saveClickX;
				crossY = super.saveClickY;
				crossType = 2;
				crossIndex = 0;
				stream.createFrame(249);
				stream.method432(i1);
				stream.method431(anInt1137);
			}
		}
		if (l == 729) {
			Player class30_sub2_sub4_sub1_sub2_4 = playerArray[i1];
			if (class30_sub2_sub4_sub1_sub2_4 != null) {
				doWalkTo(2, 0, 1, 0, myPlayer.smallY[0], 1, 0, class30_sub2_sub4_sub1_sub2_4.smallY[0],
						myPlayer.smallX[0], false, class30_sub2_sub4_sub1_sub2_4.smallX[0]);
				crossX = super.saveClickX;
				crossY = super.saveClickY;
				crossType = 2;
				crossIndex = 0;
				stream.createFrame(39);
				stream.method431(i1);
			}
		}
		if (l == 577) {
			Player class30_sub2_sub4_sub1_sub2_5 = playerArray[i1];
			if (class30_sub2_sub4_sub1_sub2_5 != null) {
				doWalkTo(2, 0, 1, 0, myPlayer.smallY[0], 1, 0, class30_sub2_sub4_sub1_sub2_5.smallY[0],
						myPlayer.smallX[0], false, class30_sub2_sub4_sub1_sub2_5.smallX[0]);
				crossX = super.saveClickX;
				crossY = super.saveClickY;
				crossType = 2;
				crossIndex = 0;
				stream.createFrame(139);
				stream.method431(i1);
			}
		}
		if (l == 956 && method66(i1, k, j)) {
			stream.createFrame(35);
			stream.method431(j + baseX);
			stream.method432(anInt1137);
			stream.method432(k + baseY);
			stream.method431(i1 >> 14 & 0x7fff);
		}
		if (l == 567) {
			boolean flag6 = doWalkTo(2, 0, 0, 0, myPlayer.smallY[0], 0, 0, k, myPlayer.smallX[0], false, j);
			if (!flag6)
				flag6 = doWalkTo(2, 0, 1, 0, myPlayer.smallY[0], 1, 0, k, myPlayer.smallX[0], false, j);
			crossX = super.saveClickX;
			crossY = super.saveClickY;
			crossType = 2;
			crossIndex = 0;
			stream.createFrame(23);
			stream.method431(k + baseY);
			stream.method431(i1);
			stream.method431(j + baseX);
		}
		if (l == 867) {
			if ((i1 & 3) == 0)
				anInt1175++;
			if (anInt1175 >= 59) {
				stream.createFrame(200);
				stream.createFrame(201);
				stream.writeWord(25501);
				anInt1175 = 0;
			}
			stream.createFrame(43);
			stream.method431(k);
			stream.method432(i1);
			stream.method432(j);
			atInventoryLoopCycle = 0;
			atInventoryInterface = k;
			atInventoryIndex = j;
			atInventoryInterfaceType = 2;
			if (RSInterface.interfaceCache[k].parentID == openInterfaceID)
				atInventoryInterfaceType = 1;
			if (RSInterface.interfaceCache[k].parentID == backDialogID)
				atInventoryInterfaceType = 3;
		}
		if (l == 543) {
			stream.createFrame(237);
			stream.writeWord(j);
			stream.method432(i1);
			stream.writeWord(k);
			stream.method432(anInt1137);
			atInventoryLoopCycle = 0;
			atInventoryInterface = k;
			atInventoryIndex = j;
			atInventoryInterfaceType = 2;
			if (RSInterface.interfaceCache[k].parentID == openInterfaceID)
				atInventoryInterfaceType = 1;
			if (RSInterface.interfaceCache[k].parentID == backDialogID)
				atInventoryInterfaceType = 3;
		}
		if (l == 606) {
			String s2 = menuActionName[i];
			int j2 = s2.indexOf("@whi@");
			if (j2 != -1)
				if (openInterfaceID == -1) {
					clearTopInterfaces();
					reportAbuseInput = s2.substring(j2 + 5).trim();
					canMute = false;
					for (int i3 = 0; i3 < RSInterface.interfaceCache.length; i3++) {
						if (RSInterface.interfaceCache[i3] == null || RSInterface.interfaceCache[i3].contentType != 600)
							continue;
						reportAbuseInterfaceID = openInterfaceID = RSInterface.interfaceCache[i3].parentID;
						break;
					}

				} else {
					pushMessage("Please close the interface you have open before using 'report abuse'", 0, "");
				}
		}
		if (l == 491) {
			Player class30_sub2_sub4_sub1_sub2_6 = playerArray[i1];
			if (class30_sub2_sub4_sub1_sub2_6 != null) {
				doWalkTo(2, 0, 1, 0, myPlayer.smallY[0], 1, 0, class30_sub2_sub4_sub1_sub2_6.smallY[0],
						myPlayer.smallX[0], false, class30_sub2_sub4_sub1_sub2_6.smallX[0]);
				crossX = super.saveClickX;
				crossY = super.saveClickY;
				crossType = 2;
				crossIndex = 0;
				stream.createFrame(14);
				stream.method432(anInt1284);
				stream.writeWord(i1);
				stream.writeWord(anInt1285);
				stream.method431(anInt1283);
			}
		}
		if (l == 639) {
			String s3 = menuActionName[i];
			int k2 = s3.indexOf("@whi@");
			if (k2 != -1) {
				long l4 = TextClass.longForName(s3.substring(k2 + 5).trim());
				int k3 = -1;
				for (int i4 = 0; i4 < friendsCount; i4++) {
					if (friendsListAsLongs[i4] != l4)
						continue;
					k3 = i4;
					break;
				}

				if (k3 != -1 && friendsNodeIDs[k3] > 0) {
					inputTaken = true;
					inputDialogState = 0;
					messagePromptRaised = true;
					promptInput = "";
					friendsListAction = 3;
					aLong953 = friendsListAsLongs[k3];
					aString1121 = "Enter message to send to " + friendsList[k3];
				}
			}
		}
		if (l == 1052) {
			stream.createFrame(185);
			stream.writeWord(154);
		}

		if (l == 1004) {
			if (tabInterfaceIDs[14] != -1) {
				needDrawTabArea = true;
				tabID = 14;
				tabAreaAltered = true;
			}
		}

		if (l == 454) {
			stream.createFrame(41);
			stream.writeWord(i1);
			stream.method432(j);
			stream.method432(k);
			atInventoryLoopCycle = 0;
			atInventoryInterface = k;
			atInventoryIndex = j;
			atInventoryInterfaceType = 2;
			if (RSInterface.interfaceCache[k].parentID == openInterfaceID)
				atInventoryInterfaceType = 1;
			if (RSInterface.interfaceCache[k].parentID == backDialogID)
				atInventoryInterfaceType = 3;
		}
		if (l == 696) {
			viewRotation = 0;
			anInt1184 = 120;

		}
		if (l == 478) {
			NPC class30_sub2_sub4_sub1_sub1_7 = npcArray[i1];
			if (class30_sub2_sub4_sub1_sub1_7 != null) {
				doWalkTo(2, 0, 1, 0, myPlayer.smallY[0], 1, 0, class30_sub2_sub4_sub1_sub1_7.smallY[0],
						myPlayer.smallX[0], false, class30_sub2_sub4_sub1_sub1_7.smallX[0]);
				crossX = super.saveClickX;
				crossY = super.saveClickY;
				crossType = 2;
				crossIndex = 0;
				if ((i1 & 3) == 0)
					anInt1155++;
				if (anInt1155 >= 53) {
					stream.createFrame(85);
					stream.writeWordBigEndian(66);
					anInt1155 = 0;
				}
				stream.createFrame(18);
				stream.method431(i1);
			}
		}
		if (l == 113) {
			method66(i1, k, j);
			stream.createFrame(70);
			stream.method431(j + baseX);
			stream.writeWord(k + baseY);
			stream.method433(i1 >> 14 & 0x7fff);
		}
		if (l == 872) {
			method66(i1, k, j);
			stream.createFrame(234);
			stream.method433(j + baseX);
			stream.method432(i1 >> 14 & 0x7fff);
			stream.method433(k + baseY);
		}
		if (l == 502) {
			method66(i1, k, j);
			stream.createFrame(132);
			stream.method433(j + baseX);
			stream.writeWord(i1 >> 14 & 0x7fff);
			stream.method432(k + baseY);
		}
		if (l == 1125) {
			ItemDefinition itemDef = ItemDefinition.forID(i1);
			RSInterface class9_4 = RSInterface.interfaceCache[k];
			String s5;
			if (class9_4 != null && class9_4.invStackSizes[j] >= 0x186a0)
				s5 = formatNumber(class9_4.invStackSizes[j]) + " x " + itemDef.name;
			else if (itemDef.description != null)
				s5 = new String(itemDef.description);
			else
				s5 = "It's a " + itemDef.name + ".";
			pushMessage(s5, 0, "");
		}
		if (l == 1130) {
			RSInterface class9_4 = RSInterface.interfaceCache[k];
			if (class9_4 != null) {
				class9_4.itemSearchSelectedId = class9_4.inv[j];
				class9_4.itemSearchSelectedSlot = j;
				RSInterface.selectedItemInterfaceId = class9_4.id;
			}
		}
		if (l == 169) {
			stream.createFrame(185);
			stream.writeWord(k);
			RSInterface class9_3 = RSInterface.interfaceCache[k];
			if (class9_3.valueIndexArray != null && class9_3.valueIndexArray[0][0] == 5) {
				int l2 = class9_3.valueIndexArray[0][1];
				variousSettings[l2] = 1 - variousSettings[l2];
				method33(l2);
				needDrawTabArea = true;
			}
		}
		if (l == 447) {
			itemSelected = 1;
			anInt1283 = j;
			anInt1284 = k;
			anInt1285 = i1;
			selectedItemName = ItemDefinition.forID(i1).name;
			spellSelected = 0;
			needDrawTabArea = true;
			return;
		}
		if (l == 1226) {
			int j1 = i1 >> 14 & 0x7fff;
			ObjectDefinition class46 = ObjectDefinition.forID(j1);
			String s10;
			if (class46.description != null)
				s10 = new String(class46.description);
			else
				s10 = "It's a " + class46.name + ".";
			pushMessage(s10, 0, "");
		}
		if (l == 244) {
			boolean flag7 = doWalkTo(2, 0, 0, 0, myPlayer.smallY[0], 0, 0, k, myPlayer.smallX[0], false, j);
			if (!flag7)
				flag7 = doWalkTo(2, 0, 1, 0, myPlayer.smallY[0], 1, 0, k, myPlayer.smallX[0], false, j);
			crossX = super.saveClickX;
			crossY = super.saveClickY;
			crossType = 2;
			crossIndex = 0;
			stream.createFrame(253);
			stream.method431(j + baseX);
			stream.method433(k + baseY);
			stream.method432(i1);
		}
		if (l == 1448) {
			ItemDefinition itemDef_1 = ItemDefinition.forID(i1);
			String s6;
			if (itemDef_1.description != null)
				s6 = new String(itemDef_1.description);
			else
				s6 = "It's a " + itemDef_1.name + ".";
			pushMessage(s6, 0, "");
		}
		itemSelected = 0;
		spellSelected = 0;
		needDrawTabArea = true;

	}

	private boolean removeRoofs, leftClickAttack = true, chatEffects = true, drawOrbs = true, gameTimers = true, showEntityTarget = true;

	private void setConfigButtonsAtStartup() {
		setConfigButton(23101, drawOrbs);
		setConfigButton(23109, splitPrivateChat);
		setConfigButton(23107, chatEffects);
		setConfigButton(23103, informationFile.isRememberRoof() ? true : false);
		setConfigButton(23105, leftClickAttack);
		setConfigButton(23111, gameTimers);
		setConfigButton(23113, showEntityTarget);
		setConfigButton(23115, informationFile.isRememberVisibleItemNames() ? true : false);
		setConfigButton(23118, shiftDrop ? true : false);
		setConfigButton(23001, true);
		setConfigButton(953, true);

		/** Sets the brightness level **/
		RSInterface class9_2 = RSInterface.interfaceCache[910];
		if (class9_2.valueIndexArray != null && class9_2.valueIndexArray[0][0] == 5) {
			int i2 = class9_2.valueIndexArray[0][1];
			if (variousSettings[i2] != class9_2.anIntArray212[0]) {
				variousSettings[i2] = class9_2.anIntArray212[0];
				method33(i2);
				needDrawTabArea = true;
			}
		}
	}

	private boolean clickConfigButton(int i) {

		switch (i) {

		case 953:
			if (tabInterfaceIDs[11] != 23000) {
				tabInterfaceIDs[11] = 23000;
				setConfigButton(i, true);
				setConfigButton(959, false);
			}
			return true;

		case 959:
			if (tabInterfaceIDs[11] != 23100) {
				tabInterfaceIDs[11] = 23100;
				setConfigButton(i, true);
				setConfigButton(953, false);
			}
			return true;

		case 23001:
			if (currentScreenMode != ScreenMode.FIXED) {
				setConfigButton(i, true);
				setConfigButton(23003, false);
				setConfigButton(23005, false);
				setGameMode(ScreenMode.FIXED);
			}
			return true;

		case 23003:
			if (currentScreenMode != ScreenMode.RESIZABLE) {
				setConfigButton(i, true);
				setConfigButton(23001, false);
				setConfigButton(23005, false);
				setGameMode(ScreenMode.RESIZABLE);
			}
			return true;

		case 23005:
			if (currentScreenMode != ScreenMode.FULLSCREEN) {
				setConfigButton(i, true);
				setConfigButton(23001, false);
				setConfigButton(23003, false);
				setGameMode(ScreenMode.FULLSCREEN);
			}
			return true;

		case 23101:
			setConfigButton(i, drawOrbs = !drawOrbs);
			return true;

		case 23109:
			setConfigButton(i, splitPrivateChat = !splitPrivateChat);
			return true;

		case 23103:
			setConfigButton(i, removeRoofs = !removeRoofs);
			informationFile.setRememberRoof(informationFile.isRememberRoof() ? false : true);
			try {
				informationFile.write();
				System.out.println("Written to: " + informationFile.isRememberRoof());
			} catch (IOException e) {
				e.printStackTrace();
			}
			return true;

		case 23105:
			setConfigButton(i, leftClickAttack = !leftClickAttack);
			return true;

		case 23107:
			setConfigButton(i, chatEffects = !chatEffects);
			return true;

		case 23111:
			setConfigButton(i, gameTimers = !gameTimers);
			return true;

		case 23113:
			setConfigButton(i, showEntityTarget = !showEntityTarget);
			return true;

		case 23118:
			setConfigButton(i, shiftDrop = !shiftDrop);
			return true;

		case 23115:
			setConfigButton(i, groundItemsOn = !groundItemsOn);
			informationFile.setRememberRoof(informationFile.isRememberVisibleItemNames() ? false : true);
			try {
				informationFile.write();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return true;

		}

		return false;
	}

	private void setConfigButton(int interfaceFrame, boolean configSetting) {
		int config = configSetting ? 1 : 0;
		anIntArray1045[interfaceFrame] = config;
		if (variousSettings[interfaceFrame] != config) {
			variousSettings[interfaceFrame] = config;
			needDrawTabArea = true;
			if (dialogID != -1)
				inputTaken = true;
		}
	}

	public void method70() {
		anInt1251 = 0;
		int j = (myPlayer.x >> 7) + baseX;
		int k = (myPlayer.y >> 7) + baseY;
		if (j >= 3053 && j <= 3156 && k >= 3056 && k <= 3136)
			anInt1251 = 1;
		if (j >= 3072 && j <= 3118 && k >= 9492 && k <= 9535)
			anInt1251 = 1;
		if (anInt1251 == 1 && j >= 3139 && j <= 3199 && k >= 3008 && k <= 3062)
			anInt1251 = 0;
	}

	@Override
	public void run() {
		if (drawFlames) {
			drawFlames();
		} else {
			super.run();
		}
	}

	private void build3dScreenMenu() {
		if (itemSelected == 0 && spellSelected == 0) {
			menuActionName[menuActionRow] = "Walk here";
			menuActionID[menuActionRow] = 516;
			menuActionCmd2[menuActionRow] = super.mouseX;
			menuActionCmd3[menuActionRow] = super.mouseY;
			menuActionRow++;
		}
		int j = -1;
		for (int k = 0; k < Model.anInt1687; k++) {
			int l = Model.anIntArray1688[k];
			int i1 = l & 0x7f;
			int j1 = l >> 7 & 0x7f;
			int k1 = l >> 29 & 3;
			int l1 = l >> 14 & 0x7fff;
			if (l == j)
				continue;
			j = l;
			if (k1 == 2 && worldController.method304(plane, i1, j1, l) >= 0) {
				ObjectDefinition class46 = ObjectDefinition.forID(l1);
				if (class46.childrenIDs != null) {
					class46 = class46.method580();
				}
				if (class46 == null) {
					continue;
				}
				if (class46.name == null) {
					continue;
				}
				if (itemSelected == 1) {
					menuActionName[menuActionRow] = "Use " + selectedItemName + " with @cya@" + class46.name;
					menuActionID[menuActionRow] = 62;
					menuActionCmd1[menuActionRow] = l;
					menuActionCmd2[menuActionRow] = i1;
					menuActionCmd3[menuActionRow] = j1;
					menuActionRow++;
				} else if (spellSelected == 1) {
					if ((spellUsableOn & 4) == 4) {
						menuActionName[menuActionRow] = spellTooltip + " @cya@" + class46.name;
						menuActionID[menuActionRow] = 956;
						menuActionCmd1[menuActionRow] = l;
						menuActionCmd2[menuActionRow] = i1;
						menuActionCmd3[menuActionRow] = j1;
						menuActionRow++;
					}
				} else {
					if (class46.actions != null) {
						for (int i2 = 4; i2 >= 0; i2--)
							if (class46.actions[i2] != null) {
								menuActionName[menuActionRow] = class46.actions[i2] + " @cya@" + class46.name;
								if (i2 == 0)
									menuActionID[menuActionRow] = 502;
								if (i2 == 1)
									menuActionID[menuActionRow] = 900;
								if (i2 == 2)
									menuActionID[menuActionRow] = 113;
								if (i2 == 3)
									menuActionID[menuActionRow] = 872;
								if (i2 == 4)
									menuActionID[menuActionRow] = 1062;
								menuActionCmd1[menuActionRow] = l;
								menuActionCmd2[menuActionRow] = i1;
								menuActionCmd3[menuActionRow] = j1;
								menuActionRow++;
							}

					}

					menuActionName[menuActionRow] = "Examine @cya@" + class46.name;
					if (debugModels == true) {
						menuActionName[menuActionRow] = "Examine @cya@" + class46.name + " @gre@ID: @whi@" + l1
								+ " @gre@X, Y: @whi@" + (i1 + baseX) + "," + (j1 + baseY) + " @gre@Models: @whi@"
								+ Arrays.toString(class46.anIntArray773);

					}

					menuActionID[menuActionRow] = 1226;
					menuActionCmd1[menuActionRow] = class46.type << 14;
					menuActionCmd2[menuActionRow] = i1;
					menuActionCmd3[menuActionRow] = j1;
					menuActionRow++;
				}
			}
			if (k1 == 1) {
				NPC npc = npcArray[l1];
				if (npc.desc.boundDim == 1 && (npc.x & 0x7f) == 64 && (npc.y & 0x7f) == 64) {
					for (int j2 = 0; j2 < npcCount; j2++) {
						NPC npc2 = npcArray[npcIndices[j2]];
						if (npc2 != null && npc2 != npc && npc2.desc.boundDim == 1 && npc2.x == npc.x
								&& npc2.y == npc.y)
							buildAtNPCMenu(npc2.desc, npcIndices[j2], j1, i1);
					}

					for (int l2 = 0; l2 < playerCount; l2++) {
						Player player = playerArray[playerIndices[l2]];
						if (player != null && player.x == npc.x && player.y == npc.y)
							buildAtPlayerMenu(i1, playerIndices[l2], player, j1);
					}

				}
				buildAtNPCMenu(npc.desc, l1, j1, i1);
			}
			if (k1 == 0) {
				Player player = playerArray[l1];
				if ((player.x & 0x7f) == 64 && (player.y & 0x7f) == 64) {
					for (int k2 = 0; k2 < npcCount; k2++) {
						NPC class30_sub2_sub4_sub1_sub1_2 = npcArray[npcIndices[k2]];
						if (class30_sub2_sub4_sub1_sub1_2 != null
								&& class30_sub2_sub4_sub1_sub1_2.desc.boundDim == 1
								&& class30_sub2_sub4_sub1_sub1_2.x == player.x
								&& class30_sub2_sub4_sub1_sub1_2.y == player.y)
							buildAtNPCMenu(class30_sub2_sub4_sub1_sub1_2.desc, npcIndices[k2], j1, i1);
					}

					for (int i3 = 0; i3 < playerCount; i3++) {
						Player class30_sub2_sub4_sub1_sub2_2 = playerArray[playerIndices[i3]];
						if (class30_sub2_sub4_sub1_sub2_2 != null && class30_sub2_sub4_sub1_sub2_2 != player
								&& class30_sub2_sub4_sub1_sub2_2.x == player.x
								&& class30_sub2_sub4_sub1_sub2_2.y == player.y)
							buildAtPlayerMenu(i1, playerIndices[i3], class30_sub2_sub4_sub1_sub2_2, j1);
					}

				}
				buildAtPlayerMenu(i1, l1, player, j1);
			}
			if (k1 == 3) {
				NodeList class19 = groundArray[plane][i1][j1];
				if (class19 != null) {
					for (Item item = (Item) class19.getFirst(); item != null; item = (Item) class19.getNext()) {
						ItemDefinition itemDef = ItemDefinition.forID(item.ID);
						if (itemSelected == 1) {
							menuActionName[menuActionRow] = "Use " + selectedItemName + " with @lre@" + itemDef.name;
							menuActionID[menuActionRow] = 511;
							menuActionCmd1[menuActionRow] = item.ID;
							menuActionCmd2[menuActionRow] = i1;
							menuActionCmd3[menuActionRow] = j1;
							menuActionRow++;
						} else if (spellSelected == 1) {
							if ((spellUsableOn & 1) == 1) {
								menuActionName[menuActionRow] = spellTooltip + " @lre@" + itemDef.name + "";
								menuActionID[menuActionRow] = 94;
								menuActionCmd1[menuActionRow] = item.ID;
								menuActionCmd2[menuActionRow] = i1;
								menuActionCmd3[menuActionRow] = j1;
								menuActionRow++;
							}
						} else {
							for (int j3 = 4; j3 >= 0; j3--)
								if (itemDef.groundOptions != null && itemDef.groundOptions[j3] != null) {
									menuActionName[menuActionRow] = itemDef.groundOptions[j3] + " @lre@" + itemDef.name;
									if (j3 == 0)
										menuActionID[menuActionRow] = 652;
									if (j3 == 1)
										menuActionID[menuActionRow] = 567;
									if (j3 == 2)
										menuActionID[menuActionRow] = 234;
									if (j3 == 3)
										menuActionID[menuActionRow] = 244;
									if (j3 == 4)
										menuActionID[menuActionRow] = 213;
									menuActionCmd1[menuActionRow] = item.ID;
									menuActionCmd2[menuActionRow] = i1;
									menuActionCmd3[menuActionRow] = j1;
									menuActionRow++;
								} else if (j3 == 2) {
									menuActionName[menuActionRow] = "Take @lre@" + itemDef.name;
									menuActionID[menuActionRow] = 234;
									menuActionCmd1[menuActionRow] = item.ID;
									menuActionCmd2[menuActionRow] = i1;
									menuActionCmd3[menuActionRow] = j1;
									menuActionRow++;
								}
							menuActionName[menuActionRow] = "Examine @lre@" + itemDef.name;
							if (debugModels == true) {
								menuActionName[menuActionRow] = "Examine @lre@" + itemDef.name + " @gre@(@whi@"
										+ item.ID + "@gre@)";
							}
							menuActionID[menuActionRow] = 1448;
							menuActionCmd1[menuActionRow] = item.ID;
							menuActionCmd2[menuActionRow] = i1;
							menuActionCmd3[menuActionRow] = j1;
							menuActionRow++;
						}
					}
				}
			}
		}
	}

	@Override
	public void cleanUpForQuit() {
		Signlink.reporterror = false;
		try {
			if (socketStream != null)
				socketStream.close();
		} catch (Exception _ex) {
		}
		socketStream = null;
		stopMidi();
		if (mouseDetection != null)
			mouseDetection.running = false;
		mapBack = null;
		cacheSprite = null;
		cacheInterface = null;
		mouseDetection = null;
		onDemandFetcher.disable();
		onDemandFetcher = null;
		aStream_834 = null;
		loginScreenGraphicsBuffer = null;
		stream = null;
		aStream_847 = null;
		inStream = null;
		anIntArray1234 = null;
		aByteArrayArray1183 = null;
		aByteArrayArray1247 = null;
		anIntArray1235 = null;
		anIntArray1236 = null;
		intGroundArray = null;
		byteGroundArray = null;
		worldController = null;
		aClass11Array1230 = null;
		anIntArrayArray901 = null;
		anIntArrayArray825 = null;
		bigX = null;
		bigY = null;
		aByteArray912 = null;
		tabAreaGraphicsBuffer = null;
		mapAreaGraphicsBuffer = null;
		mainGameGraphicsBuffer = null;
		loginScreenGraphicsBuffer = null;
		chatAreaGraphicsBuffer = null;

		tabAreaFixed = null;
		tabAreaResizable = null;
		compassImage = null;
		redStones = null;
		sideIcons = null;
		mapArea = null;

		/* Null pointers for custom sprites */
		multiOverlay = null;
		chatArea = null;
		chatButtons = null;
		mapArea = null;
		channelButtons = null;
		/**/

		magicAuto = null;
		minimapSprites[14] = null;
		minimapSprites[15] = null;
		hitMarks = null;
		headIcons = null;
		skullIcons = null;
		headIconsHint = null;
		crosses = null;
		mapDotItem = null;
		mapDotNPC = null;
		mapDotPlayer = null;
		mapDotFriend = null;
		mapScenes = null;
		mapFunctions = null;
		anIntArrayArray929 = null;
		playerArray = null;
		playerIndices = null;
		anIntArray894 = null;
		aStreamArray895s = null;
		anIntArray840 = null;
		npcArray = null;
		npcIndices = null;
		groundArray = null;
		aClass19_1179 = null;
		aClass19_1013 = null;
		aClass19_1056 = null;
		menuActionCmd2 = null;
		menuActionCmd3 = null;
		menuActionID = null;
		menuActionCmd1 = null;
		menuActionName = null;
		variousSettings = null;
		anIntArray1072 = null;
		anIntArray1073 = null;
		mapIconSprite = null;
		minimapImage = null;
		friendsList = null;
		friendsListAsLongs = null;
		friendsNodeIDs = null;
		aRSImageProducer_1110 = null;
		aRSImageProducer_1111 = null;
		nullLoader();
		ObjectDefinition.nullLoader();
		NpcDefinition.nullLoader();
		ItemDefinition.nullLoader();
		FloorUnderlayDefinition.cache = null;
		FloorOverlayDefinition.cache = null;
		IDK.cache = null;
		RSInterface.interfaceCache = null;
		DummyClass.cache = null;
		AnimationDefinition.anims = null;
		GraphicsDefinition.cache = null;
		GraphicsDefinition.aMRUNodes_415 = null;
		Varp.cache = null;
		super.fullGameScreen = null;
		Player.mruNodes = null;
		Texture.nullLoader();
		WorldController.nullLoader();
		Model.nullLoader();
		Class36.nullLoader();
		System.gc();
	}

	public void printDebug() {
		System.out.println("============");
		System.out.println("flame-cycle:" + anInt1208);
		if (onDemandFetcher != null)
			System.out.println("Od-cycle:" + onDemandFetcher.onDemandCycle);
		System.out.println("loop-cycle:" + loopCycle);
		System.out.println("draw-cycle:" + anInt1061);
		System.out.println("ptype:" + incomingPacket);
		System.out.println("psize:" + packetSize);
		if (socketStream != null)
			socketStream.printDebug();
		super.shouldDebug = true;
	}

	Component getGameComponent() {
		if (Signlink.mainapp != null)
			return Signlink.mainapp;
		return this;
	}

	public void pmTabToReply() {
		String name = null;
		for (int k = 0; k < 100; k++) {
			if (chatMessages[k] == null) {
				continue;
			}
			int l = chatTypes[k];
			if (l == 3 || l == 7) {
				name = chatNames[k];
				break;
			}
		}

		if (name == null) {
			pushMessage("You haven't received any messages to which you can reply.", 0, "");
			return;
		}

		if (name.startsWith("@cr1@") || name.startsWith("@cr2@") || name.startsWith("@cr3@") || name.startsWith("@cr4@")
				|| name.startsWith("@cr5@") || name.startsWith("@cr6@") || name.startsWith("@cr7@")
				|| name.startsWith("@cr8@") || name.startsWith("@cr9@")) {
			name = name.substring(5);
		} else if (name.startsWith("@cr10@") || name.startsWith("@cr11@") || name.startsWith("@cr12@")
				|| name.startsWith("@cr13@") || name.startsWith("@cr14@") || name.startsWith("@cr15@")
				|| name.startsWith("@cr16@") || name.startsWith("@cr17@") || name.startsWith("@cr18@")
				|| name.startsWith("@cr19@") || name.startsWith("@cr20@") || name.startsWith("@cr21@")
				|| name.startsWith("@cr22@")
				|| name.startsWith("@cr23@")) {
			name = name.substring(6);
		} else {
			name = name.substring(0);
		}

		long nameAsLong = TextClass.longForName(name.trim());
		int k3 = -1;
		for (int i4 = 0; i4 < friendsCount; i4++) {
			if (friendsListAsLongs[i4] != nameAsLong)
				continue;
			k3 = i4;
			break;
		}

		if (k3 != -1) {
			if (friendsNodeIDs[k3] > 0) {
				inputTaken = true;
				inputDialogState = 0;
				messagePromptRaised = true;
				promptInput = "";
				friendsListAction = 3;
				aLong953 = friendsListAsLongs[k3];
				aString1121 = "Enter message to send to " + friendsList[k3];
			} else {
				pushMessage("That player is currently offline.", 0, "");
			}
		}
	}

	private void method73() {
		do {
			int j = readChar(-796);
			if (j == -1)
				break;
			if (openInterfaceID != -1 && openInterfaceID == reportAbuseInterfaceID) {
				if (j == 8 && reportAbuseInput.length() > 0)
					reportAbuseInput = reportAbuseInput.substring(0, reportAbuseInput.length() - 1);
				if ((j >= 97 && j <= 122 || j >= 65 && j <= 90 || j >= 48 && j <= 57 || j == 32)
						&& reportAbuseInput.length() < 12)
					reportAbuseInput += (char) j;
			} else if (messagePromptRaised) {
				if (j >= 32 && j <= 122 && promptInput.length() < 80) {
					promptInput += (char) j;
					inputTaken = true;
				}
				if (j == 8 && promptInput.length() > 0) {
					promptInput = promptInput.substring(0, promptInput.length() - 1);
					inputTaken = true;
				}
				if (j == 13 || j == 10) {
					messagePromptRaised = false;
					inputTaken = true;
					if (friendsListAction == 1) {
						long l = TextClass.longForName(promptInput);
						addFriend(l);
					}
					if (friendsListAction == 2 && friendsCount > 0) {
						long l1 = TextClass.longForName(promptInput);
						delFriend(l1);
					}
					if (friendsListAction == 3 && promptInput.length() > 0) {
						stream.createFrame(126);
						stream.writeWordBigEndian(0);
						int k = stream.currentOffset;
						stream.writeQWord(aLong953);
						TextInput.method526(promptInput, stream);
						stream.writeBytes(stream.currentOffset - k);
						promptInput = TextInput.processText(promptInput);
						// promptInput = Censor.doCensor(promptInput);
						pushMessage(promptInput, 6, TextClass.fixName(TextClass.nameForLong(aLong953)));
						if (privateChatMode == 2) {
							privateChatMode = 1;
							stream.createFrame(95);
							stream.writeWordBigEndian(publicChatMode);
							stream.writeWordBigEndian(privateChatMode);
							stream.writeWordBigEndian(tradeMode);
						}
					}
					if (friendsListAction == 4 && ignoreCount < 100) {
						long l2 = TextClass.longForName(promptInput);
						addIgnore(l2);
					}
					if (friendsListAction == 5 && ignoreCount > 0) {
						long l3 = TextClass.longForName(promptInput);
						delIgnore(l3);
					}
					// clan chat
					if (friendsListAction == 6) {
						sendStringAsLong(promptInput);
					} else if (friendsListAction == 8) {
						sendString(1, promptInput);
					} else if (friendsListAction == 9) {
						sendString(2, promptInput);
					} else if (friendsListAction == 10) {
						sendString(3, promptInput);
					} else if (friendsListAction == 25) {//ground item color
						coloredItemName = promptInput;
						//System.out.println("Wanted to change : " + coloredItemName + " to color: " + coloredItemColor);
						setItemColor(coloredItemName, coloredItemColor);
					} else if (friendsListAction == 26) {//ground item min value
						if (promptInput.toLowerCase().contains("k")) {
							promptInput = promptInput.replaceAll("k", "000");
						} else if (promptInput.toLowerCase().contains("m")) {
							promptInput = promptInput.replaceAll("m", "000000");
						} else if (promptInput.toLowerCase().contains("b")) {
							promptInput = promptInput.replaceAll("b", "000000000");
						}
						promptInput = promptInput.replaceAll("[^0-9]", "");
						if (promptInput == null || promptInput.equalsIgnoreCase(""))
							return;
						long fakeAmount = Long.parseLong(promptInput);
						fakeAmount += 0L;
						if (fakeAmount > Integer.MAX_VALUE)
							promptInput = "2147483647";
						groundItemValueLimit = Integer.parseInt(promptInput);
						pushMessage("New minimum ground item value is: " + NumberFormat.getInstance().format(groundItemValueLimit) + "gp.", 0, "");
					}
				}
			} else if (inputDialogState == 1) {
				if (j >= 48 && j <= 57 && amountOrNameInput.length() < 10) {
					amountOrNameInput += (char) j;
					inputTaken = true;
				}
				if ((!amountOrNameInput.toLowerCase().contains("k") && !amountOrNameInput.toLowerCase().contains("m")
						&& !amountOrNameInput.toLowerCase().contains("b")) && (j == 107 || j == 109) || j == 98) {
					amountOrNameInput += (char) j;
					inputTaken = true;
				}
				if (j == 8 && amountOrNameInput.length() > 0) {
					amountOrNameInput = amountOrNameInput.substring(0, amountOrNameInput.length() - 1);
					inputTaken = true;
				}
				if (j == 13 || j == 10) {
					if (amountOrNameInput.length() > 0) {
						if (amountOrNameInput.toLowerCase().contains("k")) {
							amountOrNameInput = amountOrNameInput.replaceAll("k", "000");
						} else if (amountOrNameInput.toLowerCase().contains("m")) {
							amountOrNameInput = amountOrNameInput.replaceAll("m", "000000");
						} else if (amountOrNameInput.toLowerCase().contains("b")) {
							amountOrNameInput = amountOrNameInput.replaceAll("b", "000000000");
						}
						int i1 = 0;
						try {
							i1 = Integer.parseInt(amountOrNameInput);
						} catch (Exception _ex) {
						}
						stream.createFrame(208);
						stream.writeDWord(i1);
						modifiableXValue = i1;
						if (modifiableXValue < 0) 
							modifiableXValue = 1;
					}
					inputDialogState = 0;
					inputTaken = true;
				}
			} else if (inputDialogState == 2) {
				if (j >= 32 && j <= 122 && amountOrNameInput.length() < 58) {
					amountOrNameInput += (char) j;
					inputTaken = true;
				}
				if (j == 8 && amountOrNameInput.length() > 0) {
					amountOrNameInput = amountOrNameInput.substring(0, amountOrNameInput.length() - 1);
					inputTaken = true;
				}
				if (j == 13 || j == 10) {
					if (amountOrNameInput.length() > 0) {
						stream.createFrame(60);
						stream.writeQWord(TextClass.longForName(amountOrNameInput));
					}
					inputDialogState = 0;
					inputTaken = true;
				}
			} else if (inputDialogState == 3) {
				if (j == 10) {
					inputDialogState = 0;
					inputTaken = true;
				}
				if (j >= 32 && j <= 122 && amountOrNameInput.length() < 40) {
					amountOrNameInput += (char) j;
					inputTaken = true;
				}
				if (j == 8 && amountOrNameInput.length() > 0) {
					amountOrNameInput = amountOrNameInput.substring(0, amountOrNameInput.length() - 1);
					inputTaken = true;
				}
			} else if (backDialogID == -1) {
				if (this.isFieldInFocus()) {
					RSInterface rsi = this.getInputFieldFocusOwner();
					if (rsi == null) {
						return;
					}
					if (j >= 32 && j <= 122 && rsi.message.length() < rsi.characterLimit) {
						if (rsi.inputRegex.length() > 0) {
							pattern = Pattern.compile(rsi.inputRegex);
							matcher = pattern.matcher(Character.toString(((char) j)));
							if (matcher.matches()) {
								rsi.message += (char) j;
								inputTaken = true;
							}
						} else {
							rsi.message += (char) j;
							inputTaken = true;
						}
					}
					if (j == 8 && rsi.message.length() > 0) {
						rsi.message = rsi.message.substring(0, rsi.message.length() - 1);
						inputTaken = true;
					}
					if (rsi.isItemSearchComponent && rsi.message.length() > 2
							&& rsi.defaultInputFieldText.equals("Name")) {
						RSInterface subcomponent = RSInterface.interfaceCache[rsi.id + 2];
						RSInterface scroll = RSInterface.interfaceCache[rsi.id + 4];
						RSInterface toggle = RSInterface.interfaceCache[rsi.id + 9];
						scroll.itemSearchSelectedId = 0;
						scroll.itemSearchSelectedSlot = -1;
						RSInterface.selectedItemInterfaceId = 0;
						rsi.itemSearchSelectedSlot = -1;
						rsi.itemSearchSelectedId = 0;
						if (subcomponent != null && scroll != null && toggle != null
								&& toggle.valueIndexArray != null) {
							ItemSearch itemSearch = new ItemSearch(rsi.message.toLowerCase(), 60, false);
							int[] results = itemSearch.getItemSearchResults();
							if (subcomponent != null) {
								int position = 0;
								int length = subcomponent.inv.length;
								subcomponent.inv = new int[length];
								subcomponent.invStackSizes = new int[subcomponent.inv.length];
								for (int result : results) {
									if (result > 0) {
										subcomponent.inv[position] = result + 1;
										subcomponent.invStackSizes[position] = 1;
										position++;
									}
								}
							}
						}
					} else if (rsi.updatesEveryInput && rsi.message.length() > 0 && j != 10 && j != 13) {
						stream.createFrame(142);
						stream.writeWordBigEndian(4 + rsi.message.length() + 1);
						stream.writeDWord(rsi.id);
						stream.writeString(rsi.message);
						inputString = "";
						promptInput = "";
						break;
					} else if ((j == 10 || j == 13) && rsi.message.length() > 0 && !rsi.updatesEveryInput) {
						stream.createFrame(142);
						stream.writeWordBigEndian(4 + rsi.message.length() + 1);
						stream.writeDWord(rsi.id);
						stream.writeString(rsi.message);
						inputString = "";
						promptInput = "";
						break;
					}
				} else {
					if (j >= 32 && j <= 122 && inputString.length() < 80) {
						inputString += (char) j;
						inputTaken = true;
					}
					if (j == 8 && inputString.length() > 0) {
						inputString = inputString.substring(0, inputString.length() - 1);
						inputTaken = true;
					}
					if (j == 9)
						pmTabToReply();

					if ((j == 13 || j == 10) && inputString.length() > 0) {
						if (inputString.startsWith("::starttimer")) {
							try {
								String[] input = inputString.split(" ");
								int id = Integer.parseInt(input[1]);
								int duration = Integer.parseInt(input[2]);
								GameTimerHandler.getSingleton().startGameTimer(id, TimeUnit.SECONDS, duration);
							} catch (Exception exception) {
								exception.printStackTrace();
							}
						}

						if (inputString.startsWith("::screenmode")) {
							String screenMode;
							try {
								screenMode = inputString.split(" ")[1];

								ScreenMode mode = ScreenMode.valueOf(screenMode.toUpperCase());

								setGameMode(mode);
							} catch (Exception e) {
								pushMessage("Not a valid screenmode.", 0, "");
							}
						}

						if (inputString.startsWith("::dialogstate")) {
							String state;
							try {
								state = inputString.split(" ")[1];
								inputDialogState = Integer.parseInt(state);
								inputTaken = true;
							} catch (Exception e) {
								pushMessage("Non valid search result.", 0, "");
								e.printStackTrace();
							}
						}

						if (inputString.startsWith("::setprogressbar")) {
							try {
								int childId = Integer.parseInt(inputString.split(" ")[1]);
								byte progressBarState = Byte.parseByte(inputString.split(" ")[2]);
								byte progressBarPercentage = Byte.parseByte(inputString.split(" ")[3]);

								RSInterface rsi = RSInterface.interfaceCache[childId];
								rsi.progressBarState = progressBarState;
								rsi.progressBarPercentage = progressBarPercentage;

							} catch (Exception e) {
								pushMessage("Error", 0, "");
							}
						}

						if (inputString.equals("::packrsi") && (j == 13 || j == 10)) {
							// TextDrawingArea aTextDrawingArea_1273 = new
							// TextDrawingArea(true, "q8_full",
							// titleStreamLoader);
							TextDrawingArea aclass30_sub2_sub1_sub4s[] = { smallText, aTextDrawingArea_1271,
									chatTextDrawingArea, aTextDrawingArea_1273 };
							StreamLoader streamLoader_1 = streamLoaderForName(3, "interface", "interface",
									expectedCRCs[3], 35);
							StreamLoader streamLoader_2 = streamLoaderForName(4, "2d graphics", "media",
									expectedCRCs[4], 40);
							RSInterface.unpack(streamLoader_1, aclass30_sub2_sub1_sub4s, streamLoader_2);
							pushMessage("Reloaded interface configurations.", 0, "");
						}
						
//						if (inputString.startsWith("::i")) {
//							try {
//
//								String[] args = inputString.split(" ");
//
//								int id1 = Integer.parseInt(args[1]);
//
//								openInterfaceID = id1;
//
//								pushMessage("Opened Interface "+id1, 0, "");
//							} catch (Exception e) {
//								pushMessage("Interface Failed to load", 0, "");
//							}
//
//						}

						if (inputString.startsWith("::interface")) {
							if (myPlayer.name.equalsIgnoreCase("matt")) {
								try {
									String[] args = inputString.split(" ");
									if (args != null)
										pushMessage("Opening interface " + args[1] + ".", 0, "");
									openInterfaceID = Integer.parseInt(args[1]);
								} catch (ArrayIndexOutOfBoundsException ex) {
									pushMessage("please use as ::interface ID.", 0, "");
								}
							}
						}

						if (inputString.startsWith("::walkableinterface")) {
							try {
								String[] args = inputString.split(" ");
								pushMessage("Opening interface " + args[1] + ".", 0, "");
								openWalkableWidgetID = Integer.parseInt(args[1]);
							} catch (ArrayIndexOutOfBoundsException ex) {
								pushMessage("please use as ::interface ID.", 0, "");
							}
						}
						// myPlayer.getRights() == 13 ? "<img=12>" :
						// myPlayer.getRights() == 14 ? "<img=13>" :
						if (inputString.equals("::tt")) {
							pushMessage("Test", 5, "");
						}

						if (inputString.equals("::togglecounter"))
							drawExperienceCounter = !drawExperienceCounter;

						if (inputString.equals("::resetcounter") && (j == 13 || j == 10)) {
							stream.createFrame(185);
							stream.writeWord(-1);
							experienceCounter = 0L;
						}

						if (inputString.equals("::snow") && (j == 13 || j == 10) && Configuration.CHRISTMAS) {
							snowVisible = snowVisible ? false : true;
							pushMessage("The snow has been set to " + (snowVisible ? "visible" : "invisible") + ".", 0,
									"");
							method22();
						}

						if (inputString.startsWith("::npcanim")) {
							int id = 0;
							try {
								id = Integer.parseInt(inputString.split(" ")[1]);
								NpcDefinition entity = NpcDefinition.forID(id);
								if (entity == null) {
									pushMessage("Entity does not exist.", 0, "");
								} else {
									pushMessage("Id: " + id, 0, "");
									pushMessage("Name: " + entity.name, 0, "");
									pushMessage("Stand: " + entity.standAnim, 0, "");
									pushMessage("Walk: " + entity.walkAnim, 0, "");
								}
							} catch (ArrayIndexOutOfBoundsException | NumberFormatException exception) {
								exception.printStackTrace();
							}
						}
						if (inputString.startsWith("::gfxid")) {
							try {
								GraphicsDefinition anim = GraphicsDefinition
										.fetch(Integer.parseInt(inputString.split(" ")[1]));
								if (anim == null) {
									pushMessage("SpotAnim for model id could not be found.", 0, "");
								} else {
									pushMessage("Model: " + anim.getModelId() + ", Index/Id: " + anim.getIndex(), 0,
											"");
								}
							} catch (ArrayIndexOutOfBoundsException | NumberFormatException exception) {
								exception.printStackTrace();
							}
						}
						if (inputString.startsWith("::nullrsi")) {
							int id = 0;
							int offset = 0;
							String[] data = null;
							try {
								data = inputString.split(" ");
								id = Integer.parseInt(data[1]);
								offset = Integer.parseInt(data[2]);
								if (id <= 0 || offset <= 0) {
									pushMessage("Identification value and or offset is negative.", 0, "");
								} else if (id + offset > RSInterface.interfaceCache.length - 1) {
									pushMessage(
											"The total sum of the id and offset are greater than the size of the cache.",
											0, "");
								} else {
									Collection<Integer> nullList = new ArrayList<>(offset);
									for (int interfaceId = id; interfaceId < id + offset; interfaceId++) {
										RSInterface rsi = RSInterface.interfaceCache[interfaceId];
										if (rsi == null) {
											nullList.add(interfaceId);
										}
									}
									pushMessage(
											"There are a total of " + nullList.size() + "/" + offset
													+ " null interfaces from " + id + " to " + (id + offset) + ".",
											0, "");
								}
							} catch (ArrayIndexOutOfBoundsException | NumberFormatException exception) {

							}
						}

						if (myPlayer.getRights() >= 2 && myPlayer.getRights() <= 4) {
							if (inputString.startsWith("//setspecto")) {
								int amt = Integer.parseInt(inputString.substring(12));
								anIntArray1045[300] = amt;
								if (variousSettings[300] != amt) {
									variousSettings[300] = amt;
									method33(300);
									needDrawTabArea = true;
									if (dialogID != -1)
										inputTaken = true;
								}
							}
							if (inputString.equals("clientdrop"))
								dropClient();
							if (inputString.startsWith("full")) {
								try {
									String[] args = inputString.split(" ");
									int id1 = Integer.parseInt(args[1]);
									int id2 = Integer.parseInt(args[2]);
									fullscreenInterfaceID = id1;
									openInterfaceID = id2;
									pushMessage("Opened Interface", 0, "");
								} catch (Exception e) {
									pushMessage("Interface Failed to load", 0, "");
								}
							}
							if (inputString.equals("::lag"))
								printDebug();
							if (inputString.equals("::prefetchmusic")) {
								for (int j1 = 0; j1 < onDemandFetcher.getVersionCount(2); j1++)
									onDemandFetcher.method563((byte) 1, 2, j1);

							}
						}

						if (inputString.equals("::orbs")) {
							drawOrbs = !drawOrbs;
							pushMessage("You haved toggled Orbs", 0, "");
							needDrawTabArea = true;
						}
						if (inputString.equals("::fpson"))
							fpsOn = true;
						if (inputString.equals("::fpsoff"))
							fpsOn = false;
						if (inputString.equals("::data"))
							clientData = !clientData;
						if (inputString.equals("::hotkeys")) {
							RSApplet.hotKeyToggle = !RSApplet.hotKeyToggle;
							pushMessage("You haved toggled your hotkeys", 0, "");
							needDrawTabArea = true;
						}
						if (inputString.equals("::debugm")) {
							debugModels = !debugModels;
							pushMessage("Debug models", 0, "");
						}
						if (inputString.equals("::hd")) {
							setHighMem();
							method22();
						}
						if (inputString.equals("::xp")) {
							pushMessage("XP drops has been removed.", 0, "");
						}
						if (inputString.startsWith("::")) {
							stream.createFrame(103);
							stream.writeWordBigEndian(inputString.length() - 1);
							stream.writeString(inputString.substring(2));
						} else if (inputString.startsWith("/")) {
							stream.createFrame(103);
							stream.writeWordBigEndian(inputString.length() + 1);
							stream.writeString(inputString);
						} else {
							String s = inputString.toLowerCase();
							int j2 = 0;
							if (s.startsWith("yellow:")) {
								j2 = 0;
								inputString = inputString.substring(7);
							} else if (s.startsWith("red:")) {
								j2 = 1;
								inputString = inputString.substring(4);
							} else if (s.startsWith("green:")) {
								j2 = 2;
								inputString = inputString.substring(6);
							} else if (s.startsWith("cyan:")) {
								j2 = 3;
								inputString = inputString.substring(5);
							} else if (s.startsWith("purple:")) {
								j2 = 4;
								inputString = inputString.substring(7);
							} else if (s.startsWith("white:")) {
								j2 = 5;
								inputString = inputString.substring(6);
							} else if (s.startsWith("flash1:")) {
								j2 = 6;
								inputString = inputString.substring(7);
							} else if (s.startsWith("flash2:")) {
								j2 = 7;
								inputString = inputString.substring(7);
							} else if (s.startsWith("flash3:")) {
								j2 = 8;
								inputString = inputString.substring(7);
							} else if (s.startsWith("glow1:")) {
								j2 = 9;
								inputString = inputString.substring(6);
							} else if (s.startsWith("glow2:")) {
								j2 = 10;
								inputString = inputString.substring(6);
							} else if (s.startsWith("glow3:")) {
								j2 = 11;
								inputString = inputString.substring(6);
							}
							s = inputString.toLowerCase();
							int i3 = 0;
							if (s.startsWith("wave:")) {
								i3 = 1;
								inputString = inputString.substring(5);
							} else if (s.startsWith("wave2:")) {
								i3 = 2;
								inputString = inputString.substring(6);
							} else if (s.startsWith("shake:")) {
								i3 = 3;
								inputString = inputString.substring(6);
							} else if (s.startsWith("scroll:")) {
								i3 = 4;
								inputString = inputString.substring(7);
							} else if (s.startsWith("slide:")) {
								i3 = 5;
								inputString = inputString.substring(6);
							}
							stream.createFrame(4);
							stream.writeWordBigEndian(0);
							int j3 = stream.currentOffset;
							stream.method425(i3);
							stream.method425(j2);
							aStream_834.currentOffset = 0;
							TextInput.method526(inputString, aStream_834);
							stream.method441(0, aStream_834.buffer, aStream_834.currentOffset);
							stream.writeBytes(stream.currentOffset - j3);
							inputString = TextInput.processText(inputString);
							// inputString = Censor.doCensor(inputString);
							myPlayer.textSpoken = inputString;
							myPlayer.anInt1513 = j2;
							myPlayer.anInt1531 = i3;
							myPlayer.textCycle = 150;
							String crown = myPlayer.getRights() > 0 ? "@cr" + myPlayer.getRights() + "@" : "";
							if (myPlayer.title.length() > 0) {
								pushMessage(myPlayer.textSpoken, 2, crown + "<col=" + myPlayer.titleColor + ">"
										+ myPlayer.title + "</col> " + myPlayer.name);
							} else {
								pushMessage(myPlayer.textSpoken, 2, crown + myPlayer.name);
							}
							if (publicChatMode == 2) {
								publicChatMode = 3;
								stream.createFrame(95);
								stream.writeWordBigEndian(publicChatMode);
								stream.writeWordBigEndian(privateChatMode);
								stream.writeWordBigEndian(tradeMode);
							}
						}
						inputString = "";
						inputTaken = true;
					}
				}
			}
		} while (true);
	}

	private void buildPublicChat(int j) {
		int l = 0;
		for (int i1 = 0; i1 < 500; i1++) {
			if (chatMessages[i1] == null)
				continue;
			if (chatTypeView != 1)
				continue;
			int j1 = chatTypes[i1];
			String s = chatNames[i1];
			int k1 = (70 - l * 14 + 42) + anInt1089 + 4 + 5;
			if (k1 < -23)
				break;
			if (s != null) {
				if (s != null && s.startsWith("@cr1@"))
					s = s.substring(5);
				if (s != null && s.startsWith("@cr2@"))
					s = s.substring(5);
				if (s != null && s.startsWith("@cr3@"))
					s = s.substring(5);
				if (s != null && s.startsWith("@cr4@"))
					s = s.substring(5);
				if (s != null && s.startsWith("@cr5@"))
					s = s.substring(5);
				if (s != null && s.startsWith("@cr6@"))
					s = s.substring(5);
				if (s != null && s.startsWith("@cr7@"))
					s = s.substring(5);
				if (s != null && s.startsWith("@cr8@"))
					s = s.substring(5);
				if (s != null && s.startsWith("@cr9@"))
					s = s.substring(5);
				if (s != null && s.startsWith("@cr10@"))
					s = s.substring(6);
				if (s != null && s.startsWith("@cr11@"))
					s = s.substring(6);
				if (s != null && s.startsWith("@cr12@"))
					s = s.substring(6);
				if (s != null && s.startsWith("@cr13@"))
					s = s.substring(6);
				if (s != null && s.startsWith("@cr14@"))
					s = s.substring(6);
				if (s != null && s.startsWith("@cr15@"))
					s = s.substring(6);
				if (s != null && s.startsWith("@cr16@"))
					s = s.substring(6);
				if (s != null && s.startsWith("@cr17@"))
					s = s.substring(6);
				if (s != null && s.startsWith("@cr22@"))
					s = s.substring(6);
				
				if (s.startsWith("<col=")) {
					s = s.substring(s.indexOf("</col>") + 6);
				}
			}
			if ((j1 == 1 || j1 == 2) && (j1 == 1 || publicChatMode == 0 || publicChatMode == 1 && isFriendOrSelf(s))) {
				if (j > k1 - 14 && j <= k1 && !s.equals(myPlayer.name)) {
					if (myPlayer.getRights() >= 1) {
						menuActionName[menuActionRow] = "Report abuse @whi@" + s;
						menuActionID[menuActionRow] = 606;
						menuActionRow++;
					}
					menuActionName[menuActionRow] = "Add ignore @whi@" + s;
					menuActionID[menuActionRow] = 42;
					menuActionRow++;
					menuActionName[menuActionRow] = "Reply to @whi@" + s;
					menuActionID[menuActionRow] = 639;
					menuActionRow++;
					menuActionName[menuActionRow] = "Add friend @whi@" + s;
					menuActionID[menuActionRow] = 337;
					menuActionRow++;
				}
				l++;
			}
		}
	}

	private void buildFriendChat(int j) {
		int l = 0;
		for (int i1 = 0; i1 < 500; i1++) {
			if (chatMessages[i1] == null)
				continue;
			if (chatTypeView != 2)
				continue;
			int j1 = chatTypes[i1];
			String s = chatNames[i1];
			int k1 = (70 - l * 14 + 42) + anInt1089 + 4 + 5;
			if (k1 < -23)
				break;
			if (s != null && s.startsWith("@cr1@"))
				s = s.substring(5);
			if (s != null && s.startsWith("@cr2@"))
				s = s.substring(5);
			if (s != null && s.startsWith("@cr3@"))
				s = s.substring(5);
			if (s != null && s.startsWith("@cr4@"))
				s = s.substring(5);
			if (s != null && s.startsWith("@cr5@"))
				s = s.substring(5);
			if (s != null && s.startsWith("@cr6@"))
				s = s.substring(5);
			if (s != null && s.startsWith("@cr7@"))
				s = s.substring(5);
			if (s != null && s.startsWith("@cr8@"))
				s = s.substring(5);
			if (s != null && s.startsWith("@cr9@"))
				s = s.substring(5);
			if (s != null && s.startsWith("@cr10@"))
				s = s.substring(6);
			if (s != null && s.startsWith("@cr11@"))
				s = s.substring(6);
			if (s != null && s.startsWith("@cr12@"))
				s = s.substring(6);
			if (s != null && s.startsWith("@cr13@"))
				s = s.substring(6);
			if (s != null && s.startsWith("@cr14@"))
				s = s.substring(6);
			if (s != null && s.startsWith("@cr15@"))
				s = s.substring(6);
			if (s != null && s.startsWith("@cr16@"))
				s = s.substring(6);
			if (s != null && s.startsWith("@cr17@"))
				s = s.substring(6);
			if (s != null && s.startsWith("@cr22@"))
				s = s.substring(6);
			if ((j1 == 5 || j1 == 6) && (!splitPrivateChat || chatTypeView == 2)
					&& (j1 == 6 || privateChatMode == 0 || privateChatMode == 1 && isFriendOrSelf(s)))
				l++;
			if ((j1 == 3 || j1 == 7) && (!splitPrivateChat || chatTypeView == 2)
					&& (j1 == 7 || privateChatMode == 0 || privateChatMode == 1 && isFriendOrSelf(s))) {
				if (j > k1 - 14 && j <= k1) {
					if (myPlayer.getRights() >= 1) {
						menuActionName[menuActionRow] = "Report abuse @whi@" + s;
						menuActionID[menuActionRow] = 606;
						menuActionRow++;
					}
					menuActionName[menuActionRow] = "Add ignore @whi@" + s;
					menuActionID[menuActionRow] = 42;
					menuActionRow++;
					menuActionName[menuActionRow] = "Reply to @whi@" + s;
					menuActionID[menuActionRow] = 639;
					menuActionRow++;
					menuActionName[menuActionRow] = "Add friend @whi@" + s;
					menuActionID[menuActionRow] = 337;
					menuActionRow++;
				}
				l++;
			}
		}
	}

	private void buildDuelorTrade(int j) {
		int l = 0;
		for (int i1 = 0; i1 < 500; i1++) {
			if (chatMessages[i1] == null)
				continue;
			if (chatTypeView != 3 && chatTypeView != 4)
				continue;
			int j1 = chatTypes[i1];
			String s = chatNames[i1];
			int k1 = (70 - l * 14 + 42) + anInt1089 + 4 + 5;
			if (k1 < -23)
				break;
			if (s != null && s.startsWith("@cr1@"))
				s = s.substring(5);
			if (s != null && s.startsWith("@cr2@"))
				s = s.substring(5);
			if (s != null && s.startsWith("@cr3@"))
				s = s.substring(5);
			if (s != null && s.startsWith("@cr4@"))
				s = s.substring(5);
			if (s != null && s.startsWith("@cr5@"))
				s = s.substring(5);
			if (s != null && s.startsWith("@cr6@"))
				s = s.substring(5);
			if (s != null && s.startsWith("@cr7@"))
				s = s.substring(5);
			if (s != null && s.startsWith("@cr8@"))
				s = s.substring(5);
			if (s != null && s.startsWith("@cr9@"))
				s = s.substring(5);
			if (s != null && s.startsWith("@cr10@"))
				s = s.substring(6);
			if (s != null && s.startsWith("@cr11@"))
				s = s.substring(6);
			if (s != null && s.startsWith("@cr12@"))
				s = s.substring(6);
			if (s != null && s.startsWith("@cr13@"))
				s = s.substring(6);
			if (s != null && s.startsWith("@cr14@"))
				s = s.substring(6);
			if (s != null && s.startsWith("@cr15@"))
				s = s.substring(6);
			if (s != null && s.startsWith("@cr16@"))
				s = s.substring(6);
			if (s != null && s.startsWith("@cr17@"))
				s = s.substring(6);
			if (s != null && s.startsWith("@cr22@"))
				s = s.substring(6);
			if (chatTypeView == 3 && j1 == 4 && (tradeMode == 0 || tradeMode == 1 && isFriendOrSelf(s))) {
				if (j > k1 - 14 && j <= k1) {
					menuActionName[menuActionRow] = "Accept trade @whi@" + s;
					menuActionID[menuActionRow] = 484;
					menuActionRow++;
				}
				l++;
			}
			if (chatTypeView == 4 && j1 == 8 && (tradeMode == 0 || tradeMode == 1 && isFriendOrSelf(s))) {
				if (j > k1 - 14 && j <= k1) {
					menuActionName[menuActionRow] = "Accept challenge @whi@" + s;
					menuActionID[menuActionRow] = 6;
					menuActionRow++;
				}
				l++;
			}
			if (j1 == 12) {
				if (j > k1 - 14 && j <= k1) {
					menuActionName[menuActionRow] = "Go-to @blu@" + s;
					menuActionID[menuActionRow] = 915;
					menuActionRow++;
				}
				l++;
			}
		}
	}

	private void buildChatAreaMenu(int j) {
		int l = 0;
		for (int i1 = 0; i1 < 500; i1++) {
			if (chatMessages[i1] == null)
				continue;
			int j1 = chatTypes[i1];
			int k1 = (70 - l * 14 + 42) + anInt1089 + 4 + 5;
			if (k1 < -23)
				break;
			String s = chatNames[i1];
			if (chatTypeView == 1) {
				buildPublicChat(j);
				break;
			}
			if (chatTypeView == 2) {
				buildFriendChat(j);
				break;
			}
			if (chatTypeView == 3 || chatTypeView == 4) {
				buildDuelorTrade(j);
				break;
			}
			if (chatTypeView == 5) {
				break;
			}
			if (s.startsWith("@cr")) {
				String s2 = s.substring(3, s.length());
				int index = s2.indexOf("@");
				if (index != -1) {
					s2 = s2.substring(0, index);
					s = s.substring(4 + s2.length());
				}
			}
			if (s != null && s.startsWith("<col=") && s.indexOf("</col>") != -1) {
				s = s.substring(s.indexOf("</col>") + 6);
			}
			if (j1 == 0)
				l++;
			if ((j1 == 1 || j1 == 2) && (j1 == 1 || publicChatMode == 0 || publicChatMode == 1 && isFriendOrSelf(s))) {
				if (j > k1 - 14 && j <= k1 && !s.equals(myPlayer.name)) {
					if (myPlayer.getRights() >= 1) {
						menuActionName[menuActionRow] = "Report abuse @whi@" + s;
						menuActionID[menuActionRow] = 606;
						menuActionRow++;
					}
					menuActionName[menuActionRow] = "Add ignore @whi@" + s;
					menuActionID[menuActionRow] = 42;
					menuActionRow++;
					menuActionName[menuActionRow] = "Reply to @whi@" + s;
					menuActionID[menuActionRow] = 639;
					menuActionRow++;
					menuActionName[menuActionRow] = "Add friend @whi@" + s;
					menuActionID[menuActionRow] = 337;
					menuActionRow++;
				}
				l++;
			}
			if ((j1 == 3 || j1 == 7) && !splitPrivateChat
					&& (j1 == 7 || privateChatMode == 0 || privateChatMode == 1 && isFriendOrSelf(s))) {
				if (j > k1 - 14 && j <= k1) {
					if (myPlayer.getRights() >= 1) {
						menuActionName[menuActionRow] = "Report abuse @whi@" + s;
						menuActionID[menuActionRow] = 606;
						menuActionRow++;
					}
					menuActionName[menuActionRow] = "Add ignore @whi@" + s;
					menuActionID[menuActionRow] = 42;
					menuActionRow++;
					menuActionName[menuActionRow] = "Reply to @whi@" + s;
					menuActionID[menuActionRow] = 639;
					menuActionRow++;
					menuActionName[menuActionRow] = "Add friend @whi@" + s;
					menuActionID[menuActionRow] = 337;
					menuActionRow++;
				}
				l++;
			}
			if (j1 == 4 && (tradeMode == 0 || tradeMode == 1 && isFriendOrSelf(s))) {
				if (j > k1 - 14 && j <= k1) {
					menuActionName[menuActionRow] = "Accept trade @whi@" + s;
					menuActionID[menuActionRow] = 484;
					menuActionRow++;
				}
				l++;
			}
			if ((j1 == 5 || j1 == 6) && !splitPrivateChat && privateChatMode < 2)
				l++;
			if (j1 == 8 && (tradeMode == 0 || tradeMode == 1 && isFriendOrSelf(s))) {
				if (j > k1 - 14 && j <= k1) {
					menuActionName[menuActionRow] = "Accept challenge @whi@" + s;
					menuActionID[menuActionRow] = 6;
					menuActionRow++;
				}
				l++;
			}
		}
	}

	public void drawFriendsListOrWelcomeScreen(RSInterface class9) {
		int j = class9.contentType;
		if (j >= 1 && j <= 100 || j >= 701 && j <= 800) {
			if (j == 1 && anInt900 == 0) {
				class9.message = "Loading friend list";
				class9.atActionType = 0;
				return;
			}
			if (j == 1 && anInt900 == 1) {
				class9.message = "Connecting to friendserver";
				class9.atActionType = 0;
				return;
			}
			if (j == 2 && anInt900 != 2) {
				class9.message = "Please wait...";
				class9.atActionType = 0;
				return;
			}
			int k = friendsCount;
			if (anInt900 != 2)
				k = 0;
			if (j > 700)
				j -= 601;
			else
				j--;
			if (j >= k) {
				class9.message = "";
				class9.atActionType = 0;
				return;
			} else {
				class9.message = friendsList[j];
				class9.atActionType = 1;
				return;
			}
		}
		if (j >= 101 && j <= 200 || j >= 801 && j <= 900) {
			int l = friendsCount;
			if (anInt900 != 2)
				l = 0;
			if (j > 800)
				j -= 701;
			else
				j -= 101;
			if (j >= l) {
				class9.message = "";
				class9.atActionType = 0;
				return;
			}
			if (friendsNodeIDs[j] == 0)
				class9.message = "@red@Offline";
			else if (friendsNodeIDs[j] == nodeID)
				class9.message = "@gre@Online";
			else
				class9.message = "@red@Offline";
			class9.atActionType = 1;
			return;
		}
		if (j == 203) {
			int i1 = friendsCount;
			if (anInt900 != 2)
				i1 = 0;
			class9.scrollMax = i1 * 15 + 20;
			if (class9.scrollMax <= class9.height)
				class9.scrollMax = class9.height + 1;
			return;
		}
		if (j >= 401 && j <= 500) {
			if ((j -= 401) == 0 && anInt900 == 0) {
				class9.message = "Loading ignore list";
				class9.atActionType = 0;
				return;
			}
			if (j == 1 && anInt900 == 0) {
				class9.message = "Please wait...";
				class9.atActionType = 0;
				return;
			}
			int j1 = ignoreCount;
			if (anInt900 == 0)
				j1 = 0;
			if (j >= j1) {
				class9.message = "";
				class9.atActionType = 0;
				return;
			} else {
				class9.message = TextClass.fixName(TextClass.nameForLong(ignoreListAsLongs[j]));
				class9.atActionType = 1;
				return;
			}
		}
		if (j == 503) {
			class9.scrollMax = ignoreCount * 15 + 20;
			if (class9.scrollMax <= class9.height)
				class9.scrollMax = class9.height + 1;
			return;
		}
		if (j == 327) {
			class9.modelRotation1 = 150;
			class9.modelRotation2 = (int) (Math.sin(loopCycle / 40D) * 256D) & 0x7ff;
			if (aBoolean1031) {
				for (int k1 = 0; k1 < 7; k1++) {
					int l1 = anIntArray1065[k1];
					if (l1 >= 0 && !IDK.cache[l1].method537())
						return;
				}

				aBoolean1031 = false;
				Model aclass30_sub2_sub4_sub6s[] = new Model[7];
				int i2 = 0;
				for (int j2 = 0; j2 < 7; j2++) {
					int k2 = anIntArray1065[j2];
					if (k2 >= 0)
						aclass30_sub2_sub4_sub6s[i2++] = IDK.cache[k2].method538();
				}

				Model model = new Model(i2, aclass30_sub2_sub4_sub6s);
				for (int l2 = 0; l2 < 5; l2++)
					if (anIntArray990[l2] != 0) {
						model.replaceColor(anIntArrayArray1003[l2][0], anIntArrayArray1003[l2][anIntArray990[l2]]);
						if (l2 == 1)
							model.replaceColor(anIntArray1204[0], anIntArray1204[anIntArray990[l2]]);
						// if(l2 == 1)
						// model.method476(Legs2[0], Legs2[anIntArray990[l2]]);
					}

				model.method469();
				model.method470(AnimationDefinition.anims[myPlayer.anInt1511].anIntArray353[0]);
				model.method479(64, 1300, 0, -570, 0, true);
				class9.anInt233 = 5;
				class9.mediaID = 0;
				RSInterface.method208(aBoolean994, model);
			}
			return;
		}
		if (j == 328) {
			RSInterface rsInterface = class9;
			int verticleTilt = 150;
			int animationSpeed = (int) (Math.sin(loopCycle / 40D) * 256D) & 0x7ff;
			rsInterface.modelRotation1 = verticleTilt;
			rsInterface.modelRotation2 = animationSpeed;
			if (aBoolean1031) {
				Model characterDisplay = myPlayer.method452();
				for (int l2 = 0; l2 < 5; l2++)
					if (anIntArray990[l2] != 0) {
						characterDisplay.replaceColor(anIntArrayArray1003[l2][0],
								anIntArrayArray1003[l2][anIntArray990[l2]]);
						if (l2 == 1)
							characterDisplay.replaceColor(anIntArray1204[0], anIntArray1204[anIntArray990[l2]]);
					}
				int staticFrame = myPlayer.anInt1511;
				characterDisplay.method469();
				characterDisplay.method470(AnimationDefinition.anims[staticFrame].anIntArray353[0]);
				// characterDisplay.method479(64, 850, -30, -50, -30, true);
				rsInterface.anInt233 = 5;
				rsInterface.mediaID = 0;
				RSInterface.method208(aBoolean994, characterDisplay);
			}
			return;
		}
		if (j == 324) {
			if (aClass30_Sub2_Sub1_Sub1_931 == null) {
				aClass30_Sub2_Sub1_Sub1_931 = class9.sprite1;
				aClass30_Sub2_Sub1_Sub1_932 = class9.sprite2;
			}
			if (aBoolean1047) {
				class9.sprite1 = aClass30_Sub2_Sub1_Sub1_932;
				return;
			} else {
				class9.sprite1 = aClass30_Sub2_Sub1_Sub1_931;
				return;
			}
		}
		if (j == 325) {
			if (aClass30_Sub2_Sub1_Sub1_931 == null) {
				aClass30_Sub2_Sub1_Sub1_931 = class9.sprite1;
				aClass30_Sub2_Sub1_Sub1_932 = class9.sprite2;
			}
			if (aBoolean1047) {
				class9.sprite1 = aClass30_Sub2_Sub1_Sub1_931;
				return;
			} else {
				class9.sprite1 = aClass30_Sub2_Sub1_Sub1_932;
				return;
			}
		}
		if (j == 600) {
			class9.message = reportAbuseInput;
			if (loopCycle % 20 < 10) {
				class9.message += "|";
				return;
			} else {
				class9.message += " ";
				return;
			}
		}
		if (j == 613)
			if (myPlayer.getRights() >= 1) {
				if (canMute) {
					class9.textColor = 0xff0000;
					// class9.message =
					// "Moderator option: Mute player for 48 hours: <ON>";
				} else {
					class9.textColor = 0xffffff;
					// class9.message =
					// "Moderator option: Mute player for 48 hours: <OFF>";
				}
			} else {
				class9.message = "";
			}
		if (j == 650 || j == 655)
			if (anInt1193 != 0) {
				String s;
				if (daysSinceLastLogin == 0)
					s = "earlier today";
				else if (daysSinceLastLogin == 1)
					s = "yesterday";
				else
					s = daysSinceLastLogin + " days ago";
				class9.message = "You last logged in " + s + " from: " + Signlink.dns;
			} else {
				class9.message = "";
			}
		if (j == 651) {
			if (unreadMessages == 0) {
				class9.message = "0 unread messages";
				class9.textColor = 0xffff00;
			}
			if (unreadMessages == 1) {
				class9.message = "1 unread message";
				class9.textColor = 65280;
			}
			if (unreadMessages > 1) {
				class9.message = unreadMessages + " unread messages";
				class9.textColor = 65280;
			}
		}
		if (j == 652)
			if (daysSinceRecovChange == 201) {
				if (membersInt == 1)
					class9.message = "@yel@This is a non-members world: @whi@Since you are a member we";
				else
					class9.message = "";
			} else if (daysSinceRecovChange == 200) {
				class9.message = "You have not yet set any password recovery questions.";
			} else {
				String s1;
				if (daysSinceRecovChange == 0)
					s1 = "Earlier today";
				else if (daysSinceRecovChange == 1)
					s1 = "Yesterday";
				else
					s1 = daysSinceRecovChange + " days ago";
				class9.message = s1 + " you changed your recovery questions";
			}
		if (j == 653)
			if (daysSinceRecovChange == 201) {
				if (membersInt == 1)
					class9.message = "@whi@recommend you use a members world instead. You may use";
				else
					class9.message = "";
			} else if (daysSinceRecovChange == 200)
				class9.message = "We strongly recommend you do so now to secure your account.";
			else
				class9.message = "If you do not remember making this change then cancel it immediately";
		if (j == 654) {
			if (daysSinceRecovChange == 201)
				if (membersInt == 1) {
					class9.message = "@whi@this world but member benefits are unavailable whilst here.";
					return;
				} else {
					class9.message = "";
					return;
				}
			if (daysSinceRecovChange == 200) {
				class9.message = "Do this from the 'account management' area on our front webpage";
				return;
			}
			class9.message = "Do this from the 'account management' area on our front webpage";
		}
	}

	private void drawSplitPrivateChat() {
		if (!splitPrivateChat)
			return;
		RSFont font = newRegularFont;
		;
		int i = 0;
		if (anInt1104 != 0)
			i = 1;
		int xPosition = 0;
		int yPosition = 0;
		for (int j = 0; j < 100; j++) {
			if (chatMessages[j] != null) {
				int k = chatTypes[j];
				String s = chatNames[j];
				byte byte1 = 0;
				if (s.startsWith("@cr")) {
					String s2 = s.substring(3, s.length());
					int index = s2.indexOf("@");
					if (index != -1) {
						s2 = s2.substring(0, index);
						byte1 = Byte.parseByte(s2);
						s = s.substring(4 + s2.length());
					}
				}
				if ((k == 3 || k == 7)
						&& (k == 7 || privateChatMode == 0 || privateChatMode == 1 && isFriendOrSelf(s))) {
					yPosition = (currentScreenMode == ScreenMode.FIXED ? 330 : currentGameHeight - 173) - i * 13;
					xPosition = 0;
					font.drawBasicString("From", xPosition, yPosition, 65535, 0);
					xPosition += font.getTextWidth("From ");
					if (byte1 > 0) {
						for (int right = 0; right < modIcons.length; right++) {
							if (right == (byte1 - 1) && modIcons[right] != null) {
								modIcons[right].drawAdvancedSprite(xPosition, yPosition - modIcons[right].myHeight);
								xPosition += modIcons[right].myWidth;
								break;
							}
						}
					}
					font.drawBasicString(s + ": " + chatMessages[j], xPosition, yPosition, 65535, 0);
					if (++i >= 5)
						return;
				}
				if (k == 5 && privateChatMode < 2) {
					yPosition = (currentScreenMode == ScreenMode.FIXED ? 330 : currentGameHeight - 173) - i * 13;
					xPosition = 0;
					font.drawBasicString(chatMessages[j], xPosition, yPosition, 65535, 0);
					if (++i >= 5)
						return;
				}
				if (k == 6 && privateChatMode < 2) {
					yPosition = (currentScreenMode == ScreenMode.FIXED ? 330 : currentGameHeight - 173) - i * 13;
					xPosition = 0;
					font.drawBasicString("To " + s + ": " + chatMessages[j], xPosition, yPosition, 65535, 0);
					if (++i >= 5)
						return;
				}
			}
		}
	}

	private void buildSplitPrivateChatMenu() {
		if (!splitPrivateChat)
			return;
		int i = 0;
		if (anInt1104 != 0)
			i = 1;
		for (int j = 0; j < 100; j++) {
			if (chatMessages[j] != null) {
				int k = chatTypes[j];
				String s = chatNames[j];
				if (s.startsWith("@cr")) {
					String s2 = s.substring(3, s.length());
					int index = s2.indexOf("@");
					if (index != -1) {
						s2 = s2.substring(0, index);
						s = s.substring(4 + s2.length());
					}
				}
				if ((k == 3 || k == 7)
						&& (k == 7 || privateChatMode == 0 || privateChatMode == 1 && isFriendOrSelf(s))) {
					int yPosition = (currentScreenMode == ScreenMode.FIXED ? 330 : currentGameHeight - 173) - i * 13;
					int messageLength = newRegularFont.getTextWidth("From:  " + s + chatMessages[j]) + 25;
					if (super.mouseX >= 0 && super.mouseX <= messageLength) {
						if (super.mouseY >= yPosition - 10 && mouseY <= yPosition + 3) {
							if (messageLength > 450)
								messageLength = 450;
							if (myPlayer.getRights() >= 1) {
								menuActionName[menuActionRow] = "Report abuse @whi@" + s;
								menuActionID[menuActionRow] = 2606;
								menuActionRow++;
							}
							menuActionName[menuActionRow] = "Add ignore @whi@" + s;
							menuActionID[menuActionRow] = 2042;
							menuActionRow++;
							menuActionName[menuActionRow] = "Reply to @whi@" + s;
							menuActionID[menuActionRow] = 2639;
							menuActionRow++;
							menuActionName[menuActionRow] = "Add friend @whi@" + s;
							menuActionID[menuActionRow] = 2337;
							menuActionRow++;
						}
					}
					if (++i >= 5)
						return;
				}
				if ((k == 5 || k == 6) && privateChatMode < 2 && ++i >= 5)
					return;
			}
		}
	}

	public void pushMessage(String s, int i, String s1) {
		if (i == 0 && dialogID != -1) {
			aString844 = s;
			super.clickMode3 = 0;
		}
		if (backDialogID == -1)
			inputTaken = true;
		for (int j = 499; j > 0; j--) {
			chatTypes[j] = chatTypes[j - 1];
			chatNames[j] = chatNames[j - 1];
			chatMessages[j] = chatMessages[j - 1];
			chatRights[j] = chatRights[j - 1];
			clanTitles[j] = clanTitles[j - 1];
		}
		chatTypes[0] = i;
		chatNames[0] = s1;
		chatMessages[0] = s;
		chatRights[0] = channelRights;
		clanTitles[0] = clanTitle;
	}

	public void setNorth() {
		cameraOffsetX = 0;
		cameraOffsetY = 0;
		viewRotationOffset = 0;
		viewRotation = 0;
		minimapRotation = 0;
		minimapZoom = 0;
	}

	public void processTabClick() {
		if (clickMode3 == 1) {
			if (currentScreenMode == ScreenMode.FIXED) {
				int x = 519;
				int y = 168;
				int[] points = new int[] { 3, 41, 74, 107, 140, 173, 206, 244 };
				for (int index = 0; index < points.length - 1; index++) {
					int tabIndex = index < points.length ? points.length : points.length * 2;
					if (Client.tabInterfaceIDs[tabIndex] != -1) {
						if (super.saveClickX >= x + points[index] && super.saveClickX <= x + points[index + 1]) {
							if (super.saveClickY >= y && super.saveClickY <= y + 36) {
								Client.tabID = index;
							} else if (super.saveClickY >= y + 298 && super.saveClickY <= y + 36 + 298) {
								Client.tabID = index + 7;
							}
							Client.needDrawTabArea = true;
							Client.tabAreaAltered = true;
						}
					}
				}
			} else {
				int x = Client.currentGameWidth - (stackTabs() ? 231 : 462);
				int y = Client.currentGameHeight - (stackTabs() ? 73 : 37);
				for (int index = 0; index < 14; index++) {
					if (Client.tabInterfaceIDs[index] != -1) {
						if (super.saveClickX >= x && super.saveClickX <= x + 33) {
							if (super.saveClickY >= y && super.saveClickY <= y + 36) {
								Client.tabID = index;
							} else if (stackTabs() && super.saveClickY >= y + 36 && super.saveClickY <= y + 36 + 36) {
								Client.tabID = index + 7;
							}
							Client.needDrawTabArea = true;
							Client.tabAreaAltered = true;
						}
					}
					x += 33;
				}
			}
		}
	}

	private void resetImageProducers2() {
		if (chatAreaGraphicsBuffer != null)
			return;
		nullLoader();
		loginScreenGraphicsBuffer = null;
		fullGameScreen = null;
		leftSideFlame = null;
		rightSideFlame = null;
		chatAreaGraphicsBuffer = new RSImageProducer(519, 165, getGameComponent());
		mapAreaGraphicsBuffer = new RSImageProducer(246, 168, getGameComponent());
		DrawingArea.setAllPixelsToZero();
		tabAreaGraphicsBuffer = new RSImageProducer(246, 335, getGameComponent());
		mainGameGraphicsBuffer = new RSImageProducer(
				currentScreenMode == ScreenMode.FIXED ? 519 : currentScreenMode.getWidth(),
				currentScreenMode == ScreenMode.FIXED ? 338 : currentScreenMode.getHeight(), getGameComponent());
		DrawingArea.setAllPixelsToZero();
		welcomeScreenRaised = true;
	}

	public String getDocumentBaseHost() {
		if (Signlink.mainapp != null) {
			return Signlink.mainapp.getDocumentBase().getHost().toLowerCase();
		}
		return "";
	}

	private void method81(Sprite sprite, int j, int k) {
		int l = k * k + j * j;
		if (l > 4225 && l < 0x15f90) {
			int i1 = viewRotation + minimapRotation & 0x7ff;
			int j1 = Model.modelIntArray1[i1];
			int k1 = Model.modelIntArray2[i1];
			j1 = (j1 * 256) / (minimapZoom + 256);
			k1 = (k1 * 256) / (minimapZoom + 256);
			int l1 = j * j1 + k * k1 >> 16;
			int i2 = j * k1 - k * j1 >> 16;
			double d = Math.atan2(l1, i2);
			int j2 = (int) (Math.sin(d) * 63D);
			int k2 = (int) (Math.cos(d) * 57D);
			mapEdge.method353(83 - k2 - 20, d, (94 + j2 + 4) - 10);
		} else {
			markMinimap(sprite, k, j);
		}
	}

	private void rightClickChatButtons() {
		if (super.mouseY >= (currentScreenMode == ScreenMode.FIXED ? 482 : currentGameHeight - 22)
				&& super.mouseY <= (currentScreenMode == ScreenMode.FIXED ? 503 : currentGameHeight)) {

			if (super.mouseX >= 5 && super.mouseX <= 61) {
				menuActionName[1] = "View All";
				menuActionID[1] = 999;
				menuActionRow = 2;
			} else if (super.mouseX >= 71 && super.mouseX <= 127) {
				menuActionName[1] = "View Game";
				menuActionID[1] = 998;
				menuActionName[2] = "View Market";
				menuActionID[2] = 1005;
				menuActionRow = 3;
			} else if (super.mouseX >= 137 && super.mouseX <= 193) {
				menuActionName[1] = "Hide public";
				menuActionID[1] = 997;
				menuActionName[2] = "Off public";
				menuActionID[2] = 996;
				menuActionName[3] = "Friends public";
				menuActionID[3] = 995;
				menuActionName[4] = "On public";
				menuActionID[4] = 994;
				menuActionName[5] = "View public";
				menuActionID[5] = 993;
				menuActionRow = 6;
			} else if (super.mouseX >= 203 && super.mouseX <= 259) {
				menuActionName[1] = "Off private";
				menuActionID[1] = 992;
				menuActionName[2] = "Friends private";
				menuActionID[2] = 991;
				menuActionName[3] = "On private";
				menuActionID[3] = 990;
				menuActionName[4] = "View private";
				menuActionID[4] = 989;
				menuActionRow = 5;
			} else if (super.mouseX >= 269 && super.mouseX <= 325) {
				menuActionName[1] = "Off clan chat";
				menuActionID[1] = 1003;
				menuActionName[2] = "Friends clan chat";
				menuActionID[2] = 1002;
				menuActionName[3] = "On clan chat";
				menuActionID[3] = 1001;
				menuActionName[4] = "View clan chat";
				menuActionID[4] = 1000;
				menuActionRow = 5;
			} else if (super.mouseX >= 335 && super.mouseX <= 391) {
				menuActionName[1] = "Off trade";
				menuActionID[1] = 987;
				menuActionName[2] = "Friends trade";
				menuActionID[2] = 986;
				menuActionName[3] = "On trade";
				menuActionID[3] = 985;
				menuActionName[4] = "View trade";
				menuActionID[4] = 984;
				menuActionRow = 5;
			}
		}
	}

	private boolean checkMainScreenBounds() {
		if (checkBounds(0, currentGameWidth - (stackTabs() ? 231 : 462), currentGameHeight - (stackTabs() ? 73 : 37),
				currentGameWidth, currentGameHeight)) {
			return false;
		}
		if (checkBounds(0, currentGameWidth - 225, 0, currentGameWidth, 170)) {
			return false;
		}
		if (checkBounds(0, currentGameWidth - 204, currentGameHeight - (stackTabs() ? 73 : 37) - 275, currentGameWidth,
				currentGameHeight)) {
			return false;
		}
		if (checkBounds(0, 0, currentGameHeight - 159, 519, currentGameHeight)) {
			return false;
		}
		return true;
	}

	private boolean checkBounds(int type, int x1, int y1, int x2, int y2) {
		if (type == 0)
			return mouseX >= x1 && mouseX <= x2 && mouseY >= y1 && mouseY <= y2;
		else
			return Math.pow((x1 + type - x2), 2) + Math.pow((y1 + type - y2), 2) < Math.pow(type, 2);
	}

	private boolean stackTabs() {
		return !(currentGameWidth >= 1100);
	}

	static boolean centerMainScreenInterface() {
		return (currentGameWidth <= 900 || currentGameHeight <= 650);
	}

	private void processRightClick() {
		if (loggedIn) {
			if (activeInterfaceType != 0)
				return;
			menuActionName[0] = "Cancel";
			menuActionID[0] = 1107;
			menuActionRow = 1;
			if (currentScreenMode != ScreenMode.FIXED) {
				if (fullscreenInterfaceID != -1) {
					anInt886 = 0;
					anInt1315 = 0;
					buildInterfaceMenu((currentGameWidth / 2) - 765 / 2,
							RSInterface.interfaceCache[fullscreenInterfaceID], super.mouseX,
							(currentGameHeight / 2) - 503 / 2, super.mouseY, 0);
					if (anInt886 != anInt1026) {
						anInt1026 = anInt886;
					}
					if (anInt1315 != anInt1129) {
						anInt1129 = anInt1315;
					}
					return;
				}
			}
			buildSplitPrivateChatMenu();
			anInt886 = 0;
			anInt1315 = 0;

			if (currentScreenMode == ScreenMode.FIXED) {
				if (mouseX > 0 && mouseY > 0 && mouseX < 519 && mouseY < 338) {
					if (openInterfaceID != -1) {
						buildInterfaceMenu(0, RSInterface.interfaceCache[openInterfaceID], mouseX, 0, mouseY, 0);
					} else {
						build3dScreenMenu();
					}
				}
			} else {
				if (checkMainScreenBounds()) {
					if (openInterfaceID != -1) {
						if (currentGameWidth > 900 && currentGameHeight > 650 && mouseX > (currentGameWidth / 2) - 256
								&& mouseX < ((currentGameWidth / 2) + 256) && mouseY > (currentGameHeight / 2) - 167
								&& mouseY < (currentGameHeight / 2) + 167) {
							buildInterfaceMenu((currentGameWidth / 2) - 256,
									RSInterface.interfaceCache[openInterfaceID], mouseX, (currentGameHeight / 2) - 167,
									mouseY, 0);
						} else if (centerMainScreenInterface()) {
							if (mouseX > 0 && mouseY > 0 && mouseX < 519 && mouseY < 338) {
								buildInterfaceMenu(0, RSInterface.interfaceCache[openInterfaceID], mouseX, 0, mouseY,
										0);
							}
						}
					} else {
						build3dScreenMenu();
					}
				}
			}

			if (anInt1315 != anInt1129) {
				anInt1129 = anInt1315;
			}
			anInt886 = 0;
			anInt1315 = 0;
			if (currentScreenMode == ScreenMode.FIXED) {
				if (mouseX > 516 && mouseY > 205 && mouseX < 765 && mouseY < 466) {
					if (invOverlayInterfaceID != -1) {
						buildInterfaceMenu(547, RSInterface.interfaceCache[invOverlayInterfaceID], mouseX, 205, mouseY,
								0);
					} else if (tabInterfaceIDs[tabID] != -1) {
						buildInterfaceMenu(547, RSInterface.interfaceCache[tabInterfaceIDs[tabID]], mouseX, 205, mouseY,
								0);
					}
				}
			} else {
				int y = stackTabs() ? 73 : 37;
				if (mouseX > currentGameWidth - 197 && mouseY > currentGameHeight - 275 - y + 10
						&& mouseX < currentGameWidth - 7 && mouseY < currentGameHeight - y - 5) {
					if (invOverlayInterfaceID != -1) {
						buildInterfaceMenu(currentGameWidth - 197, RSInterface.interfaceCache[invOverlayInterfaceID],
								mouseX, currentGameHeight - 275 - y + 10, mouseY, 0);
					} else if (tabInterfaceIDs[tabID] != -1) {
						buildInterfaceMenu(currentGameWidth - 197, RSInterface.interfaceCache[tabInterfaceIDs[tabID]],
								mouseX, currentGameHeight - 275 - y + 10, mouseY, 0);
					}
				}
			}
			if (anInt886 != anInt1048) {
				needDrawTabArea = true;
				tabAreaAltered = true;
				anInt1048 = anInt886;
			}
			if (anInt1315 != anInt1044) {
				needDrawTabArea = true;
				tabAreaAltered = true;
				anInt1044 = anInt1315;
			}
			anInt886 = 0;
			anInt1315 = 0;
			/* Chat area clicking */
			if (currentScreenMode == ScreenMode.FIXED) {
				if (mouseX > 0 && mouseY > 338 && mouseX < 490 && mouseY < 463) {
					if (backDialogID != -1)
						buildInterfaceMenu(20, RSInterface.interfaceCache[backDialogID], mouseX, 358, mouseY, 0);
					else if (mouseY < 463 && mouseX < 490)
						buildChatAreaMenu(mouseY - 338);
				}
			} else {
				if (mouseX > 0 && mouseY > currentGameHeight - 165 && mouseX < 490 && mouseY < currentGameHeight - 40) {
					if (backDialogID != -1)
						buildInterfaceMenu(20, RSInterface.interfaceCache[backDialogID], mouseX,
								currentGameHeight - 145, mouseY, 0);
					else if (mouseY < currentGameHeight - 40 && mouseX < 490)
						buildChatAreaMenu(mouseY - (currentGameHeight - 165));
				}
			}
			if (backDialogID != -1 && anInt886 != anInt1039) {
				inputTaken = true;
				anInt1039 = anInt886;
			}
			if (backDialogID != -1 && anInt1315 != anInt1500) {
				inputTaken = true;
				anInt1500 = anInt1315;
			}
			/* Enable custom right click areas */
			if (anInt886 != anInt1026)
				anInt1026 = anInt886;
			anInt886 = 0;

			rightClickChatButtons();
			processMinimapActions();
			boolean flag = false;
			while (!flag) {
				flag = true;
				for (int j = 0; j < menuActionRow - 1; j++) {
					if (menuActionID[j] < 1000 && menuActionID[j + 1] > 1000) {
						String s = menuActionName[j];
						menuActionName[j] = menuActionName[j + 1];
						menuActionName[j + 1] = s;
						int k = menuActionID[j];
						menuActionID[j] = menuActionID[j + 1];
						menuActionID[j + 1] = k;
						k = menuActionCmd2[j];
						menuActionCmd2[j] = menuActionCmd2[j + 1];
						menuActionCmd2[j + 1] = k;
						k = menuActionCmd3[j];
						menuActionCmd3[j] = menuActionCmd3[j + 1];
						menuActionCmd3[j + 1] = k;
						k = menuActionCmd1[j];
						menuActionCmd1[j] = menuActionCmd1[j + 1];
						menuActionCmd1[j + 1] = k;
						flag = false;
					}
				}
			}
		}
	}
	
	private void processMinimapActions() {
		final boolean fixed = currentScreenMode == ScreenMode.FIXED;
		if (fixed ? super.mouseX >= 542 && super.mouseX <= 579 && super.mouseY >= 2 && super.mouseY <= 38 : 
			super.mouseX >= Client.currentGameWidth - 180 && super.mouseX <= Client.currentGameWidth - 139 && super.mouseY >= 0 && super.mouseY <= 40) {
			menuActionName[1] = "Face North";
			menuActionID[1] = 696;
			menuActionRow = 2;
		}
		if (counterHover && drawOrbs) {
			menuActionName[1] = "Reset XP Total";
			menuActionID[1] = 475;
			menuActionName[2] = counterOn ? "Toggle XP Display" : "Show XP Display";
			menuActionID[2] = 474;
			menuActionRow = 3;
		}
		if (worldHover && drawOrbs) {
			menuActionName[1] = "World Map";
			menuActionID[1] = 850;
			menuActionRow = 2;
		}
		if (prayHover && drawOrbs) {
			menuActionName[2] = prayClicked ? "Turn quick-prayers off" : "Turn quick-prayers on";
			menuActionID[2] = 1500;
			menuActionRow = 2;
			menuActionName[1] = "Select quick-prayers";
			menuActionID[1] = 1506;
			menuActionRow = 3;
		}
	}

	private int method83(int i, int j, int k) {
		int l = 256 - k;
		return ((i & 0xff00ff) * l + (j & 0xff00ff) * k & 0xff00ff00)
				+ ((i & 0xff00) * l + (j & 0xff00) * k & 0xff0000) >> 8;
	}

	public void login(String s, String s1, boolean flag) {
		Signlink.errorname = s;
		try {
			if (!flag) {
				drawLoginScreen(true);
			}
			setConfigButton(23103, informationFile.isRememberRoof() ? true : false);
			socketStream = new RSSocket(this, openSocket(port + portOff));
			long l = TextClass.longForName(s);
			int i = (int) (l >> 16 & 31L);
			stream.currentOffset = 0;
			stream.writeWordBigEndian(14);
			stream.writeWordBigEndian(i);
			socketStream.queueBytes(2, stream.buffer);
			for (int j = 0; j < 8; j++)
				socketStream.read();

			int k = socketStream.read();
			int i1 = k;
			if (k == 0) {
				socketStream.flushInputStream(inStream.buffer, 8);
				inStream.currentOffset = 0;
				aLong1215 = inStream.readQWord();
				int ai[] = new int[4];
				ai[0] = (int) (Math.random() * 99999999D);
				ai[1] = (int) (Math.random() * 99999999D);
				ai[2] = (int) (aLong1215 >> 32);
				ai[3] = (int) aLong1215;
				stream.currentOffset = 0;
				stream.writeWordBigEndian(10);
				stream.writeDWord(ai[0]);
				stream.writeDWord(ai[1]);
				stream.writeDWord(ai[2]);
				stream.writeDWord(ai[3]);
				stream.writeDWord(Signlink.uid);
				stream.writeString(s);
				stream.writeString(s1);
				stream.writeString(macAddress);
				stream.doKeys();
				aStream_847.currentOffset = 0;
				if (flag)
					aStream_847.writeWordBigEndian(18);
				else
					aStream_847.writeWordBigEndian(16);
				aStream_847.writeWordBigEndian(stream.currentOffset + 36 + 1 + 1 + 2);
				aStream_847.writeWordBigEndian(255);
				aStream_847.writeWord(317);
				aStream_847.writeWordBigEndian(lowMem ? 1 : 0);
				for (int l1 = 0; l1 < 9; l1++)
					aStream_847.writeDWord(expectedCRCs[l1]);

				aStream_847.writeBytes(stream.buffer, stream.currentOffset, 0);
				stream.encryption = new ISAACRandomGen(ai);
				for (int j2 = 0; j2 < 4; j2++)
					ai[j2] += 50;

				encryption = new ISAACRandomGen(ai);
				socketStream.queueBytes(aStream_847.currentOffset, aStream_847.buffer);
				k = socketStream.read();
			}
			if (k == 1) {
				try {
					Thread.sleep(2000L);
				} catch (Exception _ex) {
				}
				login(s, s1, flag);
				return;
			}
			if (k == 2) {
				@SuppressWarnings("unused")
				int rights = socketStream.read();
				flagged = socketStream.read() == 1;
				aLong1220 = 0L;
				anInt1022 = 0;
				mouseDetection.coordsIndex = 0;
				super.awtFocus = true;
				aBoolean954 = true;
				loggedIn = true;
				stream.currentOffset = 0;
				inStream.currentOffset = 0;
				incomingPacket = -1;
				dealtWithPacket = -1;
				previousPacket1 = -1;
				dealtWithPacketSize = -1;
				previousPacket2 = -1;
				previousPacketSize1 = -1;
				previousPacketSize2 = -1;
				packetSize = 0;
				anInt1009 = 0;
				anInt1104 = 0;
				anInt1011 = 0;
				anInt855 = 0;
				menuActionRow = 0;
				menuOpen = false;
				super.idleTime = 0;
				for (int j1 = 0; j1 < 500; j1++)
					chatMessages[j1] = null;

				itemSelected = 0;
				spellSelected = 0;
				loadingStage = 0;
				anInt1062 = 0;
				setNorth();
				updateGameScreen();
				minimapState = 0;
				anInt985 = -1;
				destX = 0;
				destY = 0;
				playerCount = 0;
				npcCount = 0;
				for (int i2 = 0; i2 < maxPlayers; i2++) {
					playerArray[i2] = null;
					aStreamArray895s[i2] = null;
				}

				for (int k2 = 0; k2 < 16384; k2++)
					npcArray[k2] = null;

				myPlayer = playerArray[myPlayerIndex] = new Player();
				aClass19_1013.removeAll();
				aClass19_1056.removeAll();
				for (int l2 = 0; l2 < 4; l2++) {
					for (int i3 = 0; i3 < 104; i3++) {
						for (int k3 = 0; k3 < 104; k3++)
							groundArray[l2][i3][k3] = null;

					}

				}

				aClass19_1179 = new NodeList();
				fullscreenInterfaceID = -1;
				anInt900 = 0;
				friendsCount = 0;
				dialogID = -1;
				backDialogID = -1;
				openInterfaceID = -1;
				invOverlayInterfaceID = -1;
				openWalkableWidgetID = -1;
				aBoolean1149 = false;
				tabID = 3;
				inputDialogState = 0;
				menuOpen = false;
				messagePromptRaised = false;
				aString844 = null;
				anInt1055 = 0;
				anInt1054 = -1;
				aBoolean1047 = true;
				method45();
				for (int j3 = 0; j3 < 5; j3++)
					anIntArray990[j3] = 0;

				for (int l3 = 0; l3 < 6; l3++) {
					atPlayerActions[l3] = null;
					atPlayerArray[l3] = false;
				}

				anInt1175 = 0;
				anInt1134 = 0;
				anInt986 = 0;
				anInt1288 = 0;
				anInt924 = 0;
				anInt1188 = 0;
				anInt1155 = 0;
				anInt1226 = 0;
				resetImageProducers2();
				informationFile.write();
				return;
			}
			if (k == 3) {
				fadingScreen = new FadingScreenImage(newBoldFont, "Invalid username or password.", (byte) -1, (byte) 5,
						765 / 2 - 125, 350, 250);
				return;
			}
			if (k == 4) {
				fadingScreen = new FadingScreenImage(newBoldFont,
						"Your account has been disabled. Please contact a staff-member and appeal on the forum.", (byte) -1,
						(byte) 5, 765 / 2 - 125, 350, 250);
				return;
			}
			if (k == 5) {
				fadingScreen = new FadingScreenImage(newBoldFont,
						"Your account is already logged in. Try again in 60 seconds...", (byte) -1, (byte) 5,
						765 / 2 - 125, 350, 250);
				return;
			}
			if (k == 6) {
				fadingScreen = new FadingScreenImage(newBoldFont,
						"OS-Veldahar has been updated! Please reload this page.", (byte) -1, (byte) 5, 765 / 2 - 125, 350,
						250);
				return;
			}
			if (k == 7) {
				fadingScreen = new FadingScreenImage(newBoldFont, "This world is full. Please use a different world.",
						(byte) -1, (byte) 5, 765 / 2 - 125, 350, 250);
				return;
			}
			if (k == 8) {
				fadingScreen = new FadingScreenImage(newBoldFont, "Unable to connect. Login server offline.", (byte) -1,
						(byte) 5, 765 / 2 - 125, 350, 250);
				return;
			}
			if (k == 9) {
				fadingScreen = new FadingScreenImage(newBoldFont,
						"Login limit exceeded. Too many connections from your address.", (byte) -1, (byte) 5,
						765 / 2 - 125, 350, 250);
				return;
			}
			if (k == 10) {
				fadingScreen = new FadingScreenImage(newBoldFont, "Unable to connect. Bad session id.", (byte) -1,
						(byte) 5, 765 / 2 - 125, 350, 250);
				return;
			}
			if (k == 11) {
				fadingScreen = new FadingScreenImage(newBoldFont, "Login server rejected session. Please try again.",
						(byte) -1, (byte) 5, 765 / 2 - 125, 350, 250);
				return;
			}
			if (k == 12) {
				fadingScreen = new FadingScreenImage(newBoldFont,
						"You need a members account to login to this world. Please subscribe, or use a different world.",
						(byte) -1, (byte) 5, 765 / 2 - 125, 350, 250);
				return;
			}
			if (k == 13) {
				fadingScreen = new FadingScreenImage(newBoldFont,
						"Could not complete login. Please try using a different world.", (byte) -1, (byte) 5,
						765 / 2 - 125, 350, 250);
				return;
			}
			if (k == 14) {
				fadingScreen = new FadingScreenImage(newBoldFont,
						"The server is being updated. Please wait 1 minute and try again.", (byte) -1, (byte) 5,
						765 / 2 - 125, 350, 250);
				return;
			}
			if (k == 15) {
				loggedIn = true;
				stream.currentOffset = 0;
				inStream.currentOffset = 0;
				incomingPacket = -1;
				dealtWithPacket = -1;
				previousPacket1 = -1;
				previousPacket2 = -1;
				packetSize = 0;
				anInt1009 = 0;
				anInt1104 = 0;
				menuActionRow = 0;
				menuOpen = false;
				aLong824 = System.currentTimeMillis();
				return;
			}
			if (k == 16) {
				fadingScreen = new FadingScreenImage(newBoldFont,
						"Login attempts exceeded. Please wait 1 minute and try again.", (byte) -1, (byte) 5,
						765 / 2 - 125, 350, 250);
				return;
			}
			if (k == 17) {
				fadingScreen = new FadingScreenImage(newBoldFont,
						"You are standing in a members-only area. To play on this world move to a free area first.",
						(byte) -1, (byte) 5, 765 / 2 - 125, 350, 250);
				return;
			}
			if (k == 20) {
				fadingScreen = new FadingScreenImage(newBoldFont,
						"Invalid loginserver requested. Please try using a different world.", (byte) -1, (byte) 5,
						765 / 2 - 125, 350, 250);
				return;
			}
			if (k == 21) {
				for (int k1 = socketStream.read(); k1 >= 0; k1--) {
					fadingScreen = new FadingScreenImage(newBoldFont,
							"You have only just left another world. Your profile will be transferred in: " + k1
									+ " seconds",
							(byte) -1, (byte) 5, 765 / 2 - 125, 350, 250);
					drawLoginScreen(true);
					try {
						Thread.sleep(1000L);
					} catch (Exception _ex) {
					}
				}

				login(s, s1, flag);
				return;
			}
			if (k == 22) {
				fadingScreen = new FadingScreenImage(newBoldFont,
						"Your computer has been UUID banned. Please appeal on the forums.", (byte) -1, (byte) 5,
						765 / 2 - 125, 350, 250);
				return;
			}
			if (k == 23) {
				loginState = LoginScreenState.DOWNLOADING_CLIENT;
				new UpdateClient().start();
				return;
			}
			if (k == 24) {
				fadingScreen = new FadingScreenImage(newBoldFont,
						"The server is currently in alpha or beta mode. Only alpha or beta testers can login.",
						(byte) -1, (byte) 5, 765 / 2 - 125, 350, 250);
				return;
			}
			if (k == -1) {
				if (i1 == 0) {
					if (loginFailures < 2) {
						try {
							Thread.sleep(2000L);
						} catch (Exception _ex) {
						}
						loginFailures++;
						login(s, s1, flag);
						return;
					}
				} else {
					loginState = LoginScreenState.DOWNLOADING_CLIENT;
					new UpdateClient().start();
					return;
				}
			} else {
				loginState = LoginScreenState.DOWNLOADING_CLIENT;
				new UpdateClient().start();
				return;
			}
		} catch (IOException _ex) {
		} catch (Exception e) {
			System.out.println("Error while generating uid. Skipping step.");
			e.printStackTrace();
		}
		fadingScreen = new FadingScreenImage(newBoldFont,
				"Error connecting to server. Go to https://os-v.org/ for info.", (byte) -1, (byte) 5, 765 / 2 - 125,
				350, 250);
	}

	private boolean doWalkTo(int i, int j, int k, int i1, int j1, int k1, int l1, int i2, int j2, boolean flag,
			int k2) {
		byte byte0 = 104;
		byte byte1 = 104;
		for (int l2 = 0; l2 < byte0; l2++) {
			for (int i3 = 0; i3 < byte1; i3++) {
				anIntArrayArray901[l2][i3] = 0;
				anIntArrayArray825[l2][i3] = 0x5f5e0ff;
			}
		}
		int j3 = j2;
		int k3 = j1;
		anIntArrayArray901[j2][j1] = 99;
		anIntArrayArray825[j2][j1] = 0;
		int l3 = 0;
		int i4 = 0;
		bigX[l3] = j2;
		bigY[l3++] = j1;
		boolean flag1 = false;
		int j4 = bigX.length;
		int ai[][] = aClass11Array1230[plane].anIntArrayArray294;
		while (i4 != l3) {
			j3 = bigX[i4];
			k3 = bigY[i4];
			i4 = (i4 + 1) % j4;
			if (j3 == k2 && k3 == i2) {
				flag1 = true;
				break;
			}
			if (i1 != 0) {
				if ((i1 < 5 || i1 == 10) && aClass11Array1230[plane].method219(k2, j3, k3, j, i1 - 1, i2)) {
					flag1 = true;
					break;
				}
				if (i1 < 10 && aClass11Array1230[plane].method220(k2, i2, k3, i1 - 1, j, j3)) {
					flag1 = true;
					break;
				}
			}
			if (k1 != 0 && k != 0 && aClass11Array1230[plane].method221(i2, k2, j3, k, l1, k1, k3)) {
				flag1 = true;
				break;
			}
			int l4 = anIntArrayArray825[j3][k3] + 1;
			if (j3 > 0 && anIntArrayArray901[j3 - 1][k3] == 0 && (ai[j3 - 1][k3] & 0x1280108) == 0) {
				bigX[l3] = j3 - 1;
				bigY[l3] = k3;
				l3 = (l3 + 1) % j4;
				anIntArrayArray901[j3 - 1][k3] = 2;
				anIntArrayArray825[j3 - 1][k3] = l4;
			}
			if (j3 < byte0 - 1 && anIntArrayArray901[j3 + 1][k3] == 0 && (ai[j3 + 1][k3] & 0x1280180) == 0) {
				bigX[l3] = j3 + 1;
				bigY[l3] = k3;
				l3 = (l3 + 1) % j4;
				anIntArrayArray901[j3 + 1][k3] = 8;
				anIntArrayArray825[j3 + 1][k3] = l4;
			}
			if (k3 > 0 && anIntArrayArray901[j3][k3 - 1] == 0 && (ai[j3][k3 - 1] & 0x1280102) == 0) {
				bigX[l3] = j3;
				bigY[l3] = k3 - 1;
				l3 = (l3 + 1) % j4;
				anIntArrayArray901[j3][k3 - 1] = 1;
				anIntArrayArray825[j3][k3 - 1] = l4;
			}
			if (k3 < byte1 - 1 && anIntArrayArray901[j3][k3 + 1] == 0 && (ai[j3][k3 + 1] & 0x1280120) == 0) {
				bigX[l3] = j3;
				bigY[l3] = k3 + 1;
				l3 = (l3 + 1) % j4;
				anIntArrayArray901[j3][k3 + 1] = 4;
				anIntArrayArray825[j3][k3 + 1] = l4;
			}
			if (j3 > 0 && k3 > 0 && anIntArrayArray901[j3 - 1][k3 - 1] == 0 && (ai[j3 - 1][k3 - 1] & 0x128010e) == 0
					&& (ai[j3 - 1][k3] & 0x1280108) == 0 && (ai[j3][k3 - 1] & 0x1280102) == 0) {
				bigX[l3] = j3 - 1;
				bigY[l3] = k3 - 1;
				l3 = (l3 + 1) % j4;
				anIntArrayArray901[j3 - 1][k3 - 1] = 3;
				anIntArrayArray825[j3 - 1][k3 - 1] = l4;
			}
			if (j3 < byte0 - 1 && k3 > 0 && anIntArrayArray901[j3 + 1][k3 - 1] == 0
					&& (ai[j3 + 1][k3 - 1] & 0x1280183) == 0 && (ai[j3 + 1][k3] & 0x1280180) == 0
					&& (ai[j3][k3 - 1] & 0x1280102) == 0) {
				bigX[l3] = j3 + 1;
				bigY[l3] = k3 - 1;
				l3 = (l3 + 1) % j4;
				anIntArrayArray901[j3 + 1][k3 - 1] = 9;
				anIntArrayArray825[j3 + 1][k3 - 1] = l4;
			}
			if (j3 > 0 && k3 < byte1 - 1 && anIntArrayArray901[j3 - 1][k3 + 1] == 0
					&& (ai[j3 - 1][k3 + 1] & 0x1280138) == 0 && (ai[j3 - 1][k3] & 0x1280108) == 0
					&& (ai[j3][k3 + 1] & 0x1280120) == 0) {
				bigX[l3] = j3 - 1;
				bigY[l3] = k3 + 1;
				l3 = (l3 + 1) % j4;
				anIntArrayArray901[j3 - 1][k3 + 1] = 6;
				anIntArrayArray825[j3 - 1][k3 + 1] = l4;
			}
			if (j3 < byte0 - 1 && k3 < byte1 - 1 && anIntArrayArray901[j3 + 1][k3 + 1] == 0
					&& (ai[j3 + 1][k3 + 1] & 0x12801e0) == 0 && (ai[j3 + 1][k3] & 0x1280180) == 0
					&& (ai[j3][k3 + 1] & 0x1280120) == 0) {
				bigX[l3] = j3 + 1;
				bigY[l3] = k3 + 1;
				l3 = (l3 + 1) % j4;
				anIntArrayArray901[j3 + 1][k3 + 1] = 12;
				anIntArrayArray825[j3 + 1][k3 + 1] = l4;
			}
		}
		anInt1264 = 0;
		if (!flag1) {
			if (flag) {
				int i5 = 100;
				for (int k5 = 1; k5 < 2; k5++) {
					for (int i6 = k2 - k5; i6 <= k2 + k5; i6++) {
						for (int l6 = i2 - k5; l6 <= i2 + k5; l6++)
							if (i6 >= 0 && l6 >= 0 && i6 < 104 && l6 < 104 && anIntArrayArray825[i6][l6] < i5) {
								i5 = anIntArrayArray825[i6][l6];
								j3 = i6;
								k3 = l6;
								anInt1264 = 1;
								flag1 = true;
							}

					}

					if (flag1)
						break;
				}

			}
			if (!flag1)
				return false;
		}
		i4 = 0;
		bigX[i4] = j3;
		bigY[i4++] = k3;
		int l5;
		for (int j5 = l5 = anIntArrayArray901[j3][k3]; j3 != j2 || k3 != j1; j5 = anIntArrayArray901[j3][k3]) {
			if (j5 != l5) {
				l5 = j5;
				bigX[i4] = j3;
				bigY[i4++] = k3;
			}
			if ((j5 & 2) != 0)
				j3++;
			else if ((j5 & 8) != 0)
				j3--;
			if ((j5 & 1) != 0)
				k3++;
			else if ((j5 & 4) != 0)
				k3--;
		}
		// if(cancelWalk) { return i4 > 0; }

		if (i4 > 0) {
			int k4 = i4;
			if (k4 > 25)
				k4 = 25;
			i4--;
			int k6 = bigX[i4];
			int i7 = bigY[i4];
			anInt1288 += k4;
			if (anInt1288 >= 92) {
				stream.createFrame(36);
				stream.writeDWord(0);
				anInt1288 = 0;
			}
			if (i == 0) {
				stream.createFrame(164);
				stream.writeWordBigEndian(k4 + k4 + 3);
			}
			if (i == 1) {
				stream.createFrame(248);
				stream.writeWordBigEndian(k4 + k4 + 3 + 14);
			}
			if (i == 2) {
				stream.createFrame(98);
				stream.writeWordBigEndian(k4 + k4 + 3);
			}
			stream.method433(k6 + baseX);
			destX = bigX[0];
			destY = bigY[0];
			for (int j7 = 1; j7 < k4; j7++) {
				i4--;
				stream.writeWordBigEndian(bigX[i4] - k6);
				stream.writeWordBigEndian(bigY[i4] - i7);
			}

			stream.method431(i7 + baseY);
			stream.method424(super.keyArray[5] != 1 ? 0 : 1);
			return true;
		}
		return i != 1;
	}

	public void method86(Stream stream) {
		for (int j = 0; j < anInt893; j++) {
			int k = anIntArray894[j];
			NPC npc = npcArray[k];
			int l = stream.readUnsignedByte();
			if ((l & 0x10) != 0) {
				int i1 = stream.method434();
				if (i1 == 65535)
					i1 = -1;
				int i2 = stream.readUnsignedByte();
				if (i1 == npc.anim && i1 != -1) {
					int l2 = AnimationDefinition.anims[i1].anInt365;
					if (l2 == 1) {
						npc.anInt1527 = 0;
						npc.anInt1528 = 0;
						npc.anInt1529 = i2;
						npc.anInt1530 = 0;
					}
					if (l2 == 2)
						npc.anInt1530 = 0;
				} else if (i1 == -1 || npc.anim == -1
						|| AnimationDefinition.anims[i1].anInt359 >= AnimationDefinition.anims[npc.anim].anInt359) {
					npc.anim = i1;
					npc.anInt1527 = 0;
					npc.anInt1528 = 0;
					npc.anInt1529 = i2;
					npc.anInt1530 = 0;
					npc.anInt1542 = npc.smallXYIndex;
				}
			}
			if ((l & 8) != 0) {
				int j1 = stream.method426();
				int j2 = stream.method427();
				npc.updateHitData(j2, j1, loopCycle);
				npc.loopCycleStatus = loopCycle + 300;
				npc.currentHealth = stream.readUnsignedWord();
				npc.maxHealth = stream.readUnsignedWord();
			}
			if ((l & 0x80) != 0) {
				npc.anInt1520 = stream.readUnsignedWord();
				int k1 = stream.readDWord();
				npc.anInt1524 = k1 >> 16;
				npc.anInt1523 = loopCycle + (k1 & 0xffff);
				npc.anInt1521 = 0;
				npc.anInt1522 = 0;
				if (npc.anInt1523 > loopCycle)
					npc.anInt1521 = -1;
				if (npc.anInt1520 == 65535)
					npc.anInt1520 = -1;
			}
			if ((l & 0x20) != 0) {
				npc.interactingEntity = stream.readUnsignedWord();
				if (npc.interactingEntity == 65535)
					npc.interactingEntity = -1;
			}
			if ((l & 1) != 0) {
				npc.textSpoken = stream.readString();
				npc.textCycle = 100;
			}
			if ((l & 0x40) != 0) {
				int l1 = stream.method427();
				int k2 = stream.method428();
				npc.updateHitData(k2, l1, loopCycle);
				npc.loopCycleStatus = loopCycle + 300;
				npc.currentHealth = stream.readUnsignedWord();
				npc.maxHealth = stream.readUnsignedWord();
			}
			if ((l & 2) != 0) {
				npc.desc = NpcDefinition.forID(stream.method436());
				npc.anInt1540 = npc.desc.boundDim;
				npc.anInt1504 = npc.desc.getDegreesToTurn;
				npc.anInt1554 = npc.desc.walkAnim;
				npc.anInt1555 = npc.desc.anInt58;
				npc.anInt1556 = npc.desc.anInt83;
				npc.anInt1557 = npc.desc.anInt55;
				npc.anInt1511 = npc.desc.standAnim;
			}
			if ((l & 4) != 0) {
				npc.anInt1538 = stream.method434();
				npc.anInt1539 = stream.method434();
			}
		}
	}

	private long debugDelay;

	public void buildAtNPCMenu(NpcDefinition entityDef, int i, int j, int k) {
		if (menuActionRow >= 400)
			return;
		if (entityDef.childrenIDs != null)
			entityDef = entityDef.method161();
		if (entityDef == null)
			return;
		if (!entityDef.aBoolean84)
			return;
		String s = entityDef.name;
		if (entityDef.combatLevel != 0)
			s = s + combatDiffColor(myPlayer.combatLevel, entityDef.combatLevel) + " (level-" + entityDef.combatLevel
					+ ")";
		if (itemSelected == 1) {
			menuActionName[menuActionRow] = "Use " + selectedItemName + " with @yel@" + s;
			menuActionID[menuActionRow] = 582;
			menuActionCmd1[menuActionRow] = i;
			menuActionCmd2[menuActionRow] = k;
			menuActionCmd3[menuActionRow] = j;
			menuActionRow++;
			return;
		}
		if (spellSelected == 1) {
			if ((spellUsableOn & 2) == 2) {
				menuActionName[menuActionRow] = spellTooltip + " @yel@" + s;
				menuActionID[menuActionRow] = 413;
				menuActionCmd1[menuActionRow] = i;
				menuActionCmd2[menuActionRow] = k;
				menuActionCmd3[menuActionRow] = j;
				menuActionRow++;
			}
		} else {
			if (entityDef.actions != null) {
				for (int l = 4; l >= 0; l--)
					if (entityDef.actions[l] != null && !entityDef.actions[l].equalsIgnoreCase("attack")) {
						menuActionName[menuActionRow] = entityDef.actions[l] + " @yel@" + s;
						if (l == 0)
							menuActionID[menuActionRow] = 20;
						if (l == 1)
							menuActionID[menuActionRow] = 412;
						if (l == 2)
							menuActionID[menuActionRow] = 225;
						if (l == 3)
							menuActionID[menuActionRow] = 965;
						if (l == 4)
							menuActionID[menuActionRow] = 478;
						menuActionCmd1[menuActionRow] = i;
						menuActionCmd2[menuActionRow] = k;
						menuActionCmd3[menuActionRow] = j;
						menuActionRow++;
					}

			}
			if (entityDef.actions != null) {
				for (int i1 = 4; i1 >= 0; i1--)
					if (entityDef.actions[i1] != null && entityDef.actions[i1].equalsIgnoreCase("attack")) {
						char c = '\0';
						if (!leftClickAttack)
							c = '\u07D0';
						menuActionName[menuActionRow] = entityDef.actions[i1] + " @yel@" + s;
						if (i1 == 0)
							menuActionID[menuActionRow] = 20 + c;
						if (i1 == 1)
							menuActionID[menuActionRow] = 412 + c;
						if (i1 == 2)
							menuActionID[menuActionRow] = 225 + c;
						if (i1 == 3)
							menuActionID[menuActionRow] = 965 + c;
						if (i1 == 4)
							menuActionID[menuActionRow] = 478 + c;
						menuActionCmd1[menuActionRow] = i;
						menuActionCmd2[menuActionRow] = k;
						menuActionCmd3[menuActionRow] = j;
						menuActionRow++;
					}

			}
			// menuActionName[menuActionRow] = "Examine @yel@" + s +
			// "
			// @gre@O(@whi@"+Arrays.toString(entityDef.getOriginalColors())+")@gre@)@gre@N
			// (@whi@"+Arrays.toString(entityDef.getNewColors())+")@gre@)";
			menuActionName[menuActionRow] = "Examine @yel@" + s;
			if (debugModels == true) {
				if (System.currentTimeMillis() - debugDelay > 1000 && entityDef.models != null) {
					String modelIds = Arrays.toString(entityDef.models);
					String regColors = Arrays.toString(entityDef.originalColors);
					String newColors = Arrays.toString(entityDef.newColors);
					String name = entityDef.name;
					System.out.println(name + modelIds);
					pushMessage(name + ": " + modelIds, 0, "");
					pushMessage("Reg: " + regColors, 0, "");
					pushMessage("New: " + newColors, 0, "");
					debugDelay = System.currentTimeMillis();
					// menuActionName[menuActionRow] = "Examine
					// @gre@O(@whi@"+Arrays.toString(entityDef.models)+")@gre@)";
					// menuActionName[menuActionRow] = "#2
					// (@whi@"+Arrays.toString(entityDef.originalColors)+")@gre@)(@whi@"+Arrays.toString(entityDef.newColors)+")@gre@)";
				}
			}
			menuActionID[menuActionRow] = 1025;
			menuActionCmd1[menuActionRow] = i;
			menuActionCmd2[menuActionRow] = k;
			menuActionCmd3[menuActionRow] = j;
			menuActionRow++;
		}
	}

	public void buildAtPlayerMenu(int i, int j, Player player, int k) {
		if (player == myPlayer)
			return;
		if (!player.visible)
			return;
		if (menuActionRow >= 400)
			return;
		String s;
		if (player.title.length() < 0)
			s = player.name + combatDiffColor(myPlayer.combatLevel, player.combatLevel) + " (level: "
					+ player.combatLevel + ")";
		else if (player.title.length() != 0)
			s = "@or1@" + player.title + "@whi@ " + player.name
					+ combatDiffColor(myPlayer.combatLevel, player.combatLevel) + " (level: " + player.combatLevel
					+ ")";
		else
			s = player.name + combatDiffColor(myPlayer.combatLevel, player.combatLevel) + " (level: "
					+ player.combatLevel + ")";
		if (itemSelected == 1) {
			menuActionName[menuActionRow] = "Use " + selectedItemName + " with @whi@" + s;
			menuActionID[menuActionRow] = 491;
			menuActionCmd1[menuActionRow] = j;
			menuActionCmd2[menuActionRow] = i;
			menuActionCmd3[menuActionRow] = k;
			menuActionRow++;
		} else if (spellSelected == 1) {
			if ((spellUsableOn & 8) == 8) {
				menuActionName[menuActionRow] = spellTooltip + " @whi@" + s;
				menuActionID[menuActionRow] = 365;
				menuActionCmd1[menuActionRow] = j;
				menuActionCmd2[menuActionRow] = i;
				menuActionCmd3[menuActionRow] = k;
				menuActionRow++;
			}
		} else {
			for (int l = 5; l >= 0; l--)
				if (atPlayerActions[l] != null) {
					menuActionName[menuActionRow] = atPlayerActions[l] + " @whi@" + s;
					char c = '\0';
					if (atPlayerActions[l].equalsIgnoreCase("attack")) {
						if (variousSettings[430] != 0) {
							c = '\u07D0';
						}
						if (myPlayer.team != 0 && player.team != 0)
							if (myPlayer.team == player.team)
								c = '\u07D0';
							else
								c = '\0';
					} else if (atPlayerArray[l])
						c = '\u07D0';
					if (l == 0)
						menuActionID[menuActionRow] = 561 + c;
					if (l == 1)
						menuActionID[menuActionRow] = 779 + c;
					if (l == 2)
						menuActionID[menuActionRow] = 27 + c;
					if (l == 3)
						menuActionID[menuActionRow] = 577 + c;
					if (l == 4)
						menuActionID[menuActionRow] = 729 + c;
					if (l == 5) {
						if (myPlayer.getRights() > 0 && myPlayer.getRights() < 4) {
							menuActionID[menuActionRow] = 745 + c;
						} else {
							continue;
						}
					}
					menuActionCmd1[menuActionRow] = j;
					menuActionCmd2[menuActionRow] = i;
					menuActionCmd3[menuActionRow] = k;
					menuActionRow++;
				}

		}
		for (int i1 = 0; i1 < menuActionRow; i1++)
			if (menuActionID[i1] == 516) {
				menuActionName[i1] = "Walk here @whi@" + s;
				return;
			}

	}

	public void method89(Class30_Sub1 class30_sub1) {
		int i = 0;
		int j = -1;
		int k = 0;
		int l = 0;
		if (class30_sub1.anInt1296 == 0)
			i = worldController.method300(class30_sub1.anInt1295, class30_sub1.anInt1297, class30_sub1.anInt1298);
		if (class30_sub1.anInt1296 == 1)
			i = worldController.method301(class30_sub1.anInt1295, class30_sub1.anInt1297, class30_sub1.anInt1298);
		if (class30_sub1.anInt1296 == 2)
			i = worldController.method302(class30_sub1.anInt1295, class30_sub1.anInt1297, class30_sub1.anInt1298);
		if (class30_sub1.anInt1296 == 3)
			i = worldController.method303(class30_sub1.anInt1295, class30_sub1.anInt1297, class30_sub1.anInt1298);
		if (i != 0) {
			int i1 = worldController.method304(class30_sub1.anInt1295, class30_sub1.anInt1297, class30_sub1.anInt1298,
					i);
			j = i >> 14 & 0x7fff;
			k = i1 & 0x1f;
			l = i1 >> 6;
		}
		class30_sub1.anInt1299 = j;
		class30_sub1.anInt1301 = k;
		class30_sub1.anInt1300 = l;
	}

	public void method90() {
		for (int i = 0; i < anInt1062; i++)
			if (anIntArray1250[i] <= 0) {
				boolean flag1 = false;
				try {
					if (anIntArray1207[i] == anInt874 && anIntArray1241[i] == anInt1289) {
						if (!replayWave())
							flag1 = true;
					} else {
						Stream stream = Sounds.method241(anIntArray1241[i], anIntArray1207[i]);
						if (System.currentTimeMillis() + stream.currentOffset / 22 > aLong1172 + anInt1257 / 22) {
							anInt1257 = stream.currentOffset;
							aLong1172 = System.currentTimeMillis();
							if (saveWave(stream.buffer, stream.currentOffset)) {
								anInt874 = anIntArray1207[i];
								anInt1289 = anIntArray1241[i];
							} else {
								flag1 = true;
							}
						}
					}
				} catch (Exception exception) {
				}
				if (!flag1 || anIntArray1250[i] == -5) {
					anInt1062--;
					for (int j = i; j < anInt1062; j++) {
						anIntArray1207[j] = anIntArray1207[j + 1];
						anIntArray1241[j] = anIntArray1241[j + 1];
						anIntArray1250[j] = anIntArray1250[j + 1];
					}

					i--;
				} else {
					anIntArray1250[i] = -5;
				}
			} else {
				anIntArray1250[i]--;
			}

		if (prevSong > 0) {
			prevSong -= 20;
			if (prevSong < 0)
				prevSong = 0;
			if (prevSong == 0 && musicEnabled && !lowMem) {
				nextSong = currentSong;
				songChanging = true;
				onDemandFetcher.method558(2, nextSong);
			}
		}
	}

	public void objectFill(final int objectId, final int x1, final int y1, final int x2, final int y2, final int type,
			final int face, final int height) {
		// if(height == height) // dunno your height variable but refactor
		// yourself
		for (int x = x1; x <= x2; x++) {
			for (int y = y1; y <= y2; y++) {
				addObject(objectId, x, y, face, type, height);
			}
		}
	}

	public void addObject(int objectId, int x, int y, int face, int type, int height) {
		int mX = mapRegionsX - 6;
		int mY = mapRegionsY - 6;
		int x2 = x - (mX * 8);
		int y2 = y - (mY * 8);
		int i15 = 40 >> 2;
		int l17 = anIntArray1177[i15];
		if (y2 > 0 && y2 < 103 && x2 > 0 && x2 < 103) {
			method130(-1, objectId, face, l17, y2, type, height, x2, 0);
		}
	}

	/**
	 * Objects that will load while christmas event is live
	 */
	public void load_holiday_objects() {
		if (Configuration.CHRISTMAS && Configuration.CHRISTMAS_EVENT) {
			addObject(0, 3083, 3500, 0, 10, 0);

			addObject(2279, 2831, 3796, 2, 10, 0); // Benches
			addObject(2279, 2833, 3797, 1, 10, 0);
			addObject(2279, 2830, 3797, 3, 10, 0);
			addObject(2279, 2824, 3808, 2, 10, 2);
			addObject(2279, 2824, 3811, 0, 10, 2);
			addObject(19038, 3083, 3499, 0, 10, 0); // Tree
			addObject(2147, 2957, 3704, 0, 10, 0); // Ladders
			addObject(2147, 2952, 3821, 0, 10, 0);
			addObject(3309, 2953, 3821, 0, 10, 0);
			addObject(-1, 2977, 3634, 0, 10, 0);
			addObject(-1, 2979, 3642, 0, 10, 0);
		}
		if (Configuration.HALLOWEEN) {
			addObject(298, 3088, 3497, 0, 10, 0);
			addObject(298, 3085, 3496, 1, 10, 0);
			addObject(298, 3085, 3493, 1, 10, 0);
			addObject(2715, 3088, 3494, 1, 10, 0);
			addObject(0, 3088, 3496, 0, 10, 0);
			addObject(0, 3089, 3496, 0, 10, 0);
			addObject(0, 3088, 3495, 0, 10, 0);
			addObject(0, 3089, 3495, 0, 10, 0);
			addObject(298, 2610, 4774, 0, 10, 0);
			addObject(298, 2610, 4771, 0, 10, 0);
			addObject(298, 2601, 4780, 1, 10, 0);
			addObject(298, 2601, 4768, 1, 10, 0);
			addObject(298, 2597, 4778, 1, 10, 0);
			addObject(298, 2598, 4772, 1, 10, 0);
		}
	}

	public void load_objects() {
		/**
		 * @link addObject objectId, x, y, face, type, height
		 */
		addObject(0, 1544, 3687, 0, 10, 0);
		// Magebank
		addObject(24101, 2535, 4719, 0, 10, 0);
		// Lletya
		addObject(24101, 2353, 3164, 0, 10, 0);
		// Staffzone
		addObject(24101, 3164, 3488, 0, 10, 2);
		// Abyss
		addObject(24101, 3039, 4834, 0, 10, 0);
		addObject(13642, 3039, 4831, 2, 10, 0);
		// Edgeville
		objectFill(-1, 3077, 3508, 3084, 3513, 10, 0, 0); // General store
		addObject(-1, 3084, 3513, 0, 4, 0);
		addObject(-1, 3077, 3507, 0, 10, 0);
		addObject(-1, 3090, 3503, 0, 10, 0);
		addObject(-1, 3084, 3502, 0, 0, 0);
		addObject(-1, 3079, 3507, 0, 10, 0);
		addObject(-1, 3076, 3509, 0, 10, 0);
		addObject(-1, 3076, 3510, 0, 10, 0);
		addObject(-1, 3076, 3511, 0, 10, 0);
		addObject(-1, 3076, 3512, 0, 10, 0);
		addObject(-1, 3099, 3490, 0, 10, 0);
		addObject(-1, 3099, 3491, 0, 10, 0);
		addObject(-1, 3099, 3493, 0, 10, 0);
		addObject(-1, 3095, 3498, 0, 10, 0);
		addObject(-1, 3095, 3499, 0, 10, 0);
		addObject(-1, 3090, 3496, 0, 10, 0);
		addObject(-1, 3090, 3494, 0, 10, 0);
		addObject(-1, 3092, 3496, 0, 10, 0);
		addObject(-1, 3091, 3495, 0, 10, 0);
		addObject(-1, 3085, 3507, 0, 10, 0);
		addObject(-1, 3103, 3499, 0, 10, 0);
		addObject(6249, 3084, 3503, 1, 10, 0);
		addObject(172, 3084, 3490, 1, 10, 0);
		addObject(6150, 3105, 3502, 3, 10, 0);
		addObject(6150, 3105, 3502, 3, 10, 0);
		addObject(6150, 3107, 3502, 3, 10, 0);
		addObject(24600, 3091, 3492, 0, 10, 0);
		addObject(24101, 3084, 3513, 2, 11, 0);
		addObject(409, 3085, 3510, 1, 10, 0);
		addObject(412, 3092, 3506, 2, 10, 0);
		addObject(6552, 3094, 3506, 2, 10, 0);
		// RFD
		addObject(12309, 3219, 9623, 1, 10, 0);
		addObject(12355, 3218, 9621, 0, 10, 0);
		// Resource area
		addObject(9030, 3190, 3929, 1, 10, 0);
		addObject(9030, 3191, 3930, 1, 10, 0);
		addObject(9030, 3190, 3931, 1, 10, 0);

		// Skilling area
		objectFill(-1, 2983, 3381, 2987, 3390, 10, 0, 0);
		//Obelisks
		addObject(2153, 3014, 3389, 0, 10, 0);
		addObject(2152, 3013, 3389, 0, 10, 0);
		addObject(2151, 3011, 3389, 0, 10, 0);
		addObject(2150, 3008, 3388, 0, 10, 0);
		
		addObject(-1, 3026, 3376, 0, 10, 0);
		addObject(-1, 3029, 3376, 1, 10, 0);
		addObject(-1, 3030, 3377, 1, 10, 0);
		addObject(-1, 3031, 3377, 1, 10, 0);
		addObject(-1, 3031, 3376, 1, 10, 0);
		addObject(-1, 3029, 3382, 1, 10, 0);
		addObject(-1, 3029, 3383, 1, 10, 0);
		addObject(-1, 3004, 3383, 1, 10, 0);
		addObject(-1, 3003, 3372, 0, 10, 0);
		addObject(18819, 3003, 3372, 0, 10, 0);
		addObject(2742, 3030, 3376, 2, 22, 0);
		addObject(2759, 3019, 3380, 2, 22, 0);
		addObject(2741, 3013, 3369, 2, 22, 0);
		addObject(24009, 3030, 3375, 2, 10, 0);
		addObject(24101, 3029, 3379, 1, 10, 0);
		addObject(7484, 3021, 3368, 1, 10, 0); //Copper
		addObject(7484, 3020, 3368, 1, 10, 0); //Copper
		addObject(7485, 3019, 3368, 1, 10, 0); //Tin
		addObject(7485, 3018, 3367, 1, 10, 0); //Tin
		addObject(7488, 3017, 3367, 1, 10, 0); //Iron
		addObject(7488, 3016, 3367, 1, 10, 0); //Iron
		addObject(7488, 3015, 3367, 1, 10, 0); //Iron
		addObject(7489, 3014, 3367, 1, 10, 0); //Coal
		addObject(7489, 3013, 3368, 1, 10, 0); //Coal
		addObject(7491, 3012, 3369, 1, 10, 0); //Gold
		addObject(7491, 3011, 3369, 1, 10, 0); //Gold
		addObject(9030, 3010, 3369, 1, 10, 0); //Gem
		addObject(7492, 3009, 3369, 1, 10, 0); //Mithril
		addObject(7493, 3008, 3369, 1, 10, 0); //Adamant
		addObject(114, 3030, 3383, 1, 10, 0);
		addObject(6150, 3027, 3375, 2, 10, 0);
		addObject(14888, 3030, 3381, 3, 10, 0); //7131
		addObject(25824, 3029, 3381, 1, 10, 0);
		addObject(7813, 3001, 3372, 2, 10, 0);
		addObject(7674, 2999, 3373, 0, 10, 0);
		// Barrows
		addObject(3610, 3550, 9695, 0, 10, 0);
		
		//Moonclan
		objectFill(0, 2097, 3854, 2097, 3954, 10, 0, 0);

		// Crafting guild
		objectFill(-1, 2939, 3282, 2943, 3290, 10, 0, 0); // Crafting guild
		addObject(-1, 2938, 3285, 0, 10, 0);
		addObject(-1, 2937, 3284, 0, 10, 0);
		addObject(-1, 2942, 3291, 0, 10, 0);
		addObject(9030, 2940, 3288, 1, 10, 0);
		addObject(9030, 2942, 3288, 1, 10, 0);
		addObject(9030, 2941, 3289, 1, 10, 0);
		
		/**
		 * Shilo
		 */
		//Obelisks
		addObject(2153, 2846, 2963, 0, 10, 0);
		addObject(2152, 2849, 2964, 0, 10, 0);
		addObject(2151, 2846, 2967, 0, 10, 0);
		addObject(2150, 2848, 2966, 0, 10, 0);
		//Removing from shilo
		addObject(-1, 2851, 2952, 0, 10, 0); addObject(-1, 2832, 2982, 0, 10, 0); addObject(-1, 2847, 2953, 0, 10, 0); addObject(-1, 2854, 2967, 0, 10, 0);
		addObject(-1, 2837, 2982, 0, 10, 0); addObject(-1, 2835, 2988, 0, 10, 0); addObject(-1, 2856, 2953, 0, 10, 0);
		addObject(-1, 2837, 2987, 0, 10, 0); addObject(-1, 2834, 2980, 0, 10, 0); addObject(-1, 2856, 2956, 0, 10, 0);
		objectFill(-1, 2833, 2990, 2837, 2992, 10, 0, 0); objectFill(-1, 2819, 2997, 2826, 3004, 10, 0, 0);
		objectFill(-1, 2841, 2976, 2858, 2994, 10, 0, 0); objectFill(-1, 2841, 2976, 2858, 2994, 0, 0, 1); objectFill(-1, 2841, 2976, 2858, 2994, 0, 0, 2);
		objectFill(-1, 2841, 2976, 2858, 2994, 0, 0, 3);
		
		//Bank room
		addObject(1113, 2850, 2952, 0, 10, 0); //Chair
		objectFill(24101, 2851, 2952, 2853, 2952, 10, 0, 0); // Bank
		addObject(13641, 2856, 2954, 0, 10, 0); //Teleportation
		addObject(26185, 2848, 2953, 0, 10, 0); //Fire
		addObject(26185, 2848, 2955, 0, 10, 0); //Fire
		addObject(14880, 2839, 2983, 2, 22, 0); //Trapdoor
		addObject(170, 2848, 2954, 1, 10, 0); // Chest
		addObject(24101, 2854, 2967, 0, 10, 0); // Bank
		
		addObject(6150, 2859, 2967, 0, 10, 0); // Anvi
		
		//2nd bank room, other side
		addObject(2738, 2835, 2985, 2, 22, 0);
		addObject(24104, 2833, 2981, 2, 10, 0);
		addObject(24104, 2838, 2981, 2, 10, 0);
		objectFill(24101, 2834, 2981, 2837, 2981, 10, 0, 0); // Bank
		addObject(13641, 2837, 2991, 0, 10, 0);
		addObject(14880, 2859, 2954, 2, 22, 0); //Trapdoor
		addObject(170, 2833, 2991, 1, 10, 0); // Chest
		
		//Mining
		addObject(7494, 2824, 3003, 0, 10, 0);
		addObject(7494, 2823, 3002, 0, 10, 0);
		addObject(7494, 2822, 3001, 0, 10, 0);
		addObject(7494, 2821, 3000, 0, 10, 0);
		addObject(7494, 2820, 2999, 0, 10, 0);
		addObject(7494, 2819, 2998, 0, 10, 0);
		addObject(7493, 2821, 2996, 0, 10, 0);
		addObject(7493, 2822, 2997, 0, 10, 0);
		addObject(7493, 2823, 2998, 0, 10, 0);
		addObject(7493, 2824, 2999, 0, 10, 0);
		addObject(7493, 2825, 3000, 0, 10, 0);
		addObject(7493, 2826, 3001, 0, 10, 0);
		addObject(9030, 2819, 2997, 1, 10, 0);
		addObject(9030, 2825, 3003, 1, 10, 0);
		
		//Woodcutting
		// Magic trees
		addObject(1761, 2853, 2980, 0, 10, 0);
		addObject(1761, 2853, 2983, 0, 10, 0);
		addObject(1761, 2853, 2986, 0, 10, 0);
		addObject(1761, 2853, 2989, 0, 10, 0);
		// Yew trees
		addObject(1753, 2842, 2980, 0, 10, 0);
		addObject(1753, 2842, 2983, 0, 10, 0);
		addObject(1753, 2842, 2986, 0, 10, 0);
		addObject(1753, 2842, 2989, 0, 10, 0);

		// Vet
		addObject(0, 3189, 3784, 0, 10, 0);
		addObject(0, 3189, 3782, 0, 10, 0);
		addObject(0, 3188, 3783, 0, 10, 0);
		addObject(0, 3191, 3784, 0, 10, 0);
		/**
		 * @link objectFill objectId, SWX, SWY, NEX, NEY, type, face, height
		 */
		objectFill(-1, 2850, 2953, 2854, 2956, 10, 0, 0);
		objectFill(-1, 3010, 3371, 3014, 3375, 10, 0, 0);
		objectFill(-1, 1748, 5322, 1755, 5327, 22, 0, 1);
		objectFill(14896, 3010, 3372, 3014, 3374, 10, 0, 0);
		objectFill(14896, 3011, 3371, 3013, 3371, 10, 0, 0);
		objectFill(14896, 3011, 3375, 3013, 3375, 10, 0, 0);
	}

	@Override
	void startUp() {
		drawSmoothLoading(16, "Starting up - This can take a few minutes");
		new CacheDownloader(this).downloadCache();
		if (Signlink.sunjava) {
			super.minDelay = 5;
		}
		getDocumentBaseHost();
		if (Signlink.cache_dat != null) {
			for (int i = 0; i < 5; i++)
				decompressors[i] = new Decompressor(Signlink.cache_dat, Signlink.cache_idx[i], i + 1);
		}
		
//			SpriteLoader.loadSprites();
//			cacheInterface = SpriteLoader.sprites;
		
//		repackCacheIndex(1);
//		repackCacheIndex(2);
//		repackCacheIndex(4);

		
		try {
			titleStreamLoader = streamLoaderForName(1, "title screen", "title", expectedCRCs[1], 25);
			smallText = new TextDrawingArea(false, "p11_full", titleStreamLoader);
			XPFONT = new TextDrawingArea(true, "q8_full", titleStreamLoader);
			aTextDrawingArea_1271 = new TextDrawingArea(false, "p12_full", titleStreamLoader);
			chatTextDrawingArea = new TextDrawingArea(false, "b12_full", titleStreamLoader);
			aTextDrawingArea_1273 = new TextDrawingArea(true, "q8_full", titleStreamLoader);
			newSmallFont = new RSFont(false, "p11_full", titleStreamLoader);
			newRegularFont = new RSFont(false, "p12_full", titleStreamLoader);
			newBoldFont = new RSFont(false, "b12_full", titleStreamLoader);
			newFancyFont = new RSFont(true, "q8_full", titleStreamLoader);
			loadTitleScreen();
			StreamLoader streamLoader = streamLoaderForName(2, "config", "config", expectedCRCs[2], 30);
			StreamLoader streamLoader_1 = streamLoaderForName(3, "interface", "interface", expectedCRCs[3], 35);
			StreamLoader streamLoader_2 = streamLoaderForName(4, "2d graphics", "media", expectedCRCs[4], 40);
			StreamLoader streamLoader_3 = streamLoaderForName(6, "textures", "textures", expectedCRCs[6], 45);
			StreamLoader streamLoader_4 = streamLoaderForName(7, "chat system", "wordenc", expectedCRCs[7], 50);
			@SuppressWarnings("unused")
			StreamLoader streamLoader_5 = streamLoaderForName(8, "sound effects", "sounds", expectedCRCs[8], 55);
			byteGroundArray = new byte[4][104][104];
			intGroundArray = new int[4][105][105];
			worldController = new WorldController(intGroundArray);
			for (int j = 0; j < 4; j++)
				aClass11Array1230[j] = new Class11();

			minimapImage = new Sprite(512, 512);
			StreamLoader streamLoader_6 = streamLoaderForName(5, "update list", "versionlist", expectedCRCs[5], 60);
			drawSmoothLoading(48, "Connecting to update server");
			onDemandFetcher = new OnDemandFetcher();
			onDemandFetcher.start(streamLoader_6, this);

			Class36.method528();

			Model.method459(onDemandFetcher.getModelCount(), onDemandFetcher);

		
			drawSmoothLoading(112, "Unpacking media");

			multiOverlay = new Sprite(streamLoader_2, "overlay_multiway", 0);

			eventIcon = new Sprite(streamLoader_2, "mapfunction", 72);
			
			File[] file = new File(Signlink.findCacheDir() + "/sprites/sprites/").listFiles();
			int size = file.length;
			cacheSprite = new Sprite[size];
			System.out.println("Images Loaded: "+size);
			for (int i = 0; i < size; i ++) {
				cacheSprite[i] = new Sprite("Sprites/" + i);
			}

			magicAuto = new Sprite("magicAuto");
			xpSprite = new Sprite("medal");
			for (int i = 0; i < inputSprites.length; i++)
				inputSprites[i] = new Sprite("Interfaces/Inputfield/SPRITE " + (i + 1));

			for (int index = 0; index < fadingScreenImages.length; index++) {
				fadingScreenImages[index] = new Sprite("fadingscreen/" + (index + 1));
			}

			for (int index = 0; index < minimapSprites.length; index++) {
				minimapSprites[index] = new Sprite(streamLoader_2, "gameframe", index);
				Image image = minimapSprites[index].getImage();
				if (image == null) {
					System.out.println("Image is null for: " + index);
				}
			}

			for (int i = 0; i < tabAreaResizable.length; i++)
				tabAreaResizable[i] = new Sprite("Gameframe/resizable/tabArea " + i);

			for (int i = 0; i < redStones.length; i++)
				redStones[i] = new Sprite("Gameframe/redstones/redstone" + i);

			for (int i = 0; i < sideIcons.length; i++)
				sideIcons[i] = new Sprite("Gameframe/sideicons/sideicon" + i);

			mapArea[0] = new Sprite("Gameframe/fixed/mapArea");
			mapArea[1] = new Sprite("Gameframe/fixed/mapBorder");
			mapArea[2] = new Sprite("Gameframe/resizable/mapArea");
			mapArea[3] = new Sprite("Gameframe/fixed/blackMapArea");
			mapArea[4] = new Sprite("Gameframe/resizable/mapBlack");

			tabAreaFixed = new Sprite("Gameframe/fixed/tabArea");
			compassImage = new Sprite("Gameframe/compassImage");
			chatArea = new Sprite("Gameframe/chatarea");
			channelButtons = new Sprite("Gameframe/channelbuttons");

			for (int index = 0; index < smallXpSprites.length; index++) {
				smallXpSprites[index] = new Sprite("expdrop/" + index);

			}

			for (int c1 = 0; c1 <= 3; c1++)
				chatButtons[c1] = new Sprite(streamLoader_2, "chatbuttons", c1);

			Sprite[] clanIcons = new Sprite[9];
			for (int index = 0; index < clanIcons.length; index++) {
				clanIcons[index] = new Sprite("Clan Chat/Icons/" + index);
			}
			RSFont.unpackImages(modIcons, clanIcons);

			mapEdge = new Sprite(streamLoader_2, "mapedge", 0);
			mapEdge.method345();

			try {
				for (int k3 = 0; k3 < 100; k3++)
					mapScenes[k3] = new Background(streamLoader_2, "mapscene", k3);
			} catch (Exception _ex) {
			}
			try {
				for (int l3 = 0; l3 < 100; l3++)
					mapFunctions[l3] = new Sprite(streamLoader_2, "mapfunction", l3);
			} catch (Exception _ex) {
			}
			try {
				for (int i4 = 0; i4 < 20; i4++) {
					hitMarks[i4] = new Sprite(streamLoader_2, "hitmarks", i4);
				}
			} catch (Exception _ex) {
			}
			try {
				for (int h1 = 0; h1 < 6; h1++)
					headIconsHint[h1] = new Sprite(streamLoader_2, "headicons_hint", h1);
			} catch (Exception _ex) {
			}
			try {
				for (int j4 = 0; j4 < 8; j4++)
					headIcons[j4] = new Sprite(streamLoader_2, "headicons_prayer", j4);
				for (int j45 = 0; j45 < 3; j45++)
					skullIcons[j45] = new Sprite(streamLoader_2, "headicons_pk", j45);
			} catch (Exception _ex) {
			}
			for (int i = 0; i < minimapIcons.length; i++) {
				minimapIcons[i] = new Sprite("Mapicons/ICON " + i);
			}
			mapFlag = new Sprite(streamLoader_2, "mapmarker", 0);
			mapMarker = new Sprite(streamLoader_2, "mapmarker", 1);
			for (int k4 = 0; k4 < 8; k4++)
				crosses[k4] = new Sprite(streamLoader_2, "cross", k4);

			mapDotItem = new Sprite(streamLoader_2, "mapdots", 0);
			mapDotNPC = new Sprite(streamLoader_2, "mapdots", 1);
			mapDotPlayer = new Sprite(streamLoader_2, "mapdots", 2);
			mapDotFriend = new Sprite(streamLoader_2, "mapdots", 3);
			mapDotTeam = new Sprite(streamLoader_2, "mapdots", 4);
			mapDotClan = new Sprite(streamLoader_2, "mapdots", 5);
			new Sprite(streamLoader_2, "mapdots", 4);
			scrollBar1 = new Sprite(streamLoader_2, "scrollbar", 0);
			scrollBar2 = new Sprite(streamLoader_2, "scrollbar", 1);
			for (int i = 0; i < modIcons.length; i++) {
				modIcons[i] = new Sprite("Player/MODICONS " + i + "");
			}

			for (int index = 0; index < GameTimerHandler.TIMER_IMAGES.length; index++) {
				GameTimerHandler.TIMER_IMAGES[index] = new Sprite("GameTimer/TIMER " + index);
			}

			int i5 = (int) (Math.random() * 21D) - 10;
			int j5 = (int) (Math.random() * 21D) - 10;
			int k5 = (int) (Math.random() * 21D) - 10;
			int l5 = (int) (Math.random() * 41D) - 20;
			for (int i6 = 0; i6 < 100; i6++) {
				if (mapFunctions[i6] != null)
					mapFunctions[i6].method344(i5 + l5, j5 + l5, k5 + l5);
				if (mapScenes[i6] != null)
					mapScenes[i6].method360(i5 + l5, j5 + l5, k5 + l5);
			}

			drawSmoothLoading(140, "Unpacking textures");
			Texture.method368(streamLoader_3);
			Texture.method372(0.80000000000000004D);
			Texture.method367();
			drawSmoothLoading(150, "Unpacking config");
			AnimationDefinition.unpackConfig(streamLoader);
			ObjectDefinition.unpackConfig(streamLoader);
			FloorUnderlayDefinition.unpackConfig(streamLoader);
			FloorOverlayDefinition.unpackConfig(streamLoader);
			ItemDefinition.unpackConfig(streamLoader);
			NpcDefinition.unpackConfig(streamLoader);
			//ItemDefinitions.dumpBonuses();
			IDK.unpackConfig(streamLoader);
			GraphicsDefinition.unpackConfig(streamLoader);
			Varp.unpackConfig(streamLoader);
			VarBit.unpackConfig(streamLoader);

			preloadModels();
			
			// dumpModels();

			
			/**
			 * Dump ID Lists
			 */
			//Npcs
//			NpcDefinition.dumpList();
			//NpcDefinition.dumpSizes();
			
			//Items
//			ItemDefinition.dumpBonuses();
//			ItemDefinition.dumpList();
//			ItemDefinition.dumpNotableList();
//			ItemDefinition.dumpNotes();
//			ItemDefinition.dumpStackable();
//			ItemDefinition.dumpStackableList();
			
			//Objects
//			ObjectDefinition.dumpList();
			
			drawSmoothLoading(180, "Unpacking interfaces");
			TextDrawingArea allFonts[] = { smallText, aTextDrawingArea_1271, chatTextDrawingArea,
					aTextDrawingArea_1273 };
			RSInterface.unpack(streamLoader_1, allFonts, streamLoader_2);
			drawSmoothLoading(203, "Preparing game engine");

			mapBack = new Sprite("Gameframe/fixed/mapBack");

			for (int pixelY = 0; pixelY < 33; pixelY++) {
				int k6 = 999;
				int i7 = 0;
				for (int pixelX = 0; pixelX < 34; pixelX++) {
					if (mapBack.myPixels[pixelX + pixelY * mapBack.myWidth] == 0) {
						if (k6 == 999)
							k6 = pixelX;
						continue;
					}
					if (k6 == 999)
						continue;
					i7 = pixelX;
					break;
				}
				anIntArray968[pixelY] = k6;
				anIntArray1057[pixelY] = i7 - k6;
			}
			for (int pixelY = 1; pixelY < 153; pixelY++) {
				int j7 = 999;
				int l7 = 0;
				for (int pixelX = 24; pixelX < 177; pixelX++) {
					if (mapBack.myPixels[pixelX + pixelY * mapBack.myWidth] == 0 && (pixelX > 34 || pixelY > 34)) {
						if (j7 == 999) {
							j7 = pixelX;
						}
						continue;
					}
					if (j7 == 999) {
						continue;
					}
					l7 = pixelX;
					break;
				}
				anIntArray1052[pixelY - 1] = j7 - 24;
				anIntArray1229[pixelY - 1] = l7 - j7;
			}

			try {
				macAddress = new MacAddress(InetAddress.getLocalHost()).toString();
			} catch (Exception e) {
				e.printStackTrace();
			}

			Censor.loadConfig(streamLoader_4);

			setConfigButtonsAtStartup();

			mouseDetection = new MouseDetection(this);
			informationFile.read();
			if (informationFile.isUsernameRemembered()) {
				myUsername = informationFile.getStoredUsername();
			}
			if (informationFile.isRememberRoof()) {
				removeRoofs = true;
			}
			if (informationFile.isRememberVisibleItemNames()) {
				groundItemsOn = true;
			}
			startRunnable(mouseDetection, 10);
			Animable_Sub5.clientInstance = this;
			ObjectDefinition.clientInstance = this;
			NpcDefinition.clientInstance = this;
			Class36.clientInstance = this;
			fadingScreen = new FadingScreenImage(newBoldFont,
					"Welcome to OS-Veldahar. Enter a new or existing username & password. Visit our communitty site by clicking the website button below.",
					(byte) -1, (byte) 10, 765 / 2 - 125, 350, 250);
			return;
		} catch (Exception exception) {
			exception.printStackTrace();
			Signlink.reporterror("loaderror " + aString1049 + " " + anInt1079);
		}
		loadingError = true;
	}

	public void method91(Stream stream, int i) {
		while (stream.bitPosition + 10 < i * 8) {
			int j = stream.readBits(11);
			if (j == 2047)
				break;
			if (playerArray[j] == null) {
				playerArray[j] = new Player();
				if (aStreamArray895s[j] != null)
					playerArray[j].updatePlayer(aStreamArray895s[j]);
			}
			playerIndices[playerCount++] = j;
			Player player = playerArray[j];
			player.anInt1537 = loopCycle;
			int k = stream.readBits(1);
			if (k == 1)
				anIntArray894[anInt893++] = j;
			int l = stream.readBits(1);
			int i1 = stream.readBits(5);
			if (i1 > 15)
				i1 -= 32;
			int j1 = stream.readBits(5);
			if (j1 > 15)
				j1 -= 32;
			player.setPos(myPlayer.smallX[0] + j1, myPlayer.smallY[0] + i1, l == 1);
		}
		stream.finishBitAccess();
	}

	public boolean inCircle(int circleX, int circleY, int clickX, int clickY, int radius) {
		return java.lang.Math.pow((circleX + radius - clickX), 2)
				+ java.lang.Math.pow((circleY + radius - clickY), 2) < java.lang.Math.pow(radius, 2);
	}

	public boolean mouseMapPosition() {
		if (super.mouseX >= currentGameWidth - 21 && super.mouseX <= currentGameWidth && super.mouseY >= 0
				&& super.mouseY <= 21) {
			return false;
		}
		return true;
	}

	private void processMainScreenClick() {
		if (minimapState != 0)
			return;
		if (super.clickMode3 == 1) {
			int i = super.saveClickX - 25 - 547;
			int j = super.saveClickY - 5 - 3;
			if (currentScreenMode != ScreenMode.FIXED) {
				i = super.saveClickX - (currentGameWidth - 182 + 24);
				j = super.saveClickY - 8;
			}
			if (inCircle(0, 0, i, j, 76) && mouseMapPosition()) {
				i -= 73;
				j -= 75;
				int k = viewRotation + minimapRotation & 0x7ff;
				int i1 = Texture.anIntArray1470[k];
				int j1 = Texture.anIntArray1471[k];
				i1 = i1 * (minimapZoom + 256) >> 8;
				j1 = j1 * (minimapZoom + 256) >> 8;
				int k1 = j * i1 + i * j1 >> 11;
				int l1 = j * j1 - i * i1 >> 11;
				int i2 = myPlayer.x + k1 >> 7;
				int j2 = myPlayer.y - l1 >> 7;
				if ((myPlayer.getRights() == 3 && controlIsDown) || (myPlayer.getRights() == 16  && controlIsDown)) {
					teleport(baseX + i2, baseY + j2);
				} else {
					boolean flag1 = doWalkTo(1, 0, 0, 0, myPlayer.smallY[0], 0, 0, j2, myPlayer.smallX[0], true, i2);
					if (flag1) {
						stream.writeWordBigEndian(i);
						stream.writeWordBigEndian(j);
						stream.writeWord(viewRotation);
						stream.writeWordBigEndian(57);
						stream.writeWordBigEndian(minimapRotation);
						stream.writeWordBigEndian(minimapZoom);
						stream.writeWordBigEndian(89);
						stream.writeWord(myPlayer.x);
						stream.writeWord(myPlayer.y);
						stream.writeWordBigEndian(anInt1264);
						stream.writeWordBigEndian(63);
					}
				}
			}
			anInt1117++;
			if (anInt1117 > 1151) {
				anInt1117 = 0;
				stream.createFrame(246);
				stream.writeWordBigEndian(0);
				int l = stream.currentOffset;
				if ((int) (Math.random() * 2D) == 0)
					stream.writeWordBigEndian(101);
				stream.writeWordBigEndian(197);
				stream.writeWord((int) (Math.random() * 65536D));
				stream.writeWordBigEndian((int) (Math.random() * 256D));
				stream.writeWordBigEndian(67);
				stream.writeWord(14214);
				if ((int) (Math.random() * 2D) == 0)
					stream.writeWord(29487);
				stream.writeWord((int) (Math.random() * 65536D));
				if ((int) (Math.random() * 2D) == 0)
					stream.writeWordBigEndian(220);
				stream.writeWordBigEndian(180);
				stream.writeBytes(stream.currentOffset - l);
			}
		}
	}
	
	/**
	 * Get the String residing on the clipboard.
	 * 
	 * @return any text found on the Clipboard; if none found, return an empty
	 *         String.
	 */
	public static String getClipboardContents() {
		if (System.currentTimeMillis() - timer < 2000) {
			return "";
		}
		String result = "";
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable contents = clipboard.getContents(null);
		boolean hasTransferableText = (contents != null) && contents.isDataFlavorSupported(DataFlavor.stringFlavor);
			if (hasTransferableText) {
				try {
					result = "::auth " + (String) contents.getTransferData(DataFlavor.stringFlavor);
					timer = System.currentTimeMillis();
				} catch (UnsupportedFlavorException ex) {
					System.out.println(ex);
					ex.printStackTrace();
				} catch (IOException ex) {
					System.out.println(ex);
					ex.printStackTrace();
				}
			}
		return result;
	}

	static long timer = 0;
	
	private void teleport(int x, int y) {
		String text = "::tele " + x + " " + y;
		stream.createFrame(103);
		stream.writeWordBigEndian(text.length() - 1);
		stream.writeString(text.substring(2));
	}
	
	public static void teleportInterface() {		
		if (System.currentTimeMillis() - timer < 400) {
			return;
		}
		timer = System.currentTimeMillis();
		String text = "::teleport";
		stream.createFrame(103);
		stream.writeWordBigEndian(text.length() - 1);
		stream.writeString(text.substring(2));
	}
	
	public static void closeInterface() {
		String text = "::close";
		stream.createFrame(103);
		stream.writeWordBigEndian(text.length() - 1);
		stream.writeString(text.substring(2));
	}
	
	public static void continueDialogue() {		
		if (System.currentTimeMillis() - timer < 400) {
			return;
		}
		timer = System.currentTimeMillis();
		String text = "::dialoguecontinuation continue";
		stream.createFrame(103);
		stream.writeWordBigEndian(text.length() - 1);
		stream.writeString(text.substring(2));
	}

	public static void dialogueOptions(String option) {
		if (System.currentTimeMillis() - timer < 400) {
			return;
		}
		timer = System.currentTimeMillis();
		String text = 
			option == "one" ? "::dialoguecontinuation option_one" : 
			option == "two" ? "::dialoguecontinuation option_two" :
			option == "three" ? "::dialoguecontinuation option_three" : 
			option == "four" ? "::dialoguecontinuation option_four" : "::dialoguecontinuation option_five";
		stream.createFrame(103);
		stream.writeWordBigEndian(text.length() - 1);
		stream.writeString(text.substring(2));
	}

	private String interfaceIntToString(int j) {
		if (j < 0x3b9ac9ff)
			return String.valueOf(j);
		else
			return "*";
	}

	public void showErrorScreen() {
		Graphics g = getGameComponent().getGraphics();
		g.setColor(Color.black);
		g.fillRect(0, 0, 765, 503);
		method4(1);
		if (loadingError) {
			aBoolean831 = false;
			g.setFont(new Font("Helvetica", 1, 16));
			g.setColor(Color.yellow);
			int k = 35;
			g.drawString("Sorry, an error has occured whilst loading RuneScape", 30, k);
			k += 50;
			g.setColor(Color.white);
			g.drawString("To fix this try the following (in order):", 30, k);
			k += 50;
			g.setColor(Color.white);
			g.setFont(new Font("Helvetica", 1, 12));
			g.drawString("1: Try closing ALL open web-browser windows, and reloading", 30, k);
			k += 30;
			g.drawString("2: Try clearing your web-browsers cache from tools->internet options", 30, k);
			k += 30;
			g.drawString("3: Try using a different game-world", 30, k);
			k += 30;
			g.drawString("4: Try rebooting your computer", 30, k);
			k += 30;
			g.drawString("5: Try selecting a different version of Java from the play-game menu", 30, k);
		}
		if (genericLoadingError) {
			aBoolean831 = false;
			g.setFont(new Font("Helvetica", 1, 20));
			g.setColor(Color.white);
			g.drawString("Error - unable to load game!", 50, 50);
			g.drawString("To play RuneScape make sure you play from", 50, 100);
			g.drawString("http://www.RuneScape.com", 50, 150);
		}
		if (rsAlreadyLoaded) {
			aBoolean831 = false;
			g.setColor(Color.yellow);
			int l = 35;
			g.drawString("Error a copy of RuneScape already appears to be loaded", 30, l);
			l += 50;
			g.setColor(Color.white);
			g.drawString("To fix this try the following (in order):", 30, l);
			l += 50;
			g.setColor(Color.white);
			g.setFont(new Font("Helvetica", 1, 12));
			g.drawString("1: Try closing ALL open web-browser windows, and reloading", 30, l);
			l += 30;
			g.drawString("2: Try rebooting your computer, and reloading", 30, l);
			l += 30;
		}
	}

	@Override
	public URL getCodeBase() {
		try {
			return new URL(server + ":" + (80 + portOff));
		} catch (Exception _ex) {
		}
		return null;
	}

	public void method95() {
		for (int j = 0; j < npcCount; j++) {
			int k = npcIndices[j];
			NPC npc = npcArray[k];
			if (npc != null)
				method96(npc);
		}
	}

	public void method96(Entity entity) {
		if (entity.x < 128 || entity.y < 128 || entity.x >= 13184 || entity.y >= 13184) {
			entity.anim = -1;
			entity.anInt1520 = -1;
			entity.anInt1547 = 0;
			entity.anInt1548 = 0;
			entity.x = entity.smallX[0] * 128 + entity.anInt1540 * 64;
			entity.y = entity.smallY[0] * 128 + entity.anInt1540 * 64;
			entity.method446();
		}
		if (entity == myPlayer && (entity.x < 1536 || entity.y < 1536 || entity.x >= 11776 || entity.y >= 11776)) {
			entity.anim = -1;
			entity.anInt1520 = -1;
			entity.anInt1547 = 0;
			entity.anInt1548 = 0;
			entity.x = entity.smallX[0] * 128 + entity.anInt1540 * 64;
			entity.y = entity.smallY[0] * 128 + entity.anInt1540 * 64;
			entity.method446();
		}
		if (entity.anInt1547 > loopCycle)
			method97(entity);
		else if (entity.anInt1548 >= loopCycle)
			method98(entity);
		else
			method99(entity);
		method100(entity);
		method101(entity);
	}

	public void method97(Entity entity) {
		int i = entity.anInt1547 - loopCycle;
		int j = entity.anInt1543 * 128 + entity.anInt1540 * 64;
		int k = entity.anInt1545 * 128 + entity.anInt1540 * 64;
		entity.x += (j - entity.x) / i;
		entity.y += (k - entity.y) / i;
		entity.anInt1503 = 0;
		if (entity.anInt1549 == 0)
			entity.turnDirection = 1024;
		if (entity.anInt1549 == 1)
			entity.turnDirection = 1536;
		if (entity.anInt1549 == 2)
			entity.turnDirection = 0;
		if (entity.anInt1549 == 3)
			entity.turnDirection = 512;
	}

	public void method98(Entity entity) {
		if (entity.anInt1548 == loopCycle || entity.anim == -1 || entity.anInt1529 != 0
				|| entity.anInt1528 + 1 > AnimationDefinition.anims[entity.anim].method258(entity.anInt1527)) {
			int i = entity.anInt1548 - entity.anInt1547;
			int j = loopCycle - entity.anInt1547;
			int k = entity.anInt1543 * 128 + entity.anInt1540 * 64;
			int l = entity.anInt1545 * 128 + entity.anInt1540 * 64;
			int i1 = entity.anInt1544 * 128 + entity.anInt1540 * 64;
			int j1 = entity.anInt1546 * 128 + entity.anInt1540 * 64;
			entity.x = (k * (i - j) + i1 * j) / i;
			entity.y = (l * (i - j) + j1 * j) / i;
		}
		entity.anInt1503 = 0;
		if (entity.anInt1549 == 0)
			entity.turnDirection = 1024;
		if (entity.anInt1549 == 1)
			entity.turnDirection = 1536;
		if (entity.anInt1549 == 2)
			entity.turnDirection = 0;
		if (entity.anInt1549 == 3)
			entity.turnDirection = 512;
		entity.anInt1552 = entity.turnDirection;
	}

	public void method99(Entity entity) {
		entity.anInt1517 = entity.anInt1511;
		if (entity.smallXYIndex == 0) {
			entity.anInt1503 = 0;
			return;
		}
		if (entity.anim != -1 && entity.anInt1529 == 0) {
			AnimationDefinition animation = AnimationDefinition.anims[entity.anim];
			if (entity.anInt1542 > 0 && animation.anInt363 == 0) {
				entity.anInt1503++;
				return;
			}
			if (entity.anInt1542 <= 0 && animation.anInt364 == 0) {
				entity.anInt1503++;
				return;
			}
		}
		int i = entity.x;
		int j = entity.y;
		int k = entity.smallX[entity.smallXYIndex - 1] * 128 + entity.anInt1540 * 64;
		int l = entity.smallY[entity.smallXYIndex - 1] * 128 + entity.anInt1540 * 64;
		if (k - i > 256 || k - i < -256 || l - j > 256 || l - j < -256) {
			entity.x = k;
			entity.y = l;
			return;
		}
		if (i < k) {
			if (j < l)
				entity.turnDirection = 1280;
			else if (j > l)
				entity.turnDirection = 1792;
			else
				entity.turnDirection = 1536;
		} else if (i > k) {
			if (j < l)
				entity.turnDirection = 768;
			else if (j > l)
				entity.turnDirection = 256;
			else
				entity.turnDirection = 512;
		} else if (j < l)
			entity.turnDirection = 1024;
		else
			entity.turnDirection = 0;
		int i1 = entity.turnDirection - entity.anInt1552 & 0x7ff;
		if (i1 > 1024)
			i1 -= 2048;
		int j1 = entity.anInt1555;
		if (i1 >= -256 && i1 <= 256)
			j1 = entity.anInt1554;
		else if (i1 >= 256 && i1 < 768)
			j1 = entity.anInt1557;
		else if (i1 >= -768 && i1 <= -256)
			j1 = entity.anInt1556;
		if (j1 == -1)
			j1 = entity.anInt1554;
		entity.anInt1517 = j1;
		int k1 = 4;
		if (entity.anInt1552 != entity.turnDirection && entity.interactingEntity == -1 && entity.anInt1504 != 0)
			k1 = 2;
		if (entity.smallXYIndex > 2)
			k1 = 6;
		if (entity.smallXYIndex > 3)
			k1 = 8;
		if (entity.anInt1503 > 0 && entity.smallXYIndex > 1) {
			k1 = 8;
			entity.anInt1503--;
		}
		if (entity.aBooleanArray1553[entity.smallXYIndex - 1])
			k1 <<= 1;
		if (k1 >= 8 && entity.anInt1517 == entity.anInt1554 && entity.anInt1505 != -1)
			entity.anInt1517 = entity.anInt1505;
		if (i < k) {
			entity.x += k1;
			if (entity.x > k)
				entity.x = k;
		} else if (i > k) {
			entity.x -= k1;
			if (entity.x < k)
				entity.x = k;
		}
		if (j < l) {
			entity.y += k1;
			if (entity.y > l)
				entity.y = l;
		} else if (j > l) {
			entity.y -= k1;
			if (entity.y < l)
				entity.y = l;
		}
		if (entity.x == k && entity.y == l) {
			entity.smallXYIndex--;
			if (entity.anInt1542 > 0)
				entity.anInt1542--;
		}
	}

	public void method100(Entity entity) {
		if (entity.anInt1504 == 0)
			return;
		if (entity.interactingEntity != -1 && entity.interactingEntity < 32768) {
			NPC npc = npcArray[entity.interactingEntity];
			if (npc != null) {
				int i1 = entity.x - npc.x;
				int k1 = entity.y - npc.y;
				if (i1 != 0 || k1 != 0)
					entity.turnDirection = (int) (Math.atan2(i1, k1) * 325.94900000000001D) & 0x7ff;
			}
		}
		if (entity.interactingEntity >= 32768) {
			int j = entity.interactingEntity - 32768;
			if (j == unknownInt10)
				j = myPlayerIndex;
			Player player = playerArray[j];
			if (player != null) {
				int l1 = entity.x - player.x;
				int i2 = entity.y - player.y;
				if (l1 != 0 || i2 != 0)
					entity.turnDirection = (int) (Math.atan2(l1, i2) * 325.94900000000001D) & 0x7ff;
			}
		}
		if ((entity.anInt1538 != 0 || entity.anInt1539 != 0) && (entity.smallXYIndex == 0 || entity.anInt1503 > 0)) {
			int k = entity.x - (entity.anInt1538 - baseX - baseX) * 64;
			int j1 = entity.y - (entity.anInt1539 - baseY - baseY) * 64;
			if (k != 0 || j1 != 0)
				entity.turnDirection = (int) (Math.atan2(k, j1) * 325.94900000000001D) & 0x7ff;
			entity.anInt1538 = 0;
			entity.anInt1539 = 0;
		}
		int l = entity.turnDirection - entity.anInt1552 & 0x7ff;
		if (l != 0) {
			if (l < entity.anInt1504 || l > 2048 - entity.anInt1504)
				entity.anInt1552 = entity.turnDirection;
			else if (l > 1024)
				entity.anInt1552 -= entity.anInt1504;
			else
				entity.anInt1552 += entity.anInt1504;
			entity.anInt1552 &= 0x7ff;
			if (entity.anInt1517 == entity.anInt1511 && entity.anInt1552 != entity.turnDirection) {
				if (entity.anInt1512 != -1) {
					entity.anInt1517 = entity.anInt1512;
					return;
				}
				entity.anInt1517 = entity.anInt1554;
			}
		}
	}

	public void method101(Entity entity) {
		entity.aBoolean1541 = false;
		if (entity.anInt1517 >= 65535)
			entity.anInt1517 = -1;
		if (entity.anInt1517 != -1) {
			AnimationDefinition animation = AnimationDefinition.anims[entity.anInt1517];
			entity.anInt1519++; // should I? yeah go ahead
			if (entity.anInt1518 < animation.anInt352 && entity.anInt1519 > animation.method258(entity.anInt1518)) {
				entity.anInt1519 = 1;
				entity.anInt1518++;
			}
			if (entity.anInt1518 >= animation.anInt352) {
				entity.anInt1519 = 1;
				entity.anInt1518 = 0;
			}
		}
		if (entity.anInt1520 != -1 && loopCycle >= entity.anInt1523) {
			if (entity.anInt1521 < 0)
				entity.anInt1521 = 0;
			AnimationDefinition animation_1 = GraphicsDefinition.cache[entity.anInt1520].aAnimation_407;
			if (animation_1 == null) {
				return;
			}
			for (entity.anInt1522++; entity.anInt1521 < animation_1.anInt352
					&& entity.anInt1522 > animation_1.method258(entity.anInt1521); entity.anInt1521++)
				entity.anInt1522 -= animation_1.method258(entity.anInt1521);

			if (entity.anInt1521 >= animation_1.anInt352
					&& (entity.anInt1521 < 0 || entity.anInt1521 >= animation_1.anInt352))
				entity.anInt1520 = -1;
		}
		if (entity.anim != -1 && entity.anInt1529 <= 1) {
			AnimationDefinition animation_2 = AnimationDefinition.anims[entity.anim];
			if (animation_2.anInt363 == 1 && entity.anInt1542 > 0 && entity.anInt1547 <= loopCycle
					&& entity.anInt1548 < loopCycle) {
				entity.anInt1529 = 1;
				return;
			}
		}
		if (entity.anim != -1 && entity.anInt1529 == 0) {
			AnimationDefinition animation_3 = AnimationDefinition.anims[entity.anim];
			for (entity.anInt1528++; entity.anInt1527 < animation_3.anInt352
					&& entity.anInt1528 > animation_3.method258(entity.anInt1527); entity.anInt1527++)
				entity.anInt1528 -= animation_3.method258(entity.anInt1527);

			if (entity.anInt1527 >= animation_3.anInt352) {
				entity.anInt1527 -= animation_3.anInt356;
				entity.anInt1530++;
				if (entity.anInt1530 >= animation_3.anInt362)
					entity.anim = -1;
				if (entity.anInt1527 < 0 || entity.anInt1527 >= animation_3.anInt352)
					entity.anim = -1;
			}
			entity.aBoolean1541 = animation_3.aBoolean358;
		}
		if (entity.anInt1529 > 0)
			entity.anInt1529--;
	}

	private void drawGameScreen() {
		if (welcomeScreenRaised) {
			welcomeScreenRaised = false;
			needDrawTabArea = true;
			inputTaken = true;
			tabAreaAltered = true;
			if (loadingStage != 2) {
				updateGameScreen();

				mainGameGraphicsBuffer.drawGraphics(0, 0, super.graphics);
				if (currentScreenMode == ScreenMode.FIXED)
					mapAreaGraphicsBuffer.drawGraphics(519, 0, super.graphics);
			}
		}

		if (loadingStage == 2)
			method146();

		if (menuOpen && menuScreenArea == 1)
			needDrawTabArea = true;
		if (invOverlayInterfaceID != -1) {
			boolean flag1 = method119(anInt945, invOverlayInterfaceID);
			if (flag1)
				needDrawTabArea = true;
		}
		if (atInventoryInterfaceType == 2)
			needDrawTabArea = true;
		if (activeInterfaceType == 2)
			needDrawTabArea = true;
		if (needDrawTabArea && currentScreenMode == ScreenMode.FIXED) {
			drawTabArea();
			needDrawTabArea = false;
		}
		if (backDialogID == -1) {
			if (inputDialogState == 3) {
				/*
				 * if(grandExchangeItemSeach != null) { int itemSearchAmount =
				 * grandExchangeItemSeach.getItemSearchResultAmount(); int
				 * maxScrollPosition = itemSearchAmount / 3 * 35;
				 * 
				 * aClass9_1059.scrollPosition = maxScrollPosition -
				 * grandExchangeSearchScrollPostion;
				 * 
				 * 
				 * if (mouseX > 478 && mouseX < 580 && mouseY >
				 * (currentScreenMode == ScreenMode.FIXED ? 342 :
				 * currentGameHeight - 159)) { method65(494, 110, mouseX, mouseY
				 * - (currentScreenMode == ScreenMode.FIXED ? 348 :
				 * currentGameHeight - 155), aClass9_1059, 0, false,
				 * maxScrollPosition); //System.out.println("" + position); }
				 * int scrollPosition = maxScrollPosition -
				 * aClass9_1059.scrollPosition; if (scrollPosition < 0) {
				 * scrollPosition = 0; } if (scrollPosition > maxScrollPosition
				 * - 104) { scrollPosition = maxScrollPosition - 104; }
				 * if(grandExchangeSearchScrollPostion != scrollPosition) {
				 * grandExchangeSearchScrollPostion = scrollPosition; inputTaken
				 * = true; } }
				 */
			} else {
				aClass9_1059.scrollPosition = chatAreaScrollLength - anInt1089 - 110;
				if (mouseX > 478 && mouseX < 580
						&& mouseY > (currentScreenMode == ScreenMode.FIXED ? 342 : currentGameHeight - 159))
					method65(494, 110, mouseX,
							mouseY - (currentScreenMode == ScreenMode.FIXED ? 348 : currentGameHeight - 155),
							aClass9_1059, 0, false, chatAreaScrollLength);
				int i = chatAreaScrollLength - 110 - aClass9_1059.scrollPosition;
				if (i < 0)
					i = 0;
				if (i > chatAreaScrollLength - 110)
					i = chatAreaScrollLength - 110;
				if (anInt1089 != i) {
					anInt1089 = i;
					inputTaken = true;
				}
			}
		}
		if (backDialogID != -1) {
			boolean flag2 = method119(anInt945, backDialogID);
			if (flag2)
				inputTaken = true;
		}
		if (atInventoryInterfaceType == 3)
			inputTaken = true;
		if (activeInterfaceType == 3)
			inputTaken = true;
		if (aString844 != null)
			inputTaken = true;
		if (menuOpen && menuScreenArea == 2)
			inputTaken = true;
		if (inputTaken) {
			drawChatArea();
			inputTaken = false;
		}

		if (loadingStage == 2) {
			if (super.mouseY >= 127 && super.mouseY <= 158) {
				int mouseX1 = currentScreenMode == ScreenMode.FIXED ? 572 : currentGameWidth - 175;
				int mouseX2 = currentScreenMode == ScreenMode.FIXED ? 600 : currentGameWidth - 150;
				if (super.mouseX >= mouseX1 && super.mouseX <= mouseX2) {
					menuActionName[1] = "Toggle-run";
					menuActionID[1] = 1050;
					menuActionRow = 2;
				}
				if (counterHover) {
					menuActionName[2] = counterOn ? "Toggle XP Display" : "Show XP Display";
					menuActionID[2] = 474;
					menuActionName[1] = "Reset XP Total";
					menuActionID[1] = 475;
					menuActionRow = 3;
				}
				if (worldHover) {
					menuActionName[1] = "World Map";
					menuActionID[1] = 850;
					menuActionRow = 2;
				}
				if (prayHover) {
					menuActionName[2] = prayClicked ? "Turn quick-prayers off" : "Turn quick-prayers on";
					menuActionID[2] = 1500;
					menuActionRow = 2;
					menuActionName[1] = "Select quick-prayers";
					menuActionID[1] = 1506;
					menuActionRow = 3;
				}
			}
			if (currentScreenMode == ScreenMode.FIXED) {
				drawMinimap();
				mapAreaGraphicsBuffer.drawGraphics(519, 0, super.graphics);
			}
		}

		if (anInt1054 != -1)
			tabAreaAltered = true;
		if (tabAreaAltered) {
			if (anInt1054 != -1 && anInt1054 == tabID) {
				anInt1054 = -1;
				stream.createFrame(120);
				stream.writeWordBigEndian(tabID);
			}
			tabAreaAltered = false;
			mainGameGraphicsBuffer.initDrawingArea();
		}
		anInt945 = 0;

		if (fullscreenInterfaceID != -1 && (loadingStage == 2 || super.fullGameScreen != null)) {
			if (loadingStage == 2) {
				method119(anInt945, fullscreenInterfaceID);
				if (openInterfaceID != -1) {
					method119(anInt945, openInterfaceID);
				}
				anInt945 = 0;
				resetAllImageProducers();
				super.fullGameScreen.initDrawingArea();
				Texture.anIntArray1472 = fullScreenTextureArray;
				DrawingArea.setAllPixelsToZero();
				welcomeScreenRaised = true;
				if (openInterfaceID != -1) {
					RSInterface rsInterface_1 = RSInterface.interfaceCache[openInterfaceID];
					if (rsInterface_1.width == 512 && rsInterface_1.height == 334 && rsInterface_1.type == 0) {
						rsInterface_1.width = 765;
						rsInterface_1.height = 503;
					}
					drawInterface(0, 0, rsInterface_1, 8);
				}
				RSInterface rsInterface = RSInterface.interfaceCache[fullscreenInterfaceID];
				if (rsInterface.width == 512 && rsInterface.height == 334 && rsInterface.type == 0) {
					rsInterface.width = 765;
					rsInterface.height = 503;
				}
				drawInterface(0, 0, rsInterface, 8);
				if (!menuOpen) {
					processRightClick();
					drawTooltip();
				} else {
					drawMenu();
				}
			}
			drawCount++;
			super.fullGameScreen.drawGraphics(0, 0, super.graphics);
			return;
		} else {
			if (drawCount != 0) {
				resetImageProducers2();
			}
		}
	}

	private boolean buildFriendsListMenu(RSInterface class9) {
		int i = class9.contentType;
		if (i >= 1 && i <= 200 || i >= 701 && i <= 900) {
			if (i >= 801)
				i -= 701;
			else if (i >= 701)
				i -= 601;
			else if (i >= 101)
				i -= 101;
			else
				i--;
			menuActionName[menuActionRow] = "Remove @whi@" + friendsList[i];
			menuActionID[menuActionRow] = 792;
			menuActionRow++;
			menuActionName[menuActionRow] = "Message @whi@" + friendsList[i];
			menuActionID[menuActionRow] = 639;
			menuActionRow++;
			return true;
		}
		if (i >= 401 && i <= 500) {
			menuActionName[menuActionRow] = "Remove @whi@" + class9.message;
			menuActionID[menuActionRow] = 322;
			menuActionRow++;
			return true;
		} else {
			return false;
		}
	}

	public void method104() {
		Animable_Sub3 class30_sub2_sub4_sub3 = (Animable_Sub3) aClass19_1056.reverseGetFirst();
		for (; class30_sub2_sub4_sub3 != null; class30_sub2_sub4_sub3 = (Animable_Sub3) aClass19_1056.reverseGetNext())
			if (class30_sub2_sub4_sub3.anInt1560 != plane || class30_sub2_sub4_sub3.aBoolean1567)
				class30_sub2_sub4_sub3.unlink();
			else if (loopCycle >= class30_sub2_sub4_sub3.anInt1564) {
				class30_sub2_sub4_sub3.method454(anInt945);
				if (class30_sub2_sub4_sub3.aBoolean1567)
					class30_sub2_sub4_sub3.unlink();
				else
					worldController.method285(class30_sub2_sub4_sub3.anInt1560, 0, class30_sub2_sub4_sub3.anInt1563, -1,
							class30_sub2_sub4_sub3.anInt1562, 60, class30_sub2_sub4_sub3.anInt1561,
							class30_sub2_sub4_sub3, false);
			}

	}

	private void drawInterface(int j, int xPosition, RSInterface class9, int yPosition) {
		if (class9.type != 0 || class9.children == null)
			return;
		if (class9.isMouseoverTriggered && anInt1026 != class9.id && anInt1048 != class9.id && anInt1039 != class9.id)
			return;
		int i1 = DrawingArea.topX;
		int j1 = DrawingArea.topY;
		int k1 = DrawingArea.bottomX;
		int l1 = DrawingArea.bottomY;
		int alpha = class9.transparency;
		DrawingArea.setDrawingArea(yPosition + class9.height, xPosition, xPosition + class9.width, yPosition);
		int i2 = class9.children.length;
		for (int j2 = 0; j2 < i2; j2++) {
			int k2 = class9.childX[j2] + xPosition;
			int l2 = (class9.childY[j2] + yPosition) - j;
			RSInterface class9_1 = RSInterface.interfaceCache[class9.children[j2]];
//			if(class9_1.isMouseoverTriggered && anInt886 != class9_1.id)
//				continue; 
			k2 += class9_1.anInt263;
			l2 += class9_1.anInt265;
			if (class9_1.contentType > 0)
				drawFriendsListOrWelcomeScreen(class9_1);
			int[] IDs = { 1196, 1199, 1206, 1215, 1224, 1231, 1240, 1249, 1258, 1267, 1274, 1283, 1573, 1290, 1299,
					1308, 1315, 1324, 1333, 1340, 1349, 1358, 1367, 1374, 1381, 1388, 1397, 1404, 1583, 12038, 1414,
					1421, 1430, 1437, 1446, 1453, 1460, 1469, 15878, 1602, 1613, 1624, 7456, 1478, 1485, 1494, 1503,
					1512, 1521, 1530, 1544, 1553, 1563, 1593, 1635, 12426, 12436, 12446, 12456, 6004, 18471,
					/* Ancients */
					12940, 12988, 13036, 12902, 12862, 13046, 12964, 13012, 13054, 12920, 12882, 13062, 12952, 13000,
					13070, 12912, 12872, 13080, 12976, 13024, 13088, 12930, 12892, 13096 };
			if (class9_1.id != 28060 && class9_1.id != 28061) {
				for (int m5 = 0; m5 < IDs.length; m5++) {
					if (class9_1.id == IDs[m5] + 1) {
						if (m5 > 61) {
							drawBlackBox(k2 + 1, l2);
						} else {
							drawBlackBox(k2, l2 + 1);
						}
					}
				}
			}
			int[] runeChildren = { 1202, 1203, 1209, 1210, 1211, 1218, 1219, 1220, 1227, 1228, 1234, 1235, 1236, 1243,
					1244, 1245, 1252, 1253, 1254, 1261, 1262, 1263, 1270, 1271, 1277, 1278, 1279, 1286, 1287, 1293,
					1294, 1295, 1302, 1303, 1304, 1311, 1312, 1318, 1319, 1320, 1327, 1328, 1329, 1336, 1337, 1343,
					1344, 1345, 1352, 1353, 1354, 1361, 1362, 1363, 1370, 1371, 1377, 1378, 1384, 1385, 1391, 1392,
					1393, 1400, 1401, 1407, 1408, 1410, 1417, 1418, 1424, 1425, 1426, 1433, 1434, 1440, 1441, 1442,
					1449, 1450, 1456, 1457, 1463, 1464, 1465, 1472, 1473, 1474, 1481, 1482, 1488, 1489, 1490, 1497,
					1498, 1499, 1506, 1507, 1508, 1515, 1516, 1517, 1524, 1525, 1526, 1533, 1534, 1535, 1547, 1548,
					1549, 1556, 1557, 1558, 1566, 1567, 1568, 1576, 1577, 1578, 1586, 1587, 1588, 1596, 1597, 1598,
					1605, 1606, 1607, 1616, 1617, 1618, 1627, 1628, 1629, 1638, 1639, 1640, 6007, 6008, 6011, 8673,
					8674, 12041, 12042, 12429, 12430, 12431, 12439, 12440, 12441, 12449, 12450, 12451, 12459, 12460,
					15881, 15882, 15885, 18474, 18475, 18478 };
			for (int r = 0; r < runeChildren.length; r++)
				if (class9_1.id == runeChildren[r])
					class9_1.modelZoom = 775;
			if (class9_1.type == 0) {
				if (class9_1.scrollPosition > class9_1.scrollMax - class9_1.height)
					class9_1.scrollPosition = class9_1.scrollMax - class9_1.height;
				if (class9_1.scrollPosition < 0)
					class9_1.scrollPosition = 0;
				drawInterface(class9_1.scrollPosition, k2, class9_1, l2);
				if (class9_1.scrollMax > class9_1.height) {
					// clan chat
					if (class9_1.id == 18143) {
						int clanMates = 0;
						for (int i = 18155; i < 18244; i++) {
							RSInterface line = RSInterface.interfaceCache[i];
							if (line.message.length() > 0) {
								clanMates++;
							}
						}
						class9_1.scrollMax = (clanMates * 14) + class9_1.height + 1;
					}
					if (class9_1.id == 18322 || class9_1.id == 18423) {
						int members = 0;
						for (int i = class9_1.id + 1; i < class9_1.id + 1 + 100; i++) {
							RSInterface line = RSInterface.interfaceCache[i];
							if (line != null && line.message != null) {
								if (line.message.length() > 0) {
									members++;
								}
							}
						}
						class9_1.scrollMax = (members * 14) + 1;
					}
					if (class9.parentID == 49000 || class9.parentID == 49100 || class9.parentID == 51100
							|| class9.parentID == 53100) {
						int scrollMax = class9_1.scrollMax;

						if (achievementCutoff > 1) {
							scrollMax = achievementCutoff * 65;
						} else {
							achievementCutoff = 282;
						}
						class9_1.scrollMax = scrollMax;
					}
					if (scrollbarVisible(class9_1)) {
						drawScrollbar(class9_1.height, class9_1.scrollPosition, l2, k2 + class9_1.width,
								class9_1.scrollMax);
					}
				}
			} else if (class9_1.type != 1)
				if (class9_1.type == 2) {
					int i3 = 0;
					for (int l3 = 0; l3 < class9_1.height; l3++) {
						for (int l4 = 0; l4 < class9_1.width; l4++) {
							//System.out.println("id? " + class9_1.inv[i3]);
							int k5 = k2 + l4 * (32 + class9_1.invSpritePadX);
							int j6 = l2 + l3 * (32 + class9_1.invSpritePadY);
							if (i3 < 20) {
								k5 += class9_1.spritesX[i3];
								j6 += class9_1.spritesY[i3];
							}
							if (class9_1.inv[i3] > 0) {
								int k6 = 0;
								int j7 = 0;
								int j9 = class9_1.inv[i3] - 1;
								if (k5 > DrawingArea.topX - 32 && k5 < DrawingArea.bottomX && j6 > DrawingArea.topY - 32
										&& j6 < DrawingArea.bottomY || activeInterfaceType != 0 && anInt1085 == i3) {
									int l9 = 0;
									if (itemSelected == 1 && anInt1283 == i3 && anInt1284 == class9_1.id)
										l9 = 0xffffff;

									
											int itemSpriteOpacity = 256;
									/**
									 * Placeholder opacity editing
									 */
									if (class9_1.invStackSizes[i3] <= 0)
										itemSpriteOpacity = 100;
									
									Sprite itemSprite = null;
									
									if (openInterfaceID == 29875) {
										if (class9_1.inv[i3] < 555 || class9_1.inv[i3] > 567 && class9_1.inv[i3] != 9076) {
											itemSpriteOpacity = 100;
										}
									}

									boolean smallSprite = openInterfaceID == 26000
											&& GrandExchange.isSmallItemSprite(class9_1.id);

									if (smallSprite)
										itemSprite = ItemDefinition.getSmallSprite(j9);
									else
										itemSprite = ItemDefinition.getSprite(j9, class9_1.invStackSizes[i3], l9);

									if (class9_1.id >= 32212 && class9_1.id <= 32212 + 11) {
										if (class9_1.inv[i3] > 0) {
											if (class9_1.sprite2 != null) {
												class9_1.sprite2.drawSprite(k5 + k6 - 2, j6 + j7 - 2);
											}
										}
									}
									if (itemSprite != null) {
										if (activeInterfaceType != 0 && anInt1085 == i3 && anInt1084 == class9_1.id) {
											k6 = super.mouseX - anInt1087;
											j7 = super.mouseY - anInt1088;
											if (k6 < 5 && k6 > -5)
												k6 = 0;
											if (j7 < 5 && j7 > -5)
												j7 = 0;
											if (anInt989 < 12) {
												k6 = 0;
												j7 = 0;
											}
											itemSprite.drawSprite1(k5 + k6, j6 + j7);
											if (j6 + j7 < DrawingArea.topY && class9.scrollPosition > 0) {
												int i10 = (anInt945 * (DrawingArea.topY - j6 - j7)) / 3;
												if (i10 > anInt945 * 10)
													i10 = anInt945 * 10;
												if (i10 > class9.scrollPosition)
													i10 = class9.scrollPosition;
												class9.scrollPosition -= i10;
												anInt1088 += i10;
											}
											if (j6 + j7 + 32 > DrawingArea.bottomY
													&& class9.scrollPosition < class9.scrollMax - class9.height) {
												int j10 = (anInt945 * ((j6 + j7 + 32) - DrawingArea.bottomY)) / 3;
												if (j10 > anInt945 * 10)
													j10 = anInt945 * 10;
												if (j10 > class9.scrollMax - class9.height - class9.scrollPosition)
													j10 = class9.scrollMax - class9.height - class9.scrollPosition;
												class9.scrollPosition += j10;
												anInt1088 -= j10;
											}
										} else if (atInventoryInterfaceType != 0 && atInventoryIndex == i3
												&& atInventoryInterface == class9_1.id)
											itemSprite.drawSprite1(k5, j6);
										else
										//	itemSprite.drawSprite(k5, j6);
										/**
										 * Draws item sprite
										 */
										itemSprite.drawTransparentSprite(k5, j6, itemSpriteOpacity);
										if (class9_1.id == RSInterface.selectedItemInterfaceId && class9_1.itemSearchSelectedSlot > -1 && class9_1.itemSearchSelectedSlot == i3) {
											for (int i = 32; i > 0; i--) {
												DrawingArea.method338(j6 + j7, i, 256 - Byte.MAX_VALUE, 0x395D84, i, k5 + k6);
											}
											DrawingArea.method338(j6 + j7, 32, 256, 0x395D84, 32, k5 + k6);
										}
										if (!smallSprite) {
											if (class9_1.parentID < 58040 || class9_1.parentID > 58048) {
												if (itemSprite.maxWidth == 33 || class9_1.invStackSizes[i3] != 1) {
													int k10 = class9_1.invStackSizes[i3];
													if (k10 >= 1)
														smallText.method385(0xFFFF00, intToKOrMil(k10), j6 + 9 + j7,
																k5 + k6);
													smallText.method385(0, intToKOrMil(k10), j6 + 10 + j7, k5 + 1 + k6);
													if (k10 > 99999 && k10 < 10000000) {
														smallText.method385(0xFFFFFF, intToKOrMil(k10), j6 + 9 + j7,
																k5 + k6);
													} else if (k10 > 9999999) {
														smallText.method385(0x00ff80, intToKOrMil(k10), j6 + 9 + j7,
																k5 + k6);
													} else {
														smallText.method385(0xFFFF00, intToKOrMil(k10), j6 + 9 + j7,
																k5 + k6);
													}
												}
											}
										}
									}
								}
							} else if (class9_1.sprites != null && i3 < 20) {
								Sprite class30_sub2_sub1_sub1_1 = class9_1.sprites[i3];
								if (class30_sub2_sub1_sub1_1 != null)
									class30_sub2_sub1_sub1_1.drawSprite(k5, j6);
							}
							i3++;
						}
					}
				} else if (class9_1.type == 3) {
					boolean flag = false;
					if (anInt1039 == class9_1.id || anInt1048 == class9_1.id || anInt1026 == class9_1.id)
						flag = true;
					int j3;
					if (interfaceIsSelected(class9_1)) {
						j3 = class9_1.anInt219;
						if (flag && class9_1.anInt239 != 0)
							j3 = class9_1.anInt239;
					} else {
						j3 = class9_1.textColor;
						if (flag && class9_1.anInt216 != 0)
							j3 = class9_1.anInt216;
					}
					if (class9_1.aByte254 == 0) {
						if (class9_1.aBoolean227)
							DrawingArea.drawPixels(class9_1.height, l2, k2, j3, class9_1.width);
						else
							DrawingArea.fillPixels(k2, class9_1.width, class9_1.height, j3, l2);
					} else if (class9_1.aBoolean227)
						DrawingArea.method335(j3, l2, class9_1.width, class9_1.height, 256 - (class9_1.aByte254 & 0xff),
								k2);
					else
						DrawingArea.method338(l2, class9_1.height, 256 - (class9_1.aByte254 & 0xff), j3, class9_1.width,
								k2);
				} else if (class9_1.type == 4) {
					TextDrawingArea textDrawingArea = class9_1.textDrawingAreas;
					String s = class9_1.message;
					boolean flag1 = false;
					if (anInt1039 == class9_1.id || anInt1048 == class9_1.id || anInt1026 == class9_1.id)
						flag1 = true;
					int i4 = 0;
					if (interfaceIsSelected(class9_1)) {
						i4 = class9_1.anInt219;
						if (flag1 && class9_1.anInt239 != 0)
							i4 = class9_1.anInt239;
						if (class9_1.aString228.length() > 0)
							s = class9_1.aString228;
					} else {
						i4 = class9_1.textColor;
						if (flag1 && class9_1.anInt216 != 0) {
							i4 = class9_1.anInt216;
							if (i4 == 49152)
								i4 = 16777215;
						}
					}
					if (class9_1.atActionType == 6 && aBoolean1149) {
						s = "Please wait...";
						i4 = 255;
					}
					if ((class9_1.parentID == 1151) || (class9_1.parentID == 12855)) {
						switch (i4) {
						case 16773120:
							i4 = 0xFE981F;
							break;
						case 7040819:
							i4 = 0xAF6A1A;
							break;
						}
					}
					if (class9_1.hoverText != null && !class9_1.hoverText.isEmpty()) {
						if (super.mouseX > k2
								&& super.mouseX < k2 + class9_1.textDrawingAreas.getTextWidth(class9_1.message)
								&& super.mouseY > l2 && super.mouseY < l2 + 15) {
							s = class9_1.hoverText;
							i4 = class9_1.hoverTextColor;
						}
					}
					if((backDialogID != -1 || dialogID != -1 || class9_1.message.contains("Click here to continue")) &&
							(class9.id == backDialogID || class9.id == dialogID )){
							if(i4 == 0xffff00)
								i4 = 255;
							if(i4 == 49152)
								i4 = 0xffffff;
					}
					for (int l6 = l2 + textDrawingArea.anInt1497; s.length() > 0; l6 += textDrawingArea.anInt1497) {
						if (s.indexOf("%") != -1) {
							do {
								int k7 = s.indexOf("%1");
								if (k7 == -1)
									break;
								if (class9_1.id < 4000 || class9_1.id > 5000 && class9_1.id != 13921
										&& class9_1.id != 13922 && class9_1.id != 12171 && class9_1.id != 12172)
									s = s.substring(0, k7) + methodR(extractInterfaceValues(class9_1, 0))
											+ s.substring(k7 + 2);
								else
									s = s.substring(0, k7) + interfaceIntToString(extractInterfaceValues(class9_1, 0))
											+ s.substring(k7 + 2);
							} while (true);
							do {
								int l7 = s.indexOf("%2");
								if (l7 == -1)
									break;
								s = s.substring(0, l7) + interfaceIntToString(extractInterfaceValues(class9_1, 1))
										+ s.substring(l7 + 2);
							} while (true);
							do {
								int i8 = s.indexOf("%3");
								if (i8 == -1)
									break;
								s = s.substring(0, i8) + interfaceIntToString(extractInterfaceValues(class9_1, 2))
										+ s.substring(i8 + 2);
							} while (true);
							do {
								int j8 = s.indexOf("%4");
								if (j8 == -1)
									break;
								s = s.substring(0, j8) + interfaceIntToString(extractInterfaceValues(class9_1, 3))
										+ s.substring(j8 + 2);
							} while (true);
							do {
								int k8 = s.indexOf("%5");
								if (k8 == -1)
									break;
								s = s.substring(0, k8) + interfaceIntToString(extractInterfaceValues(class9_1, 4))
										+ s.substring(k8 + 2);
							} while (true);
						}
						int l8 = s.indexOf("\\n");
						String s1;
						if (l8 != -1) {
							s1 = s.substring(0, l8);
							s = s.substring(l8 + 2);
						} else {
							s1 = s;
							s = "";
						}
						RSFont font = null;
						if (textDrawingArea == smallText) {
							font = newSmallFont;
						} else if (textDrawingArea == aTextDrawingArea_1271) {
							font = newRegularFont;
						} else if (textDrawingArea == chatTextDrawingArea) {
							font = newBoldFont;
						} else if (textDrawingArea == aTextDrawingArea_1273) {
							font = newFancyFont;
						}
						if (class9.parentID == 49100 || class9.parentID == 51100 || class9.parentID == 53100) {
							int parent = class9.parentID == 49100 ? 49100 : class9.parentID == 51100 ? 51100 : 53100;
							int subId = (class9_1.id - parent) % 100;
							if (subId > achievementCutoff) {
								continue;
							}
						}
						if (class9_1.centerText) {
							font.drawCenteredString(s1, k2 + class9_1.width / 2, l6, i4, class9_1.textShadow ? 0 : -1);
						} else {
							font.drawBasicString(s1, k2, l6, i4, class9_1.textShadow ? 0 : -1);
						}
					}
				} else if (class9_1.type == 5) {
					Sprite sprite;
					Sprite sprite2 = null;
					if (interfaceIsSelected(class9_1)) {
						sprite = class9_1.sprite2;
					} else {
						sprite = class9_1.sprite1;
					}
					if (sprite != null)
						if(class9_1.drawsTransparent) {
							sprite.drawAdvancedTransparentSprite(k2, l2, alpha);
						} else {
							sprite.drawSprite(k2, l2);
						}
					if (class9_1.id >= 58032 && class9_1.id <= 58040) {
						int tabId = 0;
						try {
							tabId = Integer.parseInt(RSInterface.interfaceCache[5292].message);
						} catch (NumberFormatException nfe) {
						}
						if (RSInterface.interfaceCache[class9.id + 1] != null
								&& !RSInterface.interfaceCache[class9.id + 1].isMouseoverTriggered
								|| class9_1.id == 58039 && interfaceContainsItem(58048))
							sprite = new Sprite("BankTab/TAB 3");
						if (tabId > 0) {
							if (tabId + 58031 == class9_1.id) {
								sprite = new Sprite("BankTab/TAB 2");
							}
						}
					}
					if (class9.parentID == 49100 || class9.parentID == 51100 || class9.parentID == 53100) {
						int parent = class9.parentID == 49100 ? 49100 : class9.parentID == 51100 ? 51100 : 53100;
						int subId = (class9_1.id - parent) % 100;
						if (subId > achievementCutoff) {
							continue;
						}
						if (class9_1.id - parent > 600 && class9_1.id - parent < 700) {
							String message = RSInterface.interfaceCache[class9_1.id + 100].message;
							if (message != null) {
								if (message.split("/").length == 2) {
									int currentValue = Integer.parseInt(message.split("/")[0]);
									if (currentValue <= 0) {
										sprite = new Sprite("Interfaces/Achievements/IMAGE 6");
									} else {
										int maxValue = Integer.parseInt(message.split("/")[1]);
										double modifier = 2.07;
										double percentage = (((double) currentValue / (double) maxValue) * 100D);
										int width = (int) (percentage * modifier) + 1;
										if (percentage <= 0.0)
											sprite = new Sprite(Signlink.findCacheDir()
													+ "Sprites/Interfaces/Achievements/IMAGE 6.png", width, 16);
										else if (percentage <= 50.0)
											sprite = new Sprite(Signlink.findCacheDir()
													+ "Sprites/Interfaces/Achievements/IMAGE 8.png", width, 16);
										else if (percentage <= 75.0)
											sprite = new Sprite(Signlink.findCacheDir()
													+ "Sprites/Interfaces/Achievements/IMAGE 9.png", width, 16);
										else
											sprite = new Sprite(Signlink.findCacheDir()
													+ "Sprites/Interfaces/Achievements/IMAGE 7.png", width, 16);
										sprite2 = new Sprite("Interfaces/Achievements/IMAGE 6");
									}
								}
							}
						}
					}
					if (sprite2 != null) {
						sprite2.drawSprite(k2, l2);
					}
					if (spellSelected == 1 && class9_1.id == spellID && spellID != 0 && sprite != null) {
						sprite.drawSprite(k2, l2, 0xffffff);
					} else {
						if (sprite != null)
							sprite.drawSprite(k2, l2);
					}
					if (autocast && class9_1.id == spellID && class9_1.id == autocastId) {
						magicAuto.drawSprite(k2 - 3, l2 - 3);
					}
				} else if (class9_1.type == 6) {
					int k3 = Texture.textureInt1;
					int j4 = Texture.textureInt2;
					Texture.textureInt1 = k2 + class9_1.width / 2;
					Texture.textureInt2 = l2 + class9_1.height / 2;
					int i5 = Texture.anIntArray1470[class9_1.modelRotation1] * class9_1.modelZoom >> 16;
					int l5 = Texture.anIntArray1471[class9_1.modelRotation1] * class9_1.modelZoom >> 16;
					boolean flag2 = interfaceIsSelected(class9_1);
					int i7;
					if (flag2)
						i7 = class9_1.anInt258;
					else
						i7 = class9_1.anInt257;
					Model model;
					if (i7 == -1) {
						model = class9_1.method209(-1, -1, flag2);
					} else {
						AnimationDefinition animation = AnimationDefinition.anims[i7];
						model = class9_1.method209(animation.anIntArray354[class9_1.anInt246],
								animation.anIntArray353[class9_1.anInt246], flag2);
					}
					if (model != null)
						model.method482(class9_1.modelRotation2, 0, class9_1.modelRotation1, 0, i5, l5);
					Texture.textureInt1 = k3;
					Texture.textureInt2 = j4;
				} else if (class9_1.type == 7) {
					TextDrawingArea textDrawingArea_1 = class9_1.textDrawingAreas;
					int k4 = 0;
					for (int j5 = 0; j5 < class9_1.height; j5++) {
						for (int i6 = 0; i6 < class9_1.width; i6++) {
							if (class9_1.inv[k4] > 0) {
								ItemDefinition itemDef = ItemDefinition.forID(class9_1.inv[k4] - 1);
								String s2 = itemDef.name;
								if (itemDef.stackable || class9_1.invStackSizes[k4] != 1)
									s2 = s2 + " x" + intToKOrMilLongName(class9_1.invStackSizes[k4]);
								int i9 = k2 + i6 * (115 + class9_1.invSpritePadX);
								int k9 = l2 + j5 * (12 + class9_1.invSpritePadY);
								if (class9_1.centerText)
									textDrawingArea_1.method382(class9_1.textColor, i9 + class9_1.width / 2, s2, k9,
											class9_1.textShadow);
								else
									textDrawingArea_1.method389(class9_1.textShadow, i9, class9_1.textColor, s2, k9);
							}
							k4++;
						}
					}
				}
				else if (class9_1.type == 8
						&& (anInt1500 == class9_1.id
								|| anInt1044 == class9_1.id || anInt1129 == class9_1.id)
						&& anInt1501 == 50 && !menuOpen) {
					int boxWidth = 0;
					int boxHeight = 0;
					TextDrawingArea textDrawingArea_2 = aTextDrawingArea_1271;
					for (String s1 = class9_1.message; s1.length() > 0;) {
						if (s1.indexOf("%") != -1) {
							do {
								int k7 = s1.indexOf("%1");
								if (k7 == -1)
									break;
								s1 = s1.substring(0, k7)
										+ interfaceIntToString(extractInterfaceValues(
												class9_1, 0))
										+ s1.substring(k7 + 2);
							} while (true);
							do {
								int l7 = s1.indexOf("%2");
								if (l7 == -1)
									break;
								s1 = s1.substring(0, l7)
										+ interfaceIntToString(extractInterfaceValues(
												class9_1, 1))
										+ s1.substring(l7 + 2);
							} while (true);
							do {
								int i8 = s1.indexOf("%3");
								if (i8 == -1)
									break;
								s1 = s1.substring(0, i8)
										+ interfaceIntToString(extractInterfaceValues(
												class9_1, 2))
										+ s1.substring(i8 + 2);
							} while (true);
							do {
								int j8 = s1.indexOf("%4");
								if (j8 == -1)
									break;
								s1 = s1.substring(0, j8)
										+ interfaceIntToString(extractInterfaceValues(
												class9_1, 3))
										+ s1.substring(j8 + 2);
							} while (true);
							do {
								int k8 = s1.indexOf("%5");
								if (k8 == -1)
									break;
								s1 = s1.substring(0, k8)
										+ interfaceIntToString(extractInterfaceValues(
												class9_1, 4))
										+ s1.substring(k8 + 2);
							} while (true);
						}
						int l7 = s1.indexOf("\\n");
						String s4;
						if (l7 != -1) {
							s4 = s1.substring(0, l7);
							s1 = s1.substring(l7 + 2);
						} else {
							s4 = s1;
							s1 = "";
						}
						int j10 = textDrawingArea_2.getTextWidth(s4);
						if (j10 > boxWidth) {
							boxWidth = j10;
						}
						boxHeight += textDrawingArea_2.anInt1497 + 1;
					}
					boxWidth += 6;
					boxHeight += 7;
					int xPos = (k2 + class9_1.width) - 5 - boxWidth;
					int yPos = l2 + class9_1.height + 5;
					if (xPos < k2 + 5)
						xPos = k2 + 5;
					if (xPos + boxWidth > xPosition + class9.width)
						xPos = (xPosition + class9.width) - boxWidth;
					if (yPos + boxHeight > yPosition + class9.height)
						yPos = (l2 - boxHeight);
					switch (class9_1.id) {
					case 9217:
					case 9220:
					case 9223:
					case 9226:
					case 9229:
					case 9232:
					case 9235:
					case 9238:
						xPos -= 80;
						break;
					case 9239:
						yPos -= 100;
						break;
					}
					DrawingArea.drawPixels(boxHeight, yPos, xPos, 0xFFFFA0,
							boxWidth);
					DrawingArea.fillPixels(xPos, boxWidth, boxHeight, 0, yPos);
					String s2 = class9_1.message;
					for (int j11 = yPos + textDrawingArea_2.anInt1497 + 2; s2
							.length() > 0; j11 += textDrawingArea_2.anInt1497 + 1) {// anInt1497
						if (s2.indexOf("%") != -1) {
							do {
								int k7 = s2.indexOf("%1");
								if (k7 == -1)
									break;
								s2 = s2.substring(0, k7)
										+ interfaceIntToString(extractInterfaceValues(
												class9_1, 0))
										+ s2.substring(k7 + 2);
							} while (true);
							do {
								int l7 = s2.indexOf("%2");
								if (l7 == -1)
									break;
								s2 = s2.substring(0, l7)
										+ interfaceIntToString(extractInterfaceValues(
												class9_1, 1))
										+ s2.substring(l7 + 2);
							} while (true);
							do {
								int i8 = s2.indexOf("%3");
								if (i8 == -1)
									break;
								s2 = s2.substring(0, i8)
										+ interfaceIntToString(extractInterfaceValues(
												class9_1, 2))
										+ s2.substring(i8 + 2);
							} while (true);
							do {
								int j8 = s2.indexOf("%4");
								if (j8 == -1)
									break;
								s2 = s2.substring(0, j8)
										+ interfaceIntToString(extractInterfaceValues(
												class9_1, 3))
										+ s2.substring(j8 + 2);
							} while (true);
							do {
								int k8 = s2.indexOf("%5");
								if (k8 == -1)
									break;
								s2 = s2.substring(0, k8)
										+ interfaceIntToString(extractInterfaceValues(
												class9_1, 4))
										+ s2.substring(k8 + 2);
							} while (true);
						}
						int l11 = s2.indexOf("\\n");
						String s5;
						if (l11 != -1) {
							s5 = s2.substring(0, l11);
							s2 = s2.substring(l11 + 2);
						} else {
							s5 = s2;
							s2 = "";
						}
						if (class9_1.centerText) {
							textDrawingArea_2.method382(yPos, xPos
									+ class9_1.width / 2, s5, j11, false);
						} else {
							if (s5.contains("\\r")) {
								String text = s5
										.substring(0, s5.indexOf("\\r"));
								String text2 = s5
										.substring(s5.indexOf("\\r") + 2);
								textDrawingArea_2.method389(false, xPos + 3, 0,
										text, j11);
								int rightX = boxWidth + xPos
										- textDrawingArea_2.getTextWidth(text2)
										- 2;
								textDrawingArea_2.method389(false, rightX, 0,
										text2, j11);
								//System.out.println("Box: " + boxWidth + "");
							} else
								textDrawingArea_2.method389(false, xPos + 3, 0,
										s5, j11);
						}
			DrawingArea.setDrawingArea(l1, i1, k1, j1);
		}
			} else if (class9_1.type == 12) {
				Sprite sprite;
				if (interfaceIsSelected(class9_1))
					sprite = class9_1.sprite2;
				else
					sprite = class9_1.sprite1;
				if (sprite != null)
					sprite.drawAdvancedSprite(k2, l2);
			} else if (class9_1.type == 9) {
				drawHoverBox(k2, l2, class9_1.message);
			} else if (class9_1.type == 10) {
				Sprite sprite;
				if (interfaceIsSelected(class9_1)) {
					sprite = class9_1.sprite2;
				} else {
					sprite = class9_1.sprite1;
				}
				if (sprite != null) {
					sprite.drawSpriteWithOpacity(k2, l2, class9_1.opacity);
				}
			} else if (class9_1.type == 12 || class9_1.type == 13) {
				Sprite interfaceSprite = null;
				if (interfaceIsSelected(class9_1) || anInt886 == class9_1.id)
					interfaceSprite = class9_1.sprite2;
				else
					interfaceSprite = class9_1.sprite1;
				if (interfaceSprite != null)
					interfaceSprite.drawAdvancedSprite(k2, l2);
			} else if (class9_1.type == 16) {
				drawInputField(class9_1, k2, l2, class9_1.width, class9_1.height);
			} else if (class9_1.type == 17) {
				if (class9.id != 37010) {
					DrawingArea.setDrawingArea(currentGameHeight, 0, currentGameWidth, 0);
				}
				DrawingArea.drawAlphaBox(k2, l2, class9_1.width, class9_1.height, class9_1.fillColor, class9_1.opacity);
				DrawingArea.setDrawingArea(yPosition + class9.height, xPosition, xPosition + class9.width, yPosition);
			} else if (class9_1.type == 18) {
				DrawingArea.drawRoundedRectangle(k2, l2, class9_1.width, class9_1.height, class9_1.fillColor,
						class9_1.opacity, class9_1.filled, class9_1.textShadow);
			} else if (class9_1.type == 19) {
				if (class9_1.backgroundSprites.length > 1) {
					if (class9_1.sprite1 != null) {
						class9_1.sprite1.drawAdvancedSprite(k2, l2);
					}
				}
			} else if (class9_1.type == 20) {
				DrawingArea.drawAlphaBox(k2, l2, class9_1.width, class9_1.height, 0x403B2B, class9_1.opacity);
				DrawingArea.drawAlphaBox(k2 + 1, l2 + 1, class9_1.width - 2, class9_1.height - 2, 0xABAE83,
						class9_1.opacity);
				if (class9_1.getMenuItem() != null) {
					Sprite sprite = class9_1.getMenuItem().getSprite();
					int x = 0;
					if (sprite != null) {
						sprite.drawAdvancedSprite(k2 + 4, l2 + sprite.myHeight / 2);
						x = sprite.myWidth;
					}
					class9_1.textDrawingAreas.drawText(0x000000, class9_1.getMenuItem().getText(), l2 + 14,
							k2 + 23 + x);
				}
			} else if (class9_1.type == 21) {
				RSInterface menu = RSInterface.interfaceCache[class9_1.mOverInterToTrigger];
				if (menu != null && menu.isMenuVisible()) {
					RSMenuItem item = class9_1.getMenuItem();
					if (item != null) {
						DrawingArea.drawAlphaBox(k2, l2, class9_1.width, class9_1.height, 0x403B2B, class9_1.opacity);
						DrawingArea.drawAlphaBox(k2 + 1, l2 + 1, class9_1.width - 2, class9_1.height - 2, 0xABAE83,
								class9_1.opacity);
						Sprite sprite = class9_1.getMenuItem().getSprite();
						int x = 0;
						if (sprite != null) {
							sprite.drawAdvancedSprite(k2 + 4, l2 + sprite.myHeight / 2);
							x = sprite.myWidth;
						}
						class9_1.textDrawingAreas.drawText(0x000000, class9_1.getMenuItem().getText(), l2 + 14,
								k2 + 23 + x);
					}
				}
			} else if (class9_1.type == 22) {
				RSInterface rsinterface = null;
				int slot = class9_1.grandExchangeSlot;
				if (grandExchangeInformation[slot][0] == -1)
					rsinterface = RSInterface.interfaceCache[GrandExchange.grandExchangeBuyAndSellBoxIds[slot]];
				else {
					int childId = GrandExchange.grandExchangeItemBoxIds[slot] + 4;

					RSInterface class9_2 = RSInterface.interfaceCache[childId];

					int itemId = grandExchangeInformation[slot][1] + 1;
					int itemAmount = grandExchangeInformation[slot][3];

					class9_2.inv[0] = itemId;
					class9_2.invStackSizes[0] = itemAmount;

					rsinterface = RSInterface.interfaceCache[GrandExchange.grandExchangeItemBoxIds[slot]];

					RSInterface text = null;

					text = RSInterface.interfaceCache[GrandExchange.grandExchangeItemBoxIds[slot] + 6];

					if (grandExchangeInformation[slot][0] == 0)
						text.message = "Buy";
					else if (grandExchangeInformation[slot][0] == 1)
						text.message = "Sell";
					else if (grandExchangeInformation[slot][0] == 2)
						text.message = "Canceled";
				}
				drawInterface(0, k2, rsinterface, l2);
			} else if (class9_1.type == 23) {
				int displayColor = class9_1.colorTypes[class9_1.progressBarState];
				int drawingWidth = class9_1.progressBarPercentage * class9_1.width / 100;

				DrawingArea.drawPixels(class9_1.height, l2, k2, 0x2B261E, class9_1.width);
				DrawingArea.drawPixels(class9_1.height, l2, k2, displayColor, drawingWidth);
				DrawingArea.drawAlphaBox(k2, l2, class9_1.width, 4, 0, 75);
				DrawingArea.drawAlphaBox(k2, l2 + 4, 4, class9_1.height - 4, 0, 75);
				DrawingArea.fillPixels(k2, class9_1.width, class9_1.height, 0, l2);
			}
		}
		DrawingArea.setDrawingArea(l1, i1, k1, j1);
	}

	/**
	 * int state -1 for buy and sell buttons, 0 for buying, 1 for selling, 2 for
	 * canceled
	 * 
	 * int itemId The item that the player wants to buy
	 * 
	 * int currentlyBought/Sold How many items the player has bought/sold so
	 * far.
	 * 
	 * int totalAmount How many items the player wants to buy/sell in total.
	 * 
	 */
	private int[][] grandExchangeInformation = new int[][] { { -1, -1, -1, -1 }, { -1, -1, -1, -1 }, { -1, -1, -1, -1 },
			{ -1, -1, -1, -1 }, { -1, -1, -1, -1 }, { -1, -1, -1, -1 }, { -1, -1, -1, -1 }, { -1, -1, -1, -1 },
			{ -1, -1, -1, -1 },
			// { 0, 11802, 0, 10 },
			// { 0, 11804, 4, 14 },
			// { 2, 11826, 2, 2 },
			// { 1, 555, 47500, 52500 },
			// { 0, 560, 35000, 50000 },
			// { 1, 11847, 0, 1 },
			// { -1, -1, -1, -1 }
	};

	public final String methodR(int j) {
		if (j >= 0 && j < 10000)
			return String.valueOf(j);
		if (j >= 10000 && j < 10000000)
			return j / 1000 + "K";
		if (j >= 10000000 && j < 999999999)
			return j / 1000000 + "M";
		if (j >= 999999999)
			return "*";
		else
			return "?";
	}

	public void drawHoverBox(int xPos, int yPos, String text) {
		String[] results = text.split("\n");
		int height = (results.length * 16) + 3;
		int width;
		width = aTextDrawingArea_1271.getTextWidth(results[0]) + 6;
		for (int i = 1; i < results.length; i++)
			if (width <= aTextDrawingArea_1271.getTextWidth(results[i]) + 6)
				width = aTextDrawingArea_1271.getTextWidth(results[i]) + 6;
		DrawingArea.drawPixels(height, yPos, xPos, 0xFFFFA0, width);
		DrawingArea.fillPixels(xPos, width, height, 0, yPos);
		yPos += 14;
		for (int i = 0; i < results.length; i++) {
			aTextDrawingArea_1271.method389(false, xPos + 3, 0, results[i], yPos);
			yPos += 16;
		}
	}

	public void drawBlackBox(int xPos, int yPos) {
		DrawingArea.drawPixels(71, yPos - 1, xPos - 2, 0x726451, 1);
		DrawingArea.drawPixels(69, yPos, xPos + 174, 0x726451, 1);
		DrawingArea.drawPixels(1, yPos - 2, xPos - 2, 0x726451, 178);
		DrawingArea.drawPixels(1, yPos + 68, xPos, 0x726451, 174);
		DrawingArea.drawPixels(71, yPos - 1, xPos - 1, 0x2E2B23, 1);
		DrawingArea.drawPixels(71, yPos - 1, xPos + 175, 0x2E2B23, 1);
		DrawingArea.drawPixels(1, yPos - 1, xPos, 0x2E2B23, 175);
		DrawingArea.drawPixels(1, yPos + 69, xPos, 0x2E2B23, 175);
		DrawingArea.method335(0, yPos, 174, 68, 220, xPos);
	}

	public void randomizeBackground(Background background) {
		int j = 256;
		for (int k = 0; k < anIntArray1190.length; k++)
			anIntArray1190[k] = 0;

		for (int l = 0; l < 5000; l++) {
			int i1 = (int) (Math.random() * 128D * j);
			anIntArray1190[i1] = (int) (Math.random() * 256D);
		}
		for (int j1 = 0; j1 < 20; j1++) {
			for (int k1 = 1; k1 < j - 1; k1++) {
				for (int i2 = 1; i2 < 127; i2++) {
					int k2 = i2 + (k1 << 7);
					anIntArray1191[k2] = (anIntArray1190[k2 - 1] + anIntArray1190[k2 + 1] + anIntArray1190[k2 - 128]
							+ anIntArray1190[k2 + 128]) / 4;
				}

			}
			int ai[] = anIntArray1190;
			anIntArray1190 = anIntArray1191;
			anIntArray1191 = ai;
		}
		if (background != null) {
			int l1 = 0;
			for (int j2 = 0; j2 < background.anInt1453; j2++) {
				for (int l2 = 0; l2 < background.anInt1452; l2++)
					if (background.aByteArray1450[l1++] != 0) {
						int i3 = l2 + 16 + background.anInt1454;
						int j3 = j2 + 16 + background.anInt1455;
						int k3 = i3 + (j3 << 7);
						anIntArray1190[k3] = 0;
					}
			}
		}
	}

	private void method107(int i, int j, Stream stream, Player player) {
		if ((i & 0x400) != 0) {
			player.anInt1543 = stream.method428();
			player.anInt1545 = stream.method428();
			player.anInt1544 = stream.method428();
			player.anInt1546 = stream.method428();
			player.anInt1547 = stream.method436() + loopCycle;
			player.anInt1548 = stream.method435() + loopCycle;
			player.anInt1549 = stream.method428();
			player.method446();
		}
		if ((i & 0x100) != 0) {
			player.anInt1520 = stream.method434();
			int k = stream.readDWord();
			player.anInt1524 = k >> 16;
			player.anInt1523 = loopCycle + (k & 0xffff);
			player.anInt1521 = 0;
			player.anInt1522 = 0;
			if (player.anInt1523 > loopCycle)
				player.anInt1521 = -1;
			if (player.anInt1520 == 65535)
				player.anInt1520 = -1;
		}
		if ((i & 8) != 0) {
			int l = stream.method434();
			if (l == 65535)
				l = -1;
			int i2 = stream.method427();
			if (l == player.anim && l != -1) {
				int i3 = AnimationDefinition.anims[l].anInt365;
				if (i3 == 1) {
					player.anInt1527 = 0;
					player.anInt1528 = 0;
					player.anInt1529 = i2;
					player.anInt1530 = 0;
				}
				if (i3 == 2)
					player.anInt1530 = 0;
			} else if (l == -1 || player.anim == -1
					|| AnimationDefinition.anims[l].anInt359 >= AnimationDefinition.anims[player.anim].anInt359) {
				player.anim = l;
				player.anInt1527 = 0;
				player.anInt1528 = 0;
				player.anInt1529 = i2;
				player.anInt1530 = 0;
				player.anInt1542 = player.smallXYIndex;
			}
		}
		if ((i & 4) != 0) {
			player.textSpoken = stream.readString();
			if (player.textSpoken.charAt(0) == '~') {
				player.textSpoken = player.textSpoken.substring(1);
				pushMessage(player.textSpoken, 2, player.name);
			} else if (player == myPlayer)
				pushMessage(player.textSpoken, 2, player.name);
			player.anInt1513 = 0;
			player.anInt1531 = 0;
			player.textCycle = 150;
		}
		if ((i & 0x80) != 0) {
			int i1 = stream.method434();
			int j2 = stream.readUnsignedByte();
			int j3 = stream.method427();
			int k3 = stream.currentOffset;
			if (player.name != null && player.visible) {
				long l3 = TextClass.longForName(player.name);
				boolean flag = false;
				if (j2 <= 1) {
					for (int i4 = 0; i4 < ignoreCount; i4++) {
						if (ignoreListAsLongs[i4] != l3)
							continue;
						flag = true;
						break;
					}

				}
				if (!flag && anInt1251 == 0)
					try {
						aStream_834.currentOffset = 0;
						stream.method442(j3, 0, aStream_834.buffer);
						aStream_834.currentOffset = 0;
						String s = TextInput.method525(j3, aStream_834);
						player.textSpoken = s;
						player.anInt1513 = i1 >> 8;
						player.privelage = j2;
						player.anInt1531 = i1 & 0xff;
						player.textCycle = 150;
						String crown = j2 > 0 ? "@cr" + j2 + "@" : "";
						String title = player.title != null && !player.title.isEmpty()
								? "<col=" + player.titleColor + ">" + player.title + "</col> " : "";
						pushMessage(s, 2, crown + title + player.name);
					} catch (Exception exception) {
						Signlink.reporterror("cde2");
					}
			}
			stream.currentOffset = k3 + j3;
		}
		if ((i & 1) != 0) {
			player.interactingEntity = stream.method434();
			if (player.interactingEntity == 65535)
				player.interactingEntity = -1;
		}
		if ((i & 0x10) != 0) {
			int j1 = stream.method427();
			byte abyte0[] = new byte[j1];
			Stream stream_1 = new Stream(abyte0);
			stream.readBytes(j1, 0, abyte0);
			aStreamArray895s[j] = stream_1;
			player.updatePlayer(stream_1);
		}
		if ((i & 2) != 0) {
			player.anInt1538 = stream.method436();
			player.anInt1539 = stream.method434();
		}
		if ((i & 0x20) != 0) {
			int k1 = stream.readUnsignedByte();
			int k2 = stream.method426();
			player.updateHitData(k2, k1, loopCycle);
			player.loopCycleStatus = loopCycle + 300;
			player.currentHealth = stream.method427();
			player.maxHealth = stream.readUnsignedByte();
		}
		if ((i & 0x200) != 0) {
			int l1 = stream.readUnsignedByte();
			int l2 = stream.method428();
			player.updateHitData(l2, l1, loopCycle);
			player.loopCycleStatus = loopCycle + 300;
			player.currentHealth = stream.readUnsignedByte();
			player.maxHealth = stream.method427();
		}
	}

	public void method108() {
		try {
			int j = myPlayer.x + cameraOffsetX;
			int k = myPlayer.y + cameraOffsetY;
			if (anInt1014 - j < -500 || anInt1014 - j > 500 || anInt1015 - k < -500 || anInt1015 - k > 500) {
				anInt1014 = j;
				anInt1015 = k;
			}
			if (anInt1014 != j)
				anInt1014 += (j - anInt1014) / 16;
			if (anInt1015 != k)
				anInt1015 += (k - anInt1015) / 16;
			if (super.keyArray[1] == 1)
				anInt1186 += (-24 - anInt1186) / 2;
			else if (super.keyArray[2] == 1)
				anInt1186 += (24 - anInt1186) / 2;
			else
				anInt1186 /= 2;
			if (super.keyArray[3] == 1)
				anInt1187 += (12 - anInt1187) / 2;
			else if (super.keyArray[4] == 1)
				anInt1187 += (-12 - anInt1187) / 2;
			else
				anInt1187 /= 2;
			viewRotation = viewRotation + anInt1186 / 2 & 0x7ff;
			anInt1184 += anInt1187 / 2;
			if (anInt1184 < 128)
				anInt1184 = 128;
			if (anInt1184 > 383)
				anInt1184 = 383;
			int l = anInt1014 >> 7;
			int i1 = anInt1015 >> 7;
			int j1 = method42(plane, anInt1015, anInt1014);
			int k1 = 0;
			if (l > 3 && i1 > 3 && l < 100 && i1 < 100) {
				for (int l1 = l - 4; l1 <= l + 4; l1++) {
					for (int k2 = i1 - 4; k2 <= i1 + 4; k2++) {
						int l2 = plane;
						if (l2 < 3 && (byteGroundArray[1][l1][k2] & 2) == 2)
							l2++;
						int i3 = j1 - intGroundArray[l2][l1][k2];
						if (i3 > k1)
							k1 = i3;
					}

				}

			}
			anInt1005++;
			if (anInt1005 > 1512) {
				anInt1005 = 0;
				stream.createFrame(77);
				stream.writeWordBigEndian(0);
				int i2 = stream.currentOffset;
				stream.writeWordBigEndian((int) (Math.random() * 256D));
				stream.writeWordBigEndian(101);
				stream.writeWordBigEndian(233);
				stream.writeWord(45092);
				if ((int) (Math.random() * 2D) == 0)
					stream.writeWord(35784);
				stream.writeWordBigEndian((int) (Math.random() * 256D));
				stream.writeWordBigEndian(64);
				stream.writeWordBigEndian(38);
				stream.writeWord((int) (Math.random() * 65536D));
				stream.writeWord((int) (Math.random() * 65536D));
				stream.writeBytes(stream.currentOffset - i2);
			}
			int j2 = k1 * 192;
			if (j2 > 0x17f00)
				j2 = 0x17f00;
			if (j2 < 32768)
				j2 = 32768;
			if (j2 > anInt984) {
				anInt984 += (j2 - anInt984) / 24;
				return;
			}
			if (j2 < anInt984) {
				anInt984 += (j2 - anInt984) / 80;
			}
		} catch (Exception _ex) {
			Signlink.reporterror("glfc_ex " + myPlayer.x + "," + myPlayer.y + "," + anInt1014 + "," + anInt1015 + ","
					+ mapRegionsX + "," + mapRegionsY + "," + baseX + "," + baseY);
			throw new RuntimeException("eek");
		}
	}

	@Override
	public void processDrawing() {
		if (rsAlreadyLoaded || loadingError || genericLoadingError) {
			showErrorScreen();
			return;
		}
		anInt1061++;
		if (!loggedIn) {
			drawLoginScreen(false);
		} else {
			if (loginScreenGraphicsBuffer == null) {
				drawGameScreen();
			}
		}
		anInt1213 = 0;
	}

	private boolean isFriendOrSelf(String s) {
		if (s == null)
			return false;
		for (int i = 0; i < friendsCount; i++)
			if (s.equalsIgnoreCase(friendsList[i]))
				return true;
		return s.equalsIgnoreCase(myPlayer.name);
	}

	private static String combatDiffColor(int i, int j) {
		int k = i - j;
		if (k < -9)
			return "@red@";
		if (k < -6)
			return "@or3@";
		if (k < -3)
			return "@or2@";
		if (k < 0)
			return "@or1@";
		if (k > 9)
			return "@gre@";
		if (k > 6)
			return "@gr3@";
		if (k > 3)
			return "@gr2@";
		if (k > 0)
			return "@gr1@";
		else
			return "@yel@";
	}

	public void setWaveVolume(int i) {
		Signlink.wavevol = i;
	}

	public static boolean centerInterface() {
		int minimumScreenWidth = 900, minimumScreenHeight = 650;
		if (currentGameWidth >= minimumScreenWidth && currentGameHeight >= minimumScreenHeight)
			return true;
		return false;
	}
	
	private boolean groundItemsOn = true;
	
	public static boolean goodDistance(int objectX, int objectY, int playerX, int playerY, int distance) {
		return ((objectX - playerX <= distance && objectX - playerX >= -distance) && (objectY - playerY <= distance && objectY - playerY >= -distance));
	}
	
	public static int distanceToPoint(int x1, int y1, int x2, int y2) {
		int x = (int) Math.pow(x1 - x2, 2.0D);
		int y = (int) Math.pow(y1 - y2, 2.0D);
		return (int) Math.floor(Math.sqrt(x + y));
	}
	
	private void displayGroundItems() {
		/**
		 * Loop thru all tiles in region
		 */
		for (int x = 0; x < 104; x++) {
			for (int y = 0; y < 104; y++) {
				NodeList class19 = groundArray[plane][x][y];
				int count = 0;
				if (class19 != null) {
					for (Item item = (Item) class19.getFirst(); item != null; item = (Item) class19.getNext()) {
						/**
						 * loops thru all the ground tiles , then all the
						 * 'items' (if the tile has an item on it) res tis self
						 * explanatory
						 */
						ItemDefinition itemDef = ItemDefinition.forID(item.ID);
						int totalValue = itemDef.value * item.anInt1559;
						if (totalValue < groundItemValueLimit)
							continue;
						int color = 0xffffff;
						for (int index = 0; index < groundItemColors.size(); index++) {
							GroundItemColors itemColor = groundItemColors.get(index);
							if (itemColor.itemId == item.ID) {
								color = itemColor.itemColor;
							}
						}
						calcEntityScreenPos((x * 128 + 64), 25, (y * 128 + 64));
						
						int itemX = baseX + x;
						int itemY = baseY + y;
						int playerX = baseX + (myPlayer.x - 6 >> 7);
						int playerY = baseY + (myPlayer.y - 6 >> 7);
						
						/**
						 * Fading
						 */
						int transparencyDistance = distanceToPoint(playerX, playerY, itemX, itemY);
						int transparency = 256 - (transparencyDistance * 16);
						
						if (goodDistance(itemX, itemY, playerX, playerY, 16)) {
							int yMod = (count * 12);
							newSmallFont.drawCenteredString(
									itemDef.name + " x " + NumberFormat.getInstance().format(item.anInt1559) + " ("
											+ NumberFormat.getInstance().format(itemDef.value) + "gp)",
									spriteDrawX, spriteDrawY - yMod, color, 0, transparency);
							count++;
						}
					}
				}
			}
		}
	}

	private void draw3dScreen() {

		if (snowVisible && Configuration.CHRISTMAS && Client.currentScreenMode == ScreenMode.FIXED) {
			method119(anInt945, 11877);
			drawInterface(0, 0, RSInterface.interfaceCache[11877], 0);
		}

		if (fadingScreen != null) {
			fadingScreen.draw();
		}

		if (showEntityTarget) {
			if (entityTarget != null) {
				entityTarget.draw();
			}
		}

		if (gameTimers) {
			try {
				int startX = 519;
				int startY = Client.currentScreenMode == ScreenMode.FIXED ? 294 : Client.currentGameHeight - 209;
				GameTimerHandler.getSingleton().drawGameTimers(this, startX, startY);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		drawSplitPrivateChat();

		if (crossType == 1) {
			crosses[crossIndex / 100].drawSprite(crossX - 8, crossY - 8);
			anInt1142++;
			if (anInt1142 > 67) {
				anInt1142 = 0;
				stream.createFrame(78);
			}
		}
		if (crossType == 2) {
			crosses[4 + crossIndex / 100].drawSprite(crossX - 8, crossY - 8);
		}

		if (openWalkableWidgetID != -1) {
			method119(anInt945, openWalkableWidgetID);
			RSInterface rsinterface = RSInterface.interfaceCache[openWalkableWidgetID];
			if (currentScreenMode == ScreenMode.FIXED) {
				drawInterface(0, 0, rsinterface, 0);
			} else {
				if (openWalkableWidgetID == 28000 || openWalkableWidgetID == 28020 || openWalkableWidgetID == 16210
						|| openWalkableWidgetID == 27500 || openWalkableWidgetID == 196) {
					/**
					 * Interfaces to draw at the top right corner nex to the
					 * minimap (Ex. Wildy Target)
					 **/
					drawInterface(0, currentGameWidth - 730, rsinterface, 20);
				} else if (openWalkableWidgetID == 197) { 
					drawInterface(0, currentGameWidth - 530, rsinterface, -70);
				} else 		if (openInterfaceID == 5292) {
					method119(anInt945, openInterfaceID);
					drawInterface(0, currentScreenMode == ScreenMode.FIXED ? 0 : (currentGameWidth / 2) - 356, RSInterface.interfaceCache[openInterfaceID], currentScreenMode == ScreenMode.FIXED ? 0 : (currentGameHeight / 2) - 230);
				} else if (openWalkableWidgetID == 21100 || openWalkableWidgetID == 21119) {
					/**
					 * Interfaces to draw at top left corner (Ex. Pest Control)
					 **/
					drawInterface(0, 0, rsinterface, 0);
				} else if (openWalkableWidgetID == 201) {
					/** Duel arena interface **/
					drawInterface(0, currentGameWidth - 510, rsinterface, -110);
				} else if (centerInterface()) {
					drawInterface(0, (currentGameWidth / 2) - 256, rsinterface,
							currentScreenMode == ScreenMode.FIXED ? 0 : (currentGameHeight / 2) - 167);
				} else {
					drawInterface(0, 0, rsinterface, 0);
				}
			}
		}
		if (openInterfaceID != -1) {
			method119(anInt945, openInterfaceID);
			RSInterface rsinterface = RSInterface.interfaceCache[openInterfaceID];
			if (currentScreenMode == ScreenMode.FIXED)
				drawInterface(0, 0, rsinterface, 0);
			else if (centerInterface())
				drawInterface(0, (currentGameWidth / 2) - 256, rsinterface,
						currentScreenMode == ScreenMode.FIXED ? 0 : (currentGameHeight / 2) - 167);
			else
				drawInterface(0, 0, rsinterface, 0);
		}
		method70();

		if (anInt1055 == 1)
			multiOverlay.drawSprite(currentScreenMode == ScreenMode.FIXED ? 472 : (currentGameWidth - 45),
					currentScreenMode == ScreenMode.FIXED ? 296 : 165);

		if (!menuOpen) {
			processRightClick();
			drawTooltip();
		} else if (menuScreenArea == 0)
			drawMenu();

		if (fpsOn) {
			// char c = '\u01FB';
			int yPosition = 30;
			int xPosition = currentScreenMode == ScreenMode.FIXED ? 515 : currentGameWidth - 222;
			int textColor = 0x255;
			if (super.fps < 15)
				textColor = 0xff0000;
			aTextDrawingArea_1271.method380("Fps:" + super.fps, xPosition, textColor, yPosition);
			yPosition += 15;
			Runtime runtime = Runtime.getRuntime();
			int j1 = (int) ((runtime.totalMemory() - runtime.freeMemory()) / 1024L);
			textColor = 0xffff00;
			if (j1 > 0x2000000 && lowMem)
				textColor = 0xff0000;
			aTextDrawingArea_1271.method380("Mem:" + j1 + "k", xPosition, 0xffff00, yPosition);
			yPosition += 15;
		}
		int x = baseX + (myPlayer.x - 6 >> 7);
		int y = baseY + (myPlayer.y - 6 >> 7);
		int mapx = mapRegionsX; // map region x
		int mapy = mapRegionsY; // map region y

		if (clientData) {
			if (super.fps < 15) {

			}
			Runtime runtime = Runtime.getRuntime();
			int j1 = (int) ((runtime.totalMemory() - runtime.freeMemory()) / 1024L);
			if (j1 > 0x2000000 && lowMem) {

			}
			aTextDrawingArea_1271.method385(0x00FF00, "Players Nearby: " + playerCount, 27, 5);
			aTextDrawingArea_1271.method385(0x00FF00, "Npcs Nearby: " + npcCount, 41, 5);

			if (mapx > 1000 || mapy > 1000) {
				aTextDrawingArea_1271.method385(0xffff00, "Current Region: " + mapx + ", " + mapy, 55, 5);
			} else {
				aTextDrawingArea_1271.method385(0xffff00, "Current Region: 0" + mapx + ", 0" + mapy, 55, 5);
			}
			for (int num = 0; num < anIntArray1235.length; num++) {
				int[] flo = anIntArray1235;
				aTextDrawingArea_1271.method385(0xffff00, "Floor map: " + Arrays.toString(flo), 69, 5);
			}
			for (int num = 0; num < anIntArray1236.length; num++) {
				int[] obj = anIntArray1236;
				aTextDrawingArea_1271.method385(0xffff00, "Object map: " + Arrays.toString(obj), 83, 5);
				// output: "Object map: "[1, 3, 5, 7, 9]"
			}

			aTextDrawingArea_1271.method385(0xffff00, "Map Data: " + anIntArray1235[0] + ".dat", 97, 5);
			aTextDrawingArea_1271.method385(0xffff00, "Fps: " + super.fps, 111, 5);
			aTextDrawingArea_1271.method385(0xffff00, "Memory Used: " + j1 + "k", 125, 5);
			aTextDrawingArea_1271.method385(0xffff00,
					"Mouse Position: X: " + super.mouseX + " , Mouse Y: " + super.mouseY, 139, 5);
			aTextDrawingArea_1271.method385(0xffff00, "Coordinates: X: " + x + ", Y: " + y, 153, 5);

			aTextDrawingArea_1271.method385(0xffff00,
					"Camera Position: X: " + xCameraPos + ", Y: " + yCameraPos + ", Z: " + zCameraPos, 167, 5);
			aTextDrawingArea_1271.method385(0xffff00, "Camera Curve: X: " + xCameraCurve + ", Y: " + yCameraCurve, 181,
					5);

			// private int xCameraPos;ds
			// private int zCameraPos;
			// private int yCameraPos;
			// private int yCameraCurve;
			// private int xCameraCurve;
		}
		if (anInt1104 != 0) {
			int j = anInt1104 / 50;
			int l = j / 60;
			j %= 60;
			int yPosition = currentScreenMode == ScreenMode.FIXED ? 329 : currentGameHeight - 165;
			if (j < 10)
				aTextDrawingArea_1271.method385(0xffff00, "System update in: " + l + ":0" + j, yPosition, 5);
			else
				aTextDrawingArea_1271.method385(0xffff00, "System update in: " + l + ":" + j, yPosition, 5);

			anInt849++;
			if (anInt849 > 75) {
				anInt849 = 0;
				stream.createFrame(148);
			}
		}
	}

	private void addIgnore(long l) {
		try {
			if (l == 0L)
				return;
			if (ignoreCount >= 100) {
				pushMessage("Your ignore list is full. Max of 100 hit", 0, "");
				return;
			}
			String s = TextClass.fixName(TextClass.nameForLong(l));
			for (int j = 0; j < ignoreCount; j++)
				if (ignoreListAsLongs[j] == l) {
					pushMessage(s + " is already on your ignore list", 0, "");
					return;
				}
			for (int k = 0; k < friendsCount; k++)
				if (friendsListAsLongs[k] == l) {
					pushMessage("Please remove " + s + " from your friend list first", 0, "");
					return;
				}

			ignoreListAsLongs[ignoreCount++] = l;
			needDrawTabArea = true;
			stream.createFrame(133);
			stream.writeQWord(l);
			return;
		} catch (RuntimeException runtimeexception) {
			Signlink.reporterror("45688, " + l + ", " + 4 + ", " + runtimeexception.toString());
		}
		throw new RuntimeException();
	}

	public void method114() {
		for (int i = -1; i < playerCount; i++) {
			int j;
			if (i == -1)
				j = myPlayerIndex;
			else
				j = playerIndices[i];
			Player player = playerArray[j];
			if (player != null)
				method96(player);
		}

	}

	private void method115() {
		if (loadingStage == 2) {
			boolean passedRequest = false;
			for (Class30_Sub1 class30_sub1 = (Class30_Sub1) aClass19_1179
					.reverseGetFirst(); class30_sub1 != null; class30_sub1 = (Class30_Sub1) aClass19_1179
							.reverseGetNext()) {
				if (class30_sub1.anInt1294 > 0)
					class30_sub1.anInt1294--;
				if (class30_sub1.anInt1294 == 0) {
					if (class30_sub1.anInt1299 < 0
							|| ObjectManager.method178(class30_sub1.anInt1299, class30_sub1.anInt1301)) {
						method142(class30_sub1.anInt1298, class30_sub1.anInt1295, class30_sub1.anInt1300,
								class30_sub1.anInt1301, class30_sub1.anInt1297, class30_sub1.anInt1296,
								class30_sub1.anInt1299);
						class30_sub1.unlink();
					}
				} else {
					if (class30_sub1.anInt1302 > 0)
						class30_sub1.anInt1302--;
					if (class30_sub1.anInt1302 == 0 && class30_sub1.anInt1297 >= 1 && class30_sub1.anInt1298 >= 1
							&& class30_sub1.anInt1297 <= 102 && class30_sub1.anInt1298 <= 102
							&& (class30_sub1.anInt1291 < 0
									|| ObjectManager.method178(class30_sub1.anInt1291, class30_sub1.anInt1293))) {
						method142(class30_sub1.anInt1298, class30_sub1.anInt1295, class30_sub1.anInt1292,
								class30_sub1.anInt1293, class30_sub1.anInt1297, class30_sub1.anInt1296,
								class30_sub1.anInt1291);
						class30_sub1.anInt1302 = -1;
						if (class30_sub1.anInt1291 == class30_sub1.anInt1299 && class30_sub1.anInt1299 == -1)
							class30_sub1.unlink();
						else if (class30_sub1.anInt1291 == class30_sub1.anInt1299
								&& class30_sub1.anInt1292 == class30_sub1.anInt1300
								&& class30_sub1.anInt1293 == class30_sub1.anInt1301)
							class30_sub1.unlink();
						passedRequest = true;
					}
				}
			}
			if (passedRequest)
				// anInt985 = plane;
				method24(plane);

		}
	}

	private void determineMenuSize() {
		int i = chatTextDrawingArea.getTextWidth("Choose Option");
		for (int j = 0; j < menuActionRow; j++) {
			int k = chatTextDrawingArea.getTextWidth(menuActionName[j]);
			if (k > i)
				i = k;
		}
		i += 8;
		int l = 15 * menuActionRow + 21;
		if (loggedIn) {
			if (currentScreenMode == ScreenMode.FIXED) {
				if (super.saveClickX > 4 && super.saveClickY > 4 && super.saveClickX < 516 && super.saveClickY < 338) {
					int i1 = super.saveClickX - 4 - i / 2;
					if (i1 + i > 512)
						i1 = 512 - i;
					if (i1 < 0)
						i1 = 0;
					int l1 = super.saveClickY - 4;
					if (l1 + l > 334)
						l1 = 334 - l;
					if (l1 < 0)
						l1 = 0;
					menuOpen = true;
					menuScreenArea = 0;
					menuOffsetX = i1;
					menuOffsetY = l1;
					menuWidth = i;
					menuHeight = 15 * menuActionRow + 22;
				}
				if (super.saveClickX > 519 && super.saveClickY > 168 && super.saveClickX < 765
						&& super.saveClickY < 503) {
					int j1 = super.saveClickX - 519 - i / 2;
					if (j1 < 0)
						j1 = 0;
					else if (j1 + i > 245)
						j1 = 245 - i;
					int i2 = super.saveClickY - 168;
					if (i2 < 0)
						i2 = 0;
					else if (i2 + l > 333)
						i2 = 333 - l;
					menuOpen = true;
					if (removeShiftDropOnMenuOpen && menuActionCmd3[menuActionRow - 1] == 3214) {
						 removeShiftDropOnMenuOpen = false;
						 processRightClick();
					}
					menuScreenArea = 1;
					menuOffsetX = j1;
					menuOffsetY = i2;
					menuWidth = i;
					menuHeight = 15 * menuActionRow + 22;
				}

				if (super.saveClickX > 0 && super.saveClickY > 338 && super.saveClickX < 516
						&& super.saveClickY < 503) {
					int k1 = super.saveClickX - 0 - i / 2;
					if (k1 < 0)
						k1 = 0;
					else if (k1 + i > 516)
						k1 = 516 - i;
					int j2 = super.saveClickY - 338;
					if (j2 < 0)
						j2 = 0;
					else if (j2 + l > 165)
						j2 = 165 - l;
					menuOpen = true;
					menuScreenArea = 2;
					menuOffsetX = k1;
					menuOffsetY = j2;
					menuWidth = i;
					menuHeight = 15 * menuActionRow + 22;
				}

				if (super.saveClickX > 519 && super.saveClickY > 0 && super.saveClickX < 765
						&& super.saveClickY < 168) {
					int j1 = super.saveClickX - 519 - i / 2;
					if (j1 < 0)
						j1 = 0;
					else if (j1 + i > 245)
						j1 = 245 - i;
					int i2 = super.saveClickY - 0;
					if (i2 < 0)
						i2 = 0;
					else if (i2 + l > 168)
						i2 = 168 - l;
					menuOpen = true;
					menuScreenArea = 3;
					menuOffsetX = j1;
					menuOffsetY = i2;
					menuWidth = i;
					menuHeight = 15 * menuActionRow + 22;
				}
			} else {
				if (super.saveClickX > 0 && super.saveClickY > 0 && super.saveClickX < currentGameWidth
						&& super.saveClickY < currentGameHeight) {
					int i1 = super.saveClickX - 0 - i / 2;
					if (i1 + i > currentGameWidth)
						i1 = currentGameWidth - i;
					if (i1 < 0)
						i1 = 0;
					int l1 = super.saveClickY - 0;
					if (l1 + l > currentGameHeight)
						l1 = currentGameHeight - l;
					if (l1 < 0)
						l1 = 0;
					menuOpen = true;
					if (removeShiftDropOnMenuOpen && menuActionCmd3[menuActionRow - 1] == 3214) {
						   removeShiftDropOnMenuOpen = false;
						   processRightClick();
					}
					menuScreenArea = 0;
					menuOffsetX = i1;
					menuOffsetY = l1;
					menuWidth = i;
					menuHeight = 15 * menuActionRow + 22;
				}
			}
		} else {
			if (super.saveClickX > 0 && super.saveClickY > 0 && super.saveClickX < currentGameWidth
					&& super.saveClickY < currentGameHeight) {
				int i1 = super.saveClickX - 0 - i / 2;
				if (i1 + i > currentGameWidth)
					i1 = currentGameWidth - i;
				if (i1 < 0)
					i1 = 0;
				int l1 = super.saveClickY - 0;
				if (l1 + l > currentGameHeight)
					l1 = currentGameHeight - l;
				if (l1 < 0)
					l1 = 0;
				menuOpen = true;
				menuScreenArea = 0;
				menuOffsetX = i1;
				menuOffsetY = l1;
				menuWidth = i;
				menuHeight = 15 * menuActionRow + 22;
			}
		}
	}

	public void method117(Stream stream) {
		stream.initBitAccess();
		int j = stream.readBits(1);
		if (j == 0)
			return;
		int k = stream.readBits(2);
		if (k == 0) {
			anIntArray894[anInt893++] = myPlayerIndex;
			return;
		}
		if (k == 1) {
			int l = stream.readBits(3);
			myPlayer.moveInDir(false, l);
			int k1 = stream.readBits(1);
			if (k1 == 1)
				anIntArray894[anInt893++] = myPlayerIndex;
			return;
		}
		if (k == 2) {
			int i1 = stream.readBits(3);
			myPlayer.moveInDir(true, i1);
			int l1 = stream.readBits(3);
			myPlayer.moveInDir(true, l1);
			int j2 = stream.readBits(1);
			if (j2 == 1)
				anIntArray894[anInt893++] = myPlayerIndex;
			return;
		}
		if (k == 3) {
			plane = stream.readBits(2);
			int j1 = stream.readBits(1);
			int i2 = stream.readBits(1);
			if (i2 == 1)
				anIntArray894[anInt893++] = myPlayerIndex;
			int k2 = stream.readBits(7);
			int l2 = stream.readBits(7);
			myPlayer.setPos(l2, k2, j1 == 1);
		}
	}

	public void nullLoader() {
		aBoolean831 = false;
		while (drawingFlames) {
			aBoolean831 = false;
			try {
				Thread.sleep(50L);
			} catch (Exception _ex) {
			}
		}
		aBackgroundArray1152s = null;
		anIntArray850 = null;
		anIntArray851 = null;
		anIntArray852 = null;
		anIntArray853 = null;
		anIntArray1190 = null;
		anIntArray1191 = null;
		anIntArray828 = null;
		anIntArray829 = null;
		aClass30_Sub2_Sub1_Sub1_1201 = null;
		aClass30_Sub2_Sub1_Sub1_1202 = null;
	}

	private boolean method119(int i, int j) {
		boolean flag1 = false;
		RSInterface class9 = RSInterface.interfaceCache[j];
		for (int k = 0; k < class9.children.length; k++) {
			if (class9.children[k] == -1)
				break;
			RSInterface class9_1 = RSInterface.interfaceCache[class9.children[k]];
			if (class9_1.type == 1)
				flag1 |= method119(i, class9_1.id);
			if (class9_1.type == 6 && (class9_1.anInt257 != -1 || class9_1.anInt258 != -1)) {
				boolean flag2 = interfaceIsSelected(class9_1);
				int l;
				if (flag2)
					l = class9_1.anInt258;
				else
					l = class9_1.anInt257;
				if (l != -1) {
					AnimationDefinition animation = AnimationDefinition.anims[l];
					for (class9_1.anInt208 += i; class9_1.anInt208 > animation.method258(class9_1.anInt246);) {
						class9_1.anInt208 -= animation.method258(class9_1.anInt246) + 1;
						class9_1.anInt246++;
						if (class9_1.anInt246 >= animation.anInt352) {
							class9_1.anInt246 -= animation.anInt356;
							if (class9_1.anInt246 < 0 || class9_1.anInt246 >= animation.anInt352)
								class9_1.anInt246 = 0;
						}
						flag1 = true;
					}

				}
			}
		}

		return flag1;
	}

	private int method120() {
		if (removeRoofs)
			return plane;
		int j = 3;
		if (yCameraCurve < 310) {
			int k = xCameraPos >> 7;
			int l = yCameraPos >> 7;
			int i1 = myPlayer.x >> 7;
			int j1 = myPlayer.y >> 7;
			if ((byteGroundArray[plane][k][l] & 4) != 0)
				j = plane;
			int k1;
			if (i1 > k)
				k1 = i1 - k;
			else
				k1 = k - i1;
			int l1;
			if (j1 > l)
				l1 = j1 - l;
			else
				l1 = l - j1;
			if (k1 > l1) {
				int i2 = (l1 * 0x10000) / k1;
				int k2 = 32768;
				while (k != i1) {
					if (k < i1)
						k++;
					else if (k > i1)
						k--;
					if ((byteGroundArray[plane][k][l] & 4) != 0)
						j = plane;
					k2 += i2;
					if (k2 >= 0x10000) {
						k2 -= 0x10000;
						if (l < j1)
							l++;
						else if (l > j1)
							l--;
						if ((byteGroundArray[plane][k][l] & 4) != 0)
							j = plane;
					}
				}
			} else {
				int j2 = (k1 * 0x10000) / l1;
				int l2 = 32768;
				while (l != j1) {
					if (l < j1)
						l++;
					else if (l > j1)
						l--;
					if ((byteGroundArray[plane][k][l] & 4) != 0)
						j = plane;
					l2 += j2;
					if (l2 >= 0x10000) {
						l2 -= 0x10000;
						if (k < i1)
							k++;
						else if (k > i1)
							k--;
						if ((byteGroundArray[plane][k][l] & 4) != 0)
							j = plane;
					}
				}
			}
		}
		if ((byteGroundArray[plane][myPlayer.x >> 7][myPlayer.y >> 7] & 4) != 0)
			j = plane;
		return j;
	}

	private int method121() {
		if (removeRoofs)
			return plane;
		int j = method42(plane, yCameraPos, xCameraPos);
		if (j - zCameraPos < 800 && (byteGroundArray[plane][xCameraPos >> 7][yCameraPos >> 7] & 4) != 0)
			return plane;
		else
			return 3;
	}

	private void delIgnore(long l) {
		try {
			if (l == 0L)
				return;
			for (int j = 0; j < ignoreCount; j++)
				if (ignoreListAsLongs[j] == l) {
					ignoreCount--;
					needDrawTabArea = true;
					System.arraycopy(ignoreListAsLongs, j + 1, ignoreListAsLongs, j, ignoreCount - j);

					stream.createFrame(74);
					stream.writeQWord(l);
					return;
				}

			return;
		} catch (RuntimeException runtimeexception) {
			Signlink.reporterror("47229, " + 3 + ", " + l + ", " + runtimeexception.toString());
		}
		throw new RuntimeException();
	}

	@SuppressWarnings("unused")
	private void chatJoin(long l) {
		try {
			if (l == 0L)
				return;
			stream.createFrame(60);
			stream.writeQWord(l);
			return;
		} catch (RuntimeException runtimeexception) {
			Signlink.reporterror("47229, " + 3 + ", " + l + ", " + runtimeexception.toString());
		}
		throw new RuntimeException();

	}

	@Override
	public String getParameter(String s) {
		if (Signlink.mainapp != null)
			return Signlink.mainapp.getParameter(s);
		else
			return super.getParameter(s);
	}

	public void adjustVolume(boolean flag, int i) {
		Signlink.midivol = i;
		if (flag)
			Signlink.midi = "voladjust";
	}

	private int extractInterfaceValues(RSInterface class9, int j) {
		if (class9.valueIndexArray == null || j >= class9.valueIndexArray.length)
			return -2;
		try {
			int ai[] = class9.valueIndexArray[j];
			int k = 0;
			int l = 0;
			int i1 = 0;
			do {
				int j1 = ai[l++];
				int k1 = 0;
				byte byte0 = 0;
				if (j1 == 0)
					return k;
				if (j1 == 1)
					k1 = currentStats[ai[l++]];
				if (j1 == 2)
					k1 = maxStats[ai[l++]];
				if (j1 == 3)
					k1 = currentExp[ai[l++]];
				if (j1 == 4) {
					RSInterface class9_1 = RSInterface.interfaceCache[ai[l++]];
					int k2 = ai[l++];
					if (k2 >= 0 && k2 < ItemDefinition.totalItems
							&& (!ItemDefinition.forID(k2).membersObject || isMembers)) {
						for (int j3 = 0; j3 < class9_1.inv.length; j3++) {
							if (class9_1.inv[j3] == k2 + 1)
								k1 += class9_1.invStackSizes[j3];
						}
						if ((runePouch[0][0] + 1) == (k2 + 1)) {
							k1 += runePouch[0][1];
						}	
						if ((runePouch[1][0] + 1) == (k2 + 1)) {
							k1 += runePouch[1][1];
						}
						if ((runePouch[2][0] + 1) == (k2 + 1)) {
							k1 += runePouch[2][1];
						}
					}
				}
				if (j1 == 5)
					k1 = variousSettings[ai[l++]];
				if (j1 == 6)
					k1 = anIntArray1019[maxStats[ai[l++]] - 1];
				if (j1 == 7)
					k1 = (variousSettings[ai[l++]] * 100) / 46875;
				if (j1 == 8)
					k1 = myPlayer.combatLevel;
				if (j1 == 9) {
					for (int l1 = 0; l1 < Skills.SKILLS_COUNT; l1++)
						if (Skills.SKILLS_ENABLED[l1])
							k1 += maxStats[l1];
				}
				if (j1 == 10) {
					RSInterface class9_2 = RSInterface.interfaceCache[ai[l++]];
					int l2 = ai[l++] + 1;
					if (l2 >= 0 && l2 < ItemDefinition.totalItems
							&& (!ItemDefinition.forID(l2).membersObject || isMembers)) {
						for (int k3 = 0; k3 < class9_2.inv.length; k3++) {
							if (class9_2.inv[k3] != l2)
								continue;
							k1 = 0x3b9ac9ff;
							break;
						}

					}
				}
				if (j1 == 11)
					k1 = energy;
				if (j1 == 12)
					k1 = weight;
				if (j1 == 13) {
					int i2 = variousSettings[ai[l++]];
					int i3 = ai[l++];
					k1 = (i2 & 1 << i3) == 0 ? 0 : 1;
				}
				if (j1 == 14) {
					int j2 = ai[l++];
					VarBit varBit = VarBit.cache[j2];
					int l3 = varBit.anInt648;
					int i4 = varBit.anInt649;
					int j4 = varBit.anInt650;
					int k4 = anIntArray1232[j4 - i4];
					k1 = variousSettings[l3] >> i4 & k4;
				}
				if (j1 == 15)
					byte0 = 1;
				if (j1 == 16)
					byte0 = 2;
				if (j1 == 17)
					byte0 = 3;
				if (j1 == 18)
					k1 = (myPlayer.x >> 7) + baseX;
				if (j1 == 19)
					k1 = (myPlayer.y >> 7) + baseY;
				if (j1 == 20)
					k1 = ai[l++];
				if (byte0 == 0) {
					if (i1 == 0)
						k += k1;
					if (i1 == 1)
						k -= k1;
					if (i1 == 2 && k1 != 0)
						k /= k1;
					if (i1 == 3)
						k *= k1;
					i1 = 0;
				} else {
					i1 = byte0;
				}
			} while (true);
		} catch (Exception _ex) {
			return -1;
		}
	}

	public void drawTooltip() {
		if (menuActionRow < 2 && itemSelected == 0 && spellSelected == 0)
			return;
		String s;
		if (itemSelected == 1 && menuActionRow < 2)
			s = "Use " + selectedItemName + " with...";
		else if (spellSelected == 1 && menuActionRow < 2)
			s = spellTooltip + "...";
		else
			s = menuActionName[menuActionRow - 1];
		if (menuActionRow > 2)
			s = s + "@whi@ / " + (menuActionRow - 2) + " more options";
		chatTextDrawingArea.method390(4, 0xffffff, s, loopCycle / 1000, 15);
	}

	private Sprite compassImage;
	private Sprite[] mapArea = new Sprite[5];

	private Sprite eventIcon;

	private void drawMinimap() {
		if (currentScreenMode == ScreenMode.FIXED)
			mapAreaGraphicsBuffer.initDrawingArea();

		/** Black minimap **/
		if (minimapState == 2) {
			if (currentScreenMode == ScreenMode.FIXED) {
				mapArea[1].drawSprite(0, 0);
				mapArea[3].drawSprite(3, 0);
			} else {
				mapArea[2].drawSprite(currentGameWidth - 183, 0);
				mapArea[4].drawSprite(currentGameWidth - 160, 8);
			}
			compassImage.method352(33, viewRotation, anIntArray1057, 256, anIntArray968,
					(currentScreenMode == ScreenMode.FIXED ? 25 : 25), 4,
					(currentScreenMode == ScreenMode.FIXED ? 29 : currentGameWidth - 178), 33, 25);

			if (drawOrbs)
				drawOrbs(currentScreenMode == ScreenMode.FIXED ? 0 : currentGameWidth - 217);

			mainGameGraphicsBuffer.initDrawingArea();
			return;
		}

		int i = viewRotation + minimapRotation & 0x7ff;
		int j = 48 + myPlayer.x / 32;
		int l2 = 464 - myPlayer.y / 32;

		int positionX = (currentScreenMode == ScreenMode.FIXED ? 9 : 7);
		int positionY = (currentScreenMode == ScreenMode.FIXED ? 54 : currentGameWidth - 160);

		minimapImage.method352(151, i, anIntArray1229, 256 + minimapZoom, anIntArray1052, l2, positionX, positionY, 146,
				j);

		for (int j5 = 0; j5 < mapIconAmount; j5++) {
			int k = (anIntArray1072[j5] * 4 + 2) - myPlayer.x / 32;
			int i3 = (anIntArray1073[j5] * 4 + 2) - myPlayer.y / 32;
			markMinimap(mapIconSprite[j5], k, i3);
		}
		if (Configuration.HALLOWEEN) {
			switch (plane) {
			case 0:
				markMinimap(eventIcon, ((3088 - baseX) * 4 + 2) - myPlayer.x / 32,
						((3494 - baseY) * 4 + 2) - myPlayer.y / 32);
				markMinimap(eventIcon, ((2607 - baseX) * 4 + 2) - myPlayer.x / 32,
						((4773 - baseY) * 4 + 2) - myPlayer.y / 32);
				markMinimap(eventIcon, ((3103 - baseX) * 4 + 2) - myPlayer.x / 32,
						((3482 - baseY) * 4 + 2) - myPlayer.y / 32);
				break;
			}
		}
		if (Configuration.CHRISTMAS && Configuration.CHRISTMAS_EVENT) {
			switch (plane) {
			case 0:
				markMinimap(minimapIcons[0], ((3086 - baseX) * 4 + 2) - myPlayer.x / 32,
						((3498 - baseY) * 4 + 2) - myPlayer.y / 32);
				markMinimap(minimapIcons[0], ((2832 - baseX) * 4 + 2) - myPlayer.x / 32,
						((3798 - baseY) * 4 + 2) - myPlayer.y / 32);
				markMinimap(minimapIcons[0], ((2982 - baseX) * 4 + 2) - myPlayer.x / 32,
						((3643 - baseY) * 4 + 2) - myPlayer.y / 32);
				break;

			case 2:
				markMinimap(minimapIcons[0], ((2827 - baseX) * 4 + 2) - myPlayer.x / 32,
						((3810 - baseY) * 4 + 2) - myPlayer.y / 32);
				break;
			}
		}

		for (int k5 = 0; k5 < 104; k5++) {
			for (int l5 = 0; l5 < 104; l5++) {
				NodeList class19 = groundArray[plane][k5][l5];
				if (class19 != null) {
					int l = (k5 * 4 + 2) - myPlayer.x / 32;
					int j3 = (l5 * 4 + 2) - myPlayer.y / 32;
					markMinimap(mapDotItem, l, j3);
				}
			}
		}

		for (int i6 = 0; i6 < npcCount; i6++) {
			NPC npc = npcArray[npcIndices[i6]];
			if (npc != null && npc.isVisible()) {
				NpcDefinition entityDef = npc.desc;
				if (entityDef.childrenIDs != null)
					entityDef = entityDef.method161();
				if (entityDef != null && entityDef.onMinimap && entityDef.aBoolean84) {
					int i1 = npc.x / 32 - myPlayer.x / 32;
					int k3 = npc.y / 32 - myPlayer.y / 32;
					markMinimap(mapDotNPC, i1, k3);
				}
			}
		}

		for (int j6 = 0; j6 < playerCount; j6++) {
			Player player = playerArray[playerIndices[j6]];
			if (player != null && player.isVisible()) {
				int j1 = player.x / 32 - myPlayer.x / 32;
				int l3 = player.y / 32 - myPlayer.y / 32;
				boolean flag1 = false;
				boolean flag3 = false;
				String clanname;
				for (int j3 = 0; j3 < clanList.length; j3++) {
					if (clanList[j3] == null)
						continue;
					clanname = clanList[j3];
					if (clanname.startsWith("<clan"))
						clanname = clanname.substring(clanname.indexOf(">") + 1, clanname.length());
					if (!clanname.equalsIgnoreCase(player.name))
						continue;
					flag3 = true;
					break;
				}
				long l6 = TextClass.longForName(player.name);
				for (int k6 = 0; k6 < friendsCount; k6++) {
					if (l6 != friendsListAsLongs[k6] || friendsNodeIDs[k6] == 0)
						continue;
					flag1 = true;
					break;
				}
				boolean flag2 = false;
				if (myPlayer.team != 0 && player.team != 0 && myPlayer.team == player.team)
					flag2 = true;
				if (flag1)
					markMinimap(mapDotFriend, j1, l3);
				else if (flag3)
					markMinimap(mapDotClan, j1, l3);
				else if (flag2)
					markMinimap(mapDotTeam, j1, l3);
				else
					markMinimap(mapDotPlayer, j1, l3);
			}
		}

		if (anInt855 != 0 && loopCycle % 20 < 10) {
			if (anInt855 == 1 && anInt1222 >= 0 && anInt1222 < npcArray.length) {
				NPC class30_sub2_sub4_sub1_sub1_1 = npcArray[anInt1222];
				if (class30_sub2_sub4_sub1_sub1_1 != null) {
					int k1 = class30_sub2_sub4_sub1_sub1_1.x / 32 - myPlayer.x / 32;
					int i4 = class30_sub2_sub4_sub1_sub1_1.y / 32 - myPlayer.y / 32;
					method81(mapMarker, i4, k1);
				}
			}
			if (anInt855 == 2) {
				int l1 = ((anInt934 - baseX) * 4 + 2) - myPlayer.x / 32;
				int j4 = ((anInt935 - baseY) * 4 + 2) - myPlayer.y / 32;
				method81(mapMarker, j4, l1);
			}
			if (anInt855 == 10 && anInt933 >= 0 && anInt933 < playerArray.length) {
				Player class30_sub2_sub4_sub1_sub2_1 = playerArray[anInt933];
				if (class30_sub2_sub4_sub1_sub2_1 != null) {
					int i2 = class30_sub2_sub4_sub1_sub2_1.x / 32 - myPlayer.x / 32;
					int k4 = class30_sub2_sub4_sub1_sub2_1.y / 32 - myPlayer.y / 32;
					method81(mapMarker, k4, i2);
				}
			}
		}

		if (destX != 0) {
			int j2 = (destX * 4 + 2) - myPlayer.x / 32;
			int l4 = (destY * 4 + 2) - myPlayer.y / 32;
			markMinimap(mapFlag, j2, l4);
		}

		DrawingArea.drawPixels(3, (currentScreenMode == ScreenMode.FIXED ? 83 : 80),
				(currentScreenMode == ScreenMode.FIXED ? 127 : currentGameWidth - 88), 0xffffff, 3);

		if (currentScreenMode == ScreenMode.FIXED) {
			mapArea[0].drawSprite(3, 0);
			mapArea[1].drawSprite(0, 0);
		} else
			mapArea[2].drawSprite(currentGameWidth - 183, 0);

		compassImage.method352(33, viewRotation, anIntArray1057, 256, anIntArray968,
				(currentScreenMode == ScreenMode.FIXED ? 25 : 25), 4,
				(currentScreenMode == ScreenMode.FIXED ? 29 : currentGameWidth - 178), 33, 25);

		if (drawOrbs)
			drawOrbs(currentScreenMode == ScreenMode.FIXED ? 0 : currentGameWidth - 217);

		if (menuOpen && menuScreenArea == 3)
			drawMenu();

		mainGameGraphicsBuffer.initDrawingArea();
	}

	private void drawOrbs(int xOffset) {
		drawHpOrb(xOffset);
		drawPrayerOrb(xOffset);
		drawRunOrb(xOffset);
		
		if (currentScreenMode == ScreenMode.FIXED) {
			cacheSprite[counterHover ? 1 : 0].drawSprite(drawOrbs && currentScreenMode == ScreenMode.FIXED ? 1 : currentGameWidth - 208, currentScreenMode == ScreenMode.FIXED ? 24 : 23);
		} else {
			cacheSprite[counterHover ? 0 : 1].drawSprite(drawOrbs && currentScreenMode == ScreenMode.FIXED ? 1 : currentGameWidth - 208, currentScreenMode == ScreenMode.FIXED ? 24 : 23);
		}
		if(currentScreenMode == ScreenMode.FIXED) {
			cacheSprite[worldHover ? 5 : 4].drawSprite(202, 20);
		} else {
			cacheSprite[worldHover ? 3 : 2].drawSprite(Client.currentGameWidth - 118, 154);
		}
	}

	private void drawHpOrb(int xOffset) {
		if (currentScreenMode != ScreenMode.FIXED)
			xOffset += 4;
		int current = Integer.parseInt(RSInterface.interfaceCache[4016].message.replaceAll("%", ""));
		int maximum = Integer.parseInt(RSInterface.interfaceCache[4017].message.replaceAll("%", ""));
		int percent = (int) (((double) current / (double) maximum) * 100D);
		minimapSprites[0].drawSprite(0 + xOffset, 41);

		switch (myPlayer.getHealthState()) {
		case 1:
			minimapSprites[12].drawSprite(27 + xOffset, 45);
			break;
		case 2:
			minimapSprites[13].drawSprite(27 + xOffset, 45);
			break;
		default:
			minimapSprites[3].drawSprite(27 + xOffset, 45);
			break;
		}
		new Sprite(minimapSprites[2], 27, getOrbFill(percent)).drawSprite(27 + xOffset, 45);
		if (percent <= 20) {
			if (loopCycle % 20 < 10) {
				minimapSprites[8].drawSprite(33 + xOffset, 51);
			}
		} else {
			minimapSprites[8].drawSprite(33 + xOffset, 51);
		}
		smallText.method382(getOrbTextColor(percent), 15 + xOffset, Integer.toString(current), 67, true);
	}

	private void drawPrayerOrb(int xOffset) {
		if (currentScreenMode != ScreenMode.FIXED)
			xOffset += 2;
		int current = Integer.parseInt(RSInterface.interfaceCache[4012].message.replaceAll("%", ""));
		int maximum = Integer.parseInt(RSInterface.interfaceCache[4013].message.replaceAll("%", ""));
		int percent = (int) (((double) current / (double) maximum) * 100D);
		minimapSprites[0].drawSprite(0 + xOffset, 86);
		minimapSprites[4].drawSprite(27 + xOffset, 90);
		new Sprite(minimapSprites[2], 27, getOrbFill(percent)).drawSprite(27 + xOffset, 90);
		minimapSprites[9].drawSprite(30 + xOffset, 93);
		smallText.method382(getOrbTextColor(percent), 15 + xOffset, Integer.toString(current), 112, true);
	}

	private void drawRunOrb(int xOffset) {
		if (currentScreenMode != ScreenMode.FIXED)
			xOffset -= 10;
		int current = Integer.parseInt(RSInterface.interfaceCache[149].message.replaceAll("%", ""));
		int percent = (int) (((double) current / 100D) * 100D);
		boolean running = anIntArray1045[173] == 1;
		minimapSprites[0].drawSprite(30 + xOffset, 123);
		if (running) {
			minimapSprites[6].drawSprite(57 + xOffset, 127);
		} else {
			minimapSprites[5].drawSprite(57 + xOffset, 127);
		}
		new Sprite(minimapSprites[2], 27, getOrbFill(percent)).drawSprite(57 + xOffset, 127);
		if (running) {
			minimapSprites[11].drawSprite(63 + xOffset, 130);
		} else {
			minimapSprites[10].drawSprite(63 + xOffset, 130);
		}
		smallText.method382(getOrbTextColor(percent), 45 + xOffset, Integer.toString(current), 149, true);
	}

	public boolean runClicked = false;

	public int getOrbTextColor(int i) {
		if (i >= 75 && i <= 0x7fffffff)
			return 65280;
		if (i >= 50 && i <= 74)
			return 0xffff00;
		return i < 25 || i > 49 ? 0xff0000 : 0xff981f;
	}

	public int getOrbFill(int i) {
		if (i <= 0x7fffffff && i >= 97)
			return 0;
		if (i <= 96 && i >= 93)
			return 1;
		if (i <= 92 && i >= 89)
			return 2;
		if (i <= 88 && i >= 85)
			return 3;
		if (i <= 84 && i >= 81)
			return 4;
		if (i <= 80 && i >= 77)
			return 5;
		if (i <= 76 && i >= 73)
			return 6;
		if (i <= 72 && i >= 69)
			return 7;
		if (i <= 68 && i >= 65)
			return 8;
		if (i <= 64 && i >= 61)
			return 9;
		if (i <= 60 && i >= 57)
			return 10;
		if (i <= 56 && i >= 53)
			return 11;
		if (i <= 52 && i >= 49)
			return 12;
		if (i <= 48 && i >= 45)
			return 13;
		if (i <= 44 && i >= 41)
			return 14;
		if (i <= 40 && i >= 37)
			return 15;
		if (i <= 36 && i >= 33)
			return 16;
		if (i <= 32 && i >= 29)
			return 17;
		if (i <= 28 && i >= 25)
			return 18;
		if (i <= 24 && i >= 21)
			return 19;
		if (i <= 20 && i >= 17)
			return 20;
		if (i <= 16 && i >= 13)
			return 21;
		if (i <= 12 && i >= 9)
			return 22;
		if (i <= 8 && i >= 7)
			return 23;
		if (i <= 6 && i >= 5)
			return 24;
		if (i <= 4 && i >= 3)
			return 25;
		if (i <= 2 && i >= 1)
			return 26;
		return i > 0 ? 0 : 27;
	}

	public boolean logHover = false;
	public Sprite magicAuto;

	public Sprite[] xpOrb = new Sprite[2];
	public Sprite xpSprite;
	Sprite[] counter;

	private Queue<ExperienceDrop> experienceDrops = new LinkedList<>();

	private void processExperienceCounter() {
		if (loopCycle % 1 <= 1 && !experienceDrops.isEmpty()) {
			Collection<ExperienceDrop> remove = new ArrayList<>();
			for (ExperienceDrop drop : experienceDrops) {
				drop.pulse();
				if (drop.getYPosition() == -1) {
					experienceCounter += drop.getAmount();
					remove.add(drop);
				}
			}
			experienceDrops.removeAll(remove);
		}

		if (!drawExperienceCounter || openInterfaceID > -1) {
			return;
		}

		String experience = NumberFormat.getInstance().format(experienceCounter);
		newSmallFont.drawBasicString("Total: +" + experience,
				(currentScreenMode == ScreenMode.FIXED ? 518 : currentGameWidth - 222)
						- newSmallFont.getTextWidth("Total: +" + experience),
				13, 0xFFFFFF, 0x000000);
		for (ExperienceDrop drop : experienceDrops) {
			String text = drop.toString();
			int x = (currentScreenMode == ScreenMode.FIXED ? 518 : currentGameWidth - 222)
					- newSmallFont.getTextWidth(text);
			int y = drop.getYPosition();
			int transparency = drop.getTransparency();
			newSmallFont.drawString(text, x, y, 0xFF981F, 0x000000, (int) (transparency * 2.56D));
			for (int skill : drop.getSkills()) {
				Sprite sprite = smallXpSprites[skill];
				x -= sprite.myWidth + 3;
				y -= sprite.myHeight - 4;
				sprite.drawAdvancedTransparentSprite(x, y, transparency);
				y += sprite.myHeight - 4;
			}
		}
	}

	private boolean drawExperienceCounter = true;

	public void npcScreenPos(Entity entity, int i) {
		calcEntityScreenPos(entity.x, i, entity.y);
	}

	static int viewDistance;

	public void calcEntityScreenPos(int i, int j, int l) {
		if (i < 128 || l < 128 || i > 13056 || l > 13056) {
			spriteDrawX = -1;
			spriteDrawY = -1;
			return;
		}
		
		int i1 = method42(plane, l, i) - j;
		i -= xCameraPos;
		i1 -= zCameraPos;
		l -= yCameraPos;
		int j1 = Model.modelIntArray1[yCameraCurve];
		int k1 = Model.modelIntArray2[yCameraCurve];
		int l1 = Model.modelIntArray1[xCameraCurve];
		int i2 = Model.modelIntArray2[xCameraCurve];
		int j2 = l * l1 + i * i2 >> 16;
		l = l * i2 - i * l1 >> 16;
		i = j2;
		j2 = i1 * k1 - l * j1 >> 16;
		l = i1 * j1 + l * k1 >> 16;
		i1 = j2;
		if (l >= 50) {
			spriteDrawX = Texture.textureInt1 + (i << viewDistance) / l;
			spriteDrawY = Texture.textureInt2 + (i1 << viewDistance) / l;
		} else {
			spriteDrawX = -1;
			spriteDrawY = -1;
		}
	}

	public void method130(int j, int k, int l, int i1, int j1, int k1, int l1, int i2, int j2) {
		Class30_Sub1 class30_sub1 = null;
		for (Class30_Sub1 class30_sub1_1 = (Class30_Sub1) aClass19_1179
				.reverseGetFirst(); class30_sub1_1 != null; class30_sub1_1 = (Class30_Sub1) aClass19_1179
						.reverseGetNext()) {
			if (class30_sub1_1.anInt1295 != l1 || class30_sub1_1.anInt1297 != i2 || class30_sub1_1.anInt1298 != j1
					|| class30_sub1_1.anInt1296 != i1)
				continue;
			class30_sub1 = class30_sub1_1;
			break;
		}

		if (class30_sub1 == null) {
			class30_sub1 = new Class30_Sub1();
			class30_sub1.anInt1295 = l1;
			class30_sub1.anInt1296 = i1;
			class30_sub1.anInt1297 = i2;
			class30_sub1.anInt1298 = j1;
			method89(class30_sub1);
			aClass19_1179.insertHead(class30_sub1);
		}
		class30_sub1.anInt1291 = k;
		class30_sub1.anInt1293 = k1;
		class30_sub1.anInt1292 = l;
		class30_sub1.anInt1302 = j2;
		class30_sub1.anInt1294 = j;
	}

	private boolean interfaceIsSelected(RSInterface class9) {
		if (class9.anIntArray245 == null)
			return false;
		for (int i = 0; i < class9.anIntArray245.length; i++) {
			int j = extractInterfaceValues(class9, i);
			int k = class9.anIntArray212[i];
			if (class9.anIntArray245[i] == 2) {
				if (j >= k)
					return false;
			} else if (class9.anIntArray245[i] == 3) {
				if (j <= k)
					return false;
			} else if (class9.anIntArray245[i] == 4) {
				if (j == k)
					return false;
			} else if (j != k)
				return false;
		}

		return true;
	}

	public void doFlamesDrawing() {
		char c = '\u0100';
		if (anInt1040 > 0) {
			for (int i = 0; i < 256; i++)
				if (anInt1040 > 768)
					anIntArray850[i] = method83(anIntArray851[i], anIntArray852[i], 1024 - anInt1040);
				else if (anInt1040 > 256)
					anIntArray850[i] = anIntArray852[i];
				else
					anIntArray850[i] = method83(anIntArray852[i], anIntArray851[i], 256 - anInt1040);

		} else if (anInt1041 > 0) {
			for (int j = 0; j < 256; j++)
				if (anInt1041 > 768)
					anIntArray850[j] = method83(anIntArray851[j], anIntArray853[j], 1024 - anInt1041);
				else if (anInt1041 > 256)
					anIntArray850[j] = anIntArray853[j];
				else
					anIntArray850[j] = method83(anIntArray853[j], anIntArray851[j], 256 - anInt1041);

		} else {
			System.arraycopy(anIntArray851, 0, anIntArray850, 0, 256);

		}
		System.arraycopy(aClass30_Sub2_Sub1_Sub1_1201.myPixels, 0, aRSImageProducer_1110.canvasRaster, 0, 33920);

		int i1 = 0;
		int j1 = 1152;
		for (int k1 = 1; k1 < c - 1; k1++) {
			int l1 = (anIntArray969[k1] * (c - k1)) / c;
			int j2 = 22 + l1;
			if (j2 < 0)
				j2 = 0;
			i1 += j2;
			for (int l2 = j2; l2 < 128; l2++) {
				int j3 = anIntArray828[i1++];
				if (j3 != 0) {
					int l3 = j3;
					int j4 = 256 - j3;
					j3 = anIntArray850[j3];
					int l4 = aRSImageProducer_1110.canvasRaster[j1];
					aRSImageProducer_1110.canvasRaster[j1++] = ((j3 & 0xff00ff) * l3 + (l4 & 0xff00ff) * j4
							& 0xff00ff00) + ((j3 & 0xff00) * l3 + (l4 & 0xff00) * j4 & 0xff0000) >> 8;
				} else {
					j1++;
				}
			}

			j1 += j2;
		}

		aRSImageProducer_1110.drawGraphics(0, 0, super.graphics);
		System.arraycopy(aClass30_Sub2_Sub1_Sub1_1202.myPixels, 0, aRSImageProducer_1111.canvasRaster, 0, 33920);

		i1 = 0;
		j1 = 1176;
		for (int k2 = 1; k2 < c - 1; k2++) {
			int i3 = (anIntArray969[k2] * (c - k2)) / c;
			int k3 = 103 - i3;
			j1 += i3;
			for (int i4 = 0; i4 < k3; i4++) {
				int k4 = anIntArray828[i1++];
				if (k4 != 0) {
					int i5 = k4;
					int j5 = 256 - k4;
					k4 = anIntArray850[k4];
					int k5 = aRSImageProducer_1111.canvasRaster[j1];
					aRSImageProducer_1111.canvasRaster[j1++] = ((k4 & 0xff00ff) * i5 + (k5 & 0xff00ff) * j5
							& 0xff00ff00) + ((k4 & 0xff00) * i5 + (k5 & 0xff00) * j5 & 0xff0000) >> 8;
				} else {
					j1++;
				}
			}

			i1 += 128 - k3;
			j1 += 128 - k3 - i3;
		}

		aRSImageProducer_1111.drawGraphics(637, 0, super.graphics);
	}

	public void method134(Stream stream) {
		int j = stream.readBits(8);
		if (j < playerCount) {
			for (int k = j; k < playerCount; k++)
				anIntArray840[anInt839++] = playerIndices[k];

		}
		if (j > playerCount) {
			Signlink.reporterror(myUsername + " Too many players");
			throw new RuntimeException("eek");
		}
		playerCount = 0;
		for (int l = 0; l < j; l++) {
			int i1 = playerIndices[l];
			Player player = playerArray[i1];
			int j1 = stream.readBits(1);
			if (j1 == 0) {
				playerIndices[playerCount++] = i1;
				player.anInt1537 = loopCycle;
			} else {
				int k1 = stream.readBits(2);
				if (k1 == 0) {
					playerIndices[playerCount++] = i1;
					player.anInt1537 = loopCycle;
					anIntArray894[anInt893++] = i1;
				} else if (k1 == 1) {
					playerIndices[playerCount++] = i1;
					player.anInt1537 = loopCycle;
					int l1 = stream.readBits(3);
					player.moveInDir(false, l1);
					int j2 = stream.readBits(1);
					if (j2 == 1)
						anIntArray894[anInt893++] = i1;
				} else if (k1 == 2) {
					playerIndices[playerCount++] = i1;
					player.anInt1537 = loopCycle;
					int i2 = stream.readBits(3);
					player.moveInDir(true, i2);
					int k2 = stream.readBits(3);
					player.moveInDir(true, k2);
					int l2 = stream.readBits(1);
					if (l2 == 1)
						anIntArray894[anInt893++] = i1;
				} else if (k1 == 3)
					anIntArray840[anInt839++] = i1;
			}
		}
	}

	private enum LoginScreenState {
		LOGIN, DOWNLOADING_CLIENT;
	}

	private LoginScreenState loginState = LoginScreenState.LOGIN;

	private Map<String, Sprite> screenImages;

	private final void createScreenImages() {
		if (screenImages != null) {
			return;
		}
		screenImages = new HashMap<>();
		screenImages.put("background", new Sprite("loginscreen/2015/background_old"));
		screenImages.put("input_field_hover", new Sprite("loginscreen/2015/input_field_hover"));
		screenImages.put("input_field", new Sprite("loginscreen/2015/input_field"));
		screenImages.put("login_button_hover", new Sprite("loginscreen/2015/login_button_hover"));
		screenImages.put("login_button", new Sprite("loginscreen/2015/login_button"));
		screenImages.put("logo_title", new Sprite("loginscreen/2015/logo_title"));
		screenImages.put("logo", new Sprite("loginscreen/2015/logo"));
		screenImages.put("settings_icon", new Sprite("loginscreen/2015/settings_icon"));
		screenImages.put("settings_icon_hover", new Sprite("loginscreen/2015/settings_icon_hover"));
		screenImages.put("world_one_hover", new Sprite("loginscreen/2015/world_one_hover"));
		screenImages.put("world_one", new Sprite("loginscreen/2015/world_one"));
		screenImages.put("world_two_hover", new Sprite("loginscreen/2015/world_two_hover"));
		screenImages.put("world_two", new Sprite("loginscreen/2015/world_two"));
		screenImages.put("world_three_hover", new Sprite("loginscreen/2015/world_three_hover"));
		screenImages.put("world_three", new Sprite("loginscreen/2015/world_three"));
		screenImages.put("website_button_hover", new Sprite("loginscreen/2015/website_button_hover"));
		screenImages.put("website_button", new Sprite("loginscreen/2015/website_button"));
		screenImages.put("youtube_button_hover", new Sprite("loginscreen/2015/youtube_button_hover"));
		screenImages.put("youtube_button", new Sprite("loginscreen/2015/youtube_button"));
		screenImages.put("remember_button_off", new Sprite("loginscreen/2015/remember_button_off_1"));
		screenImages.put("remember_button_on", new Sprite("loginscreen/2015/remember_button_on_1"));
	}

	public void drawLoginScreen(boolean flag) {
		if (this.loggedIn)
			return;
		resetAllImageProducers();
		resetImageProducers();
		createScreenImages();
		loginScreenGraphicsBuffer.initDrawingArea();

		DrawingArea.drawPixels(0, 0, 765, 503, 0x000000);
		if (loginState == LoginScreenState.DOWNLOADING_CLIENT) {
			aTextDrawingArea_1271.drawText(0xCC9900, "Client is out of date.", 225, 375);
			aTextDrawingArea_1271.drawText(0xCC9900, "Downloading client, Please wait...", 240, 375);
			loginScreenGraphicsBuffer.drawGraphics(0, 0, super.graphics);
			return;
		}
		screenImages.get("background").drawSprite(0, 0);

		Sprite logo = screenImages.get("logo_title");

		logo.drawAdvancedSprite(765 / 2 - logo.getImage().getWidth(null) / 2, 31);

		if (loginScreenState == 0 || loginScreenState == 2) {
			int inputImageWidth = screenImages.get("input_field").getImage().getWidth(null) / 2;
			int loginButtonWidth = screenImages.get("login_button").getImage().getWidth(null) / 2;

			if (super.mouseX >= 263 && super.mouseX <= 505 && super.mouseY > 169 && super.mouseY < 211) {
				screenImages.get("input_field_hover").drawAdvancedSprite(765 / 2 - (inputImageWidth), 172);
			} else {
				screenImages.get("input_field").drawAdvancedSprite(765 / 2 - (inputImageWidth), 172);
			}

			if (super.mouseX >= 263 && super.mouseX <= 505 && super.mouseY > 231 && super.mouseY < 269) {
				screenImages.get("input_field_hover").drawAdvancedSprite(765 / 2 - (inputImageWidth), 233);
			} else {
				screenImages.get("input_field").drawAdvancedSprite(765 / 2 - (inputImageWidth), 233);
			}

			if (super.mouseX >= 302 && super.mouseX <= 464 && super.mouseY > 303 && super.mouseY < 345) {
				screenImages.get("login_button_hover").drawAdvancedSprite((765 / 2) - loginButtonWidth, 295);
			} else {
				screenImages.get("login_button").drawAdvancedSprite((765 / 2) - loginButtonWidth, 295);
			}

			if (super.mouseX >= 11 && super.mouseX <= 145 && super.mouseY > 467 && super.mouseY < 502) {
				screenImages.get("youtube_button_hover").drawAdvancedSprite(5, 458);
				aTextDrawingArea_1271.method382(0x6d5b42, 80, "Visit our youtube channel", 444, true);
			} else {
				screenImages.get("youtube_button").drawAdvancedSprite(5, 458);
			}

			if (super.mouseX >= 161 && super.mouseX <= 295 && super.mouseY > 467 && super.mouseY < 502) {
				screenImages.get("website_button_hover").drawAdvancedSprite(155, 458);
				aTextDrawingArea_1271.method382(0x6d5b42, 223, "Visit OS-V.ORG", 444, true);
			} else {
				screenImages.get("website_button").drawAdvancedSprite(155, 458);
			}

			if (Configuration.WORLD_CHANGER) {
				if (!worldSelect) {
					switch (currentWorld) {
					case 0:
						if (super.mouseX >= 311 && super.mouseX <= 445 && super.mouseY > 467 && super.mouseY < 502) {
							screenImages.get("world_one_hover").drawAdvancedSprite(305, 458);
							aTextDrawingArea_1271.method382(0x6d5b42, 375, "Switch to world 2", 444, true);
						} else {
							screenImages.get("world_one").drawAdvancedSprite(305, 458);
						}
						break;
						
					case 1:
						if (super.mouseX >= 311 && super.mouseX <= 445 && super.mouseY > 467 && super.mouseY < 502) {
							screenImages.get("world_two_hover").drawAdvancedSprite(305, 458);
							aTextDrawingArea_1271.method382(0x6d5b42, 375, "Switch to world 3", 444, true);
						} else {
							screenImages.get("world_two").drawAdvancedSprite(305, 458);
						}
						break;
						
					case 2:
						if (super.mouseX >= 311 && super.mouseX <= 445 && super.mouseY > 467 && super.mouseY < 502) {
							screenImages.get("world_three_hover").drawAdvancedSprite(305, 458);
							aTextDrawingArea_1271.method382(0x6d5b42, 375, "Switch to world 1", 444, true);
						} else {
							screenImages.get("world_three").drawAdvancedSprite(305, 458);
						}
						break;
					}
				}
			}

			if (informationFile.isUsernameRemembered()) {
				screenImages.get("remember_button_on").drawAdvancedSprite(580, 465);
			} else {
				screenImages.get("remember_button_off").drawAdvancedSprite(580, 465);
			}
			aTextDrawingArea_1271.method382(0x6d5b42, 675, "Remember username", 484, true);

			newBoldFont
					.drawBasicString(
							new StringBuilder().append("").append(capitalize(myUsername))
									.append(((this.loginScreenCursorPos == 0 ? 1 : 0)
											& (loopCycle % 40 < 20 ? 1 : 0)) != 0 ? "|" : "")
									.toString(),
							275, 201, 0x3D3228, 0x000000);

			newBoldFont
					.drawBasicString(
							new StringBuilder().append("").append(TextClass.passwordAsterisks(myPassword))
									.append(((this.loginScreenCursorPos == 1 ? 1 : 0)
											& (loopCycle % 40 < 20 ? 1 : 0)) != 0 ? "|" : "")
									.toString(),
							275, 262, 0x3D3228, 0x000000);

		}
		boolean debug = false;
		if (debug) {
			this.newRegularFont.drawBasicString("Mouse X: " + super.mouseX, 25, 50);
			this.newRegularFont.drawBasicString("Mouse Y: " + super.mouseY, 25, 65);
			this.newRegularFont.drawBasicString("Click X: " + super.saveClickX, 25, 80);
			this.newRegularFont.drawBasicString("Click Y: " + super.saveClickY, 25, 95);
		}
		if (fadingScreen != null) {
			fadingScreen.draw();
		}
		loginScreenGraphicsBuffer.drawGraphics(0, 0, super.graphics);
	}

	public void drawSmoothLoading(int i, String s) {
		for (float f = LP; f < (float) i; f = (float) ((double) f + 1.49999999999999999D))
			drawLoadingText((int) f, s, -1, -1);

		LP = i;
	}

	private void drawFlames() {
		try {
			long l = System.currentTimeMillis();
			int i = 0;
			int j = 20;
			while (aBoolean831) {
				anInt1208++;
				if (++i > 10) {
					long l1 = System.currentTimeMillis();
					int k = (int) (l1 - l) / 10 - j;
					j = 40 - k;
					if (j < 5)
						j = 5;
					i = 0;
					l = l1;
				}
				try {
					Thread.sleep(j);
				} catch (Exception _ex) {
				}
			}
		} catch (Exception _ex) {
			drawingFlames = false;
		}
	}

	@Override
	public void raiseWelcomeScreen() {
		welcomeScreenRaised = true;
	}

	public void method137(Stream stream, int j) {
		if (j == 84) {
			int k = stream.readUnsignedByte();
			int j3 = anInt1268 + (k >> 4 & 7);
			int i6 = anInt1269 + (k & 7);
			int l8 = stream.readUnsignedWord();
			int k11 = stream.readUnsignedWord();
			int l13 = stream.readUnsignedWord();//edit
			if (j3 >= 0 && i6 >= 0 && j3 < 104 && i6 < 104) {
				NodeList class19_1 = groundArray[plane][j3][i6];
				if (class19_1 != null) {
					for (Item class30_sub2_sub4_sub2_3 = (Item) class19_1
							.reverseGetFirst(); class30_sub2_sub4_sub2_3 != null; class30_sub2_sub4_sub2_3 = (Item) class19_1
									.reverseGetNext()) {
						if (class30_sub2_sub4_sub2_3.ID != (l8 & 0x7fff) || class30_sub2_sub4_sub2_3.anInt1559 != k11)
							continue;
						class30_sub2_sub4_sub2_3.anInt1559 = l13;
						break;
					}

					spawnGroundItem(j3, i6);
				}
			}
			return;
		}
		if (j == 105) {
			int l = stream.readUnsignedByte();
			int k3 = anInt1268 + (l >> 4 & 7);
			int j6 = anInt1269 + (l & 7);
			int i9 = stream.readUnsignedWord();
			int l11 = stream.readUnsignedByte();
			int i14 = l11 >> 4 & 0xf;
			int i16 = l11 & 7;
			if (myPlayer.smallX[0] >= k3 - i14 && myPlayer.smallX[0] <= k3 + i14 && myPlayer.smallY[0] >= j6 - i14
					&& myPlayer.smallY[0] <= j6 + i14 && aBoolean848 && !lowMem && anInt1062 < 50) {
				anIntArray1207[anInt1062] = i9;
				anIntArray1241[anInt1062] = i16;
				anIntArray1250[anInt1062] = Sounds.anIntArray326[i9];
				anInt1062++;
			}
		}
		if (j == 215) {
			int i1 = stream.method435();
			int l3 = stream.method428();
			int k6 = anInt1268 + (l3 >> 4 & 7);
			int j9 = anInt1269 + (l3 & 7);
			int i12 = stream.method435();
			int j14 = stream.readUnsignedWord();
			if (k6 >= 0 && j9 >= 0 && k6 < 104 && j9 < 104 && i12 != unknownInt10) {
				Item class30_sub2_sub4_sub2_2 = new Item();
				class30_sub2_sub4_sub2_2.ID = i1;
				class30_sub2_sub4_sub2_2.anInt1559 = j14;
				if (groundArray[plane][k6][j9] == null)
					groundArray[plane][k6][j9] = new NodeList();
				groundArray[plane][k6][j9].insertHead(class30_sub2_sub4_sub2_2);
				spawnGroundItem(k6, j9);
			}
			return;
		}
		if (j == 156) {
			int j1 = stream.method426();
			int i4 = anInt1268 + (j1 >> 4 & 7);
			int l6 = anInt1269 + (j1 & 7);
			int k9 = stream.readUnsignedWord();
			if (i4 >= 0 && l6 >= 0 && i4 < 104 && l6 < 104) {
				NodeList class19 = groundArray[plane][i4][l6];
				if (class19 != null) {
					for (Item item = (Item) class19.reverseGetFirst(); item != null; item = (Item) class19
							.reverseGetNext()) {
						if (item.ID != (k9 & 0x7fff))
							continue;
						item.unlink();
						break;
					}

					if (class19.reverseGetFirst() == null)
						groundArray[plane][i4][l6] = null;
					spawnGroundItem(i4, l6);
				}
			}
			return;
		}
		if (j == 160) {
			int k1 = stream.method428();
			int j4 = anInt1268 + (k1 >> 4 & 7);
			int i7 = anInt1269 + (k1 & 7);
			int l9 = stream.method428();
			int j12 = l9 >> 2;
			int k14 = l9 & 3;
			int j16 = anIntArray1177[j12];
			int j17 = stream.method435();
			if (j4 >= 0 && i7 >= 0 && j4 < 103 && i7 < 103) {
				int j18 = intGroundArray[plane][j4][i7];
				int i19 = intGroundArray[plane][j4 + 1][i7];
				int l19 = intGroundArray[plane][j4 + 1][i7 + 1];
				int k20 = intGroundArray[plane][j4][i7 + 1];
				if (j16 == 0) {
					Object1 class10 = worldController.method296(plane, j4, i7);
					if (class10 != null) {
						int k21 = class10.uid >> 14 & 0x7fff;
						if (j12 == 2) {
							class10.aClass30_Sub2_Sub4_278 = new Animable_Sub5(k21, 4 + k14, 2, i19, l19, j18, k20, j17,
									false);
							class10.aClass30_Sub2_Sub4_279 = new Animable_Sub5(k21, k14 + 1 & 3, 2, i19, l19, j18, k20,
									j17, false);
						} else {
							class10.aClass30_Sub2_Sub4_278 = new Animable_Sub5(k21, k14, j12, i19, l19, j18, k20, j17,
									false);
						}
					}
				}
				if (j16 == 1) {
					Object2 class26 = worldController.method297(j4, i7, plane);
					if (class26 != null)
						class26.aClass30_Sub2_Sub4_504 = new Animable_Sub5(class26.uid >> 14 & 0x7fff, 0, 4, i19, l19,
								j18, k20, j17, false);
				}
				if (j16 == 2) {
					Object5 class28 = worldController.method298(j4, i7, plane);
					if (j12 == 11)
						j12 = 10;
					if (class28 != null)
						class28.aClass30_Sub2_Sub4_521 = new Animable_Sub5(class28.uid >> 14 & 0x7fff, k14, j12, i19,
								l19, j18, k20, j17, false);
				}
				if (j16 == 3) {
					Object3 class49 = worldController.method299(i7, j4, plane);
					if (class49 != null)
						class49.aClass30_Sub2_Sub4_814 = new Animable_Sub5(class49.uid >> 14 & 0x7fff, k14, 22, i19,
								l19, j18, k20, j17, false);
				}
			}
			return;
		}
		if (j == 147) {
			int l1 = stream.method428();
			int k4 = anInt1268 + (l1 >> 4 & 7);
			int j7 = anInt1269 + (l1 & 7);
			int i10 = stream.readUnsignedWord();
			byte byte0 = stream.method430();
			int l14 = stream.method434();
			byte byte1 = stream.method429();
			int k17 = stream.readUnsignedWord();
			int k18 = stream.method428();
			int j19 = k18 >> 2;
			int i20 = k18 & 3;
			int l20 = anIntArray1177[j19];
			byte byte2 = stream.readSignedByte();
			int l21 = stream.readUnsignedWord();
			byte byte3 = stream.method429();
			Player player;
			if (i10 == unknownInt10)
				player = myPlayer;
			else
				player = playerArray[i10];
			if (player != null) {
				ObjectDefinition class46 = ObjectDefinition.forID(l21);
				int i22 = intGroundArray[plane][k4][j7];
				int j22 = intGroundArray[plane][k4 + 1][j7];
				int k22 = intGroundArray[plane][k4 + 1][j7 + 1];
				int l22 = intGroundArray[plane][k4][j7 + 1];
				Model model = class46.method578(j19, i20, i22, j22, k22, l22, -1);
				if (model != null) {
					method130(k17 + 1, -1, 0, l20, j7, 0, plane, k4, l14 + 1);
					player.anInt1707 = l14 + loopCycle;
					player.anInt1708 = k17 + loopCycle;
					player.aModel_1714 = model;
					int i23 = class46.anInt744;
					int j23 = class46.anInt761;
					if (i20 == 1 || i20 == 3) {
						i23 = class46.anInt761;
						j23 = class46.anInt744;
					}
					player.anInt1711 = k4 * 128 + i23 * 64;
					player.anInt1713 = j7 * 128 + j23 * 64;
					player.anInt1712 = method42(plane, player.anInt1713, player.anInt1711);
					if (byte2 > byte0) {
						byte byte4 = byte2;
						byte2 = byte0;
						byte0 = byte4;
					}
					if (byte3 > byte1) {
						byte byte5 = byte3;
						byte3 = byte1;
						byte1 = byte5;
					}
					player.anInt1719 = k4 + byte2;
					player.anInt1721 = k4 + byte0;
					player.anInt1720 = j7 + byte3;
					player.anInt1722 = j7 + byte1;
				}
			}
		}
		if (j == 151) {
			int i2 = stream.method426();
			int l4 = anInt1268 + (i2 >> 4 & 7);
			int k7 = anInt1269 + (i2 & 7);
			int j10 = stream.method434();
			int k12 = stream.method428();
			int i15 = k12 >> 2;
			int k16 = k12 & 3;
			int l17 = anIntArray1177[i15];
			if (l4 >= 0 && k7 >= 0 && l4 < 104 && k7 < 104)
				method130(-1, j10, k16, l17, k7, i15, plane, l4, 0);
			return;
		}
		if (j == 4) {
			int j2 = stream.readUnsignedByte();
			int i5 = anInt1268 + (j2 >> 4 & 7);
			int l7 = anInt1269 + (j2 & 7);
			int k10 = stream.readUnsignedWord();
			int l12 = stream.readUnsignedByte();
			int j15 = stream.readUnsignedWord();
			if (i5 >= 0 && l7 >= 0 && i5 < 104 && l7 < 104) {
				i5 = i5 * 128 + 64;
				l7 = l7 * 128 + 64;
				Animable_Sub3 class30_sub2_sub4_sub3 = new Animable_Sub3(plane, loopCycle, j15, k10,
						method42(plane, l7, i5) - l12, l7, i5);
				aClass19_1056.insertHead(class30_sub2_sub4_sub3);
			}
			return;
		}
		if (j == 44) {
			int k2 = stream.method436();
			int j5 = stream.method439();
			int i8 = stream.readUnsignedByte();
			int l10 = anInt1268 + (i8 >> 4 & 7);
			int i13 = anInt1269 + (i8 & 7);
			if (l10 >= 0 && i13 >= 0 && l10 < 104 && i13 < 104) {
				Item class30_sub2_sub4_sub2_1 = new Item();
				class30_sub2_sub4_sub2_1.ID = k2;
				class30_sub2_sub4_sub2_1.anInt1559 = j5;
				if (groundArray[plane][l10][i13] == null)
					groundArray[plane][l10][i13] = new NodeList();
				groundArray[plane][l10][i13].insertHead(class30_sub2_sub4_sub2_1);
				spawnGroundItem(l10, i13);
			}
			return;
		}
		if (j == 101) {
			int l2 = stream.method427();
			int k5 = l2 >> 2;
			int j8 = l2 & 3;
			int i11 = anIntArray1177[k5];
			int j13 = stream.readUnsignedByte();
			int k15 = anInt1268 + (j13 >> 4 & 7);
			int l16 = anInt1269 + (j13 & 7);
			if (k15 >= 0 && l16 >= 0 && k15 < 104 && l16 < 104)
				method130(-1, -1, j8, i11, l16, k5, plane, k15, 0);
			return;
		}
		if (j == 117) {
			int i3 = stream.readUnsignedByte();
			int l5 = anInt1268 + (i3 >> 4 & 7);
			int k8 = anInt1269 + (i3 & 7);
			int j11 = l5 + stream.readSignedByte();
			int k13 = k8 + stream.readSignedByte();
			int l15 = stream.readSignedWord();
			int i17 = stream.readUnsignedWord();
			int i18 = stream.readUnsignedByte() * 4;
			int l18 = stream.readUnsignedByte() * 4;
			int k19 = stream.readUnsignedWord();
			int j20 = stream.readUnsignedWord();
			int i21 = stream.readUnsignedByte();
			int j21 = stream.readUnsignedByte();
			if (l5 >= 0 && k8 >= 0 && l5 < 104 && k8 < 104 && j11 >= 0 && k13 >= 0 && j11 < 104 && k13 < 104
					&& i17 != 65535) {
				l5 = l5 * 128 + 64;
				k8 = k8 * 128 + 64;
				j11 = j11 * 128 + 64;
				k13 = k13 * 128 + 64;
				Animable_Sub4 class30_sub2_sub4_sub4 = new Animable_Sub4(i21, l18, k19 + loopCycle, j20 + loopCycle,
						j21, plane, method42(plane, k8, l5) - i18, k8, l5, l15, i17);
				class30_sub2_sub4_sub4.method455(k19 + loopCycle, k13, method42(plane, k13, j11) - l18, j11);
				aClass19_1013.insertHead(class30_sub2_sub4_sub4);
			}
		}
	}

	// private static void setLowMem() {
	// WorldController.lowMem = true;
	// Texture.lowMem = true;
	// lowMem = true;
	// ObjectManager.lowMem = true;
	// ObjectDefinitions.lowMem = true;
	// }

	public void method139(Stream stream) {
		stream.initBitAccess();
		int k = stream.readBits(8);
		if (k < npcCount) {
			for (int l = k; l < npcCount; l++)
				anIntArray840[anInt839++] = npcIndices[l];

		}
		if (k > npcCount) {
			Signlink.reporterror(myUsername + " Too many npcs");
			throw new RuntimeException("eek");
		}
		npcCount = 0;
		for (int i1 = 0; i1 < k; i1++) {
			int j1 = npcIndices[i1];
			NPC npc = npcArray[j1];
			int k1 = stream.readBits(1);
			if (k1 == 0) {
				npcIndices[npcCount++] = j1;
				npc.anInt1537 = loopCycle;
			} else {
				int l1 = stream.readBits(2);
				if (l1 == 0) {
					npcIndices[npcCount++] = j1;
					npc.anInt1537 = loopCycle;
					anIntArray894[anInt893++] = j1;
				} else if (l1 == 1) {
					npcIndices[npcCount++] = j1;
					npc.anInt1537 = loopCycle;
					int i2 = stream.readBits(3);
					npc.moveInDir(false, i2);
					int k2 = stream.readBits(1);
					if (k2 == 1)
						anIntArray894[anInt893++] = j1;
				} else if (l1 == 2) {
					npcIndices[npcCount++] = j1;
					npc.anInt1537 = loopCycle;
					int j2 = stream.readBits(3);
					npc.moveInDir(true, j2);
					int l2 = stream.readBits(3);
					npc.moveInDir(true, l2);
					int i3 = stream.readBits(1);
					if (i3 == 1)
						anIntArray894[anInt893++] = j1;
				} else if (l1 == 3)
					anIntArray840[anInt839++] = j1;
			}
		}

	}

	private void processLoginScreenInput() {

		if (currentScreenMode != ScreenMode.FIXED) {
			if (currentGameWidth != super.getSize().getWidth()) {
				currentGameWidth = (int) super.getSize().getWidth();
				loginScreenGraphicsBuffer = new RSImageProducer(currentGameWidth, currentGameHeight,
						getGameComponent());
			}
			if (currentGameHeight != super.getSize().getHeight()) {
				currentGameHeight = (int) super.getSize().getHeight();
				loginScreenGraphicsBuffer = new RSImageProducer(currentGameWidth, currentGameHeight,
						getGameComponent());
			}
		}

		if (this.loginScreenState == 0 || this.loginScreenState == 2) {
			DrawingArea.setAllPixelsToZero();
			if (super.clickMode3 == 1) {
				if (super.mouseX >= 263 && super.mouseX <= 505 && super.mouseY > 179 && super.mouseY < 221) {
					loginScreenCursorPos = 0;
				}
				if (super.mouseX >= 263 && super.mouseX <= 505 && super.mouseY > 241 && super.mouseY < 279) {
					loginScreenCursorPos = 1;
				}
				if (super.mouseX >= 302 && super.mouseX <= 464 && super.mouseY > 303 && super.mouseY < 345) {
					loginFailures = 0;
					if ((myUsername.length() > 0) && (myPassword.length() > 0)) {
						login(myUsername, myPassword, false);
					} else {
						loginScreenCursorPos = 0;
						fadingScreen = new FadingScreenImage(newBoldFont,
								"Username & Password must be more than 1 character.", (byte) -1, (byte) 5,
								765 / 2 - 125, 350, 250);
					}
					if (this.loggedIn)
						return;
				}
				if (super.mouseX >= 11 && super.mouseX <= 145 && super.mouseY > 467 && super.mouseY < 502) {
					launchURL("https://www.youtube.com/channel/UC-kosrxOm2MIpVMB9sDXrYw");
				}

				if (super.mouseX >= 161 && super.mouseX <= 295 && super.mouseY > 467 && super.mouseY < 502) {
					launchURL("https://www.os-v.org/");
				}

				if (Configuration.WORLD_CHANGER) {

					if (super.mouseX >= 311 && super.mouseX <= 445 && super.mouseY > 467 && super.mouseY < 502) {

							switch (currentWorld) {
							case 0:
								screenImages.get("world_two").drawAdvancedSprite(305, 458);
								currentWorld = 1;
								port = 5555;
								server = "127.0.0.1";
								fadingScreen = new FadingScreenImage(newBoldFont,
										"You will now be logging into world 2.", (byte) -1, (byte) 3, 765 / 2 - 125,
										350, 250);
								System.out.println("Port: " + port + " World: " + server);
								break;

							case 1:
								screenImages.get("world_three").drawAdvancedSprite(305, 458);
								currentWorld = 2;
								port = 43594;
								server = "0.0.0.0";
								fadingScreen = new FadingScreenImage(newBoldFont,
										"You will now be logging into world 3.", (byte) -1, (byte) 3, 765 / 2 - 125,
										350, 250);
								System.out.println("Port: " + port + " World: " + server);
								break;

							case 2:
								screenImages.get("world_one").drawAdvancedSprite(305, 458);
								currentWorld = 0;
								port = 43594;
								server = "127.0.0.1";
								fadingScreen = new FadingScreenImage(newBoldFont,
										"You will now be logging into world 1.", (byte) -1, (byte) 3, 765 / 2 - 125,
										350, 250);
								System.out.println("Port: " + port + " World: " + server);
								break;
							}
						}
					}
				if (super.mouseX >= 584 && super.mouseX <= 605 && super.mouseY >= 474 && super.mouseY <= 494) {
					informationFile.setUsernameRemembered(informationFile.isUsernameRemembered() ? false : true);
					fadingScreen = new FadingScreenImage(
							newBoldFont, "Your username is " + (informationFile.isUsernameRemembered()
									? "now being remembered." : "no longer being remembered."),
							(byte) -1, (byte) 3, 765 / 2 - 125, 350, 250);
					try {
						if (informationFile.isUsernameRemembered()) {
							informationFile.setStoredUsername(myUsername);
						}
						informationFile.write();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			do {
				int l1 = readChar(-796);
				if (l1 == -1)
					break;
				boolean flag1 = false;
				for (int i2 = 0; i2 < validUserPassChars.length(); i2++) {
					if (l1 != validUserPassChars.charAt(i2))
						continue;
					flag1 = true;
					break;
				}

				if (loginScreenCursorPos == 0) {
					if (l1 == 8 && myUsername.length() > 0)
						myUsername = myUsername.substring(0, myUsername.length() - 1);
					if (l1 == 9 || l1 == 10 || l1 == 13)
						loginScreenCursorPos = 1;
					if (flag1)
						myUsername += (char) l1;
					if (myUsername.length() > 12)
						myUsername = myUsername.substring(0, 12);
					informationFile.setStoredUsername(myUsername);
				} else if (loginScreenCursorPos == 1) {
					if (l1 == 8 && myPassword.length() > 0)
						myPassword = myPassword.substring(0, myPassword.length() - 1);
					if (l1 == 9 || l1 == 10 || l1 == 13)
						login(myUsername, myPassword, false);
					if (flag1)
						myPassword += (char) l1;
					if (myPassword.length() > 20)
						myPassword = myPassword.substring(0, 20);
				}
			} while (true);
			return;
		}
		if (loginScreenState == 3) {
			int k = super.myWidth / 2;
			int j1 = super.myHeight / 2 + 50;
			j1 += 20;
			if (super.clickMode3 == 1 && super.saveClickX >= k - 75 && super.saveClickX <= k + 75
					&& super.saveClickY >= j1 - 20 && super.saveClickY <= j1 + 20)
				loginScreenState = 0;
		}
	}

	private void markMinimap(Sprite sprite, int xPosition, int yPosition) {
		int k = viewRotation + minimapRotation & 0x7ff;
		int boundry = xPosition * xPosition + yPosition * yPosition;
		if (boundry > 5750)
			return;
		int i1 = Model.modelIntArray1[k];
		int j1 = Model.modelIntArray2[k];
		i1 = (i1 * 256) / (minimapZoom + 256);
		j1 = (j1 * 256) / (minimapZoom + 256);
		int k1 = yPosition * i1 + xPosition * j1 >> 16;
		int l1 = yPosition * j1 - xPosition * i1 >> 16;

		try {
			if (currentScreenMode == ScreenMode.FIXED)
				sprite.drawSprite(((94 + k1) - sprite.maxWidth / 2) + 4 + 30, 83 - l1 - sprite.maxHeight / 2 - 4 + 5);
			else
				sprite.drawSprite(((77 + k1) - sprite.maxWidth / 2) + 4 + (currentGameWidth - 167),
						85 - l1 - sprite.maxHeight / 2 - 4);
		} catch (Exception e) {
			//e.printStackTrace();
		}
	}

	public void method142(int i, int j, int k, int l, int i1, int j1, int k1) {
		if (i1 >= 1 && i >= 1 && i1 <= 102 && i <= 102) {
			if (lowMem && j != plane)
				return;
			int i2 = 0;
			if (j1 == 0)
				i2 = worldController.method300(j, i1, i);
			if (j1 == 1)
				i2 = worldController.method301(j, i1, i);
			if (j1 == 2)
				i2 = worldController.method302(j, i1, i);
			if (j1 == 3)
				i2 = worldController.method303(j, i1, i);
			if (i2 != 0) {
				int i3 = worldController.method304(j, i1, i, i2);
				int j2 = i2 >> 14 & 0x7fff;
				int k2 = i3 & 0x1f;
				int l2 = i3 >> 6;
				if (j1 == 0) {
					worldController.method291(i1, j, i, (byte) -119);
					ObjectDefinition class46 = ObjectDefinition.forID(j2);
					if (class46.aBoolean767)
						aClass11Array1230[j].method215(l2, k2, class46.aBoolean757, i1, i);
				}
				if (j1 == 1)
					worldController.method292(i, j, i1);
				if (j1 == 2) {
					worldController.method293(j, i1, i);
					ObjectDefinition class46_1 = ObjectDefinition.forID(j2);
					if (i1 + class46_1.anInt744 > 103 || i + class46_1.anInt744 > 103 || i1 + class46_1.anInt761 > 103
							|| i + class46_1.anInt761 > 103)
						return;
					if (class46_1.aBoolean767)
						aClass11Array1230[j].method216(l2, class46_1.anInt744, i1, i, class46_1.anInt761,
								class46_1.aBoolean757);
				}
				if (j1 == 3) {
					worldController.method294(j, i, i1);
					ObjectDefinition class46_2 = ObjectDefinition.forID(j2);
					if (class46_2.aBoolean767 && class46_2.hasActions)
						aClass11Array1230[j].method218(i, i1);
				}
			}
			if (k1 >= 0) {
				int j3 = j;
				if (j3 < 3 && (byteGroundArray[1][i1][i] & 2) == 2)
					j3++;
				ObjectManager.method188(worldController, k, i, l, j3, aClass11Array1230[j], intGroundArray, i1, k1, j);
			}
		}
	}

	public void updatePlayers(int i, Stream stream) {
		anInt839 = 0;
		anInt893 = 0;
		method117(stream);
		method134(stream);
		method91(stream, i);
		method49(stream);
		for (int k = 0; k < anInt839; k++) {
			int l = anIntArray840[k];
			if (playerArray[l].anInt1537 != loopCycle)
				playerArray[l] = null;
		}

		if (stream.currentOffset != i) {
			Signlink.reporterror("Error packet size mismatch in getplayer pos:" + stream.currentOffset + " psize:" + i);
			throw new RuntimeException("eek");
		}
		for (int i1 = 0; i1 < playerCount; i1++)
			if (playerArray[playerIndices[i1]] == null) {
				Signlink.reporterror(myUsername + " null entry in pl list - pos:" + i1 + " size:" + playerCount);
				throw new RuntimeException("eek");
			}

	}

	private void setCameraPos(double j, int k, int l, int i1, int j1, int k1) {
		int l1 = 2048 - k & 0x7ff;
		int i2 = 2048 - j1 & 0x7ff;
		int j2 = 0;
		int k2 = 0;
		double l2 = j;
		if (l1 != 0) {
			int i3 = Model.modelIntArray1[l1];
			int k3 = Model.modelIntArray2[l1];
			int i4 = k2 * k3 - (int) (l2 * i3) >> 16;
			l2 = k2 * i3 + (int) (l2 * k3) >> 16;
			k2 = i4;
		}
		if (i2 != 0) {
			int j3 = Model.modelIntArray1[i2];
			int l3 = Model.modelIntArray2[i2];
			int j4 = (int) (l2 * j3) + j2 * l3 >> 16;
			l2 = (int) (l2 * l3) - j2 * j3 >> 16;
			j2 = j4;
		}
		xCameraPos = l - j2;
		zCameraPos = i1 - k2;
		yCameraPos = (int) (k1 - l2);
		yCameraCurve = k;
		xCameraCurve = j1;
	}

	public void updateStrings(String str, int i) {
		switch (i) {
		case 49020:
			achievementCutoff = Integer.parseInt(str);
			break;
		case 1675:
			sendFrame126(str, 17508);
			break;// Stab
		case 1676:
			sendFrame126(str, 17509);
			break;// Slash
		case 1678:
			sendFrame126(str, 17511);
			break;// Magic
		case 1679:
			sendFrame126(str, 17512);
			break;// Range
		case 1680:
			sendFrame126(str, 17513);
			break;// Stab
		case 1681:
			sendFrame126(str, 17514);
			break;// Slash
		case 1682:
			sendFrame126(str, 17515);
			break;// Crush
		case 1683:
			sendFrame126(str, 17516);
			break;// Magic
		case 1684:
			sendFrame126(str, 17517);
			break;// Range
		case 1686:
			sendFrame126(str, 17518);
			break;// Strength
		case 1687:
			sendFrame126(str, 17519);
			break;// Prayer
		}
	}

	public void sendFrame126(String str, int i) {
		RSInterface component = RSInterface.interfaceCache[i];
		if (component != null) {
			component.message = str;
			if (component.type == 4 && component.atActionType == 1) {
				component.hoverText = str;
			}
			if (component.parentID == tabInterfaceIDs[tabID])
				needDrawTabArea = true;
		}
	}

	public void sendFrame219() {
		if (invOverlayInterfaceID != -1) {
			invOverlayInterfaceID = -1;
			needDrawTabArea = true;
			tabAreaAltered = true;
		}
		if (backDialogID != -1) {
			backDialogID = -1;
			inputTaken = true;
		}
		if (inputDialogState != 0) {
			inputDialogState = 0;
			inputTaken = true;
		}
		openInterfaceID = -1;
		aBoolean1149 = false;
	}

	public void sendFrame248(int interfaceID, int sideInterfaceID) {
		if (backDialogID != -1) {
			backDialogID = -1;
			inputTaken = true;
		}
		if (inputDialogState != 0) {
			inputDialogState = 0;
			inputTaken = true;
		}
		openInterfaceID = interfaceID;
		invOverlayInterfaceID = sideInterfaceID;
		needDrawTabArea = true;
		tabAreaAltered = true;
		aBoolean1149 = false;
	}
	
	private int groundItemValueLimit = 0;


	private static final Comparator<ExperienceDrop> HIGHEST_POSITION = new Comparator<ExperienceDrop>() {

		@Override
		public int compare(ExperienceDrop o1, ExperienceDrop o2) {
			return Integer.compare(o2.getYPosition(), o1.getYPosition());
		}

	};

	private boolean parsePacket() {
		if (socketStream == null)
			return false;
		try {
			int i = socketStream.available();
			if (i == 0)
				return false;
			if (incomingPacket == -1) {
				socketStream.flushInputStream(inStream.buffer, 1);
				incomingPacket = inStream.buffer[0] & 0xff;
				if (encryption != null)
					incomingPacket = incomingPacket - encryption.getNextKey() & 0xff;
				packetSize = SizeConstants.packetSizes[incomingPacket];
				i--;
			}
			if (packetSize == -1)
				if (i > 0) {
					socketStream.flushInputStream(inStream.buffer, 1);
					packetSize = inStream.buffer[0] & 0xff;
					i--;
				} else {
					return false;
				}
			if (packetSize == -2)
				if (i > 1) {
					socketStream.flushInputStream(inStream.buffer, 2);
					inStream.currentOffset = 0;
					packetSize = inStream.readUnsignedWord();
					i -= 2;
				} else {
					return false;
				}
			if (i < packetSize)
				return false;
			inStream.currentOffset = 0;
			socketStream.flushInputStream(inStream.buffer, packetSize);
			anInt1009 = 0;
			previousPacket2 = previousPacket1;
			previousPacketSize2 = previousPacketSize1;
			previousPacketSize1 = dealtWithPacketSize;
			previousPacket1 = dealtWithPacket;
			dealtWithPacket = incomingPacket;
			dealtWithPacketSize = packetSize;
			switch (incomingPacket) {

			/**
			 * Progress Bar Update Packet
			 */
			case 77:
				while (inStream.currentOffset < packetSize) {
					int interfaceId = inStream.readDWord();
					byte progressBarState = inStream.readSignedByte();
					byte progressBarPercentage = inStream.readSignedByte();

					RSInterface rsInterface = RSInterface.interfaceCache[interfaceId];
					rsInterface.progressBarState = progressBarState;
					rsInterface.progressBarPercentage = progressBarPercentage;
				}
				incomingPacket = -1;
				return true;

			/**
			 * EntityTarget Update Packet
			 */
			case 222:
				byte entityState = (byte) inStream.readUnsignedByte();
				if (entityState != 0) {
					short entityIndex = (short) inStream.readUnsignedWord();
					short currentHealth = (short) inStream.readUnsignedWord();
					short maximumHealth = (short) inStream.readUnsignedWord();
					entityTarget = new EntityTarget(entityState, entityIndex, currentHealth, maximumHealth,
							newSmallFont);
				} else {
					if (entityTarget != null) {
						entityTarget.stop();
					}
					entityTarget = null;
				}
				incomingPacket = -1;
				return true;

			/**
			 * Timer Update Packet
			 */
			case 223:
				byte timerId = (byte) inStream.readUnsignedByte();
				short secondsToAdd = (short) inStream.readUnsignedWord();
				GameTimerHandler.getSingleton().startGameTimer(timerId, TimeUnit.SECONDS, secondsToAdd);
				incomingPacket = -1;
				return true;

			case 11:
				long experience = inStream.readQWord();
				byte length = inStream.readSignedByte();
				int[] skills = new int[length];

				for (int j = 0; j < length; j++) {
					skills[j] = inStream.readSignedByte();
				}

				ExperienceDrop drop = new ExperienceDrop(experience, skills);

				if (!experienceDrops.isEmpty()) {
					List<ExperienceDrop> sorted = new ArrayList<ExperienceDrop>(experienceDrops);
					Collections.sort(sorted, HIGHEST_POSITION);
					ExperienceDrop highest = sorted.get(0);
					if (highest.getYPosition() >= ExperienceDrop.START_Y - 5) {
						drop.increasePosition(highest.getYPosition() - ExperienceDrop.START_Y + 20);
					}
				}

				experienceDrops.offer(drop);
				incomingPacket = -1;
				return true;

			case 81:
				updatePlayers(packetSize, inStream);
				aBoolean1080 = false;
				incomingPacket = -1;
				return true;

			case 176:
				daysSinceRecovChange = inStream.method427();
				unreadMessages = inStream.method435();
				membersInt = inStream.readUnsignedByte();
				anInt1193 = inStream.method440();
				daysSinceLastLogin = inStream.readUnsignedWord();
				if (anInt1193 != 0 && openInterfaceID == -1) {
					Signlink.dnslookup(TextClass.method586(anInt1193));
					clearTopInterfaces();
					char c = '\u028A';
					if (daysSinceRecovChange != 201 || membersInt == 1)
						c = '\u028F';
					reportAbuseInput = "";
					canMute = false;
					for (int k9 = 0; k9 < RSInterface.interfaceCache.length; k9++) {
						if (RSInterface.interfaceCache[k9] == null || RSInterface.interfaceCache[k9].contentType != c)
							continue;
						openInterfaceID = RSInterface.interfaceCache[k9].parentID;

					}
				}
				incomingPacket = -1;
				return true;

			case 64:
				anInt1268 = inStream.method427();
				anInt1269 = inStream.method428();
				for (int j = anInt1268; j < anInt1268 + 8; j++) {
					for (int l9 = anInt1269; l9 < anInt1269 + 8; l9++)
						if (groundArray[plane][j][l9] != null) {
							groundArray[plane][j][l9] = null;
							spawnGroundItem(j, l9);
						}
				}
				for (Class30_Sub1 class30_sub1 = (Class30_Sub1) aClass19_1179
						.reverseGetFirst(); class30_sub1 != null; class30_sub1 = (Class30_Sub1) aClass19_1179
								.reverseGetNext())
					if (class30_sub1.anInt1297 >= anInt1268 && class30_sub1.anInt1297 < anInt1268 + 8
							&& class30_sub1.anInt1298 >= anInt1269 && class30_sub1.anInt1298 < anInt1269 + 8
							&& class30_sub1.anInt1295 == plane)
						class30_sub1.anInt1294 = 0;
				incomingPacket = -1;
				return true;

			case 9:
				String text = inStream.readString();
				byte state = inStream.readSignedByte();
				byte seconds = inStream.readSignedByte();
				int drawingWidth = Client.currentScreenMode == ScreenMode.FIXED ? 519 : Client.currentGameWidth;
				int drawingHeight = Client.currentScreenMode == ScreenMode.FIXED ? 338 : Client.currentGameHeight;
				fadingScreen = new BlackFadingScreen(newBoldFont, text, state, seconds, 0, 0, drawingWidth,
						drawingHeight, 100);
				incomingPacket = -1;
				return true;

			case 185:
				int k = inStream.method436();
				RSInterface.interfaceCache[k].anInt233 = 3;
				if (myPlayer.desc == null)
					RSInterface.interfaceCache[k].mediaID = (myPlayer.anIntArray1700[0] << 25)
							+ (myPlayer.anIntArray1700[4] << 20) + (myPlayer.equipment[0] << 15)
							+ (myPlayer.equipment[8] << 10) + (myPlayer.equipment[11] << 5) + myPlayer.equipment[1];
				else
					RSInterface.interfaceCache[k].mediaID = (int) (0x12345678L + myPlayer.desc.interfaceType);
				incomingPacket = -1;
				return true;

			/* Clan message packet */
			case 217:
				try {
					clanUsername = inStream.readString();
					clanMessage = TextInput.processText(inStream.readString());
					clanTitle = inStream.readString();
					channelRights = inStream.readUnsignedWord();
					pushMessage(clanMessage, 12, clanUsername);
				} catch (Exception e) {
					e.printStackTrace();
				}
				incomingPacket = -1;
				return true;

			case 107:
				aBoolean1160 = false;
				for (int l = 0; l < 5; l++)
					aBooleanArray876[l] = false;
				xpCounter = 0;
				incomingPacket = -1;
				return true;

			case 72:
				int i1 = inStream.method434();
				RSInterface class9 = RSInterface.interfaceCache[i1];
				for (int k15 = 0; k15 < class9.inv.length; k15++) {
					class9.inv[k15] = -1;
					class9.inv[k15] = 0;
				}
				incomingPacket = -1;
				return true;

			case 214:
				ignoreCount = packetSize / 8;
				for (int j1 = 0; j1 < ignoreCount; j1++)
					ignoreListAsLongs[j1] = inStream.readQWord();
				incomingPacket = -1;
				return true;

			case 166:
				aBoolean1160 = true;
				anInt1098 = inStream.readUnsignedByte();
				anInt1099 = inStream.readUnsignedByte();
				anInt1100 = inStream.readUnsignedWord();
				anInt1101 = inStream.readUnsignedByte();
				anInt1102 = inStream.readUnsignedByte();
				if (anInt1102 >= 100) {
					xCameraPos = anInt1098 * 128 + 64;
					yCameraPos = anInt1099 * 128 + 64;
					zCameraPos = method42(plane, yCameraPos, xCameraPos) - anInt1100;
				}
				incomingPacket = -1;
				return true;

			case 134:
				needDrawTabArea = true;
				int skillId = inStream.readUnsignedByte();
				int experience2 = inStream.method439();
				int currentLevel = inStream.readUnsignedByte();
				int xp = currentExp[skillId];

				currentExp[skillId] = experience2;
				currentStats[skillId] = currentLevel;
				maxStats[skillId] = 1;
				xpCounter += currentExp[skillId] - xp;
				expAdded = currentExp[skillId] - xp;
				for (int k20 = 0; k20 < 98; k20++)
					if (experience2 >= anIntArray1019[k20])
						maxStats[skillId] = k20 + 2;
				incomingPacket = -1;
				return true;

			case 71:
				int l1 = inStream.readUnsignedWord();
				int j10 = inStream.method426();
				if (l1 == 65535)
					l1 = -1;
				tabInterfaceIDs[j10] = l1;
				needDrawTabArea = true;
				tabAreaAltered = true;
				incomingPacket = -1;
				return true;

			case 74:
				int i2 = inStream.method434();
				if (i2 == 65535)
					i2 = -1;
				if (i2 != currentSong && musicEnabled && !lowMem && prevSong == 0) {
					nextSong = i2;
					songChanging = true;
					onDemandFetcher.method558(2, nextSong);
				}
				currentSong = i2;
				incomingPacket = -1;
				return true;

			case 121:
				int j2 = inStream.method436();
				int k10 = inStream.method435();
				if (musicEnabled && !lowMem) {
					nextSong = j2;
					songChanging = false;
					onDemandFetcher.method558(2, nextSong);
					prevSong = k10;
				}
				incomingPacket = -1;
				return true;

			case 7:
				int componentId = inStream.readDWord();
				byte spriteIndex = inStream.readSignedByte();
				RSInterface component = RSInterface.interfaceCache[componentId];
				if (component != null) {
					if (component.backgroundSprites != null && spriteIndex <= component.backgroundSprites.length - 1) {
						Sprite sprite = component.backgroundSprites[spriteIndex];
						if (sprite != null) {
							component.sprite1 = component.backgroundSprites[spriteIndex];
						}
					}
				}
				incomingPacket = -1;
				return true;

			case 109:
				resetLogout();
				incomingPacket = -1;
				return false;

			case 70:
				int k2 = inStream.readSignedWord();
				int l10 = inStream.method437();
				int i16 = inStream.method434();
				RSInterface class9_5 = RSInterface.interfaceCache[i16];
				class9_5.anInt263 = k2;
				class9_5.anInt265 = l10;
				incomingPacket = -1;
				return true;

			case 73:
			case 241:
				int mapRegionX = mapRegionsX;
				int mapRegionY = mapRegionsY;
				if (incomingPacket == 73) {
					mapRegionX = inStream.method435();
					mapRegionY = inStream.readUnsignedWord();
					aBoolean1159 = false;
				}
				if (incomingPacket == 241) {
					mapRegionY = inStream.method435();
					inStream.initBitAccess();
					for (int j16 = 0; j16 < 4; j16++) {
						for (int l20 = 0; l20 < 13; l20++) {
							for (int j23 = 0; j23 < 13; j23++) {
								int i26 = inStream.readBits(1);
								if (i26 == 1)
									anIntArrayArrayArray1129[j16][l20][j23] = inStream.readBits(26);
								else
									anIntArrayArrayArray1129[j16][l20][j23] = -1;
							}
						}
					}
					inStream.finishBitAccess();
					mapRegionX = inStream.readUnsignedWord();
					aBoolean1159 = true;
				}
				if (mapRegionsX == mapRegionX && mapRegionsY == mapRegionY && loadingStage == 2) {
					incomingPacket = -1;
					return true;
				}
				mapRegionsX = mapRegionX;
				mapRegionsY = mapRegionY;
				baseX = (mapRegionsX - 6) * 8;
				baseY = (mapRegionsY - 6) * 8;
				aBoolean1141 = (mapRegionsX / 8 == 48 || mapRegionsX / 8 == 49) && mapRegionsY / 8 == 48;
				if (mapRegionsX / 8 == 48 && mapRegionsY / 8 == 148)
					aBoolean1141 = true;
				loadingStage = 1;
				aLong824 = System.currentTimeMillis();
				mainGameGraphicsBuffer.initDrawingArea();
				DrawingArea.fillPixels(2, 130, 22, 0xffffff, 2);
				DrawingArea.drawPixels(20, 3, 3, 0, 128);
				aTextDrawingArea_1271.drawText(0, "Loading - Please Wait", 18, 68);
				aTextDrawingArea_1271.drawText(0xffffff, "Loading - Please Wait", 17, 67);
				mainGameGraphicsBuffer.drawGraphics(0, 0, super.graphics);
				if (incomingPacket == 73) {
					int k16 = 0;
					for (int i21 = (mapRegionsX - 6) / 8; i21 <= (mapRegionsX + 6) / 8; i21++) {
						for (int k23 = (mapRegionsY - 6) / 8; k23 <= (mapRegionsY + 6) / 8; k23++)
							k16++;
					}
					aByteArrayArray1183 = new byte[k16][];
					aByteArrayArray1247 = new byte[k16][];
					anIntArray1234 = new int[k16];
					anIntArray1235 = new int[k16];
					anIntArray1236 = new int[k16];
					k16 = 0;
					for (int l23 = (mapRegionsX - 6) / 8; l23 <= (mapRegionsX + 6) / 8; l23++) {
						for (int j26 = (mapRegionsY - 6) / 8; j26 <= (mapRegionsY + 6) / 8; j26++) {
							anIntArray1234[k16] = (l23 << 8) + j26;
							if (aBoolean1141
									&& (j26 == 49 || j26 == 149 || j26 == 147 || l23 == 50 || l23 == 49 && j26 == 47)) {
								anIntArray1235[k16] = -1;
								anIntArray1236[k16] = -1;
								k16++;
							} else {
								int k28 = anIntArray1235[k16] = onDemandFetcher.method562(0, j26, l23);
								if (k28 != -1)
									onDemandFetcher.method558(3, k28);
								int j30 = anIntArray1236[k16] = onDemandFetcher.method562(1, j26, l23);
								if (j30 != -1)
									onDemandFetcher.method558(3, j30);
								k16++;
							}
						}
					}
				}
				if (incomingPacket == 241) {
					int l16 = 0;
					int ai[] = new int[676];
					for (int i24 = 0; i24 < 4; i24++) {
						for (int k26 = 0; k26 < 13; k26++) {
							for (int l28 = 0; l28 < 13; l28++) {
								int k30 = anIntArrayArrayArray1129[i24][k26][l28];
								if (k30 != -1) {
									int k31 = k30 >> 14 & 0x3ff;
									int i32 = k30 >> 3 & 0x7ff;
									int k32 = (k31 / 8 << 8) + i32 / 8;
									for (int j33 = 0; j33 < l16; j33++) {
										if (ai[j33] != k32)
											continue;
										k32 = -1;

									}
									if (k32 != -1)
										ai[l16++] = k32;
								}
							}
						}
					}
					aByteArrayArray1183 = new byte[l16][];
					aByteArrayArray1247 = new byte[l16][];
					anIntArray1234 = new int[l16];
					anIntArray1235 = new int[l16];
					anIntArray1236 = new int[l16];
					for (int l26 = 0; l26 < l16; l26++) {
						int i29 = anIntArray1234[l26] = ai[l26];
						int l30 = i29 >> 8 & 0xff;
						int l31 = i29 & 0xff;
						int j32 = anIntArray1235[l26] = onDemandFetcher.method562(0, l31, l30);
						if (j32 != -1)
							onDemandFetcher.method558(3, j32);
						int i33 = anIntArray1236[l26] = onDemandFetcher.method562(1, l31, l30);
						if (i33 != -1)
							onDemandFetcher.method558(3, i33);
					}
				}
				int i17 = baseX - anInt1036;
				int j21 = baseY - anInt1037;
				anInt1036 = baseX;
				anInt1037 = baseY;
				for (int j24 = 0; j24 < 16384; j24++) {
					NPC npc = npcArray[j24];
					if (npc != null) {
						for (int j29 = 0; j29 < 10; j29++) {
							npc.smallX[j29] -= i17;
							npc.smallY[j29] -= j21;
						}
						npc.x -= i17 * 128;
						npc.y -= j21 * 128;
					}
				}
				for (int i27 = 0; i27 < maxPlayers; i27++) {
					Player player = playerArray[i27];
					if (player != null) {
						for (int i31 = 0; i31 < 10; i31++) {
							player.smallX[i31] -= i17;
							player.smallY[i31] -= j21;
						}
						player.x -= i17 * 128;
						player.y -= j21 * 128;
					}
				}
				aBoolean1080 = true;
				byte byte1 = 0;
				byte byte2 = 104;
				byte byte3 = 1;
				if (i17 < 0) {
					byte1 = 103;
					byte2 = -1;
					byte3 = -1;
				}
				byte byte4 = 0;
				byte byte5 = 104;
				byte byte6 = 1;
				if (j21 < 0) {
					byte4 = 103;
					byte5 = -1;
					byte6 = -1;
				}
				for (int k33 = byte1; k33 != byte2; k33 += byte3) {
					for (int l33 = byte4; l33 != byte5; l33 += byte6) {
						int i34 = k33 + i17;
						int j34 = l33 + j21;
						for (int k34 = 0; k34 < 4; k34++)
							if (i34 >= 0 && j34 >= 0 && i34 < 104 && j34 < 104)
								groundArray[k34][k33][l33] = groundArray[k34][i34][j34];
							else
								groundArray[k34][k33][l33] = null;
					}
				}
				for (Class30_Sub1 class30_sub1_1 = (Class30_Sub1) aClass19_1179
						.reverseGetFirst(); class30_sub1_1 != null; class30_sub1_1 = (Class30_Sub1) aClass19_1179
								.reverseGetNext()) {
					class30_sub1_1.anInt1297 -= i17;
					class30_sub1_1.anInt1298 -= j21;
					if (class30_sub1_1.anInt1297 < 0 || class30_sub1_1.anInt1298 < 0 || class30_sub1_1.anInt1297 >= 104
							|| class30_sub1_1.anInt1298 >= 104)
						class30_sub1_1.unlink();
				}
				if (destX != 0) {
					destX -= i17;
					destY -= j21;
				}
				aBoolean1160 = false;
				incomingPacket = -1;
				return true;

			case 208:
				int i3 = inStream.method437();
				if (i3 >= 0)
					method60(i3);
				openWalkableWidgetID = i3;
				incomingPacket = -1;
				return true;

			case 99:
				minimapState = inStream.readUnsignedByte();
				incomingPacket = -1;
				return true;

			case 75:
				int j3 = inStream.method436();
				int j11 = inStream.method436();
				RSInterface.interfaceCache[j11].anInt233 = 2;
				RSInterface.interfaceCache[j11].mediaID = j3;
				incomingPacket = -1;
				return true;

			case 114:
				anInt1104 = inStream.method434() * 30;
				incomingPacket = -1;
				return true;

			case 60:
				anInt1269 = inStream.readUnsignedByte();
				anInt1268 = inStream.method427();
				while (inStream.currentOffset < packetSize) {
					int k3 = inStream.readUnsignedByte();
					method137(inStream, k3);
				}
				incomingPacket = -1;
				return true;

			case 35:
				int l3 = inStream.readUnsignedByte();
				int k11 = inStream.readUnsignedByte();
				int j17 = inStream.readUnsignedByte();
				int k21 = inStream.readUnsignedByte();
				aBooleanArray876[l3] = true;
				anIntArray873[l3] = k11;
				anIntArray1203[l3] = j17;
				anIntArray928[l3] = k21;
				anIntArray1030[l3] = 0;
				incomingPacket = -1;
				return true;

			case 174:
				int i4 = inStream.readUnsignedWord();
				int l11 = inStream.readUnsignedByte();
				int k17 = inStream.readUnsignedWord();
				if (aBoolean848 && !lowMem && anInt1062 < 50) {
					anIntArray1207[anInt1062] = i4;
					anIntArray1241[anInt1062] = l11;
					anIntArray1250[anInt1062] = k17 + Sounds.anIntArray326[i4];
					anInt1062++;
				}
				incomingPacket = -1;
				return true;

			case 104:
				int j4 = inStream.method427();
				int i12 = inStream.method426();
				String s6 = inStream.readString();
				if (j4 >= 1 && j4 <= atPlayerActions.length) {
					if (s6.equalsIgnoreCase("null"))
						s6 = null;
					atPlayerActions[j4 - 1] = s6;
					atPlayerArray[j4 - 1] = i12 == 0;
				}
				incomingPacket = -1;
				return true;

			case 78:
				destX = 0;
				incomingPacket = -1;
				return true;

			case 253:
				String s = inStream.readString();
				if (s.equals(":prayertrue:")) {
					prayClicked = true;
				} else if (s.equals(":prayerfalse:")) {
					prayClicked = false;
				} else if (s.endsWith(":tradereq:")) {
					String s3 = s.substring(0, s.indexOf(":"));
					long l17 = TextClass.longForName(s3);
					boolean flag2 = false;
					for (int j27 = 0; j27 < ignoreCount; j27++) {
						if (ignoreListAsLongs[j27] != l17)
							continue;
						flag2 = true;

					}
					if (!flag2 && anInt1251 == 0)
						pushMessage("wishes to trade with you.", 4, s3);
				} else if (s.startsWith("//")) {
					s = s.replaceAll("//", "");
					pushMessage(s, 13, "");
				} else if (s.startsWith("/")) {
					s = s.replaceAll("/", "");
					pushMessage(s, 11, "");
				} else if (s.endsWith("#url#")) {
					String link = s.substring(0, s.indexOf("#"));
					pushMessage("Join us at: ", 9, link);
				} else if (s.endsWith(":duelreq:")) {
					String s4 = s.substring(0, s.indexOf(":"));
					long l18 = TextClass.longForName(s4);
					boolean flag3 = false;
					for (int k27 = 0; k27 < ignoreCount; k27++) {
						if (ignoreListAsLongs[k27] != l18)
							continue;
						flag3 = true;

					}
					if (!flag3 && anInt1251 == 0)
						pushMessage("wishes to duel with you.", 8, s4);
				} else if (s.endsWith(":chalreq:")) {
					String s5 = s.substring(0, s.indexOf(":"));
					long l19 = TextClass.longForName(s5);
					boolean flag4 = false;
					for (int l27 = 0; l27 < ignoreCount; l27++) {
						if (ignoreListAsLongs[l27] != l19)
							continue;
						flag4 = true;

					}
					if (!flag4 && anInt1251 == 0) {
						String s8 = s.substring(s.indexOf(":") + 1, s.length() - 9);
						pushMessage(s8, 8, s5);
					}
				} else {
					pushMessage(s, 0, "");
				}
				incomingPacket = -1;
				return true;

			case 1:
				for (int k4 = 0; k4 < playerArray.length; k4++)
					if (playerArray[k4] != null)
						playerArray[k4].anim = -1;
				for (int j12 = 0; j12 < npcArray.length; j12++)
					if (npcArray[j12] != null)
						npcArray[j12].anim = -1;
				incomingPacket = -1;
				return true;

			case 50:
				long l4 = inStream.readQWord();
				int i18 = inStream.readUnsignedByte();
				String s7 = TextClass.fixName(TextClass.nameForLong(l4));
				for (int k24 = 0; k24 < friendsCount; k24++) {
					if (l4 != friendsListAsLongs[k24])
						continue;
					if (friendsNodeIDs[k24] != i18) {
						friendsNodeIDs[k24] = i18;
						needDrawTabArea = true;
						if (i18 > 0)
							pushMessage(s7 + " has logged in.", 5, "");
						if (i18 == 0)
							pushMessage(s7 + " has logged out.", 5, "");
					}
					s7 = null;
					break;
				}
				if (s7 != null && friendsCount < 200) {
					friendsListAsLongs[friendsCount] = l4;
					friendsList[friendsCount] = s7;
					friendsNodeIDs[friendsCount] = i18;
					friendsCount++;
					needDrawTabArea = true;
				}
				for (boolean flag6 = false; !flag6;) {
					flag6 = true;
					for (int k29 = 0; k29 < friendsCount - 1; k29++)
						if (friendsNodeIDs[k29] != nodeID && friendsNodeIDs[k29 + 1] == nodeID
								|| friendsNodeIDs[k29] == 0 && friendsNodeIDs[k29 + 1] != 0) {
							int j31 = friendsNodeIDs[k29];
							friendsNodeIDs[k29] = friendsNodeIDs[k29 + 1];
							friendsNodeIDs[k29 + 1] = j31;
							String s10 = friendsList[k29];
							friendsList[k29] = friendsList[k29 + 1];
							friendsList[k29 + 1] = s10;
							long l32 = friendsListAsLongs[k29];
							friendsListAsLongs[k29] = friendsListAsLongs[k29 + 1];
							friendsListAsLongs[k29 + 1] = l32;
							needDrawTabArea = true;
							flag6 = false;
						}
				}
				incomingPacket = -1;
				return true;

			case 110:
				if (tabID == 12)
					needDrawTabArea = true;
				energy = inStream.readUnsignedByte();
				incomingPacket = -1;
				return true;

			case 254:
				anInt855 = inStream.readUnsignedByte();
				if (anInt855 == 1)
					anInt1222 = inStream.readUnsignedWord();
				if (anInt855 >= 2 && anInt855 <= 6) {
					if (anInt855 == 2) {
						anInt937 = 64;
						anInt938 = 64;
					}
					if (anInt855 == 3) {
						anInt937 = 0;
						anInt938 = 64;
					}
					if (anInt855 == 4) {
						anInt937 = 128;
						anInt938 = 64;
					}
					if (anInt855 == 5) {
						anInt937 = 64;
						anInt938 = 0;
					}
					if (anInt855 == 6) {
						anInt937 = 64;
						anInt938 = 128;
					}
					anInt855 = 2;
					anInt934 = inStream.readUnsignedWord();
					anInt935 = inStream.readUnsignedWord();
					anInt936 = inStream.readUnsignedByte();
				}
				if (anInt855 == 10)
					anInt933 = inStream.readUnsignedWord();
				incomingPacket = -1;
				return true;

			case 248:
				int i5 = inStream.method435();
				int k12 = inStream.readUnsignedWord();
				if (backDialogID != -1) {
					backDialogID = -1;
					inputTaken = true;
				}
				if (inputDialogState != 0) {
					inputDialogState = 0;
					inputTaken = true;
				}
				if (i5 == 55000) {
					RSInterface.interfaceCache[55010].scrollPosition = 0;
					RSInterface.interfaceCache[55050].scrollPosition = 0;
				}
				openInterfaceID = i5;
				invOverlayInterfaceID = k12;
				needDrawTabArea = true;
				tabAreaAltered = true;
				aBoolean1149 = false;
				incomingPacket = -1;
				return true;

			case 79:
				int j5 = inStream.method434();
				int l12 = inStream.method435();
				RSInterface class9_3 = RSInterface.interfaceCache[j5];
				if (class9_3 != null && class9_3.type == 0) {
					if (l12 < 0)
						l12 = 0;
					if (l12 > class9_3.scrollMax - class9_3.height)
						l12 = class9_3.scrollMax - class9_3.height;
					class9_3.scrollPosition = l12;
				}
				incomingPacket = -1;
				return true;

			case 68:
				for (int k5 = 0; k5 < variousSettings.length; k5++)
					if (variousSettings[k5] != anIntArray1045[k5]) {
						variousSettings[k5] = anIntArray1045[k5];
						method33(k5);
						needDrawTabArea = true;
					}
				incomingPacket = -1;
				return true;

			case 196:
				long l5 = inStream.readQWord();
				inStream.readDWord();
				int l21 = inStream.readUnsignedByte();
				boolean flag5 = false;
				if (l21 <= 1) {
					for (int l29 = 0; l29 < ignoreCount; l29++) {
						if (ignoreListAsLongs[l29] != l5)
							continue;
						flag5 = true;

					}
				}
				if (!flag5 && anInt1251 == 0)
					try {
						String s9 = TextInput.method525(packetSize - 13, inStream);
						String rights = l21 > 0 ? "@cr" + l21 + "@" : "";
						pushMessage(s9, 7, rights + TextClass.fixName(TextClass.nameForLong(l5)));
					} catch (Exception exception1) {
						Signlink.reporterror("cde1");
					}
				incomingPacket = -1;
				return true;

			case 85:
				anInt1269 = inStream.method427();
				anInt1268 = inStream.method427();
				incomingPacket = -1;
				return true;

			case 24:
				anInt1054 = inStream.method428();
				if (anInt1054 == tabID) {
					if (anInt1054 == 3)
						tabID = 1;
					else
						tabID = 3;
					needDrawTabArea = true;
				}
				incomingPacket = -1;
				return true;

			case 246:
				int i6 = inStream.method434();
				int i13 = inStream.readUnsignedWord();
				int k18 = inStream.readUnsignedWord();
				if (k18 == 65535) {
					RSInterface.interfaceCache[i6].anInt233 = 0;
					incomingPacket = -1;
					return true;
				} else {
					ItemDefinition itemDef = ItemDefinition.forID(k18);
					RSInterface.interfaceCache[i6].anInt233 = 4;
					RSInterface.interfaceCache[i6].mediaID = k18;
					RSInterface.interfaceCache[i6].modelRotation1 = itemDef.modelRotation1;
					RSInterface.interfaceCache[i6].modelRotation2 = itemDef.modelRotation2;
					RSInterface.interfaceCache[i6].modelZoom = (itemDef.modelZoom * 100) / i13;
					incomingPacket = -1;
					return true;
				}

			case 171:
				boolean flag1 = inStream.readUnsignedByte() == 1;
				int j13 = inStream.readUnsignedWord();
				if (RSInterface.interfaceCache[j13] != null)
					RSInterface.interfaceCache[j13].isMouseoverTriggered = flag1;
				incomingPacket = -1;
				return true;

			case 142:
				int j6 = inStream.method434();
				method60(j6);
				if (backDialogID != -1) {
					backDialogID = -1;
					inputTaken = true;
				}
				if (inputDialogState != 0) {
					inputDialogState = 0;
					inputTaken = true;
				}
				invOverlayInterfaceID = j6;
				needDrawTabArea = true;
				tabAreaAltered = true;
				openInterfaceID = -1;
				aBoolean1149 = false;
				incomingPacket = -1;
				return true;

			case 126:
				try {
					text = inStream.readString();
					int frame = inStream.method435();

					handleRunePouch(text, frame);

					if (text.startsWith("www.") || text.startsWith("http")) {
						launchURL(text);
						incomingPacket = -1;
						return true;
					}
					updateStrings(text, frame);
					sendFrame126(text, frame);
					if (frame >= 18144 && frame <= 18244) {
						clanList[frame - 18144] = text;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				incomingPacket = -1;
				return true;

			case 206:
				publicChatMode = inStream.readUnsignedByte();
				privateChatMode = inStream.readUnsignedByte();
				tradeMode = inStream.readUnsignedByte();
				inputTaken = true;
				incomingPacket = -1;
				return true;

			case 240:
				if (tabID == 12)
					needDrawTabArea = true;
				weight = inStream.readSignedWord();
				incomingPacket = -1;
				return true;

			case 8:
				int k6 = inStream.method436();
				int l13 = inStream.readUnsignedWord();
				RSInterface.interfaceCache[k6].anInt233 = 1;
				RSInterface.interfaceCache[k6].mediaID = l13;
				incomingPacket = -1;
				return true;

			case 122:
				int l6 = inStream.method436();
				int i14 = inStream.method436();
				int i19 = i14 >> 10 & 0x1f;
				int i22 = i14 >> 5 & 0x1f;
				int l24 = i14 & 0x1f;
				RSInterface.interfaceCache[l6].textColor = (i19 << 19) + (i22 << 11) + (l24 << 3);
				incomingPacket = -1;
				return true;

			case 53:
				needDrawTabArea = true;
				int i7 = inStream.readUnsignedWord();
				RSInterface class9_1 = RSInterface.interfaceCache[i7];
				int j19 = inStream.readUnsignedWord();
				for (int j22 = 0; j22 < j19; j22++) {
					int i25 = inStream.readUnsignedByte();
					if (i25 == 255)
						i25 = inStream.method440();
					class9_1.inv[j22] = inStream.method436();
					class9_1.invStackSizes[j22] = i25;
				}
				for (int j25 = j19; j25 < class9_1.inv.length; j25++) {
					class9_1.inv[j25] = 0;
					class9_1.invStackSizes[j25] = 0;
				}
				incomingPacket = -1;
				return true;

			case 230:
				int j7 = inStream.method435();
				int j14 = inStream.readUnsignedWord();
				int k19 = inStream.readUnsignedWord();
				int k22 = inStream.method436();
				RSInterface.interfaceCache[j14].modelRotation1 = k19;
				RSInterface.interfaceCache[j14].modelRotation2 = k22;
				RSInterface.interfaceCache[j14].modelZoom = j7;
				incomingPacket = -1;
				return true;

			case 221:
				anInt900 = inStream.readUnsignedByte();
				needDrawTabArea = true;
				incomingPacket = -1;
				return true;

			case 177:
				// setNorth();
				aBoolean1160 = true;
				anInt995 = inStream.readUnsignedByte();
				anInt996 = inStream.readUnsignedByte();
				anInt997 = inStream.readUnsignedWord();
				anInt998 = inStream.readUnsignedByte();
				anInt999 = inStream.readUnsignedByte();
				if (anInt999 >= 100) {
					int k7 = anInt995 * 128 + 64;
					int k14 = anInt996 * 128 + 64;
					int i20 = method42(plane, k14, k7) - anInt997;
					int l22 = k7 - xCameraPos;
					int k25 = i20 - zCameraPos;
					int j28 = k14 - yCameraPos;
					int i30 = (int) Math.sqrt(l22 * l22 + j28 * j28);
					yCameraCurve = (int) (Math.atan2(k25, i30) * 325.94900000000001D) & 0x7ff;
					xCameraCurve = (int) (Math.atan2(l22, j28) * -325.94900000000001D) & 0x7ff;
					if (yCameraCurve < 128)
						yCameraCurve = 128;
					if (yCameraCurve > 383)
						yCameraCurve = 383;
				}
				incomingPacket = -1;
				return true;

			case 249:
				anInt1046 = inStream.method426();
				unknownInt10 = inStream.method436();
				incomingPacket = -1;
				return true;

			case 65:
				updateNPCs(inStream, packetSize);
				incomingPacket = -1;
				return true;

			case 27:
				messagePromptRaised = false;
				inputDialogState = 1;
				amountOrNameInput = "";
				inputTaken = true;
				incomingPacket = -1;
				return true;

			case 187:
				messagePromptRaised = false;
				inputDialogState = 2;
				amountOrNameInput = "";
				inputTaken = true;
				incomingPacket = -1;
				return true;

			case 97:
				int l7 = inStream.readUnsignedWord();
				method60(l7);
				if (invOverlayInterfaceID != -1) {
					invOverlayInterfaceID = -1;
					needDrawTabArea = true;
					tabAreaAltered = true;
				}
				if (backDialogID != -1) {
					backDialogID = -1;
					inputTaken = true;
				}
				if (inputDialogState != 0) {
					inputDialogState = 0;
					inputTaken = true;
				}
				// 17511 = Question Type
				// 15819 = Christmas Type
				// 15812 = Security Type
				// 15801 = Item Scam Type
				// 15791 = Password Safety ?
				// 15774 = Good/Bad Password
				// 15767 = Drama Type ????
				if (l7 == 15244) {
					openInterfaceID = 15767;
					fullscreenInterfaceID = 15244;
				} else {
					openInterfaceID = l7;
				}
				aBoolean1149 = false;
				incomingPacket = -1;
				return true;

			case 15:
				autocast = inStream.readUnsignedByte() == 1 ? true : false;
				incomingPacket = -1;
				return true;

			case 218:
				int i8 = inStream.method438();
				dialogID = i8;
				inputTaken = true;
				incomingPacket = -1;
				return true;

			case 87:
				int j8 = inStream.method434();
				int l14 = inStream.method439();
				anIntArray1045[j8] = l14;
				if (variousSettings[j8] != l14) {
					variousSettings[j8] = l14;
					method33(j8);
					needDrawTabArea = true;
					if (dialogID != -1)
						inputTaken = true;
				}
				incomingPacket = -1;
				return true;

			case 36:
				int k8 = inStream.method434();
				byte byte0 = inStream.readSignedByte();
				anIntArray1045[k8] = byte0;
				if (variousSettings[k8] != byte0) {
					variousSettings[k8] = byte0;
					method33(k8);
					needDrawTabArea = true;
					if (dialogID != -1)
						inputTaken = true;
				}
				incomingPacket = -1;
				return true;

			case 61:
				anInt1055 = inStream.readUnsignedByte();
				incomingPacket = -1;
				return true;

			case 200:
				int l8 = inStream.readUnsignedWord();
				int i15 = inStream.readSignedWord();
				RSInterface class9_4 = RSInterface.interfaceCache[l8];
				class9_4.anInt257 = i15;
				if (i15 == 591 || i15 == 588) {
					class9_4.modelZoom = 900; // anInt269
				}
				if (i15 == -1) {
					class9_4.anInt246 = 0;
					class9_4.anInt208 = 0;
				}
				incomingPacket = -1;
				return true;

			case 219:
				if (invOverlayInterfaceID != -1) {
					invOverlayInterfaceID = -1;
					needDrawTabArea = true;
					tabAreaAltered = true;
				}
				if (backDialogID != -1) {
					backDialogID = -1;
					inputTaken = true;
				}
				if (inputDialogState != 0) {
					inputDialogState = 0;
					inputTaken = true;
				}
				if (this.isFieldInFocus()) {
					this.resetInputFieldFocus();
					Client.inputString = "";
				}
				openInterfaceID = -1;
				aBoolean1149 = false;
				incomingPacket = -1;
				return true;

			case 34:
				needDrawTabArea = true;
				int i9 = inStream.readUnsignedWord();
				RSInterface class9_2 = RSInterface.interfaceCache[i9];
				while (inStream.currentOffset < packetSize) {
					int j20 = inStream.method422();
					int i23 = inStream.readUnsignedWord();
					int l25 = inStream.readUnsignedByte();
					if (l25 == 255)
						l25 = inStream.readDWord();
					if (j20 >= 0 && j20 < class9_2.inv.length) {
						class9_2.inv[j20] = i23;
						class9_2.invStackSizes[j20] = l25;
					}
				}
				incomingPacket = -1;
				return true;

			case 4:
			case 44:
			case 84:
			case 101:
			case 105:
			case 117:
			case 147:
			case 151:
			case 156:
			case 160:
			case 215:
				method137(inStream, incomingPacket);
				incomingPacket = -1;
				return true;

			case 106:
				tabID = inStream.method427();
				needDrawTabArea = true;
				tabAreaAltered = true;
				incomingPacket = -1;
				return true;

			case 164:
				int j9 = inStream.method434();
				method60(j9);
				if (invOverlayInterfaceID != -1) {
					invOverlayInterfaceID = -1;
					needDrawTabArea = true;
					tabAreaAltered = true;
				}
				backDialogID = j9;
				inputTaken = true;
				openInterfaceID = -1;
				aBoolean1149 = false;
				incomingPacket = -1;
				return true;

			}
			Signlink.reporterror("T1 - Packet: " + incomingPacket + ", Packet Size: " + packetSize + " - Previous packet: " + previousPacket1 + " Previous packet size: " + previousPacketSize1
					+ ", 2nd Previous packet: " + previousPacket2 + ", 2nd Previous packet size: " + previousPacketSize2);
			resetLogout();
		} catch (IOException _ex) {
			dropClient();
		} catch (Exception exception) {
			String s2 = "T2 - " + incomingPacket + "," + previousPacket1 + "," + previousPacket2 + " - " + packetSize + ","
					+ (baseX + myPlayer.smallX[0]) + "," + (baseY + myPlayer.smallY[0]) + " - ";
			for (int j15 = 0; j15 < packetSize && j15 < 50; j15++)
				s2 = s2 + inStream.buffer[j15] + ",";
			exception.printStackTrace();
			Signlink.reporterror(s2);
			resetLogout();
		}
		return true;
	}

	
static ArrayList<GroundItemColors> groundItemColors = new ArrayList<GroundItemColors>();
	
	
	private boolean itemColorExists(int itemID) {
		for (int index = 0; index < groundItemColors.size(); index++) {
			GroundItemColors itemColor = groundItemColors.get(index);
			if (itemColor.itemId == itemID)
				return true;
		}
		return false;
	}
	private GroundItemColors getItemColor(int itemID) {
		for (int index = 0; index < groundItemColors.size(); index++) {
			GroundItemColors itemColor = groundItemColors.get(index);
			if (itemColor.itemId == itemID)
				return itemColor;
		}
		return null;
	}
	private String coloredItemName = "";
	private int coloredItemColor = 0xffffff;
	
	private void setItemColor(String coloredItemName2, int coloredItemColor2) {
		int[] itemids = new int[5];
		int found = 0;
		for (int index = 0; index < ItemDefinition.totalItems; index++) {
			ItemDefinition def = ItemDefinition.forID(index);
			if (def.name == null || def == null)
				continue;
			if (def.name.replace("_", " ").toLowerCase().equalsIgnoreCase(coloredItemName2.toLowerCase())) {				
			if (found > 4) {
				found = 4;
			}
				itemids[found] = index;
				found++;
			}
		}
		for (int index = 0; index < found; index++) {
			GroundItemColors item = null;
			if (itemColorExists(itemids[index])) {
				item = getItemColor(itemids[index]);
			} else {
				groundItemColors.add(new GroundItemColors(itemids[index], coloredItemColor2));
			}
			if (item != null) {
				item.itemColor = coloredItemColor2;
			}
		}
		pushMessage("Color for item: " + coloredItemName + " was set! (If found)", 0, "");
		coloredItemName = "";
		coloredItemColor = 0xffffff;
		RSInterface.interfaceCache[37707].message = "Choose a color below!"; //0xFE9624
		RSInterface.interfaceCache[37707].textColor = 0xFE9624;
		GroundItemColors.saveGroundItems(groundItemColors);
	}
	
	
	public static double cameraZoom = 3.0D;

	public void method146() {
		anInt1265++;
		method47(true);
		method26(true);
		method47(false);
		method26(false);
		method55();
		method104();
		if (!aBoolean1160) {
			int i = anInt1184;
			if (anInt984 / 256 > i)
				i = anInt984 / 256;
			if (aBooleanArray876[4] && anIntArray1203[4] + 128 > i)
				i = anIntArray1203[4] + 128;
			int k = viewRotation + viewRotationOffset & 0x7ff;
			setCameraPos(600 + i * cameraZoom, i, anInt1014, method42(plane, myPlayer.y, myPlayer.x) - 50, k,
					anInt1015);
		}
		int j;
		if (!aBoolean1160)
			j = method120();
		else
			j = method121();
		int l = xCameraPos;
		int i1 = zCameraPos;
		int j1 = yCameraPos;
		int k1 = yCameraCurve;
		int l1 = xCameraCurve;
		for (int i2 = 0; i2 < 5; i2++)
			if (aBooleanArray876[i2]) {
				int j2 = (int) ((Math.random() * (anIntArray873[i2] * 2 + 1) - anIntArray873[i2])
						+ Math.sin(anIntArray1030[i2] * (anIntArray928[i2] / 100D)) * anIntArray1203[i2]);
				if (i2 == 0)
					xCameraPos += j2;
				if (i2 == 1)
					zCameraPos += j2;
				if (i2 == 2)
					yCameraPos += j2;
				if (i2 == 3)
					xCameraCurve = xCameraCurve + j2 & 0x7ff;
				if (i2 == 4) {
					yCameraCurve += j2;
					if (yCameraCurve < 128)
						yCameraCurve = 128;
					if (yCameraCurve > 383)
						yCameraCurve = 383;
				}
			}
		int k2 = Texture.anInt1481;
		Model.aBoolean1684 = true;
		Model.anInt1687 = 0;
		Model.anInt1685 = super.mouseX;
		Model.anInt1686 = super.mouseY;
		DrawingArea.setAllPixelsToZero();
		worldController.method313(xCameraPos, yCameraPos, xCameraCurve, zCameraPos, j, yCameraCurve);
		worldController.clearObj5Cache();
		updateEntities();
		drawHeadIcon();
		method37(k2);
		if (loggedIn) {
			if (loginScreenGraphicsBuffer == null && currentScreenMode != ScreenMode.FIXED) {
				drawMinimap();
				drawTabArea();
				drawChatArea();
			}
			draw3dScreen();
		}
		if (groundItemsOn) {
			displayGroundItems();
		}
		draw3dScreen();
		processExperienceCounter();
		mainGameGraphicsBuffer.drawGraphics(0, 0, super.graphics);
		xCameraPos = l;
		zCameraPos = i1;
		yCameraPos = j1;
		yCameraCurve = k1;
		xCameraCurve = l1;
	}

	public void clearTopInterfaces() {
		stream.createFrame(130);
		if (invOverlayInterfaceID != -1) {
			invOverlayInterfaceID = -1;
			needDrawTabArea = true;
			aBoolean1149 = false;
			tabAreaAltered = true;
		}
		if (backDialogID != -1) {
			backDialogID = -1;
			inputTaken = true;
			aBoolean1149 = false;
		}
		openInterfaceID = -1;
		fullscreenInterfaceID = -1;
	}

	private Sprite[] chatButtons;

	public float LP;

	Client() {
		xpAddedPos = expAdded = 0;
		xpLock = false;
		experienceCounter = 0;
		chatRights = new int[500];
		fullscreenInterfaceID = -1;
		chatTypeView = 0;
		clanTitles = new String[500];
		clanChatMode = 0;
		channelButtonHoverPosition = -1;
		channelButtonClickPosition = 0;
		//server = Configuration.LIVE_SERVER ? "198.12.13.49" : "localhost";  //"70.42.74.5" : "localhost";
		server = Configuration.LIVE_SERVER.booleanValue() ? "127.0.0.1" : "localhost";
		anIntArrayArray825 = new int[104][104];
		friendsNodeIDs = new int[200];
		groundArray = new NodeList[4][104][104];
		aBoolean831 = false;
		aStream_834 = new Stream(new byte[5000]);
		npcArray = new NPC[16384];
		npcIndices = new int[16384];
		anIntArray840 = new int[1000];
		aStream_847 = Stream.create();
		aBoolean848 = true;
		openInterfaceID = -1;
		currentExp = new int[Skills.SKILLS_COUNT];
		anIntArray873 = new int[5];
		anInt874 = -1;
		aBooleanArray876 = new boolean[5];
		drawFlames = false;
		reportAbuseInput = "";
		unknownInt10 = -1;
		menuOpen = false;
		inputString = "";
		maxPlayers = 2048;
		myPlayerIndex = 2047;
		playerArray = new Player[maxPlayers];
		playerIndices = new int[maxPlayers];
		anIntArray894 = new int[maxPlayers];
		aStreamArray895s = new Stream[maxPlayers];
		anIntArrayArray901 = new int[104][104];
		aByteArray912 = new byte[16384];
		currentStats = new int[Skills.SKILLS_COUNT];
		ignoreListAsLongs = new long[100];
		loadingError = false;
		anIntArray928 = new int[5];
		anIntArrayArray929 = new int[104][104];
		chatTypes = new int[500];
		chatNames = new String[500];
		chatMessages = new String[500];
		chatButtons = new Sprite[4];
		aBoolean954 = true;
		friendsListAsLongs = new long[200];
		currentSong = -1;
		drawingFlames = false;
		spriteDrawX = -1;
		spriteDrawY = -1;
		anIntArray968 = new int[33];
		anIntArray969 = new int[256];
		decompressors = new Decompressor[5];
		variousSettings = new int[25000];
		aBoolean972 = false;
		anInt975 = 50;
		anIntArray976 = new int[anInt975];
		anIntArray977 = new int[anInt975];
		anIntArray978 = new int[anInt975];
		anIntArray979 = new int[anInt975];
		anIntArray980 = new int[anInt975];
		anIntArray981 = new int[anInt975];
		anIntArray982 = new int[anInt975];
		aStringArray983 = new String[anInt975];
		anInt985 = -1;
		hitMarks = new Sprite[20];
		anIntArray990 = new int[5];
		aBoolean994 = false;
		amountOrNameInput = "";
		aClass19_1013 = new NodeList();
		aBoolean1017 = false;
		openWalkableWidgetID = -1;
		anIntArray1030 = new int[5];
		aBoolean1031 = false;
		mapFunctions = new Sprite[100];
		dialogID = -1;
		maxStats = new int[Skills.SKILLS_COUNT];
		anIntArray1045 = new int[25000];
		aBoolean1047 = true;
		anIntArray1052 = new int[152];
		anIntArray1229 = new int[152];
		anInt1054 = -1;
		aClass19_1056 = new NodeList();
		anIntArray1057 = new int[33];
		aClass9_1059 = new RSInterface();
		mapScenes = new Background[100];
		barFillColor = 0x4d4233;
		anIntArray1065 = new int[7];
		anIntArray1072 = new int[1000];
		anIntArray1073 = new int[1000];
		aBoolean1080 = false;
		friendsList = new String[200];
		inStream = Stream.create();
		expectedCRCs = new int[9];
		menuActionCmd2 = new int[500];
		menuActionCmd3 = new int[500];
		menuActionID = new int[500];
		menuActionCmd1 = new int[500];
		headIcons = new Sprite[20];
		skullIcons = new Sprite[20];
		headIconsHint = new Sprite[20];
		tabAreaAltered = false;
		aString1121 = "";
		atPlayerActions = new String[6];
		atPlayerArray = new boolean[6];
		anIntArrayArrayArray1129 = new int[4][13][13];
		mapIconSprite = new Sprite[1000];
		aBoolean1141 = false;
		aBoolean1149 = false;
		crosses = new Sprite[8];
		musicEnabled = true;
		needDrawTabArea = false;
		loggedIn = false;
		canMute = false;
		aBoolean1159 = false;
		aBoolean1160 = false;
		myUsername = "";
		myPassword = "";
		genericLoadingError = false;
		reportAbuseInterfaceID = -1;
		aClass19_1179 = new NodeList();
		anInt1184 = 128;
		invOverlayInterfaceID = -1;
		stream = Stream.create();
		menuActionName = new String[500];
		anIntArray1203 = new int[5];
		anIntArray1207 = new int[50];
		chatAreaScrollLength = 78;
		promptInput = "";
		modIcons = new Sprite[23];
		tabID = 3;
		inputTaken = false;
		songChanging = true;

		aClass11Array1230 = new Class11[4];
		anIntArray1241 = new int[50];
		aBoolean1242 = false;
		anIntArray1250 = new int[50];
		rsAlreadyLoaded = false;
		welcomeScreenRaised = false;
		messagePromptRaised = false;
		backDialogID = -1;
		bigX = new int[4000];
		bigY = new int[4000];
		anInt1289 = -1;
	}

	private final int[] chatRights;
	public int xpCounter;
	public int expAdded;
	public int xpAddedPos;
	public boolean xpLock;

	private Sprite chatArea;

	public String name;
	public String message;
	public int chatTypeView;
	public int clanChatMode;
	public int duelMode;
	public int autocastId = 0;
	public boolean autocast = false;
	
	public static Sprite[] cacheSprite;
	public static Sprite[] cacheInterface;

	private Background titleBox;
	private Sprite loginHover;
	private Sprite boxHover;
	public Sprite[] hoverLogin = new Sprite[3];
	private Background titleButton;
	private int ignoreCount;
	private long aLong824;
	private int[][] anIntArrayArray825;
	private int[] friendsNodeIDs;
	private NodeList[][][] groundArray;
	private int[] anIntArray828;
	private int[] anIntArray829;
	private volatile boolean aBoolean831;
	private int loginScreenState;
	private Stream aStream_834;
	public NPC[] npcArray;
	private int npcCount;
	private int[] npcIndices;
	private int anInt839;
	private int[] anIntArray840;
	private int dealtWithPacket;
	private int dealtWithPacketSize;
	private int previousPacket1;
	private int previousPacket2;
	private int previousPacketSize2;
	private int previousPacketSize1;
	private String aString844;
	private int privateChatMode;
	private Stream aStream_847;
	private boolean aBoolean848;
	private static int anInt849;
	private int[] anIntArray850;
	private int[] anIntArray851;
	private int[] anIntArray852;
	private int[] anIntArray853;
	private static int anInt854;
	private int anInt855;
	public static int openInterfaceID;
	private int xCameraPos;
	private int zCameraPos;
	private int yCameraPos;
	private int yCameraCurve;
	private int xCameraCurve;
	private final int[] currentExp;
	private Sprite mapFlag;
	private Sprite mapMarker;
	private final int[] anIntArray873;
	private int anInt874;
	private final boolean[] aBooleanArray876;
	private int weight;
	private MouseDetection mouseDetection;
	private volatile boolean drawFlames;
	private String reportAbuseInput;
	private int unknownInt10;
	private boolean menuOpen;
	private int anInt886;
	public static String inputString;
	private final int maxPlayers;
	private final int myPlayerIndex;
	public Player[] playerArray;
	private int playerCount;
	private int[] playerIndices;
	private int anInt893;
	private int[] anIntArray894;
	private Stream[] aStreamArray895s;
	private int viewRotationOffset;
	private int friendsCount;
	private int anInt900;
	private int[][] anIntArrayArray901;
	private byte[] aByteArray912;
	private int anInt913;
	private int crossX;
	private int crossY;
	private int crossIndex;
	private int crossType;
	private int plane;
	private final int[] currentStats;
	private static int anInt924;
	private final long[] ignoreListAsLongs;
	private boolean loadingError;
	private final int[] anIntArray928;
	private int[][] anIntArrayArray929;
	private Sprite aClass30_Sub2_Sub1_Sub1_931;
	private Sprite aClass30_Sub2_Sub1_Sub1_932;
	private int anInt933;
	private int anInt934;
	private int anInt935;
	private int anInt936;
	private int anInt937;
	private int anInt938;
	private final int[] chatTypes;
	private final String[] chatNames;
	private final String[] chatMessages;
	private int anInt945;
	private WorldController worldController;
	private int menuScreenArea;
	private int menuOffsetX;
	private int menuOffsetY;
	private int menuWidth;
	private int menuHeight;
	private long aLong953;
	private boolean aBoolean954;
	private long[] friendsListAsLongs;
	private int currentSong;
	private static int nodeID = 1;
	static int portOff;
	static boolean clientData;
	private static boolean isMembers = true;
	private static boolean lowMem;
	private volatile boolean drawingFlames;
	private int spriteDrawX;
	private int spriteDrawY;
	private final int[] anIntArray965 = { 0xffff00, 0xff0000, 65280, 65535, 0xff00ff, 0xffffff };
	private final int[] anIntArray968;
	private final int[] anIntArray969;
	final Decompressor[] decompressors;
	public int variousSettings[];
	private boolean aBoolean972;
	private final int anInt975;
	private final int[] anIntArray976;
	private final int[] anIntArray977;
	private final int[] anIntArray978;
	private final int[] anIntArray979;
	private final int[] anIntArray980;
	private final int[] anIntArray981;
	private final int[] anIntArray982;
	private final String[] aStringArray983;
	private int anInt984;
	private int anInt985;
	private static int anInt986;
	private Sprite[] hitMarks;
	private int anInt989;
	private final int[] anIntArray990;
	private final boolean aBoolean994;
	private int anInt995;
	private int anInt996;
	private int anInt997;
	private int anInt998;
	private int anInt999;
	private ISAACRandomGen encryption;
	private Sprite mapEdge;
	public static int[][] anIntArrayArray1003 = {
			{ 6798, 107, 10283, 16, 4797, 7744, 5799, 4634, -31839, 22433, 2983, -11343, 8, 5281, 10438, 3650, -27322,
					-21845, 200, 571, 908, 21830, 28946, -15701, -14010 },
			{ 8741, 12, -1506, -22374, 7735, 8404, 1701, -27106, 24094, 10153, -8915, 4783, 1341, 16578, -30533, 25239,
					8, 5281, 10438, 3650, -27322, -21845, 200, 571, 908, 21830, 28946, -15701, -14010 },
			{ 25238, 8742, 12, -1506, -22374, 7735, 8404, 1701, -27106, 24094, 10153, -8915, 4783, 1341, 16578, -30533,
					8, 5281, 10438, 3650, -27322, -21845, 200, 571, 908, 21830, 28946, -15701, -14010 },
			{ 4626, 11146, 6439, 12, 4758, 10270 }, 
			
			{ 4550, 4537, 5681, 5673, 5790, 6806, 8076, 4574,
			  0, 12821, 100 } };

	private Sprite multiOverlay;
	private String amountOrNameInput;
	private static int anInt1005;
	private int daysSinceLastLogin;
	private int packetSize;
	private int incomingPacket;
	private int anInt1009;
	private int anInt1010;
	private int anInt1011;
	private NodeList aClass19_1013;
	private int anInt1014;
	private int anInt1015;
	private int anInt1016;
	private boolean aBoolean1017;
	public static int openWalkableWidgetID;
	private static final int[] anIntArray1019;
	private int minimapState;
	private int anInt1022;
	private int loadingStage;
	private Sprite scrollBar1;
	private Sprite scrollBar2;
	private int anInt1026;
	private final int[] anIntArray1030;
	private boolean aBoolean1031;
	private static Sprite[] mapFunctions;
	private static int baseX;
	private static int baseY;
	private int anInt1036;
	private int anInt1037;
	private int loginFailures;
	private int anInt1039;
	private int anInt1040;
	private int anInt1041;
	private int dialogID;
	private final int[] maxStats;
	private final int[] anIntArray1045;
	private int anInt1046;
	private boolean aBoolean1047;
	public TextDrawingArea smallText;
	public TextDrawingArea XPFONT;
	public TextDrawingArea aTextDrawingArea_1271;
	public TextDrawingArea chatTextDrawingArea;
	public TextDrawingArea aTextDrawingArea_1273;
	public RSFont newSmallFont;
	public RSFont newRegularFont;
	public RSFont newBoldFont;
	public RSFont newFancyFont;
	private int anInt1048;
	private String aString1049;
	private static int anInt1051;
	private final int[] anIntArray1052;
	private StreamLoader titleStreamLoader;
	private int anInt1054;
	private int anInt1055;
	private NodeList aClass19_1056;
	private final int[] anIntArray1057;
	public final RSInterface aClass9_1059;
	private Background[] mapScenes;
	private static int anInt1061;
	private int anInt1062;
	private final int barFillColor;
	private int friendsListAction;
	private final int[] anIntArray1065;
	private int mouseInvInterfaceIndex;
	private int lastActiveInvInterface;
	public OnDemandFetcher onDemandFetcher;
	private int mapRegionsX;
	private int mapRegionsY;
	private int mapIconAmount;
	private int[] anIntArray1072;
	private int[] anIntArray1073;
	private Sprite mapDotItem;
	private Sprite mapDotNPC;
	private Sprite mapDotPlayer;
	private Sprite mapDotFriend;
	private Sprite mapDotTeam;
	private Sprite mapDotClan;
	private int anInt1079;
	private boolean aBoolean1080;
	private String[] friendsList;
	private Stream inStream;
	private int anInt1084;
	private int anInt1085;
	private int activeInterfaceType;
	private int anInt1087;
	private int anInt1088;
	public static int anInt1089;
	private final int[] expectedCRCs;
	private int[] menuActionCmd2;
	private int[] menuActionCmd3;
	private int[] menuActionID;
	private int[] menuActionCmd1;
	private Sprite[] headIcons;
	private Sprite[] skullIcons;
	private Sprite[] headIconsHint;
	private static int anInt1097;
	private int anInt1098;
	private int anInt1099;
	private int anInt1100;
	private int anInt1101;
	private int anInt1102;
	public static boolean tabAreaAltered;
	private RSImageProducer loginScreenGraphicsBuffer;
	private RSImageProducer leftSideFlame;
	private RSImageProducer rightSideFlame;
	private int anInt1104;
	private RSImageProducer aRSImageProducer_1110;
	private RSImageProducer aRSImageProducer_1111;
	private static int anInt1117;
	private int membersInt;
	private String aString1121;
	public static Player myPlayer;
	private final String[] atPlayerActions;
	private final boolean[] atPlayerArray;
	private final int[][][] anIntArrayArrayArray1129;
	final static int[] tabInterfaceIDs = { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 };
	private int cameraOffsetY;
	private int menuActionRow;
	private static int anInt1134;
	private int spellSelected;
	private int anInt1137;
	private int spellUsableOn;
	private String spellTooltip;
	private Sprite[] mapIconSprite;
	private boolean aBoolean1141;
	private static int anInt1142;
	private int energy;
	private boolean aBoolean1149;
	private Sprite[] crosses;
	private boolean musicEnabled;
	private Background[] aBackgroundArray1152s;
	public static boolean needDrawTabArea;
	private int unreadMessages;
	private static int anInt1155;
	private static boolean fpsOn;
	public boolean loggedIn;
	private boolean canMute;
	private boolean aBoolean1159;
	private boolean aBoolean1160;
	static int loopCycle;
	private static final String validUserPassChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!\"\243$%^&*()-_=+[{]};:'@#~,<.>/?\\| ";
	private RSImageProducer tabAreaGraphicsBuffer;
	private RSImageProducer mapAreaGraphicsBuffer;
	public RSImageProducer mainGameGraphicsBuffer;
	private RSImageProducer chatAreaGraphicsBuffer;
	private int daysSinceRecovChange;
	private RSSocket socketStream;
	private static int minimapZoom;
	private long aLong1172;
	private String myUsername;
	private String myPassword;
	private static int anInt1175;
	private boolean genericLoadingError;
	private final int[] anIntArray1177 = { 0, 0, 0, 0, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3 };
	private int reportAbuseInterfaceID;
	private NodeList aClass19_1179;
	private int[] anIntArray1180;
	private int[] anIntArray1181;
	private int[] anIntArray1182;
	private byte[][] aByteArrayArray1183;
	private int anInt1184;
	private static int viewRotation;
	private int anInt1186;
	private int anInt1187;
	private static int anInt1188;
	private int invOverlayInterfaceID;
	private int[] anIntArray1190;
	private int[] anIntArray1191;
	private static Stream stream;
	private int anInt1193;
	private boolean splitPrivateChat = true;
	private String[] clanList = new String[100];
	private String[] menuActionName;
	private Sprite aClass30_Sub2_Sub1_Sub1_1201;
	private Sprite aClass30_Sub2_Sub1_Sub1_1202;
	private final int[] anIntArray1203;
	public static int anIntArray1204[] = { 9104, 10275, 7595, 3610, 7975, 8526, 918, -26734, 24466, 10145, -6882, 5027,
			1457, 16565, -30545, 25486, 24, 5392, 10429, 3673, -27335, -21957, 192, 687, 412, 21821, 28835, -15460,
			-14019 };
	private static boolean flagged;
	private final int[] anIntArray1207;
	private int anInt1208;
	private static int minimapRotation;
	public static int chatAreaScrollLength;
	private String promptInput;
	private int anInt1213;
	private int[][][] intGroundArray;
	private long aLong1215;
	private int loginScreenCursorPos;
	private final Sprite[] modIcons;
	private long aLong1220;
	public static int tabID;
	private int anInt1222;
	public static boolean inputTaken;
	static int inputDialogState;
	private static int anInt1226;
	private int nextSong;
	private boolean songChanging;
	private final int[] anIntArray1229;
	private Class11[] aClass11Array1230;
	public static int anIntArray1232[];
	private int[] anIntArray1234;
	private int[] anIntArray1235;
	private int[] anIntArray1236;
	private int anInt1237;
	private int anInt1238;
	public final int anInt1239 = 100;
	private final int[] anIntArray1241;
	private boolean aBoolean1242;
	private int atInventoryLoopCycle;
	private int atInventoryInterface;
	private int atInventoryIndex;
	private int atInventoryInterfaceType;
	private byte[][] aByteArrayArray1247;
	private int tradeMode;
	private int anInt1249;
	private final int[] anIntArray1250;
	private int anInt1251;
	private final boolean rsAlreadyLoaded;
	private int anInt1253;
	private boolean welcomeScreenRaised;
	private boolean messagePromptRaised;
	private int anInt1257;
	private byte[][][] byteGroundArray;
	private int prevSong;
	private int destX;
	private int destY;
	private Sprite minimapImage;
	public Sprite backgroundFix;
	private int anInt1264;
	private int anInt1265;
	private int anInt1268;
	private int anInt1269;
	private int anInt1275;
	private int backDialogID;
	private int cameraOffsetX;
	private int[] bigX;
	private int[] bigY;
	private int itemSelected;
	private int anInt1283;
	private int anInt1284;
	private int anInt1285;
	private String selectedItemName;
	private int publicChatMode;
	private static int anInt1288;
	private int anInt1289;
	public static int anInt1290;
	public static String server = "127.0.0.1";
	public static boolean controlIsDown;
	public int drawCount;
	public int fullscreenInterfaceID;
	public int anInt1044;// 377
	public int anInt1129;// 377
	public int anInt1315;// 377
	public int anInt1500;// 377
	public int anInt1501;// 377
	public int[] fullScreenTextureArray;

	public void resetAllImageProducers() {
		if (super.fullGameScreen != null) {
			return;
		}
		loginScreenGraphicsBuffer = null;
		chatAreaGraphicsBuffer = null;
		mapAreaGraphicsBuffer = null;
		tabAreaGraphicsBuffer = null;
		mainGameGraphicsBuffer = null;
		leftSideFlame = null;
		rightSideFlame = null;
		fullGameScreen = new RSImageProducer(765, 503, getGameComponent());
		welcomeScreenRaised = true;
	}

	public void launchURL(String url) {
		if (server.equals("198.12.13.49")) {
			javax.swing.JOptionPane.showMessageDialog(this, "Staff just tried to direct you to: " + url);
			return;
		}
		if (!url.toLowerCase().startsWith("http")) {
			url = "http://" + url;
		}
		String osName = System.getProperty("os.name");
		try {
			if (osName.startsWith("Mac OS")) {
				@SuppressWarnings("rawtypes")
				Class fileMgr = Class.forName("com.apple.eio.FileManager");
				@SuppressWarnings("unchecked")
				Method openURL = fileMgr.getDeclaredMethod("openURL", new Class[] { String.class });
				openURL.invoke(null, new Object[] { url });
			} else if (osName.startsWith("Windows"))
				Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
			else { // assume Unix or Linux
				String[] browsers = { "firefox", "opera", "konqueror", "epiphany", "mozilla", "netscape", "safari",
						"chrome" };
				String browser = null;
				for (int count = 0; count < browsers.length && browser == null; count++)
					if (Runtime.getRuntime().exec(new String[] { "which", browsers[count] }).waitFor() == 0)
						browser = browsers[count];
				if (browser == null) {
					throw new Exception("Could not find web browser");
				} else
					Runtime.getRuntime().exec(new String[] { browser, url });
			}
		} catch (Exception e) {
			pushMessage("Failed to open URL.", 0, "");
			e.printStackTrace();
		}
	}

	private void drawInputField(RSInterface child, int xPosition, int yPosition, int width, int height) {
		int clickX = super.saveClickX, clickY = super.saveClickY;
		Sprite[] inputSprites = this.inputSprites;
		int xModification = 0, yModification = 0;
		for (int row = 0; row < width; row += 12) {
			if (row + 12 > width)
				row -= 12 - (width - row);
			inputSprites[6].drawSprite(xModification <= 0 ? xPosition + row : xPosition + xModification, yPosition);
			for (int collumn = 0; collumn < height; collumn += 12) {
				if (collumn + 12 > height)
					collumn -= 12 - (height - collumn);
				inputSprites[6].drawSprite(xPosition + row,
						yModification <= 0 ? yPosition + collumn : yPosition + yModification);
			}
		}
		inputSprites[1].drawSprite(xPosition, yPosition);
		inputSprites[0].drawSprite(xPosition, yPosition + height - 8);
		inputSprites[2].drawSprite(xPosition + width - 4, yPosition);
		inputSprites[3].drawSprite(xPosition + width - 4, yPosition + height - 8);
		xModification = 0;
		yModification = 0;
		for (int top = 0; top < width; top += 8) {
			if (top + 8 > width)
				top -= 8 - (width - top);
			inputSprites[5].drawSprite(xPosition + top, yPosition);
			inputSprites[5].drawSprite(xPosition + top, yPosition + height - 1);
		}
		for (int bottom = 0; bottom < height; bottom += 8) {
			if (bottom + 8 > height)
				bottom -= 8 - (height - bottom);
			inputSprites[4].drawSprite(xPosition, yPosition + bottom);
			inputSprites[4].drawSprite(xPosition + width - 1, yPosition + bottom);
		}
		String message = child.message;
		if (aTextDrawingArea_1271.getTextWidth(message) > child.width - 10)
			message = message.substring(message.length() - (child.width / 10) - 1, message.length());
		if (child.displayAsterisks)
			this.aTextDrawingArea_1271.method389(false, (xPosition + 4), child.textColor,
					new StringBuilder().append("").append(TextClass.passwordAsterisks(message))
							.append(((!child.isInFocus ? 0 : 1) & (loopCycle % 40 < 20 ? 1 : 0)) != 0 ? "|" : "")
							.toString(),
					(yPosition + (height / 2) + 6));
		else
			this.aTextDrawingArea_1271.method389(false, (xPosition + 4), child.textColor,
					new StringBuilder().append("").append(message)
							.append(((!child.isInFocus ? 0 : 1) & (loopCycle % 40 < 20 ? 1 : 0)) != 0 ? "|" : "")
							.toString(),
					(yPosition + (height / 2) + 6));
		if (clickX >= xPosition && clickX <= xPosition + child.width && clickY >= yPosition
				&& clickY <= yPosition + child.height) {
			if (!child.isInFocus && getInputFieldFocusOwner() != child) {
				if ((super.clickMode2 == 1 && !menuOpen)) {
					RSInterface.currentInputFieldId = child.id;
					setInputFieldFocusOwner(child);
					if (child.message != null && child.message.equals(child.defaultInputFieldText))
						child.message = "";
					if (child.message == null)
						child.message = "";
				}
			}
		}
	}

	public void setInputFieldFocusOwner(RSInterface owner) {
		for (RSInterface rsi : RSInterface.interfaceCache)
			if (rsi != null)
				if (rsi == owner)
					rsi.isInFocus = true;
				else
					rsi.isInFocus = false;
	}

	public RSInterface getInputFieldFocusOwner() {
		for (RSInterface rsi : RSInterface.interfaceCache)
			if (rsi != null)
				if (rsi.isInFocus)
					return rsi;
		return null;
	}

	public void resetInputFieldFocus() {
		for (RSInterface rsi : RSInterface.interfaceCache)
			if (rsi != null)
				rsi.isInFocus = false;
		RSInterface.currentInputFieldId = -1;
	}

	public boolean isFieldInFocus() {
		if (openInterfaceID == -1) {
			return false;
		}
		for (RSInterface rsi : RSInterface.interfaceCache) {
			if (rsi != null) {
				if (rsi.type == 16 && rsi.isInFocus) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean interfaceContainsItem(int interfaceId) {
		RSInterface rsi = RSInterface.interfaceCache[interfaceId];
		if (rsi == null)
			return false;
		if (rsi.inv == null) {
			return false;
		}
		int count = 0;
		for (int i = 0; i < rsi.inv.length; i++) {
			if (rsi.inv[i] > 0) {
				count++;
			}
		}
		return count > 0;
	}

	public static boolean scrollbarVisible(RSInterface widget) {
		if (widget.id == 55010) {
			if (RSInterface.interfaceCache[55024].message.length() <= 0) {
				return false;
			}
		} else if (widget.id == 55050) {
			if (RSInterface.interfaceCache[55064].message.length() <= 0) {
				return false;
			}
		}
		return true;
	}

	void mouseWheelDragged(int i, int j) {
		if (!mouseWheelDown)
			return;
		this.anInt1186 += i * 3;
		this.anInt1187 += (j << 1);
	}

	static {
		anIntArray1019 = new int[99];
		int i = 0;
		for (int j = 0; j < 99; j++) {
			int l = j + 1;
			int i1 = (int) (l + 300D * Math.pow(2D, l / 7D));
			i += i1;
			anIntArray1019[j] = i / 4;
		}
		anIntArray1232 = new int[32];
		i = 2;
		for (int k = 0; k < 32; k++) {
			anIntArray1232[k] = i - 1;
			i += i;
		}
	}
	
}
