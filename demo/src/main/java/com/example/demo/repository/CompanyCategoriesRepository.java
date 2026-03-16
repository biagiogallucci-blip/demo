package com.example.demo.repository;

import java.math.BigInteger;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.CompanyCategories;
import com.example.demo.entity.CompanyCategoryId;

@Repository
public interface CompanyCategoriesRepository extends JpaRepository<CompanyCategories, CompanyCategoryId> {
	
    @Query("SELECT cc.category.code FROM CompanyCategories cc where " +
            "cc.company.codeCompany =:companyCode AND cc.category.active = 'Y' ")
    List<String> findActiveCategoryCodesByCompanyCode(@Param("companyCode") String companyCode);

    List<CompanyCategories> getCompanyExclusionRules(BigInteger companyId, String search);
}
