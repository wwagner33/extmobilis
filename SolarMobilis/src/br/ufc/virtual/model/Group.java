package br.ufc.virtual.model;

public class Group {

	private int id;
	private String code;
	private String semester;

	public Group() {

	};

	public Group(int id, String code, String semester) {

		this.id = id;
		this.code = code;
		this.semester = semester;

	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getSemester() {
		return semester;
	}

	public void setSemester(String semester) {
		this.semester = semester;
	}

}
