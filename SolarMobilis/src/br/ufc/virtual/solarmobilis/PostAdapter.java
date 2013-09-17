package br.ufc.virtual.solarmobilis;

import android.app.Activity;

public class PostAdapter extends Activity {
	String name, content, date;

	public PostAdapter(String nome, String post, String data) {
		super();
		this.name = nome;
		this.content = post;
		this.date = data;
	}

	public String getNome() {
		return name;
	}

	public void setNome(String nome) {
		this.name = nome;
	}

	public String getPost() {
		return content;
	}

	public void setPost(String post) {
		this.content = post;
	}

	public String getData() {
		return date;
	}

	public void setData(String data) {
		this.date = data;
	}

}