package extras;

public class Error {
	
	private int errorId;
	private String errorMsg;
	
	public Error() {
		this.errorId = 0;
		this.errorMsg = "";
	}
	
	public Error(int errorId, String errorMsg) {
		this.errorId = errorId;
		this.errorMsg = errorMsg;
	}

	public int getErrorId() {
		return errorId;
	}

	public void setErrorId(int errorId) {
		this.errorId = errorId;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
}