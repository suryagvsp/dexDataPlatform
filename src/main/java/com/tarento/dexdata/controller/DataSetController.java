package com.tarento.dexdata.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.util.http.fileupload.FileUtils;

import com.tarento.dexdata.entity.DataSet;
import com.tarento.dexdata.entity.FormDataSet;
import com.tarento.dexdata.repository.DataSetRepository;
import com.tarento.dexdata.request.vo.FormDatasetRequest;
import com.tarento.dexdata.service.DataSetService;

@RestController
@RequestMapping(value = "/dataset")
public class DataSetController {

	@Autowired
	DataSetService dataSetService;

	private static final Logger logger = LogManager.getLogger(DataSetController.class);

	@PostMapping(value = "/create",consumes = "application/json", produces = "application/json")
	public ResponseEntity<List<DataSet>> createDataSet(@RequestBody List<DataSet> dataSet) throws Throwable {
		if (dataSet == null) {
			logger.info("Data Set is Null");
			return new ResponseEntity<List<DataSet>>(HttpStatus.NOT_FOUND);
		}
		logger.info("Data Set is {}", dataSet);
		dataSetService.createForm(dataSet);
		return new ResponseEntity<List<DataSet>>(HttpStatus.CREATED);
	}

	// Here we are saving form values

	@PostMapping(value = "/formData")
	public ResponseEntity<FormDatasetRequest> dataFromFormValues(@RequestBody List<FormDatasetRequest> formDatasetRequest) {
		if (formDatasetRequest == null) {
			logger.info(" From form data is coming Null");
			return new ResponseEntity<FormDatasetRequest>(HttpStatus.NOT_FOUND);
		}

		dataSetService.saveValueInDB(formDatasetRequest);
		logger.info("Form data has saaved {}", formDatasetRequest);
		return new ResponseEntity<FormDatasetRequest>(HttpStatus.CREATED);
	}

	
	@PostMapping("/fileupload")
	public ResponseEntity<?> singleFileUpload(@RequestParam("file") MultipartFile file,
			RedirectAttributes redirectAttributes,@RequestParam String dataSetName) throws Exception {

		if (file.isEmpty()) {
			logger.info("Upload File is Empty");
			redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
			return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
		} 
		if(dataSetName == null) {
			logger.info("DataSet Name is missing!!");
		}
		try {
			Map<String,List<Map<?, ?>>> map = dataSetService.CSVfileRead(file,dataSetName);
			redirectAttributes.addFlashAttribute("message", "File has Upload Successfull");
			return new ResponseEntity<Map<String,List<Map<?, ?>>>>(map,HttpStatus.PARTIAL_CONTENT);
		}catch(Exception ex) {
			return new ResponseEntity<>(HttpStatus.PARTIAL_CONTENT);
		}
		
	}

}
