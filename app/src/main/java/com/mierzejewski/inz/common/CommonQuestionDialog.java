package com.mierzejewski.inz.common;

import java.io.Serializable;
import java.lang.ref.WeakReference;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import com.mierzejewski.inz.R;

public class CommonQuestionDialog
        extends
        Dialog
        implements
        android.view.View.OnClickListener
{
    private WeakReference<OnClickListener> onClickListener;

    public CommonQuestionDialog(Context context, String text, boolean cancelAble)
    {
        super(context);

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.fragment_common_question_dialog, null);

        Button yesButton = (Button) dialogView.findViewById(R.id.dialog_button_yes);
        yesButton.setOnClickListener(this);

        Button noButton = (Button) dialogView.findViewById(R.id.dialog_button_no);
        noButton.setOnClickListener(this);

        TextView title = (TextView) dialogView.findViewById(R.id.textTitle);
        title.setText(text);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(dialogView);

        setCancelable(cancelAble);
        setCanceledOnTouchOutside(cancelAble);
    }

    public void setOnClickListener(OnClickListener listener)
    {
        onClickListener = new WeakReference<OnClickListener>(listener);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.dialog_button_yes:
                sendYesClickedEvent();
                this.dismiss();
                break;
            case R.id.dialog_button_no:
                sendNoClickedEvent();
                this.dismiss();
                break;
        }
    }

    private void sendYesClickedEvent()
    {
        OnClickListener listener = onClickListener.get();
        if (listener != null)
        {
            listener.onClick(this, DialogInterface.BUTTON_POSITIVE);
        }
    }

    private void sendNoClickedEvent()
    {
        OnClickListener listener = onClickListener.get();
        if (listener != null)
        {
            listener.onClick(this, DialogInterface.BUTTON_NEGATIVE);
        }
    }

    public static CommonQuestionDialogFragment show(Activity parentActivity, String text, CommonQuestionDialogFragment.EventsListener eventsListener)
    {
        return show(parentActivity, text, null, eventsListener);

    }

    public static CommonQuestionDialogFragment show(Activity parentActivity, String text, Serializable data, CommonQuestionDialogFragment.EventsListener eventsListener)
    {
        final CommonQuestionDialogFragment dialog = new CommonQuestionDialogFragment();

        CommonQuestionDialogFragment.eventsListener = eventsListener;

        final Bundle args = new Bundle();

        args.putString(CommonQuestionDialogFragment.TEXT_KEY, text);
        args.putSerializable(CommonQuestionDialogFragment.DATA_KEY, data);

        dialog.setArguments(args);

        dialog.show(parentActivity.getFragmentManager(), CommonQuestionDialogFragment.TAG);

        return dialog;
    }

    @Override
    public void dismiss()
    {
        super.dismiss();
    }
}
