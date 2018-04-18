package org.kpmp.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "case_demographics")
public class CaseDemographics {

	@Id
	@Column(name = "id")
	private int id;
	@Column(name = "subject_id")
	private String subjectId;
	@Column(name = "experiment_id")
	private String experimentId;
	@Column(name = "performed_at")
	private Date experimentDate;
	@Column(name = "created_at")
	private Date createdAt;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "uploadPackage")
	private List<FileSubmissions> fileSubmissions;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}

	public String getExperimentId() {
		return experimentId;
	}

	public void setExperimentId(String experimentId) {
		this.experimentId = experimentId;
	}

	public Date getExperimentDate() {
		return experimentDate;
	}

	public void setExperimentDate(Date experimentDate) {
		this.experimentDate = experimentDate;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public List<FileSubmissions> getFileSubmissions() {
		return fileSubmissions;
	}

	public void setFileSubmissions(List<FileSubmissions> fileSubmissions) {
		this.fileSubmissions = fileSubmissions;
	}
}
