package my.laundryapp.laundryappproviderv0.ui.services_list;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import dmax.dialog.SpotsDialog;
import my.laundryapp.laundryappproviderv0.R;
import my.laundryapp.laundryappproviderv0.SizeAddonEditActivity;
import my.laundryapp.laundryappproviderv0.adapter.MyServicesListAdapter;
import my.laundryapp.laundryappproviderv0.common.Common;
import my.laundryapp.laundryappproviderv0.common.MySwipeHelper;
import my.laundryapp.laundryappproviderv0.model.EventBus.AddonSizeEditEvent;
import my.laundryapp.laundryappproviderv0.model.EventBus.ChangeMenuClick;
import my.laundryapp.laundryappproviderv0.model.EventBus.ToastEvent;
import my.laundryapp.laundryappproviderv0.model.LaundryServicesModel;

public class ServicesListFragment extends Fragment {

    //image upload
    private static final int PICK_IMAGE_REQUEST = 1234;
    private ImageView img_services;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private android.app.AlertDialog dialog2;
    private Uri imageUri = null;


    private ServicesListViewModel serviceListViewModel;

    private List<LaundryServicesModel> servicesModelList;

    Unbinder unbinder;
    @BindView(R.id.recycler_services_list)
    RecyclerView recycler_service_list;

