package br.ufc.virtual.model;

public class CurriculumUnit {

	private String name;
	private int id;
	private String code;

	public CurriculumUnit() {

	}

	public CurriculumUnit(String name, int id, String code) {

		this.name = name;
		this.id = id;
		this.code = code;

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getid() {
		return id;
	}

	public void setid(int id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

}
