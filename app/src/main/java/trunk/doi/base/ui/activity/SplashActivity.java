package trunk.doi.base.ui.activity;

import android.animation.Animator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

import butterknife.BindView;
import trunk.doi.base.R;
import trunk.doi.base.base.BaseActivity;
import trunk.doi.base.base.BaseApplication;
import trunk.doi.base.ui.MainActivity;
import trunk.doi.base.util.AppUtils;
import trunk.doi.base.util.Const;
import trunk.doi.base.util.PermissionUtils;
import trunk.doi.base.util.SPUtils;
import trunk.doi.base.util.ToastUtil;

public class SplashActivity extends BaseActivity {




    @BindView(R.id.tv_version)
    TextView tv_version;
    @BindView(R.id.v_animation)
    LottieAnimationView v_animation;

    private static final String ANIM_NAME = "confetti.json";

    @Override
    protected int initLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {

        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mStatusBar.setVisibility(View.GONE);

        String[] pers = {android.Manifest.permission.READ_PHONE_STATE,
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.RECEIVE_SMS};
        PermissionUtils.requestAppPermissions(SplashActivity.this, 1008, pers);


        tv_version.setText("当前版本 v" + AppUtils.getVersionName(BaseApplication.getInstance()));
        String version = SPUtils.loadString(SplashActivity.this, Const.APP_VERSION);
//        if(TextUtils.isEmpty(version) || !version.equals(AppUtils.getVersionName(SplashActivity.this))){
//
//             String[] pers={  android.Manifest.permission.READ_PHONE_STATE,
//                    android.Manifest.permission.CAMERA,
//                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
//                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
//                    android.Manifest.permission.RECEIVE_SMS};
//            PermissionUtils.requestAppPermissions(SplashActivity.this,1008,pers);
//            SPUtils.saveString(SplashActivity.this, Constants.APP_VERSION,AppUtils.getVersionName(BaseApplication.instance));
//
//        }else{

//            if(!PermissionUtils.hasPermissions(SplashActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
//                PermissionUtils.requestPermissions(SplashActivity.this,1001,android.Manifest.permission.READ_EXTERNAL_STORAGE);
//            }else{
//                // queryAndLoadNewPatch不可放在attachBaseContext 中，否则无网络权限，建议放在后面任意时刻，如onCreate中
//                SophixManager.getInstance().queryAndLoadNewPatch();
//            }

//            handler.postDelayed(toMain,1000);

        //  countDown();
        //  }


        v_animation.setAnimation(ANIM_NAME);

    }

    @Override
    protected void onStart() {
        super.onStart();
        v_animation.setProgress(0f);
        v_animation.playAnimation();

    }

    @Override
    protected void onStop() {
        super.onStop();
        v_animation.cancelAnimation();
    }


    private Runnable toMain = new Runnable() {
        @Override
        public void run() {
            SplashActivity.this.startActivity(new Intent(SplashActivity.this, MainActivity.class));
            SplashActivity.this.finish();
            SplashActivity.this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        handler.removeCallbacks(toMain);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1001 && grantResults.length > 0) {
            if (permissions.length > 0 && permissions.length == grantResults.length) {
                for (int i = 0; i < grantResults.length; i++) {
                    //do something
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {

                    } else {
                        ToastUtil.show(SplashActivity.this, "请去设置中开启软件读取文件信息的权限，否则软件不能正常使用");
                    }
                }
            }
        }

    }

    @Override
    protected void setListener() {

        v_animation.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {

                SplashActivity.this.startActivity(new Intent(SplashActivity.this, MainActivity.class));
                SplashActivity.this.finish();
                SplashActivity.this.overridePendingTransition(0, android.R.anim.fade_out);

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

    }

    @Override
    protected void initData() {

    }


}
