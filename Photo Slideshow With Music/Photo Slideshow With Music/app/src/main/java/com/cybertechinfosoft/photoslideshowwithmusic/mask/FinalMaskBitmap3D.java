package com.cybertechinfosoft.photoslideshowwithmusic.mask;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;


import com.cybertechinfosoft.photoslideshowwithmusic.MyApplication;

import java.lang.reflect.Array;
import java.util.Random;

public class FinalMaskBitmap3D {
    public static float ANIMATED_FRAME = 0.0f;
    public static int ANIMATED_FRAME_CAL = 0;
    static final int HORIZONTAL = 0;
    public static int ORIGANAL_FRAME = 0;
    public static int TOTAL_FRAME = 0;
    static final int VERTICALE = 1;
    private static int averageHeight;
    private static int averageWidth;
    private static float axisX;
    private static float axisY;
    private static Bitmap[][] bitmaps;
    private static Camera camera;
    public static int direction;
    private static Matrix matrix;
    static final Paint paint;
    private static int partNumber;
    static int[][] randRect;
    static Random random;
    public static EFFECT rollMode;
    private static float rotateDegree;
    private static float f;
    public enum EFFECT {
        Roll2D_TB("Roll2D_TB") {
            public Bitmap getMask(Bitmap bottom, Bitmap top, int factor) {
                FinalMaskBitmap3D.setRotateDegree(factor);
                Bitmap mask = Bitmap.createBitmap(MyApplication.VIDEO_WIDTH, MyApplication.VIDEO_HEIGHT, Config.ARGB_8888);
                FinalMaskBitmap3D.drawRollWhole3D(bottom, top, new Canvas(mask), true);
                return mask;
            }

            public void initBitmaps(Bitmap bottom, Bitmap top) {
                FinalMaskBitmap3D.partNumber = 8;
                FinalMaskBitmap3D.direction = FinalMaskBitmap3D.VERTICALE;
                FinalMaskBitmap3D.rollMode = this;
                FinalMaskBitmap3D.camera = new Camera();
                FinalMaskBitmap3D.matrix = new Matrix();
            }
        },
        Roll2D_BT("Roll2D_BT") {
            public Bitmap getMask(Bitmap bottom, Bitmap top, int factor) {
                FinalMaskBitmap3D.setRotateDegree(FinalMaskBitmap3D.ANIMATED_FRAME_CAL - factor);
                Bitmap mask = Bitmap.createBitmap(MyApplication.VIDEO_WIDTH, MyApplication.VIDEO_HEIGHT, Config.ARGB_8888);
                FinalMaskBitmap3D.drawRollWhole3D(top, bottom, new Canvas(mask), true);
                return mask;
            }

            public void initBitmaps(Bitmap bottom, Bitmap top) {
                FinalMaskBitmap3D.partNumber = 8;
                FinalMaskBitmap3D.direction = FinalMaskBitmap3D.VERTICALE;
                FinalMaskBitmap3D.rollMode = this;
                FinalMaskBitmap3D.camera = new Camera();
                FinalMaskBitmap3D.matrix = new Matrix();
            }
        },
        Roll2D_LR("Roll2D_LR") {
            public Bitmap getMask(Bitmap bottom, Bitmap top, int factor) {
                FinalMaskBitmap3D.setRotateDegree(factor);
                Bitmap mask = Bitmap.createBitmap(MyApplication.VIDEO_WIDTH, MyApplication.VIDEO_HEIGHT, Config.ARGB_8888);
                FinalMaskBitmap3D.drawRollWhole3D(bottom, top, new Canvas(mask), true);
                return mask;
            }

            public void initBitmaps(Bitmap bottom, Bitmap top) {
                FinalMaskBitmap3D.partNumber = 8;
                FinalMaskBitmap3D.direction = FinalMaskBitmap3D.HORIZONTAL;
                FinalMaskBitmap3D.rollMode = this;
                FinalMaskBitmap3D.camera = new Camera();
                FinalMaskBitmap3D.matrix = new Matrix();
            }
        },
        Roll2D_RL("Roll2D_RL") {
            public Bitmap getMask(Bitmap bottom, Bitmap top, int factor) {
                FinalMaskBitmap3D.setRotateDegree(FinalMaskBitmap3D.ANIMATED_FRAME_CAL - factor);
                Bitmap mask = Bitmap.createBitmap(MyApplication.VIDEO_WIDTH, MyApplication.VIDEO_HEIGHT, Config.ARGB_8888);
                FinalMaskBitmap3D.drawRollWhole3D(top, bottom, new Canvas(mask), true);
                return mask;
            }

            public void initBitmaps(Bitmap bottom, Bitmap top) {
                FinalMaskBitmap3D.partNumber = 8;
                FinalMaskBitmap3D.direction = FinalMaskBitmap3D.HORIZONTAL;
                FinalMaskBitmap3D.rollMode = this;
                FinalMaskBitmap3D.camera = new Camera();
                FinalMaskBitmap3D.matrix = new Matrix();
            }
        },
        Whole3D_TB("Whole3D_TB") {
            public Bitmap getMask(Bitmap bottom, Bitmap top, int factor) {
                FinalMaskBitmap3D.setRotateDegree(factor);
                Bitmap mask = Bitmap.createBitmap(MyApplication.VIDEO_WIDTH, MyApplication.VIDEO_HEIGHT, Config.ARGB_8888);
                Canvas canvas = new Canvas(mask);
                FinalMaskBitmap3D.rollMode = this;
                FinalMaskBitmap3D.drawRollWhole3D(bottom, top, canvas, false);
                return mask;
            }

            public void initBitmaps(Bitmap bottom, Bitmap top) {
                FinalMaskBitmap3D.partNumber = 8;
                FinalMaskBitmap3D.direction = FinalMaskBitmap3D.VERTICALE;
                FinalMaskBitmap3D.camera = new Camera();
                FinalMaskBitmap3D.matrix = new Matrix();
                FinalMaskBitmap3D.rollMode = this;
            }
        },
        Whole3D_BT("Whole3D_BT") {
            public Bitmap getMask(Bitmap bottom, Bitmap top, int factor) {
                FinalMaskBitmap3D.setRotateDegree(FinalMaskBitmap3D.ANIMATED_FRAME_CAL - factor);
                Bitmap mask = Bitmap.createBitmap(MyApplication.VIDEO_WIDTH, MyApplication.VIDEO_HEIGHT, Config.ARGB_8888);
                Canvas canvas = new Canvas(mask);
                FinalMaskBitmap3D.rollMode = this;
                FinalMaskBitmap3D.drawRollWhole3D(top, bottom, canvas, false);
                return mask;
            }

            public void initBitmaps(Bitmap bottom, Bitmap top) {
                FinalMaskBitmap3D.direction = FinalMaskBitmap3D.VERTICALE;
                FinalMaskBitmap3D.partNumber = 8;
                FinalMaskBitmap3D.rollMode = this;
                FinalMaskBitmap3D.camera = new Camera();
                FinalMaskBitmap3D.matrix = new Matrix();
            }
        },
        Whole3D_LR("Whole3D_LR") {
            public Bitmap getMask(Bitmap bottom, Bitmap top, int factor) {
                FinalMaskBitmap3D.setRotateDegree(factor);
                Bitmap mask = Bitmap.createBitmap(MyApplication.VIDEO_WIDTH, MyApplication.VIDEO_HEIGHT, Config.ARGB_8888);
                Canvas canvas = new Canvas(mask);
                FinalMaskBitmap3D.rollMode = this;
                FinalMaskBitmap3D.drawRollWhole3D(bottom, top, canvas, false);
                return mask;
            }

            public void initBitmaps(Bitmap bottom, Bitmap top) {
                FinalMaskBitmap3D.partNumber = 8;
                FinalMaskBitmap3D.direction = FinalMaskBitmap3D.HORIZONTAL;
                FinalMaskBitmap3D.rollMode = this;
                FinalMaskBitmap3D.camera = new Camera();
                FinalMaskBitmap3D.matrix = new Matrix();
            }
        },
        Whole3D_RL("Whole3D_RL") {
            public Bitmap getMask(Bitmap bottom, Bitmap top, int factor) {
                FinalMaskBitmap3D.setRotateDegree(FinalMaskBitmap3D.ANIMATED_FRAME_CAL - factor);
                Bitmap mask = Bitmap.createBitmap(MyApplication.VIDEO_WIDTH, MyApplication.VIDEO_HEIGHT, Config.ARGB_8888);
                Canvas canvas = new Canvas(mask);
                FinalMaskBitmap3D.rollMode = this;
                FinalMaskBitmap3D.drawRollWhole3D(top, bottom, canvas, false);
                return mask;
            }

            public void initBitmaps(Bitmap bottom, Bitmap top) {
                FinalMaskBitmap3D.partNumber = 8;
                FinalMaskBitmap3D.rollMode = this;
                FinalMaskBitmap3D.direction = FinalMaskBitmap3D.HORIZONTAL;
                FinalMaskBitmap3D.camera = new Camera();
                FinalMaskBitmap3D.matrix = new Matrix();
            }
        },
        SepartConbine_TB("SepartConbine_TB") {
            public Bitmap getMask(Bitmap bottom, Bitmap top, int factor) {
                FinalMaskBitmap3D.rollMode = this;
                FinalMaskBitmap3D.setRotateDegree(factor);
                Bitmap mask = Bitmap.createBitmap(MyApplication.VIDEO_WIDTH, MyApplication.VIDEO_HEIGHT, Config.ARGB_8888);
                FinalMaskBitmap3D.drawSepartConbine(new Canvas(mask));
                return mask;
            }

            public void initBitmaps(Bitmap bottom, Bitmap top) {
                FinalMaskBitmap3D.partNumber = 4;
                FinalMaskBitmap3D.rollMode = this;
                FinalMaskBitmap3D.direction = FinalMaskBitmap3D.VERTICALE;
                FinalMaskBitmap3D.camera = new Camera();
                FinalMaskBitmap3D.matrix = new Matrix();
                FinalMaskBitmap3D.initBitmaps(bottom, top, this);
            }
        },

