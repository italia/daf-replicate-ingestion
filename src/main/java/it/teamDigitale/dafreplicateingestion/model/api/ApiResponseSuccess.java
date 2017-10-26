/**
 * 
 */
package it.teamDigitale.dafreplicateingestion.model.api;

/**
 * @author alessandro
 *
 */
public class ApiResponseSuccess extends ApiResponse {
	
	/**
	 * @param data
	 * @param timestamp
	 */
	public ApiResponseSuccess(Object data, long timestamp) {
		super("success", data, timestamp);
	}
}
