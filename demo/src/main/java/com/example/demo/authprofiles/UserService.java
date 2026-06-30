package com.example.demo.authprofiles;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.model.Meta;
import com.example.demo.model.Pagination;

@Service
public class UserService implements IUserService {
	
	@Autowired
    private UserRepository userRepository;
	
	@Autowired
    private PersonalDataRepository  personalDataRepository;
	
	@Autowired
    private ConventionRepository  conventionRepository;
	
	private long id = 15;

	@Override
	public Map<Long, Long> countUsersByCompanyIds(List<BigInteger> companyIds) {
		List<Object[]> results = userRepository.countUsersGroupedByCompany(companyIds);

        return results.stream()
            .collect(Collectors.toMap(
                r -> (Long) r[0],    
                r -> ((Long) r[1]) 
            ));
	}

	@Override
	public UsersResponse getUsers(BigInteger companyId, int page, int limit, String search) {
		 Pageable pageable = PageRequest.of(page - 1, limit);
		 
		 Page<User> usersPage = null;
		 
			if (companyId != null) {

				usersPage = userRepository.findUsersByCompanyId(companyId, search, pageable);
			} else {
				usersPage = userRepository.findAllUsers(search, pageable);
			}
	        
	        List<UserDto> users = new ArrayList<>();
	        
	        for(User user : usersPage.getContent()) {
	        	PersonalData pd = user.getPersonalData();

	        	UserDto userDto =  new UserDto(
                	user.getId(),
                    pd.getFirstName(),
                    pd.getLastName(),
                    pd.getTaxCode(),
                    pd.getEmail(),
                    pd.getPhoneNumber()
                );
	        	
	        	users.add(userDto);
	        	
	        	
	        }
	        
	        UsersResponse response = new UsersResponse();
	        response.setData(users);
	        
	        Pagination pagination = new Pagination();
	        pagination.setCurrentPage(page);
	        pagination.setItemsPerPage(limit);
	        pagination.setTotalPages(usersPage.getTotalPages());
	        pagination.setTotalItems(usersPage.getTotalElements());
	        
	        Meta meta = new Meta();
	        meta.setPagination(pagination);
	        
	        response.setMeta(meta);
			
	        return response;
	}
	
	@Transactional
	public CreateUserResponse createUserInternal(CreateUserRequest request, Long companyId) {

	    List<ErrorDetail> errors = new ArrayList<>();

	    if (personalDataRepository.existsByTaxCode(request.getTaxCode())) {
	        errors.add(new ErrorDetail(
	            "taxCode",
	            "DUPLICATE_VALUE",
	            "Il Codice Fiscale inserito è già presente a sistema."
	        ));
	    }

	    if (personalDataRepository.existsByEmail(request.getEmail())) {
	        errors.add(new ErrorDetail(
	            "email",
	            "DUPLICATE_VALUE",
	            "Email già presente a sistema."
	        ));
	    }

	    if (!errors.isEmpty()) {
	        throw new ValidationException(errors);
	    }

	    PersonalData pd = new PersonalData();
	    pd.setId(id++);
	    pd.setFirstName(request.getFirstName());
	    pd.setLastName(request.getLastName());
	    pd.setTaxCode(request.getTaxCode());
	    pd.setEmail(request.getEmail());
	    pd.setPhoneNumber(request.getMobile());

	    personalDataRepository.save(pd);

	    if (companyId != null) {
	        ConventionId id = new ConventionId();
	        id.setConventionId(companyId);
	        id.setPersonalDataId(pd.getId());

	        Convention c = new Convention();
	        c.setId(id);
	        c.setPersonalData(pd);

	        conventionRepository.save(c);
	    }

	    return new CreateUserResponse(
	        pd.getId(),
	        pd.getFirstName(),
	        pd.getLastName(),
	        pd.getTaxCode(),
	        pd.getEmail(),
	        pd.getPhoneNumber(),
	        Instant.now()
	    );
	}
	
