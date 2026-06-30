package com.example.demo.authprofiles;

import java.time.LocalDate;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "CONVENTIONS", schema = "XRBNPPUSR")
public class Convention {

    @EmbeddedId
    private ConventionId id;

    @ManyToOne
    @MapsId("personalDataId")
    @JoinColumn(name = "PERSONAL_DATA_ID")
    private PersonalData personalData;

    private LocalDate startDate;
    private LocalDate endDate;
	public ConventionId getId() {
		return id;
	}
	public void setId(ConventionId id) {
		this.id = id;
	}
	public PersonalData getPersonalData() {
		return personalData;
	}
	public void setPersonalData(PersonalData personalData) {
		this.personalData = personalData;
	}
	public LocalDate getStartDate() {
		return startDate;
	}
	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}
	public LocalDate getEndDate() {
		return endDate;
	}
	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

    
}