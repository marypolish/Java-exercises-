package TicketClient;

public class Main {
    public static void main(String[] args) {
        Ticket booking = new Ticket();

        //потоки це клієнти, створюємо їх
        for (int i = 1; i < 15; i++){
            Client client = new Client(booking, "Client " + i);
            client.start();

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }
}
