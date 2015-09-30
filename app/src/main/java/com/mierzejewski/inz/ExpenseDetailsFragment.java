package com.mierzejewski.inz;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.*;
import android.widget.*;
import com.mierzejewski.inz.common.CommonQuestionDialog;
import com.mierzejewski.inz.common.CommonQuestionDialogFragment;
import com.mierzejewski.inz.model.ExpenseIncome;
import com.mierzejewski.inz.model.ExpenseIncomeType;
import com.mierzejewski.inz.model.MainData;

import java.io.Serializable;
import java.sql.SQLException;

/**
 * Created by dom on 16/11/14.
 */
public class ExpenseDetailsFragment extends com.mierzejewski.inz.AddFragment
{
    public static final String EXPENSE_ID_KEY = "expenseIdkey";

    LinearLayout detailsContainer;
    Switch editingSwitch;
    boolean editingMode = false;
    ExpenseIncome expense;
    private TextWatcher nameEditTextChangedListener = new TextWatcher()
    {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after)
        {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count)
        {
            setDataChanged();
        }

        @Override
        public void afterTextChanged(Editable s)
        {

        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        rootView = inflater.inflate(R.layout.expense_details_fragment, container, false);
        detailsContainer = (LinearLayout) rootView.findViewById(R.id.content);

        long expenseId = getActivity().getIntent().getLongExtra(EXPENSE_ID_KEY,0);
        loadExpense(expenseId);
        updateViews();

        return rootView;
    }

    private void loadExpense(long expenseId)
    {
        MainData dao = MainData.getInstance();
        try
        {
            expense = dao.getExpenseIncomeData().getById(expenseId);
            ammount = expense.getCash();
            category = expense.getCategory();
        } catch (SQLException e)
        {
            e.printStackTrace();
        }

    }

    public void updateViews()
    {
        if(expense!=null)
            loadExpense(expense.getExpenseId());

        if(editingMode)
        {

            LayoutInflater layoutInflater = (LayoutInflater)
                    getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            detailsContainer.removeAllViews();
            detailsContainer.addView(layoutInflater.inflate(R.layout.add_fragment,detailsContainer,false), 0);

            prepareAddingLayout(detailsContainer);
            cashAmmountTextView.setText(ammount.toString());

            nameEditText.setText(expense.getName());
            nameEditText.addTextChangedListener(nameEditTextChangedListener);

            if(category != null)
               categorySpinner.setSelection(((ArrayAdapter<String>) categorySpinner.getAdapter()).getPosition(category.getName()));

            Button saveButton = (Button) detailsContainer.findViewById(R.id.addButton);
            saveButton.setText(R.string.save);
            saveButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {

                    try
                    {
                        save();
                        Toast.makeText(getActivity(),R.string.changes_saved,Toast.LENGTH_SHORT).show();
                        loadExpense(expense.getExpenseId());
                        editingSwitch.setChecked(false);
                    }
                    catch (SQLException e)
                    {
                        Toast.makeText(getActivity(),R.string.changes_saved_failed,Toast.LENGTH_SHORT).show();
                    }

                }
            });


        }
        else
        {
            LayoutInflater layoutInflater = (LayoutInflater)
                    getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            detailsContainer.removeAllViews();
            detailsContainer.addView(layoutInflater.inflate(R.layout.expense_details,detailsContainer,false), 0);

            TextView name = (TextView) detailsContainer.findViewById(R.id.name);
            TextView category = (TextView) detailsContainer.findViewById(R.id.category);
            TextView cash = (TextView) detailsContainer.findViewById(R.id.cash);

            Button deleteButton = (Button) detailsContainer.findViewById(R.id.deleteButton);
            deleteButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    delete();
                }
            });

            name.setText(expense.getName());

            if(expense.getCash()!=null)
                cash.setText(Html.fromHtml("<b>"+getString(R.string.ammount) +":</b> "+ expense.getCash()));

            category.setText(Html.fromHtml("<b>"+getString(R.string.category) +":</b> "+ expense.getCategory()));
        }

    }

    private void save() throws SQLException
    {

        expense.setCash(ammount);
        expense.setCategory(category);
        expense.setName(nameEditText.getText().toString());

        MainData dao = MainData.getInstance();
        dao.getExpenseIncomeData().update(expense);
        isDataChanged = false;

        getActivity().sendBroadcast(new Intent(MainApp.CHANGE_DATA_BROADCAST));

    }

    @Override
    protected boolean isIncome() {
        return expense.getType() == ExpenseIncomeType.INCOME;
    }

    private void delete()
    {

        CommonQuestionDialog.show(getActivity(), getString(R.string.delete_expense_question), new CommonQuestionDialogFragment.EventsListener()
        {

            @Override
            public void onQuestionDialogClick(CommonQuestionDialogFragment.Answer answer, Serializable data)
            {
                if (answer == CommonQuestionDialogFragment.Answer.YES)
                {
                    MainData dao = MainData.getInstance();
                    boolean result = dao.getExpenseIncomeData().deleteExpenseIncome(expense.getExpenseId());
                    if (result)
                    {
                        Toast.makeText(getActivity(), R.string.delete_expense_success, Toast.LENGTH_SHORT).show();
                        getActivity().sendBroadcast(new Intent(MainApp.CHANGE_DATA_BROADCAST));
                        getActivity().finish();
                    } else
                    {
                        Toast.makeText(getActivity(), R.string.delete_expense_failed, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.expense_details_mode,menu);


        MenuItem modeSwitch = menu.findItem(R.id.editionModeSwitch);
        View editingSwitchContainer = (View) modeSwitch.getActionView();
        editingSwitch = (Switch) editingSwitchContainer.findViewById(R.id.switchForActionBar);

        editingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,final boolean isChecked)
            {


                if(isChecked == false && isDataChanged)
                {

                    CommonQuestionDialog.show(getActivity(),getString(R.string.close_editing_mode_discard_changes),new CommonQuestionDialogFragment.EventsListener()
                    {
                        @Override
                        public void onQuestionDialogClick(CommonQuestionDialogFragment.Answer answer, Serializable data)
                        {
                            if(answer == CommonQuestionDialogFragment.Answer.YES)
                            {
                                editingMode = isChecked;
                                isDataChanged = false;
                                updateViews();
                            }
                            else
                            {
                                editingSwitch.setChecked(true);
                                editingMode = true;
                                updateViews();
                            }

                        }
                    });
                }
                else
                {
                    editingMode = isChecked;
                    updateViews();
                }
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }
}
