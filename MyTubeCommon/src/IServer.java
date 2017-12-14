import java.rmi.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This is a remote interface.
 * @author M.Susin, A.Susin
 */

public interface IServer extends Remote {
	public int login(IClient user) throws RemoteException;
	
	public int loginUser(IClient user_client) throws RemoteException;
	
	public int registerUser(IClient user_client) throws RemoteException;
	
	public byte[] download(String name) throws RemoteException;
	
	public String getUrl() throws RemoteException;
	
	public void setUrl(String url) throws RemoteException;
	
	public String getServerDownload(String name) throws RemoteException;
	
	public String globalDownload(String name) throws RemoteException;
	
	public String get_extensions(String name)throws RemoteException;
	
	public void updateID(int new_id) throws RemoteException;
	
	public int upload(String name, String description, byte[] buffer, String ext, IClient user) throws RemoteException;
	
	public void notifyNewVideo(String name) throws RemoteException;
	
	public boolean modifyTitle(String name, String new_name, IClient user) throws RemoteException;
	
	public int removeVideo(String name, IClient user) throws RemoteException;
	
	public ArrayList<String> searchByWord(String description) throws RemoteException;
	
	public String searchByID(String id) throws RemoteException;
	
	public int disconnect(IClient user) throws RemoteException;
	
	public String addMe(IServer sv, String url) throws RemoteException;

	public void setServerList(List<IServer> servers) throws RemoteException;
	
	public void addServer(IServer sv) throws RemoteException;
}
