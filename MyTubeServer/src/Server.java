
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.MediaType;
import com.google.gson.*;

public class Server extends UnicastRemoteObject implements IServer{

	private static final long serialVersionUID = 1L;
	private Map<String, Integer> map_directorio = new HashMap<String, Integer>();
	private Map<String, String> file_extensions = new HashMap<String, String>();
	private List<IClient> clients = new ArrayList<IClient>();
	private Map<String, IClient> logins= new HashMap<String, IClient>();
	private int num_server = 0;
	private String url_server;
	private String webservice_ip;

	protected Server() throws RemoteException {
		super();
	}
	
	@Override
	public int registerUser(IClient user_client) throws RemoteException{
		String name = user_client.getUsername();
		String pwd = user_client.getPassword();
		System.out.println("REGISTERING USER:"+name);
		try {
			URL url = new URL ("http://"+webservice_ip+":8080/MyTubeWebserviceWeb/rest/user");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            OutputStream os = conn.getOutputStream();
            
            String user_data = name +";"+ pwd.toString();
            os.write(user_data.getBytes());
            os.flush();

            int status = conn.getResponseCode();
            conn.disconnect();
            
			if(status == 200){
				return 0;
			}
			else if(status == 201){
				return 1;
			}
			return -1;

		} catch (IOException e) {
			e.printStackTrace();
		}
		return -1;
	}
	@Override
	public int loginUser(IClient user_client) throws RemoteException{
		String name = user_client.getUsername();
		String pwd = user_client.getPassword();
		System.out.println("LOGIN USER:"+name);
		try {
			URL url = new URL ("http://"+webservice_ip+":8080/MyTubeWebserviceWeb/rest/user/"+name);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");
		
			if(conn.getResponseCode() != 200){
				conn.disconnect();
				System.out.println("No user registered with this name");
				return 1;
			}
			
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String output = br.readLine();
			conn.disconnect();
			String[] u = output.split(";");

			if(pwd.equals(u[1])){
				logins.put(name, user_client);
				return 0;
			}
			return -1;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return -1;
	}
	//TODO
	@Override
	public int login(IClient user) throws RemoteException {
		clients.add(user);
		return 0;
	}
	
	@Override
	public String getUrl() throws RemoteException {
		try {
			URL url = new URL ("http://"+webservice_ip+":8080/MyTubeWebserviceWeb/rest/user");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Accept", MediaType.APPLICATION_JSON);
		
			if(conn.getResponseCode() != 200){
				conn.disconnect();
				return new String();
			}
			
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String output = br.readLine();
			conn.disconnect();
			for(String x: output.split(",")){
				System.out.println(x);
			}
			
		} catch (Exception e) { return "HOLI"; }
		return "HOLI";
		
	}
	@Override 
	public void setUrl(String url) throws RemoteException{
		this.url_server = url;
	}
	
	@Override
	public byte[] download(String name) throws RemoteException {
		try
		{
			System.out.println(map_directorio);
			System.out.println(file_extensions);
			int id_vid = map_directorio.get(name);
			String ext = file_extensions.get(name);
			String path = System.getProperty("user.dir") + File.separator + "BBDD" + File.separator +  id_vid;
			File file = new File(path + File.separator + name + ext);
			byte buffer[] = new byte[(int)file.length()];
			BufferedInputStream input = new BufferedInputStream(new FileInputStream(file));
			input.read(buffer,0,buffer.length);
			input.close();
			return(buffer);
		}
		catch(Exception e)
		{
			System.out.println("FileImpl: "+e.getMessage());
			e.printStackTrace();
			return(null);
		}
	}

	@Override
	public String getServerDownload(String name) throws RemoteException {
		if(!map_directorio.containsKey(name)) {
			try {
				URL url = new URL ("http://"+webservice_ip+":8080/MyTubeWebserviceWeb/rest/video/"+name);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				conn.setRequestProperty("Accept", "application/json");
				
				int code = conn.getResponseCode();
				if(code != 200){
					if(code == 401){
						conn.disconnect();
						return "Not possible to download a video with title: "+name;
					}
					conn.disconnect();
					return "No server for this video";
				}
				
				BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String output = br.readLine();
				conn.disconnect();
				Gson gs = new Gson();
				VideoData vid_data = gs.fromJson(output, VideoData.class);
				String url_server = vid_data.getVid_url();
				String title = vid_data.getVid_title();
				return url_server+";"+title;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return this.url_server;
	}
	@Override
	public String get_extensions(String name)throws RemoteException {
		return (file_extensions.get(name));
	}
 
	@Override
	public int upload(String name, String description, byte[] data, String ext, IClient user) throws RemoteException {
		try {
			URL url = new URL ("http://"+webservice_ip+":8080/MyTubeWebserviceWeb/rest/video");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            OutputStream os = conn.getOutputStream();
            
            VideoData video_data = new VideoData(name, description, user.getUsername(), this.url_server);
            Gson video_gson = new Gson();
            String video = video_gson.toJson(video_data);
            
            os.write(video.getBytes());
            os.flush();

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String id_video = br.readLine();
			System.out.println("SIIIIIII EL NUEVO ID ES"+ id_video);
            
            conn.disconnect();
            
			if(conn.getResponseCode() != 200){
				conn.disconnect();
				return -1;
			}
			
			map_directorio.put(name, Integer.parseInt(id_video));
			file_extensions.put(name, ext);
			
			String path = System.getProperty("user.dir") + File.separator + "BBDD" + File.separator +  Integer.parseInt(id_video);
			System.out.println(path);
			
			new File(path).mkdir();
			String pathFile = path + File.separator + name + ext;
			FileOutputStream vid_file = new FileOutputStream(pathFile);
			vid_file.write(data);
			vid_file.close();
				
			System.out.println("Se ha subido " + name);
			return Integer.parseInt(id_video);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return -1;
	}

	@Override
	public ArrayList<String> searchByWord(String description) throws RemoteException {
		System.out.println("SEARCHING BY:" + description);
		try {
			URL url = new URL ("http://"+webservice_ip+":8080/MyTubeWebserviceWeb/rest/videos/title/"+description);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");
		
			if(conn.getResponseCode() != 200){
				conn.disconnect();
				System.out.println("Error on request");
				return null;
			}
			
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String output = br.readLine();
			conn.disconnect();
			
			Gson gs = new Gson();
			String[] titles = gs.fromJson(output, String[].class);
			ArrayList<String> videos = new ArrayList<>(Arrays.asList(titles));
			System.out.println(output);
			return videos;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public String searchByID(String id) throws RemoteException {
		System.out.println("SEARCHING ID:" + id);
		try {
			URL url = new URL ("http://"+webservice_ip+":8080/MyTubeWebserviceWeb/rest/videos/id/"+id);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");
		
			if(conn.getResponseCode() != 200){
				System.out.println("Error on request");
				return null;
			}
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String output = br.readLine();
			conn.disconnect();
			System.out.println(output);
			return output;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	public void loadDB(File f) {
		File[] dir = f.listFiles(new FileFilter() {
		    @Override
		    public boolean accept(File file) {
		        return file.isDirectory();
		    }
		});
		for(File x : dir){
			int id = Integer.parseInt(x.getName());
			File[ ] vid = x.listFiles();
			if(vid.length > 0) {
				File vid_file = vid[0];
				String name = vid_file.getName();
				String ext = name.substring(name.indexOf("."));
				String title = (vid_file.getName()).replaceFirst("[.][^.]+$", "");
				map_directorio.put(title, id);
				file_extensions.put(title, ext);
			}
		}
		System.out.println(map_directorio);
	}
	

	@Override
	public int disconnect(IClient user) throws RemoteException {
		clients.remove(user);
		return 0;
	}
	
	@Override
	public boolean modifyTitle(String title, String new_title, IClient user) throws RemoteException{
		try {
			URL url = new URL ("http://"+webservice_ip+":8080/MyTubeWebserviceWeb/rest/video/"+title+"/modify");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-Type", "application/json");
            OutputStream os = conn.getOutputStream();
            
            VideoData video_data = new VideoData(new_title, "NO INFO", user.getUsername(), "NO INFO");
            Gson video_gson = new Gson();
            String video = video_gson.toJson(video_data);
            
            os.write(video.getBytes());
            os.flush();
            int status = conn.getResponseCode();
            conn.disconnect();
            if(status != 200){
            	return false;
            }
            int id_video = map_directorio.get(title);
            String path = System.getProperty("user.dir") + File.separator + "BBDD" + File.separator + id_video;
            
            File oldfile = new File(path+File.separator+title+file_extensions.get(title));
            File newfile =  new File(path+File.separator+new_title+file_extensions.get(title));
            if(oldfile.renameTo(newfile)){
            	System.out.println("File renamed in server");
                map_directorio.remove(title);
                map_directorio.put(new_title, id_video);
                
                String ext = file_extensions.get(title);
                file_extensions.remove(title);
                file_extensions.put(new_title, ext);
                return true;
            }
            else{
            	System.out.println("ERROR!! File NOT renamed in server");
            	return false;
            }
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}


	@Override
	public boolean removeVideo(String title, IClient user) throws RemoteException {
			System.out.println("DELETING VIDEO: " + title);
			int id_video = map_directorio.get(title);
			Path source = Paths.get(System.getProperty("user.dir") + File.separator + "BBDD" + File.separator
					+  id_video);
			Path file_source = Paths.get(System.getProperty("user.dir") + File.separator + "BBDD" + File.separator
					+  id_video + File.separator + title + file_extensions.get(title));
			try {
				title = title.replace(" ", "+");
				URL url = new URL ("http://"+webservice_ip+":8080/MyTubeWebserviceWeb/rest/video/"+title+"/remove");
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setDoOutput(true);
	            conn.setRequestMethod("DELETE");
	            conn.setRequestProperty("Content-Type", "application/json");
	            OutputStream os = conn.getOutputStream();
	            
	            VideoData video_data = new VideoData(title, "NO INFO", user.getUsername(), this.url_server);
	            Gson video_gson = new Gson();
	            String video = video_gson.toJson(video_data);
	            
	            os.write(video.getBytes());
	            os.flush();
	            int status = conn.getResponseCode();
	            System.out.println(status);
	            conn.disconnect();
	            if(status != 200){
	            	return false;
	            }
				
				Files.delete(file_source);
			    Files.delete(source);
			    System.out.println("Video borrado: " + title);
			    
				map_directorio.remove(title);
				file_extensions.remove(title);
				return true;
			
			} catch (NoSuchFileException x) {
			    System.err.format("%s: no such" + " file or directory%n", source);
			} catch (DirectoryNotEmptyException x) {
			    System.err.format("%s not empty%n", source);
			} catch (IOException x) {
			    System.err.println(x);
			}
			return false;
		}
	
	@Override
	public String addMe(IServer sv, String url) throws RemoteException {
		try {
			num_server += 1;
			String url_new = url+"/MyTube"+num_server;
			Naming.bind(url_new, sv);
			sv.setUrl(url_new);
			System.out.println("SERVER ADDED");
			return url_new;
		} catch (MalformedURLException | RemoteException | AlreadyBoundException e) {
			e.printStackTrace();
			return "ERROR";
		}
	}
	public void setWebservice(String ip) throws RemoteException{
		this.webservice_ip = ip;
	}
}
