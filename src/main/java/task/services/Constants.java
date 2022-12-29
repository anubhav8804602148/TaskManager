package task.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Constants {
	
	private Constants() {}
	
	public static final String REPORT_NAME="Task Report";
	public static final String CREATE_NEW_TASK = "CREATE_NEW_TASK";
	public static final String HOME="home";
	public static final String REDIRECT_HOME="redirect:/home";
	
	public static final int MAX_EXCEL_ROWS=64000;
	
	public static final String CSV="exportToCSV";
	public static final String PDF="exportToPDF";
	public static final String PPT="exportToPPT";
	public static final String EXCEL="exportToExcel";
	
	public static final String ID="Id";
	public static final String DESCRIPTION="Id";
	public static final String PRIORITY="Priority";
	public static final String LAST_UPDATED_DATE="Last Updated Date";
	public static final String STATUS="Status";
	public static final String COMPLETED_DATE="Completed Date";
	public static final String REGISTERED_DATE="Registered Date";
	public static final String TITLE = "Title";
	
	public static final String CONTENT_TYPE_CSV="text/plain";
	public static final String CONTENT_TYPE_PPT="application/vnd.ms-powerpoint";
	public static final String CONTENT_TYPE_PDF="application/pdf";
	public static final String CONTENT_TYPE_EXCEL="application/vnd.ms-excel";
	
	public static final String FILE_TYPE_EXCEL=".xlsx";
	public static final String FILE_TYPE_CSV=".csv";
	public static final String FILE_TYPE_PPT=".pptx";
	public static final String FILE_TYPE_PDF=".pdf";

	public static final String PENDING="PENDING";
	public static final String COMPLETED="COMPLETED";
	public static final String WORKING="WORKING";
	public static final String ERROR="ERROR";
	
	protected static final List<String> TASK_HEADERS = new ArrayList<>();
	protected static final List<String> STATUS_LIST = new ArrayList<>();
	protected static final Map<String, String> CONTENT_TYPE_MAP=new HashMap<>();
	protected static final Map<String, String> FILE_TYPE_MAP=new HashMap<>();
	
	
	static {
		TASK_HEADERS.add(ID);
		TASK_HEADERS.add(TITLE);
		TASK_HEADERS.add(DESCRIPTION);
		TASK_HEADERS.add(PRIORITY);
		TASK_HEADERS.add(STATUS);
		TASK_HEADERS.add(REGISTERED_DATE);
		TASK_HEADERS.add(LAST_UPDATED_DATE);
		TASK_HEADERS.add(COMPLETED_DATE);

		CONTENT_TYPE_MAP.put(CSV, CONTENT_TYPE_CSV);
		CONTENT_TYPE_MAP.put(PDF, CONTENT_TYPE_PDF);
		CONTENT_TYPE_MAP.put(PPT, CONTENT_TYPE_PPT);
		CONTENT_TYPE_MAP.put(EXCEL, CONTENT_TYPE_EXCEL);

		FILE_TYPE_MAP.put(CSV, FILE_TYPE_CSV);
		FILE_TYPE_MAP.put(PDF, FILE_TYPE_PDF);
		FILE_TYPE_MAP.put(PPT, FILE_TYPE_PPT);
		FILE_TYPE_MAP.put(EXCEL, FILE_TYPE_EXCEL);

		STATUS_LIST.add(COMPLETED);
		STATUS_LIST.add(WORKING);
		STATUS_LIST.add(PENDING);
		STATUS_LIST.add(ERROR);
	}
	
}
