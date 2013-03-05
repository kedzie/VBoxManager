package com.kedzie.vbox.server;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.kedzie.vbox.R;

/**
 * Detailed help information for launching <em>vboxwebsrv</em>
 */
public class HelpActivity extends SherlockActivity {
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        View view = LayoutInflater.from(this).inflate(R.layout.help, null);
        TextView sslText = (TextView)view.findViewById(R.id.ssl_text);
        Html.fromHtml(getResources().getString(R.string.help_ssl));
    }

}
