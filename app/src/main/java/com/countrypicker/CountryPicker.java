package com.countrypicker;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;

public class CountryPicker extends DialogFragment implements Comparator<Country> {

    public static final String ATTR_COUNTRIES = "countries";
    public static final String ATTR_CODE = "code";
    public static final String ATTR_PHONE_CODE = "phoneCode";
    public static final String ATTR_NAME = "name";

    /**
     * View components
     */
    private EditText searchEditText;
    private ListView countryListView;

    /**
     * Adapter for the listview
     */
    private CountryListAdapter adapter;

    /**
     * Hold all countries, sorted by country name
     */
    private List<Country> allCountriesList;

    /**
     * Hold countries that matched user query
     */
    private List<Country> selectedCountriesList;

    /**
     * Listener to which country user selected
     */
    private CountryPickerListener listener;

    /**
     * Set listener
     *
     * @param listener
     */
    public void setListener(CountryPickerListener listener) {
        this.listener = listener;
    }

    public EditText getSearchEditText() {
        return searchEditText;
    }

    public ListView getCountryListView() {
        return countryListView;
    }

    /**
     * Get all countries with code, prefix and name from res/raw/countries.json
     *
     * @return
     */
    private List<Country> getAllCountries() {
        if (allCountriesList == null) {
            try {
                allCountriesList = new ArrayList<Country>();

                // Read from local file
                String allCountriesString = readFileAsString(getActivity());
                Log.d("countrypicker", "country: " + allCountriesString);


                JSONObject jsonObject = new JSONObject(allCountriesString);


                if (jsonObject.has(ATTR_COUNTRIES)) {
                    JSONArray countries = jsonObject.getJSONArray(ATTR_COUNTRIES);

                    if (countries != null) {
                        for (int i = 0; i < countries.length(); i++) {
                            JSONObject object = countries.optJSONObject(i);

                            Country country = new Country();

                            if (object != null) {
                                country.setCode(object.getString(ATTR_CODE));
                                country.setPhoneCode(object.getString(ATTR_PHONE_CODE));
                                country.setName(object.getString(ATTR_NAME));
                            }

                            allCountriesList.add(country);
                        }
                    }
                }

                // Initialize selected countries with all countries
                selectedCountriesList = new ArrayList<Country>();
                selectedCountriesList.addAll(allCountriesList);

                // Return
                return allCountriesList;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * R.string.countries is a json string which is Base64 encoded to avoid
     * special characters in XML. It's Base64 decoded here to get original json.
     *
     * @param context
     * @return
     * @throws java.io.IOException
     */

    private static String readFileAsString(Context context)
            throws java.io.IOException {
        String base64 = context.getResources().getString(R.string.countries);
        byte[] data = Base64.decode(base64, Base64.DEFAULT);
        return new String(data, "UTF-8");
    }

    /**
     * To support show as dialog
     *
     * @param dialogTitle
     * @return
     */
    public static CountryPicker newInstance(String dialogTitle) {
        CountryPicker picker = new CountryPicker();
        Bundle bundle = new Bundle();
        bundle.putString("dialogTitle", dialogTitle);
        picker.setArguments(bundle);
        return picker;
    }

    /**
     * Create view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate view
        View view = inflater.inflate(R.layout.country_picker, null);

        // Get countries from the json
        getAllCountries();

        // Set dialog title if show as dialog
        Bundle args = getArguments();
        if (args != null) {
            String dialogTitle = args.getString("dialogTitle");
            getDialog().setTitle(dialogTitle);

            int width = getResources().getDimensionPixelSize(
                    R.dimen.cp_dialog_width);
            int height = getResources().getDimensionPixelSize(
                    R.dimen.cp_dialog_height);
            getDialog().getWindow().setLayout(width, height);
        }

        // Get view components
        searchEditText = (EditText) view.findViewById(R.id.country_picker_search);
        countryListView = (ListView) view.findViewById(R.id.country_picker_listview);

        // Set adapter
        adapter = new CountryListAdapter(getActivity(), selectedCountriesList);
        countryListView.setAdapter(adapter);

        // Inform listener
        countryListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (listener != null) {
                    Country country = selectedCountriesList.get(position);

                    listener.onSelectCountry(
                            country.getCode(),
                            country.getPhoneCode(),
                            country.getName()
                    );
                }
            }
        });

        // Search for which countries matched user query
        searchEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                search(s.toString());
            }
        });

        return view;
    }

    /**
     * Search allCountriesList contains text and put result into
     * selectedCountriesList
     *
     * @param text
     */
    @SuppressLint("DefaultLocale")
    private void search(String text) {
        selectedCountriesList.clear();

        for (Country country : allCountriesList) {
            if (country.getName().toLowerCase(Locale.ENGLISH)
                    .contains(text.toLowerCase())) {
                selectedCountriesList.add(country);
            }
        }

        adapter.notifyDataSetChanged();
    }

    /**
     * Support sorting the countries list
     */
    @Override
    public int compare(Country lhs, Country rhs) {
        return lhs.getName().compareTo(rhs.getName());
    }

}
