package sampleApi;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.net.ssl.HttpsURLConnection;

public class Test {

	public static void main(String args[]) {
		
        String strURL = "https://api.cloudsds.com/msdsdevapi/api/";
        String response = "";
        String inputdata = "";
        String accesstoken="";

	//Credentials received from Cloudsds
        String UserId = ""; 
        String Password = ""; 

        /* Steps */

        /* 1: Login to get an Authorization Token */
        inputdata = prepareLoginData(UserId, Password);
        response = Communicate(strURL + "user/login", "", inputdata);
        System.out.println(response);
        
        /* 2: Get Authorization Token */
        JsonParser parser = new JsonParser(); 
        JsonObject json = (JsonObject) parser.parse(response);
        accesstoken=json.get("token").getAsString();

        
        /* 3: Call an API to get SDS information */
        inputdata = prepareSearchData("", "Country Delight Gel", "","","","",2,30);
        response = Communicate(strURL + "v1/msds/searchsds", accesstoken, inputdata);
        System.out.println(response);
        	
	}
    static String prepareLoginData(String struser, String strpass)
    {
    	JsonObject jobj = new JsonObject();
    	jobj.addProperty("loginId", struser);
    	jobj.addProperty("loginPassword", strpass);
        return jobj.toString();
    }

    static String prepareSearchData(String msds_id, String product_name,
            String product_code,String mfg_name,String issue_date,
            String cas_no, int offset_val, int limit_val)
        {
    		JsonObject jobj = new JsonObject();
 
            jobj.addProperty("msds_id", msds_id);
            jobj.addProperty("product_name", product_name);
            jobj.addProperty("product_code", product_code);
            jobj.addProperty("mfg_name", mfg_name);
            jobj.addProperty("cas_no", cas_no);
            jobj.addProperty("issue_date", issue_date);
            jobj.addProperty("offset_val", offset_val);
            jobj.addProperty("limit_val", limit_val);
            
            return jobj.toString();
        }
    
    
	static String Communicate(String strURL,String AccessCode,String strData) {
		String result="";
		InputStream inputStream;
		try{
			
			URL obj = new URL(strURL);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("POST");
			con.setDoOutput(true);
			con.setRequestProperty("Content-Type", "application/json");
			if (!AccessCode.equals("")){
				con.setRequestProperty("Authorization", AccessCode);
			}
			DataOutputStream writer = new DataOutputStream(con.getOutputStream());
            writer.writeBytes(strData);
            writer.flush();
            
            con.connect();
			//con.getInputStream()
			int responseCode = con.getResponseCode();
			if (responseCode == HttpsURLConnection.HTTP_OK) {

                inputStream = con.getInputStream();
                
                if (inputStream != null) {
                    result = convertInputStreamToString(inputStream);
                } else {
                    result = "Did not work!";

                }
                con.disconnect();	
                writer.close();
                return result;
            } else {
            	writer.close();
            	con.disconnect();
                return "false : " + responseCode;
            }
				
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return result;
	}
	
	static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        StringBuilder result = new StringBuilder();
        while ((line = bufferedReader.readLine()) != null)
            result.append(line);
        inputStream.close();
        return result.toString();
    }
}
