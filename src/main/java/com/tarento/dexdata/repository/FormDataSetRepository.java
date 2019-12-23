package com.tarento.dexdata.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.tarento.dexdata.entity.DataSet;
import com.tarento.dexdata.entity.FormDataSet;

public interface FormDataSetRepository extends JpaRepository<FormDataSet, Long> {

//	@Query("select d from FormDataSet d where d.data_set_name=?")
//	List<FormDataSet> findByDatasetName(String datasetName);

}
