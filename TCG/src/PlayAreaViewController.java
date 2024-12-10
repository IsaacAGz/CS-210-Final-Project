import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class PlayAreaViewController implements Initializable
{
    @FXML private TextField NPCDeckCount;
    @FXML private TextField PlayerDeckCount;
    @FXML private ImageView NPCDeckImageView;
    @FXML private ImageView PlayerDeckImageView;
    @FXML private ImageView BackgroundView;
    @FXML private TextField NPCPoints;
    @FXML private TextField PlayerPoints;
    @FXML private ImageView NPCIcon;
    @FXML private ImageView PlayerIcon;
    @FXML private AnchorPane MenuPane;
    @FXML private AnchorPane PlayAreaBackgroundPane;
    @FXML private ImageView PlayerTurn;
    @FXML private ImageView OpponentTurn;
    @FXML private HBox PlayerHand = new HBox(10);
    @FXML private HBox OpponentHand = new HBox(10);
    @FXML private HBox PlayerActiveCards = new HBox(10);
    @FXML private HBox OpponentActiveCards = new HBox(10);
    @FXML private TextField Player1ManaCounter;
    @FXML private TextField Player2ManaCounter;

    public Player player1 = new Player();
    public Player player2 = new Player();

    Player currentPlayer = player1;
    Player goesNext;
    int phase;
    private Map<String, Card> allCards;

    private ArrayList<ImageView> itemList = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {

        allCards = new HashMap<>();
        setupAllCards();

        player1.buildDeck();
        player1.shuffleDeck();
        currentPlayer = player1;
        for(int i = 0; i < 5; i++)
        {
            draw();
        }

        player2.buildDeck();
        player2.shuffleDeck();
        currentPlayer = player2;
        for(int i = 0; i < 5; i++)
        {
            draw();
        }

        NPCPoints.setText(player2.getPoints()+"/20");
        PlayerPoints.setText(player1.getPoints()+"/20");
        Player1ManaCounter.setText(player1.getCurrentMana() + "/" + player1.getMaxMana());
        Player2ManaCounter.setText(player2.getCurrentMana() + "/" + player2.getMaxMana());
        NPCDeckCount.setText(player2.deck.getSize()+"");
        PlayerDeckCount.setText(player1.deck.getSize()+"");

        Image backOfCardImage= new Image("./images/PlayerDetails.png");
        Image npcIcon = new Image("./images/NPCIcon.jpg");
        Image playerIcon = new Image("./images/PlayerIcon.jpg");
        Image backgroundImage = new Image("./images/PlayAreaBackground.png");
        Image PlayerBanner = new Image("./images/Banner.jpg");
        Image OpponentBanner = new Image("./images/Banner.jpg");

        BackgroundView.setImage(backgroundImage);
        NPCIcon.setImage(npcIcon);
        PlayerIcon.setImage(playerIcon);
        NPCDeckImageView.setImage(backOfCardImage);
        PlayerDeckImageView.setImage(backOfCardImage);
        PlayerTurn.setImage(PlayerBanner);
        OpponentTurn.setImage(OpponentBanner);

        playPhase(player1);
    }

    public void menuButtonPushed(ActionEvent event) throws IOException
    {
        MenuPane.toFront();
        //MenuPane.setVisible(true);
    }

    public void closeButtonPushed(ActionEvent event) throws IOException
    {
        MenuPane.toBack();
        //MenuPane.setVisible(false);
    }

    public void exitButtonPushed(ActionEvent event) throws IOException
    {
        Parent mainMenu = FXMLLoader.load(getClass().getResource("MainMenuView.fxml"));

        Scene mainMenuScene = new Scene(mainMenu);
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(mainMenuScene);
        window.show();
    }

    public void nextPhaseButtonPushed(ActionEvent event) throws IOException
    {
        if(phase == 3)
        {
            playPhase(goesNext);
        }
        else if (phase == 2)
        {
            endPhase(currentPlayer);
        }
        else
        {
            combatPhase(currentPlayer);
        }
    }

    public void endTurnButtonPushed(ActionEvent event) throws IOException
    {
        playPhase(goesNext);
    }

    public void playPhase(Player player)
    {

        currentPlayer = player;
        currentPlayer.manaPlayed = false;
        currentPlayer.setCurrentMana();
        phase = 1;
        draw();

        if(player == player1)
        {
            System.out.println("Players play phase.");
            activateHand();

            Player1ManaCounter.setText(player1.getCurrentMana() + "/" + player1.getMaxMana());
            PlayerTurn.setVisible(true);

            //play phase turn banner
            FadeTransition ft = new FadeTransition(Duration.seconds(2),PlayerTurn);
            ft.setFromValue(1.0);
            ft.setToValue(0.001);
            ft.play();

            goesNext = player2;
        }
        else
        {
            int index = 0;
            System.out.println("Opponent's Play Phase.");
            ImageView card = (ImageView) OpponentHand.getChildren().get(index);
            Player2ManaCounter.setText(player2.getCurrentMana() + "/" + player2.getMaxMana());
            playCard(card);

            disableHand();
            OpponentTurn.toFront();
            FadeTransition ft = new FadeTransition(Duration.seconds(2),OpponentTurn);
            ft.setFromValue(1.0);
            ft.setToValue(0.001);
            ft.play();
            ft.setOnFinished(e ->
            {
                OpponentTurn.toBack();
            });

            goesNext = player1;
        }


    }

    public void combatPhase(Player player)
    {
        System.out.println("combat phase");
        disableHand();
        activatePlayerCardsForCombat();
        phase = 2;
    }

    public void endPhase(Player player)
    {
        System.out.println("end phase");
        disablePlayerCards();
        activateSpells();
        phase = 3;

    }

    private void draw()
    {
        //removes a card from the deck linked list of cards
        Card newCard = currentPlayer.deck.popBack();
        System.out.println(newCard.name + " drawn.");

        if(currentPlayer == player1)
        {
            boolean canPlay = checkCost(newCard.cost);
            Image cardImage = new Image("./images/" + newCard.name + ".png");
            ImageView cardImageView = new ImageView();
            cardImageView.setImage(cardImage);
            cardImageView.setId(newCard.name);
            cardImageView.setFitHeight(150);
            cardImageView.setFitWidth(100);
            cardImageView.setOnMouseClicked(e ->
            {
                selectCard(cardImageView, canPlay);
            });
            PlayerHand.getChildren().add(cardImageView);
            PlayerDeckCount.setText(player1.deck.getSize()+"");
        }
        else
        {
            ImageView cardImageView = new ImageView(new Image("./images/CardBack.png"));
            cardImageView.setFitHeight(150);
            cardImageView.setFitWidth(100);
            cardImageView.setId(newCard.name);
            OpponentHand.getChildren().add(cardImageView);
            NPCDeckCount.setText(player2.deck.getSize()+"");
        }
    }

    //opens new window that enlarges card to see details, play or add as mana
    private void selectCard(ImageView card, boolean canPlay)
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("PopUp.fxml"));

        try
        {
            Parent popUpParent = loader.load();
            PopUpController controller = loader.getController();
            controller.setMainController(this);
            controller.cardImage(card,canPlay, allCards.get(card.getId()));
            Scene popUpScene = new Scene(popUpParent);
            Stage popUpStage = new Stage();
            popUpStage.setTitle(card.getId());
            popUpStage.setScene(popUpScene);
            popUpStage.show();
        }

        catch(Exception e)
        {
            System.out.println("Error selecting card");
        }

    }

    public void playCard(ImageView cardView)
    {
        TranslateTransition playCardAnimation = new TranslateTransition(Duration.seconds(1));

        Card cardUsed = allCards.get(cardView.getId());

        //update the spent mana cost
        currentPlayer.payMana(cardUsed.cost);

        if(currentPlayer == player1)
        {
            int index = PlayerHand.getChildren().indexOf(cardView);
            if(allCards.get(cardView.getId()).type.equals("Item"))
            {
                attachItem(cardUsed, cardView,index);
                return;
            }
            if(allCards.get(cardView.getId()).type.equals("Spell"))
            {
                playSpell(cardUsed, cardView, index);
                return;
            }

            currentPlayer.activateCard(cardUsed);

            double startingX = PlayerHand.getLayoutX() + (100 * index);
            double startingY = PlayerHand.getLayoutY();
            double endingX = PlayerActiveCards.getLayoutX() - PlayerHand.getLayoutX() - ((index - PlayerActiveCards.getChildren().size()) * 100);
            double endingY = PlayerActiveCards.getLayoutY() - PlayerHand.getLayoutY();

            //play card animation
            ImageView tempCard = new ImageView();
            tempCard.setX(startingX);
            tempCard.setY(startingY);
            tempCard.setImage(new Image("./images/" + cardView.getId() + ".png"));
            tempCard.setFitWidth(100);
            tempCard.setFitHeight(150);
            PlayAreaBackgroundPane.getChildren().add(tempCard);
            playCardAnimation.setNode(tempCard);
            playCardAnimation.setByY(endingY);
            playCardAnimation.setByX(endingX);

            ImageView cardPlaced = new ImageView();
            cardPlaced.setFitHeight(150);
            cardPlaced.setFitWidth(100);
            cardPlaced.setImage(new Image("./images/" + cardView.getId() + ".png"));
            cardPlaced.setId(cardView.getId());

            PlayerHand.getChildren().get(index).setVisible(false);

            playCardAnimation.setOnFinished(e -> {
                PlayAreaBackgroundPane.getChildren().remove(tempCard);
                AnchorPane newCardPane = new AnchorPane();
                newCardPane.setMaxHeight(150);
                newCardPane.setMaxWidth(100);
                newCardPane.getChildren().add(cardPlaced);
                newCardPane.setId(cardPlaced.getId() + "Pane");

                TextField cardName = new TextField();
                cardName.setText(cardPlaced.getId());
                cardName.setId(cardPlaced.getId() + "TextField");
                cardName.setEditable(false);
                cardName.setStyle("-fx-background-color: transparent;");
                cardName.setMaxWidth(100);
                cardName.setAlignment(Pos.CENTER);
                newCardPane.getChildren().add(cardName);

                TextField cardStats = new TextField();
                cardStats.setId(cardPlaced.getId() + "Stats");
                cardStats.setEditable(false);
                cardStats.setMaxWidth(40);
                cardStats.setStyle("-fx-background-color: transparent;");
                cardStats.setAlignment(Pos.CENTER);
                cardStats.setLayoutX(15);
                cardStats.setLayoutY(122);
                cardStats.setText(cardUsed.attack + " | " + cardUsed.health);
                newCardPane.getChildren().add(cardStats);

                PlayerActiveCards.getChildren().add(newCardPane);

                System.out.println("Index of "+ cardPlaced.getId() + " ImageView : " + newCardPane.getChildren().indexOf(cardPlaced));

                PlayerHand.getChildren().remove(index);
            });

            playCardAnimation.play();

            Player1ManaCounter.setText(player1.getCurrentMana() + "/" + player1.getMaxMana());
        }

        //opponent play phase
        else
        {
            currentPlayer.activateCard(cardUsed);

            int index = OpponentHand.getChildren().indexOf(cardView);

            double startingX = OpponentHand.getLayoutX() - (100 * index);
            double startingY = OpponentHand.getLayoutY();
            double endingX = OpponentActiveCards.getLayoutX() - OpponentHand.getLayoutX() - ((index - OpponentActiveCards.getChildren().size()) * 100);
            double endingY = OpponentActiveCards.getLayoutY() - OpponentHand.getLayoutY();

            ImageView tempCard = new ImageView();
            tempCard.setX(startingX);
            tempCard.setY(startingY);
            tempCard.setImage(new Image("./images/" + cardView.getId() + ".png"));
            tempCard.setFitWidth(100);
            tempCard.setFitHeight(150);

            ImageView cardPlaced = new ImageView();
            cardPlaced.setFitHeight(150);
            cardPlaced.setFitWidth(100);
            cardPlaced.setImage(new Image("./images/" + cardView.getId() + ".png"));
            cardPlaced.setId(cardView.getId());

            TextField cardStats = new TextField();
            cardStats.setId(cardPlaced.getId() + "Stats");
            cardStats.setEditable(false);
            cardStats.setMaxWidth(40);
            cardStats.setStyle("-fx-background-color: transparent;");
            cardStats.setAlignment(Pos.CENTER);
            cardStats.setLayoutX(15);
            cardStats.setLayoutY(122);
            cardStats.setText(cardUsed.attack + " | " + cardUsed.health);

            PlayAreaBackgroundPane.getChildren().add(tempCard);
            OpponentHand.getChildren().get(index).setVisible(false);

            playCardAnimation.setByY(endingY);
            playCardAnimation.setByX(endingX);

            playCardAnimation.setNode(tempCard);

            playCardAnimation.setOnFinished(e ->
            {
                PlayAreaBackgroundPane.getChildren().remove(tempCard);
                AnchorPane newCardPane = new AnchorPane();
                newCardPane.setMaxHeight(150);
                newCardPane.setMaxWidth(100);
                newCardPane.getChildren().add(cardPlaced);
                TextField cardName = new TextField();
                cardName.setText(cardPlaced.getId());
                cardName.setEditable(false);
                cardName.setStyle("-fx-background-color: transparent;");
                cardName.setMaxWidth(100);
                cardName.setAlignment(Pos.CENTER);
                newCardPane.getChildren().add(cardName);
                newCardPane.getChildren().add(cardStats);
                OpponentActiveCards.getChildren().add(newCardPane);
                OpponentHand.getChildren().remove(index);
            });

            playCardAnimation.play();

            Player2ManaCounter.setText(player2.getCurrentMana() + "/" + player2.getMaxMana());

        }
    }

    public void attachItem(Card itemCard, ImageView cardView, int indexOnHand)
    {
        TranslateTransition playCardAnimation = new TranslateTransition(Duration.seconds(1));

        DropShadow glow = new DropShadow();
        glow.setColor(Color.CYAN);
        glow.setRadius(20);
        glow.setSpread(0.5);


        for(int i = 0; i < PlayerActiveCards.getChildren().size(); i++)
        {
            AnchorPane currentAnchorPane = (AnchorPane) PlayerActiveCards.getChildren().get(i);
            ImageView currentCardImageView = (ImageView) currentAnchorPane.getChildren().get(0);

            //make all card that can take an item glow
            if(!currentPlayer.activeCards.get(i).hasItem)
            {
                PlayerActiveCards.getChildren().get(i).setEffect(glow);
            }

            //prep all cards to take item
            PlayerActiveCards.getChildren().get(i).setOnMouseClicked(e ->
            {
                int index = PlayerActiveCards.getChildren().indexOf(currentAnchorPane);

                ImageView itemView = new ImageView();
                itemView.setImage(new Image("./images/" + itemCard.name + ".png"));
                itemView.setFitWidth(100);
                itemView.setFitHeight(150);

                double startingX = PlayerHand.getLayoutX() + (100 * PlayerHand.getChildren().indexOf(cardView));
                double startingY = PlayerHand.getLayoutY();
                double endingX = PlayerActiveCards.getLayoutX() - PlayerHand.getLayoutX() + ((index - PlayerHand.getChildren().indexOf(cardView)) * 100);
                double endingY = PlayerActiveCards.getLayoutY() - PlayerHand.getLayoutY() + 30;

                itemView.setY(startingY);
                itemView.setX(startingX);

                PlayAreaBackgroundPane.getChildren().add(itemView);
                itemList.add(itemView);

                PlayerHand.getChildren().get(indexOnHand).setVisible(false);
                PlayerActiveCards.toFront();

                playCardAnimation.setNode(itemView);

                playCardAnimation.setByX(endingX);
                playCardAnimation.setByY(endingY);

                playCardAnimation.setOnFinished(ex ->
                {
                    PlayerHand.getChildren().remove(cardView);
                });

                playCardAnimation.play();

                currentPlayer.activeCards.get(index).addItem(itemCard,itemView);

                //update stats
                TextField cardStats = (TextField) currentAnchorPane.getChildren().get(2);
                cardStats.setText(currentPlayer.activeCards.get(index).getStats());

                for(int j = 0; j < PlayerActiveCards.getChildren().size(); j++)
                {
                    PlayerActiveCards.getChildren().get(j).setEffect(null);
                    PlayerActiveCards.getChildren().get(j).setOnMouseClicked(null);
                }
                Player1ManaCounter.setText(player1.getCurrentMana() + "/" + player1.getMaxMana());
            });
        }
    }

    public void playSpell(Card spellCard, ImageView spellView, int index)
    {
        DropShadow glow = new DropShadow();
        glow.setColor(Color.YELLOW);
        glow.setRadius(20);
        glow.setSpread(0.5);
        ImageView currentCardView;

        if(spellCard.attack != 0)
        {
            for (int i = 0; i < OpponentActiveCards.getChildren().size(); i++)
            {
                AnchorPane opponentAnchorPane = (AnchorPane) OpponentActiveCards.getChildren().get(i);
                currentCardView = (ImageView) opponentAnchorPane.getChildren().get(0);
                int indexOfCard = OpponentActiveCards.getChildren().indexOf(opponentAnchorPane);
                currentCardView.setEffect(glow);

                currentCardView.setOnMouseClicked(e ->
                        {
                            Card defenderCard = player2.activeCards.get(indexOfCard);
                            defenderCard.recieveDamage(spellCard.attack);
                            if (defenderCard.health <= 0)
                            {
                                player1.addPoints(defenderCard.points);
                                PlayerPoints.setText(player1.getPoints() + "/20");
                                System.out.println(indexOfCard + " test ");
                                OpponentActiveCards.getChildren().remove(indexOfCard);
                                player2.activeCards.remove(indexOfCard);
                            }

                            PlayerHand.getChildren().remove(index);

                            for (int j = 0; j < OpponentActiveCards.getChildren().size(); j++)
                            {
                                AnchorPane currentAnchorPane = (AnchorPane) OpponentActiveCards.getChildren().get(j);
                                currentAnchorPane.setOnMouseClicked(null);
                                currentAnchorPane.getChildren().get(0).setEffect(null);
                            }

                            Player1ManaCounter.setText(player1.getCurrentMana() + "/" + player1.getMaxMana());
                        }
                );
            }
        }
        else if (spellCard.health != 0)
        {
            for(int i = 0; i < PlayerActiveCards.getChildren().size(); i++)
            {
                AnchorPane currentAchorPane = (AnchorPane) PlayerActiveCards.getChildren().get(i);
                currentCardView = (ImageView) currentAchorPane.getChildren().get(0);
                int indexOfCard = PlayerActiveCards.getChildren().indexOf(currentAchorPane);
                currentCardView.setEffect(glow);

                currentCardView.setOnMouseClicked(e ->
                    {
                        Card cardToHeal = player1.activeCards.get(indexOfCard);
                        cardToHeal.heal(spellCard.health);

                        //update card health
                        TextField cardStats = (TextField) currentAchorPane.getChildren().get(2);
                        cardStats.setText(cardToHeal.getStats());
                        currentAchorPane.getChildren().set(2,cardStats);

                        PlayerHand.getChildren().remove(index);

                        for(int j = 0; j < PlayerActiveCards.getChildren().size(); j++)
                        {
                            PlayerActiveCards.getChildren().get(j).setEffect(null);
                        }
                    }
                );
            }
        }
    }

    public void disableHand()
    {
        for(int i = 0; i < PlayerHand.getChildren().size(); i++)
        {
            ImageView currentImageView = (ImageView) PlayerHand.getChildren().get(i);
            currentImageView.setOnMouseClicked(null);
        }
    }

    public void activateHand()
    {
        System.out.println("Activating hand");
        for(int i = 0; i < PlayerHand.getChildren().size(); i++)
        {
            ImageView currentImageView = (ImageView) PlayerHand.getChildren().get(i);
            currentImageView.setOnMouseClicked(e ->
            {
                selectCard(currentImageView, checkCost(allCards.get(currentImageView.getId()).cost));
            }
            );
        }
    }

    //select a card from the player cards; makes them glow and allows you to select and opponents card
    public void activatePlayerCardsForCombat()
    {
        DropShadow glow = new DropShadow();
        glow.setColor(Color.CYAN);
        glow.setRadius(20);
        glow.setSpread(0.5);
        for(int i = 0; i < PlayerActiveCards.getChildren().size(); i++)
        {
            AnchorPane currentAnchorPane = (AnchorPane) PlayerActiveCards.getChildren().get(i);

            ImageView currentImageView = (ImageView) currentAnchorPane.getChildren().get(0);

            int indexOfActiveCard = PlayerActiveCards.getChildren().indexOf(currentAnchorPane);

            PlayerActiveCards.getChildren().get(i).setOnMouseClicked(e ->
            {
                if(currentImageView.getEffect() != null)
                {
                    currentImageView.setEffect(null);
                }
                else if(currentImageView.getEffect() == null)
                {
                    currentImageView.setEffect(glow);
                }
                disablePlayerCards();

                activateCard(indexOfActiveCard);
                activateOpponentHand(indexOfActiveCard);
            });
        }
    }

    //allows you de-activate a card previously selected for combat
    public void activateCard(int i)
    {
        DropShadow glow = new DropShadow();
        glow.setColor(Color.CYAN);
        glow.setRadius(20);
        glow.setSpread(0.5);
        AnchorPane currenAnchorPane = (AnchorPane) PlayerActiveCards.getChildren().get(i);
        ImageView currentImageView = (ImageView) currenAnchorPane.getChildren().getFirst();

        PlayerActiveCards.getChildren().get(i).setOnMouseClicked(e ->
        {
            if (currentImageView.getEffect() != null)
            {
                currentImageView.setEffect(null);
                activatePlayerCardsForCombat();
            }
            else if (currentImageView.getEffect() == null)
            {
                currentImageView.setEffect(glow);
            }
        });
    }

    public void disablePlayerCards()
    {
        for(int i = 0; i < PlayerActiveCards.getChildren().size(); i++)
        {
            AnchorPane currentAnchorPane = (AnchorPane) PlayerActiveCards.getChildren().get(i);
            ImageView currentImageView = (ImageView) currentAnchorPane.getChildren().get(0);

            currentImageView.setEffect(null);
            currentImageView.setOnMouseClicked(null);
        }
    }

    public void activateSpells()
    {
        DropShadow glow = new DropShadow();
        glow.setColor(Color.CYAN);
        glow.setRadius(20);
        glow.setSpread(0.5);

        for(int i = 0; i < PlayerHand.getChildren().size(); i++)
        {
            String currentCard = PlayerHand.getChildren().get(i).getId();
            if(allCards.get(currentCard).getType().equals("Spell"))
            {
                ImageView currentImageView = (ImageView) PlayerHand.getChildren().get(i);
                PlayerHand.getChildren().get(i).setOnMouseClicked(e->
                {
                    int index = PlayerHand.getChildren().indexOf(currentImageView);
                    selectCard(currentImageView, checkCost(allCards.get(currentImageView.getId()).cost));
                });
            }
        }
    }

    //allows player to select opponent card, passing the index of the card the player is going to use for combat, when the opponent card is selected, both cards enter combat.
    public void activateOpponentHand(int indexOfAttacker)
    {
        DropShadow glow = new DropShadow();
        glow.setColor(Color.YELLOW);
        glow.setRadius(20);
        glow.setSpread(0.5);
        for(int i = 0; i < OpponentActiveCards.getChildren().size(); i++)
        {
            AnchorPane opponentAnchorPane = (AnchorPane) OpponentActiveCards.getChildren().get(i);
            ImageView currentImageView = (ImageView) opponentAnchorPane.getChildren().get(0);
            int indexOfDefender = OpponentActiveCards.getChildren().indexOf(opponentAnchorPane);
            OpponentActiveCards.getChildren().get(i).setOnMouseClicked(e ->
            {
                int index = opponentAnchorPane.getChildren().indexOf(currentImageView);
                if(currentImageView.getEffect() != null)
                {
                    currentImageView.setEffect(null);
                }
                else if(currentImageView.getEffect() == null)
                {
                    currentImageView.setEffect(glow);
                    combat(indexOfAttacker, indexOfDefender);
                }
            });
        }
    }

    public void combat(int attackerIndex, int defenderIndex)
    {
        System.out.println("attacker index " + attackerIndex);
        Card attackerCard = player1.activeCards.get(attackerIndex);
        Card defenderCard = player2.activeCards.get(defenderIndex);

        AnchorPane opponentAnchorPane = (AnchorPane) OpponentActiveCards.getChildren().get(defenderIndex);
        opponentAnchorPane.getChildren().getFirst().setEffect(null);

        AnchorPane playerAnchorPane = (AnchorPane) PlayerActiveCards.getChildren().get(attackerIndex);
        playerAnchorPane.getChildren().getFirst().setEffect(null);

        System.out.println(attackerCard.name + " dealt " + attackerCard.attack + " damage to " + defenderCard.name + ".");
        System.out.println(defenderCard.name + "dealt " + defenderCard.attack + "damage to " + attackerCard.name + ".");
        System.out.println("Too violent to show");

        OpponentActiveCards.getChildren().get(defenderIndex).setEffect(null);

        defenderCard.recieveDamage(attackerCard.attack);
        attackerCard.recieveDamage(defenderCard.attack);

        TextField attackerStats = (TextField) playerAnchorPane.getChildren().get(2);
        attackerStats.setText(attackerCard.getStats());
        playerAnchorPane.getChildren().set(2,attackerStats);

        TextField defenderStats = (TextField) opponentAnchorPane.getChildren().get(2);
        defenderStats.setText(defenderCard.getStats());
        opponentAnchorPane.getChildren().set(2,defenderStats);

        if(defenderCard.health <= 0)
        {
            if(defenderCard.hasItem)
            {
                PlayAreaBackgroundPane.getChildren().remove(attackerCard.itemView);
            }
            player2.removeCard(defenderIndex);
            player2.activeCards.print();
            player1.addPoints(attackerCard.points);
            PlayerPoints.setText(player1.getPoints() + "/20");
            checkWinCondition(player1);
            OpponentActiveCards.getChildren().remove(defenderIndex);
        }
        if(attackerCard.health <= 0)
        {
            if(attackerCard.hasItem)
            {
                PlayAreaBackgroundPane.getChildren().remove(attackerCard.itemView);
            }
            shiftItems();
            player1.removeCard(attackerIndex);
            player1.activeCards.print();
            player2.addPoints(defenderCard.points);
            NPCPoints.setText(player2.getPoints() + "/20");
            checkWinCondition(player2);
            PlayerActiveCards.getChildren().remove(attackerIndex);
        }

        activatePlayerCardsForCombat();
    }

    public void setupAllCards()
    {
        String csvFile = "C:\\Users\\ceroi\\OneDrive\\Escritorio\\Code\\CS 210\\TCG\\src\\Decks\\allCards.csv";
        String line = "";
        String splitter = ",";

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile)))
        {
            br.readLine();

            while((line = br.readLine()) != null)
            {
                String[] data = line.split(splitter);
                Card card = new Card(data[0], Integer.parseInt(data[1]), Integer.parseInt(data[2]), Integer.parseInt(data[3]), data[4], Integer.parseInt(data[5]), data[6]);
                allCards.put(data[0],card);
            }
        }
        catch(IOException e)
        {
            System.out.println("Error");
        }
    }

    public void checkWinCondition(Player player)
    {
        if(player.getPoints() > 20)
        {
            System.out.println("Game end!");
        }
    }

    public void playAsMana(ImageView card)
    {
        int index = PlayerHand.getChildren().indexOf(card);
        if(!currentPlayer.manaPlayed)
        {
            currentPlayer.manaPlayed = true;
            if (currentPlayer == player1)
            {
                player1.addMana();
                PlayerHand.getChildren().remove(index);
                Player1ManaCounter.setText(player1.getCurrentMana() + "/" + player1.getMaxMana());
            } else
            {
                player2.addMana();
                OpponentHand.getChildren().remove(index);
                Player2ManaCounter.setText(player2.getCurrentMana() + "/" + player2.getMaxMana());
            }
        }
        activateHand();
    }

    public boolean checkCost(int cost)
    {
        if(currentPlayer.getCurrentMana() >= cost)
        {
            return true;
        }
        else
            return false;
    }

    public void shiftItems()
    {
        try
        {
            ImageView currentImageView;
            for(int i = 0; i < itemList.size ; i++)
            {
                currentImageView = (ImageView) itemList.getImageView(i);
                currentImageView.setLayoutX(currentImageView.getLayoutX() - 100);
            }
        }
        catch (ClassCastException e )
        {
            System.out.println("no items");
        }
    }
}

