package sme.perf.ta.enetity;

import java.io.Serializable;
import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;


/**
 * The persistent class for the TA_Category database table.
 * 
 */
@Entity
@Getter
@Setter
@NamedQuery(name="TA_Category.findAll", query="SELECT t FROM TA_Category t")
public class TA_Category implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "TA_CategoryId", sequenceName = "TA_CategoryGen", allocationSize = 1, initialValue = 1)
	private int id;

	@Column(name="Category")
	private String category;

	@Column(name="ExecutionTemplateIds")
	private long executionTemplateIds;

	public TA_Category() {
	}


}