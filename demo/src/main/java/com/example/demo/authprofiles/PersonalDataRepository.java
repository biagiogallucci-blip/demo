package com.example.demo.authprofiles;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonalDataRepository extends JpaRepository<PersonalData, Long> {

	boolean existsByTaxCode(String taxCode);
	boolean existsByEmail(String email);
	List<PersonalData> findByTaxCodeIn(List<String> taxCodes);
}