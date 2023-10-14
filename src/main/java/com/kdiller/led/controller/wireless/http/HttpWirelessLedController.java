package com.kdiller.led.controller.wireless.http;

import com.kdiller.led.controller.wireless.IWirelessLedController;

import java.awt.Color;

import java.io.OutputStream;

import java.net.URL;
import java.net.URLConnection;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;

import java.util.Map;
import java.util.HashMap;
import java.util.Scanner;

public class HttpWirelessLedController implements IWirelessLedController {
    private static final String CHALLENGE_STRING = "AAECAwQFBgcICQoLDA0ODxAREhMUFRYXGBkaGxwdHh8=";
    private static final int NUM_RETRIES = 5;
    
    private static final String GET_ACTION = "GET";
    private static final String POST_ACTION = "POST";
    
    private static final String JSON_CONTENT_TYPE = "application/json";
    private static final String OCTET_CONTENT_TYPE = "application/octet-stream";

    private String ipAddress;
    private String authToken;
    
    private LedMode mode;
    
    public HttpWirelessLedController(String ipAddress) {
        this.ipAddress = ipAddress;
        
        getAuthToken();
        mode = getMode();
    }
    
    private void getAuthToken() {
        Map<String, String> retValue = null;
        String loginData = "{\"challenge\":\"" + CHALLENGE_STRING + "\"}";
        
        authToken = null;
        
        for(int i = 0; i < NUM_RETRIES; i++){
            retValue = performAction("login", POST_ACTION, JSON_CONTENT_TYPE, loginData.getBytes());
            authToken = retValue.get("authentication_token");
            
            Map<String, Object> verifyProps = new HashMap<String, Object>();
            retValue = performAction("verify", POST_ACTION, JSON_CONTENT_TYPE, ("{\"challenge-response\":\"" + retValue.get("challenge-response") + "\"}").getBytes());
            
            if(retValue == null) {
                authToken = null;
            }else{
                break;
            }
        }
        
        if(retValue == null){
            throw new RuntimeException("Failed to get Authentication Token in " + NUM_RETRIES + " attempts");
        }
    }

    // LED Strand Details
    public String getDeviceName() {
        return getFullInfo().get("device_name");
    }
    
    public String getProductCode() {
        return getFullInfo().get("product_code");
    }
    
    public String getUUID() {
        return getFullInfo().get("uuid");
    }
    
    public String getHardwareID() {
        return getFullInfo().get("hw_id");
    }
    
    public LedMode getMode() {
        Map<String, String> retValue = null;
        
        for(int i = 0; i < NUM_RETRIES; i++){
            retValue = performAction("led/mode", GET_ACTION, null, null);
            
            if(retValue == null){
                getAuthToken();
            }else{
                break;
            }
        }
        
        LedMode mode = null;
        if(retValue != null){
            mode = LedMode.parseModeStr(retValue.get("mode"));
        }
        
        return mode;
    }
    
    public int numberOfLeds() {
        try{
            return Integer.parseInt(getFullInfo().get("number_of_led"));
        }catch(Exception e) {
            return -1;
        }
    }
    
    public Map<String, String> getFullInfo() {
        Map<String, String> retValue = null;
        
        for(int i = 0; i < NUM_RETRIES; i++){
            retValue = performAction("gestalt", GET_ACTION, null, null);
            
            if(retValue != null){
                break;
            }
        }
        
        if(retValue == null){
            retValue = new HashMap<String, String>();
        }
        
        return retValue;
    }
    
    // LED Operations
    public void setMode(LedMode mode) {
        Map<String, String> retValue = null;
        String data = "{\"mode\":\"" + mode.getModeStr() + "\"}";
        
        for(int i = 0; i < NUM_RETRIES; i++){
            retValue = performAction("led/mode", POST_ACTION, JSON_CONTENT_TYPE, data.getBytes());
            
            if(retValue == null){
                getAuthToken();
            }else{
                break;
            }
        }
    }
    
    public void updateFrame(Color[] frame) {
        if(mode != LedMode.REAL_TIME){
            setMode(LedMode.REAL_TIME);
            mode = getMode();
            
            if(mode != LedMode.REAL_TIME){
                throw new RuntimeException("Failed to change LedMode to REAL_TIME");
            }
        }
        
        Map<String, String> retValue = null;
        
        byte[] data = new byte[3 * frame.length];
        
        for(int i = 0; i < frame.length; i++){
            data[i * 3] = (byte)frame[i].getRed();
            data[i * 3 + 1] = (byte)frame[i].getGreen();
            data[i * 3 + 2] = (byte)frame[i].getBlue();
        }
        
        for(int i = 0; i < NUM_RETRIES; i++){
            retValue = performAction("led/rt/frame", POST_ACTION, OCTET_CONTENT_TYPE, data);
            
            if(retValue == null){
                getAuthToken();
            }else{
                break;
            }
        }
    }
    
