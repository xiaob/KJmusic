package net.kymjs.music;

import net.kymjs.music.service.ScanMusic;
import net.kymjs.music.ui.Main;
import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class AppStart extends FinalActivity {

    @ViewInject(id = R.id.img_start)
    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // 竖屏锁定
        requestWindowFeature(Window.FEATURE_NO_TITLE); // 取消标题
        setContentView(R.layout.aty_start);

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.alpha);
        // 监听动画过程
        animation.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                redirectTo();
//                loadRes();
            }
        });

        mImageView.setAnimation(animation);
    }

    /**
     * 跳转到...
     */
    private void redirectTo() {
        Intent intent = new Intent();
        intent.setClass(this, Main.class);
        startActivity(intent);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
        finish();
    }
    
    private void loadRes(){
        Intent it = new Intent();
        it.setClass(this, ScanMusic.class);
        startService(it);
    }
}
