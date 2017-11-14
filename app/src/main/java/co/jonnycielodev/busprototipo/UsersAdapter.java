package co.jonnycielodev.busprototipo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import co.jonnycielodev.busprototipo.entities.Users;

/**
 * Created by Jonny on 12/10/2017.
 */

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.myViewHolder> {


    private Context mContext;
    private ArrayList<Users> mUsersArrayList;

    public UsersAdapter(Context context, ArrayList<Users> usersArrayList) {
        mContext = context;
        mUsersArrayList = usersArrayList;
    }

    @Override
    public myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_item_row, parent, false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(myViewHolder holder, int position) {
        Users currentUser = mUsersArrayList.get(position);

        if (!currentUser.getPicPath().equals("NO")) {
            Glide.with(mContext).load(currentUser.getPicPath()).into(holder.avatar);
        }

        holder.tvName.setText(currentUser.getNombre());
        holder.tvEmail.setText(currentUser.getEmail());
    }


    @Override
    public int getItemCount() {
        return mUsersArrayList.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder {

        private ImageView avatar;
        private TextView tvName, tvEmail;

        public myViewHolder(View itemView) {
            super(itemView);

            avatar = itemView.findViewById(R.id.userItemRowImage);
            tvName = itemView.findViewById(R.id.userItemRowName);
            tvEmail = itemView.findViewById(R.id.userItemRowEmail);
        }
    }
}
