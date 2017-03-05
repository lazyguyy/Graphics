package graphgui.dataManagement;

import java.io.*;
import java.util.*;
import java.util.function.*;


public class Settings {
    
    private String filepath;
    private HashMap<String, Value> properties;
    private static HashMap<String, Value> defaultProperties;
    private static HashMap<String, Function<String, Value>> parsers;
    
    
    public Settings(String filepath) {
        this.filepath = filepath;
        defaultProperties = new HashMap<String, Value>();
        parsers = new HashMap<String, Function<String, Value>>();
        fillParserMap();
        loadDefaultProperties();
        properties = new HashMap<String, Value>(defaultProperties);
        loadProperties();
    }
    
    private void loadProperties() {
        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.charAt(0) == '#') 
                    continue;
                String key = line.substring(0, line.indexOf('='));
                String value = line.substring(line.indexOf('=') + 1);
                if (!parsers.keySet().contains(key)) {
                    System.out.format("Error in field %s. Field is unknown. Skipping it.%n", key);
                    continue;
                }
                try {
                    properties.put(key.trim(), parsers.get(key).apply(value.trim()));
                } catch (NumberFormatException e) {
                    System.out.format("Error in field %s: %s Defaulting to %s.%n", key, e.getMessage(), toDefault(key));
                }
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
        properties.put(key, parsers.get(key).apply(value));
        saveProperties();
    }
    
    public Value getValue(String key) {
        if (!properties.containsKey(key))
            return toDefault(key);
        return properties.get(key);
    }
    
    public static boolean holdsDoubleValue(String string) {
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
        return string.matches(fpRegex);
    }
    
    public static boolean holdsIntegerValue(String string) {
        return string.matches("\\d+");
    }
    
    public Value toDefault(String key) {
        if (defaultProperties.containsKey(key)) {
            properties.put(key, defaultProperties.get(key));
            return defaultProperties.get(key);
        }
        throw new IllegalArgumentException("Key not found");
    }
    
    private void loadDefaultProperties() {
        defaultProperties.put("VERTEX_SIZE", new Value(20));
        defaultProperties.put("MIN_DISTANCE", new Value(250));
        defaultProperties.put("MAX_DISTANCE", new Value(600));
        defaultProperties.put("TIP_ANGLE", new Value(45.0));
        defaultProperties.put("CANVAS_SIZE", new Value(1000));
        defaultProperties.put("DISTANCE_OFFSET_FACTOR", new Value(1.1));
        defaultProperties.put("FRAME_NAME", new Value("Graphics"));
        defaultProperties.put("VERTEX_REPULSION", new Value(20.0));
        defaultProperties.put("NEGLIGIBLE_FORCE", new Value(0.5));
        defaultProperties.put("WALL_FORCE", new Value(1000.0));
        defaultProperties.put("EDGE_ATTRACTION", new Value(0.2));
        defaultProperties.put("BEZIER_ACCURACY", new Value(15));
        defaultProperties.put("BEZIER_CURVE", new Value(0.5));
    }
    
    private void fillParserMap() {
        parsers.put("VERTEX_SIZE", parseInt);
        parsers.put("MIN_DISTANCE", parseInt);
        parsers.put("MAX_DISTANCE", parseInt);
        parsers.put("TIP_ANGLE", parseDouble);
        parsers.put("CANVAS_SIZE", parseInt);
        parsers.put("DISTANCE_OFFSET_FACTOR", parseDouble);
        parsers.put("FRAME_NAME", Value::new);
        parsers.put("VERTEX_REPULSION", parseDouble);
        parsers.put("NEGLIGIBLE_FORCE", parseDouble);
        parsers.put("WALL_FORCE", parseDouble);
        parsers.put("EDGE_ATTRACTION", parseDouble);
        parsers.put("BEZIER_ACCURACY", parseInt);
        parsers.put("BEZIER_CURVE", parseDouble);
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
    
    private static Function<String, Value> parseDouble = string -> {
        if (holdsDoubleValue(string)) {
            return new Value(Double.parseDouble(string));
        }
        throw new NumberFormatException("Expected Double value, got \"" + string + "\" instead.");
    };
    
    private static Function<String, Value> parseInt = string -> {
        if (holdsIntegerValue(string)) {
            return new Value(Integer.parseInt(string));
        }
        throw new NumberFormatException("Expected Integer value, got \"" + string + "\" instead.");
    };
    
}
