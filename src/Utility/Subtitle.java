package Utility;

public class Subtitle {

    private String mode;
    private String value;
    private String dataStructure;

    public Subtitle(String dataStructure) {
        this.dataStructure = dataStructure;
    }

    public Subtitle(Subtitle subtitle) {
        this.mode = subtitle.mode;
        this.value = subtitle.value;
        this.dataStructure = subtitle.dataStructure;
    }

    public String getSubtitle() {
        return mode + " " + "value " + value + " ";
    }


    public String getMode() {
        return mode;
    }

    public String getValue() {
        return value;
    }

    public String getDataStructure() {
        return dataStructure;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setDataStructure(String dataStructure) {
        this.dataStructure = dataStructure;
    }

}
