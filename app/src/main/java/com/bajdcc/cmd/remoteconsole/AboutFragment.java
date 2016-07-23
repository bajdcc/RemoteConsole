package com.bajdcc.cmd.remoteconsole;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.hannesdorfmann.fragmentargs.FragmentArgs;
import com.hannesdorfmann.fragmentargs.annotation.Arg;
import com.hannesdorfmann.fragmentargs.annotation.FragmentWithArgs;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

@FragmentWithArgs
public class AboutFragment extends Fragment {

    @BindString(R.string.about)
    String aboutText;

    @BindView(R.id.about_msg)
    TextView aboutTextView;

    @Arg
    private String title;

    public AboutFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentArgs.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_about, container, false);
        ButterKnife.bind(this, v);
        Toast.makeText(getActivity(), aboutText, Toast.LENGTH_LONG).show();
        aboutTextView.setText(aboutText);
        return v;
    }

    // Setter method for private field
    public void setTitle(String title) {
        this.title = title;
    }
}
