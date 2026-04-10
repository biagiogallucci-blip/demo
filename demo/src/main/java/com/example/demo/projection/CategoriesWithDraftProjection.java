package com.example.demo.projection;

import com.example.demo.entity.Categories;

public interface CategoriesWithDraftProjection {
	
	Categories getCategory();
	String getStatus();
}