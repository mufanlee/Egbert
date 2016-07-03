package controller.util;

import java.net.URI;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

public class HttpDeleteUtil extends HttpEntityEnclosingRequestBase {

	public static final String METHOD_NAME = "DELETE";

    public String getMethod() {
        return METHOD_NAME;
    }

    public HttpDeleteUtil(final String uri) {
        super();
        setURI(URI.create(uri));
    }

    public HttpDeleteUtil(final URI uri) {
        super();
        setURI(uri);
    }

    public HttpDeleteUtil() {
        super();
    }
}
