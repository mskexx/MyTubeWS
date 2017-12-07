import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;


public class MainServer3 {
	private static InputStreamReader is = new InputStreamReader(System.in);
	private static BufferedReader br = new BufferedReader(is);
	public static void main(String[] args) throws RemoteException, AlreadyBoundException{
		try {
			System.out.print("Introduce Server port:");
			String portNum = (br.readLine()).trim();
			int RMIPortNum = Integer.parseInt(portNum);
			System.out.print("Introduce number of  sv:");
			String name = (br.readLine()).trim();
			int name_sv = Integer.parseInt(name);
	        startRegistry(RMIPortNum);	         
			String registryUrl = "//192.168.1.5:"+RMIPortNum+"/MyTube"+name_sv;
			Server sv = new Server();
			start_BBDD(sv);
			//UnicastRemoteObject.exportObject(sv,RMIPortNum);
			//Naming.bind(registryUrl, sv);
			IServer server = (IServer) Naming.lookup("//192.168.1.5:12345/MyTube1");
			server.addMe(sv, registryUrl);
			
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
	private static void startRegistry(int RMIPortNum) throws RemoteException{
		try {
			Registry registry = LocateRegistry.getRegistry(RMIPortNum);
			registry.list( );  // This call will throw an exception
		}
		catch (RemoteException e) { 
			System.out.println("RMI registry cannot be located at port " + RMIPortNum);
			@SuppressWarnings("unused")
			Registry registry = LocateRegistry.createRegistry(RMIPortNum);
			System.out.println("RMI registry created at port " + RMIPortNum);
		}	
	} 
}
