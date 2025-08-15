package lu.kbra.shared_timetable.client.network;

import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.http.HttpHeaders;

import com.fasterxml.jackson.annotation.JsonProperty;

import lu.pcy113.pclib.config.ConfigLoader.ConfigContainer;
import lu.pcy113.pclib.config.ConfigLoader.ConfigProp;

public class RemoteConfig implements ConfigContainer {

	public static final String USER_PERSISTENT = "/user/persistent";
	public static final String USER_CHECK = "/user/check";
	public static final String USER_LOGIN = "/user/login";
	public static final String USER_REGISTER = "/user/register";

	@ConfigProp("user.name")
	@JsonProperty("user.name")
	private String username;

	@ConfigProp("user.pass")
	@JsonProperty("user.pass")
	private String password;

	@ConfigProp("user.token")
	@JsonProperty("user.token")
	private String token;

	@ConfigProp("user.regenToken")
	@JsonProperty("user.regenToken")
	private boolean regenToken = true;

	@ConfigProp("server.secure")
	@JsonProperty("server.secure")
	private boolean useSecure = false;

	@ConfigProp("server.url")
	@JsonProperty("server.url")
	private String serverUrl;

	public RemoteConfig() {
	}

	public RemoteConfig(String username, String password, String token, boolean regenToken, boolean useSecure, String serverUrl) {
		this.username = username;
		this.password = password;
		this.token = token;
		this.regenToken = regenToken;
		this.useSecure = useSecure;
		this.serverUrl = serverUrl;
	}

	public URI getWSURI(String endpoint) throws URISyntaxException {
		return new URI("ws" + (useSecure ? "s" : "") + "://" + serverUrl + endpoint);
	}

	public URI getHTTPURI(String endpoint) throws URISyntaxException {
		return new URI("http" + (useSecure ? "s" : "") + "://" + serverUrl + endpoint);
	}

	public HttpHeaders buildHttpHeaders() {
		final HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set(HttpHeaders.COOKIE, "token=" + token);
		return httpHeaders;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getToken() {
		return token;
	}

	public boolean isRegenToken() {
		return regenToken;
	}

	public boolean isUseSecure() {
		return useSecure;
	}

	public String getServerUrl() {
		return serverUrl;
	}

	public void setToken(String token) {
		this.token = token;
	}

}
