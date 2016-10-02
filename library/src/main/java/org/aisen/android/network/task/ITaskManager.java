package org.aisen.android.network.task;

public interface ITaskManager {

	void addTask(WorkTask task);

	void removeTask(String taskId, boolean cancelIfRunning);

	void removeAllTask(boolean cancelIfRunning);
	
	int getTaskCount(String taskId);

	void clearTaskCount(String taskId);
	
}
