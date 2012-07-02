package com.mobilis.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "courses")
public class Course {

	public static final String ID_FIELD_NAME = "_id";
	public static final String OFFER_ID_FIELD_NAME = "offer_id";
	public static final String GROUP_ID_FIELD_NAME = "group_id";
	public static final String ALLOCATION_TAG_ID_FIELD_NAME = "allocation_tag_id";
	public static final String SEMESTER_FIELD_NAME = "semester";
	public static final String NAME_FIELD_NAME = "name";

	public Course() {

	}

	@DatabaseField(id = true, columnName = ID_FIELD_NAME)
	private int _id;

	@DatabaseField(columnName = OFFER_ID_FIELD_NAME)
	private int offerId;

	@DatabaseField(columnName = GROUP_ID_FIELD_NAME)
	private int groupId;

	@DatabaseField(columnName = ALLOCATION_TAG_ID_FIELD_NAME)
	private int allocationTagId;

	@DatabaseField(columnName = SEMESTER_FIELD_NAME)
	private String semester;

	@DatabaseField(columnName = NAME_FIELD_NAME)
	private String name;

	public int getOfferId() {
		return offerId;
	}

	public void setOfferId(int offerId) {
		this.offerId = offerId;
	}

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public int getAllocationTagId() {
		return allocationTagId;
	}

	public void setAllocationTagId(int allocationTagId) {
		this.allocationTagId = allocationTagId;
	}

	public String getSemester() {
		return semester;
	}

	public void setSemester(String semester) {
		this.semester = semester;
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
