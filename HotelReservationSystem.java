import java.util.*;
import java.io.*;

enum RoomType {
    STANDARD, DELUXE, SUITE;

    @Override
    public String toString() {
        switch (this) {
            case STANDARD: return "Standard";
            case DELUXE: return "Deluxe";
            case SUITE: return "Suite";
            default: return name();
        }
    }

    public static RoomType fromInt(int choice) {
        switch (choice) {
            case 1: return STANDARD;
            case 2: return DELUXE;
            case 3: return SUITE;
            default: return null;
        }
    }
}

class Room {
    private int roomNumber;
    private RoomType type;
    private double pricePerNight;
    private boolean available;

    public Room(int roomNumber, RoomType type, double pricePerNight) {
        this.roomNumber = roomNumber;
        this.type = type;
        this.pricePerNight = pricePerNight;
        this.available = true;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public RoomType getType() {
        return type;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public String toString() {
        return "Room " + roomNumber + " (" + type + ") - Rs. "
                + String.format("%.2f", pricePerNight)
                + " per night - " + (available ? "Available" : "Booked");
    }
}

class Reservation {
    private int reservationId;
    private String customerName;
    private int roomNumber;
    private RoomType roomType;
    private int nights;
    private double amountPaid;
    private boolean active;

    public Reservation(int reservationId, String customerName, int roomNumber,
                       RoomType roomType, int nights, double amountPaid, boolean active) {
        this.reservationId = reservationId;
        this.customerName = customerName;
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.nights = nights;
        this.amountPaid = amountPaid;
        this.active = active;
    }

    public int getReservationId() {
        return reservationId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public int getNights() {
        return nights;
    }

    public double getAmountPaid() {
        return amountPaid;
    }

    public boolean isActive() {
        return active;
    }

    public void cancel() {
        this.active = false;
    }

    @Override
    public String toString() {
        return "Reservation ID: " + reservationId +
                ", Name: " + customerName +
                ", Room: " + roomNumber +
                " (" + roomType + ")" +
                ", Nights: " + nights +
                ", Amount: Rs. " + String.format("%.2f", amountPaid) +
                ", Status: " + (active ? "ACTIVE" : "CANCELLED");
    }
}

class Hotel {
    private Map<Integer, Room> rooms = new LinkedHashMap<>();
    private List<Reservation> reservations = new ArrayList<>();
    private int nextReservationId = 1000;
    private static final String FILE_NAME = "reservations.txt";

    public Hotel() {
        initializeRooms();
        loadReservationsFromFile();
    }

    private void initializeRooms() {
        rooms.put(101, new Room(101, RoomType.STANDARD, 1500));
        rooms.put(102, new Room(102, RoomType.STANDARD, 1500));
        rooms.put(103, new Room(103, RoomType.STANDARD, 1500));

        rooms.put(201, new Room(201, RoomType.DELUXE, 2500));
        rooms.put(202, new Room(202, RoomType.DELUXE, 2500));

        rooms.put(301, new Room(301, RoomType.SUITE, 4000));
        rooms.put(302, new Room(302, RoomType.SUITE, 4500));
    }

    public void viewAllRooms() {
        System.out.println("===== ALL ROOMS =====");
        for (Room room : rooms.values()) {
            System.out.println(room);
        }
        System.out.println("=====================");
    }

    public List<Room> getAvailableRoomsByType(RoomType type) {
        List<Room> result = new ArrayList<>();
        for (Room room : rooms.values()) {
            if (room.getType() == type && room.isAvailable()) {
                result.add(room);
            }
        }
        return result;
    }

    public void showAvailableRoomsByType(RoomType type) {
        System.out.println("===== AVAILABLE " + type + " ROOMS =====");
        List<Room> list = getAvailableRoomsByType(type);
        if (list.isEmpty()) {
            System.out.println("No available rooms of this type.");
        } else {
            for (Room r : list) {
                System.out.println(r);
            }
        }
        System.out.println("===================================");
    }

    public Reservation bookRoom(String customerName, int roomNumber, int nights) {
        Room room = rooms.get(roomNumber);
        if (room == null) {
            System.out.println("Invalid room number.");
            return null;
        }
        if (!room.isAvailable()) {
            System.out.println("Room is already booked.");
            return null;
        }

        double amount = room.getPricePerNight() * nights;
        System.out.println("Total amount to be paid: Rs. " + String.format("%.2f", amount));
        System.out.println("Processing payment...");
        System.out.println("Payment successful!");

        room.setAvailable(false);
        Reservation res = new Reservation(nextReservationId++, customerName, roomNumber,
                room.getType(), nights, amount, true);
        reservations.add(res);
        saveReservationsToFile();

        System.out.println("Booking confirmed!");
        System.out.println(res);
        return res;
    }

    public boolean cancelReservation(int reservationId) {
        for (Reservation res : reservations) {
            if (res.getReservationId() == reservationId) {
                if (!res.isActive()) {
                    System.out.println("Reservation is already cancelled.");
                    return false;
                }
                res.cancel();
                Room room = rooms.get(res.getRoomNumber());
                if (room != null) {
                    room.setAvailable(true);
                }
                saveReservationsToFile();
                System.out.println("Reservation cancelled successfully.");
                return true;
            }
        }
        System.out.println("Reservation ID not found.");
        return false;
    }

    public void viewAllReservations() {
        System.out.println("===== ALL RESERVATIONS =====");
        if (reservations.isEmpty()) {
            System.out.println("No reservations found.");
        } else {
            for (Reservation r : reservations) {
                System.out.println(r);
            }
        }
        System.out.println("============================");
    }

    public void viewReservationById(int reservationId) {
        for (Reservation r : reservations) {
            if (r.getReservationId() == reservationId) {
                System.out.println("===== RESERVATION DETAILS =====");
                System.out.println(r);
                System.out.println("================================");
                return;
            }
        }
        System.out.println("No reservation found with that ID.");
    }

    private void loadReservationsFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            int maxId = nextReservationId;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] parts = line.split(",");
                if (parts.length != 7) continue;

                int id = Integer.parseInt(parts[0]);
                String name = parts[1];
                int roomNumber = Integer.parseInt(parts[2]);
                RoomType type = RoomType.valueOf(parts[3]);
                int nights = Integer.parseInt(parts[4]);
                double amount = Double.parseDouble(parts[5]);
                boolean active = Boolean.parseBoolean(parts[6]);

                Reservation res = new Reservation(id, name, roomNumber, type, nights, amount, active);
                reservations.add(res);

                if (active) {
                    Room room = rooms.get(roomNumber);
                    if (room != null) {
                        room.setAvailable(false);
                    }
                }

                if (id > maxId) {
                    maxId = id;
                }
            }
            nextReservationId = maxId + 1;
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error loading reservations: " + e.getMessage());
        }
    }

    private void saveReservationsToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Reservation r : reservations) {
                String line = r.getReservationId() + "," +
                        r.getCustomerName() + "," +
                        r.getRoomNumber() + "," +
                        r.getRoomType().name() + "," +
                        r.getNights() + "," +
                        r.getAmountPaid() + "," +
                        r.isActive();
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving reservations: " + e.getMessage());
        }
    }
}

