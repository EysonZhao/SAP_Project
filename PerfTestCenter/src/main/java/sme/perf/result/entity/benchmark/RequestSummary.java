package sme.perf.result.entity.benchmark;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.Converter;
import org.joda.time.DateTime;

import lombok.Getter;
import lombok.Setter;
import sme.perf.utility.JodaDateConverter;

@Getter
@Setter
@Entity
@Converter(name = "jodaDateConverter2", converterClass = JodaDateConverter.class)
public class RequestSummary {
	 	@Id
	    private int id;

	    private String name;

	    @Column(columnDefinition = "DateTime")
	    @Convert("jodaDateConverter2")
	    private DateTime createDate;

	    @Column(columnDefinition = "DateTime")
	    @Convert("jodaDateConverter2")
	    private DateTime testStartDate;
	    
	    @Column(columnDefinition = "DateTime")
	    @Convert("jodaDateConverter2")
	    private DateTime testEndDate;

	    private int userNumber;

	    private String scenario;
	    
	    private String buildInfo;
	    
	    private String branch;

	  
}
