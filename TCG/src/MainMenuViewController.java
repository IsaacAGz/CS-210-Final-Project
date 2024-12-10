import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainMenuViewController implements Initializable
{

    @FXML private ImageView MainMenuBackground;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        Image menuBackground = new Image("./images/MenuBackground.png");
        MainMenuBackground.setImage(menuBackground);
    }

    public void playButtonPushed(ActionEvent event) throws IOException
    {
        Parent playAreaParent = FXMLLoader.load(getClass().getResource("PlayAreaView.fxml"));
        Scene playAreaScene = new Scene(playAreaParent);

        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(playAreaScene);
        window.show();
    }

    public void deckButtonPushed(ActionEvent event) throws IOException
    {
        Parent deckParent = FXMLLoader.load(getClass().getResource("DeckView.fxml"));
        Scene deckScene = new Scene(deckParent);
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(deckScene);
        window.show();
    }


}
