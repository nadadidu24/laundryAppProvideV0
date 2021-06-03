package my.laundryapp.laundryappproviderv0.callback;

import java.util.List;

import my.laundryapp.laundryappproviderv0.model.CategoryModel;

public interface ICategoryCallbackListener {
    void onCategoryLoadSuccess(List<CategoryModel> categoryModelList);
    void onCategoryLoadFailed(String message);
}
