package com.example.demo.authprofiles;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class ConventionId implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "CONVENTION_ID")
    private Long conventionId;

    @Column(name = "PERSONAL_DATA_ID")
    private Long personalDataId;

	public Long getConventionId() {
		return conventionId;
	}

	public void setConventionId(Long conventionId) {
		this.conventionId = conventionId;
	}

	public Long getPersonalDataId() {
		return personalDataId;
	}

	public void setPersonalDataId(Long personalDataId) {
		this.personalDataId = personalDataId;
	}

	@Override
	public int hashCode() {
		return Objects.hash(conventionId, personalDataId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ConventionId other = (ConventionId) obj;
		return Objects.equals(conventionId, other.conventionId) && Objects.equals(personalDataId, other.personalDataId);
	}

	@Override
	public String toString() {
		return "ConventionId [conventionId=" + conventionId + ", personalDataId=" + personalDataId + "]";
	}

    
}