	@Transactional
	public ImportResponse importUsers(List<ImportUserRequest> users) {

	    List<ImportResult> results = new ArrayList<>();
	    List<Map<String, Object>> errors = new ArrayList<>();

	    int success = 0;

	    for (int i = 0; i < users.size(); i++) {
	        ImportUserRequest u = users.get(i);

	        try {
	        	CreateUserResponse created =
	                createUserInternal(u, u.getCompanyId());

	            results.add(new ImportResult(
	                created.getId(),
	                u.getTaxCode(),
	                "CREATED"
	            ));

	            success++;

	        } catch (ValidationException ex) {

	            Map<String, Object> error = new HashMap<>();
	            error.put("rowNumber", i + 1);
	            error.put("rawData", u);
	            error.put("issue", "Utente già registrato");

	            errors.add(error);
	        }
	    }

	    ImportResponse response = new ImportResponse();
	    response.setStatus("SUCCESS");
	    response.setMessage("Importazione completata con successo");
	    response.setImportedCount(success);
	    response.setData(results);
	    response.setErrors(errors);

	    return response;
	}
	
	@Transactional(readOnly = true)
	public BulkDeleteValidateResponse validateCsv(MultipartFile file) {

	    List<String> taxCodes = new ArrayList<>();

	    try (BufferedReader reader = new BufferedReader(
	            new InputStreamReader(file.getInputStream()))) {

	        reader.readLine(); 

	        String line;
	        while ((line = reader.readLine()) != null) {
	            if (!line.isBlank()) {
	                taxCodes.add(line.trim());
	            }
	        }

	    } catch (Exception e) {
	        throw new RuntimeException("Errore lettura file");
	    }

	    List<PersonalData> existing =
	        personalDataRepository.findByTaxCodeIn(taxCodes);

	    Map<String, PersonalData> map = existing.stream()
	        .collect(Collectors.toMap(PersonalData::getTaxCode, p -> p));

	    List<UserCsvRecord> valid = new ArrayList<>();
	    List<UserErrorRecord> errors = new ArrayList<>();

	    int row = 1;

	    for (String cf : taxCodes) {
	        row++;

	        if (map.containsKey(cf)) {

	            PersonalData pd = map.get(cf);

	            UserCsvRecord r = new UserCsvRecord();
	            r.setRowNumber(row);
	            r.setFirstName(pd.getFirstName());
	            r.setLastName(pd.getLastName());
	            r.setTaxCode(pd.getTaxCode());
	            r.setEmail(pd.getEmail());
	            r.setMobile(pd.getPhoneNumber());

	            valid.add(r);

	        } else {

	            UserErrorRecord err = new UserErrorRecord();
	            err.setRowNumber(row);
	            err.setTaxCode(cf);
	            err.setIssue("Utente non trovato");

	            errors.add(err);
	        }
	    }

	    BulkDeleteValidateResponse response = new BulkDeleteValidateResponse();

	    Summary summary = new Summary();

	    summary.setTotalRows(taxCodes.size());
	    summary.setValidCount(valid.size());
	    summary.setErrorsCount(errors.size());

	    response.setSummary(summary);
	    response.setValidRecords(valid);
	    response.setErrors(errors);

	    return response;
	}
	
	@Transactional
	public BulkDeleteResponse deleteUsers(List<String> taxCodes) {

	    List<PersonalData> list =
	        personalDataRepository.findByTaxCodeIn(taxCodes);

	    Set<Long> pdIds = list.stream()
	        .map(PersonalData::getId)
	        .collect(Collectors.toSet());

	    // 🔥 delete in batch
	    conventionRepository.deleteByPersonalDataIds(pdIds);
	    userRepository.deleteByPersonalDataIds(pdIds);
	    personalDataRepository.deleteAll(list);

	    List<DeleteResult> results = taxCodes.stream()
	        .map(cf -> new DeleteResult(cf, "DELETED"))
	        .toList();

	    BulkDeleteResponse response = new BulkDeleteResponse();
	    response.setStatus("SUCCESS");
	    response.setMessage("Cancellazione massiva completata con successo");
	    response.setDeletedCount(list.size());
	    response.setData(results);

	    return response;
	}
}