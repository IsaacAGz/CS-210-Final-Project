import javafx.scene.image.ImageView;

import java.awt.*;

public class Card
{
    String name;
    int attack;
    int health;
    int maxHealth;
    Card next;
    Card prev;
    Card item;
    int cost;
    String type;
    Boolean hasItem;
    int points;
    ImageView itemView;
    String descirption;



    public Card(String name, int attack, int health, int cost, String type, int points, String descirption)
    {
        this.cost = cost;
        this.name = name;
        this.attack = attack;
        this.health = health;
        this.type = type;
        this.points = points;
        this.descirption = descirption;
        maxHealth = health;
        hasItem = false;
    }

    public void recieveDamage(int damage)
    {
        health -= damage;
    }

    public String getType()
    {
        return type;
    }

    public void addItem(Card card, ImageView itemView)
    {
        item = card;
        attack += card.attack;
        health += card.health;
        maxHealth += card.health;
        hasItem = true;
        this.itemView = itemView;
        System.out.println(name + " attack increased by: " + card.attack);
        System.out.println(name + " health increased by: " + card.health);
    }


    public void heal(int healthAdded)
    {
        int counter = 0;
        while(health != maxHealth && counter != healthAdded)
        {
            health++;
            counter++;
        }
        if(health == maxHealth)
        {
            System.out.println(name + " at max health.");
        }
    }

    public String getStats()
    {
        return attack + " | " + health;
    }

    public String getCostString()
    {
        return String.valueOf(cost);
    }
}
