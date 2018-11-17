package com.example.mainactivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Messages extends AppCompatActivity implements View.OnClickListener{

    int i = 0;

    Button Send, Back;
    EditText messageFromMe;
    TextView MessageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);


        Send = (Button) findViewById(R.id.button6);
        Back = (Button) findViewById(R.id.button7);

        Back.setOnClickListener(this);
        Send.setOnClickListener(this);


        messageFromMe =  (EditText) findViewById(R.id.editText);
        MessageView = (TextView) findViewById(R.id.windowText);


     }




     public void onClick(View v){
        switch(v.getId()){
            case R.id.button7:
                Intent intent1 = new Intent(this, ChatsActivity.class);
                startActivity(intent1);
                break;

            case R.id.button6:
                i++;
                Date date = new Date();
                SimpleDateFormat formatForDateNow = new SimpleDateFormat(" hh:mm:ss ");

                String time = formatForDateNow.format(date);
                String text =  messageFromMe.getText().toString();
                MessageView.append("                                                                        "+text + "\n");
                MessageView.append("                                                                     "+time.toString() + "\n");


                if(i==3){
                    MessageView.append(MainActivity.name + ": "+ "Не пиши мне!" + "\n");
                    MessageView.append(time.toString() + "\n");

                    AlertDialog.Builder builder = new AlertDialog.Builder(Messages.this);
                    builder.setTitle("")
                            .setMessage(MainActivity.name+ " решила прервать с вами общение. Мы нашли для Вас Данила!")
                            .setIcon(R.drawable.d11)
                            .setCancelable(false)
                            .setNegativeButton("Принять.",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                    MessageView.setText("");
                }

                messageFromMe.setText("");
                break;

//                try (Socket socket = new Socket("172.20.10.3", 3347);
//
//                     BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
//                     DataOutputStream oos = new DataOutputStream(socket.getOutputStream());
//                     DataInputStream ois = new DataInputStream(socket.getInputStream());) {
//
//                    if (!socket.isOutputShutdown()) {
//                        Invest_1 act = new Invest_1(oos, br);
//                        Outvest_1 act_2 = new Outvest_1(ois);
//                        Thread temp = new Thread(act);
//                        temp.start();
//                        Thread temp_2 = new Thread(act_2);
//                        temp_2.start();
//                        try {
//                            temp.join();
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        try {
//                            temp_2.join();
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//                catch (IOException e) {
//                    e.printStackTrace();
//                }
//                break;
            default: break;
        }
    }
//    private  void closeceyboard(){
//        View view = this.getCurrentFocus();
//        if(view != null){
//            //InputMethodManager imm = (InputMethodManager)getSystemServiceName(Context.INPUT_METHOD_SERVICE);
//        }
//    }
}


class Invest_1 implements Runnable{
    DataOutputStream out;
    BufferedReader br;
    public Invest_1(DataOutputStream o, BufferedReader b){
        out = o;
        br = b;
    }
    @Override
    public void run() {
        while(true){
            try {
                if(br.ready()){
                    String clientCommand = br.readLine();
                    if(clientCommand.equalsIgnoreCase("quit")){
                        System.out.println("Соединение разорвано.");
                        out.writeUTF(clientCommand);
                        out.flush();
                        break;
                    }
                    if (clientCommand.equalsIgnoreCase("image")){
                        out.writeUTF(clientCommand);
                        out.flush();
                        out.writeUTF("Здесь будет имя строки.");
                        out.flush();
                    }else {
                        out.writeUTF(clientCommand);
                        out.flush();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

class Outvest_1 implements Runnable{
    DataInputStream in;
    public Outvest_1(DataInputStream i){
        in = i;
    }
    @Override
    public void run() {
        while(true){
            try {
                String entry = in.readUTF();
                if(entry.equalsIgnoreCase("quit")){
                    System.out.println("Соединение разорвано.");
                    break;
                }
                if(entry.equalsIgnoreCase("image"))
                {
                    System.out.println("Image");
                    String pic = in.readUTF();
                    System.out.println(pic);
                } else {
                    System.out.println(entry);
                }
            } catch (IOException e) {
                break;
            }
        }
    }
}



