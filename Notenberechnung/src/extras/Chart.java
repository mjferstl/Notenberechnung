package extras;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xddf.usermodel.PresetColor;
import org.apache.poi.xddf.usermodel.XDDFColor;
import org.apache.poi.xddf.usermodel.XDDFShapeProperties;
import org.apache.poi.xddf.usermodel.XDDFSolidFillProperties;
import org.apache.poi.xddf.usermodel.chart.AxisCrosses;
import org.apache.poi.xddf.usermodel.chart.AxisPosition;
import org.apache.poi.xddf.usermodel.chart.BarDirection;
import org.apache.poi.xddf.usermodel.chart.ChartTypes;
import org.apache.poi.xddf.usermodel.chart.XDDFBarChartData;
import org.apache.poi.xddf.usermodel.chart.XDDFCategoryAxis;
import org.apache.poi.xddf.usermodel.chart.XDDFChartData;
import org.apache.poi.xddf.usermodel.chart.XDDFDataSource;
import org.apache.poi.xddf.usermodel.chart.XDDFDataSourcesFactory;
import org.apache.poi.xddf.usermodel.chart.XDDFNumericalDataSource;
import org.apache.poi.xddf.usermodel.chart.XDDFValueAxis;
import org.apache.poi.xssf.usermodel.XSSFChart;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFSheet;

public class Chart {
	
	private XSSFSheet xssfsheet;
	private XDDFDataSource<Double> xAxisData;
	private XDDFNumericalDataSource<Double> yAxisData;
	private int startRow, endRow, startColumn, endColumn;
	private String chartTitle, xAxisLabel, yAxisLabel, legend;
	
	public Chart(Sheet sheet) {
		this.xssfsheet = (XSSFSheet) sheet;
		this.startRow = 0;
		this.endRow = 10;
		this.startColumn = 0;
		this.endColumn = 10;
		this.chartTitle = "";
		this.xAxisLabel = "";
		this.yAxisLabel = "";
	}
	
	public void setXData(CellRangeAddress cra) {
		this.xAxisData = XDDFDataSourcesFactory.fromNumericCellRange(this.xssfsheet, cra);
	}
	
	public void setYData(CellRangeAddress cra) {
		this.yAxisData = XDDFDataSourcesFactory.fromNumericCellRange(this.xssfsheet, cra);
		this.legend = "";
	}
	
	public void setYData(CellRangeAddress cra, String legend) {
		this.yAxisData = XDDFDataSourcesFactory.fromNumericCellRange(this.xssfsheet, cra);
		this.legend = legend;
	}
	
	public void setPositionInSheet(int startRow, int endRow, int startColumn, int endColumn) {
		this.startRow = startRow;
		this.endRow = endRow;
		this.startColumn = startColumn;
		this.endColumn = endColumn;
	}
	
	public void createChart() {
		XSSFDrawing drawing = this.xssfsheet.createDrawingPatriarch();
        XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, this.startColumn, this.startRow, this.endColumn, this.endRow);
        
		XSSFChart chart = drawing.createChart(anchor);
        chart.setTitleText(this.chartTitle);
        chart.setTitleOverlay(false);
        
        // Use a category axis for the bottom axis.
        XDDFCategoryAxis bottomAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
        bottomAxis.setTitle(this.xAxisLabel); 
        bottomAxis.setMinimum(0);
        bottomAxis.setMaximum(7);
        XDDFValueAxis leftAxis = chart.createValueAxis(AxisPosition.LEFT);
        leftAxis.setTitle(this.yAxisLabel);
        leftAxis.setCrosses(AxisCrosses.AUTO_ZERO);
        
		XDDFChartData data = chart.createData(ChartTypes.BAR, bottomAxis, leftAxis);
        XDDFChartData.Series series1 = data.addSeries(xAxisData, yAxisData);
        series1.setTitle(this.legend, null);
        chart.plot(data);

        // in order to transform a bar chart into a column chart, you just need to change the bar direction
        XDDFBarChartData bar = (XDDFBarChartData) data;
        bar.setBarDirection(BarDirection.COL);
        // looking for "Stacked Bar Chart"? uncomment the following line
        // bar.setBarGrouping(BarGrouping.STACKED);
//        bar.setBarGrouping(BarGrouping.STANDARD);

        solidFillSeries(data, 0, PresetColor.DARK_BLUE);
	}

    private static void solidFillSeries(XDDFChartData data, int index, PresetColor color) {
        XDDFSolidFillProperties fill = new XDDFSolidFillProperties(XDDFColor.from(color));
        XDDFChartData.Series series = data.getSeries().get(index);
        XDDFShapeProperties properties = series.getShapeProperties();
        if (properties == null) {
            properties = new XDDFShapeProperties();
        }
        properties.setFillProperties(fill);
        series.setShapeProperties(properties);
    }
    
    public String getChartTitle() {
		return chartTitle;
	}

	public void setChartTitle(String chartTitle) {
		this.chartTitle = chartTitle;
	}

	public String getxAxisLabel() {
		return xAxisLabel;
	}

	public void setxAxisLabel(String xAxisLabel) {
		this.xAxisLabel = xAxisLabel;
	}

	public String getyAxisLabel() {
		return yAxisLabel;
	}

	public void setyAxisLabel(String yAxisLabel) {
		this.yAxisLabel = yAxisLabel;
	}
}