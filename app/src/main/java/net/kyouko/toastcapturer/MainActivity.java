package net.kyouko.toastcapturer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import net.kyouko.toastcapturer.model.Message;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recycler;
    private MessageListAdapter adapter;
    private List<Message> messages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initToolbar();
        initRecyclerView();

        registerCapturerReceiver();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void initRecyclerView() {
        recycler = (RecyclerView) findViewById(R.id.recycler);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        adapter = new MessageListAdapter(messages);
        recycler.setAdapter(adapter);
    }

    private void registerCapturerReceiver() {
        final IntentFilter intentFilter = new IntentFilter(CapturerService.ACTION_CATCH_NOTIFICATION);
        intentFilter.addAction(CapturerService.ACTION_CATCH_TOAST);
        registerReceiver(new BroadcastReceiver() {
            @Override public void onReceive(Context context, Intent intent) {
                Log.e("Receiver", "Received message: " + intent.getStringExtra(CapturerService.EXTRA_MESSAGE));
                Log.e("Receiver", "from package: " + intent.getStringExtra(CapturerService.EXTRA_PACKAGE));
                Log.e("Receiver", "at: " + intent.getStringExtra(CapturerService.EXTRA_TIME));
                String messageType;
                switch (intent.getAction()) {
                    case CapturerService.ACTION_CATCH_NOTIFICATION:
                        messageType = getString(R.string.notification);
                        break;
                    case CapturerService.ACTION_CATCH_TOAST:
                    default:
                        messageType = getString(R.string.toast);
                        break;
                }
                messages.add(0, new Message(messageType,
                        intent.getStringExtra(CapturerService.EXTRA_PACKAGE),
                        intent.getStringExtra(CapturerService.EXTRA_TIME),
                        intent.getStringExtra(CapturerService.EXTRA_MESSAGE)));
                adapter.notifyItemInserted(0);
            }
        }, intentFilter);
    }

    @Override protected void onResume() {
        super.onResume();
        initCapturer();
    }

    private void initCapturer() {
        View disabledLayout = findViewById(R.id.layout_disabled);
        if (!CapturerService.isAccessibilityServiceEnabled(this)) {
            disabledLayout.setVisibility(View.VISIBLE);
            recycler.setVisibility(View.INVISIBLE);

            Button enableCapturerButton = (Button) findViewById(R.id.button_enable_capturer);
            enableCapturerButton.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle(R.string.title_dialog_enable_capturer)
                            .setMessage(R.string.text_dialog_enable_capturer)
                            .setPositiveButton(R.string.text_button_open_accessibility, new DialogInterface.OnClickListener() {
                                @Override public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
                                }
                            })
                            .setNegativeButton(R.string.text_button_cancel, new DialogInterface.OnClickListener() {
                                @Override public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                }
            });
        } else {
            disabledLayout.setVisibility(View.GONE);
            recycler.setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged();
            recycler.smoothScrollToPosition(0);

            Snackbar.make(findViewById(R.id.coordinator), R.string.text_label_capturer_enabled, Snackbar.LENGTH_SHORT);
        }
    }

}
