package Rendering;

public enum Background {
    LAKE("/Resources/lake.jpg"),
    SUNSET("/Resources/sunset.jpg");

    private final String path;

    Background(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return path;
    }
}
