package MyTubewsWeb;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import com.google.gson.*;

@RequestScoped
@Path("")
@Produces({ "application/xml", "application/json" })
@Consumes({ "application/xml", "application/json" })
public class WebService {

	public Statement getStatement(){
		try {
			InitialContext cxt = new InitialContext();
			DataSource ds = (DataSource) cxt.lookup("java:/PostgresXADS");
			Connection connection = ds.getConnection();
			Statement statement = connection.createStatement();
			return statement;
				
		} catch (Exception e) {
			System.out.println("Datasource ERROR");
			return null;
		}
	}
	public Connection getConnection(){
		try {
			InitialContext cxt = new InitialContext();
			DataSource ds = (DataSource) cxt.lookup("java:/PostgresXADS");
			Connection connection = ds.getConnection();
			return connection;
				
		} catch (Exception e) {
			System.out.println("Datasource ERROR");
			return null;
		}
	}
	
	//POST Register
	@POST
	@Path("/user")
	public Response registerUser(String user_data){
		try {
			
			Statement st = getStatement();
			String[] u = user_data.split(";");
			System.out.println(u[1]);
			System.out.println(u[0]);
			
			ResultSet rs = st.executeQuery("SELECT username, pwd FROM mytube_users WHERE username='"+u[0]+"';");
			if(rs.isBeforeFirst()){
				return Response.status(201).build();
			}
			Connection con = getConnection();
			PreparedStatement state = con.prepareStatement("INSERT INTO mytube_users(username, pwd) VALUES (?,?)");
			state.setString(1, u[0]);
			state.setString(2, u[1]);
			state.execute();
			return Response.status(200).build();
			
		} catch (SQLException se) {
			return Response.status(500).entity("Database ERROR" + se.toString()).build();
		}
	}

	//GET (Check Login)
	@GET
	@Path("/user/{user}")
	@Produces("application/json")
	public Response getUser(@PathParam("user") String username){
		String msg = "";
		try {
			Statement st = getStatement();
			ResultSet rs = st.executeQuery("SELECT username, pwd FROM mytube_users WHERE username='"+username+"';");
			if(!rs.isBeforeFirst())
				return Response.status(400).entity("USER NOT REGISTERED!").build();
			else{
				System.out.println(rs);
				if(rs.next()){
					msg += rs.getString("username");
					msg += ";"+rs.getString("pwd");
				}
			}
			System.out.println("QUERYYY");
			st.close();
			System.out.println(msg);
			return Response.status(200).entity(msg).build();
			
		} catch (SQLException se) {
			return Response.status(500).entity("DATABASE ERROR!").build();
		}
	}
	
	
	//GET (Search videos)
	@GET
	@Path("/videos/title/{title}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response searchVideosByWord(@PathParam("title") String title){
		try {
			Statement st = getStatement();
			System.out.println("[SEARCH BY WORD] "+title);
			ResultSet rs = st.executeQuery("SELECT title FROM mytube_videos WHERE title LIKE '%"+title+"%' OR description LIKE '%"+title+"%';");
			List<String> videos = new ArrayList<>();
			while(rs.next()){		
				videos.add(rs.getString("title"));
			}
			return Response.status(200).entity(videos).build();
			
		} catch (SQLException se) {
			return Response.status(500).entity("Database ERROR" + se.toString()).build();
		}
	}
	//GET (Search videos)
	@GET
	@Path("/videos/id/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response searchVideosByTitle(@PathParam("id") String id){
		try {
			Statement st = getStatement();
			ResultSet rs = st.executeQuery("SELECT title FROM mytube_videos WHERE id_video="+Integer.parseInt(id));
			if(!rs.isBeforeFirst()){
				return Response.status(401).entity("No video with ID "+ id.toString()).build();
			}else{
				rs.next();
				return Response.status(200).entity(rs.getString("title")).build();
				
			}
		} catch (SQLException se) {
			return Response.status(500).entity("Database ERROR" + se.toString()).build();
		}
	}
	//POST (Upload video)
	@POST
	@Path("/videos")
	@Produces(MediaType.APPLICATION_JSON)
	public Response uploadVideo(VideoData video_data){
		try {
			 String title = video_data.getVid_title();
			 String description = video_data.getVid_desc();
			 String username = video_data.getVid_owner();
			 String url = video_data.getVid_url();
			 int id_server = 0;
			 
			System.out.println("Subiendo video");
			Statement st = getStatement();
			ResultSet rs = st.executeQuery("SELECT title FROM mytube_videos WHERE title='"+title+"';");
			if(rs.isBeforeFirst()){
				return Response.status(201).build(); //Video already exists
			}
			st = getStatement();
			rs = st.executeQuery("SELECT id_server FROM mytube_servers WHERE url_server='"+url+"';");
			if(!rs.isBeforeFirst()){
				System.out.println("No existe ese servidor");
				Connection con = getConnection();
				PreparedStatement state = con.prepareStatement("INSERT INTO mytube_servers(url_server) VALUES (?) RETURNING id_server");
				state.setString(1, url);
				state.execute();
				System.out.println("QUERY HECHA");
				ResultSet rs_return = state.getResultSet();
				rs_return.next();
				id_server = rs_return.getInt("id_server");
				System.out.println("La id del nuevo servidor es:"+ String.valueOf(id_server));
			}
			
			Connection con = getConnection();
			PreparedStatement state = con.prepareStatement("INSERT INTO mytube_videos(title, description, username_owner, id_server) VALUES (?,?,?,?) RETURNING id_video");
			state.setString(1, title);
			state.setString(2, description);
			state.setString(3, username);
			state.setInt(4, id_server);
			state.execute();
			
			ResultSet new_video = state.getResultSet();
			if(!new_video.isBeforeFirst()){
				return Response.status(201).build(); //Video already exists
			}
			new_video.next();
			int id_video = new_video.getInt("id_video");
			System.out.println("La id del nuevo video es:" + String.valueOf(id_video));
			return Response.status(200).entity(String.valueOf(id_video)).build();
			
		} catch (SQLException se) {
			return Response.status(500).entity("Database ERROR" + se.toString()).build();

		}
	}
	
