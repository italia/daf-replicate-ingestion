/**
 * 
 */
package it.teamDigitale.dafreplicateingestion.exception;

import java.lang.reflect.Method;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

/**
 * @author alessandro
 *
 */
public class AsyncExceptionHandler implements AsyncUncaughtExceptionHandler {
	protected final Logger LOGGER = LoggerFactory.getLogger(getClass());

	@Override
	public void handleUncaughtException(Throwable throwable, Method method, Object... obj) {
		LOGGER.error(ExceptionUtils.getStackTrace(throwable));
	}

}
