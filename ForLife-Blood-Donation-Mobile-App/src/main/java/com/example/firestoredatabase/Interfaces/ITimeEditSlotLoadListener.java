package com.example.firestoredatabase.Interfaces;

import com.example.firestoredatabase.Model.TimesEditSlot;

import java.util.List;

public interface ITimeEditSlotLoadListener {
    void onTimeSlotLoadSuccess(List<TimesEditSlot> timeSlotList, Long slot);
    void onTimeSlotLoadFailed(String message);
    void onTimeSlotLoadEmpty();
}
