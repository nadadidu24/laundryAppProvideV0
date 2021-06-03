package my.laundryapp.laundryappproviderv0.model.EventBus;

import java.util.List;

import my.laundryapp.laundryappproviderv0.model.AddonModel;

public class UpdateAddonModel {
    private List<AddonModel> addonModels;

    public UpdateAddonModel () {

    }

    public List<AddonModel> getAddonModels() {
        return addonModels;
    }

    public void setAddonModels(List<AddonModel> addonModels) {
        this.addonModels = addonModels;
    }
}
