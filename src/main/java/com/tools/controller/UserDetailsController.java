package com.tools.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.tools.beans.AppDropdown;
import com.tools.beans.Tasks;
import com.tools.beans.UserDetails;
import com.tools.service.ProjectUtiliyService;
import com.tools.service.TaskDetailsService;
import com.tools.service.UsersService;

@Controller
@RequestMapping("/")
public class UserDetailsController {

	@Autowired
	UsersService userService;
	
	@Autowired
	TaskDetailsService taskDetailsService;
	
	@Autowired
	ProjectUtiliyService projectUtilityService;

	@RequestMapping(value = { "/", "/home" }, method = RequestMethod.GET)
	public String homePage(ModelMap model) {
		Map<String,Integer> dashboardCounts=taskDetailsService.getTaskStats();
		model.addAttribute("NewTask", dashboardCounts.get("New"));
		model.addAttribute("InProgressTask",dashboardCounts.get("InProgress"));
		model.addAttribute("ClosedTask", dashboardCounts.get("Closed"));
		model.addAttribute("TotalTask", dashboardCounts.get("Total"));
		return "index";
	}

	@RequestMapping(value = "/admin", method = RequestMethod.GET)
	public String adminPage(ModelMap model) {
		model.addAttribute("user", getPrincipal());
		return "admin";
	}

	@RequestMapping(value = "/db", method = RequestMethod.GET)
	public String dbaPage(ModelMap model) {
		model.addAttribute("user", getPrincipal());
		return "dba";
	}

	@RequestMapping(value = "/Access_Denied", method = RequestMethod.GET)
	public String accessDeniedPage(ModelMap model) {
		model.addAttribute("user", getPrincipal());
		return "accessDenied";
	}

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String loginPage() {
		return "login";
	}
	
	@RequestMapping(value = "/newTask", method = RequestMethod.GET)
	public String newTask(ModelMap model) {
		Tasks task= new Tasks();
		model.addAttribute("task", task);
		List<AppDropdown> allDropdowns=projectUtilityService.getAllAppDropdown();
		
		Map<String,String> taskType=new HashMap<String,String>();
		Map<String,String> projectName=new HashMap<String,String>();
		Map<String,String> taskStatus=new HashMap<String,String>();
		
		for (AppDropdown appDropdown : allDropdowns) {
			if("TaskType".equalsIgnoreCase(appDropdown.getDropdownName())) {
				taskType.put(appDropdown.getDropdownKey(), appDropdown.getDropdownValue());
			}
			else if("ProjectName".equalsIgnoreCase(appDropdown.getDropdownName())) {
				projectName.put(appDropdown.getDropdownKey(), appDropdown.getDropdownValue());
			}
			else if("TaskStatus".equalsIgnoreCase(appDropdown.getDropdownName())) {
				taskStatus.put(appDropdown.getDropdownKey(), appDropdown.getDropdownValue());
			}	
		}		
		model.addAttribute("taskType", taskType);
		model.addAttribute("projectName", projectName);
		model.addAttribute("taskStatus", taskStatus);
		
		return "newTask";
	}
	
	@RequestMapping(value = "/newTask", method = RequestMethod.POST)
	public String saveTask(Tasks task, BindingResult result, ModelMap model) {
		
		if (result.hasErrors()) {
			System.out.println("There are errors");
			return "taskDetails";
		}
		taskDetailsService.addNewTask(task);
		model.addAttribute("success", "task has been registered successfully");
		
		return "taskDetails";
	}
	
	@RequestMapping(value = "/taskDetails", method = RequestMethod.GET)
	public String taskDetails(ModelMap model) {		
		List<Tasks> task=taskDetailsService.getAllTasks();
		model.addAttribute("task", task);
		return "taskDetails";
	}
	
	@RequestMapping(value = "/totalTasksDayWise", method = RequestMethod.GET)
	@ResponseBody
	public Map<String,Integer>  totalTasksDayWise() {
		Map<String,Integer> task=taskDetailsService.getTotalTasksDayWise();
		return task;
	}
	
	@RequestMapping(value = "/totalTaskTypeWiseWork", method = RequestMethod.GET)
	@ResponseBody
	public Map<String,Integer>  totalTaskTypeWiseWork() {
		Map<String,Integer> task=taskDetailsService.getTaskWiseTotalWork();
		return task;
	}

	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public String logoutPage(HttpServletRequest request, HttpServletResponse response) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null) {
			new SecurityContextLogoutHandler().logout(request, response, auth);
		}
		return "redirect:/login?logout";
	}

	@RequestMapping(value = "/newUser", method = RequestMethod.GET)
	public String newRegistration(ModelMap model) {
		UserDetails user = new UserDetails();
		model.addAttribute("user", user);
		return "newuser";
	}

	@RequestMapping(value = "/newUser", method = RequestMethod.POST)
	public String saveRegistration(UserDetails userDetails, BindingResult result, ModelMap model) {

		if (result.hasErrors()) {
			System.out.println("There are errors");
			return "newuser";
		}
		userService.addUserDetails(userDetails);

		model.addAttribute("success", "User " + userDetails.getUserId() + " has been registered successfully");
		return "registrationsuccess";
	}

	private String getPrincipal() {
		String userName = null;
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		if (principal instanceof UserDetails) {
			userName = ((UserDetails) principal).getUserId();
		} else {
			userName = principal.toString();
		}
		return userName;
	}
}
