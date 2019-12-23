package com.tarento.dexdata.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;


@Entity
@Table(name = "dataset")
public class DataSet {
	
	@Id
//	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private String id;

	@NotBlank
	@Column(name = "DataSet_name")
	private String dataSetName;
	
	@NotBlank
	@Column(name = "Field_Name")
	private String fieldName;
	
	@NotBlank
	@Column(name = "Data_Type")
	private String dataType;


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDataSetName() {
		return dataSetName;
	}

	public void setDataSetName(String dataSetName) {
		this.dataSetName = dataSetName;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	@Override
	public String toString() {
		return "DataSet [id=" + id + ", dataSetName=" + dataSetName + ", fieldName=" + fieldName + ", dataType="
				+ dataType + "]";
	}
	
}
