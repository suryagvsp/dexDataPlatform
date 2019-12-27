
package com.tarento.dexdata.serviceImp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONTokener;
import org.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.everit.json.schema.ValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.common.hash.Hashing;
import com.tarento.dexdata.controller.DataSetController;
import com.tarento.dexdata.entity.DataSet;
import com.tarento.dexdata.entity.FormDataSet;
import com.tarento.dexdata.repository.DataSetRepository;
import com.tarento.dexdata.repository.FormDataSetRepository;
import com.tarento.dexdata.request.vo.FormDatasetRequest;
import com.tarento.dexdata.service.DataSetService;

@Service
public class DataSetServiceImp implements DataSetService {

	private static final Logger logger = LogManager.getLogger(DataSetServiceImp.class);

	@Autowired
	DataSetRepository dataSetRepository;
	@Autowired
	FormDataSetRepository formDataSetRepository;

	List<DataSet> datasetFromDB = null;
	List<FormDataSet> formDataSetGlobal = null;
	DataSet dbData = null;
	FormDatasetRequest currentValues = null;
	DataSet values = null;

	@SuppressWarnings({ "unchecked", "null" })
	@Override
	public void createForm(List<DataSet> dataset) throws URISyntaxException, Throwable {

		try {
			if (dataset != null || !dataset.isEmpty()) {
				logger.info("dataset is null : ");
				dataset.forEach(eachData -> {
					String hashId = eachData.getDataSetName() + "_" + eachData.getFieldName() + "_"
							+ eachData.getDataType();
					hashId.replaceAll("(\\s)+", "");
					String unqID = Hashing.sha256().hashString(hashId, StandardCharsets.UTF_8).toString();
					eachData.setId(unqID);
					dataSetRepository.save(eachData);
					logger.info("Inserting new form :{}" , eachData);
				});
			} else {
				logger.info("datasetFromDB is not null : ");
				datasetFromDB = (List<DataSet>) dataSetRepository.findbyname(dataset.get(0).getDataSetName());
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(
					"Exception occured in DataSetServiceImp.save(.) method at the time of saving dataSet or fetching dataset by dataSetName : ",
					e.getCause());
			// e.printStackTrace();
		}
		
		Map<String, String> map = new LinkedHashMap<String, String>();
		JSONObject dataArray = new JSONObject();
		List<Object> fieldList = new ArrayList<>(Arrays.asList());

		dataset.forEach(data -> {
			map.put("type", data.getDataType());
			dataArray.put(data.getFieldName(), map);
			fieldList.add(data.getFieldName());
			System.out.println("dataset current data : " + map);
		});
		org.json.simple.JSONObject datasetObject = new org.json.simple.JSONObject();
		datasetObject.put("$schema", "http://json-schema.org/draft-04/schema#");
		datasetObject.put("title", dataset.get(0).getDataSetName());
		datasetObject.put("type", "object");
		datasetObject.put("properties", dataArray);
		datasetObject.put("required", fieldList);

		org.json.simple.JSONArray datasetList = new org.json.simple.JSONArray();
		datasetList.add(datasetObject);
		try (FileWriter file = new FileWriter("schema.json")) {

			String tempStr = datasetList.toJSONString();
			if (StringUtils.isNotBlank(tempStr)) {
				tempStr = tempStr.trim().substring(1, tempStr.length() - 1);
			}
			file.write(tempStr);
			file.flush();

		} catch (JsonGenerationException e) {
			logger.error("Exception occured in DataSetServiceImp.save(.) method at the time of json creation : ",
					e.getCause());
		} catch (JsonMappingException e) {
			logger.error("Exception occured in DataSetServiceImp.save(.) method at the time of json mapping : ",
					e.getCause());
		} catch (IOException e) {
			logger.error("Exception occured in DataSetServiceImp.save(.) method : ", e.getCause());
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void saveValueInDB(List<FormDatasetRequest> formDataSet) {

		try {
			org.json.simple.JSONObject datasetObject = new org.json.simple.JSONObject();
			formDataSet.forEach(data -> {
				currentValues = data;
				datasetObject.put(data.getFieldName(), data.getValue());
			});

			org.json.simple.JSONArray datasetList = new org.json.simple.JSONArray();
			datasetList.add(datasetObject);
			String tempStr = datasetList.toJSONString();
			logger.debug("tempStr  :  " + tempStr);

			try (FileWriter file = new FileWriter("product_invalid.json")) {
				if (StringUtils.isNotBlank(tempStr)) {
					tempStr = tempStr.trim().substring(1, tempStr.length() - 1);
				}
				file.write(tempStr);
				file.flush();
				logger.debug("Json structure Value : " + tempStr);

				String errorMsg = "";
				String dataSetName = "";
				try {
					dataValidateTest(); // Validation exception during sending values

					if (formDataSet != null && !formDataSet.isEmpty())
						dataSetName = formDataSet.get(0).getDataSetName();
					// Retrieve the field data type form DateSet
					List<DataSet> dataSetList = dataSetRepository.findbyname(dataSetName);

					List<FormDataSet> formDatasets = mapper(formDataSet, dataSetList);

					formDataSetRepository.saveAll(formDatasets);
					logger.debug("formDataSetRepository  has saved :");
				} catch (Exception ex) {
					ex.printStackTrace();
					if (ex instanceof ValidationException) {
						errorMsg = ex.getMessage();
						logger.error("Schema is not matching as per defined schema in at the time of form creation : "
								+ errorMsg);
					}
				}
			} catch (JsonGenerationException e1) {
				e1.printStackTrace();
			} catch (JsonMappingException e2) {
				e2.printStackTrace();
			} catch (IOException e3) {
				e3.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private List<FormDataSet> mapper(List<FormDatasetRequest> formDataSet, List<DataSet> dataSetList) throws Exception {
		logger.info("Enter in mapper() method");
		logger.debug("Size   : " + formDataSet.size());
		List<FormDataSet> currentDatas = new LinkedList<>();
		formDataSet.forEach(value -> {
			FormDataSet currentData = new FormDataSet();
			currentData.setFieldName(value.getFieldName());
			currentData.setValue(String.valueOf(value.getValue()));
			currentData.setDataSetName(value.getDataSetName());
			dataSetList.forEach(dataSet -> {
				if (dataSet.getDataSetName() != null && dataSet.getFieldName() != null
						&& dataSet.getDataSetName().equals(currentData.getDataSetName())
						&& dataSet.getFieldName().equalsIgnoreCase(currentData.getFieldName())) {
					currentData.setDataType(String.valueOf(dataSet.getDataType()));
				}
			});
			currentDatas.add(currentData);
			// System.out.println("Printing from Mapper Method : " +
			// currentData.getValue().toString());
		});
		logger.debug("Final list of form data : " + currentDatas);
		return currentDatas;
	}

	public void dataValidateTest() throws Exception {
		try {
			JSONObject jsonSchema = new JSONObject(new JSONTokener(new FileInputStream(new File("./schema.json"))));
			JSONObject jsonSubject = new JSONObject(
					new JSONTokener(new FileInputStream(new File("./product_invalid.json"))));

			Schema schema = SchemaLoader.load(jsonSchema);
			schema.validate(jsonSubject);
		} catch (Exception e) {

			e.printStackTrace();
			throw new Exception(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, List<Map<?, ?>>> CSVfileRead(MultipartFile file, String dataSetName) throws Exception {
		Map<String, List<Map<?, ?>>> finalMap = new HashMap();
		List<Map<?, ?>> success = null;
		List<Map<?, ?>> failure = null;
		try {
			logger.info("Enter in CSVfileRead(.)");

			String files = new String(file.getBytes());
			logger.debug("files   " + files);

			List<String> line = new ArrayList<String>(Arrays.asList(files.split("\n")));
			String[] str = line.get(0).split(",");
//			System.out.println("Line Size : " + line.size());
//			System.out.println(line);

			List<Map<?, ?>> response = new LinkedList<>();
			success = new LinkedList<>();
			failure = new LinkedList<>();
			CsvMapper mapper = new CsvMapper();
			CsvSchema schema = CsvSchema.emptySchema().withHeader();
			MappingIterator<Map<?, ?>> iterator = mapper.reader(Map.class).with(schema).readValues(files);
			List<Map<?, ?>> cSVDatas = iterator.readAll();

			File newFiles = new File("product_invalid.json");
			
			for (Map<?, ?> map : cSVDatas) {
				ObjectMapper newMapper = new ObjectMapper();
				newMapper.writeValue(newFiles, map);
				System.out.println(newFiles);
				System.out.println("newMapper  : "  +newMapper);
				String errorMsg = "";
				try {
					dataValidateTest();
					success.add(map);
				} catch (Exception e) {
					failure.add(map);
					errorMsg = e.getMessage();

					logger.error("Schema is not matching as per data set entered ", e.getCause());
					// throw new Exception(e.getCause());
				}
			}
			int value = 1;

			try {
				List<FormDatasetRequest> formDataSetsReq = new LinkedList<>();
				if (success != null && !success.isEmpty()) {
					success.forEach(formData -> {
						if (formData != null) {
							FormDatasetRequest formDataSetReq = new FormDatasetRequest();
							formDataSetReq.setDataSetName(dataSetName);
							formData.forEach((fieldName, fieldValue) -> {
//								formDataSetReq.setFieldName(fieldName);
								formDataSetReq.setValue(fieldValue);
							});
							formDataSetsReq.add(formDataSetReq);
						}
					});
				}

				List<DataSet> dataSetList = dataSetRepository.findbyname(dataSetName);

				List<FormDataSet> formDatasets = mapper(formDataSetsReq, dataSetList);

				formDataSetRepository.saveAll(formDatasets);
				logger.debug("Csv file has been saved successfully !!");
			} catch (Exception ex) {
				logger.error("Exception occurred at the time of fetching the data");
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finalMap.put("Success", success);
		finalMap.put("Failure", failure);
		return finalMap;
	}

}

	
	
	
	
	
	
	
	
	
/*
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, List<Map<?, ?>>> CSVfileRead(MultipartFile file, String dataSetName) throws Exception {
		Map<String, List<Map<?, ?>>> finalMap = new HashMap();
		List<Map<?, ?>> success = null;
		List<Map<?, ?>> failure = null;
		try {
			logger.info("Enter in CSVfileRead(.)");

			String files = new String(file.getBytes());
			logger.debug("files   " + files);

			List<String> line = new ArrayList<String>(Arrays.asList(files.split("\n")));
			String[] str = line.get(0).split(",");
//			System.out.println("Line Size : " + line.size());
//			System.out.println(line);

			List<Map<String, Object>> response = new LinkedList<>();
			success = new LinkedList<>();
			failure = new LinkedList<>();
			CsvMapper mapper = new CsvMapper();
			CsvSchema schema = CsvSchema.emptySchema().withHeader();
			MappingIterator<Map<String, Object>> iterator = mapper.reader(Map.class).with(schema).readValues(files);
			int value = 1;
			while (iterator.hasNext()) {
				if (value == line.size()) {
					break;
				}
				response.add(iterator.next());
			}
//			System.out.println("Resposne : " + response);

			org.json.simple.JSONObject map = new org.json.simple.JSONObject();
			org.json.simple.JSONArray arrayList = new org.json.simple.JSONArray();

			try (FileWriter jsonFile = new FileWriter("product_invalid.json")) {

				for (Map<String, Object> data : response) {
					arrayList.add(data);
					String tempStr = arrayList.toJSONString();
					if (StringUtils.isNotBlank(tempStr)) {
						tempStr = tempStr.trim().substring(1, tempStr.length() - 1);
					}

					try {
						(jsonFile).write(tempStr);
						(jsonFile).flush();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					String errorMsg = "";
					try {
						dataValidateTest();
						success.add(data);
					} catch (Exception e) {
						failure.add(data);
						errorMsg = e.getMessage();
						logger.error("Schema is not matching as per data set entered ", e.getCause());
						// throw new Exception(e.getCause());

					}
				}

				try {
					List<FormDatasetRequest> formDataSetsReq = new LinkedList<>();
					if (success != null && !success.isEmpty()) {
						success.forEach(formData -> {
							if (formData != null) {
								FormDatasetRequest formDataSetReq = new FormDatasetRequest();
								formDataSetReq.setDataSetName(dataSetName);
								formData.forEach((fieldName, fieldValue) -> {
//									formDataSetReq.setFieldName(fieldName);
									formDataSetReq.setValue(fieldValue);
								});
								formDataSetsReq.add(formDataSetReq);
							}
						});
					}

					List<DataSet> dataSetList = dataSetRepository.findbyname(dataSetName);

					List<FormDataSet> formDatasets = mapper(formDataSetsReq, dataSetList);

					formDataSetRepository.saveAll(formDatasets);
					logger.debug("Csv file has been saved successfully !!");
				} catch (Exception ex) {
					logger.error("Exception occurred at the time of fetching the data");
				}
			} catch (JsonGenerationException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finalMap.put("Success", success);
		finalMap.put("Failure", failure);
		return finalMap;

	}

}
	*/