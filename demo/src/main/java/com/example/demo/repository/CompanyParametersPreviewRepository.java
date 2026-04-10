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
import com.example.demo.entity.CompanyParametersPreview;
import com.example.demo.entity.CustomizationParameters;

@Repository
public interface CompanyParametersPreviewRepository extends JpaRepository<CompanyParametersPreview, BigInteger>{
	
	@Modifying
    @Transactional
    @Query("DELETE FROM CompanyParametersPreview cp WHERE cp.company.idCompany = :companyId")
	void deleteByCompanyId(@Param("companyId") BigInteger companyId);
	
	@Query("SELECT COUNT(cp) FROM CompanyParametersPreview cp " +
	           "WHERE cp.company = :company AND cp.customizationParameters = :customizationParameters")
	Integer countByCompanyAndCustomizationParameters(@Param("company") Company company, @Param("customizationParameters") CustomizationParameters customizationParameters);
	
	Boolean existsByCompanyAndCustomizationParametersAndParameterValue(Company company, CustomizationParameters customizationParameters, String parameterValue);

	Optional<CompanyParametersPreview> findByCompanyAndCustomizationParametersAndIdNot(Company company,
			CustomizationParameters customizationParameters, BigInteger paramId);
	
	@Modifying
	@Query("DELETE FROM CompanyParametersPreview cpp WHERE cpp.company.idCompany = :companyId AND cpp.customizationParameters = (SELECT "
			+ "p FROM CustomizationParameters p WHERE p.id = :paramId)")
	void deleteByCompanyAndParamId(@Param("companyId") BigInteger companyId,
	                              @Param("paramId") BigInteger paramId);
	
	@Modifying
	@Query("DELETE FROM CompanyParametersPreview cpp WHERE cpp.customizationParameters.id = :paramId")
	void deletePreview(@Param("paramId") BigInteger paramId);
	
	@Modifying
	@Query(value = "INSERT INTO XRBNPPUSR.COMPANY_PARAMETERS_PREVIEW (ID, COMPANY_ID, PARAMETER_CODE, PARAMETER_VALUE) SELECT XRBNPPUSR.COMPANY_PARAMETERS_PREVIEW_SEQ.NEXTVAL, "
			+ "cp.COMPANY_ID, cp.PARAMETER_CODE,cp.PARAMETER_VALUE FROM XRBNPPUSR.COMPANY_PARAMETERS cp JOIN XRBNPPUSR.CUSTOMIZATION_PARAMETERS p "
			+ "ON p.CODE = cp.PARAMETER_CODE WHERE p.ID = :paramId", nativeQuery = true)
	void insertPreview(@Param("paramId") BigInteger paramId);
	
	boolean existsByCompanyAndCustomizationParameters(Company company,
            CustomizationParameters param);
	
	List<CompanyParametersPreview> findByCompanyAndCustomizationParameters(Company company, CustomizationParameters param);
}