        SepartConbine_BT("SepartConbine_BT") {
            public Bitmap getMask(Bitmap bottom, Bitmap top, int factor) {
                FinalMaskBitmap3D.rollMode = this;
                FinalMaskBitmap3D.setRotateDegree(FinalMaskBitmap3D.ANIMATED_FRAME_CAL - factor);
                Bitmap mask = Bitmap.createBitmap(MyApplication.VIDEO_WIDTH, MyApplication.VIDEO_HEIGHT, Config.ARGB_8888);
                FinalMaskBitmap3D.drawSepartConbine(new Canvas(mask));
                return mask;
            }

            public void initBitmaps(Bitmap bottom, Bitmap top) {
                FinalMaskBitmap3D.partNumber = 4;
                FinalMaskBitmap3D.rollMode = this;
                FinalMaskBitmap3D.direction = FinalMaskBitmap3D.VERTICALE;
                FinalMaskBitmap3D.camera = new Camera();
                FinalMaskBitmap3D.matrix = new Matrix();
                FinalMaskBitmap3D.initBitmaps(top, bottom, this);
            }
        },
        SepartConbine_LR("SepartConbine_LR") {
            public Bitmap getMask(Bitmap bottom, Bitmap top, int factor) {
                FinalMaskBitmap3D.rollMode = this;
                FinalMaskBitmap3D.setRotateDegree(factor);
                Bitmap mask = Bitmap.createBitmap(MyApplication.VIDEO_WIDTH, MyApplication.VIDEO_HEIGHT, Config.ARGB_8888);
                FinalMaskBitmap3D.drawSepartConbine(new Canvas(mask));
                return mask;
            }

            public void initBitmaps(Bitmap bottom, Bitmap top) {
                FinalMaskBitmap3D.partNumber = 4;
                FinalMaskBitmap3D.rollMode = this;
                FinalMaskBitmap3D.direction = FinalMaskBitmap3D.HORIZONTAL;
                FinalMaskBitmap3D.camera = new Camera();
                FinalMaskBitmap3D.matrix = new Matrix();
                FinalMaskBitmap3D.initBitmaps(bottom, top, this);
            }
        },
        SepartConbine_RL("SepartConbine_RL") {
            public Bitmap getMask(Bitmap bottom, Bitmap top, int factor) {
                FinalMaskBitmap3D.rollMode = this;
                FinalMaskBitmap3D.setRotateDegree(FinalMaskBitmap3D.ANIMATED_FRAME_CAL - factor);
                Bitmap mask = Bitmap.createBitmap(MyApplication.VIDEO_WIDTH, MyApplication.VIDEO_HEIGHT, Config.ARGB_8888);
                FinalMaskBitmap3D.drawSepartConbine(new Canvas(mask));
                return mask;
            }

            public void initBitmaps(Bitmap bottom, Bitmap top) {
                FinalMaskBitmap3D.partNumber = 4;
                FinalMaskBitmap3D.rollMode = this;
                FinalMaskBitmap3D.direction = FinalMaskBitmap3D.HORIZONTAL;
                FinalMaskBitmap3D.camera = new Camera();
                FinalMaskBitmap3D.matrix = new Matrix();
                FinalMaskBitmap3D.initBitmaps(top, bottom, this);
            }
        },
        RollInTurn_TB("RollInTurn_TB") {
            public Bitmap getMask(Bitmap bottom, Bitmap top, int factor) {
                FinalMaskBitmap3D.rollMode = this;
                FinalMaskBitmap3D.setRotateDegree(factor);
                Bitmap mask = Bitmap.createBitmap(MyApplication.VIDEO_WIDTH, MyApplication.VIDEO_HEIGHT, Config.ARGB_8888);
                FinalMaskBitmap3D.drawRollInTurn(new Canvas(mask));
                return mask;
            }

            public void initBitmaps(Bitmap bottom, Bitmap top) {
                FinalMaskBitmap3D.partNumber = 8;
                FinalMaskBitmap3D.rollMode = this;
                FinalMaskBitmap3D.direction = FinalMaskBitmap3D.VERTICALE;
                FinalMaskBitmap3D.camera = new Camera();
                FinalMaskBitmap3D.matrix = new Matrix();
                FinalMaskBitmap3D.initBitmaps(bottom, top, this);
            }
        },
        RollInTurn_BT("RollInTurn_BT") {
            public Bitmap getMask(Bitmap bottom, Bitmap top, int factor) {
                FinalMaskBitmap3D.rollMode = this;
                FinalMaskBitmap3D.setRotateDegree(FinalMaskBitmap3D.ANIMATED_FRAME_CAL - factor);
                Bitmap mask = Bitmap.createBitmap(MyApplication.VIDEO_WIDTH, MyApplication.VIDEO_HEIGHT, Config.ARGB_8888);
                FinalMaskBitmap3D.drawRollInTurn(new Canvas(mask));
                return mask;
            }

            public void initBitmaps(Bitmap bottom, Bitmap top) {
                FinalMaskBitmap3D.rollMode = this;
                FinalMaskBitmap3D.partNumber = 8;
                FinalMaskBitmap3D.direction = FinalMaskBitmap3D.VERTICALE;
                FinalMaskBitmap3D.camera = new Camera();
                FinalMaskBitmap3D.matrix = new Matrix();
                FinalMaskBitmap3D.initBitmaps(top, bottom, this);
            }
        },
        RollInTurn_LR("RollInTurn_LR") {
            public Bitmap getMask(Bitmap bottom, Bitmap top, int factor) {
                FinalMaskBitmap3D.rollMode = this;
                FinalMaskBitmap3D.setRotateDegree(factor);
                Bitmap mask = Bitmap.createBitmap(MyApplication.VIDEO_WIDTH, MyApplication.VIDEO_HEIGHT, Config.ARGB_8888);
                FinalMaskBitmap3D.drawRollInTurn(new Canvas(mask));
                return mask;
            }

            public void initBitmaps(Bitmap bottom, Bitmap top) {
                FinalMaskBitmap3D.rollMode = this;
                FinalMaskBitmap3D.partNumber = 8;
                FinalMaskBitmap3D.direction = FinalMaskBitmap3D.HORIZONTAL;
                FinalMaskBitmap3D.camera = new Camera();
                FinalMaskBitmap3D.matrix = new Matrix();
                FinalMaskBitmap3D.initBitmaps(bottom, top, this);
            }
        },
        RollInTurn_RL("RollInTurn_RL") {
            public Bitmap getMask(Bitmap bottom, Bitmap top, int factor) {
                FinalMaskBitmap3D.rollMode = this;
                FinalMaskBitmap3D.setRotateDegree(FinalMaskBitmap3D.ANIMATED_FRAME_CAL - factor);
                Bitmap mask = Bitmap.createBitmap(MyApplication.VIDEO_WIDTH, MyApplication.VIDEO_HEIGHT, Config.ARGB_8888);
                FinalMaskBitmap3D.drawRollInTurn(new Canvas(mask));
                return mask;
            }

            public void initBitmaps(Bitmap bottom, Bitmap top) {
                FinalMaskBitmap3D.rollMode = this;
                FinalMaskBitmap3D.partNumber = 8;
                FinalMaskBitmap3D.direction = FinalMaskBitmap3D.HORIZONTAL;
                FinalMaskBitmap3D.camera = new Camera();
                FinalMaskBitmap3D.matrix = new Matrix();
                FinalMaskBitmap3D.initBitmaps(top, bottom, this);
            }
        },
        Jalousie_BT("Jalousie_BT") {
            public Bitmap getMask(Bitmap bottom, Bitmap top, int factor) {
                FinalMaskBitmap3D.rollMode = this;
                FinalMaskBitmap3D.setRotateDegree(FinalMaskBitmap3D.ANIMATED_FRAME_CAL - factor);
                Bitmap mask = Bitmap.createBitmap(MyApplication.VIDEO_WIDTH, MyApplication.VIDEO_HEIGHT, Config.ARGB_8888);
                FinalMaskBitmap3D.drawJalousie(new Canvas(mask));
                return mask;
            }

            public void initBitmaps(Bitmap bottom, Bitmap top) {
                FinalMaskBitmap3D.rollMode = this;
                FinalMaskBitmap3D.partNumber = 8;
                FinalMaskBitmap3D.direction = FinalMaskBitmap3D.VERTICALE;
                FinalMaskBitmap3D.camera = new Camera();
                FinalMaskBitmap3D.matrix = new Matrix();
                FinalMaskBitmap3D.initBitmaps(top, bottom, this);
            }
        },
        Jalousie_LR("Jalousie_LR") {
            public Bitmap getMask(Bitmap bottom, Bitmap top, int factor) {
                FinalMaskBitmap3D.rollMode = this;
                FinalMaskBitmap3D.setRotateDegree(factor);
                Bitmap mask = Bitmap.createBitmap(MyApplication.VIDEO_WIDTH, MyApplication.VIDEO_HEIGHT, Config.ARGB_8888);
                FinalMaskBitmap3D.drawJalousie(new Canvas(mask));
                return mask;
            }

            public void initBitmaps(Bitmap bottom, Bitmap top) {
                FinalMaskBitmap3D.rollMode = this;
                FinalMaskBitmap3D.partNumber = 8;
                FinalMaskBitmap3D.direction = FinalMaskBitmap3D.HORIZONTAL;
                FinalMaskBitmap3D.camera = new Camera();
                FinalMaskBitmap3D.matrix = new Matrix();
                FinalMaskBitmap3D.initBitmaps(bottom, top, this);
            }
        };

