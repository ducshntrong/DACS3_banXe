package com.example.banhang.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.banhang.R;
import com.example.banhang.utils.Utils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

import io.paperdb.Paper;

public class LienHeActivity extends AppCompatActivity {
    Toolbar toobar;
    TextInputEditText msg,name,phone,subject;
    MaterialButton cancel_button,submit_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lienhe);
        AnhXa();
        ActionToolbar();
        initControl();
    }
    private void ActionToolbar() {
        setSupportActionBar(toobar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toobar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    private void AnhXa() {
        toobar = findViewById(R.id.toobar);
        name = findViewById(R.id.name_edit_text);
        phone = findViewById(R.id.phone_number_edit_text);
        subject = findViewById(R.id.sub_edit_text);
        msg = findViewById(R.id.msg_edit_text);
        cancel_button = findViewById(R.id.cancel_button);
        submit_button = findViewById(R.id.save_button);
    }
    private void initControl() {
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name.setText("");
                phone.setText("");
                subject.setText("");
                msg.setText("");
            }
        });
        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = "ducht.21it@vku.udn.vn,huynv.21it@vku.udn.vn";
                String str_name = name.getText().toString();
                String str_phone = phone.getText().toString();
                String str_subject = subject.getText().toString();
                String str_msg = msg.getText().toString();

                sendEmail(email, str_name, str_phone, str_subject, str_msg);
            }
        });
    }

    private void sendEmail(String email, String str_name, String str_phone, String str_subject, String str_msg) {
        Intent mIntent = new Intent(Intent.ACTION_SEND);
        mIntent.putExtra(Intent.EXTRA_EMAIL, email);
        mIntent.putExtra(Intent.EXTRA_SUBJECT, str_name);
        mIntent.putExtra(Intent.EXTRA_SUBJECT, str_phone);
        mIntent.putExtra(Intent.EXTRA_SUBJECT, str_subject);
        mIntent.putExtra(Intent.EXTRA_TEXT, str_msg);

        //need this to prompts email client only
        mIntent.setType("message/rfc822");

        startActivity(Intent.createChooser(mIntent, "Choose an Email client :"));
    }
}
