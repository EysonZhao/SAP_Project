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
import org.joda.time.DateTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;


@Entity
public class IOHeader {
    // @GeneratedValue(strategy=GenerationType.AUTO)
    @Id
    @GeneratedValue(generator = "IOHeaderSeq")
    @SequenceGenerator(name = "IOHeaderSeq", sequenceName = "IOHEADER_SEQ", allocationSize = 1, initialValue = 1)
    private int id;

    @ManyToOne
    private ResultSession resultSession;

    @Column(columnDefinition = "DateTime")
    @Convert("jodaDateConverter1")
	@JsonSerialize(using = sme.perf.utility.JsonDateSerializer.class)
	@JsonDeserialize(using = sme.perf.utility.JsonDateDeserializer.class)
    private DateTime date;

    private String hostName;
    
    private String service;

    @OneToMany(mappedBy = "ioHeader", cascade = CascadeType.PERSIST)
    private List<IOSubLine> ioSubLines = new ArrayList<IOSubLine>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ResultSession getResultSession() {
        return resultSession;
    }

    public void setResultSession(ResultSession resultSession) {
        this.resultSession = resultSession;
    }

    public DateTime getDate() {
        return date;
    }

    public void setDate(DateTime date) {
        this.date = date;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public List<IOSubLine> getIoSubLines() {
        return ioSubLines;
    }

    public void setIoSubLines(List<IOSubLine> ioSubLines) {
        this.ioSubLines = ioSubLines;
    }

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}
}
