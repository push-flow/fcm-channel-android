package io.fcmchannel.sdk.sample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import io.fcmchannel.sdk.FcmClient;
import io.fcmchannel.sdk.services.FcmClientRegistrationIntentService;

public class LoginActivity extends AppCompatActivity {

    private BroadcastReceiver registrationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            navigateToChat();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (FcmClient.isContactRegistered()) {
            navigateToChat();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setupView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerBroadcastReceivers();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterBroadcastReceivers();
    }

    private void registerBroadcastReceivers() {
        final IntentFilter registrationFilter = new IntentFilter(
            FcmClientRegistrationIntentService.ACTION_REGISTRATION_COMPLETE
        );
        LocalBroadcastManager.getInstance(this).registerReceiver(registrationReceiver, registrationFilter);
    }

    private void unregisterBroadcastReceivers() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(registrationReceiver);
    }

    private void setupView() {
        final EditText editText = findViewById(R.id.email);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String urn = editText.getText().toString().trim();

                if (!TextUtils.isEmpty(urn)) {
                    showLoading();
                    FcmClient.registerContact(urn);
                }
            }
        });
    }

    private void showLoading() {
        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
    }

    private void navigateToChat() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

}
