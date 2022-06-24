package yalantis.com.sidemenu.sample.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.cybertechinfosoft.photoslideshowwithmusic.R;


public class ContentFragment extends Fragment {
    public static final String CLOSE = "Close";
    public static final String GAME = "GAME";
    public static final String HOME = "Home";
    public static final String LIKE = "Like";
    public static final String LOVE = "LOVE";
    public static final String MORE = "More";
    public static final String PRIVACY = "Privacy";
    public static final String RATEUS = "Rateus";
    public static final String SHARE = "Share";
    private View containerView;
    protected ImageView mImageView;
    protected int res;

    public static ContentFragment newInstance(int i) {
        ContentFragment contentFragment = new ContentFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Integer.class.getName(), i);
        contentFragment.setArguments(bundle);
        return contentFragment;
    }

    public void onViewCreated(View view, @Nullable Bundle bundle) {
        super.onViewCreated(view, bundle);
        this.containerView = view.findViewById(R.id.container);
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.res = getArguments().getInt(Integer.class.getName());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mImageView = (ImageView) rootView.findViewById(R.id.image_content);
        mImageView.setClickable(true);
        mImageView.setFocusable(true);
        mImageView.setImageResource(res);
        return rootView;
    }
}
