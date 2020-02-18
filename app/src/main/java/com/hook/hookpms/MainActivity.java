package com.hook.hookpms;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        System.out.println(getSignature(this));
        HookProxy.KillSign(this);
        System.out.println(getSignature(this));

    }

    public String getSignature(Context context) {
        try {
            StringBuilder sb = new StringBuilder();
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            Signature[] sign = info.signatures;
            for (Signature signature : sign) {
                sb.append(signature.toCharsString());
            }
            return sb.toString();
        } catch (Exception e) {
            System.out.println(e.toString());
            return null;
        }
    }
}
