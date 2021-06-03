package my.laundryapp.laundryappproviderv0.model.EventBus;

import my.laundryapp.laundryappproviderv0.model.AddonModel;

public class SelectAddonModel {

    private AddonModel addonModel;

    public SelectAddonModel(AddonModel addonModel) {
        this.addonModel = addonModel;
    }

    public AddonModel getAddonModel() {
        return addonModel;
    }

    public void setAddonModel(AddonModel addonModel) {
        this.addonModel = addonModel;
    }
}
