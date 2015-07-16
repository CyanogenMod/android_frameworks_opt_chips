/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.ex.chips;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Process;

public class ChipsUtil {

    /**
     * Permissions required by Chips library.
     */
    public static final String[] REQUIRED_PERMISSIONS =
            new String[] { Manifest.permission.READ_CONTACTS };

    /**
     * Returns true when the caller can use Chips UI in its environment.
     */
    public static boolean supportsChipsUi() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    }

    /**
     * Whether we are running on M or later version.
     *
     * <p>This is interesting for us because new permission model is introduced in M and we need to
     * check if we have {@link #REQUIRED_PERMISSIONS}.
     */
    public static boolean isRunningMncOrLater() {
        // TODO: Update to use M once it's finalized in VERSION_CODES
        return Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1;
    }

    /**
     * Returns {@link PackageManager#PERMISSION_GRANTED} if given permission is granted, or
     * {@link PackageManager#PERMISSION_DENIED} if not.
     */
    public static int checkPermission(Context context, String permission) {
        if (isRunningMncOrLater()) {
            // Use "context.checkSelfPermission(permission)" once it's safe to move to M sdk
            return context.checkPermission(permission, Process.myPid(), Process.myUid());
        } else {
            // Assume that we have permission before M.
            return PackageManager.PERMISSION_GRANTED;
        }
    }

    /**
     * Returns true if all permissions in {@link #REQUIRED_PERMISSIONS} are granted.
     */
    public static boolean hasPermissions(Context context) {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (checkPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
}