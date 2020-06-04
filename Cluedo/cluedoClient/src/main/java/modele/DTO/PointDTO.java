package modele.DTO;


public class PointDTO {

    /*
     * ATTRIBUTS
     */
    private int x;
    private int y;


    /*
     * METHODES
     */
    public PointDTO(){

    }

    public PointDTO(int x, int y){
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

    @Override
    public String toString() {
        return "("+x+","+y+")";
    }
}
