package com.guide.lunar;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
//import org.apache.http.Consts;
import org.apache.http.client.ResponseHandler;
//import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.entity.StringEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.apache.http.client.ClientProtocolException;

//http://topmanopensource.iteye.com/blog/821964

public class HttpAction {

	public String httpGet (String urls, int timeout) throws IOException {

		//http://chenqing24.blog.163.com/blog/static/84653842011917104533280/
		//http://www.360doc.com/content/10/0805/11/61497_43814046.shtml
		//http://www.360doc.com/content/09/1201/18/203871_10149531.shtml
		//http://simpleframework.net/blog/v/11410.html
		//http://my.oschina.net/javagg/blog/16240
		DefaultHttpClient httpclient = new DefaultHttpClient();
		httpclient.addRequestInterceptor(new HttpEncodingRequest ());
        httpclient.addResponseInterceptor(new HttpEncodingResponse ());
		httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, timeout);
		httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, timeout);

		//HttpGet httpget = new HttpGet(urls);
        HttpUriRequest request = new HttpGet (urls);
		//ResponseHandler
		ResponseHandler<String> responseHandler = new BasicResponseHandler();

        String content = null;
        
        //HttpParams httpParams = new BasicHttpParams(); 
        //HttpConnectionParams.setConnectionTimeout (httpParams, )

        //try {
			content = httpclient.execute(request, responseHandler);
        /*} catch (ClientProtocolException e) {
        } catch (IOException e) {
		} catch (Exception e) {
			// e.printStackTrace();
		}*/
		httpclient.getConnectionManager().shutdown();

        return content;
	}

    public String httpPost (String urls, HttpEntity entity, List<NameValuePair> headers, int timeout) throws IOException {
        DefaultHttpClient httpClient = new DefaultHttpClient ();
        httpClient.addRequestInterceptor(new HttpEncodingRequest ());
        httpClient.addResponseInterceptor(new HttpEncodingResponse ());
        httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, timeout);
        httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, timeout);

        HttpPost request = new HttpPost (urls);
        request.setEntity (entity);

        for (NameValuePair nvp : headers) {
            request.setHeader (nvp.getName (), nvp.getValue ());
        }

        String result = null;

        //try {
            HttpResponse response = httpClient.execute (request);

            if (response.getStatusLine ().getStatusCode () == 200) {
                result = EntityUtils.toString (response.getEntity ());
            }
//        } catch (ClientProtocolException e) {
//        } catch (IOException e) {
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        return result;
    }


	 //压缩文件处理的实体包装类  
	 //该类 apache自带：org.apache.http.client.entity.GzipDecompressingEntity
	//但是android中没有找到
    static class GzipDecompressingEntity extends HttpEntityWrapper {  
        public GzipDecompressingEntity(final HttpEntity entity) {  
            super(entity);  
        }  
        @Override  
        public InputStream getContent()  
            throws IOException, IllegalStateException {  
            // the wrapped entity's getContent() decides about repeatability  
            InputStream wrappedin = wrappedEntity.getContent();  
            return new GZIPInputStream(wrappedin);  
        }  
        @Override  
        public long getContentLength() {  
            // length of ungzipped content is not known  
            return -1;  
        }  
  
    }   

	static class HttpEncodingRequest implements HttpRequestInterceptor {
        public void process(
                final HttpRequest request,
                final HttpContext context) throws HttpException, IOException {
            if (!request.containsHeader("Accept-Encoding")) {
                request.addHeader("Accept-Encoding", "gzip");
            }
        }

    }

	static class HttpEncodingResponse implements HttpResponseInterceptor {
        public void process(
                final HttpResponse response,
                final HttpContext context) throws HttpException, IOException {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                Header ceheader = entity.getContentEncoding();
                if (ceheader != null) {
                    HeaderElement[] codecs = ceheader.getElements();
                    for (int i = 0; i < codecs.length; i++) {
                        if (codecs[i].getName().equalsIgnoreCase("gzip")) {
                            response.setEntity(
                                    new GzipDecompressingEntity(response.getEntity()));
                        }
                    }
                }
            }
        }

    }

}
