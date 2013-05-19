package gsi.calista.beans;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.Properties;

import com.pi4j.io.gpio.PinState;

import gsi.calista.actions.RaspberryPI;
import gsn.beans.DataField;
import gsn.beans.DataTypes;
import gsn.beans.StreamElement;

public class Actuator implements DeviceAbstract {

	private String name;
	private final DeviceType type = DeviceType.ACTUATOR;
	private int pin; 
	private int state;
	private int lastState;
	private static final String FIELD_NAME_STATE = "STATE";
	private static final String FIELD_NAME_LAST_STATE = "LAST_STATE";

	private static final String[] FIELD_NAMES = new String[] {
			FIELD_NAME_STATE, FIELD_NAME_LAST_STATE };

	private static final Byte[] datatypes = new Byte[] { DataTypes.INTEGER,
			DataTypes.INTEGER};

	private static DataField[] outputFormat = new DataField[] {
			new DataField(FIELD_NAME_STATE, "integer", "Actuator state."),
			new DataField(FIELD_NAME_LAST_STATE, "integer", "Actuator last state.") };

	public Actuator(String name) {
		this.name = name;
		this.state=0;
		this.lastState=0;
		Properties prop= new Properties();
		try {
			prop.load(new FileInputStream("config/calista.properties"));
			
			this.pin=Integer.parseInt( prop.getProperty(name+"_out"));
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getName() {
		return name;
	}

	public StreamElement getData() {

		StreamElement streamElement = new StreamElement(FIELD_NAMES, datatypes,
				new Serializable[] {
						state,
						lastState },
				System.currentTimeMillis());

		return streamElement;
	}

	public DataField[] getOutputFormat() {
		return outputFormat;
	}

	public DeviceType getType() {
		return type;
	}

	

	public boolean updateData() {
		lastState=state;
		
		if(Math.random()>0.5){
			if(state==1) state=0;
			else state=1;
		}
		
		return true;
	
	}
	
	public boolean setDigitalValue(int value){
		RaspberryPI raspi = new RaspberryPI();
		try{
			if(value==1){
				raspi.setDigitalValue(RaspberryPI.int2Pin(pin), PinState.HIGH);
			}else{
				raspi.setDigitalValue(RaspberryPI.int2Pin(pin), PinState.LOW);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		
		return true;
	}
	
	

	

}
