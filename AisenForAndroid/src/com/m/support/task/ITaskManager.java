package com.m.support.task;

public interface ITaskManager {

	public void addTask(WorkTask task);

	public void removeTask(String taskId, boolean cancelIfRunning);

	public void removeAllTask(boolean cancelIfRunning);
	
	public int getTaskCount(String taskId);
	
}
