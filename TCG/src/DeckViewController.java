import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DeckViewController implements Initializable
{

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {

    }

    @FXML
    public void mainMenuButtonPushed(ActionEvent event) throws IOException
    {
        Parent mainMenu = FXMLLoader.load(getClass().getResource("MainMenuView.fxml"));
        Scene mainMenuScene = new Scene(mainMenu);
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(mainMenuScene);
        window.show();
    }
}
