package com.hook.hookpms;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class HookProxy implements InvocationHandler {

    private Object target;
    private String sign;
    private String packName;

    @SuppressLint("PrivateApi")
    public HookProxy(Context context, String sign) {
        try {
            this.sign = sign;
            this.packName = context.getPackageName();
            Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
            Object currentActivityThread = activityThreadClass.getDeclaredMethod("currentActivityThread").invoke(null);
            Field sPackageManagerField = activityThreadClass.getDeclaredField("sPackageManager");
            sPackageManagerField.setAccessible(true);

            target = sPackageManagerField.get(currentActivityThread);
            Class<?> iPackageManagerInterface = Class.forName("android.content.pm.IPackageManager");
            Object proxy = Proxy.newProxyInstance(iPackageManagerInterface.getClassLoader(), new Class[]{iPackageManagerInterface}, this);
            sPackageManagerField.set(currentActivityThread, proxy);

            PackageManager pm = context.getPackageManager();
            Field mPmField = pm.getClass().getDeclaredField("mPM");

            mPmField.setAccessible(true);
            mPmField.set(pm, proxy);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        if ("getPackageInfo".equals(method.getName())) {

            String pkgName = objects[0].toString();
            int flag = (int) objects[1];
            if (flag == 64 && this.packName.contains(pkgName)) {
                PackageInfo info = (PackageInfo) method.invoke(this.target, objects);
                info.signatures[0] = new Signature(this.sign);
                info.packageName = packName;
                return info;
            }
        }
        return method.invoke(this.target, objects);
    }

    public static void KillSign(Context context) {
        try {
            new HookProxy(context, "3082021d30820186a003020102020455a5f3b5300d06092a864886f70d01010505003053310b30090603550406130238363110300e060355040813076265696a696e673110300e060355040713076265696a696e67310b3009060355040a13023538311330110603550403130a7a6875616e7a6875616e301e170d3135303731353035343632395a170d3430303730383035343632395a3053310b30090603550406130238363110300e060355040813076265696a696e673110300e060355040713076265696a696e67310b3009060355040a13023538311330110603550403130a7a6875616e7a6875616e30819f300d06092a864886f70d010101050003818d003081890281810081500d203511a23539feaf0d2fa115488aceea539d92fa8344b4b72baa1e7a31d40494f0edaa8250a51db704412ecdd2ff5f85a0b3a25074d327d5c0c7d91de78bc9cea3f7ff85d7510e6c12d2875b9855d18963b3b9c6eeb541a5d0e9d0bac1255baadbd52c4e6ad962db1bf9d1d3963d6b8d3e8ba57ec9cabd551cb1b867670203010001300d06092a864886f70d0101050500038181001dff508c17de0dd941771c13019327efa62291fd144a4ccbe3152748852500bfacd44454d6b17ef6113b97cf480bf458b39b48794ae1f1389e75b11cb28cd28d91fa29ccd24349a025a5bfd8796e0b495a37b966ba7c004ecec76bd501a2a81780bfc5a7b27a9474c5020fb160d5a95ad293e22b2919fae9eb986abc405c3f41");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //
}
