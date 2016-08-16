package sme.perf.result.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;

import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.Converter;
import org.joda.time.DateTime;

import sme.perf.utility.JodaDateConverter;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Entity
@Converter(name = "jodaDateConverter1", converterClass = JodaDateConverter.class)
public class DockerStatsHeader {
    @Id
    @GeneratedValue(generator = "DockerStatsHeaderSeq")
    @SequenceGenerator(name = "DockerStatsHeaderSeq", sequenceName = "DOCKDERSTATSHEADER_SEQ", allocationSize = 1, initialValue = 1)
    private int id;
    	
	@Column(columnDefinition = "DateTime")
	@JsonSerialize(using = sme.perf.utility.JsonDateSerializer.class)
	@JsonDeserialize(using = sme.perf.utility.JsonDateDeserializer.class)
	@Convert("jodaDateConverter1")
    private DateTime date;

    @ManyToOne
    private ResultSession resultSession;
    
    private String hostName;
    
    private String service;
    
    @OneToMany(mappedBy = "dockerStatsHeader", cascade = CascadeType.PERSIST)
    private List<DockerStatsSubLine> subLines = new ArrayList<DockerStatsSubLine>();

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public DateTime getDate() {
		return date;
	}

	public void setDate(DateTime date) {
		this.date = date;
	}

	public ResultSession getResultSession() {
		return resultSession;
	}

	public void setResultSession(ResultSession resultSession) {
		this.resultSession = resultSession;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public List<DockerStatsSubLine> getSubLines() {
		return subLines;
	}

	public void setSubLines(List<DockerStatsSubLine> subLines) {
		this.subLines = subLines;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}
}
