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
public class TopHeader {
    // @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    @GeneratedValue(generator = "TopHeaderSeq")
    @SequenceGenerator(name = "TopHeaderSeq", sequenceName = "TOPHEADER_SEQ", allocationSize = 1, initialValue = 1)
    private int id;
    private double cpuLoad;
    private String hostName;

    @Column(columnDefinition = "DateTime")
    @Convert("jodaDateConverter1")
	@JsonSerialize(using = sme.perf.utility.JsonDateSerializer.class)
	@JsonDeserialize(using = sme.perf.utility.JsonDateDeserializer.class)
    private DateTime date;

    @ManyToOne
    private ResultSession resultSession;

    @OneToMany(mappedBy = "topHeader", cascade = CascadeType.PERSIST)
    List<TopSubLine> topSubLines = new ArrayList<TopSubLine>();

    private long memoryTotal;
    private long memoryUsed;
    private long memoryFree;
    private long memoryCached;
    private double cpuUser;
    private double cpuSystem;
    private double cpuNice;
    
    public long getMemoryTotal() {
		return memoryTotal;
	}

	public void setMemoryTotal(long memoryTotal) {
		this.memoryTotal = memoryTotal;
	}

	public long getMemoryUsed() {
		return memoryUsed;
	}

	public void setMemoryUsed(long memoryUsed) {
		this.memoryUsed = memoryUsed;
	}

	public long getMemoryFree() {
		return memoryFree;
	}

	public void setMemoryFree(long memoryFree) {
		this.memoryFree = memoryFree;
	}

	public long getMemoryCached() {
		return memoryCached;
	}

	public void setMemoryCached(long memoryCached) {
		this.memoryCached = memoryCached;
	}

	public double getCpuUser() {
		return cpuUser;
	}

	public void setCpuUser(double cpuUser) {
		this.cpuUser = cpuUser;
	}

	public double getCpuSystem() {
		return cpuSystem;
	}

	public void setCpuSystem(double cpuSysytem) {
		this.cpuSystem = cpuSysytem;
	}

	public double getCpuNice() {
		return cpuNice;
	}

	public void setCpuNice(double cpuNice) {
		this.cpuNice = cpuNice;
	}

	public double getCpuIdle() {
		return cpuIdle;
	}

	public void setCpuIdle(double cpuIdle) {
		this.cpuIdle = cpuIdle;
	}

	private double cpuIdle;
    
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

    public List<TopSubLine> getTopSubLines() {
        return topSubLines;
    }

    public void setTopSubLines(List<TopSubLine> topSubLines) {
        this.topSubLines = topSubLines;
    }

    public double getCpuLoad() {
        return cpuLoad;
    }

    public void setCpuLoad(double cpuLoad) {
        this.cpuLoad = cpuLoad;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

}
