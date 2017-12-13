package MyTubewsWeb;
public class Client{
	String user;
	String pwd;
	protected Client(String name, String pass){
		user = name;
		pwd = pass;
	}
	public String getUsername(){
		return this.user;
	}
	public String getPassword(){
		return this.pwd;
	}
}