package com.tarento.dexdata.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tarento.dexdata.entity.DataSet;

@Repository
public interface DataSetRepository extends JpaRepository<DataSet, Long> {
	
	@Query("select d from DataSet d where d.dataSetName = :name")  //
	List<DataSet> findbyname( String name);

//	@Transactional
//	@Modifying
//	@Query("delete from DataSet d where d.dataSetName = :dataSetName")
//	void deleteAll(String dataSetName);
	
	
//	void deleteBooks(@Param("title") String title);
//	 @Query("delete from CLimit l where l.trader.id =:#{#trader.id}")
//	  void deleteLimitsByTrader(@Param("trader") CTrader trader);


//	@Query("update DataSet d set d WHERE d.dataSetName = :dataSetName")
//    void setDatasetName(String dataSetName);

}
