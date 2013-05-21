package gsi.calista.wrappers;

import java.util.HashMap;
import gsi.calista.beans.*;
import gsn.beans.AddressBean;
import gsn.beans.DataField;
import gsn.wrappers.AbstractWrapper;
import org.apache.log4j.Logger;

import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

public class RaspberryPIWrapper extends AbstractWrapper {

	private final transient Logger logger = Logger
			.getLogger(RaspberryPIWrapper.class);

	private static int threadCounter = 0;

	private transient DeviceAbstract device;

	private static HashMap<String, DeviceAbstract> devices = new HashMap<String, DeviceAbstract>();

	private long rate = 1000;

	// Specified params in the VSD

	private AddressBean params;


	private Boolean pool = false;

	public boolean initialize() {

		params = getActiveAddressBean();

		if (params.getPredicateValue("rate") != null) {
			rate = (long) Integer.parseInt(params.getPredicateValue("rate"));

			logger.info("Sampling rate set to "
					+ params.getPredicateValue("rate") + " msec.");
		}

		if (params.getPredicateValue("pool") != null) {
			pool = Boolean.parseBoolean(params.getPredicateValue("pool"));

			logger.info("Sampling rate set to "
					+ params.getPredicateValue("rate") + " msec.");
		}

		setName("Device " + (++threadCounter));

		device = createDevice();
		
		if (device != null) {
			devices.put(device.getName(), device);
			return true;
		}
		return false;

	}

	public void run() {

		while (isActive()) {

			try {
				// delay for rate sampling
				Thread.sleep(rate);
			} catch (InterruptedException e) {
				logger.error(e.getMessage(), e);
			}

			// Listen for change events
			
			if (pool) {

				((BinarySensor) device).getPin().addListener(
						new GpioPinListenerDigital() {
							@Override
							public void handleGpioPinDigitalStateChangeEvent(
									GpioPinDigitalStateChangeEvent event) {
								// display pin state on console
								if (device.updateData()) {
									postStreamElement(device.getData());
									System.out
											.println(" --> GPIO PIN STATE CHANGE: "
													+ event.getPin()
													+ " = "
													+ event.getState());
								}

							}

						});

			} else {
				if (device.updateData()) {
					postStreamElement(device.getData());
				} else {
					System.out.println("Unable to gather device data");
				}
			}

		}

	}

	public void dispose() {
		threadCounter--;
	}

	/**
	 * The output fields exported by this virtual sensor.
	 * 
	 * @return The strutucture of the output.
	 */

	public final DataField[] getOutputFormat() {
		return device.getOutputFormat();
	}

	public String getWrapperName() {
		return "Raspberry PI wrapper";
	}

	public static DeviceAbstract findDeviceById(String name) {
		return devices.get(name);
	}

	private DeviceAbstract createDevice() {

		String name = params.getPredicateValue("device");
		String type = params.getPredicateValue("type");
		if ((name != null) && (type != null)) {
			DeviceAbstract dev;
			if (type.equals("actuator")) {
				dev = new Actuator(params.getPredicateValue("device"));
			}

			else if (type.equals("sensor")) {
				dev = new Sensor(name);
			} else if (type.equals("computer")) {
				dev = new Computer(name, params.getPredicateValue("ipaddress"),
						params.getPredicateValue("macaddress"));
			} else if (type.equals("door")) {
				dev = new Door(name);
			}

			else {
				dev = new BinarySensor(name);
			}

			return dev;

		} else {

			System.out
					.println("Empty device name or device type field in the virtual sensor description");
			return null;
		}

	}
}
