package io.github.tipline.android_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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

    //handle clicks on the main menu for each tip type (text, call, voice, camera)
    @Override
    //tip options if clicked execute
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


}
