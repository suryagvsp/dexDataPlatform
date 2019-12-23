package com.tarento.dexdata.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.tarento.dexdata.entity.DataSet;
import com.tarento.dexdata.entity.FormDataSet;
import com.tarento.dexdata.request.vo.FormDatasetRequest;

public interface DataSetService {
  
	public void createForm(List<DataSet> dataSet) throws URISyntaxException, Throwable;

	public void saveValueInDB(List<FormDatasetRequest> formDatasetRequest);

	public Map<String,List<Map<?, ?>>> CSVfileRead(MultipartFile file,String dataSetName) throws Exception;


}
