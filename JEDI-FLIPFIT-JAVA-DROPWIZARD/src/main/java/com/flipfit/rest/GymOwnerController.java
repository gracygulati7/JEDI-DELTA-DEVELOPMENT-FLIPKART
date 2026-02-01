package com.flipfit.rest;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import com.flipfit.business.GymOwnerService;
import com.flipfit.business.GymOwnerServiceImpl;
import com.flipfit.dao.GymCentreDAO;
import com.flipfit.dao.SlotDAO;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Path("/gym-owner")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GymOwnerController {

    private GymOwnerService gymOwnerService = new GymOwnerServiceImpl();
    private final GymCentreDAO gymCentreDAO = GymCentreDAO.getInstance();
    private final SlotDAO slotDAO = SlotDAO.getInstance();

    /**
     * View all centres owned by a gym owner
     */
    @GET
    @Path("/{ownerId}/centres")
    public Response viewCentres(@PathParam("ownerId") int ownerId) {
        try {
            gymOwnerService.viewCentres(ownerId);
            return Response.ok()
                .entity(new ApiResponse(true, "Centres retrieved successfully"))
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ApiResponse(false, "Failed to retrieve centres: " + e.getMessage()))
                .build();
        }
    }

    /**
     * Add a new gym centre
     */
    @POST
    @Path("/{ownerId}/centres")
    public Response addGymCentre(@PathParam("ownerId") int ownerId, AddCentreRequest request) {
        try {
            // Auto-generate centre ID
            int centerId = gymCentreDAO.getNextCentreId();
            
            gymOwnerService.addCentre(
                ownerId, 
                centerId, 
                request.getGymName(), 
                request.getCity(), 
                request.getState(), 
                request.getPincode(), 
                request.getCapacity()
            );
            
            return Response.status(Response.Status.CREATED)
                .entity(new CentreCreatedResponse(true, "Gym Centre added successfully", centerId))
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ApiResponse(false, "Failed to add centre: " + e.getMessage()))
                .build();
        }
    }

    /**
     * Add a slot to a gym centre
     */
    @POST
    @Path("/{ownerId}/centres/{centreId}/slots")
    public Response addSlot(
            @PathParam("ownerId") int ownerId,
            @PathParam("centreId") int centreId,
            AddSlotRequest request) {
        try {
            if (!gymCentreDAO.centreIdExists(centreId)) {
                return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ApiResponse(false, "Gym Centre with ID " + centreId + " not found!"))
                    .build();
            }
            
            // Auto-generate slot ID
            int slotId = slotDAO.getNextSlotId();
            
            LocalDate date;
            try {
                date = LocalDate.parse(request.getDate());
            } catch (DateTimeParseException e) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ApiResponse(false, "Invalid date format! Please use YYYY-MM-DD."))
                    .build();
            }
            
            gymOwnerService.addSlot(
                centreId, 
                slotId, 
                date, 
                request.getStartTime(), 
                request.getEndTime(), 
                request.getSeats()
            );
            
            return Response.status(Response.Status.CREATED)
                .entity(new SlotCreatedResponse(true, "Slot added successfully", slotId))
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ApiResponse(false, "Failed to add slot: " + e.getMessage()))
                .build();
        }
    }

    /**
     * View slots in a centre
     */
    @GET
    @Path("/{ownerId}/centres/{centreId}/slots")
    public Response viewSlots(
            @PathParam("ownerId") int ownerId,
            @PathParam("centreId") int centreId) {
        try {
            gymOwnerService.viewSlots(ownerId, centreId);
            return Response.ok()
                .entity(new ApiResponse(true, "Slots retrieved successfully"))
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ApiResponse(false, "Failed to retrieve slots: " + e.getMessage()))
                .build();
        }
    }

    /**
     * View customers for a centre
     */
    @GET
    @Path("/{ownerId}/centres/{centreId}/customers")
    public Response viewCustomers(
            @PathParam("ownerId") int ownerId,
            @PathParam("centreId") int centreId) {
        try {
            gymOwnerService.viewCustomers(ownerId, centreId);
            return Response.ok()
                .entity(new ApiResponse(true, "Customers retrieved successfully"))
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ApiResponse(false, "Failed to retrieve customers: " + e.getMessage()))
                .build();
        }
    }

    /**
     * View gym owner profile
     */
    @GET
    @Path("/{ownerId}/profile")
    public Response viewProfile(@PathParam("ownerId") int ownerId) {
        try {
            gymOwnerService.viewProfile(ownerId);
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
     * Edit gym owner profile
     */
    @PUT
    @Path("/{ownerId}/profile")
    public Response editProfile(@PathParam("ownerId") int ownerId) {
        try {
            gymOwnerService.editDetails(ownerId);
            return Response.ok()
                .entity(new ApiResponse(true, "Profile updated successfully"))
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ApiResponse(false, "Failed to update profile: " + e.getMessage()))
                .build();
        }
    }

    // DTOs
    public static class AddCentreRequest {
        private String gymName;
        private String city;
        private String state;
        private int pincode;
        private int capacity;
        
        public String getGymName() { return gymName; }
        public void setGymName(String gymName) { this.gymName = gymName; }
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        public String getState() { return state; }
        public void setState(String state) { this.state = state; }
        public int getPincode() { return pincode; }
        public void setPincode(int pincode) { this.pincode = pincode; }
        public int getCapacity() { return capacity; }
        public void setCapacity(int capacity) { this.capacity = capacity; }
    }
    
    public static class AddSlotRequest {
        private String date; // YYYY-MM-DD format
        private String startTime; // HH:MM format
        private String endTime; // HH:MM format
        private int seats;
        
        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
        public String getStartTime() { return startTime; }
        public void setStartTime(String startTime) { this.startTime = startTime; }
        public String getEndTime() { return endTime; }
        public void setEndTime(String endTime) { this.endTime = endTime; }
        public int getSeats() { return seats; }
        public void setSeats(int seats) { this.seats = seats; }
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
    
    public static class CentreCreatedResponse extends ApiResponse {
        private int centreId;
        
        public CentreCreatedResponse(boolean success, String message, int centreId) {
            super(success, message);
            this.centreId = centreId;
        }
        
        public int getCentreId() { return centreId; }
        public void setCentreId(int centreId) { this.centreId = centreId; }
    }
    
    public static class SlotCreatedResponse extends ApiResponse {
        private int slotId;
        
        public SlotCreatedResponse(boolean success, String message, int slotId) {
            super(success, message);
            this.slotId = slotId;
        }
        
        public int getSlotId() { return slotId; }
        public void setSlotId(int slotId) { this.slotId = slotId; }
    }
}
