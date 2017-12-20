import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.Font;
import javax.swing.SwingConstants;
import java.awt.CardLayout;
import javax.swing.JProgressBar;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.rmi.RemoteException;
import java.util.ArrayList;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;

public class ClientMenu {

	protected JFrame frame;
	private JTextField PortText;
	private JTextField IPText;
	private JTextField searchTitle;
	private JTextField textField;
	private JTextField username_field;
	private JTextField passwordField;
	private JTextField removeTitle;
	private JTextField oldTitle;
	private JTextField newTitle;
	private JTextField downloadTitle;
	private File uploadFile;
	private boolean logged;
	private String user;
	private String pwd;
	private IServer server;
	private IClient user_client;
	private JTextArea textField_desc;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClientMenu window = new ClientMenu();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ClientMenu() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 514, 310);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel footer = new JLabel("NOT CONNECTED");
		footer.setHorizontalAlignment(SwingConstants.CENTER);
		footer.setFont(new Font("Tahoma", Font.BOLD, 17));
		footer.setForeground(Color.RED);
		footer.setBounds(141, 226, 203, 34);
		frame.getContentPane().add(footer);
		
		JLabel lblLoggedAs = new JLabel("Not logged in");
		lblLoggedAs.setBounds(10, 239, 152, 14);
		frame.getContentPane().add(lblLoggedAs);
		
		
		JPanel CARD = new JPanel();
		CARD.setBounds(10, 11, 478, 215);
		frame.getContentPane().add(CARD);
		CARD.setLayout(new CardLayout(0, 0));
		
		JPanel connectionPanel = new JPanel();
		CARD.add(connectionPanel, "connection");
		connectionPanel.setVisible(false);
		connectionPanel.setLayout(null);
		
		IPText = new JTextField();
		IPText.setBounds(161, 30, 159, 20);
		connectionPanel.add(IPText);
		IPText.setColumns(10);
		
		PortText = new JTextField();
		PortText.setBounds(160, 63, 159, 20);
		connectionPanel.add(PortText);
		PortText.setColumns(10);
		
		JLabel lblRegistryIp = new JLabel("Registry IP");
		lblRegistryIp.setBounds(47, 33, 85, 14);
		connectionPanel.add(lblRegistryIp);
		
		JLabel lblRegistryPort = new JLabel("Registry Port");
		lblRegistryPort.setBounds(47, 66, 103, 14);
		connectionPanel.add(lblRegistryPort);

		
		JLabel lblYouNeedTo = new JLabel("You need to log in first!");
		lblYouNeedTo.setBounds(120, 129, 219, 14);
		lblYouNeedTo.setVisible(false);
		lblYouNeedTo.setHorizontalAlignment(SwingConstants.CENTER);
		lblYouNeedTo.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 13));
		
		JPanel MenuPanel = new JPanel();
		MenuPanel.setVisible(false);
		CARD.add(MenuPanel, "Menu");
		MenuPanel.setLayout(null);
		MenuPanel.add(lblYouNeedTo);
		
		JButton btnUpload = new JButton("Upload");
		btnUpload.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				CardLayout cl = (CardLayout)CARD.getLayout();
				if(!logged) {
					lblYouNeedTo.setVisible(true);
				}else {
					cl.show(CARD, "Upload");
				}
			}
		});
		btnUpload.setBounds(357, 31, 111, 43);
		MenuPanel.add(btnUpload);
		
		JButton btnDownload = new JButton("Download");
		btnDownload.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				CardLayout cl = (CardLayout)CARD.getLayout();
				cl.show(CARD, "Download");
			}
		});
		btnDownload.setBounds(10, 138, 111, 48);
		MenuPanel.add(btnDownload);
		
		JButton btnSearch = new JButton("Search");
		btnSearch.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				CardLayout cl = (CardLayout)CARD.getLayout();
				cl.show(CARD, "Search");
			}
		});
		btnSearch.setBounds(10, 31, 111, 43);
		MenuPanel.add(btnSearch);
		
		JButton btnModify = new JButton("Modify");
		btnModify.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				CardLayout cl = (CardLayout)CARD.getLayout();
				if(!logged) {
					lblYouNeedTo.setVisible(true);
				}else {
					cl.show(CARD, "Modify");
				}
			}
		});
		btnModify.setBounds(357, 85, 111, 45);
		MenuPanel.add(btnModify);
		
		JButton btnDelete = new JButton("Delete");
		btnDelete.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				CardLayout cl = (CardLayout)CARD.getLayout();
				if(!logged) {
					lblYouNeedTo.setVisible(true);
				}else {
					cl.show(CARD, "Remove");
				}
			}
		});
		btnDelete.setBounds(357, 141, 111, 43);
		MenuPanel.add(btnDelete);
		
		JButton btnLogin = new JButton("Login");
		btnLogin.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(!logged){
					CardLayout cl = (CardLayout)CARD.getLayout();
					cl.show(CARD, "Login");
				}else{
					lblYouNeedTo.setText("You're already logged");
					lblYouNeedTo.setVisible(true);
				}
			}
		});
		btnLogin.setBounds(187, 96, 89, 23);
		MenuPanel.add(btnLogin);
		
		JLabel lblMenu = new JLabel("MENU");
		lblMenu.setHorizontalAlignment(SwingConstants.CENTER);
		lblMenu.setFont(new Font("Tahoma", Font.BOLD, 18));
		lblMenu.setBounds(142, 18, 177, 63);
		MenuPanel.add(lblMenu);
		
		JPanel LoginPanel = new JPanel();
		CARD.add(LoginPanel, "Login");
		LoginPanel.setLayout(null);
		
		JPanel SearchPanel = new JPanel();
		CARD.add(SearchPanel, "Search");
		SearchPanel.setLayout(null);
		
		searchTitle = new JTextField();
		searchTitle.setBounds(38, 69, 275, 20);
		SearchPanel.add(searchTitle);
		searchTitle.setColumns(10);
		
		JLabel lblSearchAVideo = new JLabel("Introduce title to search");
		lblSearchAVideo.setBounds(101, 44, 185, 14);
		lblSearchAVideo.setFont(new Font("Tahoma", Font.PLAIN, 14));
		SearchPanel.add(lblSearchAVideo);
		lblSearchAVideo.setFont(new Font("Tahoma", Font.PLAIN, 13));
		
		JRadioButton rdbtnByTitle = new JRadioButton("By title");
		rdbtnByTitle.setHorizontalAlignment(SwingConstants.LEFT);
		rdbtnByTitle.setBounds(346, 100, 109, 23);
		rdbtnByTitle.setSelected(true);
		SearchPanel.add(rdbtnByTitle);
		
		JRadioButton rdbtnById = new JRadioButton("By ID");
		rdbtnById.setHorizontalAlignment(SwingConstants.LEFT);
		rdbtnById.setBounds(346, 126, 109, 23);
		SearchPanel.add(rdbtnById);
		
		JRadioButton rdbtnByUser = new JRadioButton("By User");
		rdbtnByUser.setHorizontalAlignment(SwingConstants.LEFT);
		rdbtnByUser.setBounds(346, 152, 109, 23);
		//SearchPanel.add(rdbtnByUser);
		
		ButtonGroup video_group = new ButtonGroup();
		video_group.add(rdbtnById);
		video_group.add(rdbtnByTitle);
		video_group.add(rdbtnByUser);
		
		JLabel lblSearchResults = new JLabel("");
		lblSearchResults.setBounds(38, 100, 275, 104);
		SearchPanel.add(lblSearchResults);
		lblSearchResults.setVerticalAlignment(SwingConstants.TOP);
		lblSearchResults.setHorizontalAlignment(SwingConstants.LEFT);
		
		JButton btnSearch_1 = new JButton("Search");
		btnSearch_1.setBounds(336, 68, 89, 23);
		btnSearch_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				
				try {
					String searched = searchTitle.getText();
					ArrayList<String> results = new ArrayList<>();
					String res_id;
					if(rdbtnById.isSelected()) {
						res_id = MainClient.searchVideoID(server, searched);
						if(res_id != null)
							results.add(res_id);
					}
					else if(rdbtnByTitle.isSelected()) {
						results = MainClient.searchVideo(server, searched);
					}else{
						results = MainClient.searchByUsername(server, searched);
						System.out.println(results);
					}
					String text_titles = "<html>----[RESULTS FOR  "+searched+"]----";
					for(String x: results) {
						text_titles += "<br>    · "+ x;
					}
					text_titles += "</html>";
					lblSearchResults.setText(text_titles);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		});
		SearchPanel.add(btnSearch_1);
		
		JLabel lblSearchMode = new JLabel("SEARCH MODE");
		lblSearchMode.setBounds(164, 1, 115, 32);
		lblSearchMode.setHorizontalAlignment(SwingConstants.CENTER);
		lblSearchMode.setFont(new Font("Tahoma", Font.BOLD, 13));
		SearchPanel.add(lblSearchMode);
		
		JPanel UploadPanel = new JPanel();
		CARD.add(UploadPanel, "Upload");
		UploadPanel.setLayout(null);
		
		textField = new JTextField();
		textField.setBounds(118, 31, 250, 20);
		UploadPanel.add(textField);
		textField.setColumns(10);
		
		JLabel lblTitle = new JLabel("Title");
		lblTitle.setBounds(62, 34, 46, 14);
		UploadPanel.add(lblTitle);
		
		JLabel lblPathUpload = new JLabel("");
		lblPathUpload.setHorizontalAlignment(SwingConstants.CENTER);
		lblPathUpload.setBounds(10, 85, 458, 14);
		UploadPanel.add(lblPathUpload);

		JButton btnSelectFile = new JButton("Select File..");
		btnSelectFile.setBounds(161, 62, 134, 23);
		btnSelectFile.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				uploadFile = MainClient.uploadVideo();
				lblPathUpload.setText(uploadFile.getAbsolutePath());
			}
		});
		
		JLabel lblDesc = new JLabel("Description");
		lblDesc.setBounds(21, 132, 87, 14);
		UploadPanel.add(lblDesc);
		
		JLabel lblUploadCorrecto = new JLabel("");
		lblUploadCorrecto.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblUploadCorrecto.setForeground(Color.GREEN);
		lblUploadCorrecto.setBounds(305, 185, 163, 19);
		UploadPanel.add(lblUploadCorrecto);
		UploadPanel.add(btnSelectFile);
		
		textField_desc = new JTextArea();
		textField_desc.setBounds(107, 109, 250, 61);
		UploadPanel.add(textField_desc);
		textField_desc.setColumns(10);
		
		JButton btnUpload_1 = new JButton("Upload");
		btnUpload_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String title_name = textField.getText();
				String description = textField_desc.getText();
				if((title_name.length() > 0) && (uploadFile != null)) {
					int success = MainClient.uploadVideo2(server, uploadFile, title_name, description, user_client);
					if(success>0){
						lblUploadCorrecto.setText("ID:"+String.valueOf(success)+" --UPLOADED CORRECTLY!");
						//lblUploadCorrecto.setForeground(Color.green);
					
					}else{
						lblUploadCorrecto.setText("UPLOADED ERROR!");
						lblUploadCorrecto.setForeground(Color.RED);
					}
				}
				else {
					lblPathUpload.setForeground(Color.RED);
					lblPathUpload.setText("ERROR! Title or file not selected");
				}
			}
		});
		btnUpload_1.setBounds(173, 181, 122, 23);
		UploadPanel.add(btnUpload_1);
		
		
		JLabel lblUploadMode = new JLabel("UPLOAD MODE");
		lblUploadMode.setFont(new Font("Tahoma", Font.BOLD, 13));
		lblUploadMode.setHorizontalAlignment(SwingConstants.CENTER);
		lblUploadMode.setBounds(179, 0, 116, 25);
		UploadPanel.add(lblUploadMode);
		
		username_field = new JTextField();
		username_field.setBounds(180, 61, 106, 20);
		LoginPanel.add(username_field);
		username_field.setColumns(10);
		
		passwordField = new JTextField();
		passwordField.setBounds(180, 92, 106, 20);
		LoginPanel.add(passwordField);
		
		JLabel lblUsername = new JLabel("Username");
		lblUsername.setBounds(74, 64, 69, 14);
		LoginPanel.add(lblUsername);
		
		JLabel lblPassword = new JLabel("Password");
		lblPassword.setBounds(74, 95, 69, 14);
		LoginPanel.add(lblPassword);
		
		JLabel lblErrorLogin = new JLabel("");
		lblErrorLogin.setHorizontalAlignment(SwingConstants.CENTER);
		lblErrorLogin.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblErrorLogin.setForeground(Color.RED);
		lblErrorLogin.setBounds(166, 124, 131, 20);
		LoginPanel.add(lblErrorLogin);
		
		JButton btnLogin_1 = new JButton("Login");
		btnLogin_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					user = username_field.getText();
					pwd = passwordField.getText();
					user_client = new ClientObj(user, pwd);
					System.out.println(pwd.toString());
					boolean succes = MainClient.login(server, user_client);
					if(succes) {
						lblLoggedAs.setText("Logged as "+user);
						logged = true;
						CardLayout cl = (CardLayout)CARD.getLayout();
						cl.previous(CARD);
						lblYouNeedTo.setVisible(false);
					}else{
						lblErrorLogin.setText("LOGIN ERROR!");
					}
				} catch (RemoteException e1) {
					e1.printStackTrace();
				}
			}
		});
		btnLogin_1.setBounds(341, 61, 89, 51);
		LoginPanel.add(btnLogin_1);
		
		JButton btnRegister_1 = new JButton("Register");
		btnRegister_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					user = username_field.getText();
					pwd = passwordField.getText();
					user_client = new ClientObj(user, pwd);
					int succes = MainClient.register(server, user_client);
					System.out.println(succes);
					if(succes == 0) {
						lblErrorLogin.setForeground(Color.green);
						lblErrorLogin.setText("REGISTER CORRECT!");
					}else if(succes == 1){
						lblErrorLogin.setForeground(Color.red);
						lblErrorLogin.setText("USER ALREADY REGISTERED!");
					}else{
						lblErrorLogin.setForeground(Color.red);
						lblErrorLogin.setText("REGISTER ERROR!");
					}
				} catch (RemoteException e1) {
					e1.printStackTrace();
				}
			}
		});
		btnRegister_1.setBounds(341, 123, 89, 23);
		LoginPanel.add(btnRegister_1);
		
		JLabel lblLoginPanel = new JLabel("LOGIN PANEL");
		lblLoginPanel.setBounds(200, 11, 139, 14);
		LoginPanel.add(lblLoginPanel);
		
		JPanel RemovePanel = new JPanel();
		CARD.add(RemovePanel, "Remove");
		RemovePanel.setLayout(null);
		
		removeTitle = new JTextField();
		removeTitle.setBounds(28, 97, 275, 20);
		RemovePanel.add(removeTitle);
		removeTitle.setColumns(10);
		
		JLabel lblRemoveMode = new JLabel("DELETE MODE");
		lblRemoveMode.setFont(new Font("Tahoma", Font.BOLD, 13));
		lblRemoveMode.setHorizontalAlignment(SwingConstants.CENTER);
		lblRemoveMode.setBounds(184, 11, 119, 34);
		RemovePanel.add(lblRemoveMode);
		
		JLabel removeOK = new JLabel("");
		removeOK.setHorizontalAlignment(SwingConstants.CENTER);
		removeOK.setFont(new Font("Tahoma", Font.BOLD, 11));
		removeOK.setForeground(Color.GREEN);
		removeOK.setBounds(331, 130, 89, 20);
		RemovePanel.add(removeOK);
		
		JButton btnRemove = new JButton("Delete");
		btnRemove.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(MainClient.removeVideo(server, removeTitle.getText(), user_client)) {
					removeOK.setText("Video deleted");
				}else {
					removeOK.setText("ERROR, video NOT deleted correctly");
				}
			}
		});
		btnRemove.setBounds(331, 96, 89, 23);
		RemovePanel.add(btnRemove);
		
		JLabel lblIntroduceVideoTitle = new JLabel("Introduce video title to remove");
		lblIntroduceVideoTitle.setBounds(78, 72, 211, 14);
		RemovePanel.add(lblIntroduceVideoTitle);

		
		JPanel ModifyPanel = new JPanel();
		CARD.add(ModifyPanel, "Modify");
		ModifyPanel.setLayout(null);
		
		JLabel lblModified = new JLabel("");
		lblModified.setForeground(Color.GREEN);
		lblModified.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblModified.setHorizontalAlignment(SwingConstants.CENTER);
		lblModified.setBounds(42, 154, 227, 32);
		ModifyPanel.add(lblModified);
		
		oldTitle = new JTextField();
		oldTitle.setBounds(32, 54, 248, 20);
		ModifyPanel.add(oldTitle);
		oldTitle.setColumns(10);
		
		newTitle = new JTextField();
		newTitle.setBounds(32, 123, 248, 20);
		ModifyPanel.add(newTitle);
		newTitle.setColumns(10);
		
		JLabel lblOldTitle = new JLabel("Title");
		lblOldTitle.setBounds(130, 29, 73, 14);
		ModifyPanel.add(lblOldTitle);
		
		JLabel lblNewTitle = new JLabel("New Title");
		lblNewTitle.setBounds(130, 98, 89, 14);
		ModifyPanel.add(lblNewTitle);
		
		JButton btnModify_1 = new JButton("Modify");
		btnModify_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					String old_t = oldTitle.getText();
					String new_t = newTitle.getText();
					if(MainClient.modifyTitle(server, old_t, new_t, user_client)){
						lblModified.setText("Video title modified");
					}else {
						lblModified.setText("ERROR! Video title NOT modified");
					}
				} catch (RemoteException e1) {
					e1.printStackTrace();
				}
			}
		});
		btnModify_1.setBounds(336, 68, 89, 58);
		ModifyPanel.add(btnModify_1);
		
		JLabel lblModifyMode = new JLabel("MODIFY MODE");
		lblModifyMode.setFont(new Font("Tahoma", Font.BOLD, 13));
		lblModifyMode.setHorizontalAlignment(SwingConstants.CENTER);
		lblModifyMode.setBounds(323, 0, 111, 46);
		ModifyPanel.add(lblModifyMode);
		

		
		JPanel DownloadPanel = new JPanel();
		CARD.add(DownloadPanel, "Download");
		DownloadPanel.setLayout(null);
		
		downloadTitle = new JTextField();
		downloadTitle.setBounds(37, 36, 261, 20);
		DownloadPanel.add(downloadTitle);
		downloadTitle.setColumns(10);

		JProgressBar progressBar = new JProgressBar();
		progressBar.setMaximum(10);
		progressBar.setBounds(89, 67, 146, 14);


		
		DownloadPanel.add(progressBar);
		
		JLabel downloadLabel = new JLabel("");
		downloadLabel.setHorizontalAlignment(SwingConstants.CENTER);
		downloadLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
		downloadLabel.setForeground(Color.GREEN);
		downloadLabel.setBounds(37, 93, 261, 25);
		DownloadPanel.add(downloadLabel);
		
		JButton btnDownload_1 = new JButton("Download");
		btnDownload_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				boolean success = MainClient.downloadVideo(server, downloadTitle.getText());
				if(success){
					progressBar.setForeground(Color.GREEN);
					progressBar.setValue(10);
					downloadLabel.setForeground(Color.GREEN);
					downloadLabel.setText("· Video downloaded ·");
				}else{
					downloadLabel.setForeground(Color.red);
					downloadLabel.setText("· Download ERROR ·");
				}
				
			}
		});
		btnDownload_1.setBounds(321, 35, 132, 23);
		DownloadPanel.add(btnDownload_1);
		
		JLabel lblDownloadMode = new JLabel("DOWNLOAD MODE");
		lblDownloadMode.setHorizontalAlignment(SwingConstants.CENTER);
		lblDownloadMode.setFont(new Font("Tahoma", Font.BOLD, 13));
		lblDownloadMode.setBounds(89, 0, 158, 25);
		DownloadPanel.add(lblDownloadMode);
		
		
		JButton btnGoToMenu = new JButton("Go to Menu");
		btnGoToMenu.setVisible(false);
		btnGoToMenu.setOpaque(false);
		btnGoToMenu.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				removeOK.setText("");
				progressBar.setBackground(Color.red);
				progressBar.setValue(0);
				downloadLabel.setText("");
				lblModified.setText("");
				lblPathUpload.setForeground(Color.black);
				lblPathUpload.setText("");
				lblSearchResults.setText("");
				lblYouNeedTo.setVisible(false);
				lblErrorLogin.setText("");
				lblUploadCorrecto.setText("");
				CardLayout cl = (CardLayout)CARD.getLayout();
				cl.show(CARD, "Menu");
			}
		});
		btnGoToMenu.setBounds(354, 235, 121, 23);
		frame.getContentPane().add(btnGoToMenu);
		
		JButton btnNewButton = new JButton("Connect");
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				try {
					String ip = IPText.getText();
					String port = PortText.getText();
					System.out.print(ip);
					server = MainClient.connect(ip, port);
					if(server != null) {
						footer.setText("CONNECTED");
						footer.setForeground(Color.GREEN);
						btnGoToMenu.setVisible(true);
						btnGoToMenu.setOpaque(true);
						CardLayout cl = (CardLayout)CARD.getLayout();
						cl.show(CARD, "Menu");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		});
		btnNewButton.setBounds(170, 94, 124, 47);
		connectionPanel.add(btnNewButton);
	}
}