    LayoutAnimationController layoutAnimationController;
    MyServicesListAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        serviceListViewModel =
                new ViewModelProvider(this).get(ServicesListViewModel.class);
        View root = inflater.inflate(R.layout.fragment_services_list, container, false);
        unbinder= ButterKnife.bind(this,root);
        initViews();
        serviceListViewModel.getMutableLiveDataFoodList().observe(getViewLifecycleOwner(), laundryServicesModels -> {

            if(laundryServicesModels != null) {
                servicesModelList = laundryServicesModels;
                adapter = new MyServicesListAdapter(getContext(), servicesModelList);
                recycler_service_list.setAdapter(adapter);
                recycler_service_list.setLayoutAnimation(layoutAnimationController);
            }

        });
        return root;
    }

    private void initViews() {

        dialog2 = new SpotsDialog.Builder().setContext(getContext()).setCancelable(false).build();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();



        ((AppCompatActivity)getActivity())
                .getSupportActionBar()
                .setTitle(Common.categorySelected.getName());

        recycler_service_list.setHasFixedSize(true);
        recycler_service_list.setLayoutManager(new LinearLayoutManager(getContext()));

        layoutAnimationController = AnimationUtils.loadLayoutAnimation(getContext(),R.anim.layout_item_from_left);

        //get size
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;

        /*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowMetrics windowMetrics = activity.getWindowManager().getCurrentWindowMetrics();
            Insets insets = windowMetrics.getWindowInsets()
                    .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars());
            return windowMetrics.getBounds().width() - insets.left - insets.right;
        } else {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            return displayMetrics.widthPixels;
        }

         */


        MySwipeHelper mySwipeHelper =new MySwipeHelper(getContext(),recycler_service_list,width/6) {
            @Override
            public void instantiateMyButton(RecyclerView.ViewHolder viewHolder, List<MyButton> buf) {
                buf.add(new MyButton(getContext(),"Delete",30,0, Color.parseColor("#9b0000"),
                        pos ->{

                    if(servicesModelList != null )
                    Common.selectedService = servicesModelList.get(pos);
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setTitle("DELETE")
                                    .setMessage("Do you want to delete this service?")
                                    .setNegativeButton("CANCEl",((dialog, which) -> dialog.dismiss()))
                                    .setPositiveButton("DELETE",((dialog, which) -> {

                                        Common.categorySelected.getServices().remove(pos);
                                        updateServices(Common.categorySelected.getServices(),true);

                                    }));
                            AlertDialog deleteDialog = builder.create();
                            deleteDialog.show();


                        }));

                buf.add(new MyButton(getContext(),"Update",30,0, Color.parseColor("#560027"),
                        pos ->{

                    showUpdateDialog(pos);


                        }));

                buf.add(new MyButton(getContext(),"Size",30,0, Color.parseColor("#12005e"),
                        pos ->{

                            Common.selectedService = servicesModelList.get(pos);
                            startActivity(new Intent(getContext(), SizeAddonEditActivity.class));
                            EventBus.getDefault().postSticky(new AddonSizeEditEvent(false,pos));


                        }));

                buf.add(new MyButton(getContext(),"Addon",30,0, Color.parseColor("#336699"),
                        pos ->{

                            Common.selectedService = servicesModelList.get(pos);
                            startActivity(new Intent(getContext(), SizeAddonEditActivity.class));
                            EventBus.getDefault().postSticky(new AddonSizeEditEvent(true,pos));


                        }));
            }
        };

    }

    private void showUpdateDialog(int pos) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getContext());
        builder.setTitle("Update");
        builder.setMessage("Please fill information");

        View itemView = LayoutInflater.from(getContext()).inflate(R.layout.layout_update_services,null);
        EditText edt_services_name = (EditText) itemView.findViewById(R.id.edt_services_name);
        EditText edt_services_price = (EditText) itemView.findViewById(R.id.edt_services_price);
        EditText edt_services_description = (EditText) itemView.findViewById(R.id.edt_services_description);

        img_services = (ImageView) itemView.findViewById(R.id.img_services_image);


        //set data
        edt_services_name.setText(new StringBuilder("")
        .append(Common.categorySelected.getServices().get(pos).getName()));
        edt_services_price.setText(new StringBuilder("")
                .append(Common.categorySelected.getServices().get(pos).getPrice()));
        edt_services_description.setText(new StringBuilder("")
                .append(Common.categorySelected.getServices().get(pos).getDescription()));

        Glide.with(getContext()).load(Common.categorySelected.getServices().get(pos).getImage()).into(img_services);

        //set event
        img_services.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent,"Select Picture"),PICK_IMAGE_REQUEST);

        });

        builder.setNegativeButton("CANCEL",((dialog1, which) -> dialog1.dismiss()))
                .setPositiveButton("UPDATE",((dialog1, which) -> {



                    LaundryServicesModel updateServices = Common.categorySelected.getServices().get(pos);
                    updateServices.setName(edt_services_name.getText().toString());
                    updateServices.setDescription(edt_services_description.getText().toString());
                    updateServices.setPrice(TextUtils.isEmpty(edt_services_price.getText()) ? 0:
                                    Long.parseLong(edt_services_price.getText().toString()) );

                    if(imageUri != null)
                    {
                        //in this, we will use firebase storage to upload image
                        dialog2.setMessage("Uploading...");
                        dialog2.show();

                        String unique_name = UUID.randomUUID().toString();
                        StorageReference imageFolder = storageReference.child("images/"+unique_name);

                        imageFolder.putFile(imageUri)
                                .addOnFailureListener(e -> {
                                    dialog2.dismiss();
                                    Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }).addOnCompleteListener(task -> {
                            dialog2.dismiss();
                            imageFolder.getDownloadUrl().addOnSuccessListener(uri ->{
                                updateServices.setImage(uri.toString());
                                Common.categorySelected.getServices().set(pos,updateServices);
                                updateServices(Common.categorySelected.getServices(),false);
                            });
                        }).addOnProgressListener(snapshot -> {
                            double progress = (100.0* snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                            dialog2.setMessage(new StringBuilder("Uploading: ").append(progress).append("%"));

                        });
                    }
                    else
                    {
                        Common.categorySelected.getServices().set(pos,updateServices);
                        updateServices(Common.categorySelected.getServices(),false);
                    }

                }));

        builder.setView(itemView);
        AlertDialog updateDialog = builder.create();
        updateDialog.show();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK)
        {
            if(data != null && data.getData() != null)
            {
                imageUri = data.getData();
                img_services.setImageURI(imageUri);
            }
        }
    }

    private void updateServices(List<LaundryServicesModel> services,boolean isDelete) {
        Map<String,Object> updateData = new HashMap<>();
        updateData.put("services",services);

        FirebaseDatabase.getInstance()
                .getReference(Common.CATEGORY_REF)
                .child(Common.categorySelected.getCatalog_id())
                .updateChildren(updateData)
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                })
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful())
                    {
                        serviceListViewModel.getMutableLiveDataFoodList();
                        EventBus.getDefault().postSticky(new ToastEvent(!isDelete,true));

                    }
                });

    }


    @Override
    public void onDestroy() {
        EventBus.getDefault().postSticky(new ChangeMenuClick(true));
        super.onDestroy();
    }
}