package com.countrypicker;

/**
 * Inform the client which country has been selected
 */
public interface CountryPickerListener {
    public void onSelectCountry(String code, String phoneCode, String name);
}
