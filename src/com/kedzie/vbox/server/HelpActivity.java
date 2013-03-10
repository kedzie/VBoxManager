package com.kedzie.vbox.server;

import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.kedzie.vbox.R;

/**
 * Detailed help information for launching <em>vboxwebsrv</em>
 */
public class HelpActivity extends SherlockActivity {
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help);
        
        TextView mainText = (TextView)findViewById(R.id.main_text);
        mainText.setText(Html.fromHtml(getResources().getString(R.string.help_main)));
        mainText.setMovementMethod(LinkMovementMethod.getInstance());
        
        TextView sslText = (TextView)findViewById(R.id.ssl_text);
        sslText.setText(Html.fromHtml(getResources().getString(R.string.help_ssl)));
        sslText.setMovementMethod(LinkMovementMethod.getInstance());
    }

}
