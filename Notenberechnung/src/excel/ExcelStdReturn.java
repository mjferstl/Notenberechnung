package excel;

public class ExcelStdReturn {
	
	private final String formula;
	private final String columnName;
	
	public ExcelStdReturn(String formula, String columnName) {
		this.formula = formula;
		this.columnName = columnName;
	}

	public String getFormula() {
		return formula;
	}

	public String getColumnName() {
		return columnName;
	}


}
