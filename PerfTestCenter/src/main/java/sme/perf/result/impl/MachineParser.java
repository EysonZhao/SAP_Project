package sme.perf.result.impl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import sme.perf.result.entity.Machine;
import sme.perf.result.entity.ResultSession;

public class MachineParser implements IParseResult<Machine> {

    private Logger logger;
    // private final static DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
    // private final static Pattern dataSizePattern = Pattern.compile("\\d+");
    private ResultSession resultSession;
    //private Pattern interfacePattern = null;

    public MachineParser(ResultSession session,Logger logger) {
        this.resultSession = session;
        this.logger=logger;
    }
    
    @Override
    public List<Machine> parse(String fileName) throws Exception {
        // File f = new File(fileName);
        // String shortFileName = f.getName();

        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        List<Machine> machineInfoList = new ArrayList<Machine>();

        int nCurrentLineNum = 0;
        try {
            String newLine = null;
            Machine machine = new Machine();
            machine.setResultSession(resultSession);
            while ((newLine = reader.readLine()) != null) {
                nCurrentLineNum++;
                if (newLine.length() > 0) {
                    String[] strNewLine = newLine.split(":");
                    String key=strNewLine[0].trim();
                    String value=null;
                    if(strNewLine.length>1){
                        value=strNewLine[1].trim();
                        if(strNewLine.length>2){
                        	value+=strNewLine[2].trim();
                        }
                    }
					if (value != null) {
						switch (key) {
						case "os":
							machine.setOs(value);
							break;
						case "hostname":
							machine.setHostName(value);
							break;
						case "cpu_core_number":
							machine.setCpuCoreNumber(Integer.parseInt(value));
							break;
						case "cpu_core_model":
							machine.setCpuCoreModel(value);
							break;
						case "mem_total":
							long memory = 0;
							if (value.toLowerCase().contains("kb")) {
								memory = (long) (Double.parseDouble(value
										.substring(0, value.length() - 2)) * 1024);
							} else if (value.toLowerCase().contains("mb")) {
								memory = (long) (Double.parseDouble(value
										.substring(0, value.length() - 2)) * 1048576);
							} else if (value.toLowerCase().contains("gb")) {
								memory = (long) (Double.parseDouble(value
										.substring(0, value.length() - 2)) * 1073741824);
							} else if (value.toLowerCase().contains("tb")) {
								memory = (long) (Double.parseDouble(value
										.substring(0, value.length() - 2)) * 1073741824 * 1024);
							}
							machine.setMemorySize(memory);
							break;
						case "os_type":
							machine.setOsType(value);
							break;
						case "hana_version":
							machine.setHanaVersion(value);
							break;
						case "mariadb_verion":
							machine.setMariadbVersion(value);
							break;
						case "filesystem":
							machine.setFileSystem(value);
							break;
						case "build_version":
							machine.setBuildVersion(value);
							break;
						case "container_csm":
							machine.setCsmContainer(value);
							break;
						case "csm_jvm_version":
							machine.setCsmJavaVersion(value);
							break;
						case "csm_jvm_heapsize":
							machine.setCsmJVMHeapSize(value);
							break;
						case "container_occ":
							machine.setOccContainer(value);
							break;
						case "occ_jvm_version":
							machine.setOccJavaVersion(value);
							break;
						case "occ_jvm_heapsize":
							machine.setOccJVMHeapSize(value);
							break;
						case "container_job":
							machine.setJobContainer(value);
							break;
						case "job_jvm_version":
							machine.setJobJavaVersion(value);
							break;
						case "job_jvm_heapsize":
							machine.setJobJVMHeapSize(value);
							break;
						case "container_eshop":
							machine.setEshopContainer(value);
							break;
						}
					}
				}
			}
            machineInfoList.add(machine);
        } catch (Exception ex) {
            logger.error(String.format("Parse Machine result fail @line: %d", nCurrentLineNum), ex);
            reader.close();
            throw ex;
        }
        reader.close();
        logger.info(String.format("%d Machine records are parsed from file %s", machineInfoList.size(), fileName));
        return machineInfoList;
    }
}
