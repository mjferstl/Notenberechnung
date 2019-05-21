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
	private int r1, r2, c1, c2;
	private String chartTitle, xAxisLabel, yAxisLabel, legend;
	
	public Chart(Sheet sheet) {
		this.xssfsheet = (XSSFSheet) sheet;
		this.r1 = 0;
		this.r2 = 10;
		this.c1 = 0;
		this.c2 = 10;
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
	
	public void setPositionInSheet(int r1, int r2, int c1, int c2) {
		this.r1 = r1;
		this.r2 = r2;
		this.c1 = c1;
		this.c2 = c2;
	}
	
	public void createChart() {
		XSSFDrawing drawing = this.xssfsheet.createDrawingPatriarch();
        XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, this.c1, this.r1, this.c2, this.r2);
        
		XSSFChart chart = drawing.createChart(anchor);
        chart.setTitleText(this.chartTitle);
        chart.setTitleOverlay(false);
        
        // Use a category axis for the bottom axis.
        XDDFCategoryAxis bottomAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
        bottomAxis.setTitle(this.xAxisLabel); 
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