        String name;

        public abstract Bitmap getMask(Bitmap bitmap, Bitmap bitmap2, int i);

        public abstract void initBitmaps(Bitmap bitmap, Bitmap bitmap2);

        private EFFECT(String name) {
            this.name = "";
            this.name = name;
        }

    }

    static {
        TOTAL_FRAME = 30;
        ORIGANAL_FRAME = 8;
        ANIMATED_FRAME = 22.0f;
        ANIMATED_FRAME_CAL = (int) (ANIMATED_FRAME -1.0f);
        random = new Random();
        partNumber = 8;
        direction = HORIZONTAL;
        paint = new Paint();
        paint.setColor(-16777216);
        paint.setAntiAlias(true);
        paint.setStyle(Style.FILL_AND_STROKE);
        camera = new Camera();
        matrix = new Matrix();
    }

    public static void reintRect() {
        randRect = (int[][]) Array.newInstance(Integer.TYPE, new int[]{(int) ANIMATED_FRAME, (int) ANIMATED_FRAME});
        for (int i = HORIZONTAL; i < randRect.length; i += VERTICALE) {
            for (int j = HORIZONTAL; j < randRect[i].length; j += VERTICALE) {
                randRect[i][j] = HORIZONTAL;
            }
        }
    }

    public static void setRotateDegree(int factor) {
        int i = 90;
        if (rollMode == EFFECT.RollInTurn_BT || rollMode == EFFECT.RollInTurn_LR || rollMode == EFFECT.RollInTurn_RL || rollMode == EFFECT.RollInTurn_TB) {
            rotateDegree = (((((float) (partNumber - 1)) * 30.0f) + 90.0f) * ((float) factor)) / ((float) ANIMATED_FRAME_CAL);
        } else if (rollMode == EFFECT.Jalousie_BT || rollMode == EFFECT.Jalousie_LR) {
            rotateDegree = (180.0f * ((float) factor)) / ((float) ANIMATED_FRAME_CAL);
        } else {
            rotateDegree = (((float) factor) * 90.0f) / ((float) ANIMATED_FRAME_CAL);
        }
        if (direction == VERTICALE) {
            float f = rotateDegree;
            if (rollMode == EFFECT.Jalousie_BT || rollMode == EFFECT.Jalousie_LR) {
                i = 180;
            }
            axisY = (f / ((float) i)) * ((float) MyApplication.VIDEO_HEIGHT);
            return;
        }
        f = rotateDegree;
        if (rollMode == EFFECT.Jalousie_BT || rollMode == EFFECT.Jalousie_LR) {
            i = 180;
        }
        axisX = (f / ((float) i)) * ((float) MyApplication.VIDEO_WIDTH);
    }

