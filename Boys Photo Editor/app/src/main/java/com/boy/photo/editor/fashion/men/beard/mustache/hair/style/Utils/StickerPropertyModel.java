package com.boy.photo.editor.fashion.men.beard.mustache.hair.style.Utils;

import java.io.Serializable;

public class StickerPropertyModel implements Serializable {
	private static final long serialVersionUID = 3800737478616389410L;
	private float degree;
	private int horizontal_mirror;
	private int order;
	private float scaling;
	private long sticker_id;
	private String sticker_URL;
	private String text;
	private float xLocation;
	private float yLocation;

	public float getDegree() {
		return this.degree;
	}

	public int getHorizonMirror() {
		return this.horizontal_mirror;
	}

	public int getOrder() {
		return this.order;
	}

	public float getScaling() {
		return this.scaling;
	}

	public long getStickerId() {
		return this.sticker_id;
	}

	public String getStickerURL() {
		return this.sticker_URL;
	}

	public String getText() {
		return this.text;
	}

	public float getxLocation() {
		return this.xLocation;
	}

	public float getyLocation() {
		return this.yLocation;
	}

	public void setDegree(float paramFloat) {
		this.degree = paramFloat;
	}

	public void setHorizonMirror(int paramInt) {
		this.horizontal_mirror = paramInt;
	}

	public void setOrder(int paramInt) {
		this.order = paramInt;
	}

	public void setScaling(float paramFloat) {
		this.scaling = paramFloat;
	}

	public void setStickerId(long paramLong) {
		this.sticker_id = paramLong;
	}

	public void setStickerURL(String paramString) {
		this.sticker_URL = paramString;
	}

	public void setText(String paramString) {
		this.text = paramString;
	}

	public void setxLocation(float paramFloat) {
		this.xLocation = paramFloat;
	}

	public void setyLocation(float paramFloat) {
		this.yLocation = paramFloat;
	}
}
