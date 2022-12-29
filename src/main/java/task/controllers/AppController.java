package task.controllers;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import jakarta.servlet.http.HttpServletResponse;
import task.models.Task;
import task.services.Constants;
import task.services.TaskService;

@Controller
public class AppController {
	
	@Autowired
	TaskService taskService;

	@GetMapping("/home")
	public String home(Model model) {
		model.addAttribute("allTasks", taskService.fetchTasksToDisplay());
		model.addAttribute("newTask", new Task());
		return Constants.HOME;
	}

	@PostMapping(value="/task", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public String createNewTask(Task task) {
		taskService.createNewTask(task);
		return Constants.REDIRECT_HOME;
	}
	
	@PostMapping(value="/deleteTask", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public String deleteTask(Task task) {
		taskService.deleteTaskById(task);
		return Constants.REDIRECT_HOME;
	}
	
	@GetMapping(value="/updateStatus/{id}/{action}")
	public String updateStatus(@PathVariable("action") String action, @PathVariable("id") String id) {
		taskService.updateTaskStatus(action, id);
		return Constants.REDIRECT_HOME;
	}
	
	@GetMapping("/download/{action}")
	public void download(Model model, @PathVariable(value="action", required=true) String action, HttpServletResponse response) {
		if(!Strings.isBlank(action)) {
			if(action.equals(Constants.CREATE_NEW_TASK)) {
				model.addAttribute(Constants.CREATE_NEW_TASK, Constants.CREATE_NEW_TASK);
			}
			else {
				response.setContentType(taskService.getContentType(action));
		        response.setHeader("Content-Disposition", "attachment;filename="+taskService.getFileName(action));
		        try {
					OutputStream outputStream = response.getOutputStream();
					taskService.handleAction(action, outputStream);
					outputStream.flush();
					outputStream.close();
				}catch (IOException e) {
					e.printStackTrace();
				}
			}			
		}		
	}
}
