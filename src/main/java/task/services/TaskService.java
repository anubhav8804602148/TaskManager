package task.services;

import java.awt.Rectangle;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.logging.log4j.util.Strings;
import org.apache.poi.sl.usermodel.Placeholder;
import org.apache.poi.sl.usermodel.StrokeStyle;
import org.apache.poi.sl.usermodel.TableCell.BorderEdge;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFHyperlink;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTable;
import org.apache.poi.xslf.usermodel.XSLFTableCell;
import org.apache.poi.xslf.usermodel.XSLFTableRow;
import org.apache.poi.xslf.usermodel.XSLFTextRun;
import org.apache.poi.xslf.usermodel.XSLFTextShape;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import task.models.Task;
import task.repositories.TaskRepository;

@Service
public class TaskService {
	
	@Autowired
	TaskRepository taskRepository;

	public List<Task> findAllTasks() {
		return taskRepository.findAll();
	}

	public List<Task> findFirstHundredTasks() {
		return taskRepository.findFirstHundredTasks();
	}

	public List<Task> fetchTasksToDisplay() {
		List<Task> requiredTasks = taskRepository.findUncompletedHundredTasks();
		int size = requiredTasks.size();
		if(size>100) return requiredTasks;
		requiredTasks.addAll(findFirstNCompletedTasks(100-size));
		return requiredTasks;
	}

	public List<Task> findFirstNCompletedTasks(int n) {
		return taskRepository.findFirstNCompletedTasks(n);
	}

	public void handleAction(String action, OutputStream outStream) {
		switch(action) {
			case Constants.EXCEL:
				exportToExcel(outStream);
				break;
			case Constants.CSV:
				exportToCSV(outStream);
				break;
			case Constants.PDF:
				exportToPDF(outStream);
				break;
			case Constants.PPT:
				exportToPPT(outStream);
				break;
			default:
				break;
		}		
	}

