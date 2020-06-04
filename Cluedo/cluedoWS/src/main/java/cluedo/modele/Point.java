package cluedo.modele;

public class Point {

    /*
     * ATTRIBUTS
     */
    private int x;
    private int y;


    /*
     * CONSTRUCTEUR
     */
    public Point(){}

    public Point(int x, int y){
        this.x = x;
        this.y = y;
    }


    /*
     * GETTERS / SETTERS
     */
    public int getX() { return x; }
    public void setX(int x) { this.x = x; }

    public int getY() { return y; }
    public void setY(int y) { this.y = y; }
}
