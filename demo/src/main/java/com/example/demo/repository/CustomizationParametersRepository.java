package com.example.demo.repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.CustomizationParameters;
import com.example.demo.projection.CompaniesWithCustomParameterProjection;
import com.example.demo.projection.CompanyLookupProjection;
import com.example.demo.projection.CustomParameterListProjection;

@Repository
public interface CustomizationParametersRepository extends JpaRepository<CustomizationParameters, String> {

	@Query(value = "SELECT XRBNPPUSR.CUSTOMIZATION_PARAMETERS_SEQ.NEXTVAL FROM dual", nativeQuery = true)
	BigInteger getNextId();

	@Query(value = "SELECT cp.code AS code, cp.description AS description, COUNT(DISTINCT cpar.company.id) AS companiesCount, SUM(CASE WHEN cpp.parameterValue "
			+ "IS NOT NULL AND cpp.parameterValue <> cpar.parameterValue THEN 1 ELSE 0 END) AS draftCount FROM CustomizationParameters cp LEFT JOIN CompanyParameters "
			+ "cpar ON cp.code = cpar.customizationParameters.code LEFT JOIN CompanyParametersPreview cpp ON cpp.customizationParameters.code = cpar.customizationParameters.code "
			+ "AND cpp.company.id = cpar.company.id WHERE (:search IS NULL OR LOWER(cp.description) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(cp.code) LIKE "
			+ "LOWER(CONCAT('%', :search, '%'))) GROUP BY cp.code, cp.description HAVING (:status = 'ALL' OR (:status = 'DRAFT' AND SUM(CASE WHEN cpp.parameterValue "
			+ "IS NOT NULL AND cpp.parameterValue <> cpar.parameterValue THEN 1 ELSE 0 END) > 0) OR (:status = 'PUBLISHED' AND SUM(CASE WHEN cpp.parameterValue "
			+ "IS NOT NULL AND cpp.parameterValue <> cpar.parameterValue THEN 1 ELSE 0 END) = 0))",
	       countQuery = "SELECT COUNT(DISTINCT cp.code) FROM CustomizationParameters cp LEFT JOIN CompanyParameters cpar ON cp.code = cpar.customizationParameters.code "
	        + "LEFT JOIN CompanyParametersPreview cpp ON cpp.customizationParameters.code = cpar.customizationParameters.code AND cpp.company.id = cpar.company.id WHERE "
	        + "(:search IS NULL OR LOWER(cp.description) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(cp.code) LIKE LOWER(CONCAT('%', :search, '%'))) GROUP BY cp.code " 
	        + "HAVING (:status = 'ALL' OR (:status = 'DRAFT' AND SUM(CASE WHEN cpp.parameterValue IS NOT NULL AND cpp.parameterValue <> cpar.parameterValue THEN 1 ELSE 0 "
	        + "END) > 0) OR (:status = 'PUBLISHED' AND SUM(CASE WHEN cpp.parameterValue IS NOT NULL AND cpp.parameterValue <> cpar.parameterValue THEN 1 ELSE 0 END) = 0))")
	Page<CustomParameterListProjection> getCustomParameters(@Param("search") String search, @Param("status") String status, Pageable pageable);

	Optional<CustomizationParameters> findById(BigInteger id);

	@Query(value = "SELECT c.idCompany AS companyId, c.nameCompany AS companyName, c.codeCompany AS companyCode, cp.parameterValue AS publishedValue, "
			+ "(SELECT MAX(cpp.parameterValue) FROM CompanyParametersPreview cpp WHERE cpp.company = c AND cpp.customizationParameters.id = :paramId "
			+ "AND cpp.parameterValue <> cp.parameterValue) AS previewValue FROM Company c JOIN CompanyParameters cp ON cp.company = c AND cp.customizationParameters.id = :paramId "
	        + "WHERE (:search IS NULL OR LOWER(c.nameCompany) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(c.codeCompany) LIKE LOWER(CONCAT('%', :search, '%')))",
	       countQuery = "SELECT COUNT(c) FROM Company c JOIN CompanyParameters cp ON cp.company = c AND cp.customizationParameters.id = :paramId WHERE (:search IS NULL "
	       		+ "OR LOWER(c.nameCompany) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(c.codeCompany) LIKE LOWER(CONCAT('%', :search, '%')))")
	Page<CompaniesWithCustomParameterProjection> getCompaniesWithCustomParameter(@Param("paramId") BigInteger paramId, @Param("search") String search, Pageable pageable); 

	@Query(value = "SELECT c.ID_COMPANY AS id, c.NAME_COMPANY AS name, c.CODE_COMPANY AS code FROM XRBNPPUSR.COMPANY c WHERE NOT EXISTS (SELECT 1 "
			+ "FROM XRBNPPUSR.COMPANY_PARAMETERS cp JOIN XRBNPPUSR.CUSTOMIZATION_PARAMETERS p ON p.CODE = cp.PARAMETER_CODE WHERE cp.COMPANY_ID = c.ID_COMPANY "
			+ "AND p.ID = :paramId)", nativeQuery = true)
	List<CompanyLookupProjection> getCustomParametersLookup(@Param("paramId")BigInteger paramId);
}