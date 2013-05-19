package gsi.calista.beans;

import java.io.FileInputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Properties;

import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.wiringpi.Gpio;

import gsi.calista.actions.RaspberryPI;
import gsn.beans.DataField;
import gsn.beans.DataTypes;
import gsn.beans.StreamElement;

public class Door implements DeviceAbstract {

	private String name;
	private Pin pin_out;
	private Pin pin_in;
	private int state;
	private int lastState;
	private static final String FIELD_NAME_STATE = "STATE";
	private static final String FIELD_NAME_LAST_STATE = "LAST_STATE";

	private static final String[] FIELD_NAMES = new String[] {
			FIELD_NAME_STATE, FIELD_NAME_LAST_STATE };

	private static final Byte[] datatypes = new Byte[] { DataTypes.INTEGER,
			DataTypes.INTEGER };

	private static DataField[] outputFormat = new DataField[] {
			new DataField(FIELD_NAME_STATE, "integer", "Door state."),
			new DataField(FIELD_NAME_LAST_STATE, "integer", "Door last state.") };

	public Door(String name) {
		this.name = name;
		this.state = 0;
		this.lastState = 0;
		try {
			Properties prop = new Properties();
			prop.load(new FileInputStream("conf/calista.properties"));
//			this.pin_in = RaspberryPI.int2Pin(Integer.parseInt(prop
//					.getProperty(name+ "_in") ));
//			this.pin_out = RaspberryPI.int2Pin(Integer.parseInt(prop
//					.getProperty(name+ "_out") ));
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
//		RaspberryPI raspi = new RaspberryPI();
//		PinState pinstate = raspi.getDigitalValue(pin_in);
//		raspi.shutdownPinController();
//		if (pinstate == PinState.LOW) {
//			state = 0;
//		} else if (pinstate == PinState.HIGH) {
//			state = 1;
//		}

		if (Math.random() > 0.9) {
			state = 1;
		} else {
			state = 0;
		}
		return true;

	}

	public void open() {
//		RaspberryPI raspi = new RaspberryPI();
//		raspi.setDigitalValue(pin_out, PinState.HIGH);
//		try {
//			Thread.sleep(5000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		raspi.setDigitalValue(pin_out, PinState.LOW);
//		raspi.shutdownPinController();
//		lastState = state;
//		state = 1;
	}
	
	
	

}
