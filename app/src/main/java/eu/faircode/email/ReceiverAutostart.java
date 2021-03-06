package eu.faircode.email;

/*
    This file is part of FairEmail.

    FairEmail is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    FairEmail is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with FairEmail.  If not, see <http://www.gnu.org/licenses/>.

    Copyright 2018-2019 by Marcel Bokhorst (M66B)
*/

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.List;

import static android.os.Process.THREAD_PRIORITY_BACKGROUND;

public class ReceiverAutostart extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()) ||
                Intent.ACTION_MY_PACKAGE_REPLACED.equals(intent.getAction())) {
            EntityLog.log(context, intent.getAction());
            ServiceSynchronize.init(context, true);

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        DB db = DB.getInstance(context);
                        List<EntityMessage> messages = db.message().getSnoozed();
                        for (EntityMessage message : messages)
                            EntityMessage.snooze(context, message.id, message.ui_snoozed);
                    } catch (Throwable ex) {
                        Log.e(ex);
                    }
                }
            });
            thread.setPriority(THREAD_PRIORITY_BACKGROUND);
            thread.start();
        }
    }
}
