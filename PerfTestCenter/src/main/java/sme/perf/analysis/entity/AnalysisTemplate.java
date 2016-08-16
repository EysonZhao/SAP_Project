package sme.perf.analysis.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

@Entity
public class AnalysisTemplate {
    @Id
    @GeneratedValue(generator = "AnalysisTemplate")
    @SequenceGenerator(name = "AnalysisTemplate", sequenceName = "AnalysisTemplate_Seq", allocationSize = 1, initialValue = 1)
	private long id;
	
	private String name;
	private String owner;
	private String description;
	private String fileName;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
