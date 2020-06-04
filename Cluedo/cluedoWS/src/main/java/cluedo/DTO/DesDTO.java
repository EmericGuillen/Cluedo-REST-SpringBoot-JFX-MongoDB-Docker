package cluedo.DTO;

import cluedo.modele.Des;

public class DesDTO {

    private int de1;
    private int de2;
    private int somme;


    public static DesDTO creerDesDTO(Des des){
        DesDTO desDTO = new DesDTO();
        desDTO.setDe1(des.getDe1());
        desDTO.setDe2(des.getDe2());
        desDTO.setSomme(des.getSomme());
        return desDTO;
    }

    public int getDe1() {
        return de1;
    }

    public void setDe1(int de1) {
        this.de1 = de1;
    }

    public int getDe2() {
        return de2;
    }

    public void setDe2(int de2) {
        this.de2 = de2;
    }

    public int getSomme() {
        return somme;
    }

    public void setSomme(int somme) {
        this.somme = somme;
    }

}
