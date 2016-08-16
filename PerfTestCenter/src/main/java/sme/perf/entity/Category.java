package sme.perf.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

@Entity
public class Category {

	@Id
	@GeneratedValue(generator="CategorySeq")
    @SequenceGenerator(name = "CategorySeq", sequenceName = "CATEGORY_REQ", allocationSize = 1, initialValue = 1)
	private long id;
	private String name;
	
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
	
}
