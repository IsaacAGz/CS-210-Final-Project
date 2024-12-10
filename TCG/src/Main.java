import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application
{
    public static void main(String[] args)
    {
    launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception
    {
        Parent mainMenu = FXMLLoader.load(getClass().getResource(("MainMenuView.fxml")));
        Scene scene = new Scene(mainMenu);
        stage.setTitle("Main Menu");
        stage.setScene(scene);
        stage.show();
    }
}