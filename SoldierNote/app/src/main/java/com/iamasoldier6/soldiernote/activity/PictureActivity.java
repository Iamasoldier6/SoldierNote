package com.iamasoldier6.soldiernote.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;

import com.iamasoldier6.soldiernote.R;
import com.iamasoldier6.soldiernote.database.PictureItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Iamasoldier6 on 5/30/16.
 */
public class PictureActivity extends AppCompatActivity {

    private ExecutorService mExecutorService;

    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter mRecyclerViewAdapter;

    private boolean mRecyclerViewIsIdle = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerViewAdapter = new RecyclerViewAdapter();
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    mRecyclerViewIsIdle = true;
                    mRecyclerViewAdapter.notifyDataSetChanged();
                } else {
                    mRecyclerViewIsIdle = false;
                }
            }
        });

        mExecutorService = Executors.newFixedThreadPool(10);
        new DownloadImageURL().startDownloadImageURL();
    }

    public class DownloadImageThread implements Runnable {

        private String mUrlString;
        private Handler mMainThreadHandler;
        private ImageView mImageView;

        public DownloadImageThread(String string, Handler handler, ImageView imageView) {
            this.mUrlString = string;
            this.mMainThreadHandler = handler;
            this.mImageView = imageView;
        }

        @Override
        public void run() {
            try {
                HttpURLConnection httpURLConnection = (HttpURLConnection)
                        new URL(mUrlString).openConnection();
                final Bitmap bitmap = BitmapFactory.decodeStream(httpURLConnection.getInputStream());
                mMainThreadHandler.post(new Runnable() {

                    @Override
                    public void run() {
                        if (mImageView.getTag().equals(mUrlString)) {
                            mImageView.setImageBitmap(bitmap);
                        }
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<MyViewHolder> {

        private List<PictureItem> mPictureItems;
        private MyViewHolder mMyViewHolder;

        public void setPictureItems(List<PictureItem> pictureItems) {
            mPictureItems = pictureItems;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            mMyViewHolder = new MyViewHolder(LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.recycler_view_item, viewGroup, false));
            return mMyViewHolder;
        }


        @Override
        public void onBindViewHolder(MyViewHolder viewHolder, int i) {
            viewHolder.mImageView.setImageResource(R.drawable.default_picture);
            if (mRecyclerViewIsIdle) {
                viewHolder.mImageView.setTag(mPictureItems.get(i).getImageUrl());
                //把HandlerThread用线程池替代
                mExecutorService.execute(new DownloadImageThread(
                        mPictureItems.get(i).getImageUrl(), new Handler(), viewHolder.mImageView));
            }
        }

        @Override
        public int getItemCount() {
            return mPictureItems.size();
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView mImageView;

        public MyViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.image_view);
        }
    }

    public class DownloadImageURL {
        public void startDownloadImageURL() {
            new AsyncTask<Void, Void, List<PictureItem>>() {

                @Override
                protected List<PictureItem> doInBackground(Void... params) {
                    try {
                        return parseJson(getJsonString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(List<PictureItem> data) {
                    if (data != null) {
                        mRecyclerViewAdapter.setPictureItems(data);
                        mRecyclerView.setAdapter(mRecyclerViewAdapter);
                    } else {
                        new DownloadImageURL().startDownloadImageURL();
                    }
                }
            }.execute();
        }

        /**
         * 解析 JSON 字符串
         */
        List<PictureItem> parseJson(String jsonString) throws JSONException {
            List<PictureItem> data = new ArrayList<>();
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONObject jsonObject1 = jsonObject.getJSONObject("photos");
            JSONArray jsonArray = jsonObject1.getJSONArray("photo");

            for (int i = 0; i < jsonArray.length(); i++) {
                PictureItem pictureItem = new PictureItem();
                pictureItem.setImageUrl((String) jsonArray.getJSONObject(i).get("url_s"));
                data.add(pictureItem);
            }

            return data;
        }

        /**
         * 获取 JSON 字符串
         */
        public String getJsonString() throws IOException {
            URL url = new URL("https://api.flickr.com/services/rest/?method=flickr.photos.getRecent" +
                    "&api_key=8f47b0dcceabd83f680b17d7826a5ad0&format=json&nojsoncallback=1" +
                    "&extras=url_s");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

            try {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                InputStream inputStream = httpURLConnection.getInputStream();

                if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return null;
                }

                int bytesRead = 0;
                byte[] buffer = new byte[1024];
                while ((bytesRead = inputStream.read(buffer)) > 0) {
                    byteArrayOutputStream.write(buffer, 0, bytesRead);
                }
                byteArrayOutputStream.close();
                return new String(byteArrayOutputStream.toByteArray());
            } finally {
                httpURLConnection.disconnect();
            }
        }
    }
}
