import java.util.Arrays;
import java.util.Random;
import javafx.scene.image.ImageView;

public class ArrayList <T>
{
    private Object[] elements;
    public int size;

    public ArrayList()
    {
        elements = new Object[30];
        size=0;
    }

    public void add(T element)
    {
        checkCapacity();
        elements[size] = element;
        size++;
    }

    public void checkCapacity()
    {
        if(size == elements.length)
        {
            elements = Arrays.copyOf(elements,elements.length+10);
        }
    }

    public void shuffle()
    {
        Random random = new Random();
        for (int i = size - 1; i > 0; i--)
        {
            int j = random.nextInt(i + 1);
            swap(i, j);
        }
    }

    public boolean isEmpty()
    {
        if(size > 0)
        {
            return false;
        }
        return true;
    }

    private void swap(int i, int j)
    {
        Object temp = elements[i];
        elements[i] = elements[j];
        elements[j] = temp;
    }

    public Card get(int index)
    {
        return (Card) elements[index];
    }

    public ImageView getImageView(int index)
    {
        return (ImageView) elements[index];
    }

    public Card removeAndGet(int index)
    {
        Card card = (Card) elements[index];
        index++;
        while(elements[index] != null)
        {
            elements[index-1] = elements[index];
            index++;
        }

        System.out.println("size of shuffle pile: " + size + ".");
        System.out.println(card.name + " card removed from shuffle pile.");
        size--;
        return card;
    }

    public void remove(int index)
    {
        elements[index] = null;
        index++;
        while (elements[index] != null)
        {
            elements[index-1]  = elements[index];
            index++;
        }
        size--;
    }

    public void print()
    {
        Card currentCard;

        System.out.print("{");
        for(int i = 0; i < size; i++)
        {
            currentCard = (Card) elements[i];
            System.out.print(currentCard.name + ", ");
        }
        System.out.println("}");
        System.out.println("");
    }

}
