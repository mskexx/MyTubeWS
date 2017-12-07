
import java.rmi.*;
public interface IClient extends Remote{
    public void notify(String msg) throws RemoteException;
    public String getUsername() throws RemoteException;
    public String getPassword() throws RemoteException;
}
