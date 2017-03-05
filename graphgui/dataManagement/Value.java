package graphgui.dataManagement;

public class Value {
    private String sValue;
    private double dValue;
    private int iValue;
    private ValueType type = ValueType.UNDEFINED;
    
    public Value(String value) {
        set(value);
    }
    
    public Value(double value) {
        set(value);
    }
    
    public Value(int value) {
        set(value);
    }
    
    public void set(String value) {
        if (type != ValueType.STRING && type != ValueType.UNDEFINED)
            throw new IllegalArgumentException("This Value-Object has already been initialized as " + String.valueOf(type) + ".");
        sValue = value;
        type = ValueType.STRING;
    }
    
    public String string() {
        if (type != ValueType.STRING)
            throw new IllegalArgumentException("This is not a String-Value-Object.");
        return sValue;
    }
    
    public void set(double value) {
        if (type != ValueType.DOUBLE && type != ValueType.UNDEFINED)
            throw new IllegalArgumentException("This Value-Object has already been initialized as " + String.valueOf(type) + ".");
        dValue = value;
        type = ValueType.DOUBLE;
    }
    
    public double getDouble() {
        if (type != ValueType.DOUBLE)
            throw new IllegalArgumentException("This is not a Double-Value-Object.");
        return dValue;
    }
    
    public void set(int value) {
        if (type != ValueType.INT && type != ValueType.UNDEFINED)
            throw new IllegalArgumentException("This Value-Object has already been initialized as " + String.valueOf(type) + ".");
        iValue = value;
        type = ValueType.INT;
    }
    
    public int getInt() {
        if (type != ValueType.INT)
            throw new IllegalArgumentException("This is not a String-Value-Object.");
        return iValue;
    }
    
    public String toString() {
        switch(type) {
        case INT:
            return String.valueOf(iValue);
        case DOUBLE:
            return String.valueOf(dValue);
        default:
            return sValue;
        }
    }
    
    private enum ValueType {
        INT, DOUBLE, STRING, UNDEFINED;
    }
}
