package tsukasa.finatic.schooltracking.ViewHolder;

import android.media.Image;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import tsukasa.finatic.schooltracking.Interface.IRecycleItemClickListener;
import tsukasa.finatic.schooltracking.R;

public class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public ImageView imgView;

    public TextView txt_user_email;
    IRecycleItemClickListener iRecyclerItemClickListener;
    View view;

    public void setIRecyclerItemClickListener(IRecycleItemClickListener IRecyclerItemClickListener) {
        this.iRecyclerItemClickListener = IRecyclerItemClickListener;
    }

    public UserViewHolder(@NonNull View itemView) {
        super(itemView);
        imgView = (ImageView) itemView.findViewById(R.id.imgView);
        txt_user_email = (TextView) itemView.findViewById(R.id.txt_user_email);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        iRecyclerItemClickListener.onItemClickListener(view,getAdapterPosition());

    }
}
