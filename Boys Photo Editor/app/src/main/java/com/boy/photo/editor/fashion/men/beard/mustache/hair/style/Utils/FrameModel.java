package com.boy.photo.editor.fashion.men.beard.mustache.hair.style.Utils;

public class FrameModel {
	private int effectId;
	private int frameID;
	private int image1;
	private int image2;
	private int thumbId;

	public FrameModel(int paramInt1, int paramInt2) {
		this.thumbId = paramInt1;
		this.frameID = paramInt2;
	}

	public int getEffectId() {
		return this.effectId;
	}

	public int getFrameID() {
		return this.frameID;
	}

	public int getImage1() {
		return this.image1;
	}

	public int getImage2() {
		return this.image2;
	}

	public int getThumbId() {
		return this.thumbId;
	}

	public void setEffectId(int paramInt) {
		this.effectId = paramInt;
	}

	public void setFrameID(int paramInt) {
		this.frameID = paramInt;
	}

	public void setImage1(int paramInt) {
		this.image1 = paramInt;
	}

	public void setImage2(int paramInt) {
		this.image2 = paramInt;
	}

	public void setThumbId(int paramInt) {
		this.thumbId = paramInt;
	}

}
