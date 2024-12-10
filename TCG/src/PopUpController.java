import com.sun.javafx.css.CssUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class PopUpController implements Initializable
{
    @FXML private ImageView CardView;
    @FXML private Button CloseButton;
    @FXML private AnchorPane Pane;
    @FXML private TextField CardStats;
    @FXML private TextField CardCost;
    @FXML private TextArea Description;
    @FXML private TextField CardName;

    private PlayAreaViewController playAreaViewController;
    boolean canPlay;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {

    }

    public void cardImage(ImageView card, boolean canPlay, Card cardObject)
    {
        CardView.setImage(new Image("./images/" + card.getId() + ".png"));
        CardView = card;
        CardName.setText(cardObject.name);
        CardStats.setText(cardObject.getStats());
        CardCost.setText(cardObject.getCostString());
        Description.setText(cardObject.descirption);
        this.canPlay = canPlay;
    }

    public void setMainController(PlayAreaViewController controller)
    {
        playAreaViewController = controller;
    }

    public void playCard()
    {
        if(canPlay)
        {
            CardView.setFitHeight(150);
            CardView.setFitWidth(100);
            playAreaViewController.playCard(CardView);
            Stage stage = (Stage) CloseButton.getScene().getWindow();
            stage.close();
        }
        else
        {
            System.out.println("Not enough Mana!");
        }
    }

    public void close()
    {
        Stage stage= (Stage) CloseButton.getScene().getWindow();
        stage.close();
    }

    public void PlayAsManaButtonPushed()
    {
        playAreaViewController.playAsMana(CardView);
        Stage stage = (Stage) CloseButton.getScene().getWindow();
        stage.close();
    }




}
