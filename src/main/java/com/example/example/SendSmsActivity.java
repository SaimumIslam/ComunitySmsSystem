package com.example.example;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class SendSmsActivity extends AppCompatActivity {
    Button send,group,template;
    EditText EtGroup,EtMessage;
    ListView listView;
    String ConDetail[];

    ArrayList Grouplist;
    ArrayAdapter<String> adapter;
    DBHelper mydb;
    TempleteDbHelper tempdb;
    String message="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_sms);

        tempdb = new TempleteDbHelper(this);
        mydb = new DBHelper(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 1);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 1);
            }
        } else {
            //do nothing;
        }

        send = (Button) findViewById(R.id.send);
        group = (Button) findViewById(R.id.Group);
        template = (Button) findViewById(R.id.template);


        EtGroup = (EditText) findViewById(R.id.GroupName);
        EtMessage = (EditText) findViewById(R.id.message);

        template.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccessContact();
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                for (String aStr : ConDetail) {
                    String split[] = aStr.split("=");
                    String sms=message.replace("XXX",split[0]);
                    //Snackbar.make(view,sms,Snackbar.LENGTH_LONG).show();
                    //Snackbar.make(view,split[1],Snackbar.LENGTH_LONG).show();
                    sendSms(split[1],sms);
                }
            }
        });
    }
    public void AccessContact()
    {
        View mView = LayoutInflater.from(this).inflate(R.layout.select_contact, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(mView);
        listView= (ListView)mView.findViewById(R.id.list);

        Grouplist = mydb.getAllGroup();
        getContact(Grouplist);
        builder
                .setPositiveButton("Select", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        SelectOption();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    public void getContact(ArrayList<String> list)
    {
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_multiple_choice, list);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        //listView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
    }
    public void SelectOption() {
        SparseBooleanArray checked = listView.getCheckedItemPositions();
        for (int i = 0; i < checked.size(); i++) {
            // Item position in adapter
            int position = checked.keyAt(i);
            // Add sport if it is checked i.e.) == TRUE!
            if (checked.valueAt(i))
            {
                EtGroup.setText((CharSequence) Grouplist.get(position));
                String salt= mydb.getDescription(String.valueOf(position+1));
                ConDetail=salt.split(",");
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        switch (requestCode)
        {
            case 1:
            {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    if(ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED)
                    {
                        Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(this, "No permission granted!", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }
    public void sendSms(String numbe,String sms)
    {
        try{
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(numbe,null,sms,null,null);
            Toast.makeText(SendSmsActivity.this, "Sent!", Toast.LENGTH_SHORT).show();

        }
        catch (Exception e)
        {
            Toast.makeText(SendSmsActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
        }
    }
}