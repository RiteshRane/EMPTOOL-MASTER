package com.tools.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import com.tools.beans.Tasks;

@Service("TaskDetailsService")
public interface TaskDetailsService {

	public int addNewTask(Tasks task);
	public List<Tasks> getAllTasks();
	public Tasks getTaskByName(String taskName);
	public Tasks getTaskById(Integer taskId);
	public Map<String,Integer> getTaskStats();
	public Map<String,Integer> getTotalTasksDayWise();
	public Map<String,Integer> getTaskWiseTotalWork();
}
