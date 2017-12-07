import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;


public class MainServer {
	private static InputStreamReader is = new InputStreamReader(System.in);
	private static BufferedReader br = new BufferedReader(is);
	public static void main(String[] args) throws RemoteException, AlreadyBoundException{
		try {
			System.out.print("Introduce IP of the server:");
			String ip = br.readLine();
			System.out.print("Introduce Server port:");
			String portNum = (br.readLine()).trim();
			int RMIPortNum = Integer.parseInt(portNum);
	        boolean master = startRegistry(RMIPortNum);
	        String url = "//"+ip+":"+RMIPortNum;
	        String master_url = url +"/MyTubeMaster";
	        if(master){
	        	System.out.print("I'll be the master of the REGISTRY HUEHUEHUE");
	        	Server sv = new Server();
	        	Naming.bind(master_url, sv);
	        	sv.setUrl(master_url);
	        	start_BBDD(sv);
	        }else {
	        	Server sv = new Server();
	        	IServer server = (IServer) Naming.lookup(master_url);
	        	String myDirection = server.addMe(sv, url);
	        	System.out.println(myDirection);
	        	start_BBDD(sv);
	        }
			System.out.println("Server bound");

		} catch (IOException | NotBoundException e) {
			e.printStackTrace();
		}
	}
	private static void start_BBDD(Server server) {
		File f = new File(System.getProperty("user.dir") + File.separator + "BBDD");
		if(! f.exists()) {
			f.mkdir();
		} else {
			server.loadDB(f);
		}
	}
	private static boolean startRegistry(int RMIPortNum) throws RemoteException{
		try {
			Registry registry = LocateRegistry.getRegistry(RMIPortNum);
			registry.list( );  // This call will throw an exception
			return false;
		}
		catch (RemoteException e) { 
			System.out.println("RMI registry cannot be located at port " + RMIPortNum);
			@SuppressWarnings("unused")
			Registry registry = LocateRegistry.createRegistry(RMIPortNum);
			System.out.println("RMI registry created at port " + RMIPortNum);
			return true;
		}	
	} 
}
