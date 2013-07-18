package br.ufc.virtual.solarmobilis;

public class User {

	private String password = null;
	private String login = null;

	public User(String password, String login) {
		super();
		this.password = password;
		this.login = login;
	}

	public User() {
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

}
