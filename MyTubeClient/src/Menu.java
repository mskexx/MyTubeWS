import java.awt.CardLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.*;
import java.io.File;
import java.rmi.RemoteException;

import javax.swing.*;

public class Menu {

	private JFrame mainFrame;
	private JLabel headerLabel;
	private JLabel statusLabel;
	private JPanel controlPanel;
	private JPanel uploadPanel;
	private JPanel downloadPanel;
	private JPanel searchPanel;
	private JPanel loginPanel;
	private JPanel modifyPanel;
	private JPanel removePanel;
	private JPanel menuPanel;
	private JPanel contentPane;
	
	private JButton registerJButton;
	
	private JButton searchJButton;
	private JButton downJButton;
	private JButton upJButton;
	private JButton modJButton;
	private JButton delJButton;
	private JButton discJButton;
	
	private JButton uploadJButton;
	private JButton examinarJButton;
	private JButton returnJButton;
	private JButton downloadJButton;
	private JButton loginJButton;
	private JButton connectJButton;
	
	private TextField ipText;
	private TextField portText;
	private TextField usernameText;
	private TextField pwdText;
	private TextField downloadTitle;
	private TextField searchTitle;
	
	private File selectedFile;
	
	private Label pwdLabel;
	private Label usernameLabel;
	private Label downloadText;
	private Label searchLabelTitle;
	private Label dir;
	private Label namelabel;
	private Label passwordLabel;
	
	private boolean logged = false;
	private String user;
	private String pwd;
	CardLayout cl = new CardLayout();
	public Menu() {
		doNothing();
	}

