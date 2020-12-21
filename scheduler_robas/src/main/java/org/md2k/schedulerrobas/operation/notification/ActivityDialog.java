package org.md2k.schedulerrobas.operation.notification;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.md2k.schedulerrobas.R;

public class ActivityDialog extends AppCompatActivity {

    public static Activity fa;
    public static final String TYPE = "TYPE";
    public static final String TITLE = "TITLE";
    public static final String CONTENT = "CONTENT";
    public static final String BUTTONS = "BUTTONS";
    public static final String CONFIRM = "CONFIRM";
    public static final String RESULT = "RESULT";
    public static final String INTENT_RESULT = "INTENT_RESULT";

    String title, content, type;
    String[] buttons;
    boolean[] confirm;
//    MaterialDialog dialo, dialogConfir;
    AlertDialog dialogg, dialoggConfirm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
        fa = this;
        getValues();
        showDialogg();
    }
    @Override
    protected void onDestroy(){
        try{
//            dialog.dismiss();
            dialogg.dismiss();
        }catch (Exception ignored){}
        try{
            dialoggConfirm.dismiss();
//            dialogConfirm.dismiss();
        }catch (Exception ignored){}
        super.onDestroy();
    }
    private void showDialogg(){
        AlertDialog.Builder myDialogBox = new AlertDialog.Builder(this);
        myDialogBox.setTitle(title);
        myDialogBox.setMessage(content);
        myDialogBox.setCancelable(false);
        if (buttons.length > 0) {
            myDialogBox.setPositiveButton(buttons[0], new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (confirm[0]) {
                        showDialoggConfirm(buttons[0]);
                    } else sendData(buttons[0]);
                }
            });
        }
        if (buttons.length > 1) {
            myDialogBox.setNegativeButton(buttons[1], new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (confirm[1]) {
                        showDialoggConfirm(buttons[1]);
                    } else sendData(buttons[1]);

                }
            });
        }
        if (buttons.length > 2) {
            myDialogBox.setNeutralButton(buttons[2], new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (confirm[2]) {
                        showDialoggConfirm(buttons[2]);
                    } else sendData(buttons[2]);
                }
            });
        }

        dialogg = myDialogBox.create();
        dialogg.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialogg.show();
    }

/*
    private void showDialog() {
        MaterialDialog.Builder md = new MaterialDialog.Builder(this)
                .title(title)
                .titleGravity(GravityEnum.CENTER)
                .content(content)
                .contentGravity(GravityEnum.CENTER)
                .canceledOnTouchOutside(false)
                .cancelable(false);
        if (buttons.length > 0) {
            md = md.positiveText(buttons[0]).onPositive((dialog, which) -> {
                if (confirm[0]) {
                    showDialogConfirm(buttons[0]);
                } else sendData(buttons[0]);
            });
        }
        if (buttons.length > 1) {
            md = md.negativeText(buttons[1]).onNegative((dialog, which) -> {
                if (confirm[1]) {
                    showDialogConfirm(buttons[1]);
                } else sendData(buttons[1]);
            });
        }
        if (buttons.length > 2) {
            md = md.neutralText(buttons[2]).onNeutral((dialog, which) -> {
                if (confirm[2]) {
                    showDialogConfirm(buttons[2]);
                } else sendData(buttons[2]);
            });
        }

        dialog = md.build();
        dialog.show();
    }
*/
private void showDialoggConfirm(String text) {
    AlertDialog.Builder myDialogBox = new AlertDialog.Builder(this);
    myDialogBox.setTitle("Confirm");
    myDialogBox.setMessage(content+".\n\nYou select: \""+text+"\"");
    myDialogBox.setCancelable(false);
    myDialogBox.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sendData(text);
            }
        });
        myDialogBox.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialoggConfirm.dismiss();
                showDialogg();
            }
        });
    dialoggConfirm = myDialogBox.create();
    dialoggConfirm.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
    dialoggConfirm.show();
}

/*
    private void showDialogConfirm(String text) {
        MaterialDialog.Builder md = new MaterialDialog.Builder(this)
                .title("Confirm")
                .content(content+".\n\nYou select: \""+text+"\"")
                .contentGravity(GravityEnum.CENTER)
                .cancelable(false)
                .canceledOnTouchOutside(false)
                .positiveText("Confirm")
                .onPositive((dialog, which) -> sendData(text)).negativeText("Cancel").onNegative((dialog, which) -> {
                    dialogConfirm.dismiss();
                    showDialog();
                });
        dialogConfirm=md.build();
        dialogConfirm.show();
    }
*/

    void getValues() {
        type = getIntent().getStringExtra(TYPE);
        title = getIntent().getStringExtra(TITLE);
        content = getIntent().getStringExtra(CONTENT);
        buttons = getIntent().getStringArrayExtra(BUTTONS);
        confirm = getIntent().getBooleanArrayExtra(CONFIRM);
    }
    void sendData(String data){
        Intent intent = new Intent(INTENT_RESULT);
        // You can also include some extra data.
        intent.putExtra(RESULT, data);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
