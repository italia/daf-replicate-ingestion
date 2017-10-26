/**
 * 
 */
package it.teamDigitale.dafreplicateingestion.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import it.teamDigitale.dafreplicateingestion.model.api.IngestionParams;

/**
 * @author alessandro
 *
 */
@Service
public class ApiIngestionService extends AbstractIngestionService {

	@Async
	@Override
	public void ingest(IngestionParams ingestion) {
		super.ingest(ingestion);
	}
}
