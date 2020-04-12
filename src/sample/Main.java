package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.apple.eawt.*;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        TransactionAnalyzerManager manager = new TransactionAnalyzerManager("./lib/id_map.json", "./lib/ss_map.json");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainView.fxml"));
        Parent root = loader.load();

        MainViewController controller = (MainViewController) loader.getController();
        controller.setModel(manager);
        primaryStage.setOnCloseRequest(windowEvent -> {
            // Save maps upon close
            controller.saveMaps();
        });



        primaryStage.setTitle("Statement Analyzer V2");
        primaryStage.setScene(new Scene(root, 1000, 800));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
