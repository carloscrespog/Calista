package gsi.calista.actions;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class TwitterJ {

	private  static String CONSUMER_KEY;
	private  static String CONSUMER_KEY_SECRET;
	private  static String ACCESS_TOKEN_SECRET;
	private  static String ACCESS_TOKEN;

	public static void sendFirstTweet(String tweet) throws TwitterException,
			IOException {

		Twitter twitter = new TwitterFactory().getInstance();
		setProperties();
		twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_KEY_SECRET);
		RequestToken requestToken = twitter.getOAuthRequestToken();
		System.out.println("Authorization URL: \n"
				+ requestToken.getAuthorizationURL());

		AccessToken accessToken = null;

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while (null == accessToken) {
			try {
				System.out.print("Input PIN here: ");
				String pin = br.readLine();

				accessToken = twitter.getOAuthAccessToken(requestToken, pin);

			} catch (TwitterException te) {

				System.out.println("Failed to get access token, caused by: "
						+ te.getMessage());

				System.out.println("Retry input PIN");

			}
		}

		System.out.println("Access Token: " + accessToken.getToken());
		System.out.println("Access Token Secret: "
				+ accessToken.getTokenSecret());

		twitter.updateStatus(tweet);

	}

	public static void sendTweet(String tweet) throws TwitterException,
			IOException {
		
		Twitter twitter = new TwitterFactory().getInstance();
		setProperties();
		twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_KEY_SECRET);
		
		// here's the difference
		String accessToken = getSavedAccessToken();
		String accessTokenSecret = getSavedAccessTokenSecret();
		AccessToken oathAccessToken = new AccessToken(accessToken,
				accessTokenSecret);

		twitter.setOAuthAccessToken(oathAccessToken);
		// end of difference

		twitter.updateStatus(tweet);

	}

	private static String getSavedAccessTokenSecret() {
		// consider this is method to get your previously saved Access Token
		// Secret
		return ACCESS_TOKEN_SECRET;
	}

	private static String getSavedAccessToken() {
		// consider this is method to get your previously saved Access Token
		return ACCESS_TOKEN;
	}
	
	private static void setProperties(){
		Properties prop= new Properties();
		try {
			prop.load(new FileInputStream("config/calista.properties"));
			ACCESS_TOKEN=prop.getProperty("access_token");
			ACCESS_TOKEN_SECRET=prop.getProperty("access_token_secret");
			CONSUMER_KEY=prop.getProperty("consumer_key");
			CONSUMER_KEY_SECRET=prop.getProperty("consumer_key_secret");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
