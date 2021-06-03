package my.laundryapp.laundryappproviderv0;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import my.laundryapp.laundryappproviderv0.adapter.MyAddonAdapter;
import my.laundryapp.laundryappproviderv0.adapter.MySizeAdapter;
import my.laundryapp.laundryappproviderv0.common.Common;
import my.laundryapp.laundryappproviderv0.model.AddonModel;
import my.laundryapp.laundryappproviderv0.model.EventBus.AddonSizeEditEvent;
import my.laundryapp.laundryappproviderv0.model.EventBus.SelectAddonModel;
import my.laundryapp.laundryappproviderv0.model.EventBus.SelectSizeModel;
import my.laundryapp.laundryappproviderv0.model.EventBus.UpdateAddonModel;
import my.laundryapp.laundryappproviderv0.model.EventBus.UpdateSizeModel;
import my.laundryapp.laundryappproviderv0.model.SizeModel;

public class SizeAddonEditActivity extends AppCompatActivity {

    @BindView(R.id.tool_bar)
    Toolbar toolbar;
    @BindView(R.id.edt_name)
    EditText edt_name;
    @BindView(R.id.edt_price)
    EditText edt_price;
    @BindView(R.id.btn_create)
    Button btn_create;
    @BindView(R.id.btn_edit)
    Button btn_edit;
    @BindView(R.id.recycler_addon_size)
    RecyclerView recycler_addon_size;

    //Variable
    MySizeAdapter adapter;
    MyAddonAdapter addonAdapter;
    private int servicesEditPosition=-1;
    private boolean needSave=false;
    private boolean isAddon=false;

    //Event

    @OnClick(R.id.btn_create)
    void onCreateNew()
    {
        if(!isAddon) //size
        {
            if(adapter !=null)
            {
                SizeModel sizeModel = new SizeModel();
                sizeModel.setName(edt_name.getText().toString());
                sizeModel.setPrice(Long.valueOf(edt_price.getText().toString()));
                adapter.addNewSize(sizeModel);
            }
        }
        else //addon
        {
            if(addonAdapter !=null)
            {
                AddonModel addonModel = new AddonModel();
                addonModel.setName(edt_name.getText().toString());
                addonModel.setPrice(Long.valueOf(edt_price.getText().toString()));
                addonAdapter.addNewSize(addonModel);
            }

        }
    }

    @OnClick(R.id.btn_edit)
    void onEdit()
    {
        if(!isAddon) //size
        {
            if(adapter != null)
            {

                SizeModel sizeModel = new SizeModel();
                sizeModel.setName(edt_name.getText().toString());
                sizeModel.setPrice(Long.valueOf(edt_price.getText().toString()));
                adapter.editSize(sizeModel);

            }
        }
        else //addon
        {
            if(addonAdapter != null)
            {

                AddonModel addonModel = new AddonModel();
                addonModel.setName(edt_name.getText().toString());
                addonModel.setPrice(Long.valueOf(edt_price.getText().toString()));
                addonAdapter.editSize(addonModel);

            }
        }
    }

    //menu


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.addon_size_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.action_save:
                saveData();
                break;
            case android.R.id.home:
            {
                if(needSave)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Cancel?")
                            .setMessage("Do you reallt want to close without saving?")
                            .setNegativeButton("CANCEL", (dialog, which) -> dialog.dismiss())
                            .setPositiveButton("OK", (dialog, which) -> {
                                needSave = false;
                                closeActivity();
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else
                {
                    closeActivity();
                }
            }
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void saveData() {
        if(servicesEditPosition != -1)
        {
            Common.categorySelected.getServices().set(servicesEditPosition,Common.selectedService); //save service to category

            Map<String,Object> updateData = new HashMap<>();
            updateData.put("services",Common.categorySelected.getServices());

            FirebaseDatabase.getInstance()
                    .getReference(Common.CATEGORY_REF)
                    .child(Common.categorySelected.getCatalog_id())
                    .updateChildren(updateData)
                    .addOnFailureListener(e -> Toast.makeText(SizeAddonEditActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show())
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(this, "Reload success!", Toast.LENGTH_SHORT).show();
                            needSave=false;
                            edt_price.setText("0");
                            edt_name.setText("");
                        }

                    });
        }
    }

    private void closeActivity() {

        edt_name.setText("");
        edt_price.setText("0");
        finish();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_size_addon_edit);
        init();
    }

    private void init() {
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        recycler_addon_size.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recycler_addon_size.setLayoutManager(layoutManager);
        recycler_addon_size.addItemDecoration(new DividerItemDecoration(this,layoutManager.getOrientation()));

    }

    //register event


    @Override
    protected void onStart() {
        super.onStart();
        if(!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().removeStickyEvent(UpdateSizeModel.class);
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
        super.onStop();
    }

    //receive event
    @Subscribe(sticky =  true,threadMode = ThreadMode.MAIN)
    public void onAddonSizeReceive(AddonSizeEditEvent event)
    {
        if(!event.isAddon()) //if event is size
        {
            if(Common.selectedService.getSize() != null) //if size is not empty
            {
                adapter = new MySizeAdapter(this,Common.selectedService.getSize());
                servicesEditPosition = event.getPos(); //save service edit to update
                recycler_addon_size.setAdapter(adapter);

                isAddon = event.isAddon();
            }
        }
        else //is addon
        {
            if(Common.selectedService.getAddon() != null) //if addon is not empty
            {
                addonAdapter = new MyAddonAdapter(this,Common.selectedService.getAddon());
                servicesEditPosition = event.getPos(); //save service edit to update
                recycler_addon_size.setAdapter(addonAdapter);

                isAddon = event.isAddon();
            }

        }
    }

    //receive event
    @Subscribe(sticky =  true,threadMode = ThreadMode.MAIN)
    public void onSizeModelUpdate(UpdateSizeModel event)
    {
        if(event.getSizeModelList() != null)
        {
            needSave = true;
            Common.selectedService.setSize(event.getSizeModelList());

        }
    }

    @Subscribe(sticky =  true,threadMode = ThreadMode.MAIN)
    public void onAddonModelUpdate(UpdateAddonModel event)
    {
        if(event.getAddonModels() != null)
        {
            needSave = true;
            Common.selectedService.setAddon(event.getAddonModels());

        }
    }

    //receive event
    @Subscribe(sticky =  true,threadMode = ThreadMode.MAIN)
    public void onSelectSizeModel(SelectSizeModel event)
    {
        if(event.getSizeModel() !=null)
        {
            edt_name.setText(event.getSizeModel().getName());
            edt_price.setText(String.valueOf(event.getSizeModel().getPrice()));

            btn_edit.setEnabled(true);

        }
        else
        {
            btn_edit.setEnabled(false);
        }
    }

    @Subscribe(sticky =  true,threadMode = ThreadMode.MAIN)
    public void onSelectAddonModel(SelectAddonModel event)
    {
        if(event.getAddonModel() !=null)
        {
            edt_name.setText(event.getAddonModel().getName());
            edt_price.setText(String.valueOf(event.getAddonModel().getPrice()));

            btn_edit.setEnabled(true);

        }
        else
        {
            btn_edit.setEnabled(false);
        }
    }

}