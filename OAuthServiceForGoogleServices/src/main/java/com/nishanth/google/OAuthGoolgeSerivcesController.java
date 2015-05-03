package com.nishanth.google;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.QueryParam;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

@Controller
public class OAuthGoolgeSerivcesController {
	
	private static final String GoogleAuth_URL = "https://accounts.google.com/o/oauth2/auth";
	private static final String Google_AccessToken_URL = "https://accounts.google.com/o/oauth2/token";
	private static final String Youtube_Scope = "https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fyoutube";
	private static final String Contacts_Scope ="https%3A%2F%2Fwww.google.com%2Fm8%2Ffeeds";
	private static final String Response_Type = "code";
	private static final String Access_Type = "offline";
	
	private AccessTokenClass accessTokenClass;
	private YoutubeAccessTokenClass youtubeAccessTokenClass;
	private ContactsAccessTokenClass contactsAccessTokenClass;
	
	@Value("${google.product.CLIENT_ID}")
	private String CLIENT_ID;

	@Value("${google.product.CLIENT_SECRET}")
	private String CLIENT_SECRET;

	@Value("${google.redirect.REDIRECT_URI}")
	private String REDIRECT_URI;

	@Value("${google.redirect.REDIRECT_URI_FOR_ACCESS_TOKEN}")
	private String REDIRECT_URI_FOR_ACCESS_TOKEN;
	
	@Value("${google.youtube.refresh.REFRESH_TOKEN}")
	private String YOUTUBE_REFRESH_TOKEN;
	
	@Value("${google.contacts.refresh.REFRESH_TOKEN}")
	private String CONTACTS_REFRESH_TOKEN;
	
	@RequestMapping(value ="/",method = RequestMethod.GET)
	public @ResponseBody String printHello(ModelMap model, HttpServletRequest httpReq) 
	{
		return "Welcome To OAuth Service for Google Services";
	}
	
	@RequestMapping( value="/youtube", method = RequestMethod.GET)
	public @ResponseBody String youtube()
	{
		String url = GoogleAuth_URL+"?client_id="+CLIENT_ID+"&redirect_uri="+REDIRECT_URI+"&scope="+Youtube_Scope+"&response_type="+Response_Type+"&access_type="+Access_Type;

		if(Desktop.isDesktopSupported()){
			Desktop desktop = Desktop.getDesktop();
			try {
				desktop.browse(new URI(url));
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException();
			}
		}else{
			Runtime runtime = Runtime.getRuntime();
			try {
				runtime.exec("xdg-open " + url);
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException();
			}
		}
		return "redirecting to /oauth2callback";
	}

	@RequestMapping( value="/contacts", method = RequestMethod.GET)
	public @ResponseBody String contacts()
	{
		String url = GoogleAuth_URL+"?client_id="+CLIENT_ID+"&redirect_uri="+REDIRECT_URI+"&scope="+Contacts_Scope+"&response_type="+Response_Type+"&access_type="+Access_Type;

		if(Desktop.isDesktopSupported()){
			Desktop desktop = Desktop.getDesktop();
			try {
				desktop.browse(new URI(url));
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException();
			}
		}else{
			Runtime runtime = Runtime.getRuntime();
			try {
				runtime.exec("xdg-open " + url);
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException();
			}
		}
		return "redirecting to /oauth2callback";
	}
	
	@RequestMapping( value="/oauth2callback", method = RequestMethod.GET)
	public @ResponseBody String oAuthCallback(@QueryParam("code") String code)
	{
		try {

			Client client = Client.create();
			WebResource webResource = client.resource(Google_AccessToken_URL);

			String input = "code="+code+"&"
					+ "client_id="+CLIENT_ID+"&"
					+ "client_secret="+CLIENT_SECRET+"&"
					+ "redirect_uri="+REDIRECT_URI_FOR_ACCESS_TOKEN+"&"
					+ "grant_type=authorization_code";

			ClientResponse response = webResource.type("application/x-www-form-urlencoded")
					.post(ClientResponse.class, input);

			if (response.getStatus() != 200) {
				System.out.println("Response = "+response.getStatus());
				System.out.println("Content  = "+response.getEntity(String.class));
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatus());
			}

			String str = response.getEntity(String.class);
			accessTokenClass = null;
			accessTokenClass = new ObjectMapper().readValue(str, AccessTokenClass.class);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
		
		return accessTokenClass.getAccess_token();
	}
	
	@RequestMapping( value="/refreshYoutubeToken", method = RequestMethod.GET)
	public @ResponseBody String refreshYoutubeAccessToken()
	{
		String input = "client_id="+CLIENT_ID+"&"
				+ "client_secret="+CLIENT_SECRET+"&"
				+ "refresh_token="+YOUTUBE_REFRESH_TOKEN+"&"
				+ "grant_type=refresh_token";
		try{
			Client client = Client.create();
			WebResource webResource1 = client.resource(Google_AccessToken_URL);
			ClientResponse response = webResource1.type("application/x-www-form-urlencoded")
					.post(ClientResponse.class, input);
			
			if (response.getStatus() != 200) {
				System.out.println("Response = "+response.getStatus());
				System.out.println("Content  = "+response.getEntity(String.class));
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatus());
			}
			String str = response.getEntity(String.class);
			youtubeAccessTokenClass = null;
			youtubeAccessTokenClass = new ObjectMapper().readValue(str, YoutubeAccessTokenClass.class);
			return youtubeAccessTokenClass.getAccess_token();
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}
	
	@RequestMapping( value="/refreshContactsToken", method = RequestMethod.GET)
	public @ResponseBody String refreshContactsAccessToken()
	{
		String input = "client_id="+CLIENT_ID+"&"
				+ "client_secret="+CLIENT_SECRET+"&"
				+ "refresh_token="+CONTACTS_REFRESH_TOKEN+"&"
				+ "grant_type=refresh_token";
		try{
			Client client = Client.create();
			WebResource webResource1 = client.resource(Google_AccessToken_URL);
			ClientResponse response = webResource1.type("application/x-www-form-urlencoded")
					.post(ClientResponse.class, input);
			
			if (response.getStatus() != 200) {
				System.out.println("Response = "+response.getStatus());
				System.out.println("Content  = "+response.getEntity(String.class));
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatus());
			}
			String str = response.getEntity(String.class);
			contactsAccessTokenClass = null;
			contactsAccessTokenClass = new ObjectMapper().readValue(str, ContactsAccessTokenClass.class);
			return contactsAccessTokenClass.getAccess_token();
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}
}
