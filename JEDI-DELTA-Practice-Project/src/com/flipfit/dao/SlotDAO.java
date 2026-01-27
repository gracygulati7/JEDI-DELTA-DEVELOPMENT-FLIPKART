package com.flipfit.dao;

import com.flipfit.bean.Slot;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SlotDAO {

	private static SlotDAO instance = null;
	private final List<Slot> slots = new ArrayList<>();
	private int nextSlotId = 1001;

	private SlotDAO() {
	}

	public static SlotDAO getInstance() {
		if (instance == null) {
			instance = new SlotDAO();
		}
		return instance;
	}

	public void addSlot(Slot slot) {
		slots.add(slot);
	}

	public List<Slot> getSlotsByCenterId(int centerId) {
		List<Slot> centerSlots = new ArrayList<>();
		for (Slot slot : slots) {
			if (slot.getCenterId() == centerId) {
				centerSlots.add(slot);
			}
		}
		return centerSlots;
	}

	public Slot getSlotById(int slotId) {
		for (Slot slot : slots) {
			if (slot.getSlotId() == slotId) {
				return slot;
			}
		}
		return null;
	}

	public Slot getSlotById(int userId, int slotId, int centerId) {
		for (Slot slot : slots) {
			if (slot.getSlotId() == slotId && slot.getCenterId() == centerId) {
				return slot;
			}
		}
		return null;
	}

	public List<Slot> getAllSlots() {
		return new ArrayList<>(slots);
	}

	public int getNextSlotId() {
		return nextSlotId++;
	}

	/**
	 * Get available slots for a specific center and date
	 */
	public List<Slot> getAvailableSlotsByDateAndCenter(int centerId, LocalDate date) {
		List<Slot> availableSlots = new ArrayList<>();
		for (Slot slot : slots) {
			if (slot.getCenterId() == centerId && slot.getDate().equals(date) && slot.getSeatsAvailable() > 0
					&& !slot.isExpired()) {
				availableSlots.add(slot);
			}
		}
		return availableSlots;
	}

	/**
	 * Get full slots for a specific center and date (for waitlist)
	 */
	public List<Slot> getFullSlotsByDateAndCenter(int centerId, LocalDate date) {
		List<Slot> fullSlots = new ArrayList<>();
		for (Slot slot : slots) {
			if (slot.getCenterId() == centerId && slot.getDate().equals(date) && slot.isFull()) {
				fullSlots.add(slot);
			}
		}
		return fullSlots;
	}

	/**
	 * Get expired slots
	 */
	public List<Slot> getExpiredSlots() {
		List<Slot> expiredSlots = new ArrayList<>();
		for (Slot slot : slots) {
			if (slot.isExpired()) {
				expiredSlots.add(slot);
			}
		}
		return expiredSlots;
	}

	/**
	 * Get slots by center for a specific date range
	 */
	public List<Slot> getSlotsByDateRange(int centerId, LocalDate startDate, LocalDate endDate) {
		List<Slot> rangeSlots = new ArrayList<>();
		for (Slot slot : slots) {
			if (slot.getCenterId() == centerId && !slot.getDate().isBefore(startDate)
					&& !slot.getDate().isAfter(endDate)) {
				rangeSlots.add(slot);
			}
		}
		return rangeSlots;
	}
}