package com.example.demo.repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.Categories;
import com.example.demo.entity.Company;
import com.example.demo.entity.CompanyCategories;
import com.example.demo.projection.CompaniesByExclusionRuleProjection;
import com.example.demo.projection.CompanyExclusionRulesProjection;

@Repository
public interface CompanyCategoriesRepository extends JpaRepository<CompanyCategories, BigInteger> {

	@Query("SELECT cc.category.code FROM CompanyCategories cc where "
			+ "cc.company.codeCompany =:companyCode AND cc.category.active = 'Y' ")
	List<String> findActiveCategoryCodesByCompanyCode(@Param("companyCode") String companyCode);

	@Query(value = "SELECT c.id AS id, c.code AS tag, c.name AS name, CASE WHEN cc.company_id IS NOT NULL THEN 1 ELSE 0 END AS isEnabled, "
			+ "CASE WHEN (cc.company_id IS NULL AND cp.company_id IS NOT NULL) OR (cc.company_id IS NOT NULL AND cp.company_id IS NULL) THEN 1 ELSE 0 "
			+ "END AS hasDraft, CASE WHEN cc.company_id IS NOT NULL AND cp.company_id IS NULL THEN 0 WHEN cc.company_id IS NULL AND cp.company_id "
			+ "IS NOT NULL THEN 1 ELSE NULL END AS draftIsEnabled, CASE WHEN (cc.company_id IS NULL AND cp.company_id IS NOT NULL) OR "
			+ "(cc.company_id IS NOT NULL AND cp.company_id IS NULL) THEN 'PENDING_PUBLICATION' ELSE 'PUBLISHED' END AS status FROM "
			+ "XRBNPPUSR.CATEGORIES c LEFT JOIN XRBNPPUSR.COMPANY_CATEGORIES cc ON cc.category_code = c.code AND cc.company_id = :idCompany "
			+ "LEFT JOIN XRBNPPUSR.COMPANY_CATEGORIES_PREVIEW cp ON cp.category_code = c.code AND cp.company_id = :idCompany WHERE (:search IS NULL "
			+ "OR :search = '' OR LOWER(c.code) LIKE LOWER('%' || :search || '%') OR LOWER(c.name) LIKE LOWER('%' || :search || '%')) "
			+ "ORDER BY c.name", nativeQuery = true)
	List<CompanyExclusionRulesProjection> getCompanyExclusionRules(@Param("idCompany") BigInteger companyId, @Param("search") String search);

	@Modifying
	@Query("DELETE FROM CompanyCategories cc WHERE cc.company.idCompany = :companyId")
	void deleteByCompanyId(@Param("companyId") BigInteger companyId);

	@Query("SELECT cc FROM CompanyCategories cc WHERE cc.company.idCompany = :companyId")
	List<CompanyCategories> findCategoriesByCompanyId(@Param("companyId") BigInteger companyId);

	Optional<CompanyCategories> findByCompanyAndCategory(Company company, Categories category);

	@Query(value = "SELECT comp.id_company AS companyId, comp.name_company AS companyName, comp.code_company AS companyCode, CASE WHEN EXISTS (SELECT 1 FROM "
			+ "XRBNPPUSR.COMPANY_CATEGORIES_PREVIEW cp WHERE cp.company_id = comp.id_company AND cp.category_code = c.code) THEN 'PUBLISHED' ELSE 'DRAFT' END AS status "
			+ "FROM XRBNPPUSR.COMPANY_CATEGORIES cc JOIN XRBNPPUSR.COMPANY comp ON comp.id_company = cc.company_id JOIN XRBNPPUSR.CATEGORIES c "
			+ "ON c.code = cc.category_code WHERE c.id = :ruleId AND (:search IS NULL OR :search = '' OR LOWER(comp.name_company) LIKE LOWER('%' || :search || '%') "
			+ "OR LOWER(comp.code_company) LIKE LOWER('%' || :search || '%'))",
			countQuery = "SELECT COUNT(*) FROM (SELECT comp.id_company FROM XRBNPPUSR.COMPANY_CATEGORIES cc JOIN XRBNPPUSR.COMPANY comp ON "
					+ "comp.id_company = cc.company_id JOIN XRBNPPUSR.CATEGORIES c ON c.code = cc.category_code WHERE c.id = :ruleId AND "
					+ "(:search IS NULL OR :search = '' OR LOWER(comp.name_company) LIKE LOWER('%' || :search || '%') OR LOWER(comp.code_company) "
					+ "LIKE LOWER('%' || :search || '%')) GROUP BY comp.id_company) t", nativeQuery = true)
	Page<CompaniesByExclusionRuleProjection> getCompaniesByExclusionRuleId(@Param("ruleId") BigInteger ruleId,
			@Param("search") String search, Pageable pageable);
	