    public static void initBitmaps(Bitmap bottom, Bitmap top, EFFECT effect) {
        rollMode = effect;
        if (MyApplication.VIDEO_HEIGHT > 0 || MyApplication.VIDEO_WIDTH > 0) {
            bitmaps = (Bitmap[][]) Array.newInstance(Bitmap.class, new int[]{2, partNumber});
            averageWidth = MyApplication.VIDEO_WIDTH / partNumber;
            averageHeight = MyApplication.VIDEO_HEIGHT / partNumber;
            int i = HORIZONTAL;
            while (i < 2) {
                for (int j = HORIZONTAL; j < partNumber; j += VERTICALE) {
                    Bitmap partBitmap;
                    Rect rect;
                    Bitmap bitmap;
                    if (rollMode == EFFECT.Jalousie_BT || rollMode == EFFECT.Jalousie_LR) {
                        if (direction == VERTICALE) {
                            rect = new Rect(HORIZONTAL, averageHeight * j, MyApplication.VIDEO_WIDTH, (j + VERTICALE) * averageHeight);
                            if (i == 0) {
                                bitmap = bottom;
                            } else {
                                bitmap = top;
                            }
                            partBitmap = getPartBitmap(bitmap, HORIZONTAL, averageHeight * j, rect);
                        } else {
                            rect = new Rect(averageWidth * j, HORIZONTAL, (j + VERTICALE) * averageWidth, MyApplication.VIDEO_HEIGHT);
                            if (i == 0) {
                                bitmap = bottom;
                            } else {
                                bitmap = top;
                            }
                            partBitmap = getPartBitmap(bitmap, averageWidth * j, HORIZONTAL, rect);
                        }
                    } else if (direction == VERTICALE) {
                        rect = new Rect(averageWidth * j, HORIZONTAL, (j + VERTICALE) * averageWidth, MyApplication.VIDEO_HEIGHT);
                        if (i == 0) {
                            bitmap = bottom;
                        } else {
                            bitmap = top;
                        }
                        partBitmap = getPartBitmap(bitmap, averageWidth * j, HORIZONTAL, rect);
                    } else {
                        partBitmap = getPartBitmap(i == 0 ? bottom : top, HORIZONTAL, averageHeight * j, new Rect(HORIZONTAL, averageHeight * j, MyApplication.VIDEO_WIDTH, (j + VERTICALE) * averageHeight));
                    }
                    bitmaps[i][j] = partBitmap;
                }
                i += VERTICALE;
            }
        }
    }

