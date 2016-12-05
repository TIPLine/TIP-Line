package io.github.tipline.android_app;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import android.support.v4.content.ContextCompat;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.annotation.NonNull;

public class TipMenuFragment extends Fragment implements View.OnClickListener {

    private Button textTipButton;
    private Button tipCallButton;
    private Button voiceButton;
    private Button photoButton;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.fragment_tip_menu, container, false);
        textTipButton = (Button) myView.findViewById(R.id.buttonTipText);
        textTipButton.setOnClickListener(this);
        voiceButton = (Button) myView.findViewById(R.id.buttonTipVoice);
        voiceButton.setOnClickListener(this);
        photoButton = (Button) myView.findViewById(R.id.buttonTipCamera);
        photoButton.setOnClickListener(this);
        tipCallButton = (Button) myView.findViewById(R.id.buttonTipCall);
        tipCallButton.setOnClickListener(this);
        return myView;

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.buttonTipText:
                Intent textTipPage = new Intent(getActivity().getApplication(), TextTip.class);
                this.startActivity(textTipPage);
                break;

            case R.id.buttonTipCall:
                Intent myIntent = new Intent(getActivity().getApplication(), TipCall.class);
                this.startActivity(myIntent);
                break;

            case R.id.buttonTipVoice:
                Intent audioIntent = new Intent(getActivity().getApplication(), AudioTip.class);
                this.startActivity(audioIntent);
                break;

            case R.id.buttonTipCamera:
                Intent cameraIntent = new Intent(getActivity().getApplication(), CameraTip.class);
                this.startActivity(cameraIntent);
                break;

            default:
                break;
        }

    }

    public void call() {
        int permissionCheck = ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.CALL_PHONE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    getActivity(), new String[]{Manifest.permission.CALL_PHONE}, 123);
        } else {

            Intent in = new Intent(Intent.ACTION_CALL, Uri.parse("tel:6789100416"));
            try {
                startActivity(in);
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(getActivity(), "yourActivity is not founded", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            case 123:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    call();
                } else {
                    Log.d("TAG", "Call Permission Not Granted");
                }
                break;

            default:
                break;
        }
    }

    //monitor phone call activities
    private class PhoneCallListener extends PhoneStateListener {

        private boolean isPhoneCalling = false;

        String LOG_TAG = "LOGGING 123";

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {

            if (TelephonyManager.CALL_STATE_RINGING == state) {
                // phone ringing
                Log.i(LOG_TAG, "RINGING, number: " + incomingNumber);
            }

            if (TelephonyManager.CALL_STATE_OFFHOOK == state) {
                // active
                Log.i(LOG_TAG, "OFFHOOK");

                isPhoneCalling = true;
            }

            if (TelephonyManager.CALL_STATE_IDLE == state) {
                // run when class initial and phone call ended,
                // need detect flag from CALL_STATE_OFFHOOK
                Log.i(LOG_TAG, "IDLE");

                if (isPhoneCalling) {

                    Log.i(LOG_TAG, "restart app");

                    // restart app
                    Intent i = getActivity().getBaseContext().getPackageManager()
                            .getLaunchIntentForPackage(
                                    getActivity().getBaseContext().getPackageName());
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);

                    isPhoneCalling = false;
                }

            }
        }
    }


}
