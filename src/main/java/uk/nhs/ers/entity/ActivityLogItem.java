package uk.nhs.ers.entity;

public class ActivityLogItem {

	private String activity;
	private String sessionId;
	private String ubrn;
	private String datetime;
	private String reasonCode;

	public String getActivity() {
		return activity;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getUbrn() {
		return ubrn;
	}

	public void setUbrn(String ubrn) {
		this.ubrn = ubrn;
	}

	public String getDatetime() {
		return datetime;
	}

	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}

	public String getReasonCode() {
		return reasonCode;
	}

	public void setReasonCode(String reasonCode) {
		this.reasonCode = reasonCode;
	}

}
