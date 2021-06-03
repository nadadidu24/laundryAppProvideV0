package my.laundryapp.laundryappproviderv0.common;

import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.widget.TextView;

import my.laundryapp.laundryappproviderv0.model.CategoryModel;
import my.laundryapp.laundryappproviderv0.model.LaundryServicesModel;
import my.laundryapp.laundryappproviderv0.model.ServerUserModel;

public class Common {
    public static final String SERVER_REF = "Server";
    public static final String CATEGORY_REF = "Category" ;
    public static ServerUserModel currentServerUser;
    public static CategoryModel categorySelected;

    public static final int DEFAULT_COLUMN_COUNT =0 ;
    public static final int FULL_WIDTH_COLUMN =1 ;
    public static LaundryServicesModel selectedService;

    public static void setSpanString(String welcome, String name, TextView textView, String s) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(welcome);

        SpannableString spannableString = new SpannableString(name);
        StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
        spannableString.setSpan(boldSpan,0,name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.append(spannableString);
        builder.append(s);
        textView.setText(builder,TextView.BufferType.SPANNABLE);

    }

}
