package sme.perf.result.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

@Entity
public class TopSubLine {
    // @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    @GeneratedValue(generator = "TopSubLineSeq")
    @SequenceGenerator(name = "TopSubLineSeq", sequenceName = "TOPSUBLINE_SEQ", allocationSize = 1, initialValue = 1)
    private int id;

    @ManyToOne
    private TopHeader topHeader;
    private long processId;
    private long virtMemory;
    private long memory;
    private double cpu;
    private String command;
    private String userName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TopHeader getTopHeader() {
        return topHeader;
    }

    public void setTopHeader(TopHeader topHeader) {
        this.topHeader = topHeader;
    }

    public long getProcessId() {
        return processId;
    }

    public void setProcessId(long processId) {
        this.processId = processId;
    }

    public long getVirtMemory() {
        return virtMemory;
    }

    public void setVirtMemory(long virtMemory) {
        this.virtMemory = virtMemory;
    }

    public long getMemory() {
        return memory;
    }

    public void setMemory(long memory) {
        this.memory = memory;
    }

    public double getCpu() {
        return cpu;
    }

    public void setCpu(double cpu) {
        this.cpu = cpu;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
