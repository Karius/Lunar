package com.guide.lunar;

import java.util.regex.Matcher; 
import java.util.regex.Pattern;

import java.util.ArrayList;
import java.util.List;

import java.util.Date;
import java.text.SimpleDateFormat;

import org.apache.http.Header;
// import org.apache.http.Consts;
import org.apache.http.client.ResponseHandler;
import org.apache.http.HttpResponse;
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.http.client.ClientProtocolException;

import java.io.IOException;


public class CarDataManager {

	public String httpGet (String urls) {

		DefaultHttpClient httpclient = new DefaultHttpClient();
		//HttpGet httpget = new HttpGet(urls);
                HttpUriRequest request = new HttpGet (urls);
		//ResponseHandler
		ResponseHandler<String> responseHandler = new BasicResponseHandler();

                String content = null;

        try {
			content = httpclient.execute(request, responseHandler);
        } catch (ClientProtocolException e) {
        } catch (IOException e) {
		} catch (Exception e) {
			// e.printStackTrace();
		}
		httpclient.getConnectionManager().shutdown();

                return content;
	}

    public Date getDatebaseUpdateDate () {

        String urls = "http://www.xzjgw.com/2010cx/cars.asp";

        String htmlContent = httpGet (urls);

        if (htmlContent == null) {
            return null;
        }

        Date dt = null;

        try {
            htmlContent = new String(htmlContent.getBytes("ISO-8859-1"),"GBK");

            Pattern p = Pattern.compile ("更新时间：<FONT color=red>(.*?)</FONT>");
            Matcher m = p.matcher(htmlContent);

            /*if (m.groupCount () <= 0) {
              return null;
              }*/

            if (m.find ()) {
                SimpleDateFormat sdf = new SimpleDateFormat ("yyyy-MM-dd");
                dt = sdf.parse (m.group (1));
            }

        } catch (Exception e) {
        }
        return dt;
    }

}
