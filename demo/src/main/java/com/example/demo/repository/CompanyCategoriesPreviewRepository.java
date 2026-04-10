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
import com.example.demo.projection.PublishExclusionRuleProjection;
import com.example.demo.utils.ActiveFlag;

@Repository
public interface CompanyCategoriesPreviewRepository extends JpaRepository<CompanyCategoriesPreview, BigInteger>{
	
	@Modifying
    @Transactional
    @Query("DELETE FROM CompanyCategoriesPreview cc WHERE cc.company.idCompany = :companyId")
	void deleteByCompanyId(@Param("companyId") BigInteger companyId);
	
	Optional<CompanyCategoriesPreview> findByCompanyAndCategoryAndIdNot(Company company, Categories category,BigInteger id);
	
	Boolean existsByCompanyAndCategoryAndIsEnabled(Company company, Categories category, ActiveFlag isEnabled);
	
	@Query("SELECT cc.company.id AS companyId, ccp.id AS companyCategoriesPreviewId FROM CompanyCategories cc JOIN cc.category c JOIN CompanyCategoriesPreview ccp "
			+ "ON ccp.company.id = cc.company.id AND ccp.category.code = c.code WHERE c.id = :categoryId AND ccp.isEnabled <> cc.isEnabled  AND EXISTS "
			+ "(SELECT 1 FROM CompanyCategoriesPreview ccpSame WHERE ccpSame.company.id = cc.company.id AND ccpSame.category.code = c.code AND "
			+ "ccpSame.isEnabled = cc.isEnabled)")
		List<PublishExclusionRuleProjection> findExclusionRuleToPublish(@Param("categoryId") BigInteger categoryId);
}