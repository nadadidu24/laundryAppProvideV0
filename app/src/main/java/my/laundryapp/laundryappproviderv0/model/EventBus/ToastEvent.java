package my.laundryapp.laundryappproviderv0.model.EventBus;

public class ToastEvent {
    private boolean isUpdate;
    private boolean isFromFoodList;

    public ToastEvent(boolean isUpdate, boolean isFromFoodList) {
        this.isUpdate = isUpdate;
        this.isFromFoodList = isFromFoodList;
    }

    public boolean isFromFoodList() {
        return isFromFoodList;
    }

    public void setFromFoodList(boolean fromFoodList) {
        isFromFoodList = fromFoodList;
    }

    public boolean isUpdate() {
        return isUpdate;
    }

    public void setUpdate(boolean update) {
        isUpdate = update;
    }
}
