import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

public class Player
{
    int points;
    Deck deck;
    int currentMana;
    int maxMana;
    public String name;
    boolean manaPlayed;
    ArrayList<Card> activeCards = new ArrayList<>();


    public Player()
    {
        points = 0;
        deck = new Deck();
    }

    public void activateCard(Card card)
    {
        activeCards.add(card);
    }

    public void removeCard(int index)
    {
        activeCards.remove(index);
    }

    public void addMana()
    {
        currentMana++;
        maxMana++;
    }

    public void shuffleDeck()
    {
        deck.shuffle();
    }

    public boolean payMana(int cost)
    {
        if(currentMana >= cost)
        {
            currentMana -= cost;
            return true;
        }
        else
        {
            return false;
        }
    }

    public int getPoints()
    {
        return points;
    }

    public void addPoints(int pointsToAdd)
    {
        points += pointsToAdd;
    }

    public void setCurrentMana()
    {
        currentMana = maxMana;
    }

    public int getMaxMana()
    {
        return maxMana;
    }

    public int getCurrentMana()
    {
        return currentMana;
    }

    public void buildDeck()
    {
        String csvFile = "C:\\Users\\ceroi\\OneDrive\\Escritorio\\Code\\CS 210\\TCG\\src\\Decks\\defaultDeck.csv";
        String line = "";
        String splitter = ",";

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile)))
        {
            br.readLine();

            while((line = br.readLine()) != null)
            {
                String[] data = line.split(splitter);
                Card card = new Card(data[0], Integer.parseInt(data[1]), Integer.parseInt(data[2]), Integer.parseInt(data[3]),data[4], Integer.parseInt(data[5]), data[6]);
                deck.addCard(card);
            }
        }
        catch(IOException e)
        {
            System.out.println("Error");
        }

    }
}
