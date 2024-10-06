package TicketClients;
class Client extends Thread{
    private Ticket booking;
    private String clientName;

    public Client(Ticket booking, String clientName){
        this.booking = booking;
        this.clientName = clientName;
    }

    @Override
    public void run(){
        booking.bookingTickets(clientName);
    }
}
