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

import com.example.demo.entity.Categories;
import com.example.demo.entity.Company;
import com.example.demo.entity.CompanyCategoriesPreview;

@Repository
public interface CompanyCategoriesPreviewRepository extends JpaRepository<CompanyCategoriesPreview, BigInteger>{
	
	@Modifying
    @Transactional
    @Query("DELETE FROM CompanyCategoriesPreview cc WHERE cc.company.idCompany = :companyId")
	void deleteByCompanyId(@Param("companyId") BigInteger companyId);
	
	Optional<CompanyCategoriesPreview> findByCompanyAndCategoryAndIdNot(Company company, Categories category,BigInteger id);
	
	@Query("SELECT ccp FROM CompanyCategoriesPreview ccp WHERE ccp.company.idCompany = :idCompany AND ccp.category.id = :categoryId") 
	Optional<CompanyCategoriesPreview> findByCompanyIdAndCategoryId(@Param("idCompany") BigInteger idCompany, @Param("categoryId") BigInteger categoryId);
	
	@Query("SELECT cc.id AS companyCategoriesId FROM CompanyCategories cc JOIN cc.category c WHERE c.id = :categoryId AND NOT EXISTS (SELECT 1 FROM CompanyCategoriesPreview ccp WHERE ccp.company.id = cc.company.id AND ccp.category.code = c.code)")
	List<BigInteger> findExclusionRuleToPublish(@Param("categoryId") BigInteger categoryId);
	
	@Modifying
	@Query(value = "INSERT INTO XRBNPPUSR.COMPANY_CATEGORIES_PREVIEW (ID, COMPANY_ID, CATEGORY_CODE) SELECT XRBNPPUSR.COMPANY_CATEGORIES_PREVIEW_SEQ.NEXTVAL, c.ID_COMPANY, :categoryCode FROM XRBNPPUSR.COMPANY c", nativeQuery = true)
	void insertCategoriesPreviewForAllCompanies (@Param("categoryCode") String categoryCode);
	
	@Modifying
	@Query(value = "INSERT INTO XRBNPPUSR.COMPANY_CATEGORIES_PREVIEW (ID, COMPANY_ID, CATEGORY_CODE) SELECT XRBNPPUSR.COMPANY_CATEGORIES_PREVIEW_SEQ.NEXTVAL, COMPANY_ID, :newCode FROM XRBNPPUSR.COMPANY_CATEGORIES_PREVIEW WHERE CATEGORY_CODE = :oldCode", nativeQuery = true)
	void cloneCompanyCategoriesPreview(@Param("oldCode") String oldCode,
	                                   @Param("newCode") String newCode);
	
	@Query(value = "SELECT COUNT(*) FROM XRBNPPUSR.COMPANY_CATEGORIES_PREVIEW WHERE COMPANY_ID = :companyId AND CATEGORY_CODE = :categoryCode", nativeQuery = true)
	long existsPreview(@Param("companyId") BigInteger companyId,
	                   @Param("categoryCode") String categoryCode);
	
	@Modifying
	@Query(value = "INSERT INTO XRBNPPUSR.COMPANY_CATEGORIES_PREVIEW (ID, COMPANY_ID, CATEGORY_CODE) VALUES (XRBNPPUSR.COMPANY_CATEGORIES_PREVIEW_SEQ.NEXTVAL, :companyId, :categoryCode)", nativeQuery = true)
	void insertPreview(@Param("companyId") BigInteger companyId,
	                   @Param("categoryCode") String categoryCode);
	
	@Modifying
	@Query(value = "DELETE FROM XRBNPPUSR.COMPANY_CATEGORIES_PREVIEW WHERE COMPANY_ID = :companyId AND CATEGORY_CODE = :categoryCode", nativeQuery = true)
	void deletePreview(@Param("companyId") BigInteger companyId,
	                   @Param("categoryCode") String categoryCode);
	
	@Modifying
	@Query(value = "INSERT INTO XRBNPPUSR.COMPANY_CATEGORIES_PREVIEW (ID, COMPANY_ID, CATEGORY_CODE) SELECT XRBNPPUSR.COMPANY_CATEGORIES_PREVIEW_SEQ.NEXTVAL, :targetCompanyId, CATEGORY_CODE FROM XRBNPPUSR.COMPANY_CATEGORIES_PREVIEW WHERE COMPANY_ID = :sourceCompanyId", nativeQuery = true)
	void copyCompanyCategoriesPreview(@Param("sourceCompanyId") BigInteger sourceCompanyId,
	                                  @Param("targetCompanyId") BigInteger targetCompanyId);
}