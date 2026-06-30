package com.example.demo.authprofiles;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ConventionRepository extends JpaRepository<Convention, Long>{
	
	@Modifying
	@Query("DELETE FROM Convention c WHERE c.personalData.id IN :ids")
	void deleteByPersonalDataIds(@Param("ids") Set<Long> ids);
}