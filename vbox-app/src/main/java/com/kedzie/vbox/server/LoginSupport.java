package com.kedzie.vbox.server;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import androidx.appcompat.app.AppCompatActivity;
import timber.log.Timber;

import com.kedzie.vbox.R;
import com.kedzie.vbox.api.IVirtualBox;
import com.kedzie.vbox.app.Utils;
import com.kedzie.vbox.soap.VBoxSvc;
import com.kedzie.vbox.soap.SSLUtil;
import com.kedzie.vbox.task.DialogTask;

import org.ksoap2.SoapFault;

import java.io.IOException;
import java.security.cert.X509Certificate;

/**
 * Support to connect to vboxweb server with/without SSL
 * @author kedzie
 */
public class LoginSupport implements ServerListFragment.OnSelectServerListener {

  public static final int REQUEST_CODE_KEYCHAIN = 1;

  private AppCompatActivity activity;
  private LoginCallback callback;

  /**
   * For Untrusted certificates prompt user to save certificate to keystore
   */
  private Handler _sslHandler = new Handler(Looper.getMainLooper()) {
    @Override
    public void handleMessage(Message msg) {
      final Server server = msg.getData().getParcelable(Server.BUNDLE);
      if(msg.getData().getBoolean("isTrusted")) {
        new LogonTask(activity, callback).execute(server);
      } else {
        final X509Certificate[] chain = (X509Certificate[]) msg.getData().getSerializable("certs");

        X509Certificate root = chain[chain.length-1];
        String text = String.format("Issuer: %1$s\nSubject: %2$s", root.getIssuerDN().getName(), root.getSubjectDN().getName());

        new AlertDialog.Builder(activity)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle("Unrecognized Certificate")
            .setMessage("Do you trust this certificate?\n" + text)
            .setPositiveButton("Trust", new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
//                try {
//                  Intent intent = KeyChain.createInstallIntent().putExtra(KeyChain.EXTRA_CERTIFICATE, root.getEncoded());
//                  Utils.startActivityForResult(activity, intent, REQUEST_CODE_KEYCHAIN);
//                } catch (CertificateEncodingException e) {
//                  Timber.e(e, "Error encoding certificate");
//                }
                new SSLUtil.AddCertificateToKeystoreTask(activity, server) {
                  @Override
                  protected void onSuccess(Void result) {
                    super.onSuccess(result);
                    Utils.toastLong(getContext(), "Successfully updated keystore");
                    new LogonTask(activity, callback).execute(server);
                  };
                }.execute(chain);
                dialog.dismiss();
              }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
          }
        }).show();
      }
    }
  };

  public LoginSupport(AppCompatActivity activity, LoginCallback callback) {
    this.activity = activity;
    this.callback = callback;
  }



  @Override
  public void onSelectServer(final Server server) {
    if(server.isSSL())
      new Thread() {
        public void run() {
          try {
            new VBoxSvc(server).pingInteractiveTLS(_sslHandler);
          } catch (Exception e1) {
            Timber.e(e1, "Error interactive ping");
          }
        }
      }.start();
    else {
      new LoginSupport.LogonTask(activity, callback).execute(server);
    }
  }

  /**
   * Log on to VirtualBox webservice
   */
  public static class LogonTask extends DialogTask<Server, IVirtualBox> {
    private LoginCallback callback;

    public LogonTask(AppCompatActivity context, LoginCallback callback) {
      super(context, null, true);
      this.callback = callback;
    }

    @Override
    protected IVirtualBox work(Server... params) throws Exception {
      _vmgr = new VBoxSvc(params[0]);
      try {
        _vmgr.logon();
      } catch(SoapFault e) {
        //login error
        throw new IllegalAccessException("Authorization error.  try 'VBoxManage setproperty websrvauthlibrary null' if you cannot login");
      }
      _vmgr.getVBox().getVersion();
      return _vmgr.getVBox();
    }

    @Override
    protected void onSuccess(IVirtualBox vbox) {
      super.onSuccess(vbox);
      Utils.toastLong(getContext(), getContext().getString(R.string.toast_connected_to_vbox) + vbox.getVersion());
      callback.onLogin(_vmgr);
    }
  }

  /**
   * Disconnect from VirtualBox webservice
   */
  public static class LogoffTask extends AsyncTask<Void, Void, Void> {

    private VBoxSvc _vmgr;

    public LogoffTask(AppCompatActivity context, VBoxSvc vmgr) {
      super();
      _vmgr = vmgr;
    }

    @Override
    protected Void doInBackground(Void... params) {
      try {
        _vmgr.logoff();
      } catch (IOException e) {
      }
      return null;
    }
  }

  public interface LoginCallback {
    void onLogin(VBoxSvc vmgr);
  }
}
