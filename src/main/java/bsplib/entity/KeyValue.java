package bsplib.entity;

import java.util.Map.*;

/**
 * Abstraction of an epair_t structure used for key-values.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class KeyValue implements Entry<String, String> {

    private final String key;
    private String value;

    public KeyValue(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String setValue(String value) {
        String old = this.value;
        this.value = value;
        return old;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('"');
        sb.append(key);
        sb.append("\" \"");
        sb.append(value);
        sb.append('"');
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + (this.key != null ? this.key.hashCode() : 0);
        hash = 41 * hash + (this.value != null ? this.value.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final KeyValue other = (KeyValue) obj;
        if ((this.key == null) ? (other.key != null) : !this.key.equals(other.key)) {
            return false;
        }
        if ((this.value == null) ? (other.value != null) : !this.value.equals(other.value)) {
            return false;
        }
        return true;
    }
}
