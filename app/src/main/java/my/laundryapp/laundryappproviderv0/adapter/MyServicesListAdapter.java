package my.laundryapp.laundryappproviderv0.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import my.laundryapp.laundryappproviderv0.R;
import my.laundryapp.laundryappproviderv0.callback.IRecyclerClickListener;
import my.laundryapp.laundryappproviderv0.common.Common;
import my.laundryapp.laundryappproviderv0.model.LaundryServicesModel;

public class MyServicesListAdapter extends RecyclerView.Adapter<MyServicesListAdapter.MyViewHolder> {

    //change
    private FirebaseUser user;
    private DatabaseReference reference;
    private String userCustID;

    //change

    private Context context;
    private List<LaundryServicesModel> foodModelList;


    public MyServicesListAdapter(Context context, List<LaundryServicesModel> foodModelList) {
        this.context = context;
        this.foodModelList = foodModelList;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.layout_services_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(context).load(foodModelList.get(position).getImage()).into(holder.img_services_image);
        holder.txt_services_price.setText(new StringBuilder("RM")
                .append(foodModelList.get(position).getPrice()));
        holder.txt_services_name.setText(new StringBuilder("")
                .append(foodModelList.get(position).getName()));

        //Event
        holder.setListener((view, pos) -> {
            Common.selectedService = foodModelList.get(pos);
            Common.selectedService.setKey(String.valueOf(pos));
        });



    }

    @Override
    public int getItemCount() {
        return foodModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Unbinder unbinder;
        @BindView(R.id.txt_services_name)
        TextView txt_services_name;
        @BindView(R.id.txt_services_price)
        TextView txt_services_price;
        @BindView(R.id.img_services_image)
        ImageView img_services_image;


        IRecyclerClickListener listener;

        public void setListener(IRecyclerClickListener listener) {
            this.listener = listener;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onItemClickListener(itemView,getAdapterPosition());
        }
    }
}

