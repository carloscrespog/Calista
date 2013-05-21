package gsi.calista.beans;

import java.io.FileInputStream;
import java.io.Serializable;
import java.util.Properties;

import com.pi4j.device.Device;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

import gsi.calista.actions.RaspberryPI;
import gsn.beans.DataField;
import gsn.beans.DataTypes;
import gsn.beans.StreamElement;
import gsn.wrappers.AbstractWrapper;


// CLASS FOR CONTROLLING SENSOR WITH 2 DIFFERENT STATES ( 1 o 0 ).

// UNCOMMENT LINES FOR USING RASPBERRY
public class BinarySensor implements DeviceAbstract {

	private String name;

	private int state;
	private int lastState;
	private GpioPinDigitalInput pinR;

	//private final GpioController gpio = GpioFactory.getInstance();

	private static final String FIELD_NAME_STATE = "STATE";
	private static final String FIELD_NAME_LAST_STATE = "LAST_STATE";

	private static final String[] FIELD_NAMES = new String[] {
			FIELD_NAME_STATE, FIELD_NAME_LAST_STATE };

	private static final Byte[] datatypes = new Byte[] { DataTypes.INTEGER,
			DataTypes.INTEGER };

	private static DataField[] outputFormat = new DataField[] {
			new DataField(FIELD_NAME_STATE, "integer", "Device state."),
			new DataField(FIELD_NAME_LAST_STATE, "integer", "State last state.") };

	public BinarySensor(String name) {
		this.name = name;
		this.state = 0;
		this.lastState = 0;

		try {
			Properties prop = new Properties();
			prop.load(new FileInputStream("conf/calista.properties"));
//			Pin pin_in = RaspberryPI.int2Pin(Integer.getInteger(prop
//					.getProperty(name+"_in")));
//			pinR = gpio.provisionDigitalInputPin(pin_in,
//					PinPullResistance.PULL_DOWN);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public String getName() {
		return name;
	}

	public StreamElement getData() {

		StreamElement streamElement = new StreamElement(FIELD_NAMES, datatypes,
				new Serializable[] { state, lastState },
				System.currentTimeMillis());

		return streamElement;
	}

	public DataField[] getOutputFormat() {
		return outputFormat;
	}

	public boolean updateData() {

		lastState = state;

		// PinState pinstate = pinR.getState();
		//
		// if (pinstate == PinState.LOW) {
		// state = 0;
		// } else if (pinstate == PinState.HIGH) {
		// state = 1;
		// }

		if (Math.random() > 0.9) {
			state = 1;
		} else {
			state = 0;
		}

		return true;

	}

	public GpioPinDigitalInput getPin() {
		return pinR;
	}

}
