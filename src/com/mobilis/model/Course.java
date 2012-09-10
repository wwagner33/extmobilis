package com.mobilis.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "courses")
public class Course {

	public static final String ID_FIELD_NAME = "_id";
	public static final String ALLOCATION_TAG_ID_FIELD_NAME = "allocation_tag_id";
	public static final String NAME_FIELD_NAME = "name";

	public Course() {
	}

	@DatabaseField(id = true, columnName = ID_FIELD_NAME)
	private int _id;

	@DatabaseField(columnName = ALLOCATION_TAG_ID_FIELD_NAME)
	private int allocationTagId;

	@DatabaseField(columnName = NAME_FIELD_NAME)
	private String name;

	public int getAllocationTagId() {
		return allocationTagId;
	}

	public void setAllocationTagId(int allocationTagId) {
		this.allocationTagId = allocationTagId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

}
