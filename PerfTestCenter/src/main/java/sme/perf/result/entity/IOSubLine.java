package sme.perf.result.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

@Entity
public class IOSubLine {
    // @GeneratedValue(strategy=GenerationType.AUTO)
    @Id
    @GeneratedValue(generator = "IOSubLineSeq")
    @SequenceGenerator(name = "IOSubLineSeq", sequenceName = "IOSUBLINE_SEQ", allocationSize = 1, initialValue = 1)
    private int id;

    @ManyToOne
    private IOHeader ioHeader;

    private String device;

    private long readBytePerSecond;

    private long writeBytePerSecond;

    private double averageQueueSize;

    private double await;

    private double serviceTime;

    private double utility;

    private double readPerSecond;

    private double writePerSecond;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public IOHeader getIoHeader() {
        return ioHeader;
    }

    public void setIoHeader(IOHeader ioHeader) {
        this.ioHeader = ioHeader;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public long getReadBytePerSecond() {
        return readBytePerSecond;
    }

    public void setReadBytePerSecond(long readBytePerSecond) {
        this.readBytePerSecond = readBytePerSecond;
    }

    public long getWriteBytePerSecond() {
        return writeBytePerSecond;
    }

    public void setWriteBytePerSecond(long writeBytePerSecond) {
        this.writeBytePerSecond = writeBytePerSecond;
    }

    public double getAverageQueueSize() {
        return averageQueueSize;
    }

    public void setAverageQueueSize(double averageQueueSize) {
        this.averageQueueSize = averageQueueSize;
    }

    public double getAwait() {
        return await;
    }

    public void setAwait(double await) {
        this.await = await;
    }

    public double getServiceTime() {
        return serviceTime;
    }

    public void setServiceTime(double serviceTime) {
        this.serviceTime = serviceTime;
    }

    public double getUtility() {
        return utility;
    }

    public void setUtility(double utility) {
        this.utility = utility;
    }

    public double getReadPerSecond() {
        return readPerSecond;
    }

    public void setReadPerSecond(double readPerSecond) {
        this.readPerSecond = readPerSecond;
    }

    public double getWritePerSecond() {
        return writePerSecond;
    }

    public void setWritePerSecond(double writePerSecond) {
        this.writePerSecond = writePerSecond;
    }
}
