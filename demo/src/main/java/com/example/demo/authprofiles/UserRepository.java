package com.example.demo.authprofiles;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, BigInteger> {

	@Query("SELECT c.id.conventionId, COUNT(DISTINCT u.id) FROM User u JOIN Convention c ON u.personalData.id = c.personalData.id "
			+ "WHERE c.id.conventionId IN :companyIds GROUP BY c.id.conventionId")
	List<Object[]> countUsersGroupedByCompany(List<BigInteger> companyIds);

	@Query("SELECT u FROM User u JOIN u.personalData pd JOIN Convention c ON pd.id = c.personalData.id WHERE c.id.conventionId = :companyId AND (:search IS NULL "
			+ "OR LOWER(pd.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(pd.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(pd.taxCode) "
			+ "LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(pd.email) LIKE LOWER(CONCAT('%', :search, '%')))")
	Page<User> findUsersByCompanyId(@Param("companyId") BigInteger companyId, @Param("search") String search,
			Pageable pageable);
	
	@Query("SELECT u FROM User u JOIN u.personalData pd WHERE (:search IS NULL OR LOWER(pd.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR "
			+ "LOWER(pd.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(pd.taxCode) LIKE LOWER(CONCAT('%', :search, '%')) OR "
			+ "LOWER(pd.email) LIKE LOWER(CONCAT('%', :search, '%')))")
	Page<User> findAllUsers(String search, Pageable pageable);
	
	@Modifying
	@Query("DELETE FROM User u WHERE u.personalData.id IN :ids")
	void deleteByPersonalDataIds(@Param("ids") Set<Long> ids);
}