    public void setColor(Color color) {
        if(mode != LedMode.COLOR){
            setMode(LedMode.COLOR);
            mode = getMode();
            
            if(mode != LedMode.COLOR){
                throw new RuntimeException("Failed to change LedMode to COLOR");
            }
        }
        
        Map<String, String> retValue = null;
        String data = "{\"red\":" + color.getRed() + ",\"green\":" + color.getGreen() + ",\"blue\":" + color.getBlue() + "}";
        
        for(int i = 0; i < NUM_RETRIES; i++){
            retValue = performAction("led/color", POST_ACTION, JSON_CONTENT_TYPE, data.getBytes());
            
            if(retValue == null){
                getAuthToken();
            }else{
                break;
            }
        }
    }
    
    public void turnOff() {
        setMode(LedMode.OFF);
        mode = getMode();
        
        if(mode != LedMode.OFF){
            throw new RuntimeException("Failed to change LedMode to OFF");
        }
    }
    
    // Http Actions
    private Map<String, String> performAction(String url, String action, String contentType, byte[] data) {
        try{
            URL obj = new URL("http://" + ipAddress + "/xled/v1/" + url);
            URLConnection urlCon = obj.openConnection();
            
            if (!(urlCon instanceof HttpURLConnection)) {
                throw new RuntimeException("Failed to create a HTTP Connection");
            }
            
            HttpURLConnection httpCon = (HttpURLConnection) urlCon;
            httpCon.setRequestMethod(action);
            httpCon.setRequestProperty("User-Agent", "Java/1.0");
            
            if(authToken != null){
                httpCon.setRequestProperty("X-Auth-Token", authToken);
            }
            
            httpCon.setDoOutput(true);
            
            if(contentType != null){
                httpCon.setRequestProperty("Content-Type", contentType);
                httpCon.setDoInput(true);
                
                // jsonString of the properties is the body for the request
                OutputStream os = httpCon.getOutputStream();
                os.write(data);
                os.flush();
            }
            
            int responseCode = httpCon.getResponseCode();
            
            if(responseCode != 200){
                // Failed HTTP request
                return null;
            }
            
            String response = "";
            Scanner sc = new Scanner(httpCon.getInputStream());
            while(sc.hasNext()){
                response += sc.nextLine();
            }
            sc.close();
            
            Map<String, String> retValue = fromJSON(response);
            
            if(retValue.get("code").equals("1000")){
                return retValue;
            }else{
                return null;
            }
        }catch(Exception e){
            throw new RuntimeException("Dev Screwed Up", e);
        }
    }
    
    private Map<String, String> fromJSON(String json) {
        Map<String, String> retValue = new HashMap<String, String>();
        
        json = json.substring(1, json.length() - 1);
        String[] props = json.split(",");
        
        for(String p : props){
            p = p.trim();
            int colon = p.indexOf(":");
            
            String key = p.substring(0, colon);
            String value = p.substring(colon + 1);
            
            retValue.put(key.replaceAll("\"", ""), value.replaceAll("\"", ""));
        }
        
        return retValue;
    }
    
    // Device Searching
    private static final byte[] DISCOVER_IP_ADDRESS = new byte[] {(byte)255, (byte)255, (byte)255, (byte)255}; // Use IP Address 255.255.255.255 to send the discover request
    private static final int DISCOVER_PORT = 5555;
    private static final byte[] DISCOVER_MESSAGE = {0x01, 'd', 'i', 's', 'c', 'o', 'v', 'e', 'r'};
    
    private static final int DISCOVER_REQUEST_RESPONSE_LENGTH = 21;
    
    public static Map<String, String> findDevices() throws Exception {
        return findDevices(1000); // Default to 1 second timeout
    }
    
    // Returns a Map: IP Address -> Device ID
    public static Map<String, String> findDevices(int timeoutMs) throws Exception {
        DatagramSocket socket = new DatagramSocket();
        InetAddress address = InetAddress.getByAddress(DISCOVER_IP_ADDRESS);
        
        // Send the discover request
        DatagramPacket packet = new DatagramPacket(DISCOVER_MESSAGE, DISCOVER_MESSAGE.length, address, DISCOVER_PORT);
        socket.send(packet);
        
        // Get the discover response
        byte[] buffer = new byte[DISCOVER_REQUEST_RESPONSE_LENGTH];
        packet = new DatagramPacket(buffer, buffer.length);
        socket.setSoTimeout(timeoutMs);
        boolean timeoutOccurred = false;
        Map<String, String> data = new HashMap<String, String>();
        
        while(!timeoutOccurred){
            try{
                socket.receive(packet);
                
                // Check for OK and null terminator before parsing
                if(buffer[4] == 'O' && buffer[5] == 'K' && buffer[buffer.length - 1] == 0x00){
                    // First 4 bytes are the IP address in reverse order (eg. 192.168.1.1 -> 1 1 168 192)
                    String ipAddress = String.format("%d.%d.%d.%d", removeSign(buffer[3]), removeSign(buffer[2]), removeSign(buffer[1]), removeSign(buffer[0]));
                    // The device id is everything else (start at index 6, and length the total length - ip address (4) - "OK" (2) - null terminator (1)
                    String deviceId = new String(buffer, 6, buffer.length - 4 - 2 - 1);
                    data.put(ipAddress, deviceId);
                }
            }catch(SocketTimeoutException ste){
                timeoutOccurred = true;
            }
        }
        
        return data;
    }
    
    private static int removeSign(byte b){
        return (b + 256) & 0xFF;
    }
}
