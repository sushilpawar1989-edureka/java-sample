import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.IntStream;

interface Vehicle {
    int getNumberOfWheels();

    String getVehicleNumber();

    VehicleType getVehicleType();

    default ParkingSlot getAvailableParkingSlot(VehicleSize size, List<ParkingSlot> parkingSlots)
            throws ParkingSlotNotAvailableException {

        return parkingSlots.stream()
                .filter(slot -> slot.isAvailable() && size == slot.getSize())
                .findFirst()
                .orElseThrow(() -> new ParkingSlotNotAvailableException());
    }

    Ticket getParkingTicket(List<ParkingSlot> parkingSlots) throws ParkingSlotNotAvailableException;

    VehicleSize getVehicleSize();
}

class ParkingSlotNotAvailableException extends Exception {

    private static final long serialVersionUID = 1L;
}

enum VehicleType {
    LMV,
    HMV
}

enum VehicleSize {
    MOTOR_CYCLE_SIZE,
    CAR_SIZE,
    TRUCK_SIZE;

    public static VehicleSize randomVehicleSize() {
        VehicleSize[] sizes = VehicleSize.values();
        return sizes[new Random().nextInt(sizes.length)];
    }
}

abstract class TwoWheeler implements Vehicle {
    int basePrice = 10;

    public int getNumberOfWheels() {
        return 2;
    }
}

abstract class FourWheeler implements Vehicle {
    int basePrice = 20;

    public int getNumberOfWheels() {
        return 4;
    }
}

class Scooter extends TwoWheeler {
    private String vehicleNo;

    public Scooter(String vehicleNo) {
        this.vehicleNo = vehicleNo;
    }

    @Override
    public VehicleType getVehicleType() {
        return VehicleType.LMV;
    }

    @Override
    public VehicleSize getVehicleSize() {
        return VehicleSize.MOTOR_CYCLE_SIZE;
    }

    public Ticket getParkingTicket(List<ParkingSlot> parkingSlots)
            throws ParkingSlotNotAvailableException {

        ParkingSlot parkingSlot = getAvailableParkingSlot(getVehicleSize(), parkingSlots);
        parkingSlot.useParkingSlot();
        return new Ticket(basePrice, vehicleNo, parkingSlot.getSlotNumber());
    }

    @Override
    public String getVehicleNumber() {
        return vehicleNo;
    }
}

class Car extends FourWheeler {
    private String vehicleNo;

    public Car(String vehicleNo) {
        this.vehicleNo = vehicleNo;
    }

    @Override
    public VehicleType getVehicleType() {
        return VehicleType.LMV;
    }

    @Override
    public VehicleSize getVehicleSize() {
        return VehicleSize.CAR_SIZE;
    }

    public Ticket getParkingTicket(List<ParkingSlot> parkingSlots)
            throws ParkingSlotNotAvailableException {

        ParkingSlot parkingSlot = getAvailableParkingSlot(getVehicleSize(), parkingSlots);
        parkingSlot.useParkingSlot();
        return new Ticket(basePrice, vehicleNo, parkingSlot.getSlotNumber());
    }

    @Override
    public String getVehicleNumber() {
        return vehicleNo;
    }
}

class Truck extends FourWheeler {
    private String vehicleNo;

    public Truck(String vehicleNo) {
        this.vehicleNo = vehicleNo;
    }

    @Override
    public VehicleType getVehicleType() {
        return VehicleType.HMV;
    }

    @Override
    public VehicleSize getVehicleSize() {
        return VehicleSize.TRUCK_SIZE;
    }

    public Ticket getParkingTicket(List<ParkingSlot> parkingSlots)
            throws ParkingSlotNotAvailableException {

        ParkingSlot parkingSlot = getAvailableParkingSlot(getVehicleSize(), parkingSlots);
        parkingSlot.useParkingSlot();
        return new Ticket(basePrice + (basePrice * 0.2f), vehicleNo, parkingSlot.getSlotNumber());
    }

    @Override
    public String getVehicleNumber() {
        return vehicleNo;
    }
}

class ParkingSlot {
    private VehicleSize size;

    public VehicleSize getSize() {
        return size;
    }

    private int slotNumber;

    public int getSlotNumber() {
        return slotNumber;
    }

    private boolean isAvailable;

    public boolean isAvailable() {
        return isAvailable;
    }

    public ParkingSlot(VehicleSize size, int slotNumber) {
        this.size = size;
        this.slotNumber = slotNumber;
        this.isAvailable = true;
    }

    public boolean canFitInParkingSlot(VehicleSize size) {
        return this.size == size;
    }

    public void useParkingSlot() {
        this.isAvailable = false;
    }
}

class Ticket {
    private String ticketNo;
    private float price;
    private String vehicleNo;
    private LocalDateTime issuedDateTime;
    private int slotNumber;

    public Ticket(float price, String vehicleNo, int slotNumber) {
        this.price = price;
        this.vehicleNo = vehicleNo;
        this.ticketNo = UUID.randomUUID().toString();
        this.issuedDateTime = LocalDateTime.now();
        this.slotNumber = slotNumber;
    }
    // setter & getters

    void print() {
        System.out.println(" === Ticket No : " + ticketNo + " === ");
        System.out.println(" === Ticket issue Date and Time : " + issuedDateTime + " === ");
        System.out.println(" === Vehicle No : " + vehicleNo + " === ");
        System.out.println(" === Price : Rs " + price + " ===");
        System.out.println(" === Slot number : " + slotNumber + " ===");
        System.out.println();
    }
}

public class Test {

    public static void main(String[] args) {
        List<ParkingSlot> parkingSlots = new ArrayList<>();
        int totalSlots = 100;

        IntStream.rangeClosed(1, totalSlots)
                .forEach(
                        slotNumber -> {
                            parkingSlots.add(
                                    new ParkingSlot(VehicleSize.randomVehicleSize(), slotNumber));
                        });

        Vehicle v1 = new Car("MH12AB1111");
        Vehicle v2 = new Truck("MH12AB1111");
        Vehicle v3 = new Scooter("MH12AB1111");
        try {
            v1.getParkingTicket(parkingSlots).print();
            v2.getParkingTicket(parkingSlots).print();
            v3.getParkingTicket(parkingSlots).print();
        } catch (ParkingSlotNotAvailableException e) {
            System.err.println(
                    "Parking slot not available for vehicle :: " + v1.getVehicleNumber());
        }
    }
}
