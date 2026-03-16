package com.example.demo.entity;

import java.io.Serializable;
import java.time.LocalDate;

import com.example.demo.utils.ActiveFlag;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "CATEGORIES", schema = "XRBNPPUSR")
public class Categories implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
    @Column(name = "CODE")
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(name = "ACTIVE", nullable = false, length = 1)
    private ActiveFlag active;

    @Column(name = "CREATION", nullable = false)
    private LocalDate creation;

    @Column(name = "LAST_DEACTIVATION")
    private LocalDate lastDeactivation;

    @Column(name = "NAME")
    private String name;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public ActiveFlag getActive() {
		return active;
	}

	public void setActive(ActiveFlag active) {
		this.active = active;
	}

	public LocalDate getCreation() {
		return creation;
	}

	public void setCreation(LocalDate creation) {
		this.creation = creation;
	}

	public LocalDate getLastDeactivation() {
		return lastDeactivation;
	}

	public void setLastDeactivation(LocalDate lastDeactivation) {
		this.lastDeactivation = lastDeactivation;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}