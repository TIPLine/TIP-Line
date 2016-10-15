package io.github.tipline.android_app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


public class TipMenuFragment extends Fragment implements View.OnClickListener {

    private Button textTipButton;
    private Button tipCallButton;
    private Button voiceButton;
    private Button photoButton;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.fragment_tip_menu, container, false);
        textTipButton = (Button) myView.findViewById(R.id.button_tip_text);
        textTipButton.setOnClickListener(this);
        voiceButton = (Button) myView.findViewById(R.id.button_tip_voice);
        voiceButton.setOnClickListener(this);
        photoButton = (Button) myView.findViewById(R.id.button_tip_camera);
        photoButton.setOnClickListener(this);
        tipCallButton = (Button) myView.findViewById(R.id.button_tip_call);
        tipCallButton.setOnClickListener(this);
        return myView;

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.button_tip_text:
                Intent textTipPage = new Intent(getActivity().getApplication(), TextTip.class);
                this.startActivity(textTipPage);
                break;

            case R.id.button_tip_call:
                call();
//                Intent myIntent = new Intent(getActivity().getApplication(), TipCall.class);
//                this.startActivity(myIntent);
                break;

            case R.id.button_tip_voice:
                Intent audioIntent = new Intent(getActivity().getApplication(), AudioTip.class);
                this.startActivity(audioIntent);
                break;

            case R.id.button_tip_camera:
                Intent cameraIntent = new Intent(getActivity().getApplication(), CameraTip.class);
                this.startActivity(cameraIntent);
                break;

            default:
                break;
        }

    }

    private void call() {
        Intent in=new Intent(Intent.ACTION_CALL, Uri.parse("6789100416"));
        try{
            startActivity(in);
        }

        catch (android.content.ActivityNotFoundException ex){
            Toast.makeText(getActivity(),"yourActivity is not founded",Toast.LENGTH_SHORT).show();
        }
    }

}
