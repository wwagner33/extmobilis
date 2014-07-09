package br.ufc.virtual.solarmobilis.model;

public class User {

	@SuppressWarnings("unused")
	private final String grant_type = "password";
	private String password = null;
	private String login = null;

	public User() {
	}

	public User(String password, String login) {
		super();
		this.password = password;
		this.login = login;
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
