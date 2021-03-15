package bsplib.modules.geom;

public enum BrushMode {

    BRUSHPLANES("Brushes and planes"),
    ORIGFACE("Original faces"),
    ORIGFACE_PLUS("Original plus split faces"),
    SPLITFACE("Split faces");

    private final String name;

    BrushMode(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
