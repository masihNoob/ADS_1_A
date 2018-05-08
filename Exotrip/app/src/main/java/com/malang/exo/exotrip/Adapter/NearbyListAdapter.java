package com.malang.exo.exotrip.Adapter;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.aakira.expandablelayout.ExpandableLayoutListener;
import com.github.aakira.expandablelayout.ExpandableLayoutListenerAdapter;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;
import com.github.aakira.expandablelayout.Utils;
import com.malang.exo.exotrip.Nearby.Item;
import com.malang.exo.exotrip.R;

import java.util.List;

class ViewHolderWithoutChild extends RecyclerView.ViewHolder
{
    public TextView textView;

    public ViewHolderWithoutChild(View itemView) {
        super(itemView);
        textView = (TextView)itemView.findViewById(R.id.textExpand);

    }
}

class ViewHolderWithChild extends RecyclerView.ViewHolder
{
    public TextView textView, textViewChild;
    public RelativeLayout buttonExpand;
    public ExpandableLinearLayout expandableLinearLayout;

    public ViewHolderWithChild(View itemView) {
        super(itemView);
        textView = (TextView)itemView.findViewById(R.id.textExpand);
        textViewChild = (TextView)itemView.findViewById(R.id.textExpandChild);
        buttonExpand = (RelativeLayout) itemView.findViewById(R.id.buttonExpand);
        expandableLinearLayout = (ExpandableLinearLayout) itemView.findViewById(R.id.expandableLayout);

    }
}
public class NearbyListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    List<Item> items;
    Context context;
    SparseBooleanArray expandState = new SparseBooleanArray();

    public NearbyListAdapter(List<Item> items) {
        this.items = items;
        for (int i=0; i<items.size(); i++)
            expandState.append(i, false);
    }

    @Override
    public int getItemViewType(int position) {
        if(items.get(position).isExpandable())
            return 1;
        else
            return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        if(viewType == 0)
        {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.layout_without_child, parent, false);
            return new ViewHolderWithoutChild(view);
        }else {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.layout_with_child, parent, false);
            return new ViewHolderWithChild(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        switch (holder.getItemViewType())
        {
            case 0:
            {
                ViewHolderWithoutChild viewHolderWithoutChild = (ViewHolderWithoutChild)holder;
                Item item = items.get(position);
                viewHolderWithoutChild.setIsRecyclable(false);
                viewHolderWithoutChild.textView.setText(item.getText());
            }
            break;
            case 1:
            {
                final ViewHolderWithChild viewHolderWithChild = (ViewHolderWithChild)holder;
                Item item = items.get(position);
                viewHolderWithChild.setIsRecyclable(false);
                viewHolderWithChild.textView.setText(item.getText());

                viewHolderWithChild.expandableLinearLayout.setInRecyclerView(true);
                viewHolderWithChild.expandableLinearLayout.setExpanded(expandState.get(position));
                viewHolderWithChild.expandableLinearLayout.setListener(new ExpandableLayoutListenerAdapter() {

                    @Override
                    public void onPreOpen() {
                        changeRotate(viewHolderWithChild.buttonExpand, 0f, 180f).start();
                        expandState.put(position, true);
                    }

                    @Override
                    public void onPreClose() {
                        changeRotate(viewHolderWithChild.buttonExpand, 180f, 0f).start();
                        expandState.put(position, false);
                    }
                });
                viewHolderWithChild.buttonExpand.setRotation(expandState.get(position)?180f:0f);
                viewHolderWithChild.buttonExpand.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        viewHolderWithChild.expandableLinearLayout.toggle();
                    }
                });
                viewHolderWithChild.textViewChild.setText(items.get(position).getSubText());
            }
            break;
            default:
                break;
        }
    }

    private ObjectAnimator changeRotate(RelativeLayout buttonExpand, float from, float to) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(buttonExpand, "rotation", from, to);
        objectAnimator.setDuration(300);
        objectAnimator.setInterpolator(Utils.createInterpolator(Utils.LINEAR_INTERPOLATOR));

        return objectAnimator;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