    private static Bitmap getPartBitmap(Bitmap bitmap, int x, int y, Rect rect) {
        return Bitmap.createBitmap(bitmap, x, y, rect.width(), rect.height());
    }

    private static void drawRollWhole3D(Bitmap bottom, Bitmap top, Canvas canvas, boolean draw2D) {
        Bitmap currWholeBitmap = bottom;
        Bitmap nextWholeBitmap = top;
        canvas.save();
        if (direction == VERTICALE) {
            camera.save();
            if (draw2D) {
                camera.rotateX(0.0f);
            } else {
                camera.rotateX(-rotateDegree);
            }
            camera.getMatrix(matrix);
            camera.restore();
            matrix.preTranslate((float) ((-MyApplication.VIDEO_WIDTH) / 2), 0.0f);
            matrix.postTranslate((float) (MyApplication.VIDEO_WIDTH / 2), axisY);
            canvas.drawBitmap(currWholeBitmap, matrix, paint);
            camera.save();
            if (draw2D) {
                camera.rotateX(0.0f);
            } else {
                camera.rotateX(90.0f - rotateDegree);
            }
            camera.getMatrix(matrix);
            camera.restore();
            matrix.preTranslate((float) ((-MyApplication.VIDEO_WIDTH) / 2), (float) (-MyApplication.VIDEO_HEIGHT));
            matrix.postTranslate((float) (MyApplication.VIDEO_WIDTH / 2), axisY);
            canvas.drawBitmap(nextWholeBitmap, matrix, paint);
        } else {
            camera.save();
            if (draw2D) {
                camera.rotateY(0.0f);
            } else {
                camera.rotateY(rotateDegree);
            }
            camera.getMatrix(matrix);
            camera.restore();
            matrix.preTranslate(0.0f, (float) ((-MyApplication.VIDEO_HEIGHT) / 2));
            matrix.postTranslate(axisX, (float) (MyApplication.VIDEO_HEIGHT / 2));
            canvas.drawBitmap(currWholeBitmap, matrix, paint);
            camera.save();
            if (draw2D) {
                camera.rotateY(0.0f);
            } else {
                camera.rotateY(rotateDegree - 90.0f);
            }
            camera.getMatrix(matrix);
            camera.restore();
            matrix.preTranslate((float) (-MyApplication.VIDEO_WIDTH), (float) ((-MyApplication.VIDEO_HEIGHT) / 2));
            matrix.postTranslate(axisX, (float) (MyApplication.VIDEO_HEIGHT / 2));
            canvas.drawBitmap(nextWholeBitmap, matrix, paint);
        }
        canvas.restore();
    }

