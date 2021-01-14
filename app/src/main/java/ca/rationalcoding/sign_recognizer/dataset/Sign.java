package ca.rationalcoding.sign_recognizer.dataset;

public class Sign {
    public String name;
    public int[] color;
    public int shape;
    public String text;

    public Sign (String name, int[] color, int shape, String text) {
        this.name = name;
        this.color = color;
        this.shape = shape;
        this.text = text;
    }
}
