package com.example.demo.repository;

import java.math.BigInteger;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Company;
import com.example.demo.entity.CompanyParametersPreview;
import com.example.demo.entity.CustomizationParameters;

@Repository
public interface CompanyParametersPreviewRepository extends JpaRepository<CompanyParametersPreview, BigInteger>{
	
	@Modifying
    @Query("DELETE FROM CompanyParametersPreview cp WHERE cp.company.idCompany = :companyId")
	void deleteByCompanyId(@Param("companyId") BigInteger companyId);
	
	@Query("SELECT COUNT(cp) FROM CompanyParametersPreview cp WHERE cp.customizationParameters.code = :parameterCode")
	Integer countByParameterCode(@Param("parameterCode") String parameterCode);
	
	@Modifying
	@Query(value = "DELETE FROM XRBNPPUSR.COMPANY_PARAMETERS_PREVIEW WHERE PARAMETER_CODE = (SELECT CODE FROM XRBNPPUSR.CUSTOMIZATION_PARAMETERS WHERE ID = :paramId)", nativeQuery = true)
	void deletePreview(@Param("paramId") BigInteger paramId);
	
	boolean existsByCompanyAndCustomizationParameters(Company company,
            CustomizationParameters param);
	
	Optional<CompanyParametersPreview> findByCompanyAndCustomizationParameters(Company company, CustomizationParameters param);
	
	@Modifying
	@Query(value = "INSERT INTO XRBNPPUSR.COMPANY_PARAMETERS_PREVIEW (ID, COMPANY_ID, PARAMETER_ID, PARAMETER_VALUE) "
	        + "SELECT XRBNPPUSR.COMPANY_PARAMETERS_PREVIEW_SEQ.NEXTVAL, :targetCompanyId, PARAMETER_ID, PARAMETER_VALUE "
	        + "FROM XRBNPPUSR.COMPANY_PARAMETERS_PREVIEW "
	        + "WHERE COMPANY_ID = :sourceCompanyId",
	        nativeQuery = true)
	void copyCompanyParametersPreview(@Param("sourceCompanyId") BigInteger sourceCompanyId,
	                                  @Param("targetCompanyId") BigInteger targetCompanyId);
}