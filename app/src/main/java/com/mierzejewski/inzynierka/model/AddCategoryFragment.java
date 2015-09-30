package com.mierzejewski.inzynierka.model;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.github.danielnilsson9.colorpickerview.view.ColorPickerView;

import com.mierzejewski.inzynierka.MainApp;
import com.mierzejewski.inzynierka.R;

import java.sql.SQLException;

/**
 * Created by dom on 08/04/15.
 */
public class AddCategoryFragment extends Fragment implements View.OnClickListener, ColorPickerView.OnColorChangedListener
{
    View rootView;
    EditText nameEditText;
    Button addCategoryButton;
    RadioButton expense,income;
    boolean isIncome = false;
    private ColorPickerView colorPicker;
    private String color;

    //Todo move to oher package
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        rootView = inflater.inflate(R.layout.add_category, container, false);

        nameEditText = (EditText) rootView.findViewById(R.id.nameEditText);
        addCategoryButton = (Button) rootView.findViewById(R.id.addCategoryButton);
        expense = (RadioButton) rootView.findViewById(R.id.expenseRadioButton);
        income = (RadioButton) rootView.findViewById(R.id.incomeRadioButton);
        colorPicker = (ColorPickerView) rootView.findViewById(R.id.colorpickerview);
        colorPicker.setOnColorChangedListener(this);
        addCategoryButton.setOnClickListener(this);
        expense.setOnClickListener(this);
        income.setOnClickListener(this);
    
        return rootView;
    }

    @Override
    public void onClick(View v)
    {
        if(v.getId() == R.id.addCategoryButton)
        {
            try
            {
                String name = nameEditText.getText().toString();
                Category newCategory = new Category(name,color,null,isIncome);
                MainData.getInstance().getCategoryData().storeCategory(newCategory);
                Toast.makeText(getActivity(),getString(R.string.add_success),Toast.LENGTH_SHORT).show();
                getActivity().sendBroadcast(new Intent(MainApp.CHANGE_DATA_BROADCAST));

                getActivity().finish();

            }
            catch (SQLException e)
            {
                Toast.makeText(getActivity(),getString(R.string.cannot_add_categort),Toast.LENGTH_SHORT).show();
            }


        }
        else if(v.getId() == R.id.expenseRadioButton)
        {
            isIncome = false;
        }
        else if(v.getId() == R.id.incomeRadioButton)
        {
            isIncome = true;
        }

    }

    @Override
    public void onColorChanged(int i) {
        color = String.format("#%06X", 0xFFFFFF & i);

    }
}