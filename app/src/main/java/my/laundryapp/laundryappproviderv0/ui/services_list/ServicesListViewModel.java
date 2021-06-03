package my.laundryapp.laundryappproviderv0.ui.services_list;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import my.laundryapp.laundryappproviderv0.common.Common;
import my.laundryapp.laundryappproviderv0.model.LaundryServicesModel;

public class ServicesListViewModel extends ViewModel {

    private MutableLiveData<List<LaundryServicesModel>> mutableLiveDataFoodList;

    public ServicesListViewModel() {

    }

    public MutableLiveData<List<LaundryServicesModel>> getMutableLiveDataFoodList() {
        if(mutableLiveDataFoodList == null)
            mutableLiveDataFoodList = new MutableLiveData<>();
        mutableLiveDataFoodList.setValue(Common.categorySelected.getServices());
        return mutableLiveDataFoodList;
    }
}