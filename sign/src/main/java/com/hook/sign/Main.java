package com.hook.sign;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.security.MessageDigest;

public class Main extends AppCompatActivity implements View.OnClickListener {

    Button button;
    EditText editText;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String key = "editText";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        sharedPreferences = getSharedPreferences("hook", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        button = findViewById(R.id.button);
        editText = findViewById(R.id.edittext);

        button.setOnClickListener(this);
        editText.setText(sharedPreferences.getString(key, ""));
        editText.setSelection(editText.length());

    }

    @Override
    public void onClick(View view) {
        try {
            StringBuilder sb = new StringBuilder();
            String packageName = editText.getText().toString();
            PackageInfo info = this.getPackageManager().getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            Signature[] sign = info.signatures;
            for (Signature signature : sign) {
                sb.append(signature.toCharsString());
            }
            System.out.println(packageName + "签名:" + sb.toString());
            Toast.makeText(this, "请查看Log\n：System.out", Toast.LENGTH_LONG).show();
            editor.putString(key, packageName);
            editor.commit();
        } catch (Exception e) {
            Toast.makeText(this, "异常：\n" + e.toString(), Toast.LENGTH_LONG).show();
            System.out.println(e.toString());
        }
    }

    public String getSignature(Context context, String packName) {
        try {
            StringBuilder sb = new StringBuilder();
            PackageInfo info = context.getPackageManager().getPackageInfo(packName, PackageManager.GET_SIGNATURES);
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

    /**
     * 返回MD5
     */
    public static String signatureMD5(Signature[] signatures) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            if (signatures != null) {
                for (Signature s : signatures)
                    digest.update(s.toByteArray());
            }
            return toHexString(digest.digest());
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * SHA1
     */
    public static String signatureSHA1(Signature[] signatures) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            if (signatures != null) {
                for (Signature s : signatures)
                    digest.update(s.toByteArray());
            }
            return toHexString(digest.digest());
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * SHA256
     */
    public static String signatureSHA256(Signature[] signatures) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            if (signatures != null) {
                for (Signature s : signatures)
                    digest.update(s.toByteArray());
            }
            return toHexString(digest.digest());
        } catch (Exception e) {
            return "";
        }
    }


    // 如需要小写则把ABCDEF改成小写
    private static final char HEX_DIGITS[] = {'0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    /**
     * 进行转换
     */
    public static String toHexString(byte[] bData) {
        StringBuilder sb = new StringBuilder(bData.length * 2);
        for (int i = 0; i < bData.length; i++) {
            sb.append(HEX_DIGITS[(bData[i] & 0xf0) >>> 4]);
            sb.append(HEX_DIGITS[bData[i] & 0x0f]);
        }
        return sb.toString();
    }

}
