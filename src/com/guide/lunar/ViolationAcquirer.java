package com.guide.lunar;

import java.util.regex.Matcher; 
import java.util.regex.Pattern;

import java.util.ArrayList;
import java.util.List;

import java.util.Date;
import java.text.SimpleDateFormat;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.client.ClientProtocolException;

import java.io.IOException;


public class ViolationAcquirer {


    public Date getDatebaseUpdateDate () {

        String urls = "http://www.xzjgw.com/2010cx/cars.asp";

        String htmlContent = null;

        try {
        	htmlContent = new HttpAction ().httpGet (urls, 30000);
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

            Pattern p = Pattern.compile ("����ʱ�䣺<FONT color=red>(.*?)</FONT>");
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

    
    public ViolationResult getBreaksRule (ViolationManager.VehicleData vd) {
        String urls = "http://www.xzjgw.com/chaxun/index.asp";

        List <NameValuePair> formParams = new ArrayList<NameValuePair>();
        formParams.add(new BasicNameValuePair("hpzl", vd.vehicleType));
        formParams.add(new BasicNameValuePair("cphm", vd.licenseNumber));
        formParams.add(new BasicNameValuePair("fdjh", vd.engineNumber));
        // formParams.add(new BasicNameValuePair("image.x", "31"));
        // formParams.add(new BasicNameValuePair("image.y", "11"));

        List<NameValuePair> headers = new ArrayList<NameValuePair>();
        headers.add(new BasicNameValuePair("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)"));   
        headers.add(new BasicNameValuePair("Referer", "http://www.xzjgw.com/2010cx/cars.asp"));   

        HttpEntity entity      = null;
        String     htmlContent = null;

//        ViolationManager vManager[] = {
//            new ViolationManager (ViolationManager.LOCAL),
//            new ViolationManager (ViolationManager.NONLOCAL)
//        };
        
        ViolationManager vManager = new ViolationManager (vd);

        try {
            //entity = new UrlEncodedFormEntity(formParams, Consts.UTF_8);
            entity = new UrlEncodedFormEntity(formParams, "GBK");

            htmlContent = new HttpAction ().httpPost (urls, entity, headers, 30000);
            if (null == htmlContent) {
                return new ViolationResult (ViolationResult.ERROR_NET);
            }
            htmlContent = new String(htmlContent.getBytes("ISO-8859-1"),"GBK");
            
            if (htmlContent != null) {
            	if (htmlContent.indexOf("������ķ��������복�ƺ��벻��") >= 0) {
            		return new ViolationResult (ViolationResult.ERROR_DATA);
            	}
            }

            // System.out.print (htmlContent);

            // // ����ɸѡ����Υ����Ϣ html �ַ���
            // String localInfo = "����Υ����Ϣ</td>[\\s\\S]*?</TBODY>";
            // // ����ɸѡ���/�ֳ�Υ����Ϣ
            // String remoteInfo = "���Υ����Ϣ</td>[\\s\\S]*?</TBODY>";

            // ɸѡ��Υ����Ϣ�飨����/����ֳ�ͨ�ã�
            String breaksRuleInfo = "Υ����Ϣ</td>([\\s\\S]*?)</TBODY>";

            // ��Υ����Ϣ����ɸѡ��Υ�´���ֵ������/����ֳ�ͨ�ã�
            String breaksRuleCount = "���鵽</font><font.*?>[\\s\\S]*?(\\d+)[\\s\\S]*?</font>";

            // ɸѡ������Υ����Ϣ������/����ֳ�ͨ�ã�
            String breaksRuleItem = "<tr.*?bgcolor=\"#C4CAD2\">[\\s\\S]*?<font color=\"#000000\">([\\s\\S]*?)</font>[\\s\\S]*?<font.*?>([\\s\\S]*?)</font>[\\s\\S]*?<font.*?>([\\s\\S]*?)</font>[\\s\\S]*?<font.*?>([\\s\\S]*?)</font>[\\s\\S]*?<font.*?>([\\s\\S]*?)</font>[\\s\\S]*?<font.*?>([\\s\\S]*?)</font>[\\s\\S]*?<div.*?>([\\s\\S]*?)</div></td>";

            Pattern p = Pattern.compile (breaksRuleInfo);
            Matcher m = p.matcher(htmlContent);
            int index = 0;

            while (m.find ()) {
                String infoBlock = m.group (1);

                // ����Υ�´���
                Pattern bp = Pattern.compile (breaksRuleCount);
                Matcher mm = bp.matcher (infoBlock);

                int brCount = 0;

                if (mm.find ()) {
                    // Υ�´���
                    brCount = Integer.parseInt (mm.group (1));
                }

                // ��������Υ����Ϣ
                if (brCount > 0) {
                    bp = Pattern.compile (breaksRuleItem);
                    mm = bp.matcher (infoBlock);
                    
                    int currViolationType = (index<=0) ? ViolationManager.LOCAL : ViolationManager.NONLOCAL;

                    // ����Υ����Ϣ��Ŀ
                    while (mm.find ()) {
                        if (mm.groupCount () != 7) {
                            continue;
                        }

                        vManager.add (
                        				vManager.newData (currViolationType).fillData (
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

        return new ViolationResult (ViolationResult.ERROR_OK, vManager);
    }

    private String fixInfo (String info) {
        return info.replaceAll ("<[\\s\\S]*?>", "").replaceAll ("[\\n\\r]*", "").replaceAll ("^\\s*", "").replaceAll ("\\s*$", "");
    }

}
