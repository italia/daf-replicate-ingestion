/**
 * 
 */
package it.teamDigitale.dafreplicateingestion.converter;

/**
 * @author alessandro
 *
 */
public enum ConverterType implements ConverterBuilder {
	PARKING {
        public Converter buildConverter() { return new ParkingConverterImpl(); }
    }
}
