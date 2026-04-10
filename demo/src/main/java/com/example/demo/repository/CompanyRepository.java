package com.example.demo.repository;

import java.math.BigInteger;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Company;
import com.example.demo.projection.CompanyLookupProjection;
import com.example.demo.projection.CompanyProjection;

@Repository
public interface CompanyRepository extends JpaRepository<Company, BigInteger> {

	@Query("SELECT c.idCompany AS idCompany, c.codeCompany AS codeCompany, c.nameCompany AS nameCompany, c.status AS status, "
			+ "CASE WHEN EXISTS (SELECT 1 FROM CompanyCategories cc WHERE cc.company.idCompany = c.idCompany) THEN true ELSE false END AS hasCategory, "
			+ "CASE WHEN EXISTS (SELECT 1 FROM CompanyParameters cp WHERE cp.company.idCompany = c.idCompany) THEN true ELSE false END AS hasParameters "
			+ "FROM Company c WHERE (:search IS NULL OR :search = '' OR LOWER(c.nameCompany) LIKE LOWER(CONCAT('%', :search, '%')) OR "
			+ "LOWER(c.codeCompany) LIKE LOWER(CONCAT('%', :search, '%')))")
	Page<CompanyProjection> searchCompanies(@Param("search") String search, Pageable pageable);

	@Query(value = "SELECT c.ID_COMPANY as id, c.name_company AS name, c.code_company AS code, (SELECT COUNT(*) FROM XRBNPPUSR.COMPANY_CATEGORIES cc "
			+ "WHERE cc.COMPANY_ID = c.ID_COMPANY) AS exclusionRulesCount, (SELECT COUNT(*) FROM XRBNPPUSR.COMPANY_PARAMETERS cp "
			+ "WHERE cp.COMPANY_ID = c.ID_COMPANY) AS parametersCount FROM XRBNPPUSR.COMPANY c WHERE c.ID_COMPANY <> :exclude", nativeQuery = true)
	List<CompanyLookupProjection> getCompanyLookup(@Param("exclude") BigInteger currentCompanyId);
	
	@Query(value = "SELECT c.ID_COMPANY as id, c.name_company AS name, c.code_company AS code FROM XRBNPPUSR.COMPANY c WHERE NOT EXISTS (SELECT 1 "
			+ "FROM XRBNPPUSR.COMPANY_CATEGORIES cc JOIN XRBNPPUSR.CATEGORIES cat ON cat.CODE = cc.CATEGORY_CODE WHERE cc.COMPANY_ID = c.ID_COMPANY "
			+ "AND cat.ID = :categoryId AND cc.IS_ENABLED = 'Y')", nativeQuery = true)
	List<CompanyLookupProjection> getExclusionRuleLookup(@Param("categoryId") BigInteger currentRuleId);
}