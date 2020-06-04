package cluedo.DTO;

import cluedo.modele.Point;

public class PointDTO {

    /*
     * ATTRIBUTS
     */
    private int x;
    private int y;


    /*
     * METHODES
     */
    public static PointDTO creerPointDTO(Point point){

        PointDTO pointDTO = new PointDTO();
        pointDTO.setX(point.getX());
        pointDTO.setY(point.getY());

        return pointDTO;
    }



    /*
     * GETTERS / SETTERS
     */
    public int getX() { return x; }
    public void setX(int x) { this.x = x; }

    public int getY() { return y; }
    public void setY(int y) { this.y = y; }
}
