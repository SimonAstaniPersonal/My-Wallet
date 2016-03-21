package com.example.android.mywallet;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



public class MainActivity extends AppCompatActivity {

    FileInputStream inputStream;
    FileOutputStream outputStream;
    String file_output_data = "";
    String filename_amount = "cash_file";
    String filename_summary = "summary_file";
    String file_input_data = "";
    File file;
    int ch = 0;
    // 1 for main page
    // 2 for add page
    // 3 for spend page
    // 4 for summary page

    private int currentPage = 1;
    private int money_in_wallet = 0;
    private String summary_of_expenses = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getValuesIfStored();
        goToFirstPage();
    }
    public void getValuesIfStored(){
        try{
            inputStream = openFileInput(filename_amount);
            //file_input_data = inputStream.read();
            if(file_input_data.length()!=0) {
                file_input_data = "";
            }
            while( (ch = (inputStream.read())) != -1) {
                String temp = ""+(char)ch;
                file_input_data += temp;
            }
            inputStream.close();
            money_in_wallet = Integer.parseInt(file_input_data);
        }
        catch (IOException e){
            money_in_wallet = 0;
        }



        try{
            inputStream = openFileInput(filename_summary);
            //file_input_data = inputStream.read();
            if(file_input_data.length()!=0) {
                file_input_data = "";
            }
            while( (ch = (inputStream.read())) != -1) {
                String temp = ""+(char)ch;
                file_input_data += temp;
            }
            inputStream.close();
            summary_of_expenses = file_input_data;
        }
        catch (IOException e){
            summary_of_expenses = "";
        }
    }

    @Override
    public void onBackPressed() {
        if (currentPage == 1) {
            new AlertDialog.Builder(this)
                    .setMessage("Are you sure you want to exit?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            System.exit(0);
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        }
        if (currentPage == 2) {
            goToFirstPage();
        }
        if (currentPage == 3) {
            goToFirstPage();
        }
        if (currentPage == 4) {
            goToFirstPage();
        }
    }

    public void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    public void goToFirstPage() {
	    setContentView(R.layout.activity_main);
        currentPage = 1;
        TextView textView = (TextView) findViewById(R.id.money_in_wallet);
        String temp = "Rs. " + String.valueOf(money_in_wallet);
        textView.setText(temp);

    }

    public void goToSpendPage(View view) {
        setContentView(R.layout.spend_from_wallet_page);
        initiateCategoryList();
        currentPage = 3;
    }

    public void goToAddPage(View view) {
        setContentView(R.layout.add_to_wallet_page);
        currentPage = 2;
    }

    public void goToSummaryPage(View view) {
        setContentView(R.layout.wallet_spend_summary_page);
        currentPage = 4;
        updateSummary();
    }

    public void initiateCategoryList() {
        Spinner spinner = (Spinner) findViewById(R.id.category_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.category_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    public void updateSummary() {
        TextView textView = (TextView) findViewById(R.id.summary_of_expenses);
        textView.setText(summary_of_expenses);
    }
    public void saveMoney(String amount) {
        money_in_wallet +=  Integer.parseInt(amount);


        file = new File(this.getFilesDir(), filename_amount);
        try {
            file_output_data = String.valueOf(money_in_wallet);
            outputStream = openFileOutput(filename_amount, Context.MODE_PRIVATE);
            outputStream.write(file_output_data.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }




        goToFirstPage();
        Toast toast = Toast.makeText(this, "Added Rs." + String.valueOf(amount) + ".", Toast.LENGTH_SHORT);
        toast.show();


    }
    public void deleteMoney(String amount, String category) {
        if(money_in_wallet - Integer.parseInt(amount) < 0) {
            Toast toast = Toast.makeText(this, "Cannot spend " + String.valueOf(amount) + ". Wallet has got only Rs." + String.valueOf(money_in_wallet) + ".", Toast.LENGTH_SHORT);
            toast.show();
        }
        else {
            money_in_wallet -= Integer.parseInt(amount);


            file = new File(this.getFilesDir(), filename_amount);
            try {
                file_output_data = String.valueOf(money_in_wallet);
                outputStream = openFileOutput(filename_amount, Context.MODE_PRIVATE);
                outputStream.write(file_output_data.getBytes());
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }



            summary_of_expenses += "Spent Rs." + amount +" on " + category + "\n";


            file = new File(this.getFilesDir(), filename_summary);
            try {
                file_output_data = summary_of_expenses;
                outputStream = openFileOutput(filename_summary, Context.MODE_PRIVATE);
                outputStream.write(file_output_data.getBytes());
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }


            goToFirstPage();
            Toast toast = Toast.makeText(this, "Spent Rs." + String.valueOf(amount) + ".", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
    public void clearSummary_text() {
        summary_of_expenses = "";


        file = new File(this.getFilesDir(), filename_summary);
        try {
            file_output_data = summary_of_expenses;
            outputStream = openFileOutput(filename_summary, Context.MODE_PRIVATE);
            outputStream.write(file_output_data.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }



        goToFirstPage();
        Toast toast = Toast.makeText(this, "Expenses summary has been deleted", Toast.LENGTH_SHORT);
        toast.show();
    }

    public void clearSummary(View view) {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to clear the summary?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        clearSummary_text();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }


    public void addAmount(View view) {
        final EditText input = (EditText) findViewById(R.id.amount_to_add);
        if(input.getText().toString().length() == 0) {
            Toast toast = Toast.makeText(this, "Value is empty", Toast.LENGTH_SHORT);
            toast.show();
        }
        else
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to add Rs." + input.getText().toString() + " to your wallet?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        hideSoftKeyboard();
                        saveMoney(input.getText().toString());
                    }
                })
                .setNegativeButton("No", null)
                .show();

    }

    public void spendAmount(View view) {
        final EditText input = (EditText) findViewById(R.id.amount_to_spend);
        final Spinner spinner = (Spinner)findViewById(R.id.category_spinner);
        if(input.getText().toString().length() == 0) {
            Toast toast = Toast.makeText(this, "Value is empty", Toast.LENGTH_SHORT);
            toast.show();
        }
        else
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to spend Rs." + input.getText().toString() + " on " + spinner.getSelectedItem().toString() + " from your wallet?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        hideSoftKeyboard();
                        deleteMoney(input.getText().toString(), spinner.getSelectedItem().toString());
                    }
                })
                .setNegativeButton("No", null)
                .show();

    }
}
