
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
import java.net.ProtocolException;
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
	private Map<String, IClient> uploader = new HashMap<String, IClient>();
	private List<IClient> clients = new ArrayList<IClient>();
	private List<IServer> servers = new ArrayList<IServer>();
	private Map<String, IClient> logins= new HashMap<String, IClient>();
	private int num_server = 0;
	private String url_server;
	private int gen_key = 0;

	protected Server() throws RemoteException {
		super();
	}
	
	@Override
	public int registerUser(IClient user_client) throws RemoteException{
		String name = user_client.getUsername();
		String pwd = user_client.getPassword();
		System.out.println("REGISTERING USER:"+name);
		try {
			URL url = new URL ("http://localhost:8080/MyTubeWebserviceWeb/rest/user");
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
			URL url = new URL ("http://localhost:8080/MyTubeWebserviceWeb/rest/user/"+name);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");
		
			if(conn.getResponseCode() != 200){
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
		for(IServer sv: servers) {
			sv.login(user);
		}
		return 0;
	}
	@Override
	public String globalDownload(String name) throws RemoteException{
		if(!map_directorio.containsKey(name)) {
			return null;
		}
		else{
			return this.url_server;
		}
	}
	
	@Override
	public String getUrl() throws RemoteException {
		try {
			URL url = new URL ("http://localhost:8080/MyTubeWebserviceWeb/rest/user");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Accept", MediaType.APPLICATION_JSON);
		
			if(conn.getResponseCode() != 200)
				return new String();
			
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
	public String get_extensions(String name)throws RemoteException {
		return (file_extensions.get(name));
	}
 
	@Override
	public void updateID(int new_id) throws RemoteException {
		this.gen_key = new_id;
	}
	@Override
	public int upload(String name, byte[] data, String ext, IClient user) throws RemoteException {
		if(map_directorio.containsKey(name)) {
			throw new RuntimeException("Ya existe un video con ese titulo");
		}
		int id_vid = gen_key;
		gen_key +=1;
		
		map_directorio.put(name, id_vid);
		file_extensions.put(name, ext);
		uploader.put(name, user);
	
		try {
			String path = System.getProperty("user.dir") + File.separator + "BBDD" + File.separator +  id_vid;
			System.out.println(path);
			new File(path).mkdir();
			String pathFile = path + File.separator + name + ext;
			FileOutputStream vid_file = new FileOutputStream(pathFile);
			vid_file.write(data);
			vid_file.close();
			
		} catch(Exception e) {
			 e.printStackTrace();
			 return -1;
		}
		notifyNewVideo(name);
		System.out.println("Se ha subido " + name);
		for(IServer sv: servers) {
			sv.updateID(gen_key);
			sv.notifyNewVideo(name);
		}
		return 0;
	}
	@Override
	public ArrayList<String> globalSearch(String description) throws RemoteException {
		ArrayList<String> match = new ArrayList<String>();
		System.out.println("ME HAN MANDADO BUSCAR");
		for (Map.Entry<String, Integer> vid: map_directorio.entrySet()) {
			if(vid.getKey().toLowerCase().contains(description.toLowerCase())){
				System.out.println("Added: "+vid.getValue().toString());
				match.add(vid.getKey());
			}
		}
		System.out.println(match.toString());
		return match;
	}

	@Override
	public ArrayList<String> search(String description) throws RemoteException {
		System.out.println("SEARCHING BY:" + description);
		try {
			URL url = new URL ("http://localhost:8080/MyTubeWebserviceWeb/rest/videos/title/"+description);
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
	
	

	
	public void loadDB(File f) {
		File[] dir = f.listFiles(new FileFilter() {
		    @Override
		    public boolean accept(File file) {
		        return file.isDirectory();
		    }
		});
		for(File x : dir){
			int id = Integer.parseInt(x.getName());
			gen_key = id + 1;
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
		for(IServer sv: servers) {
			sv.disconnect(user);
		}
		return 0;
	}

	@Override
	public boolean modifyTitle(String name, String new_name, IClient user) throws RemoteException {
		if(user.equals(uploader.get(name))){
			if(map_directorio.containsKey(name)) {
				
				map_directorio.put(new_name, map_directorio.get(name));
				map_directorio.remove(name);
				
				file_extensions.put(new_name, file_extensions.get(name));
				file_extensions.remove(name);
				
				uploader.put(new_name, uploader.get(name));
				uploader.remove(name);
				
				Path source = Paths.get(System.getProperty("user.dir") + File.separator + "BBDD" + File.separator
							+  map_directorio.get(new_name) + File.separator + name + file_extensions.get(new_name));
				try {
					Files.move(source, source.resolveSibling(new_name+ file_extensions.get(new_name)));
					return true;
				} catch (IOException e) {
					e.printStackTrace();
				}
			
			}else {
				user.notify("Video not found");
				return false;
			}
		}
		else {
			user.notify("You have no permission to modify this video");
			return false;
		}
		return false;
	}

	@Override
	public int removeVideo(String name, IClient user) throws RemoteException {
		if(user.equals(uploader.get(name))){
			System.out.println("Se va a borrar el video: " + name);
			Path source = Paths.get(System.getProperty("user.dir") + File.separator + "BBDD" + File.separator
					+  map_directorio.get(name));
			Path file_source = Paths.get(System.getProperty("user.dir") + File.separator + "BBDD" + File.separator
					+  map_directorio.get(name) + File.separator + name + file_extensions.get(name));
			try {
				Files.delete(file_source);
			    Files.delete(source);
			    System.out.println("Video borrado: " + name);
			    
				map_directorio.remove(name);
				file_extensions.remove(name);
				uploader.remove(name);
			
			} catch (NoSuchFileException x) {
			    System.err.format("%s: no such" + " file or directory%n", source);
			} catch (DirectoryNotEmptyException x) {
			    System.err.format("%s not empty%n", source);
			} catch (IOException x) {
			    System.err.println(x);
			}
		}
		return 0;
	}
	
	@Override
	public void notifyNewVideo(String name) throws RemoteException{
		for(IClient cl: clients) {
			cl.notify("New video uploaded to the server: "+ name);
		}
	}
	@Override
	public String addMe(IServer sv, String url) throws RemoteException {
		try {
			num_server += 1;
			String url_new = url+"/MyTube"+num_server;
			Naming.bind(url_new, sv);
			sv.setServerList(servers);
			sv.setUrl(url_new);
			for(IServer server_slave: servers) {
				server_slave.addServer(sv);
			}
			servers.add(sv);
			System.out.println("SERVER ADDDDDDDED");
			return url_new;
		} catch (MalformedURLException | RemoteException | AlreadyBoundException e) {
			e.printStackTrace();
			return "ERROR";
		}
	}
	@Override
	public void setServerList(List<IServer> servers) throws RemoteException{
		this.servers = servers;
	}
	
	@Override
	public void addServer(IServer sv) throws RemoteException{
		this.servers.add(sv);
	}

	@Override
	public String getServerDownload(String name) throws RemoteException {
		if(!map_directorio.containsKey(name)) {
			for(IServer s: servers) {
				String temp_url = s.globalDownload(name);
				if(temp_url!= null) {
					return temp_url;
				}
			}
			throw new RuntimeException("No existe video con ese titulo");
		}
		return this.url_server;
	}
}