	public void exportToCSV(OutputStream outStream) {		
		File file = new File("TaskReport_"+getDate()+".csv");	
		
		try (FileWriter writer = new FileWriter(file);CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT)){
			List<Task> tasks = findAllTasks();
			printer.print("Id");
			printer.print("Title");
			printer.print("Description");
			printer.print("Status");
			printer.print("Registered Date");
			printer.print("Last Updated Date");
			printer.print("Priority");
			printer.print("Completed Date");
			printer.println();
			for(Task task : tasks) {
				printer.print(task.getId());
				printer.print(task.getTitle());
				printer.print(task.getDescription());
				printer.print(task.getStatus());
				printer.print(task.getRegisteredDate());
				printer.print(task.getLastUpdatedDate());
				printer.print(task.getPriority());
				printer.print(task.getCompletedDate());
				printer.println();
			}
			printer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			outStream.write(Files.readAllBytes(file.toPath()));
			outStream.flush();
			outStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void exportToPDF(OutputStream outStream) {
		try {
			outStream.flush();
			outStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void exportToPPT(OutputStream outStream) {
		List<Task> tasks = findAllTasks();
		HashMap<String, Integer> statusCount = new HashMap<>();
		HashMap<Integer, Integer> priorityCount = new HashMap<>();
		for(Task task : tasks) {
			String status = task.getStatus();
			int priority = task.getPriority();			
			Integer curr = statusCount.get(status);			
			if(curr==null) {
				statusCount.put(status, 1);
			}
			else {
				statusCount.put(status, curr+1);
			}
			
			curr = priorityCount.get(priority);
			if(curr==null) {
				priorityCount.put(priority, 1);
			}
			else {
				priorityCount.put(priority, curr+1);
			}
		}
		try (XMLSlideShow pptReport = new XMLSlideShow()){
			
			//first slide
			XSLFSlide slide = pptReport.createSlide();
			//Main title
			XSLFTextShape titleBox = slide.createTextBox();
			titleBox.setPlaceholder(Placeholder.TITLE);
			titleBox.setText(Constants.REPORT_NAME);
			titleBox.setAnchor(new Rectangle(200,200,400,50));
			//Sub Title
			XSLFTextShape subTitleBox = slide.createTextBox();
			XSLFTextRun textRun = subTitleBox.addNewTextParagraph().addNewTextRun();
			textRun.setText("Author : Anubhav Sharma");
			textRun.setFontColor(java.awt.Color.RED);
			textRun.setFontSize(12.);
			subTitleBox.setAnchor(new Rectangle(200,300,400,50));
			
			//excel report
			slide = pptReport.createSlide();
			XSLFTable table = slide.createTable();
			table.setAnchor(new Rectangle(100,100,400,400));
			XSLFTableRow row = table.addRow();
			XSLFTableCell cell;
			cell = setCellBorder(row.addCell());
			cell.setText("Status");
			cell.setLineWidth(18);
			cell = setCellBorder(row.addCell());
			cell.setText("Count");
			cell.setLineWidth(18);

			row = table.addRow();
			cell = setCellBorder(row.addCell());
			cell.setText(Constants.PENDING);
			cell.setLineWidth(18);
			cell = setCellBorder(row.addCell());
			cell.setText(String.valueOf(statusCount.get(Constants.PENDING)));
			cell.setLineWidth(18);
			
			row = table.addRow();
			cell = setCellBorder(row.addCell());
			cell.setText(Constants.COMPLETED);
			cell.setLineWidth(18);
			cell = setCellBorder(row.addCell());
			cell.setText(String.valueOf(statusCount.get(Constants.COMPLETED)));
			cell.setLineWidth(18);
			
			row = table.addRow();
			cell = setCellBorder(row.addCell());
			cell.setText(Constants.WORKING);
			cell.setLineWidth(18);
			cell = setCellBorder(row.addCell());
			cell.setText(String.valueOf(statusCount.get(Constants.WORKING)));
			cell.setLineWidth(18);
			
			row = table.addRow();
			cell = setCellBorder(row.addCell());
			cell.setText(Constants.ERROR);
			cell.setLineWidth(18);
			cell = setCellBorder(row.addCell());
			cell.setText(String.valueOf(statusCount.get(Constants.ERROR)));
			cell.setLineWidth(18);

			//final slide
			slide = pptReport.createSlide();
			XSLFTextShape textBox = slide.createTextBox();
			XSLFTextRun shape = textBox.addNewTextParagraph().addNewTextRun();
			shape.setText("Home Page");
			XSLFHyperlink link = shape.createHyperlink();
			link.setAddress("http://127.0.0.1:8080/home");
			textBox.setAnchor(new Rectangle(200,200,200,200));
			
			pptReport.write(outStream);
			outStream.flush();
			outStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void exportToExcel(OutputStream outStream) {
		try(XSSFWorkbook taskReport = new XSSFWorkbook()){
			XSSFDataFormat dataFormat = taskReport.getCreationHelper().createDataFormat();
			//date cell style
			CellStyle dateCellStyle = taskReport.createCellStyle();
			dateCellStyle.setDataFormat(dataFormat.getFormat("mm/dd/yyyy hh:mm"));
			
			//header label cell style
			CellStyle headerLabelCellStyle = taskReport.createCellStyle();
			Font font = taskReport.createFont();
			font.setFontHeightInPoints((short)16);
			font.setColor(IndexedColors.DARK_RED.getIndex());
			headerLabelCellStyle.setFont(font);
			headerLabelCellStyle.setFillForegroundColor(IndexedColors.BLACK.getIndex());
			headerLabelCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			headerLabelCellStyle.setAlignment(HorizontalAlignment.CENTER);
			
			List<List<Task>> splittedTasks = splitDataToMultipleList(findAllTasks());
			int sheetInd=1;
			for(List<Task> tasks : splittedTasks) {
				XSSFSheet taskSheet = taskReport.createSheet("Task Sheet "+sheetInd);
				//Report title
				Row row = taskSheet.createRow(0);
				Cell cell = row.createCell(0);
				cell.setCellValue("Task Report");
				taskSheet.addMergedRegion(new CellRangeAddress(0,0,0,7));
				cell.setCellStyle(headerLabelCellStyle);
				int rowInd=1;
				Row header = taskSheet.createRow(rowInd++);
				int colInd=0;
				for(String headerLabel : Constants.TASK_HEADERS) {
					cell = header.createCell(colInd++);
					cell.setCellValue(headerLabel);
					cell.setCellStyle(dateCellStyle);
				}
				colInd=0;
				for(Task task : tasks) {
					row = taskSheet.createRow(rowInd++);					
					cell = row.createCell(colInd++);
					cell.setCellValue(task.getId());
					cell = row.createCell(colInd++);
					cell.setCellValue(task.getTitle());
					cell = row.createCell(colInd++);
					cell.setCellValue(task.getDescription());
					cell = row.createCell(colInd++);
					cell.setCellValue(task.getPriority());
					cell = row.createCell(colInd++);
					cell.setCellValue(task.getStatus());
					cell = row.createCell(colInd++);
					cell.setCellValue(task.getRegisteredDate());
					cell.setCellStyle(dateCellStyle);
					cell = row.createCell(colInd++);
					cell.setCellValue(task.getLastUpdatedDate());
					cell.setCellStyle(dateCellStyle);
					cell = row.createCell(colInd);
					cell.setCellValue(task.getCompletedDate());
					cell.setCellStyle(dateCellStyle);
					colInd=0;
				}
				sheetInd++;
			}
			taskReport.write(outStream);
			outStream.flush();
			outStream.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}

	public XSLFTableCell setCellBorder(XSLFTableCell cell) {
		cell.setBorderColor(BorderEdge.top, java.awt.Color.red);
		cell.setBorderColor(BorderEdge.left, java.awt.Color.red);
		cell.setBorderColor(BorderEdge.right, java.awt.Color.red);
		cell.setBorderColor(BorderEdge.bottom, java.awt.Color.red);
		cell.setBorderCap(BorderEdge.top, StrokeStyle.LineCap.FLAT);
		cell.setBorderCap(BorderEdge.left, StrokeStyle.LineCap.FLAT);
		cell.setBorderCap(BorderEdge.right, StrokeStyle.LineCap.FLAT);
		cell.setBorderCap(BorderEdge.bottom, StrokeStyle.LineCap.FLAT);
		cell.setBorderWidth(BorderEdge.top, 2);
		cell.setBorderWidth(BorderEdge.left, 2);
		cell.setBorderWidth(BorderEdge.right, 2);
		cell.setBorderWidth(BorderEdge.bottom, 2);
		return cell;
	}
	
	private List<List<Task>> splitDataToMultipleList(List<Task> tasks) {
		List<List<Task>> splittedTasks = new ArrayList<>();
		int size = tasks.size();
		int start=0;
		int end=Constants.MAX_EXCEL_ROWS;
		while(end<size) {
			splittedTasks.add(tasks.subList(start, end));
			start+=Constants.MAX_EXCEL_ROWS;
			end+=Constants.MAX_EXCEL_ROWS;
		}
		splittedTasks.add(tasks.subList(start, size));
		return splittedTasks;
	}

	public String getContentType(String action) {
		return Constants.CONTENT_TYPE_MAP.get(action);
	}

	public String getFileName(String action) {
		return Constants.REPORT_NAME+"_"+getDate()+Constants.FILE_TYPE_MAP.get(action);
	}

	public String getDate() {
		return Date.valueOf(LocalDate.now()).toString().replace("-", "_");
	}

	public void createNewTask(Task task) {
		task.setStatus("PENDING");
		task.setRegisteredDate(new Date(System.currentTimeMillis()));
		task.setLastUpdatedDate(task.getRegisteredDate());
		taskRepository.save(task);
	}

	public void deleteTaskById(Task task) {
		if(task==null || task.getId()==0) return ;
		taskRepository.deleteById(task.getId());
	}

	public void updateTaskStatus(String status, String id) {
		if(Strings.isBlank(status) 
				|| !Pattern.matches("\\d{1,15}", id) 
				|| !Constants.STATUS_LIST.contains(status)) return ;
		Optional<Task> optionalTask = taskRepository.findById(Long.parseLong(id));
		if(optionalTask.isPresent()) {
			Task task = optionalTask.get();
			task.setStatus(status);
			taskRepository.save(task);
		}
	}
}
