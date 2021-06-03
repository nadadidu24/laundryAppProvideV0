package my.laundryapp.laundryappproviderv0.model.EventBus;

public class ChangeMenuClick {

    private boolean isFromServicesList;

    public ChangeMenuClick(boolean isFromServicesList) {
        this.isFromServicesList = isFromServicesList;
    }


    public boolean isFromServicesList() {
        return isFromServicesList;
    }

    public void setFromServicesList(boolean fromServicesList) {
        isFromServicesList = fromServicesList;
    }
}
