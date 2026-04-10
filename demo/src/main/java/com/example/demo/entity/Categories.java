package com.example.demo.entity;

import java.io.Serializable;
import java.math.BigInteger;
import java.sql.Timestamp;

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
    private Timestamp creation;

    @Column(name = "LAST_DEACTIVATION")
    private Timestamp lastDeactivation;

    @Column(name = "NAME")
    private String name;
    
    @Column(name = "ID", updatable = false, nullable = false)
    private BigInteger id;

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

	public Timestamp getCreation() {
		return creation;
	}

	public void setCreation(Timestamp creation) {
		this.creation = creation;
	}

	public Timestamp getLastDeactivation() {
		return lastDeactivation;
	}

	public void setLastDeactivation(Timestamp lastDeactivation) {
		this.lastDeactivation = lastDeactivation;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigInteger getId() {
		return id;
	}

	public void setId(BigInteger id) {
		this.id = id;
	}
}