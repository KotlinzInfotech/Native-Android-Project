package yalantis.com.sidemenu.animation;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class FlipAnimation extends Animation {
    private Camera mCamera;
    private final float mCenterX;
    private final float mCenterY;
    private final float mFromDegrees;
    private final float mToDegrees;

    public FlipAnimation(float f, float f2, float f3, float f4) {
        this.mFromDegrees = f;
        this.mToDegrees = f2;
        this.mCenterX = f3;
        this.mCenterY = f4;
    }

    public void initialize(int i, int i2, int i3, int i4) {
        super.initialize(i, i2, i3, i4);
        this.mCamera = new Camera();
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        final float fromDegrees = mFromDegrees;
        float degrees = fromDegrees + ((mToDegrees - fromDegrees) * interpolatedTime);

        final float centerX = mCenterX;
        final float centerY = mCenterY;
        final Camera camera = mCamera;

        final Matrix matrix = t.getMatrix();

        camera.save();

        camera.rotateY(degrees);

        camera.getMatrix(matrix);
        camera.restore();

        matrix.preTranslate(-centerX, -centerY);
        matrix.postTranslate(centerX, centerY);

    }

}
