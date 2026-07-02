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
	
	@Query(value="WITH diff AS (SELECT DISTINCT c.category_code FROM XRBNPPUSR.COMPANY_CATEGORIES c WHERE NOT EXISTS (SELECT 1 FROM "
			+ "XRBNPPUSR.COMPANY_CATEGORIES_PREVIEW p WHERE p.category_code = c.category_code AND p.company_id = c.company_id) UNION SELECT DISTINCT "
			+ "p.category_code FROM XRBNPPUSR.COMPANY_CATEGORIES_PREVIEW p WHERE NOT EXISTS (SELECT 1 FROM XRBNPPUSR.COMPANY_CATEGORIES c WHERE "
			+ "c.category_code = p.category_code AND c.company_id = p.company_id)) SELECT c.id, c.code, c.name, COUNT(DISTINCT cc.company_id) AS companyCount, "
			+ "CASE WHEN d.category_code IS NOT NULL THEN 1 ELSE 0 END AS has_diff, CASE WHEN d.category_code IS NOT NULL THEN 'DRAFT' ELSE 'PUBLISHED' END "
			+ "AS status FROM XRBNPPUSR.CATEGORIES c LEFT JOIN XRBNPPUSR.COMPANY_CATEGORIES cc ON cc.category_code = c.code LEFT JOIN diff d ON d.category_code "
			+ "= c.code WHERE (:search IS NULL OR :search = '' OR LOWER(c.name) LIKE LOWER('%' || :search || '%') OR LOWER(c.code) LIKE LOWER('%' || :search || '%')) "
			+ "GROUP BY c.id, c.code, c.name, d.category_code HAVING (:status IS NULL OR :status = 'ALL' OR (:status = 'DRAFT' AND d.category_code IS NOT NULL) "
			+ "OR (:status = 'PUBLISHED' AND d.category_code IS NULL))",
			countQuery="SELECT COUNT(*) FROM (WITH diff AS (SELECT DISTINCT c.category_code FROM XRBNPPUSR.COMPANY_CATEGORIES c WHERE NOT EXISTS (SELECT 1 FROM "
					+ "XRBNPPUSR.COMPANY_CATEGORIES_PREVIEW p WHERE p.category_code = c.category_code AND p.company_id = c.company_id) UNION SELECT DISTINCT "
					+ "p.category_code FROM XRBNPPUSR.COMPANY_CATEGORIES_PREVIEW p WHERE NOT EXISTS (SELECT 1 FROM XRBNPPUSR.COMPANY_CATEGORIES c WHERE "
					+ "c.category_code = p.category_code AND c.company_id = p.company_id)) SELECT c.id FROM XRBNPPUSR.CATEGORIES c LEFT JOIN "
					+ "XRBNPPUSR.COMPANY_CATEGORIES cc ON cc.category_code = c.code LEFT JOIN diff d ON d.category_code = c.code WHERE (:search IS NULL "
					+ "OR :search = '' OR LOWER(c.name) LIKE LOWER('%' || :search || '%') OR LOWER(c.code) LIKE LOWER('%' || :search || '%')) "
					+ "GROUP BY c.id, c.code, c.name, d.category_code HAVING (:status IS NULL OR :status = 'ALL' OR (:status = 'DRAFT' AND d.category_code IS NOT NULL) OR (:status = 'PUBLISHED' AND d.category_code IS NULL))) t",
			nativeQuery=true)
			Page<ExclusionRulesProjection> getExclusionRules(@Param("search") String search, @Param("status") String status, Pageable pageable);
	
	@Query("SELECT c AS category, CASE WHEN (EXISTS (SELECT 1 FROM CompanyCategories cc WHERE cc.category.code = c.code AND NOT EXISTS "
			+ "(SELECT 1 FROM CompanyCategoriesPreview cp WHERE cp.category.code = cc.category.code AND cp.company.id = cc.company.id)) "
			+ "OR EXISTS (SELECT 1 FROM CompanyCategoriesPreview cp WHERE cp.category.code = c.code AND NOT EXISTS (SELECT 1 FROM CompanyCategories cc "
			+ "WHERE cc.category.code = cp.category.code AND cc.company.id = cp.company.id))) THEN 'DRAFT' ELSE 'PUBLISHED' END AS status "
			+ "FROM Categories c WHERE c.id = :id")
	Optional<CategoriesWithDraftProjection> getByRuleId(@Param("id") BigInteger id);
	
	@Query(value = "SELECT * FROM XRBNPPUSR.CATEGORIES c WHERE c.ID = :id", nativeQuery = true)
	Optional<Categories> findByBusinessId(@Param("id") BigInteger id);
}