public class HotelReservationSystem {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Hotel hotel = new Hotel();
        int choice;

        do {
            System.out.println("\n===== HOTEL RESERVATION SYSTEM =====");
            System.out.println("1. View all rooms");
            System.out.println("2. Search available rooms by type");
            System.out.println("3. Make a reservation");
            System.out.println("4. Cancel a reservation");
            System.out.println("5. View all reservations");
            System.out.println("6. View reservation by ID");
            System.out.println("0. Exit");
            System.out.print("Enter your choice: ");

            while (!sc.hasNextInt()) {
                System.out.print("Enter a valid number: ");
                sc.next();
            }
            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    hotel.viewAllRooms();
                    break;
                case 2:
                    RoomType type = askRoomType(sc);
                    if (type != null) {
                        hotel.showAvailableRoomsByType(type);
                    } else {
                        System.out.println("Invalid room type choice.");
                    }
                    break;
                case 3:
                    System.out.print("Enter customer name: ");
                    String name = sc.nextLine();

                    RoomType typeForBooking = askRoomType(sc);
                    if (typeForBooking == null) {
                        System.out.println("Invalid room type choice.");
                        break;
                    }

                    hotel.showAvailableRoomsByType(typeForBooking);
                    System.out.print("Enter room number to book: ");
                    int roomNo = sc.nextInt();
                    System.out.print("Enter number of nights: ");
                    int nights = sc.nextInt();
                    sc.nextLine();

                    hotel.bookRoom(name, roomNo, nights);
                    break;
                case 4:
                    System.out.print("Enter Reservation ID to cancel: ");
                    int resId = sc.nextInt();
                    sc.nextLine();
                    hotel.cancelReservation(resId);
                    break;
                case 5:
                    hotel.viewAllReservations();
                    break;
                case 6:
                    System.out.print("Enter Reservation ID to view: ");
                    int viewId = sc.nextInt();
                    sc.nextLine();
                    hotel.viewReservationById(viewId);
                    break;
                case 0:
                    System.out.println("Exiting... Thank you!");
                    break;
                default:
                    System.out.println("Invalid choice.");
            }

        } while (choice != 0);

        sc.close();
    }

    private static RoomType askRoomType(Scanner sc) {
        System.out.println("Select room type:");
        System.out.println("1. Standard");
        System.out.println("2. Deluxe");
        System.out.println("3. Suite");
        System.out.print("Enter choice: ");
        while (!sc.hasNextInt()) {
            System.out.print("Enter a valid number: ");
            sc.next();
        }
        int t = sc.nextInt();
        sc.nextLine();
        return RoomType.fromInt(t);
    }
}
