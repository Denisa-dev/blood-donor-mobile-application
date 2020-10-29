package com.example.firestoredatabase.Interfaces;

import com.example.firestoredatabase.Model.Judet;

import java.util.List;

public interface IAllCenterLoadListenter {
    void onAllCenterLoadSuccess(List<Judet> judetNameList);
    void onAllCenterLoadFailed(String message);
}