    private static void drawSepartConbine(Canvas canvas) {
        for (int i = HORIZONTAL; i < partNumber; i += VERTICALE) {
            Bitmap currBitmap = bitmaps[HORIZONTAL][i];
            Bitmap nextBitmap = bitmaps[VERTICALE][i];
            canvas.save();
            if (direction == VERTICALE) {
                camera.save();
                camera.rotateX(-rotateDegree);
                camera.getMatrix(matrix);
                camera.restore();
                matrix.preTranslate((float) ((-currBitmap.getWidth()) / 2), 0.0f);
                matrix.postTranslate((float) ((currBitmap.getWidth() / 2) + (averageWidth * i)), axisY);
                canvas.drawBitmap(currBitmap, matrix, paint);
                camera.save();
                camera.rotateX(90.0f - rotateDegree);
                camera.getMatrix(matrix);
                camera.restore();
                matrix.preTranslate((float) ((-nextBitmap.getWidth()) / 2), (float) (-nextBitmap.getHeight()));
                matrix.postTranslate((float) ((nextBitmap.getWidth() / 2) + (averageWidth * i)), axisY);
                canvas.drawBitmap(nextBitmap, matrix, paint);
            } else {
                camera.save();
                camera.rotateY(rotateDegree);
                camera.getMatrix(matrix);
                camera.restore();
                matrix.preTranslate(0.0f, (float) ((-currBitmap.getHeight()) / 2));
                matrix.postTranslate(axisX, (float) ((currBitmap.getHeight() / 2) + (averageHeight * i)));
                canvas.drawBitmap(currBitmap, matrix, paint);
                camera.save();
                camera.rotateY(rotateDegree - 90.0f);
                camera.getMatrix(matrix);
                camera.restore();
                matrix.preTranslate((float) (-nextBitmap.getWidth()), (float) ((-nextBitmap.getHeight()) / 2));
                matrix.postTranslate(axisX, (float) ((nextBitmap.getHeight() / 2) + (averageHeight * i)));
                canvas.drawBitmap(nextBitmap, matrix, paint);
            }
            canvas.restore();
        }
    }

