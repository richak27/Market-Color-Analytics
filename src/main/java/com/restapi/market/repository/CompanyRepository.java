package com.restapi.market.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.restapi.market.model.Company;

@Repository
public interface CompanyRepository extends MongoRepository<Company, String> {
	Company findByTicker(String ticker);
	List<Company> findBySector(String sector);

	
}
