package sme.perf.result.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class ImportedSessionIds {
	@Id
	@GeneratedValue(generator = "ImportedSessionIdsSeq")
	@SequenceGenerator(name = "ImportedSessionIdsSeq", sequenceName = "IMPORTED_SEQ", allocationSize = 1, initialValue = 1)
	private int id;

	private int resultSession_Id;
}
