package TicketClients;
import java.util.concurrent.Semaphore;
import java.time.LocalTime;
public class Ticket {
    private int availableTickets = 10;
    private Semaphore semaphore = new Semaphore(1);

    public void bookingTickets(String clientName) {
        LocalTime currentTime = LocalTime.now();
        if (currentTime.isAfter(LocalTime.MIDNIGHT) && currentTime.isBefore(LocalTime.of(6, 0))) {
            System.out.println("I'm sorry, unfortunately the booking from 00:00 to 06:00 does not work");
            return;
        }

            try {
                semaphore.acquire(); //блокуємо доступ
                System.out.println();
                if (availableTickets > 0) {
                  
                    System.out.println(clientName + " has booked a ticket. Tickets left: " + (--availableTickets) + ".");
                } else {
                    System.out.println( clientName + " could not book the ticket. Unfortunately, the tickets are sold out.");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                semaphore.release(); //звільняємо доступ
            }
    }
}

