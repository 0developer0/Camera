package com.example.camera;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPre {
    Context context;
    SharedPreferences sharedPreferences;

    public SharedPre(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("APP_SHAREDPRE", Context.MODE_PRIVATE);
    }
    public void setFlash(boolean isChecked){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("Flash", isChecked);
        editor.apply();
    }

    public boolean getFlash(){
        return sharedPreferences.getBoolean("Dark Mode", true);
    }
}
