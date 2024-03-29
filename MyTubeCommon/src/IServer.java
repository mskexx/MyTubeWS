import java.rmi.*;
import java.util.ArrayList;

/**
 * This is a remote interface.
 * @author M.Susin, A.Susin
 */

public interface IServer extends Remote {
	public int loginUser(IClient user_client) throws RemoteException;
	
	public int registerUser(IClient user_client) throws RemoteException;
	
	public byte[] download(String name) throws RemoteException;
	
	public String getUrl() throws RemoteException;
	
	public void setUrl(String url) throws RemoteException;
	
	public String getServerDownload(String name) throws RemoteException;
	
	public String get_extensions(String name)throws RemoteException;
	
	public int upload(String name, String description, byte[] buffer, String ext, IClient user) throws RemoteException;
	
	public boolean modifyTitle(String name, String new_name, IClient user) throws RemoteException;
	
	public boolean removeVideo(String name, IClient user) throws RemoteException;
	
	public ArrayList<String> searchByWord(String description) throws RemoteException;
	
	public ArrayList<String> searchByUser(String user) throws RemoteException;

	public String searchByID(String id) throws RemoteException;
	
	public String addMe(IServer sv, String url) throws RemoteException;

}
