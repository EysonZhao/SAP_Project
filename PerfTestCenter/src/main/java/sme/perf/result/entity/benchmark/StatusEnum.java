package sme.perf.result.entity.benchmark;

public enum StatusEnum {
	//None:      No Base Request.
	//Green:     10%+
	//Yellow:    -10% ~ 10%
	//Red:       -10%+
	None(0), Green(1),Yellow(2), Red(3);

    private int enumCode;

    private StatusEnum(int enumCode) {
        this.enumCode = enumCode;
    }
    
    public int getStatusEnum() {
        return this.enumCode;
    }
}
