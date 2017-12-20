
public class VideoData {
	private String vid_title;
	private String vid_desc;
	private String vid_owner;
	private String vid_url;

	public VideoData(String title, String description, String owner, String url){
		this.vid_title = title;
		this.vid_owner = owner;
		this.vid_desc = description;
		this.vid_url = url;
	}
	public VideoData(){
		
	}

	public String getVid_title() {
		return vid_title;
	}

	public void setVid_title(String vid_title) {
		this.vid_title = vid_title;
	}

	public String getVid_desc() {
		return vid_desc;
	}

	public void setVid_desc(String vid_desc) {
		this.vid_desc = vid_desc;
	}

	public String getVid_owner() {
		return vid_owner;
	}

	public void setVid_owner(String vid_owner) {
		this.vid_owner = vid_owner;
	}

	public String getVid_url() {
		return vid_url;
	}

	public void setVid_url(String vid_url) {
		this.vid_url = vid_url;
	}
	

}
