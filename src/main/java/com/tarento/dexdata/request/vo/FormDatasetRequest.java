package com.tarento.dexdata.request.vo;

import java.io.Serializable;


public class FormDatasetRequest implements Serializable {

	private static final long serialVersionUID = -6671491657683632985L;
	
	
	private int formId;
	
	private String dataSetName;
	
	private String fieldName;
	
	private Object dataType;
	
	private Object value;

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

	public Object getDataType() {
		return dataType;
	}

	public void setDataType(Object dataType) {
		this.dataType = dataType;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}


	
}
