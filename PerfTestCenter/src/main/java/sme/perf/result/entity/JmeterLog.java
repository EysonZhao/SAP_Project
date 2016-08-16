package sme.perf.result.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

import org.eclipse.persistence.annotations.Convert;
import org.joda.time.DateTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Entity
public class JmeterLog {
    // @GeneratedValue(strategy=GenerationType.AUTO)
    @Id
    @GeneratedValue(generator = "JmeterLogSeq")
    @SequenceGenerator(name = "JmeterLogSeq", sequenceName = "JMETERLOG_SEQ", allocationSize = 1, initialValue = 1)
    private int id;

    @ManyToOne
    private ResultSession resultSession;

    @Column(columnDefinition = "DateTime")
    @Convert("jodaDateConverter1")
	@JsonSerialize(using = sme.perf.utility.JsonDateSerializer.class)
	@JsonDeserialize(using = sme.perf.utility.JsonDateDeserializer.class)
    private DateTime date;

    @Column(length = 2048)
    private String request;

    private double responseTime;

    private int returnCode;

    private double latency;

    private String status = "ok";

    private int size = 0;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

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

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public double getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(double responseTime) {
        this.responseTime = responseTime;
    }

    public int getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(int returnCode) {
        this.returnCode = returnCode;
    }

    public double getLatency() {
        return latency;
    }

    public void setLatency(double latency) {
        this.latency = latency;
    }
}
