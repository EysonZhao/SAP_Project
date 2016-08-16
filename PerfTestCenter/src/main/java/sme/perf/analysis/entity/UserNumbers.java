package sme.perf.analysis.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityResult;
import javax.persistence.FieldResult;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.SqlResultSetMapping;

import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.Converter;
import org.joda.time.DateTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import sme.perf.utility.JodaDateConverter;



@SqlResultSetMapping(
		name="UserNumberMap",
		entities={
			@EntityResult(
				entityClass=UserNumbers.class,
				fields={
					@FieldResult(name="ID", column="ID"),
					@FieldResult(name="Date", column="Date"),
					@FieldResult(name="Session_Id", column="Session_Id"),
					@FieldResult(name="UserNumber", column="UserNumber"),
				}
			)
		}
	)

@Entity
@NamedNativeQueries({
	@NamedNativeQuery(
			name ="SelectUsersNumber", 
			query = "select (ROW_NUMBER() over(order by T2.DATE)) ID, T2.Date, ?1 Session_Id, sum(T5.AddUser) as UserNumber from "
					+ " (select T0.DATE, 1 AddUser from [PerfCenterResultDebug].dbo.TRANSACTIONRESPONSETIME T0 where "
					+ "T0.RESULTSESSION_ID = ?1 and T0.TRANSACTIONNAME = 'InitializeDone' union "
					+ "select T1.DATE, -1 AddUser from [PerfCenterResultDebug].dbo.TRANSACTIONRESPONSETIME T1 where "
					+ "T1.RESULTSESSION_ID = ?1 and T1.TRANSACTIONNAME = 'ActionDone')  T2 Join "
					+ "(select T3.DATE, 1 AddUser from [PerfCenterResultDebug].dbo.TRANSACTIONRESPONSETIME T3 "
					+ "where T3.RESULTSESSION_ID = ?1 and T3.TRANSACTIONNAME = 'InitializeDone' union "
					+ " select T4.DATE, -1 AddUser from [PerfCenterResultDebug].dbo.TRANSACTIONRESPONSETIME T4 "
					+ "where T4.RESULTSESSION_ID = ?1 and T4.TRANSACTIONNAME = 'ActionDone') As T5 on T2.Date >= T5.Date "
					+ "group by T2.DATE  order by T2.DATE ",		
	resultSetMapping="UserNumberMap"),
})


public class UserNumbers {
	@Id
    @GeneratedValue(generator = "UserGenSeq")
    @SequenceGenerator(name = "UserGenSeq", sequenceName = "UGenSeq", allocationSize = 1, initialValue = 1)
 	private int Id;
	
	private int session_Id;
	
	@Column(columnDefinition = "DateTime")
    @Convert("jodaDateConverter")
	@JsonSerialize(using = sme.perf.utility.JsonDateSerializer.class)
	@JsonDeserialize(using = sme.perf.utility.JsonDateDeserializer.class)
    private DateTime date;
	
	private int UserNumber;

	public int getId() {
		return Id;
	}

	public void setId(int id) {
		Id = id;
	}

	public int getSession_Id() {
		return session_Id;
	}

	public void setSession_Id(int session_Id) {
		this.session_Id = session_Id;
	}

	public DateTime getDate() {
		return date;
	}

	public void setDate(DateTime d) {
		date = d;
	}

	public int getUserNumber() {
		return UserNumber;
	}

	public void setUserNumber(int userNumber) {
		UserNumber = userNumber;
	}

	}

