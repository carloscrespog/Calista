package gsi.calista.actions;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinAnalogInput;
import com.pi4j.io.gpio.GpioPinAnalogOutput;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinMode;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

public class RaspberryPI {

	final GpioController gpio;
	


	public RaspberryPI() {
		gpio = GpioFactory.getInstance();

	}

	public PinState getDigitalValue(Pin pin) {
		// provision gpio pin #01 as an output pin and turn on
		GpioPinDigitalInput pinR = gpio.provisionDigitalInputPin((Pin) pin);
		PinState value = pinR.getState();
		gpio.unprovisionPin(pinR);
		//System.out.println("--> GPIO state should be: ON");

		return value;
	}

	// Digital pins

	public void setDigitalValue(Pin pin, PinState value) {
		// provision gpio pin #01 as an output pin and turn on
		GpioPinDigitalOutput pinR = gpio.provisionDigitalOutputPin((Pin) pin);
		pinR.setState(value);
		//pinR.setShutdownOptions(true, PinState.LOW, PinPullResistance.OFF);
		gpio.unprovisionPin(pinR);
		//System.out.println("--> GPIO state should be: ON");
	}

	public void toogle(Pin pin) {

		GpioPinDigitalOutput pinR = gpio.provisionDigitalOutputPin((Pin) pin);
		
		
		pinR.toggle();
		pinR.setShutdownOptions(true, PinState.LOW, PinPullResistance.OFF);
		gpio.unprovisionPin(pinR);
		
	}

	// Analog pins

	public void setAnalagValue(Pin pin, double value, PinMode mode) {
		// provision gpio pin #01 as an output pin and turn on
		GpioPinAnalogOutput pinR = gpio.provisionAnalogOutputPin((Pin) pin);
		pinR.setValue(value);
		pinR.setShutdownOptions(true, PinState.LOW, PinPullResistance.OFF);
		gpio.unprovisionPin(pinR);
		
	}

	public double getAnalogValue(Pin pin) {
		// provision gpio pin #01 as an output pin and turn on
		GpioPinAnalogInput pinR = gpio.provisionAnalogInputPin((Pin) pin);
		gpio.unprovisionPin(pinR);
		double value = pinR.getValue();
		return value;
	}

	public void shutdownPinController() {
		gpio.shutdown();
	}

	public static Pin int2Pin(int pin) throws Exception {
		Pin rpin = null;
		switch (pin) {
		case 0:
			rpin = RaspiPin.GPIO_00;
			break;
		case 1:
			rpin = RaspiPin.GPIO_01;
			break;
		case 2:
			rpin = RaspiPin.GPIO_02;
			break;
		case 3:
			rpin = RaspiPin.GPIO_03;
			break;
		case 4:
			rpin = RaspiPin.GPIO_04;

			break;
		case 5:
			rpin = RaspiPin.GPIO_05;
			break;
		case 6:
			rpin = RaspiPin.GPIO_06;
			break;
		case 7:
			rpin = RaspiPin.GPIO_07;
			break;
		case 8:
			rpin = RaspiPin.GPIO_08;
			break;
		case 9:
			rpin = RaspiPin.GPIO_09;
			break;
		case 10:
			rpin = RaspiPin.GPIO_10;
			break;
		case 11:
			rpin = RaspiPin.GPIO_11;
			break;
		case 12:
			rpin = RaspiPin.GPIO_12;
			break;
		case 13:
			rpin = RaspiPin.GPIO_13;
			break;
		case 14:
			rpin = RaspiPin.GPIO_14;
			break;
		case 15:
			rpin = RaspiPin.GPIO_15;
			break;
		case 16:
			rpin = RaspiPin.GPIO_16;
			break;
		case 17:
			rpin = RaspiPin.GPIO_17;
			break;
		case 18:
			rpin = RaspiPin.GPIO_18;
			break;
		case 19:
			rpin = RaspiPin.GPIO_19;
			break;
		default:

			throw new Exception("Invalid pin value in calista.properties:"
					+ pin);

		}
		return rpin;
	}
}
