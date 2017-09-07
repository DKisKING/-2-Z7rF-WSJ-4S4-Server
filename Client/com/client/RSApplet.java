package com.client;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import com.client.gameframe.ScreenMode;
import com.client.interfaces.RSInterface;

public class RSApplet extends Applet implements Runnable, MouseListener,
		MouseMotionListener, MouseWheelListener, KeyListener, FocusListener,
		WindowListener {

	private static final long serialVersionUID = 1473917011474991756L;
	public int clickType;
	public final int LEFT = 0;
	public final int RIGHT = 1;
	public final int DRAG = 2;
	public final int RELEASED = 3;
	public final int MOVE = 4;
	boolean mouseWheelDown;
	int mouseWheelX, mouseWheelY;
	
	final void initClientFrame(int clientWidth, int clientHeight) {
		myWidth = clientWidth;
		myHeight = clientHeight;
		graphics = getGameComponent().getGraphics();
		fullGameScreen = new RSImageProducer(myWidth, myHeight, getGameComponent());
		startRunnable(this, 1);
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent event) {
		int rotation = event.getWheelRotation();
		
		if (event.isControlDown()) {
			if (rotation == -1) {
				if (Client.cameraZoom > 1) {
					Client.cameraZoom -= 0.2;
				}
			} else {
				if (Client.cameraZoom < 8) {
					Client.cameraZoom += 0.2;
				}
			}
			return;
		}
		
		handleInterfaceScrolling(event);
		
		if(mouseX > 0 && mouseX < 512 && mouseY > Client.currentGameHeight - 165 && mouseY < Client.currentGameHeight - 25) {
			if(Client.inputDialogState == 3) {
				int scrollPos = Client.grandExchangeSearchScrollPostion;
				int itemSearchAmount = Client.getInstance().grandExchangeItemSearch.getItemSearchResultAmount();
				int maxScrollPosition = itemSearchAmount / 3 * 35;
				scrollPos += rotation * 30;		
				if(scrollPos > maxScrollPosition - 104)
					scrollPos = maxScrollPosition - 104;
				if(scrollPos < 0)
					scrollPos = 0;
				if(Client.grandExchangeSearchScrollPostion != scrollPos) {
					Client.grandExchangeSearchScrollPostion = scrollPos;
					Client.inputTaken = true;
				}
			} else {
				int scrollPos = Client.anInt1089;
				scrollPos -= rotation * 30;		
				if(scrollPos < 0)
					scrollPos = 0;
				if(scrollPos > Client.chatAreaScrollLength - 110)
					scrollPos = Client.chatAreaScrollLength - 110;
				if(Client.anInt1089 != scrollPos) {
					Client.anInt1089 = scrollPos;
					Client.inputTaken = true;
				}
			}
		}
	}

	public void handleInterfaceScrolling(MouseWheelEvent event) {
		int rotation = event.getWheelRotation();
		int positionX = 0;
		int positionY = 0;
		int width = 0;
		int height = 0;
		int offsetX = 0;
		int offsetY = 0;
		int childID = 0;
		/* Tab interface scrolling */
		int tabInterfaceID = Client.tabInterfaceIDs[Client.tabID];
		if (tabInterfaceID != -1) {
			RSInterface tab = RSInterface.interfaceCache[tabInterfaceID];
			offsetX = Client.currentScreenMode.equals(ScreenMode.FIXED) ? 765 - 218 : Client.currentGameWidth - 197;
			offsetY = Client.currentScreenMode.equals(ScreenMode.FIXED) ? 503 - 298 : Client.currentGameHeight - (Client.currentGameWidth >= 960 ? 37 : 74) - 267;
			for (int index = 0; index < tab.children.length; index++) {
				if (RSInterface.interfaceCache[tab.children[index]].scrollMax > 0) {
					childID = index;
					positionX = tab.childX[index];
					positionY = tab.childY[index];
					width = RSInterface.interfaceCache[tab.children[index]].width;
					height = RSInterface.interfaceCache[tab.children[index]].height;
					break;
				}
			}
			if (mouseX > offsetX + positionX && mouseY > offsetY + positionY && mouseX < offsetX + positionX + width && mouseY < offsetY + positionY + height) {
				RSInterface.interfaceCache[tab.children[childID]].scrollPosition += rotation * 30;
				Client.tabAreaAltered = true;
				Client.needDrawTabArea = true;
			}
		}
		/* Main interface scrolling */
		if (Client.openInterfaceID != -1) {
			RSInterface rsi = RSInterface.interfaceCache[Client.openInterfaceID];
			offsetX = Client.currentScreenMode.equals(ScreenMode.FIXED) || Client.centerMainScreenInterface() ? 4 : (Client.currentGameWidth / 2) - 256;
			offsetY = Client.currentScreenMode.equals(ScreenMode.FIXED) || Client.centerMainScreenInterface() ? 4 : (Client.currentGameHeight / 2) - 167;
			for (int index = 0; index < rsi.children.length; index++) {
				if (RSInterface.interfaceCache[rsi.children[index]].scrollMax <= 0) {
					continue;
				}
				if (mouseX > offsetX + rsi.childX[index] && mouseY > offsetY + rsi.childY[index] 
						&& mouseX < offsetX + rsi.childX[index] + RSInterface.interfaceCache[rsi.children[index]].width 
						&& mouseY < offsetY + rsi.childY[index] + RSInterface.interfaceCache[rsi.children[index]].height) {
					RSInterface.interfaceCache[rsi.children[index]].scrollPosition += rotation * 30;
				}
			}
		}
	}

	@Override
	public void run() {
		getGameComponent().addMouseListener(this);
		getGameComponent().addMouseMotionListener(this);
		getGameComponent().addKeyListener(this);
		getGameComponent().addFocusListener(this);
		getGameComponent().addMouseWheelListener(this);
		// drawLoadingText(0, "Loading...");
		startUp();
		int i = 0;
		int j = 256;
		int k = 1;
		int i1 = 0;
		int j1 = 0;
		for (int k1 = 0; k1 < 10; k1++)
			aLongArray7[k1] = System.currentTimeMillis();

		System.currentTimeMillis();
		while (anInt4 >= 0) {
			if (anInt4 > 0) {
				anInt4--;
				if (anInt4 == 0) {
					exit();
					return;
				}
			}
			int i2 = j;
			int j2 = k;
			j = 300;
			k = 1;
			long l1 = System.currentTimeMillis();
			if (aLongArray7[i] == 0L) {
				j = i2;
				k = j2;
			} else if (l1 > aLongArray7[i])
				j = (int) (2560 * delayTime / (l1 - aLongArray7[i]));
			if (j < 25)
				j = 25;
			if (j > 256) {
				j = 256;
				k = (int) (delayTime - (l1 - aLongArray7[i]) / 10L);
			}
			if (k > delayTime)
				k = delayTime;
			aLongArray7[i] = l1;
			i = (i + 1) % 10;
			if (k > 1) {
				for (int k2 = 0; k2 < 10; k2++)
					if (aLongArray7[k2] != 0L)
						aLongArray7[k2] += k;

			}
			if (k < minDelay)
				k = minDelay;
			try {
				Thread.sleep(k);
			} catch (InterruptedException _ex) {
				j1++;
			}
			for (; i1 < 256; i1 += j) {
				clickMode3 = clickMode1;
				saveClickX = clickX;
				saveClickY = clickY;
				aLong29 = clickTime;
				clickMode1 = 0;
				processGameLoop();
				readIndex = writeIndex;
			}

			i1 &= 0xff;
			if (delayTime > 0)
				fps = (1000 * j) / (delayTime * 256);
			processDrawing();
			if (shouldDebug) {
				System.out.println("ntime:" + l1);
				for (int l2 = 0; l2 < 10; l2++) {
					int i3 = ((i - l2 - 1) + 20) % 10;
					System.out.println("otim" + i3 + ":" + aLongArray7[i3]);
				}

				System.out.println("fps:" + fps + " ratio:" + j + " count:"
						+ i1);
				System.out.println("del:" + k + " deltime:" + delayTime
						+ " mindel:" + minDelay);
				System.out.println("intex:" + j1 + " opos:" + i);
				shouldDebug = false;
				j1 = 0;
			}
		}
		if (anInt4 == -1)
			exit();
	}

	private void exit() {
		anInt4 = -2;
		cleanUpForQuit();
		System.exit(0);
	}

	final void method4(int i) {
		delayTime = 1000 / i;
	}

	@Override
	public final void start() {
		if (anInt4 >= 0)
			anInt4 = 0;
	}

	@Override
	public final void stop() {
		if (anInt4 >= 0)
			anInt4 = 4000 / delayTime;
	}

	@Override
	public final void destroy() {
		anInt4 = -1;
		try {
			Thread.sleep(5000L);
		} catch (Exception _ex) {
		}
		if (anInt4 == -1)
			exit();
	}

	@Override
	public final void update(Graphics g) {
		if (graphics == null)
			graphics = g;
		shouldClearScreen = true;
		raiseWelcomeScreen();
	}

	@Override
	public final void paint(Graphics g) {
		if (graphics == null)
			graphics = g;
		shouldClearScreen = true;
		raiseWelcomeScreen();
	}

	@Override
	public final void mousePressed(MouseEvent mouseevent) {
		int i = mouseevent.getX();
		int j = mouseevent.getY();
		idleTime = 0;
		clickX = i;
		clickY = j;
		clickTime = System.currentTimeMillis();
		if(mouseevent.getButton() == 2) {
			mouseWheelDown = true;
			mouseWheelX = i;
			mouseWheelY = j;
			return;
		}
		if (mouseevent.isMetaDown()) {
			clickType = RIGHT;
			clickMode1 = 2;
			clickMode2 = 2;
		} else {
			clickType = LEFT;
			clickMode1 = 1;
			clickMode2 = 1;
		}
	}

	@Override
	public final void mouseReleased(MouseEvent mouseevent) {
		idleTime = 0;
		clickMode2 = 0;
		mouseWheelDown = false;
		clickType = RELEASED;
	}

	@Override
	public final void mouseClicked(MouseEvent mouseevent) {
	}

	@Override
	public final void mouseEntered(MouseEvent mouseevent) {
	}

	@Override
	public final void mouseExited(MouseEvent mouseevent) {
		idleTime = 0;
		mouseX = -1;
		mouseY = -1;
	}

	@Override
	public final void mouseDragged(MouseEvent mouseevent) {
		int i = mouseevent.getX();
		int j = mouseevent.getY();
		if (System.currentTimeMillis() - clickTime >= 250L
				|| Math.abs(saveClickX - i) > 5 || Math.abs(saveClickY - j) > 5) {
			idleTime = 5;
			mouseX = i;
			mouseY = j;
		}
		if (mouseWheelDown) {
            j = mouseWheelX - mouseevent.getX();
            int k = mouseWheelY - mouseevent.getY();
            Client.getInstance().mouseWheelDragged(j, -k);
            mouseWheelX = mouseevent.getX();
            mouseWheelY = mouseevent.getY();
            return;
		}
		clickType = DRAG;
	}

	@Override
	public final void mouseMoved(MouseEvent mouseevent) {
		int i = mouseevent.getX();
		int j = mouseevent.getY();
		if (System.currentTimeMillis() - clickTime >= 250L
				|| Math.abs(saveClickX - i) > 5 || Math.abs(saveClickY - j) > 5) {
			idleTime = 3;
			mouseX = i;
			mouseY = j;
		}
		clickType = MOVE;
	}

	public static boolean hotKeyToggle = true;

	@Override
	public void keyPressed(KeyEvent keyevent) {
		idleTime = 0;
		int i = keyevent.getKeyCode();
		int j = keyevent.getKeyChar();
		
		if (keyevent.isShiftDown()) {
			 Client.shiftDown = true;
		}
		
		if (i == KeyEvent.VK_ESCAPE) {
			Client.closeInterface();
		}
//		if (i == KeyEvent.VK_SPACE) {
//			Client
//		}
		
		if(keyevent.isControlDown()) {
			if (i == KeyEvent.VK_SPACE) {
				Client.continueDialogue();
			}
			if (i == KeyEvent.VK_1 || i == KeyEvent.VK_NUMPAD1) {
				Client.dialogueOptions("one");
			}
			if (i == KeyEvent.VK_2 || i == KeyEvent.VK_NUMPAD2) {
				Client.dialogueOptions("two");
			}
			if (i == KeyEvent.VK_3 || i == KeyEvent.VK_NUMPAD3) {
				Client.dialogueOptions("three");
			}
			if (i == KeyEvent.VK_4 || i == KeyEvent.VK_NUMPAD4) {
				Client.dialogueOptions("four");
			}
			if (i == KeyEvent.VK_5 || i == KeyEvent.VK_NUMPAD5) {
				Client.dialogueOptions("five");
			}
			switch (i) {
			case KeyEvent.VK_V:
			    Client.inputString += Client.getClipboardContents();
			    Client.inputTaken = true;
				break;
				
			case KeyEvent.VK_T:
				Client.teleportInterface();
				break;
				
			case KeyEvent.VK_R:
				Client.cameraZoom = 3.0D;
				break;
			}
		}
		
		if (hotKeyToggle == true) {
			if (i == KeyEvent.VK_F5) {
				Client.setTab(0);
			} else if (i == KeyEvent.VK_F11) {
				Client.setTab(1);
			} else if (i == KeyEvent.VK_F12) {
				Client.setTab(2);
			} else if (i == KeyEvent.VK_F1) {
				Client.setTab(3);
			} else if (i == KeyEvent.VK_F2) {
				Client.setTab(4);
			} else if (i == KeyEvent.VK_F3) {
				Client.setTab(5);
			} else if (i == KeyEvent.VK_F4) {
				Client.setTab(6);
			} else if (i == KeyEvent.VK_F8) {
				Client.setTab(7);
			} else if (i == KeyEvent.VK_F9) {
				Client.setTab(8);
			} else if (i == KeyEvent.VK_F10) {
				Client.setTab(9);
			} else if (i == KeyEvent.VK_F11) {
				Client.setTab(10);
			} else if (i == KeyEvent.VK_F12) {
				Client.setTab(11);
			}
		} else {
			if (i == KeyEvent.VK_F1) {
				Client.setTab(3);
			} else if (i == KeyEvent.VK_F2) {
				Client.setTab(1);
			} else if (i == KeyEvent.VK_F3) {
				Client.setTab(2);
			} else if (i == KeyEvent.VK_F4) {
				Client.setTab(3);
			} else if (i == KeyEvent.VK_F5) {
				Client.setTab(4);
			} else if (i == KeyEvent.VK_F6) {
				Client.setTab(5);
			} else if (i == KeyEvent.VK_F7) {
				Client.setTab(6);
			} else if (i == KeyEvent.VK_F8) {
				Client.setTab(7);
			} else if (i == KeyEvent.VK_F9) {
				Client.setTab(8);
			} else if (i == KeyEvent.VK_F10) {
				Client.setTab(9);
			} else if (i == KeyEvent.VK_F11) {
				Client.setTab(10);
			} else if (i == KeyEvent.VK_F12) {
				Client.setTab(11);
			}
		}
		if(keyevent.isControlDown()){
			Client.controlIsDown = true;
		}
		if (j < 30)
			j = 0;
		if (i == 37)
			j = 1;
		if (i == 39)
			j = 2;
		if (i == 38)
			j = 3;
		if (i == 40)
			j = 4;
		if (i == 17)
			j = 5;
		if (i == 8)
			j = 8;
		if (i == 127)
			j = 8;
		if (i == 9)
			j = 9;
		if (i == 10)
			j = 10;
		if (i >= 112 && i <= 123)
			j = (1008 + i) - 112;
		if (i == 36)
			j = 1000;
		if (i == 35)
			j = 1001;
		if (i == 33)
			j = 1002;
		if (i == 34)
			j = 1003;
		if (j > 0 && j < 128)
			keyArray[j] = 1;
		if (j > 4) {
			charQueue[writeIndex] = j;
			writeIndex = writeIndex + 1 & 0x7f;
		}
	}

	@Override
	public final void keyReleased(KeyEvent keyevent) {
		idleTime = 0;
		int i = keyevent.getKeyCode();
		char c = keyevent.getKeyChar();
		if (c < '\036')
			c = '\0';
		if (i == 37)
			c = '\001';
		if (i == 39)
			c = '\002';
		if (i == 38)
			c = '\003';
		if (i == 40)
			c = '\004';
		if (i == 17)
			c = '\005';
		if (i == 8)
			c = '\b';
		if (i == 127)
			c = '\b';
		if (i == 9)
			c = '\t';
		if (i == 10)
			c = '\n';
		if (i == KeyEvent.VK_SHIFT) {
			Client.shiftDown = false;
		}
		if(i == KeyEvent.VK_CONTROL){
			Client.controlIsDown = false;
		}
		if (c > 0 && c < '\200')
			keyArray[c] = 0;
	}

	@Override
	public final void keyTyped(KeyEvent keyevent) {
	}

	final int readChar(int dummy) {
		while (dummy >= 0) {
			for (int j = 1; j > 0; j++)
				;
		}
		int k = -1;
		if (writeIndex != readIndex) {
			k = charQueue[readIndex];
			readIndex = readIndex + 1 & 0x7f;
		}
		return k;
	}

	@Override
	public final void focusGained(FocusEvent focusevent) {
		awtFocus = true;
		shouldClearScreen = true;
		raiseWelcomeScreen();
	}

	@Override
	public final void focusLost(FocusEvent focusevent) {
		awtFocus = false;
		for (int i = 0; i < 128; i++)
			keyArray[i] = 0;

	}

	@Override
	public final void windowActivated(WindowEvent windowevent) {
	}

	@Override
	public final void windowClosed(WindowEvent windowevent) {
	}

	@Override
	public final void windowClosing(WindowEvent windowevent) {
		destroy();
	}

	@Override
	public final void windowDeactivated(WindowEvent windowevent) {
	}

	@Override
	public final void windowDeiconified(WindowEvent windowevent) {
	}

	@Override
	public final void windowIconified(WindowEvent windowevent) {
	}

	@Override
	public final void windowOpened(WindowEvent windowevent) {
	}

	void startUp() {
	}

	void processGameLoop() {
	}

	void cleanUpForQuit() {
	}

	void processDrawing() {
	}

	void raiseWelcomeScreen() {
	}

	Component getGameComponent() {
		return this;
	}

	public void startRunnable(Runnable runnable, int priority) {
		Thread thread = new Thread(runnable);
		thread.start();
		thread.setPriority(priority);
	}

	void drawLoadingText(int percentage, String s, int downloadSpeed, int secondsRemaining) {
		while (graphics == null) {
			graphics = getGameComponent().getGraphics();
			try {
				getGameComponent().repaint();
			} catch (Exception _ex) {
			}
			try {
				Thread.sleep(1000L);
			} catch (Exception _ex) {
			}
		}
		Font font = new Font("Helvetica", 1, 13);
		FontMetrics fontmetrics = getGameComponent().getFontMetrics(font);
		Font font1 = new Font("Helvetica", 0, 13);
		getGameComponent().getFontMetrics(font1);
		//if (shouldClearScreen) {
			graphics.setColor(Color.black);
			graphics.fillRect(0, 0, myWidth, myHeight);
			shouldClearScreen = false;
		//}
		Color color = new Color(140, 17, 17);
		int j = myHeight / 2 - 18;
		graphics.setColor(color);
		graphics.drawRect(myWidth / 2 - 152, j, 304, 34);
		graphics.fillRect(myWidth / 2 - 150, j + 2, percentage * 3, 30);
		graphics.setColor(Color.black);
		graphics.fillRect((myWidth / 2 - 150) + percentage * 3, j + 2, 300 - percentage * 3, 30);
		graphics.setFont(font);
		graphics.setColor(Color.white);
		graphics.drawString(s, (myWidth - fontmetrics.stringWidth(s)) / 2,
				j + 22);
		
		
		if(downloadSpeed != -1 && secondsRemaining != -1) {
			String text = "Current download speed: " + downloadSpeed + "Kb/s - Seconds remaining: " + secondsRemaining;
			graphics.drawString(text, (myWidth - fontmetrics.stringWidth(text)) / 2, j + 70);
		}
	}

	RSApplet() {
		delayTime = 20;
		minDelay = 1;
		aLongArray7 = new long[10];
		shouldDebug = false;
		shouldClearScreen = true;
		awtFocus = true;
		keyArray = new int[128];
		charQueue = new int[128];
	}

	private int anInt4;
	private int delayTime;
	int minDelay;
	private final long[] aLongArray7;
	int fps;
	boolean shouldDebug;
	int myWidth;
	int myHeight;
	Graphics graphics;
	RSImageProducer fullGameScreen;
	@SuppressWarnings("unused")
	private boolean shouldClearScreen;
	boolean awtFocus;
	int idleTime;
	int clickMode2;
	public int mouseX;
	public int mouseY;
	private int clickMode1;
	protected int clickX;
	protected int clickY;
	private long clickTime;
	int clickMode3;
	int saveClickX;
	int saveClickY;
	long aLong29;
	final int[] keyArray;
	private final int[] charQueue;
	private int readIndex;
	private int writeIndex;
	public static int anInt34;
}
