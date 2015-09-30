package com.mierzejewski.inzynierka;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

/**
 * Created by dom on 27/11/14.
 */
public class DiffrentAmmountDialog extends DialogFragment
{
    View rootView;
    Button okButton;
    EditText ammount;

    interface EventListener {
        void onChangeAmmount(double newVal);
    }

   View.OnClickListener onOkListener = new View.OnClickListener()
   {
       @Override
       public void onClick(View v)
       {
           Double value = Double.parseDouble(ammount.getText().toString());
           Activity act = getActivity();
           if(act instanceof DiffrentAmmountDialog.EventListener)
               ((EventListener) act).onChangeAmmount(value);
           dismiss();
       }
   };


   @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
   {
       super.onCreateView(inflater, container, savedInstanceState);

       getDialog().setTitle(R.string.diffrent_ammount);
       rootView = inflater.inflate(R.layout.diffrent_amount_dialog, container, false);
       ammount = (EditText) rootView.findViewById(R.id.ammount);
       okButton = (Button) rootView.findViewById(R.id.okButton);
       okButton.setOnClickListener(onOkListener);


       return rootView;
   }


}
