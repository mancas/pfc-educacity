package com.mancas.adapters;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.mancas.educacity.R;
import com.mancas.utils.Utils;

/**
 * Adapter to load images into a grid view using an asynchronous task
 * @author Manuel Casas Barrado
 * @version 1.0
 */
public class GridViewImageAdapter extends BaseAdapter
{
    /**
     * Activity host
     */
    private Activity mActivity;
    /**
     * List with the paths of the images shown in the grid view
     */
    private ArrayList<String> mPaths = new ArrayList<String>();
    /**
     * Width of each column displayed in the grid view
     */
    private int mImgWidth;
    /**
     * Application resources
     */
    private Resources mResources;

    //private SparseBooleanArray mSelectedIds;

    public GridViewImageAdapter(Activity activity, ArrayList<String> paths, int imageWidth)
    {
        mActivity = activity;
        mPaths = paths;
        mImgWidth = imageWidth;
        mResources = activity.getResources();
        //mSelectedIds = new SparseBooleanArray();
    }

    @Override
    public int getCount() {
        return mPaths.size();
    }

    @Override
    public Object getItem(int position) {
        return mPaths.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /** 
     * ViewHolder used to cache the view in order to save processor cycles
     **/
    static class ImageViewHolder {
        ImageView imageView;
        int position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageViewHolder viewHolder = new ImageViewHolder();

        if (convertView == null) {
            convertView = new ImageView(mActivity);
            viewHolder.imageView = (ImageView) convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ImageViewHolder) convertView.getTag();
        }
        viewHolder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        viewHolder.imageView.setLayoutParams(new GridView.LayoutParams(mImgWidth, mImgWidth));
        viewHolder.imageView.setOnClickListener(new OnImageClickListener(position));
        viewHolder.imageView.setClickable(true);
        loadBitmap(mPaths.get(position), viewHolder.imageView);

        return convertView;
    }

    class OnImageClickListener implements OnClickListener
    {
        private int mPosition;

        public OnImageClickListener(int position)
        {
            mPosition = position;
        }

        @Override
        public void onClick(View v) {
            /*Intent fullScreen = new Intent(mActivity, FullScreenViewActivity.class);
            fullScreen.putExtra("position", mPosition);
            fullScreen.putStringArrayListExtra("images", mPaths);
            mActivity.startActivity(fullScreen);*/
            // Use gallery to improve performance
            File f = new File(mPaths.get(mPosition));
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(f), "image/*");
            mActivity.startActivity(intent);
        }
    }

    /**
     * Load asynchronous task
     * @param path the path of the image
     * @param imageView the view where must be place the image
     */
    public void loadBitmap(String path, ImageView imageView) {
        if (cancelPotentialWork(path, imageView)) {
            final BitmapWorkerTask task = new BitmapWorkerTask(imageView);
            final AsyncDrawable asyncDrawable =
                    new AsyncDrawable(mResources, BitmapFactory.decodeResource(mResources,
                            R.drawable.empty_photo), task);
            imageView.setImageDrawable(asyncDrawable);
            task.execute(path);
        }
    }

    /**
     * Dedicated Drawable subclass to store a reference back to the worker task.
     * In this case, a BitmapDrawable is used so that a placeholder image can be displayed
     * in the ImageView while the task completes
     * @author Manuel Casas Barrado
     * @version 1.0
     */
    static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

        public AsyncDrawable(Resources res, Bitmap bitmap,
                BitmapWorkerTask bitmapWorkerTask) {
            super(res, bitmap);
            bitmapWorkerTaskReference =
                new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
        }

        public BitmapWorkerTask getBitmapWorkerTask() {
            return bitmapWorkerTaskReference.get();
        }
    }

    /**
     * Cancel the previous request if needed
     * @param path the path of the image
     * @param imageView the image view where must be place the image
     * @return true if there is no associate task to the image view
     */
    public static boolean cancelPotentialWork(String path, ImageView imageView) {
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) {
            final String pathData = bitmapWorkerTask.path;
            if (pathData != path) {
                // Cancel previous task
                bitmapWorkerTask.cancel(true);
            } else {
                // The same work is already in progress
                return false;
            }
        }
        // No task associated with the ImageView, or an existing task was cancelled
        return true;
    }

    /**
     * Gets the task associated to the image view argument
     * @param imageView the image view where the image will be placed after being loaded
     * @return the asynchronous task associated to the image view
     */
    private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
       if (imageView != null) {
           final Drawable drawable = imageView.getDrawable();
           if (drawable instanceof AsyncDrawable) {
               final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
               return asyncDrawable.getBitmapWorkerTask();
           }
        }
        return null;
    }

    /**
     * Asynchronous class that creates a bitmap from an image
     * @author Manuel Casas Barrado
     * @version 1.0
     */
    class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap>
    {
        public String path;
        public ImageView imageViewReference;

        public BitmapWorkerTask(ImageView imageView)
        {
            imageViewReference = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            path = params[0];
            Bitmap image = Utils.decodeFile(path, mImgWidth, mImgWidth);
            return image;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }

            if (imageViewReference != null && bitmap != null) {
                final ImageView imageView = imageViewReference;
                final BitmapWorkerTask bitmapWorkerTask =
                        getBitmapWorkerTask(imageView);
                if (this == bitmapWorkerTask && imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }

    /* MULTICHOICE */
    /*public void toggleSelection(int position)
    {
        selectView(position, !mSelectedIds.get(position));
    }

    private void selectView(int position, boolean value) {
        if (value) {
            mSelectedIds.put(position, value);
        } else {
            mSelectedIds.delete(position);
        }
        notifyDataSetChanged();
    }

    public void removeSelection()
    {
        mSelectedIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    public SparseBooleanArray getSelectedIds()
    {
        return mSelectedIds;
    }*/
}
