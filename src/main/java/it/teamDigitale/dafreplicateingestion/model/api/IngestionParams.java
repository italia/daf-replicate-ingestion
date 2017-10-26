/**
 * 
 */
package it.teamDigitale.dafreplicateingestion.model.api;

import java.util.List;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import it.teamDigitale.dafreplicateingestion.converter.ConverterType;

/**
 * @author alessandro
 *
 */
public class IngestionParams {
	@JsonProperty("services")
	@NotNull
	private List<String> services;
	@JsonProperty("topic")
	@NotNull
	private String topic;
	@JsonProperty("service-type")
	@NotNull
	private ConverterType serviceType;

	/**
	 * @param services
	 * @param topic
	 */
	public IngestionParams(List<String> services, String topic, ConverterType serviceType) {
		this.services = services;
		this.topic = topic;
		this.serviceType = serviceType;
	}
	
	public IngestionParams() {
		
	}

	/**
	 * @return the services
	 */
	public List<String> getServices() {
		return services;
	}

	/**
	 * @param services
	 *            the services to set
	 */
	public void setServices(List<String> services) {
		this.services = services;
	}

	/**
	 * @return the topic
	 */
	public String getTopic() {
		return topic;
	}

	/**
	 * @param topic
	 *            the topic to set
	 */
	public void setTopic(String topic) {
		this.topic = topic;
	}

	/**
	 * @return the serviceType
	 */
	public ConverterType getServiceType() {
		return serviceType;
	}

	/**
	 * @param serviceType the serviceType to set
	 */
	public void setServiceType(ConverterType serviceType) {
		this.serviceType = serviceType;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((serviceType == null) ? 0 : serviceType.hashCode());
		result = prime * result + ((services == null) ? 0 : services.hashCode());
		result = prime * result + ((topic == null) ? 0 : topic.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof IngestionParams)) {
			return false;
		}
		IngestionParams other = (IngestionParams) obj;
		if (serviceType != other.serviceType) {
			return false;
		}
		if (services == null) {
			if (other.services != null) {
				return false;
			}
		} else if (!services.equals(other.services)) {
			return false;
		}
		if (topic == null) {
			if (other.topic != null) {
				return false;
			}
		} else if (!topic.equals(other.topic)) {
			return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "IngestionParams [services=" + services + ", topic=" + topic + ", serviceType=" + serviceType + "]";
	}
}