	@Query("SELECT cc FROM CompanyCategories cc WHERE cc.company.idCompany = :idCompany AND cc.category.id = :categoryId") 
	Optional<CompanyCategories> findByCompanyIdAndCategoryId(@Param("idCompany") BigInteger idCompany, @Param("categoryId") BigInteger categoryId);
	
	@Modifying
	@Query(value = "INSERT INTO XRBNPPUSR.COMPANY_CATEGORIES (ID, COMPANY_ID, CATEGORY_CODE) SELECT XRBNPPUSR.COMPANY_CATEGORIES_SEQ.NEXTVAL, c.ID_COMPANY, :categoryCode FROM XRBNPPUSR.COMPANY c", nativeQuery = true)
	void insertCategoriesForAllCompanies(@Param("categoryCode") String categoryCode);
	
	@Modifying
	@Query(value = "INSERT INTO XRBNPPUSR.COMPANY_CATEGORIES (ID, COMPANY_ID, CATEGORY_CODE) SELECT XRBNPPUSR.COMPANY_CATEGORIES_SEQ.NEXTVAL, COMPANY_ID, :newCode FROM XRBNPPUSR.COMPANY_CATEGORIES WHERE CATEGORY_CODE = :oldCode", nativeQuery = true)
	void cloneCompanyCategories(@Param("oldCode") String oldCode,
	                            @Param("newCode") String newCode);
	
	@Query(value = "SELECT COUNT(*) FROM XRBNPPUSR.COMPANY_CATEGORIES WHERE COMPANY_ID = :companyId AND CATEGORY_CODE = :categoryCode", nativeQuery = true)
	long existsCompanyCategory(@Param("companyId") BigInteger companyId,
	                           @Param("categoryCode") String categoryCode);
	
	@Modifying
	@Query(value = "INSERT INTO XRBNPPUSR.COMPANY_CATEGORIES (ID, COMPANY_ID, CATEGORY_CODE) VALUES (XRBNPPUSR.COMPANY_CATEGORIES_SEQ.NEXTVAL, :companyId, :categoryCode)", nativeQuery = true)
	void insertCompanyCategory(@Param("companyId") BigInteger companyId,
	                           @Param("categoryCode") String categoryCode);
	
	@Modifying
	@Query(value = "DELETE FROM XRBNPPUSR.COMPANY_CATEGORIES WHERE COMPANY_ID = :companyId AND CATEGORY_CODE = :categoryCode", nativeQuery = true)
	void deleteCompanyCategory(@Param("companyId") BigInteger companyId,
	                           @Param("categoryCode") String categoryCode);
	
	@Modifying
	@Query(value = "INSERT INTO XRBNPPUSR.COMPANY_CATEGORIES (ID, COMPANY_ID, CATEGORY_CODE) SELECT XRBNPPUSR.COMPANY_CATEGORIES_SEQ.NEXTVAL, :targetCompanyId, CATEGORY_CODE FROM XRBNPPUSR.COMPANY_CATEGORIES WHERE COMPANY_ID = :sourceCompanyId", nativeQuery = true)
	int copyCompanyCategories(@Param("sourceCompanyId") BigInteger sourceCompanyId,
	                           @Param("targetCompanyId") BigInteger targetCompanyId);
}