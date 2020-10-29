package com.example.firestoredatabase.Interfaces;

import java.util.List;

public interface IJudetListener {
    void onAllJudetLoadSuccess(List<String> judetNameList);
    void onAllJudetLoadFailed(String message);
}