	//GET (Download video)
	@GET
	@Path("/videos/{video}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response donwloadVideo(@PathParam("video") String title){
		try {
			int id_server = 0;
			String url_server;
			Statement st = getStatement();
			ResultSet rs = st.executeQuery("SELECT id_server FROM mytube_videos WHERE title ='"+title+"';");
			
			if(!rs.isBeforeFirst()){
				return Response.status(401).entity("No video with this title "+ title).build();
			}else{
				rs.next();
				id_server = rs.getInt("id_server");
			}
			
			st = getStatement();
			rs = st.executeQuery("SELECT url_server FROM mytube_servers WHERE id_server ="+id_server);
			if(!rs.isBeforeFirst()){
				return Response.status(401).entity("No server with this ID "+ id_server).build();
			}else{
				rs.next();
				url_server = rs.getString("url_server");
			}
			return Response.status(200).entity(url_server).build();
			
		} catch (SQLException se) {
			return Response.status(500).entity("Database ERROR" + se.toString()).build();
		} 
	}
	
	//PUT (Upload video)
	@PUT
	@Path("/videos/{video}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response modifyVideo(@PathParam("video") String title){
		try {
			Statement st = getStatement();
			ResultSet rs = st.executeQuery("SELECT username FROM mytube_users;");
			return Response.status(400).entity("NOT IMPLEMENTED").build();
			
		} catch (SQLException se) {
			return Response.status(500).entity("Database ERROR" + se.toString()).build();
		}
	}
	
	//DELETE (Remove video)
	@DELETE
	@Path("/videos/{video}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteVideo(@PathParam("video") String title){
		try {
			Statement st = getStatement();
			ResultSet rs = st.executeQuery("SELECT username FROM mytube_users;");
			return Response.status(400).entity("NOT IMPLEMENTED").build();
			
		} catch (SQLException se) {
			return Response.status(500).entity("Database ERROR" + se.toString()).build();
		}
	}
	
	//TEST WS
	@Path("/text")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String hello(){
		try {
			Statement st = getStatement();
			ResultSet rs = st.executeQuery("SELECT username FROM mytube_users;");
			return rs.toString();
			
		} catch (SQLException se) {
			return "NOOOPE";
		}
	}
}