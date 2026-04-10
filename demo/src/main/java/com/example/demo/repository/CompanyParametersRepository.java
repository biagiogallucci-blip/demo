package com.example.demo.repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.Company;
import com.example.demo.entity.CompanyParameters;
import com.example.demo.entity.CustomizationParameters;
import com.example.demo.projection.CompanyParameterProjection;

@Repository
public interface CompanyParametersRepository extends JpaRepository<CompanyParameters, BigInteger>{
	
	@Query("SELECT cp FROM CompanyParameters cp where cp.company.codeCompany =:codeCompany")
	List<CompanyParameters> findCompanyParametersByCompanyCode(@Param("codeCompany") String codeCompany);
	
	@Query(value = "WITH cpp_ranked AS (SELECT cpp.COMPANY_ID, cpp.PARAMETER_CODE, cpp.PARAMETER_VALUE, ROW_NUMBER() OVER (PARTITION BY cpp.COMPANY_ID, cpp.PARAMETER_CODE "
			+ "ORDER BY cpp.PARAMETER_VALUE DESC) AS rn FROM XRBNPPUSR.COMPANY_PARAMETERS_PREVIEW cpp) SELECT cp.ID AS id, cp.PARAMETER_CODE AS parameterCode, "
			+ "cpar.DESCRIPTION AS description, cp.PARAMETER_VALUE AS parameterValue, CASE WHEN r.PARAMETER_VALUE IS NOT NULL AND r.PARAMETER_VALUE <> cp.PARAMETER_VALUE "
			+ "THEN r.PARAMETER_VALUE ELSE cp.PARAMETER_VALUE END AS parameterValuePreview FROM XRBNPPUSR.COMPANY_PARAMETERS cp LEFT JOIN cpp_ranked r "
			+ "ON cp.COMPANY_ID = r.COMPANY_ID AND cp.PARAMETER_CODE = r.PARAMETER_CODE AND r.rn = 1 LEFT JOIN XRBNPPUSR.CUSTOMIZATION_PARAMETERS cpar "
			+ "ON cp.PARAMETER_CODE = cpar.CODE WHERE cp.COMPANY_ID = :companyId AND (:search IS NULL OR LOWER(cp.PARAMETER_CODE) LIKE LOWER('%' || :search || '%') "
			+ "OR LOWER(cpar.DESCRIPTION) LIKE LOWER('%' || :search || '%'))", nativeQuery = true)
	List<CompanyParameterProjection> getCompanyCustomParameters(@Param("companyId") BigInteger companyId, @Param("search") String search);

	@Modifying
    @Transactional
    @Query("DELETE FROM CompanyParameters cp WHERE cp.company.idCompany = :companyId")
	void deleteByCompanyId(@Param("companyId") BigInteger companyId);

	@Query("SELECT cp FROM CompanyParameters cp WHERE cp.company.idCompany = :companyId")
	List<CompanyParameters> findParametersByCompanyId(@Param("companyId") BigInteger companyId);

	Optional<CompanyParameters> findByCompanyAndCustomizationParameters(Company company,CustomizationParameters customizationParameters);
	
	@Modifying
	@Query("DELETE FROM CompanyParameters cp WHERE cp.company.idCompany = :companyId AND cp.customizationParameters = (SELECT p FROM "
			+ "CustomizationParameters p WHERE p.id = :paramId)")
	void deleteByCompanyAndParamId(@Param("companyId") BigInteger companyId,
	                              @Param("paramId") BigInteger paramId);
	
	@Modifying
	@Query("UPDATE CompanyParameters cp SET cp.parameterValue = (SELECT cpp.parameterValue FROM CompanyParametersPreview cpp WHERE cpp.company = cp.company "
			+ "AND cpp.customizationParameters.id = :paramId AND cpp.parameterValue <> cp.parameterValue) WHERE cp.customizationParameters.id = :paramId "
			+ "AND EXISTS (SELECT 1 FROM CompanyParametersPreview cpp WHERE cpp.company = cp.company AND cpp.customizationParameters.id = :paramId AND "
			+ "cpp.parameterValue <> cp.parameterValue)")
	void updatePublishedFromDraft(@Param("paramId") BigInteger paramId);
	
	boolean existsByCompanyAndCustomizationParameters(Company company,
            CustomizationParameters param);
}