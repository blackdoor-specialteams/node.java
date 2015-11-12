package black.door.node.java.http;

import black.door.node.java.exception.HttpParsingException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by nfischer on 6/6/15.
 */
public class HttpRequest{

	private Map<String, String> headers;
	private HttpVerb verb;
	private URI uri;
	private String version;
	private byte[] body;
	private Matcher rex;
	private Map<String, String> queryParams;

	public HttpRequest(HttpVerb method, URI uri, String version){
		this.verb = method;
		this.uri = uri;
		this.version = version;
		headers = new HashMap<>();
	}

	public static HttpRequest parse(byte[] request) throws IOException,
			URISyntaxException, HttpParsingException {
		return parse(new ByteArrayInputStream(request), -1);
	}

	public static HttpRequest parse(InputStream is, int maxBodySize)
			throws IOException, URISyntaxException, HttpParsingException {
		HttpVerb verb;
		URI uri;
		String version;
		Map<String, String> headers;

		String firstLine = ParseTools.nextLine(is);

		String[] split = firstLine.split("\\s+");
		if(split.length < 3)
			throw new HttpParsingException("Request line does not have 3 " +
					"parts as described in RFC2616 5.1");

		verb = HttpVerb.valueOf(split[0]);
		uri = new URI(split[1]);
		version = split[2];

		headers = ParseTools.parseHeaders(is);

		byte[] body = ParseTools.getBody(is, headers, maxBodySize);

		HttpRequest ret = new HttpRequest(verb, uri, version);
		ret.setBody(body);
		ret.setHeaders(headers);

		ret.queryParams = ParseTools.parseQueries(uri.getQuery());

		return ret;
	}

	public void definePathParams(Pattern p) throws IllegalArgumentException{
		Matcher rex = p.matcher(this.getUri().getPath());
		if(!rex.find()){
			throw new IllegalArgumentException();
		}
		this.rex = rex;
	}

	/**
	 *
	 * @param name the name of the capture group for the parameter
	 * @return the path parameter, or null if path parameters have not been
	 *      defined or if no part of the path matched that parameter
	 * @throws IllegalArgumentException if there is no parameter with that name
	 */
	public String getPathParam(String name) throws IllegalArgumentException{
		if(rex == null)
			return null;
		return rex.group(name);
	}

	public String getPathParam(int i){
		if(rex == null)
			return null;
		return rex.group(i);
	}

	public String getQueryParam(String name){
		return queryParams.get(name);
	}

	public Map<String, String> getQueryParams(){
		return queryParams;
	}

	public HttpRequest putHeader(String headerName, String value){
		headers.put(headerName, value);
		return this;
	}

	public String getHeader(String headerName){
		return headers.get(headerName);
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	public HttpVerb getVerb() {
		return verb;
	}

	public void setVerb(HttpVerb verb) {
		this.verb = verb;
	}

	public URI getUri() {
		return uri;
	}

	public void setUri(URI uri) {
		this.uri = uri;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public byte[] getBody() {
		return body;
	}

	public void setBody(byte[] body) {
		this.body = body;
	}


	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(verb);
		sb.append(" ");
		sb.append(uri.toString());
		sb.append(" ");
		sb.append(version);
		sb.append("\n");
		for(Map.Entry<String, String> e : headers.entrySet()){
			sb.append(e.getKey());
			sb.append(": ");
			sb.append(e.getValue());
			sb.append("\n");
		}
		sb.append("\n");
		if(body != null)
			sb.append(new String(body, StandardCharsets.UTF_8));

		return sb.toString();
	}
}
