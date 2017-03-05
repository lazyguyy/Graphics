package graphgui;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;


public class Settings {
    
    private String filepath;
    private HashMap<String, String> properties;
    private HashMap<String, String> defaultProperties;
    
    
    public Settings(String filepath) {
        this.filepath = filepath;
        properties = new HashMap<String, String>();
        defaultProperties = new HashMap<String, String>();
        loadDefaultProperties();
        loadProperties();
    }
    
    private void loadProperties() {
        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] keyAndValue = line.split("#")[0].split("=");
                if (keyAndValue.length != 2 || line.charAt(0) == '#')
                    continue;
                properties.put(keyAndValue[0].trim(), keyAndValue[1].trim());
            }
            
            
        } catch (FileNotFoundException e) {
            loadDefaults();
            saveProperties();
        } catch (IOException e) {
            loadDefaults();
        }
    }
    
    public void printProperties() {
        for (String key : properties.keySet()) {
            System.out.println(key + "=" + properties.get(key));
        }
    }
    
    public void changeProperty(String key, String value) {
        properties.put(key, value);
        saveProperties();
    }
    
    public String getValue(String key) {
        if (!properties.containsKey(key))
            setDefault(key);
        return properties.get(key);
    }
    
    public boolean holdsDoubleValue(String key) {
        //Copied from https://docs.oracle.com/javase/7/docs/api/java/lang/Double.html#valueOf(java.lang.String)
        final String Digits     = "(\\p{Digit}+)";
        final String HexDigits  = "(\\p{XDigit}+)";
        // an exponent is 'e' or 'E' followed by an optionally 
        // signed decimal integer.
        final String Exp        = "[eE][+-]?"+Digits;
        final String fpRegex    =
            ("[\\x00-\\x20]*"+ // Optional leading "whitespace"
            "[+-]?(" +         // Optional sign character
            "NaN|" +           // "NaN" string
            "Infinity|" +      // "Infinity" string

            // A decimal floating-point string representing a finite positive
            // number without a leading sign has at most five basic pieces:
            // Digits . Digits ExponentPart FloatTypeSuffix
            // 
            // Since this method allows integer-only strings as input
            // in addition to strings of floating-point literals, the
            // two sub-patterns below are simplifications of the grammar
            // productions from the Java Language Specification, 2nd 
            // edition, section 3.10.2.

            // Digits ._opt Digits_opt ExponentPart_opt FloatTypeSuffix_opt
            "((("+Digits+"(\\.)?("+Digits+"?)("+Exp+")?)|"+

            // . Digits ExponentPart_opt FloatTypeSuffix_opt
            "(\\.("+Digits+")("+Exp+")?)|"+

            // Hexadecimal strings
            "((" +
            // 0[xX] HexDigits ._opt BinaryExponent FloatTypeSuffix_opt
            "(0[xX]" + HexDigits + "(\\.)?)|" +

            // 0[xX] HexDigits_opt . HexDigits BinaryExponent FloatTypeSuffix_opt
            "(0[xX]" + HexDigits + "?(\\.)" + HexDigits + ")" +

            ")[pP][+-]?" + Digits + "))" +
            "[fFdD]?))" +
            "[\\x00-\\x20]*");// Optional trailing "whitespace"
        return Pattern.matches(fpRegex, getValue(key));
    }
    
    public String setDefault(String key) {
        if (defaultProperties.containsKey(key)) {
            properties.put(key, defaultProperties.get(key));
            saveProperties();
            return defaultProperties.get(key);
        }
        return "";
    }
    
    public double parseAsDoubleOrDefault(String key) {
        return Double.parseDouble(holdsDoubleValue(key) ? getValue(key) : setDefault(key));
    }
    
    private void loadDefaultProperties() {
        defaultProperties.put("VERTEX_SIZE", "20");
        defaultProperties.put("MIN_DISTANCE", "100");
        defaultProperties.put("MAX_DISTANCE", "400");
        defaultProperties.put("TIP_ANGLE", "45");
        defaultProperties.put("CANVAS_SIZE", "1000");
        defaultProperties.put("DISTANCE_OFFSET_FACTOR", "1.1");
    }
    
    private void loadDefaults() {
        for (String key : defaultProperties.keySet()) {
            properties.put(key, getValue(key));
        }
    }
    
    private void saveProperties() {
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(filepath))) {
            for (String key : properties.keySet()) {
                bw.write(key + "=" + getValue(key));
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("IOException: tried to write to " + filepath);
        }
    }
    
    
}
