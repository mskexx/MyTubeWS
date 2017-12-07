import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;


public class Client extends UnicastRemoteObject implements IClient{
    
	private static final long serialVersionUID = -5855005257125566682L;
	String user;
	String pwd;
	
	protected Client(String name, String pass) throws RemoteException {
		super();
		user = name;
		pass = pwd;
	}

	@Override
    public void notify(String msg) throws RemoteException{
        System.out.println(msg);
    }
	
	@Override
	public String getUsername() throws RemoteException {
		return this.user;
	}
	@Override
	public String getPassword() throws RemoteException{
		return this.pwd;
	}
}