    private static void drawRollInTurn(Canvas canvas) {
        for (int i = HORIZONTAL; i < partNumber; i += VERTICALE) {
            Bitmap currBitmap = bitmaps[HORIZONTAL][i];
            Bitmap nextBitmap = bitmaps[VERTICALE][i];
            float tDegree = rotateDegree - ((float) (i * 30));
            if (tDegree < 0.0f) {
                tDegree = 0.0f;
            }
            if (tDegree > 90.0f) {
                tDegree = 90.0f;
            }
            canvas.save();
            if (direction == VERTICALE) {
                float tAxisY = (tDegree / 90.0f) * ((float) MyApplication.VIDEO_HEIGHT);
                if (tAxisY > ((float) MyApplication.VIDEO_HEIGHT)) {
                    tAxisY = (float) MyApplication.VIDEO_HEIGHT;
                }
                if (tAxisY < 0.0f) {
                    tAxisY = 0.0f;
                }
                camera.save();
                camera.rotateX(-tDegree);
                camera.getMatrix(matrix);
                camera.restore();
                matrix.preTranslate((float) (-currBitmap.getWidth()), 0.0f);
                matrix.postTranslate((float) (currBitmap.getWidth() + (averageWidth * i)), tAxisY);
                canvas.drawBitmap(currBitmap, matrix, paint);
                camera.save();
                camera.rotateX(90.0f - tDegree);
                camera.getMatrix(matrix);
                camera.restore();
                matrix.preTranslate((float) (-nextBitmap.getWidth()), (float) (-nextBitmap.getHeight()));
                matrix.postTranslate((float) (nextBitmap.getWidth() + (averageWidth * i)), tAxisY);
                canvas.drawBitmap(nextBitmap, matrix, paint);
            } else {
                float tAxisX = (tDegree / 90.0f) * ((float) MyApplication.VIDEO_WIDTH);
                if (tAxisX > ((float) MyApplication.VIDEO_WIDTH)) {
                    tAxisX = (float) MyApplication.VIDEO_WIDTH;
                }
                if (tAxisX < 0.0f) {
                    tAxisX = 0.0f;
                }
                camera.save();
                camera.rotateY(tDegree);
                camera.getMatrix(matrix);
                camera.restore();
                matrix.preTranslate(0.0f, (float) ((-currBitmap.getHeight()) / 2));
                matrix.postTranslate(tAxisX, (float) ((currBitmap.getHeight() / 2) + (averageHeight * i)));
                canvas.drawBitmap(currBitmap, matrix, paint);
                camera.save();
                camera.rotateY(tDegree - 90.0f);
                camera.getMatrix(matrix);
                camera.restore();
                matrix.preTranslate((float) (-nextBitmap.getWidth()), (float) ((-nextBitmap.getHeight()) / 2));
                matrix.postTranslate(tAxisX, (float) ((nextBitmap.getHeight() / 2) + (averageHeight * i)));
                canvas.drawBitmap(nextBitmap, matrix, paint);
            }
            canvas.restore();
        }
    }

