package com.example.demo.projection;

import com.example.demo.entity.Company;
import com.example.demo.utils.ActiveFlag;

public interface CloneExclusionRuleProjection {
	Company getCompany();
    ActiveFlag getIsEnabled();
}