	public static void main(String[] args) {
		Menu awtControlDemo = new Menu();
		awtControlDemo.prepareGUI();
	}
	private void doNothing() {
		registerJButton = new JButton("Register");
		
		searchJButton = new JButton("Search");
		downJButton = new JButton("Download");
		upJButton = new JButton("Upload");
		modJButton = new JButton("Modify");
		delJButton = new JButton("Delete");
		discJButton = new JButton("Disconnect");
		
		uploadJButton = new JButton("Upload");
		examinarJButton = new JButton("Browse..");
		returnJButton = new JButton("Return to Menu");
		returnJButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("RETURNING");
				showMenu();
				System.out.println("RETURNED");
			}
		});
		downloadJButton = new JButton("Download");
		loginJButton = new JButton("Login");
		connectJButton = new JButton("Connect");
		connectJButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String data = "Connected " + ipText.getText() + portText.getText();
				try {
					//MainClient.connect(ipText.getText(), portText.getText());
					showMenu();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		
		ipText = new TextField(15);
		portText = new TextField(6);
		usernameText = new TextField(15);
		pwdText = new TextField(10);
		downloadTitle = new TextField(30);
		searchTitle = new TextField(30);
		
		selectedFile = null;
		
		pwdLabel = new Label("Password: ");
		usernameLabel = new Label("Username: ");
		downloadText = new Label("Video Title:");
		searchLabelTitle = new Label("Video Title: ");
		dir = new Label("Directory");
		namelabel = new Label("Registry IP: ");
		passwordLabel = new Label("Port: ");
		
	}
	private void prepareGUI() {
		
		mainFrame = new JFrame("MyTube");
		mainFrame.setSize(250, 300);
		mainFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent windowEvent) {
				System.exit(0);
			}
		});

		headerLabel = new JLabel();
		headerLabel.setSize(300, 50);
		headerLabel.setHorizontalAlignment((int) JFrame.CENTER_ALIGNMENT);
		statusLabel = new JLabel();
		statusLabel.setSize(300, 50);
		statusLabel.setHorizontalAlignment((int) JFrame.CENTER_ALIGNMENT);
		headerLabel.setText("Connection to the server");
		statusLabel.setText("DISCONNECTED");
		statusLabel.setForeground(Color.red);
		controlPanel = new JPanel();
		controlPanel.setLayout(new GridLayout(3,3));


		//controlPanel.setBorder(BorderFactory.createTitledBorder("Connection"));
		controlPanel.add(namelabel);
		controlPanel.add(ipText);
		controlPanel.add(passwordLabel);
		controlPanel.add(portText);
		controlPanel.add(connectJButton);
		

		/*
		mainFrame.add(headerLabel);
		mainFrame.add(controlPanel);
		mainFrame.add(statusLabel);
		mainFrame.setVisible(true);*/
		
		//-----------------------------------------------------------------------
		menuPanel = new JPanel();
		menuPanel.setLayout(new FlowLayout());
		searchJButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showSearch();
			}
		});
		
		downJButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showDownload();
			}
		});
		
		upJButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showUpload();
			}
		});

		modJButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showModify();
			}
		});
		delJButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showDelete();
			}
		});

		discJButton.setBackground(Color.red);
		discJButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		menuPanel.add(downJButton);
		menuPanel.add(upJButton);
		menuPanel.add(searchJButton);
		menuPanel.add(modJButton);
		menuPanel.add(delJButton);
		menuPanel.add(discJButton);
		//-----------------------------------------------------------------------
		uploadPanel = new JPanel();
		uploadPanel.setLayout(new FlowLayout());
		
		uploadJButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(selectedFile.exists()) {
					//MainClient.uploadVideo2(selectedFile);
					statusLabel.setForeground(Color.green);
					statusLabel.setText("VIDEO UPLOADED");
				}
			}
		});
		examinarJButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//File selectedFile = MainClient.uploadVideo();
				dir.setText(selectedFile.getAbsolutePath());
			}
		});



		uploadPanel.add(searchLabelTitle);
		uploadPanel.add(searchTitle);
		uploadPanel.add(dir);
		uploadPanel.add(examinarJButton);
		uploadPanel.add(uploadJButton);
		uploadPanel.add(returnJButton);
		
		
		//-----------------------------------------------------------------------
		downloadPanel = new JPanel();
		downloadPanel.setLayout(new FlowLayout());
		
		downloadJButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//MainClient.downloadVideo(downloadTitle.getText());
				statusLabel.setForeground(Color.green);
				statusLabel.setText("VIDEO DOWNLOADED");
			}
		});

		downloadPanel.add(downloadText);
		downloadPanel.add(downloadTitle);
		downloadPanel.add(returnJButton);
		downloadPanel.add(downloadJButton);
		
		//-----------------------------------------------------------------------
		modifyPanel = new JPanel();
		modifyPanel.setLayout(new FlowLayout());
		
		//-----------------------------------------------------------------------
		removePanel = new JPanel();
		removePanel.setLayout(new FlowLayout());
		
		//-----------------------------------------------------------------------
		searchPanel = new JPanel();
		searchPanel.setLayout(new FlowLayout());
		
		//-----------------------------------------------------------------------
		loginPanel = new JPanel();
		loginPanel.setLayout(new FlowLayout());

		pwdText.setEchoChar('*');
		loginJButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				user = usernameText.getText();
				pwd = pwdText.getText();
				logged = dummyLogin(user,pwd);
				//logged = MainClient.login(user,pwd);
				if(logged) {
					headerLabel.setText("Logged");
					showUpload();
				}
			}
		});


		registerJButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				user = usernameText.getText();
				pwd = pwdText.getText();
				user = usernameText.getText();
				pwd = pwdText.getText();
				//MainClient.register(user, pwd);
				if(dummyRegister(user, pwd)) {
					headerLabel.setText("Logged");
				}
			}
		});

		loginPanel.add(usernameLabel);
		loginPanel.add(usernameText);
		loginPanel.add(pwdLabel);
		loginPanel.add(pwdText);
		loginPanel.add(loginJButton);
		loginPanel.add(registerJButton);
		loginPanel.add(returnJButton);
		
        contentPane = new JPanel();
        contentPane.setBorder(
            BorderFactory.createEmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new CardLayout());
		
        contentPane.add(controlPanel, "controlPanel");
        contentPane.add(menuPanel, "menuPanel");
        contentPane.add(downloadPanel, "downloadPanel");
        contentPane.add(searchPanel, "searchPanel");
        contentPane.add(removePanel, "removePanel");
        contentPane.add(modifyPanel, "modifyPanel");
        contentPane.add(loginPanel, "loginPanel");
        
        mainFrame.setLayout(new GridLayout(3,2));
        mainFrame.getContentPane().add(headerLabel); 
        mainFrame.getContentPane().add(contentPane);       
        mainFrame.pack();   
        mainFrame.setLocationByPlatform(true);
        mainFrame.setVisible(true);
	}

	private void showTextFieldDemo() {
	}

	// ---------------------------------------------------------------------------
	private void showMenu() {
        CardLayout cardLayout = (CardLayout) contentPane.getLayout();
        cardLayout.show(contentPane, "menuPanel");
		//headerLabel.setText("Main Menu");
		//menuPanel.setVisible(true);
		//mainFrame.getContentPane().add(menuPanel);
	}

	// ----------------------------------------------------------------------------
	private void showDownload() {
		headerLabel.setText("Download Menu");
		CardLayout cardLayout = (CardLayout) contentPane.getLayout();
        cardLayout.show(contentPane, "downloadPanel");
	}

	// ----------------------------------------------------------------------------
	private void showUpload() {
		// NEED login
		System.out.println(logged);
		if(!logged) {
			showLogin();
		}else {
			headerLabel.setText("Upload Menu");
			CardLayout cardLayout = (CardLayout) contentPane.getLayout();
	        cardLayout.show(contentPane, "uploadPanel");
		}
	}

	// -----------------------------------------------------------------------
	private void showLogin() {
		headerLabel.setText("USER LOGIN");
		CardLayout cardLayout = (CardLayout) contentPane.getLayout();
        cardLayout.show(contentPane,"loginPanel");
	}
	
	// -----------------------------------------------------------------------
	private void showDelete() {
		headerLabel.setText("Delete Menu");
		CardLayout cardLayout = (CardLayout) contentPane.getLayout();
        cardLayout.show(contentPane, "removePanel");
	}
	// -----------------------------------------------------------------------
	private void showModify() {
		headerLabel.setText("Modify Menu");
		CardLayout cardLayout = (CardLayout) contentPane.getLayout();
        cardLayout.show(contentPane, "modifyPanel");

	}
	// -----------------------------------------------------------------------
	private void showSearch() {
		headerLabel.setText("Search Menu");
		CardLayout cardLayout = (CardLayout) contentPane.getLayout();
        cardLayout.show(contentPane, "searchPanel");
	}
	private boolean dummyLogin(String user_log, String pwd_log) {
		System.out.println(user_log);
		System.out.println(pwd_log);
		return true;
	}
	private boolean dummyRegister(String user_log, String pwd_log) {
		System.out.println(user_log);
		System.out.println(pwd_log);
		return true;
	}
}