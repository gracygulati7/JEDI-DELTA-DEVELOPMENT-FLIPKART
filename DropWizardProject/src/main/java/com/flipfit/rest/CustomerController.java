package com.flipfit.rest;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import com.flipfit.bean.Booking;
import com.flipfit.bean.FlipFitCustomer;
import com.flipfit.bean.FlipFitGymCenter;
import com.flipfit.bean.Slot;
import com.flipfit.business.BookingService;
import com.flipfit.business.BookingServiceImpl;
import com.flipfit.business.GymCentreService;
import com.flipfit.business.GymCentreServiceImpl;
import com.flipfit.business.NotificationServiceImpl;
import com.flipfit.business.UserService;
import com.flipfit.business.UserServiceImpl;
import com.flipfit.dao.CustomerDAO;
import com.flipfit.dao.GymCentreDAO;
import com.flipfit.business.CustomerService;
import com.flipfit.business.CustomerServiceImpl;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.ArrayList;

@Path("/customer")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CustomerController {
    private final BookingService bookingService = new BookingServiceImpl();
    private final GymCentreService gymCentreService = new GymCentreServiceImpl();
    private final UserService userService = new UserServiceImpl();
    private final GymCentreDAO gymCentreDAO = GymCentreDAO.getInstance();
    private final NotificationServiceImpl notificationService = NotificationServiceImpl.getInstance();
    private final CustomerService customerService = new CustomerServiceImpl();

    /**
     * View all available gyms
     */
    @GET
    @Path("/gyms")
    public Response viewGyms() {
        try {
            List<FlipFitGymCenter> gyms = gymCentreDAO.getAllCentres();
            return Response.ok()
                .entity(gyms)
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ApiResponse(false, "Failed to retrieve gyms: " + e.getMessage()))
                .build();
        }
    }

    /**
     * View customer's bookings
     */
    @GET
    @Path("/{userId}/bookings")
    public Response viewMyBookings(@PathParam("userId") int userId) {
        try {
            List<Booking> bookings = bookingService.getBookingsByUserId(userId);
            return Response.ok()
                .entity(bookings)
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ApiResponse(false, "Failed to retrieve bookings: " + e.getMessage()))
                .build();
        }
    }

    /**
     * Book a slot
     */
    @POST
    @Path("/{userId}/bookings")
    public Response bookSlot(@PathParam("userId") int userId, BookSlotRequest request) {
        try {
            // Validate center
            FlipFitGymCenter selectedCenter = gymCentreDAO.getGymCentreById(request.getCentreId());
            if (selectedCenter == null) {
                return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ApiResponse(false, "Invalid Center ID!"))
                    .build();
            }

            // Parse booking date
            LocalDate bookingDate;
            try {
                bookingDate = LocalDate.parse(request.getDate());
            } catch (DateTimeParseException e) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ApiResponse(false, "Invalid date format! Please use YYYY-MM-DD."))
                    .build();
            }

            // Get slots for the center and date
            com.flipfit.dao.SlotDAO slotDAO = com.flipfit.dao.SlotDAO.getInstance();
            List<Slot> allSlots = slotDAO.getAllSlots();
            List<Slot> slotsForCenterAndDate = new ArrayList<>();
            List<Slot> fullSlotsForCenterAndDate = new ArrayList<>();

            for (Slot slot : allSlots) {
                if (slot.getDate() != null && slot.getDate().equals(bookingDate) 
                    && slot.getCenterId() == request.getCentreId() && !slot.isExpired()) {
                    if (slot.getSeatsAvailable() > 0) {
                        slotsForCenterAndDate.add(slot);
                    } else {
                        fullSlotsForCenterAndDate.add(slot);
                    }
                }
            }

            if (slotsForCenterAndDate.isEmpty() && fullSlotsForCenterAndDate.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ApiResponse(false, "No slots available for this center on the selected date."))
                    .build();
            }

            // Validate slot selection
            Slot selectedSlot = null;
            boolean isFullSlot = false;

            for (Slot slot : slotsForCenterAndDate) {
                if (slot.getSlotId() == request.getSlotId()) {
                    selectedSlot = slot;
                    break;
                }
            }

            if (selectedSlot == null) {
                for (Slot slot : fullSlotsForCenterAndDate) {
                    if (slot.getSlotId() == request.getSlotId()) {
                        selectedSlot = slot;
                        isFullSlot = true;
                        break;
                    }
                }
            }

            if (selectedSlot == null) {
                return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ApiResponse(false, "Invalid Slot ID!"))
                    .build();
            }

            // Process payment if slot has available seats
            if (!isFullSlot) {
                boolean paymentSuccess = customerService.makePayment(userId, request.getAmount());
                if (!paymentSuccess) {
                    return Response.status(Response.Status.PAYMENT_REQUIRED)
                        .entity(new ApiResponse(false, "Payment failed. Booking cancelled."))
                        .build();
                }
            }

            // Create booking
            Booking booking = bookingService.createBooking(userId, request.getSlotId(), request.getCentreId());
            if (booking != null) {
                if (isFullSlot) {
                    // Added to waitlist
                    return Response.status(Response.Status.ACCEPTED)
                        .entity(new BookingResponse(true, "Added to waitlist. You'll be notified when a seat becomes available.", 
                            booking, true))
                        .build();
                } else {
                    // Booking confirmed
                    // >>> START OF ADDED PERSISTENCE LOGIC <<<
                    int newSeatCount = selectedSlot.getSeatsAvailable() - 1;
                    boolean isDbUpdated = slotDAO.updateSlotSeats(request.getSlotId(), newSeatCount);
                    if (isDbUpdated) {
                        selectedSlot.setSeatsAvailable(newSeatCount);
                    }
                    // >>> END OF ADDED PERSISTENCE LOGIC <<<
                    
                    return Response.status(Response.Status.CREATED)
                        .entity(new BookingResponse(true, "Booking confirmed successfully", booking, false))
                        .build();
                }
            } else {
                return Response.status(Response.Status.CONFLICT)
                    .entity(new ApiResponse(false, "Booking failed. Time conflict or invalid slot."))
                    .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ApiResponse(false, "Booking failed: " + e.getMessage()))
                .build();
        }
    }

    /**
     * Cancel a booking
     */
    @DELETE
    @Path("/{userId}/bookings/{bookingId}")
    public Response cancelBooking(@PathParam("userId") int userId, @PathParam("bookingId") int bookingId) {
        try {
            // Check if booking exists and belongs to user
            List<Booking> myBookings = bookingService.getBookingsByUserId(userId);
            boolean exists = myBookings.stream().anyMatch(b -> b.getBookingId() == bookingId);

            if (exists) {
                bookingService.cancelBooking(bookingId);
                return Response.ok()
                    .entity(new ApiResponse(true, "Booking cancelled successfully."))
                    .build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ApiResponse(false, "Booking ID not found in your active bookings."))
                    .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ApiResponse(false, "Failed to cancel booking: " + e.getMessage()))
                .build();
        }
    }

    /**
     * View notifications
     */
    @GET
    @Path("/{userId}/notifications")
    public Response viewNotifications(@PathParam("userId") int userId) {
        try {
            notificationService.printUserNotifications(userId);
            return Response.ok()
                .entity(new ApiResponse(true, "Notifications retrieved successfully"))
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ApiResponse(false, "Failed to retrieve notifications: " + e.getMessage()))
                .build();
        }
    }

    /**
     * View available slots using UserService
     */
    @GET
    @Path("/available-slots")
    public Response viewAvailableSlots() {
        try {
            List<FlipFitGymCenter> gyms = gymCentreDAO.getAllCentres();
            if (gyms.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ApiResponse(false, "No gyms available."))
                    .build();
            }
            
            List<GymSlotsInfo> result = new ArrayList<>();
            for (FlipFitGymCenter gym : gyms) {
                List<Slot> slots = userService.findAvailableSlots(gym.getGymId());
                result.add(new GymSlotsInfo(gym, slots));
            }
            
            return Response.ok()
                .entity(result)
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ApiResponse(false, "Failed to retrieve available slots: " + e.getMessage()))
                .build();
        }
    }

    /**
     * View customer profile
     */
    @GET
    @Path("/{userId}/profile")
    public Response viewProfile(@PathParam("userId") int userId) {
        try {
            userService.viewProfile(userId);
            return Response.ok()
                .entity(new ApiResponse(true, "Profile retrieved successfully"))
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ApiResponse(false, "Failed to retrieve profile: " + e.getMessage()))
                .build();
        }
    }

    /**
     * Edit customer profile
     */
    @PUT
    @Path("/{userId}/profile")
    public Response editProfile(@PathParam("userId") int userId) {
        try {
            userService.editProfile(userId);
            return Response.ok()
                .entity(new ApiResponse(true, "Profile updated successfully"))
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ApiResponse(false, "Failed to update profile: " + e.getMessage()))
                .build();
        }
    }

    /**
     * View payment info
     */
    @GET
    @Path("/{userId}/payment-info")
    public Response viewPaymentInfo(@PathParam("userId") int userId) {
        try {
            customerService.viewPaymentInfo(userId);
            return Response.ok()
                .entity(new ApiResponse(true, "Payment info retrieved successfully"))
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ApiResponse(false, "Failed to retrieve payment info: " + e.getMessage()))
                .build();
        }
    }

    // DTOs
    public static class BookSlotRequest {
        private int centreId;
        private String date; // YYYY-MM-DD format
        private int slotId;
        private int amount;
        
        public int getCentreId() { return centreId; }
        public void setCentreId(int centreId) { this.centreId = centreId; }
        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
        public int getSlotId() { return slotId; }
        public void setSlotId(int slotId) { this.slotId = slotId; }
        public int getAmount() { return amount; }
        public void setAmount(int amount) { this.amount = amount; }
    }

    public static class ApiResponse {
        private boolean success;
        private String message;
        
        public ApiResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    public static class BookingResponse extends ApiResponse {
        private Booking booking;
        private boolean waitlisted;
        
        public BookingResponse(boolean success, String message, Booking booking, boolean waitlisted) {
            super(success, message);
            this.booking = booking;
            this.waitlisted = waitlisted;
        }
        
        public Booking getBooking() { return booking; }
        public void setBooking(Booking booking) { this.booking = booking; }
        public boolean isWaitlisted() { return waitlisted; }
        public void setWaitlisted(boolean waitlisted) { this.waitlisted = waitlisted; }
    }

    public static class GymSlotsInfo {
        private FlipFitGymCenter gym;
        private List<Slot> slots;
        
        public GymSlotsInfo(FlipFitGymCenter gym, List<Slot> slots) {
            this.gym = gym;
            this.slots = slots;
        }
        
        public FlipFitGymCenter getGym() { return gym; }
        public void setGym(FlipFitGymCenter gym) { this.gym = gym; }
        public List<Slot> getSlots() { return slots; }
        public void setSlots(List<Slot> slots) { this.slots = slots; }
    }
}

