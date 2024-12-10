public class Deck
{
    int size = 0;
    Card head;
    Card tail;
    ArrayList<Card> deckList = new ArrayList<>();

    public Deck()
    {
        head = null;
        tail = null;
    }

    public int getSize()
    {
        return size;
    }

    public String peek()
    {
        return tail.name;
    }



    public void addCard(Card card)
    {
        if(size == 0)
        {
            head = card;
            tail = card;
            size++;
            return;
        }

        tail.next = card;
        card.prev = tail;
        tail = card;
        size++;
    }

    public void print()
    {
        Card currentCard = head;
        while(currentCard != null)
        {
            System.out.println(currentCard.name);
            currentCard = currentCard.next;
        }
    }

    public Card popBack()
    {
        size--;
        Card tempCard = tail;
        if(tail.prev != null)
        {
            tail.prev.next = null;
            tail = tail.prev;
            return tempCard;
        }
        tail = null;
        return tempCard;
    }

    public void shuffle()
    {
        while(size >= 1)
        {
            Card cardToShuffle = popBack();
            deckList.add(cardToShuffle);
            System.out.println(cardToShuffle.name + " added to shuffle pile.");
        }

        System.out.println("shuffling...");
        deckList.shuffle();

        int sizeOfShufflePile = deckList.size;
        deckList.print();
        for(int i = 0; i < sizeOfShufflePile; i++)
        {
            Card nextCard = deckList.get(i);
            System.out.println(nextCard.name + " added to deck.");

            if(size == 0)
            {
                size++;
                head = nextCard;
                tail = nextCard;
                nextCard.next = null;
                nextCard.prev = null;
            }
            else
            {
                size++;
                tail.next = nextCard;
                nextCard.next = null;
                nextCard.prev = tail;
                tail = nextCard;
            }
        }
    }
}
