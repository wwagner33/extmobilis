package br.ufc.virtual.solarmobilis.model;

import java.util.List;

public class CurriculumUnit {

	private String name;
	private int id;
	private String code;
	private List<Group> groups;

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

	public List<Group> getGroups() {
		return groups;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}

}
