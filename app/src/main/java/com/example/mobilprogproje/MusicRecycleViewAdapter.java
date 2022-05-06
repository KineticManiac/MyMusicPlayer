package com.example.mobilprogproje;

import android.content.Context;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public class MusicRecycleViewAdapter extends RecyclerView.Adapter<MusicRecycleViewAdapter.ViewHolder> {
    private final Context ct;
    private final List<Recyclable> musics; //Başta burada sadece MusicInfo vardı
    private final Map<Recyclable, ViewHolder> viewHolders;
    private OnClickListener onClickListener;
    private ButtonPrepareDelegate buttonPrepareDelegate; //Başka şekilde yapamadım
    private OnItemDraggedListener onItemDraggedListener;
    private RecyclerView recyclerView;
    private ItemTouchHelper itemTouchHelper;

    enum BUTTON {
        NONE,
        PLAY,
        DOWNLOAD,
        DELETE
    }

    public MusicRecycleViewAdapter(@NonNull Context ct, @NonNull Recyclable[] musics){
        this.ct = ct;
        this.musics = new ArrayList<>(Arrays.asList(musics));
        viewHolders = new HashMap<>();
    }

    public void insertItem(Recyclable musicInfo){
        insertItem(musics.size(), musicInfo);
    }

    public void insertItem(int position, Recyclable musicInfo){
        musics.add(position, musicInfo);
        notifyItemInserted(position);
    }

    public void removeItem(Recyclable musicInfo){
        ViewHolder holder = viewHolders.get(musicInfo);
        if(holder == null)
            throw new NoSuchElementException();
        removeItem(holder.getAdapterPosition());
    }

    public void removeItem(int position){
        musics.remove(position);
        notifyItemRemoved(position);
    }

    public void changeItem(Recyclable oldItem, Recyclable newItem){
        ViewHolder holder = viewHolders.get(oldItem);
        if(holder == null)
            throw new NoSuchElementException();
        holder.setRecyclable(newItem);

        viewHolders.remove(oldItem);
        viewHolders.put(newItem, holder);
    }

    @NonNull
    @Override
    public MusicRecycleViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(ct);
        View view = inflater.inflate(R.layout.rvitem_music, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicRecycleViewAdapter.ViewHolder holder, int position) {
        Recyclable musicInfo = musics.get(position);
        holder.setRecyclable(musicInfo);
        holder.setLeftButton(buttonPrepareDelegate.getLeftButton(musicInfo));
        holder.setRightButton(buttonPrepareDelegate.getRightButton(musicInfo));
        viewHolders.put(musicInfo, holder);
    }

    @Override
    public int getItemCount() {
        return musics.size();
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void setButtonPrepareDelegate(ButtonPrepareDelegate buttonPrepareDelegate) {
        this.buttonPrepareDelegate = buttonPrepareDelegate;
    }

    public void setItemLeftButton(MusicInfo musicInfo, BUTTON button) throws NoSuchElementException {
        ViewHolder holder = viewHolders.get(musicInfo);
        assert holder != null;
        holder.setLeftButton(button);
    }

    public void setItemRightButton(Recyclable musicInfo, BUTTON button) throws NoSuchElementException{
        ViewHolder holder = viewHolders.get(musicInfo);
        assert holder != null;
        holder.setRightButton(button);
    }

    public void setItemButtons(Recyclable musicInfo, BUTTON left, BUTTON right) throws NoSuchElementException{
        ViewHolder holder = viewHolders.get(musicInfo);
        assert holder != null;
        holder.setLeftButton(left);
        holder.setRightButton(right);
    }

    public void setItemButtonsPerDelegate(Recyclable musicInfo) throws NoSuchElementException{
        setItemButtons(musicInfo,
                buttonPrepareDelegate.getLeftButton(musicInfo),
                buttonPrepareDelegate.getRightButton(musicInfo));
    }

    public void setAllItemButtonsPerDelegate(){
        for (Recyclable musicInfo : musics) {
            setItemButtonsPerDelegate(musicInfo);
        }
    }

    public void makeDraggable(OnItemDraggedListener onItemDraggedListener){
        this.onItemDraggedListener = onItemDraggedListener;
        itemTouchHelper = new ItemTouchHelper(new SimpleCallback());
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView){
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private Recyclable musicInfo;
        private final TextView titleView, authorDurationView;
        private final ImageView iconView, leftButtonImageView, rightButtonImageView;
        private BUTTON leftButtonShape, rightButtonShape;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnDragListener((view, dragEvent) -> {
                if(itemTouchHelper != null && dragEvent.getAction() == DragEvent.ACTION_DRAG_STARTED)
                    itemTouchHelper.startDrag(this);
                return true;
            });

            titleView = itemView.findViewById(R.id.RVMusicTitle);
            authorDurationView = itemView.findViewById(R.id.RVMusicAuthorDuration);
            iconView = itemView.findViewById(R.id.RVMusicIcon);

            leftButtonImageView = itemView.findViewById(R.id.RVMusicButtonLeft);
            leftButtonImageView.setOnClickListener(view -> onClickListener(leftButtonShape));
            setLeftButton(BUTTON.NONE);

            rightButtonImageView = itemView.findViewById(R.id.RVMusicButtonRight);
            rightButtonImageView.setOnClickListener(view -> onClickListener(rightButtonShape));
            setRightButton(BUTTON.NONE);
        }

        public void setRecyclable(Recyclable musicInfo) {
            this.musicInfo = musicInfo;
            titleView.setText(musicInfo.getTitle());
            authorDurationView.setText(ct.getString(R.string.author_duration,
                    musicInfo.getDescription(), musicInfo.getDurationString(ct)));
            iconView.setImageResource(musicInfo.getIconId());
        }

        private void setButton(ImageView view, BUTTON shape){
            if(shape == BUTTON.NONE){
                view.setVisibility(View.INVISIBLE);
            }
            else{
                view.setVisibility(View.VISIBLE);
                switch (shape){
                    case PLAY:
                        view.setImageResource(R.drawable.play);
                        break;
                    case DELETE:
                        view.setImageResource(R.drawable.delete);
                        break;
                    case DOWNLOAD:
                        view.setImageResource(R.drawable.download);
                }
            }
        }

        public void setLeftButton(BUTTON shape){
            leftButtonShape = shape;
            setButton(leftButtonImageView, shape);
        }

        public void setRightButton(BUTTON shape){
            rightButtonShape = shape;
            setButton(rightButtonImageView, shape);
        }

        public Recyclable getMusicInfo() {
            return musicInfo;
        }

        public void onClickListener(BUTTON button){
            if(onClickListener != null)
                onClickListener.onClick(button, musicInfo);
        }
    }

    public class SimpleCallback extends ItemTouchHelper.SimpleCallback{

        public SimpleCallback(){
            super(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            int p1 = viewHolder.getAdapterPosition();
            int p2 = target.getAdapterPosition();

            Recyclable r = musics.get(p1);

            if(onItemDraggedListener.onItemDragged(r, p2)) {
                Collections.swap(musics, p1, p2);
                notifyItemMoved(p1, p2);
                return true;
            }
            else{
                return false;
            }
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) { }
    }

    public interface OnClickListener {
        void onClick(BUTTON button, Recyclable musicInfo);
    }

    public interface ButtonPrepareDelegate {
        BUTTON getLeftButton(Recyclable musicInfo);
        BUTTON getRightButton(Recyclable musicInfo);
    }

    public interface OnItemDraggedListener {
        boolean onItemDragged(Recyclable recyclable, int position);
    }
}
