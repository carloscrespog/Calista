package gsi.calista.beans;

import java.io.FileInputStream;
import java.io.Serializable;
import java.util.Properties;
import com.pi4j.io.gpio.Pin;
import gsi.calista.actions.RaspberryPI;
import gsn.beans.DataField;
import gsn.beans.DataTypes;
import gsn.beans.StreamElement;


//UNCOMMENT LINES FOR USING RASPBERRY PI


public class Sensor implements DeviceAbstract {

	private String name;

	private Double value;

	private Pin pin_in;

	private static final String FIELD_NAME_VALUE = "VALUE";

	private static final String[] FIELD_NAMES = new String[] { FIELD_NAME_VALUE };

	private static final Byte[] datatypes = new Byte[] { DataTypes.DOUBLE };

	private static DataField[] outputFormat = new DataField[] { new DataField(
			FIELD_NAME_VALUE, "double", "Magnitude value") };

	public Sensor(String name) {
		this.name = name;
		try {
			Properties prop = new Properties();
			prop.load(new FileInputStream("conf/calista.properties"));
//			this.pin_in = RaspberryPI.int2Pin(Integer.parseInt(prop
//					.getProperty(name+"_in")));
		} catch (Exception e) {
			System.out.println(name+"_in");
			e.printStackTrace();
		}
	}

	public String getName() {
		return name;
	}

	public StreamElement getData() {

		StreamElement streamElement = new StreamElement(FIELD_NAMES, datatypes,
				new Serializable[] { value }, System.currentTimeMillis());

		return streamElement;
	}

	public DataField[] getOutputFormat() {
		return outputFormat;
	}

	@Override
	public boolean updateData() {
//		RaspberryPI raspi = new RaspberryPI();
//		value = raspi.getAnalogValue(pin_in);
//		raspi.shutdownPinController();
		
		value= Math.random()*100;
		return true;
	}

}
