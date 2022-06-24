package yalantis.com.sidemenu.util;

import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.cybertechinfosoft.photoslideshowwithmusic.R;
import java.util.ArrayList;
import java.util.List;
import yalantis.com.sidemenu.animation.FlipAnimation;
import yalantis.com.sidemenu.interfaces.Resourceble;


public class ViewAnimator<T extends Resourceble> {
    public static final int CIRCULAR_REVEAL_ANIMATION_DURATION = 500;
    private final int ANIMATION_DURATION = 175;
    private ViewAnimatorListener animatorListener;
    private AppCompatActivity appCompatActivity;
    private DrawerLayout drawerLayout;
    private List<T> list;
    private List<View> viewList = new ArrayList();

    public interface ViewAnimatorListener {
        void addViewToContainer(View view);

        void disableHomeButton();

        void enableHomeButton();

        void onSwitch(Resourceble resourceble, int i);
    }

    public ViewAnimator(AppCompatActivity appCompatActivity, List<T> list, DrawerLayout drawerLayout, ViewAnimatorListener viewAnimatorListener) {
        this.appCompatActivity = appCompatActivity;
        this.list = list;
        this.drawerLayout = drawerLayout;
        this.animatorListener = viewAnimatorListener;
    }

    public void showMenuContent() {
        setViewsClickable(false);
        this.viewList.clear();
        double size = (double) this.list.size();
        int i = 0;
        while (true) {
            final double d = (double) i;
            if (d < size) {
                View inflate = this.appCompatActivity.getLayoutInflater().inflate(R.layout.menu_list_item, null);
                final int finalI = i;
                inflate.setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        int[] iArr = new int[]{0, 0};
                        view.getLocationOnScreen(iArr);
                        ViewAnimator.this.switchItem((Resourceble) ViewAnimator.this.list.get(finalI), iArr[1] + (view.getHeight() / 2));
                    }
                });
                ((ImageView) inflate.findViewById(R.id.menu_item_image)).setImageResource(((Resourceble) this.list.get(i)).getImageRes());
                inflate.setVisibility(View.GONE);
                inflate.setEnabled(false);
                this.viewList.add(inflate);
                this.animatorListener.addViewToContainer(inflate);
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        if (d < ((double) ViewAnimator.this.viewList.size())) {
                            ViewAnimator.this.animateView((int) d);
                        }
                        if (d == ((double) (ViewAnimator.this.viewList.size() - 1))) {
                            ViewAnimator.this.setViewsClickable(true);
                        }
                    }
                }, (long) ((d / size) * 525.0d));
                i++;
            } else {
                return;
            }
        }
    }

    private void hideMenuContent() {
        setViewsClickable(false);
        double size = (double) this.list.size();
        for (int size2 = this.list.size(); size2 >= 0; size2--) {
            final double d = (double) size2;
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    if (d < ((double) ViewAnimator.this.viewList.size())) {
                        ViewAnimator.this.animateHideView((int) d);
                    }
                }
            }, (long) ((d / size) * 525.0d));
        }
    }

    private void setViewsClickable(boolean z) {
        this.animatorListener.disableHomeButton();
        for (View enabled : this.viewList) {
            enabled.setEnabled(z);
        }
    }

    private void animateView(int i) {
        final View view = (View) this.viewList.get(i);
        view.setVisibility(View.VISIBLE);
        Animation flipAnimation = new FlipAnimation(90.0f, 0.0f, 0.0f, ((float) view.getHeight()) / 2.0f);
        flipAnimation.setDuration(175);
        flipAnimation.setFillAfter(true);
        flipAnimation.setInterpolator(new AccelerateInterpolator());
        flipAnimation.setAnimationListener(new AnimationListener() {
            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                view.clearAnimation();
            }
        });
        view.startAnimation(flipAnimation);
    }

    private void animateHideView(final int i) {
        final View view = (View) this.viewList.get(i);
        Animation flipAnimation = new FlipAnimation(0.0f, 90.0f, 0.0f, ((float) view.getHeight()) / 2.0f);
        flipAnimation.setDuration(175);
        flipAnimation.setFillAfter(true);
        flipAnimation.setInterpolator(new AccelerateInterpolator());
        flipAnimation.setAnimationListener(new AnimationListener() {
            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                view.clearAnimation();
                view.setVisibility(View.INVISIBLE);
                if (i == ViewAnimator.this.viewList.size() - 1) {
                    ViewAnimator.this.animatorListener.enableHomeButton();
                    ViewAnimator.this.drawerLayout.closeDrawers();
                }
            }
        });
        view.startAnimation(flipAnimation);
    }

    private void switchItem(Resourceble resourceble, int i) {
        this.animatorListener.onSwitch(resourceble, i);
        hideMenuContent();
    }
}
