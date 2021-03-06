/*
**        DroidPlugin Project
**
** Copyright(c) 2015 Andy Zhang <zhangyong232@gmail.com>
**
** This file is part of DroidPlugin.
**
** DroidPlugin is free software: you can redistribute it and/or
** modify it under the terms of the GNU Lesser General Public
** License as published by the Free Software Foundation, either
** version 3 of the License, or (at your option) any later version.
**
** DroidPlugin is distributed in the hope that it will be useful,
** but WITHOUT ANY WARRANTY; without even the implied warranty of
** MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
** Lesser General Public License for more details.
**
** You should have received a copy of the GNU Lesser General Public
** License along with DroidPlugin.  If not, see <http://www.gnu.org/licenses/lgpl.txt>
**
**/

package com.morgoo.droidplugin.hook;

import android.content.Context;
import com.morgoo.helper.Log;

import java.lang.reflect.Method;

public class HookedMethodHandler {

    private static final String TAG = HookedMethodHandler.class.getSimpleName();
    protected final Context mHostContext;

    private Object mFakedResult = null;//定义一个欺骗系统的成员变量
    private boolean mUseFakedResult = false;//是否使用欺骗系统的结果

    public HookedMethodHandler(Context hostContext) {
        this.mHostContext = hostContext;
    }


    public synchronized Object doHookInner(Object receiver, Method method, Object[] args) throws Throwable {
        long b = System.currentTimeMillis();
        try {
            mUseFakedResult = false;//默认是不欺骗
            mFakedResult = null;//默认为空
            boolean suc = beforeInvoke(receiver, method, args);
            Object invokeResult = null;
            if (!suc) {
                invokeResult = method.invoke(receiver, args);//false时执行原始方法
            }
            afterInvoke(receiver, method, args, invokeResult);
            if (mUseFakedResult) {//如果外部的flag设置为false，则返回欺骗的结果
                return mFakedResult;
            } else {
                return invokeResult;
            }
        } finally {
            long time = System.currentTimeMillis() - b;
            if (time > 5) {
                Log.i(TAG, "doHookInner method(%s.%s) cost %s ms", method.getDeclaringClass().getName(), method.getName(), time);
            }
        }
    }

    public void setFakedResult(Object fakedResult) {
        this.mFakedResult = fakedResult;
        mUseFakedResult = true;
    }

    /**
     * 鍦ㄦ煇涓柟娉曡璋冪敤涔嬪墠鎵ц锛屽鏋滆繑鍥瀟rue锛屽垯涓嶆墽琛屽師濮嬬殑鏂规硶锛屽惁鍒欐墽琛屽師濮嬫柟娉�
     */
    protected boolean beforeInvoke(Object receiver, Method method, Object[] args) throws Throwable {
        return false;
    }

    protected void afterInvoke(Object receiver, Method method, Object[] args, Object invokeResult) throws Throwable {
    }

    public boolean isFakedResult() {
        return mUseFakedResult;
    }

    public Object getFakedResult() {
        return mFakedResult;
    }
}
