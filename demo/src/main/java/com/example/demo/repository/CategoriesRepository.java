package com.example.demo.repository;

import java.math.BigInteger;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Categories;
import com.example.demo.projection.CategoriesWithDraftProjection;
import com.example.demo.projection.ExclusionRulesProjection;

@Repository
public interface CategoriesRepository extends JpaRepository<Categories, String> {
	
	@Query(value = "SELECT XRBNPPUSR.CATEGORIES_SEQ.NEXTVAL FROM dual", nativeQuery = true)
    BigInteger getNextId();
	
	@Query(value = "SELECT c.id, c.code, c.name, COUNT(DISTINCT CASE WHEN cc.is_enabled = 'Y' THEN cc.company_id END) AS company_count, CASE WHEN EXISTS "
			+ "(SELECT 1 FROM XRBNPPUSR.COMPANY_CATEGORIES_PREVIEW ccp FULL OUTER JOIN XRBNPPUSR.COMPANY_CATEGORIES cc2 ON cc2.company_id = ccp.company_id "
			+ "AND cc2.category_code = ccp.category_code WHERE ccp.category_code = c.code AND (cc2.is_enabled != ccp.is_enabled OR cc2.is_enabled IS NULL "
			+ "OR ccp.is_enabled IS NULL)) THEN 1 ELSE 0 END AS has_diff, CASE WHEN EXISTS (SELECT 1 FROM XRBNPPUSR.COMPANY_CATEGORIES_PREVIEW ccp "
			+ "FULL OUTER JOIN XRBNPPUSR.COMPANY_CATEGORIES cc2 ON cc2.company_id = ccp.company_id AND cc2.category_code = ccp.category_code "
			+ "WHERE ccp.category_code = c.code AND (cc2.is_enabled != ccp.is_enabled OR cc2.is_enabled IS NULL OR ccp.is_enabled IS NULL)) THEN 'DRAFT' "
			+ "ELSE 'PUBLISHED' END AS status FROM XRBNPPUSR.CATEGORIES c LEFT JOIN XRBNPPUSR.COMPANY_CATEGORIES cc ON cc.category_code = c.code WHERE "
			+ "(:search IS NULL OR :search = '' OR LOWER(c.name) LIKE LOWER('%' || :search || '%') OR LOWER(c.code) LIKE LOWER('%' || :search || '%')) "
			+ "GROUP BY c.id, c.code, c.name HAVING (:status = 'ALL' OR (:status = 'DRAFT' AND MAX(CASE WHEN EXISTS (SELECT 1 FROM XRBNPPUSR.COMPANY_CATEGORIES_PREVIEW ccp "
			+ "FULL OUTER JOIN XRBNPPUSR.COMPANY_CATEGORIES cc2 ON cc2.company_id = ccp.company_id AND cc2.category_code = ccp.category_code WHERE "
			+ "ccp.category_code = c.code AND (cc2.is_enabled != ccp.is_enabled OR cc2.is_enabled IS NULL OR ccp.is_enabled IS NULL)) THEN 1 ELSE 0 END) = 1) "
	        + "OR (:status = 'PUBLISHED' AND MAX(CASE WHEN EXISTS (SELECT 1 FROM XRBNPPUSR.COMPANY_CATEGORIES_PREVIEW ccp FULL OUTER JOIN XRBNPPUSR.COMPANY_CATEGORIES cc2 "
	        + "ON cc2.company_id = ccp.company_id AND cc2.category_code = ccp.category_code WHERE ccp.category_code = c.code AND (cc2.is_enabled != ccp.is_enabled OR cc2.is_enabled "
	        + "IS NULL OR ccp.is_enabled IS NULL)) THEN 1 ELSE 0 END) = 0))", 
	       countQuery = "SELECT COUNT(*) FROM (SELECT c.id FROM XRBNPPUSR.CATEGORIES c LEFT JOIN XRBNPPUSR.COMPANY_CATEGORIES cc ON cc.category_code = "
	       		+ "c.code WHERE (:search IS NULL OR :search = '' OR LOWER(c.name) LIKE LOWER('%' || :search || '%') OR LOWER(c.code) LIKE "
	       		+ "LOWER('%' || :search || '%')) GROUP BY c.id, c.code, c.name HAVING (:status = 'ALL' OR (:status = 'DRAFT' AND MAX(CASE WHEN EXISTS "
	       		+ "(SELECT 1 FROM XRBNPPUSR.COMPANY_CATEGORIES_PREVIEW ccp FULL OUTER JOIN XRBNPPUSR.COMPANY_CATEGORIES cc2 ON cc2.company_id = ccp.company_id "
	       		+ "AND cc2.category_code = ccp.category_code WHERE ccp.category_code = c.code AND (cc2.is_enabled != ccp.is_enabled OR cc2.is_enabled IS NULL "
	       		+ "OR ccp.is_enabled IS NULL)) THEN 1 ELSE 0 END) = 1) OR (:status = 'PUBLISHED' AND MAX(CASE WHEN EXISTS (SELECT 1 FROM XRBNPPUSR.COMPANY_CATEGORIES_PREVIEW ccp "
	       		+ "FULL OUTER JOIN XRBNPPUSR.COMPANY_CATEGORIES cc2 ON cc2.company_id = ccp.company_id AND cc2.category_code = ccp.category_code WHERE "
	       		+ "ccp.category_code = c.code AND (cc2.is_enabled != ccp.is_enabled OR cc2.is_enabled IS NULL OR ccp.is_enabled IS NULL)) THEN 1 ELSE 0 END) = 0)) "
	        + ") AS temp_table", 
	       nativeQuery = true)
	Page<ExclusionRulesProjection> getExclusionRules(@Param("search") String search, @Param("status") String status, Pageable pageable);
	
	@Query("SELECT c AS category, CASE WHEN EXISTS (SELECT 1 FROM CompanyCategories cc, CompanyCategoriesPreview cp WHERE cc.category.code "
			+ "= c.code AND cp.category.code = c.code AND cp.company.id = cc.company.id AND cp.isEnabled <> cc.isEnabled) THEN 'DRAFT' ELSE "
			+ "'PUBLISHED' END AS status FROM Categories c WHERE c.id = :id")
			Optional<CategoriesWithDraftProjection> getByRuleId(@Param("id") BigInteger id);
}