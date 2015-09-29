package pl.marcinmierzejewski.materialbudget;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import pl.marcinmierzejewski.materialbudget.common.CommonFragment;
import pl.marcinmierzejewski.materialbudget.model.*;

import java.util.Date;
import java.util.List;

/**
 * Created by dom on 05/11/14.
 */
public class AddFragment extends CommonFragment implements DiffrentAmmountDialog.EventListener
{
    public static final String TYPE_OF_ADD_KEY = "typeOfAdd";
    public static final String INCOME = "income";
    public static final String EXPENSE = "expense";

    boolean isIncome;
    protected boolean isDataChanged = false;

    View rootView;
    Spinner categorySpinner;
    Spinner currencySpinner;
    SeekBar cashAmmountSeekBar;
    TextView cashAmmountTextView;
    TextView diffrentAmount;
    EditText nameEditText;
    Button addButton;


    CashAmmount ammount = new CashAmmount(0,Currency.getDefault());

    Category category = null;
    private List<Category> categories;

    private View.OnClickListener onDiffrentAmmountClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            DialogFragment dialog = new DiffrentAmmountDialog();
            dialog.show(getFragmentManager(),"DialogFragment");
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        if(getActivity().getIntent().getExtras() != null)
        {
            String type = getActivity().getIntent().getExtras().getString(TYPE_OF_ADD_KEY);
            if (type.equals(INCOME))
            {
                isIncome = true;
            }
            else
            {
                isIncome = false;
            }
        }


        //getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        //getActivity().getActionBar().setIcon(R.drawable.ic_home);
        getActivity().invalidateOptionsMenu();
        setHasOptionsMenu(true);

        rootView = inflater.inflate(R.layout.add_fragment,container,false);

        prepareAddingLayout(rootView);

        return rootView;
    }


    protected void prepareAddingLayout(View rootView)
    {
        cashAmmountTextView = (TextView) rootView.findViewById(R.id.cashAmmountTextView);
        nameEditText = (EditText) rootView.findViewById(R.id.nameText);
        addButton = (Button) rootView.findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                addExpense();

            }
        });
        diffrentAmount = (TextView) rootView.findViewById(R.id.diffrent_ammount);
        diffrentAmount.setOnClickListener(onDiffrentAmmountClickListener);
        prepareCategoriesList();
        prepareCurrenciesList();
        prepareCashAmmountSeekBar();

        updateViews();
    }

    private void prepareCategoriesList()
    {

        try
        {
            if(isIncome)
            {
                categories = MainData.getInstance().getCategoryData().getIncomeCategories();
            }
            else
            {
                categories = MainData.getInstance().getCategoryData().getExpenseCategories();
            }

            final String categoriesArray[] = new String[categories.size()];

            int i = 0;
            for(Category single: categories)
            {
                categoriesArray[i++] = single.getName();
            }

            categorySpinner = (Spinner) rootView.findViewById(R.id.categorySpinner);
            ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(getActivity(),
            android.R.layout.simple_spinner_dropdown_item,categoriesArray);
            categorySpinner.setAdapter(spinnerArrayAdapter);
            categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
            {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                {
                    if(category==null || !category.equals(categories.get(position)))
                        setDataChanged();

                    category = categories.get(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent)
                {

                }
          });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    protected void setDataChanged()
    {
        isDataChanged = true;
    }

    private void prepareCurrenciesList()
    {

        currencySpinner = (Spinner) rootView.findViewById(R.id.currencySpinner);
        ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, Currency.values());
        currencySpinner.setAdapter(spinnerArrayAdapter);
        currencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                ammount.setCurrency(Currency.values()[position]);
                updateViews();

                if(!(Currency.values()[position]).equals(ammount.getCurrency()))
                    setDataChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

    }

    protected void prepareCashAmmountSeekBar()
    {
        cashAmmountSeekBar = (SeekBar) rootView.findViewById(R.id.cashAmmountSeekBar);
        if(cashAmmountSeekBar == null)
        {
            ((DetailActivity) getActivity()).onBackPressed();
            return;
        }
        cashAmmountSeekBar.setProgress(penniesToProgress(ammount.getPennies()));
        cashAmmountSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {

                int pennies = progressToPennies(progress);

                ammount.setPennies(pennies);
                updateViews();
                setDataChanged();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {

            }
        });

    }

    private int penniesToProgress(int pennies)
    {
        if(pennies < 200 * 100)
            return pennies/100;
        else
        {
            //pen = (500*prog-(160*500)
            //pen/500 = prog-160
            //prog = (pen/500) + 160

            return 160+((pennies/500));
        }
    }

    protected int progressToPennies(int progress)
    {

        int pennies = progress * 100;


        if(progress>200)
        {
            //(200-x)*5 = 200
            //1000-5x = 200
            //800 = 5x
            //x = 160
            pennies = (progress-160) * 500;

        }

        return pennies;
    }


    private void updateViews()
    {
        cashAmmountTextView.setText(ammount.toString());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() == android.R.id.home)
            getActivity().onBackPressed();

        return super.onOptionsItemSelected(item);
    }

    public void addExpense()
    {
        if(ammount.isZero())
        {
            Toast.makeText(getActivity(), R.string.cash_ammount_is_zero, Toast.LENGTH_SHORT).show();
            return;
        }

        ExpenseIncome newExpense = new ExpenseIncome();
        newExpense.setDate(new Date());

        newExpense.setCash(ammount);
        if(isIncome)
            newExpense.setType(ExpenseIncomeType.INCOME);
        else
            newExpense.setType(ExpenseIncomeType.EXPENSE);

        newExpense.setCategory(category);
        newExpense.setName(nameEditText.getText().toString());

        try
        {
            MainData dao = MainData.getInstance();
            dao.getExpenseIncomeData().store(newExpense);
            Toast.makeText(getActivity().getApplication(), R.string.add_success,Toast.LENGTH_LONG).show();

            getActivity().sendBroadcast(new Intent(MainApp.CHANGE_DATA_BROADCAST));
            getActivity().finish();

        }
        catch (Exception e)
        {
            Toast.makeText(getActivity(),e.toString(),Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onChangeAmmount(double newVal)
    {
        ammount.setPennies((int)(newVal*100l));
        updateViews();
    }
}