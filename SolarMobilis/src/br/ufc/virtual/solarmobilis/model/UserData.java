package br.ufc.virtual.solarmobilis.model;

public class UserData {
	
	private int id;
	private String name;
	private String username;
	private String email;
	private String photo;
	
	public UserData(){
	}
	
	public UserData(int id, String name, String username, String email,
			String photo) {
		this.id = id;
		this.name = name;
		this.username = username;
		this.email = email;
		this.photo = photo;
	}

	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getPhoto() {
		return photo;
	}
	
	public void setPhoto(String photo) {
		this.photo = photo;
	}
	
}
