package com.tarento.dexdata.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;


@Entity
@Table(name = "Form_Dataset")
public class FormDataSet {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int formId;
	
	@NotBlank
	@Column(name = "DataSet_Name")
	private String dataSetName;
	
	@NotBlank
	@Column(name = "Field_Name")
	private String fieldName;
	
	@NotBlank
	@Column(name = "Data_Type")
	private String dataType;
	
	@NotBlank
	@Column(name = "Value")
	private String value;

	public int getFormId() {
		return formId;
	}

	public void setFormId(int formId) {
		this.formId = formId;
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

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	

}
