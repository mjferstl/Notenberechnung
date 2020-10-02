package excel;

public class ExcelStdReturn {
	
	private String formula = "";
	private String columnName;
	
	public ExcelStdReturn(String formula, String columnName) {
		this.formula = formula;
		this.columnName = columnName;
	}

	public String getFormula() {
		return formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	
	

}
