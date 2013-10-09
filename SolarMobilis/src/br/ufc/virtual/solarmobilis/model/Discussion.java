package br.ufc.virtual.solarmobilis.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.annotations.SerializedName;

public class Discussion {

	@SerializedName("allocation_tag_id")
	private int allocationTagId;
	private String description;
	@SerializedName("end_date")
	private String endDate;
	private int id;
	@SerializedName("last_post_date")
	private String lastPostDate;
	private String name;
	@SerializedName("shedule_id")
	private int sheduleId;
	@SerializedName("start_date")
	private String startDate;
	private String status;

	public Discussion() {

	}

	public Discussion(int allocationTagId, String description, String endDate,
			int id, String lastPostDate, String name, int sheduleId,
			String startDate, String status) {
		super();
		this.allocationTagId = allocationTagId;
		this.description = description;
		this.endDate = endDate;
		this.id = id;
		this.lastPostDate = lastPostDate;
		this.name = name;
		this.sheduleId = sheduleId;
		this.startDate = startDate;
		this.status = status;
	};

	public int getAllocationTagId() {
		return allocationTagId;
	}

	public void setAllocationTagId(int allocationTagId) {
		this.allocationTagId = allocationTagId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getEndDate() {
		SimpleDateFormat simpleFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault());
		try {
			Date date = simpleFormat.parse(endDate);
			SimpleDateFormat startDateFormat = new SimpleDateFormat(
					"dd/MM/yyyy", java.util.Locale.getDefault());
			endDate = startDateFormat.format(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLastPostDate() {
		return lastPostDate;
	}

	public void setLastPostDate(String lastPostDate) {
		this.lastPostDate = lastPostDate;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSheduleId() {
		return sheduleId;
	}

	public void setSheduleId(int sheduleId) {
		this.sheduleId = sheduleId;
	}

	public String getStartDate() {
		SimpleDateFormat simpleFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault());
		try {
			Date date = simpleFormat.parse(startDate);
			SimpleDateFormat startDateFormat = new SimpleDateFormat(
					"dd/MM/yyyy", java.util.Locale.getDefault());
			startDate = startDateFormat.format(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}