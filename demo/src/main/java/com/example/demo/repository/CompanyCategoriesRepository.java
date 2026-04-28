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
import com.example.demo.projection.CloneExclusionRuleProjection;
import com.example.demo.projection.CompaniesByExclusionRuleProjection;
import com.example.demo.projection.CompanyExclusionRulesProjection;

@Repository
public interface CompanyCategoriesRepository extends JpaRepository<CompanyCategories, BigInteger> {

	@Query("SELECT cc.category.code FROM CompanyCategories cc where "
			+ "cc.company.codeCompany =:companyCode AND cc.category.active = 'Y' ")
	List<String> findActiveCategoryCodesByCompanyCode(@Param("companyCode") String companyCode);

	@Query(value = "SELECT preview_id AS previewId, category_code AS tag, category_name AS name, is_enabled_company AS isEnabled, is_enabled_preview AS isEnabledPreview "
			+ "FROM (SELECT p.id AS preview_id, p.company_id, p.category_code, c.name AS category_name, NVL(cc.is_enabled, 'N/A') AS is_enabled_company, p.is_enabled "
			+ "AS is_enabled_preview, ROW_NUMBER() OVER (PARTITION BY p.company_id, p.category_code ORDER BY CASE WHEN cc.is_enabled IS NULL THEN 1 WHEN cc.is_enabled <> p.is_enabled "
			+ "THEN 1 ELSE 2 END) AS rn FROM xrbnppusr.company_categories_preview p JOIN xrbnppusr.categories c ON c.code = p.category_code LEFT JOIN xrbnppusr.company_categories cc "
			+ "ON cc.company_id = p.company_id AND cc.category_code = p.category_code WHERE p.company_id = :idCompany AND (:search IS NULL OR LOWER(p.category_code) "
			+ "LIKE '%' || LOWER(:search) || '%' OR LOWER(c.name) LIKE '%' || LOWER(:search) || '%')) WHERE rn = 1", nativeQuery = true)
	List<CompanyExclusionRulesProjection> getCompanyExclusionRules(@Param("idCompany") BigInteger idCompany,
			@Param("search") String search);

	@Modifying
	@Transactional
	@Query("DELETE FROM CompanyCategories cc WHERE cc.company.idCompany = :companyId")
	void deleteByCompanyId(@Param("companyId") BigInteger companyId);

	@Query("SELECT cc FROM CompanyCategories cc WHERE cc.company.idCompany = :companyId")
	List<CompanyCategories> findCategoriesByCompanyId(@Param("companyId") BigInteger companyId);

	Optional<CompanyCategories> findByCompanyAndCategory(Company company, Categories category);

	@Query(value = "SELECT comp.idCompany AS companyId, comp.nameCompany AS companyName, comp.codeCompany AS companyCode, cc.isEnabled "
			+ "AS isEnabled, (SELECT MAX(cp.isEnabled) FROM CompanyCategoriesPreview cp WHERE cp.company.idCompany = comp.idCompany "
			+ "AND cp.category.code = c.code AND cp.isEnabled <> cc.isEnabled) AS draftIsEnabled FROM CompanyCategories cc JOIN cc.company comp "
	        + "JOIN cc.category c WHERE c.id = :ruleId AND (:search IS NULL OR LOWER(comp.nameCompany) LIKE LOWER(CONCAT('%', :search, '%')) "
	        + "OR LOWER(comp.codeCompany) LIKE LOWER(CONCAT('%', :search, '%')))",
	       countQuery = "SELECT COUNT(cc) FROM CompanyCategories cc JOIN cc.company comp JOIN cc.category c WHERE c.id = :ruleId "
	       		+ "AND (:search IS NULL OR LOWER(comp.nameCompany) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(comp.codeCompany) LIKE LOWER(CONCAT('%', :search, '%')))")
	Page<CompaniesByExclusionRuleProjection> getCompaniesByExclusionRuleId(@Param("ruleId") BigInteger ruleId, @Param("search") String search, 
	        Pageable pageable);
	
	@Query("SELECT cc.company AS company, cc.isEnabled AS isEnabled FROM CompanyCategories cc JOIN cc.category c WHERE c.id = :oldCategoryId")
		List<CloneExclusionRuleProjection> findForCloneExclusionRule(@Param("oldCategoryId") BigInteger oldCategoryId);
}