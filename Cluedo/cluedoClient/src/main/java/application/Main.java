package application;

import controleur.Controleur;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception {

        Controleur monControleur = new Controleur(primaryStage);

    }




    public static void main(String[] args) {
        launch(args);
    }
}