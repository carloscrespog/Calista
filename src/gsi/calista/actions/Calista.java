package gsi.calista.actions;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;

import twitter4j.TwitterException;

import com.jcraft.jsch.JSchException;

public class Calista {
	
	public static boolean turnOnComputer(String ip, String mac) {
		String cmd="wakeonlan -i "+ip+" -p 7 "+mac;
		try {
		    // Run ls command
		   Runtime.getRuntime().exec(cmd);
		} catch (Exception e) {
			  e.printStackTrace(System.err);
		}
		  return true;
	}

	public static boolean shutdownComputer(String ipAddress, String root,
			String rootpassword) {

		try {
			SSHClient.exec(ipAddress, root, rootpassword, "sudo shutdown now");
			System.out.println("Computer turned off: " + ipAddress);
		} catch (JSchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;

		} catch (NullPointerException e) {
			e.printStackTrace();
			return true;
		}
		return true;
	}

	public static Clip play(String file) {

		Clip clip = null;
		File soundFile = new File(file);
		AudioInputStream sound;
		try {
			sound = AudioSystem.getAudioInputStream(soundFile);
			// load the sound into memory (a Clip)
			DataLine.Info info = new DataLine.Info(Clip.class,
					sound.getFormat());
			clip = (Clip) AudioSystem.getLine(info);
			clip.open(sound);

			// due to bug in Java Sound, explicitly exit the VM when
			// the sound has stopped.
			clip.addLineListener(new LineListener() {
				public void update(LineEvent event) {
					if (event.getType() == LineEvent.Type.STOP) {
						event.getLine().close();
						System.exit(0);
					}
				}
			});

			// play the sound clip
			clip.start();
			System.out.println("Playing sound:" + file);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return clip;
	}

	public static boolean sendEmail(String from, String to, String title,
			String content) {

		Properties prop = new Properties();

		try {
			// load a properties file
			prop.load(new FileInputStream("config/calista.properties"));

			// get the property value and print it out

			final String username = prop.getProperty("gmail-user");
			final String password = prop.getProperty("gmail-password");

			Properties props = new Properties();
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.host", "smtp.gmail.com");
			props.put("mail.smtp.port", "587");

			Session session = Session.getInstance(props,
					new javax.mail.Authenticator() {
						protected PasswordAuthentication getPasswordAuthentication() {
							return new PasswordAuthentication(username,
									password);
						}
					});

			try {

				Message message = new MimeMessage(session);
				message.setFrom(new InternetAddress(from));
				message.setRecipients(Message.RecipientType.TO,
						InternetAddress.parse(to));
				message.setSubject(title);
				message.setText(content);

				Transport.send(message);

				System.out.println("Email sent// FROM: " + from + " TO: " + to
						+ " TITLE: " + " CONTENT: " + content);

			} catch (MessagingException e) {
				e.printStackTrace();
				return false;
			}

		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return true;
	}

	public static boolean sendTweet(String tweet) {
		try {
			System.out.println("Sending tweet...");
			TwitterJ.sendTweet(tweet);
			System.out.println("Tweet send: " + tweet);
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;

	}
	


	public static boolean sendFirstTweet(String tweet) {
		try {
			TwitterJ.sendFirstTweet(tweet);
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;

	}

	
		
		
	}


