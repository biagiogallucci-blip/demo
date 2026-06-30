package com.example.demo.authprofiles;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1")
public class UserController {
	
	@Autowired
	private IUserService userService;
	
	@PostMapping("/companies/users/count")
    public Map<Long, Long> getUserCounts(@RequestBody List<BigInteger> companyIds) {
        return userService.countUsersByCompanyIds(companyIds);
    }
	
	@GetMapping("/companies/{companyId}/users")
    public UsersResponse getUsersByCompanyId(
        @PathVariable BigInteger companyId,
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "10") int limit,
        @RequestParam(required = false) String search
    ) {
        return userService.getUsers(companyId, page, limit, search);
    }
	
	@GetMapping("/users")
    public UsersResponse getAllUsers(
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "10") int limit,
        @RequestParam(required = false) String search
    ) {
        return userService.getUsers(null, page, limit, search);
    }
	
	@PostMapping("/companies/{companyId}/users")
	public ResponseEntity<CreateUserResponse> createUserWithCompany(
	    @PathVariable Long companyId,
	    @RequestBody CreateUserRequest request
	) {
	    return ResponseEntity.status(HttpStatus.CREATED)
	        .body(userService.createUserInternal(request, companyId));
	}
	
	@PostMapping("/users")
	public ResponseEntity<CreateUserResponse> createUser(
	    @RequestBody CreateUserRequest request
	) {
	    return ResponseEntity.status(HttpStatus.CREATED)
	        .body(userService.createUserInternal(request, null));
	}
	
	@PostMapping("/users/import/confirm")
	public ResponseEntity<ImportResponse> importUsers(
			@RequestBody List<ImportUserRequest> users
	) {
	    return ResponseEntity.status(HttpStatus.CREATED)
	        .body(userService.importUsers(users));
	}
	
	@PostMapping("/users/bulk-delete/validate")
	public ResponseEntity<BulkDeleteValidateResponse> validate(
	    @RequestParam("file") MultipartFile file
	) {
	    return ResponseEntity.ok(userService.validateCsv(file));
	}
	
	@PostMapping("/users/bulk-delete/confirm")
	public ResponseEntity<BulkDeleteResponse> confirm(
	    @RequestBody BulkDeleteRequest request
	) {
	    return ResponseEntity.ok(
	        userService.deleteUsers(request.getUsersToDelete())
	    );
	}
}