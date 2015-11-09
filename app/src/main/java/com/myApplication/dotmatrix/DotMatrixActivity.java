package com.myApplication.dotmatrix;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

public class DotMatrixActivity extends AppCompatActivity {
    protected static final int DIALOG_SIMPLE_MESSAGE = 0;
    protected static final int DIALOG_ERROR_MESSAGE =1;
    String result = new String();
    BackThread thread = new BackThread();
    boolean start = false, restart = false;
    boolean alive =true;
    private int speed = 20;
    String value;
    Handler handler = new Handler();

    public native int DotMatrixControl(String data);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        System.loadLibrary("dotmatrix");
        thread.setDaemon(true);
        thread.start();

        final EditText input = (EditText) findViewById(R.id.input);
        final Button ButStart = (Button) findViewById(R.id.ButStart);
        ButStart.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                value = input.getText().toString();
                if(start){
                    restart = true;
                    start = false;
                    ButStart.setText("Start");
                }else{
                    start = true;
                    restart = true;
                    ButStart.setText("Stop");
                }
            }
        });
    }

    class BackThread extends Thread {
        public void run() {
            while (alive) {
                if (!start) {

                } else {
                    if (value.length() > 50) {
                        handler.post(new Runnable() {
                            public void run() {
                                showDialog(DIALOG_SIMPLE_MESSAGE);
                            }
                        });
                        start = false;
                        continue;
                    } else {
                        int i, j , ch;
                        char buf[]= new char[100];
                        buf = value.toCharArray();
                        result = "00000000000000000000";
                        for (i = 0; i < value.length(); i++) {
                            ch = Integer.valueOf(buf[i]);
                            if (ch < 32 || ch > 126) {
                                handler.post(new Runnable() {
                                    public void run() {
                                        showDialog(DIALOG_ERROR_MESSAGE);
                                    }
                                });
                                start = false;
                                restart = false;
                                break;
                            }
                            ch -= 0x20;

                            // copy
                            for (j = 0; j < 5; j++) {
                                String str = new String();
                                str = Integer.toHexString((font[ch][j]));
                                if (str.length() < 2)
                                    result += "0";

                                result += str;
                            }
                            result += "00";
                        }
                        result += "00000000000000000000";
                        // print
                        for (i = 0; i < (result.length() - 18) / 2; i++) {
                            // speed control
                            for (j = 0; j < speed; j++) {
                                // thread control
                                if (!start) {
                                    break; // stop display
                                } else {
                                    DotMatrixControl(result.substring(2 * i,
                                            2 * i + 20));
                                }
                            }
                        }
                    }
                    DotMatrixControl("00000000000000000000");
                    }

                }
            }
        }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            start = false;
            alive = false;
            thread.interrupt();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        Dialog d = new Dialog(DotMatrixActivity.this);
        Window window = d.getWindow();
        window.setFlags(WindowManager.LayoutParams.FIRST_APPLICATION_WINDOW,
                WindowManager.LayoutParams.FIRST_APPLICATION_WINDOW);

        switch (id) {
            case DIALOG_SIMPLE_MESSAGE:
                d.setTitle("Max input length is 50.");
                d.show();
                return d;
            case DIALOG_ERROR_MESSAGE:
                d.setTitle("Unsupported character.");
                d.show();
                return d;
        }
        return super.onCreateDialog(id);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dot_matrix, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public int font[][] = { /* 5x7 ASCII character font */
            { 0x00, 0x00, 0x00, 0x00, 0x00 }, /* 0x20 space */
            { 0x00, 0x00, 0x4f, 0x00, 0x00 }, /* 0x21 ! */
            { 0x00, 0x07, 0x00, 0x07, 0x00 }, /* 0x22 " */
            { 0x14, 0x7f, 0x14, 0x7f, 0x14 }, /* 0x23 # */
            { 0x24, 0x2a, 0x7f, 0x2a, 0x12 }, /* 0x24 $ */
            { 0x23, 0x13, 0x08, 0x64, 0x62 }, /* 0x25 % */
            { 0x36, 0x49, 0x55, 0x22, 0x50 }, /* 0x26 & */
            { 0x00, 0x05, 0x03, 0x00, 0x00 }, /* 0x27 ' */
            { 0x00, 0x1c, 0x22, 0x41, 0x00 }, /* 0x28 ( */
            { 0x00, 0x41, 0x22, 0x1c, 0x00 }, /* 0x29 ) */
            { 0x14, 0x08, 0x3e, 0x08, 0x14 }, /* 0x2a * */
            { 0x08, 0x08, 0x3e, 0x08, 0x08 }, /* 0x2b + */
            { 0x00, 0x50, 0x30, 0x00, 0x00 }, /* 0x2c , */
            { 0x08, 0x08, 0x08, 0x08, 0x08 }, /* 0x2d - */
            { 0x00, 0x60, 0x60, 0x00, 0x00 }, /* 0x2e . */
            { 0x20, 0x10, 0x08, 0x04, 0x02 }, /* 0x2f / */
            { 0x3e, 0x51, 0x49, 0x45, 0x3e }, /* 0x30 0 */
            { 0x00, 0x42, 0x7f, 0x40, 0x00 }, /* 0x31 1 */
            { 0x42, 0x61, 0x51, 0x49, 0x46 }, /* 0x32 2 */
            { 0x21, 0x41, 0x45, 0x4b, 0x31 }, /* 0x33 3 */
            { 0x18, 0x14, 0x12, 0x7f, 0x10 }, /* 0x34 4 */
            { 0x27, 0x45, 0x45, 0x45, 0x39 }, /* 0x35 5 */
            { 0x3c, 0x4a, 0x49, 0x49, 0x30 }, /* 0x36 6 */
            { 0x01, 0x71, 0x09, 0x05, 0x03 }, /* 0x37 7 */
            { 0x36, 0x49, 0x49, 0x49, 0x36 }, /* 0x38 8 */
            { 0x06, 0x49, 0x49, 0x29, 0x1e }, /* 0x39 9 */
            { 0x00, 0x36, 0x36, 0x00, 0x00 }, /* 0x3a : */
            { 0x00, 0x56, 0x36, 0x00, 0x00 }, /* 0x3b ; */
            { 0x08, 0x14, 0x22, 0x41, 0x00 }, /* 0x3c < */
            { 0x14, 0x14, 0x14, 0x14, 0x14 }, /* 0x3d = */
            { 0x00, 0x41, 0x22, 0x14, 0x08 }, /* 0x3e > */
            { 0x02, 0x01, 0x51, 0x09, 0x06 }, /* 0x3f ? */
            { 0x32, 0x49, 0x79, 0x41, 0x3e }, /* 0x40 @ */
            { 0x7e, 0x11, 0x11, 0x11, 0x7e }, /* 0x41 A */
            { 0x7f, 0x49, 0x49, 0x49, 0x36 }, /* 0x42 B */
            { 0x3e, 0x41, 0x41, 0x41, 0x22 }, /* 0x43 C */
            { 0x7f, 0x41, 0x41, 0x22, 0x1c }, /* 0x44 D */
            { 0x7f, 0x49, 0x49, 0x49, 0x41 }, /* 0x45 E */
            { 0x7f, 0x09, 0x09, 0x09, 0x01 }, /* 0x46 F */
            { 0x3e, 0x41, 0x49, 0x49, 0x7a }, /* 0x47 G */
            { 0x7f, 0x08, 0x08, 0x08, 0x7f }, /* 0x48 H */
            { 0x00, 0x41, 0x7f, 0x41, 0x00 }, /* 0x49 I */
            { 0x20, 0x40, 0x41, 0x3f, 0x01 }, /* 0x4a J */
            { 0x7f, 0x08, 0x14, 0x22, 0x41 }, /* 0x4b K */
            { 0x7f, 0x40, 0x40, 0x40, 0x40 }, /* 0x4c L */
            { 0x7f, 0x02, 0x0c, 0x02, 0x7f }, /* 0x4d M */
            { 0x7f, 0x04, 0x08, 0x10, 0x7f }, /* 0x4e N */
            { 0x3e, 0x41, 0x41, 0x41, 0x3e }, /* 0x4f O */
            { 0x7f, 0x09, 0x09, 0x09, 0x06 }, /* 0x50 P */
            { 0x3e, 0x41, 0x51, 0x21, 0x5e }, /* 0x51 Q */
            { 0x7f, 0x09, 0x19, 0x29, 0x46 }, /* 0x52 R */
            { 0x26, 0x49, 0x49, 0x49, 0x32 }, /* 0x53 S */
            { 0x01, 0x01, 0x7f, 0x01, 0x01 }, /* 0x54 T */
            { 0x3f, 0x40, 0x40, 0x40, 0x3f }, /* 0x55 U */
            { 0x1f, 0x20, 0x40, 0x20, 0x1f }, /* 0x56 V */
            { 0x3f, 0x40, 0x38, 0x40, 0x3f }, /* 0x57 W */
            { 0x63, 0x14, 0x08, 0x14, 0x63 }, /* 0x58 X */
            { 0x07, 0x08, 0x70, 0x08, 0x07 }, /* 0x59 Y */
            { 0x61, 0x51, 0x49, 0x45, 0x43 }, /* 0x5a Z */
            { 0x00, 0x7f, 0x41, 0x41, 0x00 }, /* 0x5b [ */
            { 0x02, 0x04, 0x08, 0x10, 0x20 }, /* 0x5c \ */
            { 0x00, 0x41, 0x41, 0x7f, 0x00 }, /* 0x5d ] */
            { 0x04, 0x02, 0x01, 0x02, 0x04 }, /* 0x5e ^ */
            { 0x40, 0x40, 0x40, 0x40, 0x40 }, /* 0x5f _ */
            { 0x00, 0x01, 0x02, 0x04, 0x00 }, /* 0x60 ` */
            { 0x20, 0x54, 0x54, 0x54, 0x78 }, /* 0x61 a */
            { 0x7f, 0x48, 0x44, 0x44, 0x38 }, /* 0x62 b */
            { 0x38, 0x44, 0x44, 0x44, 0x20 }, /* 0x63 c */
            { 0x38, 0x44, 0x44, 0x48, 0x7f }, /* 0x64 d */
            { 0x38, 0x54, 0x54, 0x54, 0x18 }, /* 0x65 e */
            { 0x08, 0x7e, 0x09, 0x01, 0x02 }, /* 0x66 f */
            { 0x0c, 0x52, 0x52, 0x52, 0x3e }, /* 0x67 g */
            { 0x7f, 0x08, 0x04, 0x04, 0x78 }, /* 0x68 h */
            { 0x00, 0x04, 0x7d, 0x00, 0x00 }, /* 0x69 i */
            { 0x20, 0x40, 0x44, 0x3d, 0x00 }, /* 0x6a j */
            { 0x7f, 0x10, 0x28, 0x44, 0x00 }, /* 0x6b k */
            { 0x00, 0x41, 0x7f, 0x40, 0x00 }, /* 0x6c l */
            { 0x7c, 0x04, 0x18, 0x04, 0x7c }, /* 0x6d m */
            { 0x7c, 0x08, 0x04, 0x04, 0x78 }, /* 0x6e n */
            { 0x38, 0x44, 0x44, 0x44, 0x38 }, /* 0x6f o */
            { 0x7c, 0x14, 0x14, 0x14, 0x08 }, /* 0x70 p */
            { 0x08, 0x14, 0x14, 0x18, 0x7c }, /* 0x71 q */
            { 0x7c, 0x08, 0x04, 0x04, 0x08 }, /* 0x72 r */
            { 0x48, 0x54, 0x54, 0x54, 0x20 }, /* 0x73 s */
            { 0x04, 0x3f, 0x44, 0x40, 0x20 }, /* 0x74 t */
            { 0x3c, 0x40, 0x40, 0x20, 0x7c }, /* 0x75 u */
            { 0x1c, 0x20, 0x40, 0x20, 0x1c }, /* 0x76 v */
            { 0x3c, 0x40, 0x30, 0x40, 0x3c }, /* 0x77 w */
            { 0x44, 0x28, 0x10, 0x28, 0x44 }, /* 0x78 x */
            { 0x0c, 0x50, 0x50, 0x50, 0x3c }, /* 0x79 y */
            { 0x44, 0x64, 0x54, 0x4c, 0x44 }, /* 0x7a z */
            { 0x00, 0x08, 0x36, 0x41, 0x00 }, /* 0x7b { */
            { 0x00, 0x00, 0x77, 0x00, 0x00 }, /* 0x7c | */
            { 0x00, 0x41, 0x36, 0x08, 0x00 }, /* 0x7d } */
            { 0x08, 0x04, 0x08, 0x10, 0x08 } }; /* 0x7e ~ */

}
