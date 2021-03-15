package bsplib.util;

/**
 * Map source format enumeration.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public enum SourceFormat {

    AUTO("Automatic"),
    OLD("Source 2004 to 2009"),
    NEW("Source 2010 and later");

    private final String name;

    SourceFormat(String name) {
        this.name = name;
    }

    public static SourceFormat fromOrdinal(int index) {
        return EnumConverter.fromOrdinal(SourceFormat.class, index);
    }

    @Override
    public String toString() {
        return name;
    }
}
