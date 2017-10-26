/**
 * 
 */
package it.teamDigitale.dafreplicateingestion.config;

import java.util.concurrent.Executor;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import it.teamDigitale.dafreplicateingestion.exception.AsyncExceptionHandler;

/**
 * @author alessandro
 *
 */
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {
	private final int POOL_SIZE = 5;
	
	@Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(POOL_SIZE);
        executor.setThreadNamePrefix("AsyncThreads-");
        executor.initialize();
        return executor;
    }

	@Override
	public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
		return new AsyncExceptionHandler();
	}
     
}