    private static void drawJalousie(Canvas canvas) {
        for (int i = HORIZONTAL; i < partNumber; i += VERTICALE) {
            Bitmap currBitmap = bitmaps[HORIZONTAL][i];
            Bitmap nextBitmap = bitmaps[VERTICALE][i];
            canvas.save();
            if (direction == VERTICALE) {
                if (rotateDegree < 90.0f) {
                    camera.save();
                    camera.rotateX(rotateDegree);
                    camera.getMatrix(matrix);
                    camera.restore();
                    matrix.preTranslate((float) ((-currBitmap.getWidth()) / 2), (float) ((-currBitmap.getHeight()) / 2));
                    matrix.postTranslate((float) (currBitmap.getWidth() / 2), (float) ((currBitmap.getHeight() / 2) + (averageHeight * i)));
                    canvas.drawBitmap(currBitmap, matrix, paint);
                } else {
                    camera.save();
                    camera.rotateX(180.0f - rotateDegree);
                    camera.getMatrix(matrix);
                    camera.restore();
                    matrix.preTranslate((float) ((-nextBitmap.getWidth()) / 2), (float) ((-nextBitmap.getHeight()) / 2));
                    matrix.postTranslate((float) (nextBitmap.getWidth() / 2), (float) ((nextBitmap.getHeight() / 2) + (averageHeight * i)));
                    canvas.drawBitmap(nextBitmap, matrix, paint);
                }
            } else if (rotateDegree < 90.0f) {
                camera.save();
                camera.rotateY(rotateDegree);
                camera.getMatrix(matrix);
                camera.restore();
                matrix.preTranslate((float) ((-currBitmap.getWidth()) / 2), (float) ((-currBitmap.getHeight()) / 2));
                matrix.postTranslate((float) ((currBitmap.getWidth() / 2) + (averageWidth * i)), (float) (currBitmap.getHeight() / 2));
                canvas.drawBitmap(currBitmap, matrix, paint);
            } else {
                camera.save();
                camera.rotateY(180.0f - rotateDegree);
                camera.getMatrix(matrix);
                camera.restore();
                matrix.preTranslate((float) ((-nextBitmap.getWidth()) / 2), (float) ((-nextBitmap.getHeight()) / 2));
                matrix.postTranslate((float) ((nextBitmap.getWidth() / 2) + (averageWidth * i)), (float) (nextBitmap.getHeight() / 2));
                canvas.drawBitmap(nextBitmap, matrix, paint);
            }
            canvas.restore();
        }
    }
}