package com.mierzejewski.inzynierka;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.mierzejewski.inzynierka.model.ExpenseIncome;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExpenseAdapter extends ArrayAdapter<ExpenseIncome> {
    private final Context context;
    private ExpenseIncome[] values;



    public ExpenseAdapter(Context context, ExpenseIncome[] values)
    {
        super(context, R.layout.expense_item, values);
        this.context = context;
        this.values = values;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.expense_item, parent, false);

        rowView.setTag(values[position].getExpenseId());

        TextView name = (TextView)rowView.findViewById(R.id.name);
        TextView date = (TextView)rowView.findViewById(R.id.date);
        TextView cash = (TextView)rowView.findViewById(R.id.cash);

        try
        {

            name.setText(values[position].getName());
            final Date positionDate = values[position].getDate();
            DateFormat simpleFormat = new SimpleDateFormat("dd-MM-yyyy");
            String dateText = simpleFormat.format(positionDate);

            date.setText(dateText);
            cash.setText(values[position].getCash().toString());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return rowView;
    }
}