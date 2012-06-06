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
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.apache.http.client.ClientProtocolException;

import java.io.IOException;


public class ViolationAcquirer {

	public String httpGet (String urls, int timeout) throws IOException {

		//http://chenqing24.blog.163.com/blog/static/84653842011917104533280/
		//http://www.360doc.com/content/10/0805/11/61497_43814046.shtml
		//http://www.360doc.com/content/09/1201/18/203871_10149531.shtml
		//http://simpleframework.net/blog/v/11410.html
		//http://my.oschina.net/javagg/blog/16240
		DefaultHttpClient httpclient = new DefaultHttpClient();
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

    public Date getDatebaseUpdateDate () {

        String urls = "http://www.xzjgw.com/2010cx/cars.asp";

        String htmlContent = null;

        try {
        	htmlContent = httpGet (urls, 10000);
        } catch (ClientProtocolException e) {
        	return null;
        } catch (IOException e) {
			return null;
		}

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

    
    public ViolationResult getBreaksRule (String hpzl, String cphm, String fdjh) {
        String urls = "http://www.xzjgw.com/chaxun/index.asp";

        List <NameValuePair> formParams = new ArrayList<NameValuePair>();
        formParams.add(new BasicNameValuePair("hpzl", hpzl));
        formParams.add(new BasicNameValuePair("cphm", cphm));
        formParams.add(new BasicNameValuePair("fdjh", fdjh));
        // formParams.add(new BasicNameValuePair("image.x", "31"));
        // formParams.add(new BasicNameValuePair("image.y", "11"));

        List<NameValuePair> headers = new ArrayList<NameValuePair>();
        headers.add(new BasicNameValuePair("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)"));   
        headers.add(new BasicNameValuePair("Referer", "http://www.xzjgw.com/2010cx/cars.asp"));   

        HttpEntity entity      = null;
        String     htmlContent = null;

        ViolationManager vManager[] = {
            new ViolationManager (ViolationData.LOCAL),
            new ViolationManager (ViolationData.NONLOCAL)
        };

        try {
            //entity = new UrlEncodedFormEntity(formParams, Consts.UTF_8);
            entity = new UrlEncodedFormEntity(formParams, "GBK");

            htmlContent = httpPost (urls, entity, headers, 10000);
            if (null == htmlContent) {
                return new ViolationResult (ViolationResult.ERROR_NET);
            }
            htmlContent = new String(htmlContent.getBytes("ISO-8859-1"),"GBK");

            // System.out.print (htmlContent);

            // // 用以筛选本地违章信息 html 字符块
            // String localInfo = "本地违章信息</td>[\\s\\S]*?</TBODY>";
            // // 用以筛选异地/现场违章信息
            // String remoteInfo = "异地违章信息</td>[\\s\\S]*?</TBODY>";

            // 筛选出违章信息块（本地/异地现场通用）
            String breaksRuleInfo = "违章信息</td>([\\s\\S]*?)</TBODY>";

            // 从违章信息块中筛选出违章次数值（本地/异地现场通用）
            String breaksRuleCount = "共查到</font><font.*?>[\\s\\S]*?(\\d+)[\\s\\S]*?</font>";

            // 筛选出具体违章信息（本地/异地现场通用）
            String breaksRuleItem = "<tr.*?bgcolor=\"#C4CAD2\">[\\s\\S]*?<font color=\"#000000\">([\\s\\S]*?)</font>[\\s\\S]*?<font.*?>([\\s\\S]*?)</font>[\\s\\S]*?<font.*?>([\\s\\S]*?)</font>[\\s\\S]*?<font.*?>([\\s\\S]*?)</font>[\\s\\S]*?<font.*?>([\\s\\S]*?)</font>[\\s\\S]*?<font.*?>([\\s\\S]*?)</font>[\\s\\S]*?<div.*?>([\\s\\S]*?)</div></td>";

            Pattern p = Pattern.compile (breaksRuleInfo);
            Matcher m = p.matcher(htmlContent);
            int index = 0;

            while (m.find ()) {
                String infoBlock = m.group (1);

                // 分析违章次数
                Pattern bp = Pattern.compile (breaksRuleCount);
                Matcher mm = bp.matcher (infoBlock);

                int brCount = 0;

                if (mm.find ()) {
                    // 违章次数
                    brCount = Integer.parseInt (mm.group (1));
                }

                // 分析具体违章信息
                if (brCount > 0) {
                    bp = Pattern.compile (breaksRuleItem);
                    mm = bp.matcher (infoBlock);

                    // 遍历违章信息条目
                    while (mm.find ()) {
                        if (mm.groupCount () != 7) {
                            continue;
                        }

                        vManager[index].add (
                                             vManager[index].newData ().fillData (
                                                                                  fixInfo (mm.group (1)),
                                                                                  fixInfo (mm.group (2)),
                                                                                  fixInfo (mm.group (3)),
                                                                                  fixInfo (mm.group (4)),
                                                                                  fixInfo (mm.group (5)),
                                                                                  fixInfo (mm.group (6)),
                                                                                  fixInfo (mm.group (7))
                                                                                  )
                                             );
                    }
                }

                if (index++>=1) {
                    break;
                }
            }

        } catch (ClientProtocolException e) {
        	return new ViolationResult (ViolationResult.ERROR_NET);
        } catch (IOException e) {
        	return new ViolationResult (ViolationResult.ERROR_NET);
        } catch (Exception e) {
            e.printStackTrace();
            return new ViolationResult (ViolationResult.ERROR_PARSE);
        }

        return new ViolationResult (ViolationResult.ERROR_OK, vManager[0], vManager[1]);
    }

    private String fixInfo (String info) {
        return info.replaceAll ("<[\\s\\S]*?>", "").replaceAll ("[\\n\\r]*", "").replaceAll ("^\\s*", "").replaceAll ("\\s*$", "");
    }

}
