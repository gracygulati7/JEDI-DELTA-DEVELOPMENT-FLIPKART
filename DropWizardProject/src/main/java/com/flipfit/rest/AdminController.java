package com.flipfit.rest;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import com.flipfit.business.AdminService;
import com.flipfit.business.AdminServiceImpl;
import com.flipfit.business.UserService;
import com.flipfit.business.UserServiceImpl;
import com.flipfit.dao.GymCentreDAO;
import com.flipfit.dao.SlotDAO;
import com.flipfit.bean.FlipFitGymCenter;
import com.flipfit.bean.Slot;
import java.util.ArrayList;

@Path("/admin")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AdminController {

    private AdminService adminService = new AdminServiceImpl();
    private final UserService userService = new UserServiceImpl();
    private final GymCentreDAO gymCentreDAO = GymCentreDAO.getInstance();
    private final SlotDAO slotDAO = SlotDAO.getInstance();

    /**
     * View all gym owners
     */
    @GET
    @Path("/gym-owners")
    public Response viewAllGymOwners() {
        try {
            adminService.viewAllGymOwners();
            return Response.ok()
                .entity(new ApiResponse(true, "Gym owners retrieved successfully"))
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ApiResponse(false, "Failed to retrieve gym owners: " + e.getMessage()))
                .build();
        }
    }

    /**
     * Approve a gym owner
     */
    @PUT
    @Path("/gym-owners/{ownerId}/approve")
    public Response approveOwner(@PathParam("ownerId") int ownerId) {
        try {
            adminService.approveOwner(ownerId);
            return Response.ok()
                .entity(new ApiResponse(true, "Gym owner approved successfully"))
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ApiResponse(false, "Failed to approve owner: " + e.getMessage()))
                .build();
        }
    }

    /**
     * Validate a gym owner
     */
    @PUT
    @Path("/gym-owners/{ownerId}/validate")
    public Response validateOwner(@PathParam("ownerId") int ownerId) {
        try {
            adminService.validateOwner(ownerId);
            return Response.ok()
                .entity(new ApiResponse(true, "Gym owner validated successfully"))
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ApiResponse(false, "Failed to validate owner: " + e.getMessage()))
                .build();
        }
    }

    /**
     * Delete a gym owner
     */
    @DELETE
    @Path("/gym-owners/{ownerId}")
    public Response deleteOwner(@PathParam("ownerId") int ownerId) {
        try {
            adminService.deleteOwner(ownerId);
            return Response.ok()
                .entity(new ApiResponse(true, "Gym owner deleted successfully"))
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ApiResponse(false, "Failed to delete owner: " + e.getMessage()))
                .build();
        }
    }

    /**
     * View all FlipFit customers
     */
    @GET
    @Path("/customers")
    public Response viewFFCustomers() {
        try {
            adminService.viewFFCustomers();
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
     * Add a gym center
     */
    @POST
    @Path("/gym-centers")
    public Response addGymCenter(AddGymCenterRequest request) {
        try {
            adminService.addGymCenter(
                request.getCenterId(), 
                request.getGymName(), 
                request.getCity(), 
                request.getState(), 
                request.getPincode(), 
                request.getCapacity()
            );
            return Response.status(Response.Status.CREATED)
                .entity(new ApiResponse(true, "Gym center added successfully"))
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ApiResponse(false, "Failed to add gym center: " + e.getMessage()))
                .build();
        }
    }

    /**
     * View all gym centers with slots
     */
    @GET
    @Path("/gym-centers")
    public Response viewGymCentersWithSlots() {
        try {
            List<FlipFitGymCenter> centers = gymCentreDAO.getAllCentres();
            if (centers.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ApiResponse(false, "No gym centers found."))
                    .build();
            }

            List<GymCenterWithSlots> result = new ArrayList<>();
            for (FlipFitGymCenter center : centers) {
                List<Slot> slots = slotDAO.getSlotsByCenterId(center.getGymId());
                result.add(new GymCenterWithSlots(center, slots));
            }

            return Response.ok()
                .entity(result)
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ApiResponse(false, "Failed to retrieve gym centers: " + e.getMessage()))
                .build();
        }
    }

    /**
     * Add slot info to a gym center
     */
    @POST
    @Path("/gym-centers/{centerId}/slots")
    public Response addSlotInfo(@PathParam("centerId") int centerId, AddSlotRequest request) {
        try {
            adminService.addSlotInfo(
                centerId, 
                request.getSlotId(), 
                request.getStartTime(), 
                request.getEndTime(), 
                request.getSeats()
            );
            return Response.status(Response.Status.CREATED)
                .entity(new ApiResponse(true, "Slot added successfully"))
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ApiResponse(false, "Failed to add slot: " + e.getMessage()))
                .build();
        }
    }

    /**
     * View slots for a specific center
     */
    @GET
    @Path("/gym-centers/{centerId}/slots")
    public Response viewSlots(@PathParam("centerId") int centerId) {
        try {
            adminService.viewSlots(centerId);
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
     * View available slots using UserService
     */
    @GET
    @Path("/available-slots")
    public Response viewAvailableSlotsUserService() {
        try {
            List<FlipFitGymCenter> centers = gymCentreDAO.getAllCentres();
            if (centers.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ApiResponse(false, "No gym centers available."))
                    .build();
            }

            List<GymCenterWithSlots> result = new ArrayList<>();
            for (FlipFitGymCenter center : centers) {
                List<Slot> slots = userService.findAvailableSlots(center.getGymId());
                result.add(new GymCenterWithSlots(center, slots));
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
     * View admin profile
     */
    @GET
    @Path("/{adminId}/profile")
    public Response viewProfile(@PathParam("adminId") int adminId) {
        try {
            userService.viewProfile(adminId);
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
     * Edit admin profile
     */
    @PUT
    @Path("/{adminId}/profile")
    public Response editProfile(@PathParam("adminId") int adminId) {
        try {
            userService.editProfile(adminId);
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
    public static class AddGymCenterRequest {
        private int centerId;
        private String gymName;
        private String city;
        private String state;
        private int pincode;
        private int capacity;
        
        public int getCenterId() { return centerId; }
        public void setCenterId(int centerId) { this.centerId = centerId; }
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
        private int slotId;
        private String startTime; // HH:MM format
        private String endTime; // HH:MM format
        private int seats;
        
        public int getSlotId() { return slotId; }
        public void setSlotId(int slotId) { this.slotId = slotId; }
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

    public static class GymCenterWithSlots {
        private FlipFitGymCenter center;
        private List<Slot> slots;
        
        public GymCenterWithSlots(FlipFitGymCenter center, List<Slot> slots) {
            this.center = center;
            this.slots = slots;
        }
        
        public FlipFitGymCenter getCenter() { return center; }
        public void setCenter(FlipFitGymCenter center) { this.center = center; }
        public List<Slot> getSlots() { return slots; }
        public void setSlots(List<Slot> slots) { this.slots = slots; }
    }
}
