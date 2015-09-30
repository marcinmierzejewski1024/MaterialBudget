package com.mierzejewski.inz.common;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

import java.io.Serializable;

public class CommonQuestionDialogFragment
	extends
		DialogFragment
	implements
		OnClickListener
{
	public enum Answer
	{
		YES,
		NO,
        DISMISS
	}
	
	public static final String TAG = "COMMON_QUESTION_DIALOG_FRAGMENT";
	
	public static final String TEXT_KEY = "COMMON_QUESTION_DIALOG_TEXT";
	public static final String DATA_KEY = "COMMON_QUESTION_DIALOG_DATA";

	public interface EventsListener
	{
        public void onQuestionDialogClick(Answer answer, Serializable data);
    }

	public static EventsListener eventsListener;

	private String text;
	private Serializable data;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	
	}

	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		Bundle args = getArguments();
		
		text = args.getString(TEXT_KEY);
		data = args.getSerializable(DATA_KEY);
		
		final CommonQuestionDialog dialog = new CommonQuestionDialog(getActivity(), text,true);
		
		dialog.setOnClickListener(this);

		return dialog;
    }
	
	@Override
	public void onClick(DialogInterface dialog, int which)
	{
		switch (which)
		{
			case DialogInterface.BUTTON_POSITIVE:
				eventsListener.onQuestionDialogClick(Answer.YES, data);
				break;

			case DialogInterface.BUTTON_NEGATIVE:
				eventsListener.onQuestionDialogClick(Answer.NO, data);
				break;
		}
	}

    @Override
    public void onCancel(DialogInterface dialog)
    {
        eventsListener.onQuestionDialogClick(Answer.DISMISS, data);
        super.onCancel(dialog);
    }
}
