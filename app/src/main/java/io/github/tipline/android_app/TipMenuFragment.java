package io.github.tipline.android_app;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class TipMenuFragment extends Fragment implements View.OnClickListener {

    private Button textTipButton;
    private Button tipCallButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.fragment_tip_menu, container, false);
        textTipButton = (Button) myView.findViewById(R.id.button_tip_text);
        textTipButton.setOnClickListener(this);
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
                Intent myIntent = new Intent(getActivity().getApplication(), TipCall.class);
                this.startActivity(myIntent);
                break;

            default:
                break;
        }

    }

}
