package org.aisen.wen.component.network.task;

public interface IExceptionDelegate {

	void verifyResponse(String response) throws TaskException;

	String code2msg(String code);

}
