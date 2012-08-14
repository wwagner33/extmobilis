package com.mobilis.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "classes")
public class Class {

	public static final String ID_FIELD_NAME = "_id";
	public static final String COURSE_ID_FIELD_NAME = "course_id";
	public static final String CODE_FIELD_NAME = "code";
	public static final String SEMESTER_FIELD_NAME = "semester";

	public Class() {
	}

	@DatabaseField(id = true, columnName = ID_FIELD_NAME)
	private int _id;

	@DatabaseField(columnName = COURSE_ID_FIELD_NAME)
	private int courseId;

	@DatabaseField(columnName = CODE_FIELD_NAME)
	private String code;

	@DatabaseField(columnName = SEMESTER_FIELD_NAME)
	private String semester;

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public int getCourseId() {
		return courseId;
	}

	public void setCourseId(int courseId) {
		this.courseId = courseId;
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
