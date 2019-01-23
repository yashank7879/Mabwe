package mabwe.com.mabwe.utils;

import android.content.Context;
import android.widget.Toast;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import mabwe.com.mabwe.R;

/**
 * Created by ravi on 27/6/18.
 */

public class Validation {

    private static final String EMAIL_PATTERN = "";
    private static final String USER_NAME_PATTERN = "";
    private static final String PASSWORD_PATTERN = "^[a-zA-Z@#$%]\\w{5,19}$";
    public static final String emailPattern = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";

    private Context context;

    public Validation(Context context) {
        this.context = context;
    }


    /*.................................isValidName......................................*/
    public boolean isValidName(String input) {
        if (input.equals("") || input.isEmpty() ||input.length()< 2||input.length()>20) {
            return false;
        }
        return true;
    }

    /*public boolean isValidPassword(String string, boolean allowSpecialChars) {
        String PASSWORD_PATTERN;
        if (allowSpecialChars) {
            PASSWORD_PATTERN = "^[a-zA-Z@#$%]\\w{5,19}$";
        } else {
            PASSWORD_PATTERN = "^[a-zA-Z]\\w{5,19}$";
        }

        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        Matcher matcher = pattern.matcher(string);
        return matcher.matches();
    }*/

    /*.................................isValidPassword......................................*/
    public boolean isValidPassword(String input) {
        if (input.equals("") || input.isEmpty() || input.length()< 6||input.length()>12) {
            return false;
        }
        return true;
    }

    /*.................................isEmpty......................................*/
    public boolean isEmpty(String textView) {
        if (textView.equals("") || textView.length() == 0) {
            return false;
        }
        return true;
    }

    public boolean isvalidMobileno(String textView) {
        if (textView.length()<10) {
            return true;
        }
        return false;
    }


    /*.................................isValidEmail......................................*/
    public boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }



    /*.................................phonelibeary......................................*/

    public boolean phonelibeary(String text) {
        if (text != null) {
            PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
            Phonenumber.PhoneNumber phNumberProto = null;
            try {
                phNumberProto = phoneUtil.parse(text, String.valueOf(R.string.india)); //replace Iso code here
            } catch (NumberParseException e) {
                System.err.println("NumberParseException was thrown: " + e.toString());
            }
            boolean isValid = phoneUtil.isValidNumber(phNumberProto);
            if (isValid) {
                assert phNumberProto != null;
                String internationalFormat = phoneUtil.format(phNumberProto, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
                Toast.makeText(context, String.valueOf(R.string.phone_no_valid) + internationalFormat, Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        return false;
    }

}
