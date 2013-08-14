package br.ufc.virtual.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class CurriculumUnitList {

	@SerializedName("curriculum_units")
	private List<CurriculumUnit> curriculumuUnits;

	public List<CurriculumUnit> getCurriculumuUnits() {
		return curriculumuUnits;
	}

	public void setCurriculumuUnits(List<CurriculumUnit> curriculumuUnits) {
		this.curriculumuUnits = curriculumuUnits;
	}

}
