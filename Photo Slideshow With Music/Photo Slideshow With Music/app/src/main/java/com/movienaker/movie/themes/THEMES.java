package com.movienaker.movie.themes;

import com.cybertechinfosoft.photoslideshowwithmusic.R;
import com.cybertechinfosoft.photoslideshowwithmusic.mask.FinalMaskBitmap3D;
import java.util.ArrayList;

public enum THEMES {
    Shine("Shine") {
        public ArrayList<FinalMaskBitmap3D.EFFECT> getTheme(ArrayList<FinalMaskBitmap3D.EFFECT> arrayList) {
            return null;
        }

        public int getThemeDrawable() {
            return R.drawable.shine;
        }

        public int getThemeMusic() {
            return R.raw._1;
        }

        public ArrayList<FinalMaskBitmap3D.EFFECT> getTheme() {
            ArrayList<FinalMaskBitmap3D.EFFECT> arrayList = new ArrayList();
            arrayList.add(FinalMaskBitmap3D.EFFECT.Whole3D_BT);
            arrayList.add(FinalMaskBitmap3D.EFFECT.Whole3D_TB);
            arrayList.add(FinalMaskBitmap3D.EFFECT.Whole3D_LR);
            arrayList.add(FinalMaskBitmap3D.EFFECT.Whole3D_RL);
            arrayList.add(FinalMaskBitmap3D.EFFECT.SepartConbine_BT);
            arrayList.add(FinalMaskBitmap3D.EFFECT.SepartConbine_TB);
            arrayList.add(FinalMaskBitmap3D.EFFECT.SepartConbine_LR);
            arrayList.add(FinalMaskBitmap3D.EFFECT.SepartConbine_RL);
            arrayList.add(FinalMaskBitmap3D.EFFECT.RollInTurn_BT);
            arrayList.add(FinalMaskBitmap3D.EFFECT.RollInTurn_TB);
            arrayList.add(FinalMaskBitmap3D.EFFECT.RollInTurn_LR);
            arrayList.add(FinalMaskBitmap3D.EFFECT.RollInTurn_RL);
            arrayList.add(FinalMaskBitmap3D.EFFECT.Jalousie_BT);
            arrayList.add(FinalMaskBitmap3D.EFFECT.Jalousie_LR);
            arrayList.add(FinalMaskBitmap3D.EFFECT.Roll2D_BT);
            arrayList.add(FinalMaskBitmap3D.EFFECT.Roll2D_TB);
            arrayList.add(FinalMaskBitmap3D.EFFECT.Roll2D_LR);
            arrayList.add(FinalMaskBitmap3D.EFFECT.Roll2D_RL);
            return arrayList;
        }
    },
    Jalousie_Down_Up("Jalousie Down Up") {
        public ArrayList<FinalMaskBitmap3D.EFFECT> getTheme(ArrayList<FinalMaskBitmap3D.EFFECT> arrayList) {
            return null;
        }

        public int getThemeDrawable() {
            return R.drawable.jalousie_bt;
        }

        public int getThemeMusic() {
            return R.raw._2;
        }

        public ArrayList<FinalMaskBitmap3D.EFFECT> getTheme() {
            ArrayList<FinalMaskBitmap3D.EFFECT> arrayList = new ArrayList();
            arrayList.add(FinalMaskBitmap3D.EFFECT.Jalousie_BT);
            return arrayList;
        }
    },
    Jalousie_Left_Right("Jalousie Left Right") {
        public ArrayList<FinalMaskBitmap3D.EFFECT> getTheme(ArrayList<FinalMaskBitmap3D.EFFECT> arrayList) {
            return null;
        }

        public int getThemeDrawable() {
            return R.drawable.jalousie_lr;
        }

        public int getThemeMusic() {
            return R.raw._4;
        }

        public ArrayList<FinalMaskBitmap3D.EFFECT> getTheme() {
            ArrayList<FinalMaskBitmap3D.EFFECT> arrayList = new ArrayList();
            arrayList.add(FinalMaskBitmap3D.EFFECT.Jalousie_LR);
            return arrayList;
        }
    },
    Whole3D_Down_Up("Whole3D Down Up") {
        public ArrayList<FinalMaskBitmap3D.EFFECT> getTheme(ArrayList<FinalMaskBitmap3D.EFFECT> arrayList) {
            return null;
        }

        public int getThemeDrawable() {
            return R.drawable.whole_3d_bt;
        }

        public int getThemeMusic() {
            return R.raw._5;
        }

        public ArrayList<FinalMaskBitmap3D.EFFECT> getTheme() {
            ArrayList<FinalMaskBitmap3D.EFFECT> arrayList = new ArrayList();
            arrayList.add(FinalMaskBitmap3D.EFFECT.Whole3D_BT);
            return arrayList;
        }
    },
    Whole3D_Up_Down("Whole3D Up Down") {
        public ArrayList<FinalMaskBitmap3D.EFFECT> getTheme(ArrayList<FinalMaskBitmap3D.EFFECT> arrayList) {
            return null;
        }

        public int getThemeDrawable() {
            return R.drawable.whole_3d_tb;
        }

        public int getThemeMusic() {
            return R.raw._1;
        }

        public ArrayList<FinalMaskBitmap3D.EFFECT> getTheme() {
            ArrayList<FinalMaskBitmap3D.EFFECT> arrayList = new ArrayList();
            arrayList.add(FinalMaskBitmap3D.EFFECT.Whole3D_TB);
            return arrayList;
        }
    },
    Whole3D_Left_Right("Whole3D Left Right") {
        public ArrayList<FinalMaskBitmap3D.EFFECT> getTheme(ArrayList<FinalMaskBitmap3D.EFFECT> arrayList) {
            return null;
        }

        public int getThemeDrawable() {
            return R.drawable.whole_3d_lr;
        }

        public int getThemeMusic() {
            return R.raw._2;
        }

        public ArrayList<FinalMaskBitmap3D.EFFECT> getTheme() {
            ArrayList<FinalMaskBitmap3D.EFFECT> arrayList = new ArrayList();
            arrayList.add(FinalMaskBitmap3D.EFFECT.Whole3D_LR);
            return arrayList;
        }
    },
    Whole3D_Right_Left("Whole3D Right Left") {
        public ArrayList<FinalMaskBitmap3D.EFFECT> getTheme(ArrayList<FinalMaskBitmap3D.EFFECT> arrayList) {
            return null;
        }

        public int getThemeDrawable() {
            return R.drawable.whole_3d_rl;
        }

        public int getThemeMusic() {
            return R.raw._3;
        }

        public ArrayList<FinalMaskBitmap3D.EFFECT> getTheme() {
            ArrayList<FinalMaskBitmap3D.EFFECT> arrayList = new ArrayList();
            arrayList.add(FinalMaskBitmap3D.EFFECT.Whole3D_RL);
            return arrayList;
        }
    },
    SepartConbine_Down_Up("SepartConbine Down Up") {
        public ArrayList<FinalMaskBitmap3D.EFFECT> getTheme(ArrayList<FinalMaskBitmap3D.EFFECT> arrayList) {
            return null;
        }

        public int getThemeDrawable() {
            return R.drawable.separtcon_down;
        }

        public int getThemeMusic() {
            return R.raw._4;
        }

        public ArrayList<FinalMaskBitmap3D.EFFECT> getTheme() {
            ArrayList<FinalMaskBitmap3D.EFFECT> arrayList = new ArrayList();
            arrayList.add(FinalMaskBitmap3D.EFFECT.SepartConbine_BT);
            return arrayList;
        }
    },
    SepartConbine_Up_Down("SepartConbine Up Down") {
        public ArrayList<FinalMaskBitmap3D.EFFECT> getTheme(ArrayList<FinalMaskBitmap3D.EFFECT> arrayList) {
            return null;
        }

        public int getThemeDrawable() {
            return R.drawable.separtcon_up;
        }

        public int getThemeMusic() {
            return R.raw._5;
        }

        public ArrayList<FinalMaskBitmap3D.EFFECT> getTheme() {
            ArrayList<FinalMaskBitmap3D.EFFECT> arrayList = new ArrayList();
            arrayList.add(FinalMaskBitmap3D.EFFECT.SepartConbine_TB);
            return arrayList;
        }
    },
    SepartConbine_Left_Right("SepartConbine Left Right") {
        public ArrayList<FinalMaskBitmap3D.EFFECT> getTheme(ArrayList<FinalMaskBitmap3D.EFFECT> arrayList) {
            return null;
        }

        public int getThemeDrawable() {
            return R.drawable.separtcon;
        }

        public int getThemeMusic() {
            return R.raw._1;
        }

        public ArrayList<FinalMaskBitmap3D.EFFECT> getTheme() {
            ArrayList<FinalMaskBitmap3D.EFFECT> arrayList = new ArrayList();
            arrayList.add(FinalMaskBitmap3D.EFFECT.SepartConbine_LR);
            return arrayList;
        }
    },
    SepartConbine_Right_Left("SepartConbine Right Left") {
        public ArrayList<FinalMaskBitmap3D.EFFECT> getTheme(ArrayList<FinalMaskBitmap3D.EFFECT> arrayList) {
            return null;
        }

        public int getThemeDrawable() {
            return R.drawable.separtcon_rote;
        }

        public int getThemeMusic() {
            return R.raw._2;
        }

        public ArrayList<FinalMaskBitmap3D.EFFECT> getTheme() {
            ArrayList<FinalMaskBitmap3D.EFFECT> arrayList = new ArrayList();
            arrayList.add(FinalMaskBitmap3D.EFFECT.SepartConbine_RL);
            return arrayList;
        }
    },
    RollInTurn_Down_Up("RollInTurn Down Up") {
        public ArrayList<FinalMaskBitmap3D.EFFECT> getTheme(ArrayList<FinalMaskBitmap3D.EFFECT> arrayList) {
            return null;
        }

        public int getThemeDrawable() {
            return R.drawable.rolln_turn_right;
        }

        public int getThemeMusic() {
            return R.raw._3;
        }

        public ArrayList<FinalMaskBitmap3D.EFFECT> getTheme() {
            ArrayList<FinalMaskBitmap3D.EFFECT> arrayList = new ArrayList();
            arrayList.add(FinalMaskBitmap3D.EFFECT.RollInTurn_BT);
            return arrayList;
        }
    },
    RollInTurn_Up_Down("RollInTurn Up Down") {
        public ArrayList<FinalMaskBitmap3D.EFFECT> getTheme(ArrayList<FinalMaskBitmap3D.EFFECT> arrayList) {
            return null;
        }

        public int getThemeDrawable() {
            return R.drawable.rolln_turn_left;
        }

        public int getThemeMusic() {
            return R.raw._4;
        }

        public ArrayList<FinalMaskBitmap3D.EFFECT> getTheme() {
            ArrayList<FinalMaskBitmap3D.EFFECT> arrayList = new ArrayList();
            arrayList.add(FinalMaskBitmap3D.EFFECT.RollInTurn_TB);
            return arrayList;
        }
    },
    RollInTurn_Left_Right("RollInTurn Left Right") {
        public ArrayList<FinalMaskBitmap3D.EFFECT> getTheme(ArrayList<FinalMaskBitmap3D.EFFECT> arrayList) {
            return null;
        }

        public int getThemeDrawable() {
            return R.drawable.rolln_turn_up;
        }

        public int getThemeMusic() {
            return R.raw._5;
        }

        public ArrayList<FinalMaskBitmap3D.EFFECT> getTheme() {
            ArrayList<FinalMaskBitmap3D.EFFECT> arrayList = new ArrayList();
            arrayList.add(FinalMaskBitmap3D.EFFECT.RollInTurn_LR);
            return arrayList;
        }
    },
    RollInTurn_Right_Left("RollInTurn Right Left") {
        public ArrayList<FinalMaskBitmap3D.EFFECT> getTheme(ArrayList<FinalMaskBitmap3D.EFFECT> arrayList) {
            return null;
        }

        public int getThemeDrawable() {
            return R.drawable.rolln_turn_down;
        }

        public int getThemeMusic() {
            return R.raw._1;
        }

        public ArrayList<FinalMaskBitmap3D.EFFECT> getTheme() {
            ArrayList<FinalMaskBitmap3D.EFFECT> arrayList = new ArrayList();
            arrayList.add(FinalMaskBitmap3D.EFFECT.RollInTurn_RL);
            return arrayList;
        }
    },
    Roll2D_Down_Up("Roll2D Down Up") {
        public ArrayList<FinalMaskBitmap3D.EFFECT> getTheme(ArrayList<FinalMaskBitmap3D.EFFECT> arrayList) {
            return null;
        }

        public int getThemeDrawable() {
            return R.drawable.roll2d_bt;
        }

        public int getThemeMusic() {
            return R.raw._2;
        }

        public ArrayList<FinalMaskBitmap3D.EFFECT> getTheme() {
            ArrayList<FinalMaskBitmap3D.EFFECT> arrayList = new ArrayList();
            arrayList.add(FinalMaskBitmap3D.EFFECT.Roll2D_BT);
            return arrayList;
        }
    },
    Roll2D_Up_Down("Roll2D Up Down") {
        public ArrayList<FinalMaskBitmap3D.EFFECT> getTheme(ArrayList<FinalMaskBitmap3D.EFFECT> arrayList) {
            return null;
        }

        public int getThemeDrawable() {
            return R.drawable.roll2d_tb;
        }

        public int getThemeMusic() {
            return R.raw._3;
        }

        public ArrayList<FinalMaskBitmap3D.EFFECT> getTheme() {
            ArrayList<FinalMaskBitmap3D.EFFECT> arrayList = new ArrayList();
            arrayList.add(FinalMaskBitmap3D.EFFECT.Roll2D_TB);
            return arrayList;
        }
    },
    Roll2D_Left_Right("Roll2D Left Right") {
        public ArrayList<FinalMaskBitmap3D.EFFECT> getTheme(ArrayList<FinalMaskBitmap3D.EFFECT> arrayList) {
            return null;
        }

        public int getThemeDrawable() {
            return R.drawable.roll2d_lr;
        }

        public int getThemeMusic() {
            return R.raw._4;
        }

        public ArrayList<FinalMaskBitmap3D.EFFECT> getTheme() {
            ArrayList<FinalMaskBitmap3D.EFFECT> arrayList = new ArrayList();
            arrayList.add(FinalMaskBitmap3D.EFFECT.Roll2D_LR);
            return arrayList;
        }
    },
    Roll2D_Right_Left("Roll2D Right Left") {
        public ArrayList<FinalMaskBitmap3D.EFFECT> getTheme(ArrayList<FinalMaskBitmap3D.EFFECT> arrayList) {
            return null;
        }

        public int getThemeDrawable() {
            return R.drawable.roll2d_rl;
        }

        public int getThemeMusic() {
            return R.raw._5;
        }

        public ArrayList<FinalMaskBitmap3D.EFFECT> getTheme() {
            ArrayList<FinalMaskBitmap3D.EFFECT> arrayList = new ArrayList();
            arrayList.add(FinalMaskBitmap3D.EFFECT.Roll2D_RL);
            return arrayList;
        }
    };

    String name;

    public abstract ArrayList<FinalMaskBitmap3D.EFFECT> getTheme();

    public abstract ArrayList<FinalMaskBitmap3D.EFFECT> getTheme(ArrayList<FinalMaskBitmap3D.EFFECT> arrayList);

    public abstract int getThemeDrawable();

    public abstract int getThemeMusic();

    private THEMES(String str) {
        this.name = "";
        this.name = str;
    }
}
