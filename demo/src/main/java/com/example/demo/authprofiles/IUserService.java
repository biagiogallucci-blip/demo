package com.example.demo.authprofiles;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import org.jspecify.annotations.Nullable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface IUserService {

	Map<Long, Long> countUsersByCompanyIds(List<BigInteger> companyIds);

	UsersResponse getUsers(BigInteger companyId, int page, int limit, String search);

	CreateUserResponse createUserInternal(CreateUserRequest request, Long companyId);

	ImportResponse importUsers(List<ImportUserRequest> list);

	BulkDeleteValidateResponse validateCsv(MultipartFile file);

	BulkDeleteResponse deleteUsers(List<String> usersToDelete);
}