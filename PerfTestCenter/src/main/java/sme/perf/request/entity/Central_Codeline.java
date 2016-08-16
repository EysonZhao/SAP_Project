package sme.perf.request.entity;

import java.io.Serializable;
import javax.persistence.*;




/**
 * The persistent class for the Central_Codeline database table.
 * 
 */
@Entity
public class Central_Codeline implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name="Alias")
	private String alias;

	@Id
	@Column(name="ID")
	private long id;

	@Column(name="IsAvailable")
	private boolean isAvailable;

	@Column(name="Name")
	private String name;

	@Column(name="P4Path")
	private String p4Path;

	public Central_Codeline() {
	}

	public String getAlias() {
		return this.alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public boolean getIsAvailable() {
		return this.isAvailable;
	}

	public void setIsAvailable(boolean isAvailable) {
		this.isAvailable = isAvailable;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getP4Path() {
		return this.p4Path;
	}

	public void setP4Path(String p4Path) {
		this.p4Path = p4Path;
	}

}