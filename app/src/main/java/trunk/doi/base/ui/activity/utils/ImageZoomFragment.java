package trunk.doi.base.ui.activity.utils;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.RequestFutureTarget;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

import org.reactivestreams.Subscriber;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import trunk.doi.base.R;
import trunk.doi.base.base.BaseApplication;
import trunk.doi.base.base.BaseFragment;
import trunk.doi.base.util.MD5;
import trunk.doi.base.util.ToastUtils;
import trunk.doi.base.util.glideutils.GlideApp;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link ImageZoomFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ImageZoomFragment extends BaseFragment {


    private static final String TAG = "ImageZoomFragment";
    private static final String URL_TAG = "URL_TAG";
    @BindView(R.id.photo_view)
    PhotoView photoView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    private String url;


    public ImageZoomFragment() {
    }


    public static ImageZoomFragment newInstance(String url) {
        ImageZoomFragment fragment = new ImageZoomFragment();
        Bundle args = new Bundle();
        args.putString(URL_TAG, url);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    protected int initLayoutId() {
        return R.layout.fragment_image_zoom;
    }
    @Override
    public void initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) { }
    @Override
    public void setListener(View view, Bundle savedInstanceState) {}
    @Override
    public void initData(Bundle savedInstanceState) {
        if (getArguments() == null && getArguments().getString(URL_TAG)==null) {
            return;
        }
        url = getArguments().getString(URL_TAG);
        GlideApp.with(mContext)
                .load(url).into(photoView);

        photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                getActivity().finish();
               getActivity().overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
            }
        });
    }

    public void saveDrawble(){


    }



}