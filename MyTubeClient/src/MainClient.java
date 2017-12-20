import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

public class MainClient {
	static JFileChooser fileChooser = new JFileChooser();
	
	protected MainClient(){		
	}

	protected static boolean login(IServer server, IClient user) throws RemoteException {
		int status = server.loginUser(user);
		if(status == 0){
			return true;
		}
		return false;
	}
	protected static int register(IServer server, IClient user) throws RemoteException{
		return server.registerUser(user);
	}
	protected static IServer connect(String ip, String port) throws Exception {
		try{
			String registryURL = "rmi://"+ip+":"+port;
			String server_name = "MyTubeMaster";
			System.out.println("_____ SERVERS AVAILABLES ____");
			String[] names = Naming.list(registryURL);
			for(String s: names) {
				s = s.split(port+"/")[1];
				System.out.println(s);
				server_name = s;
			}
			//String server_name = br.readLine();
			IServer server = (IServer) Naming.lookup(registryURL+"/"+server_name);
			return server;
		} catch (IOException e) {
			return null;
		}
	}
	
	protected static boolean removeVideo(IServer server, String title, IClient user) {
		try {
			System.out.println("[DELETE] Server delete: "+ title);
			if(server.removeVideo(title, user)){
				return true;
			}
			return false;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}
	protected static boolean modifyTitle(IServer server, String title, String new_title, IClient user) throws RemoteException {
		try {
			boolean correct = server.modifyTitle(title, new_title, user);
			if(correct) {
				return true;
			}
		} catch (IOException e) {
			return false;
		}
		return false;
	}
	
	protected static File uploadVideo() {
		String path = System.getProperty("user.dir") + File.separator + "Videos";
		File folder = new File(path);
		
		fileChooser.setCurrentDirectory(folder);
		JFrame parent = new JFrame();
		int result = fileChooser.showOpenDialog(parent);

		if (result == JFileChooser.APPROVE_OPTION) {
		    File selectedFile = fileChooser.getSelectedFile();
		    return selectedFile;			
		}
		return null;
	}
	protected static int uploadVideo2(IServer server, File selectedFile, String title, String description, IClient user) {
		try {
			
			System.out.println("Selected file : " + selectedFile.getAbsolutePath());
		    String name = selectedFile.getName();
		    String ext = name.substring(name.indexOf("."));
			byte buffer[] = new byte[(int)selectedFile.length()];
			BufferedInputStream input = new BufferedInputStream(new FileInputStream(selectedFile));
			input.read(buffer,0,buffer.length);
			input.close();
			
			int correct = server.upload(title, description, buffer, ext, user);
			if (correct > 0){
				System.out.println("--> SE HA SUBIDO CORRECTAMENTE ");
				return correct;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	protected static boolean downloadVideo(IServer server, String title) {
		try {
			String sv_url = server.getServerDownload(title);
			if(!sv_url.contains("//")){
				return false;
			}
			System.out.println("rmi:"+sv_url);
			IServer server_download = (IServer) Naming.lookup("rmi:"+sv_url);
			
			byte[] filedata = server_download.download(title);
			String path = System.getProperty("user.dir") + File.separator + "Videos";
			File f = new File(path);
			if(! f.exists()) {
				f.mkdir();
			}
			String ext = server_download.get_extensions(title);
			File file = new File(path + File.separator + title + ext);
			BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(file));
			output.write(filedata,0,filedata.length);
			output.flush();
			output.close();
			return true;
			
		} catch (IOException | NotBoundException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	protected static ArrayList<String> searchVideo(IServer server, String title) throws RemoteException {
		try {
			return server.searchByWord(title);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	protected static ArrayList<String> searchByUsername(IServer server, String user_name) throws RemoteException {
		try {
			return server.searchByUser(user_name);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new ArrayList<String>();
	}
	protected static String searchVideoID(IServer server, String id) throws RemoteException {
		try {
			return server.searchByID(id);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
