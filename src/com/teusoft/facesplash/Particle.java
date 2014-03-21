package com.teusoft.facesplash;

public class Particle {
	/* coefficient of restitution */
	private static final float COR = 0.0f;
	private static final float timeSpeed = 16000000.0f;

	public float mPosX;
	public float mPosY;
	private float mVelX;
	private float mVelY;

	public void updatePosition(float sx, float sy, float sz, long timestamp) {
		float dt = (System.nanoTime() - timestamp) / timeSpeed;
		mVelX += -sx * dt;
		mVelY += -sy * dt;

		mPosX += mVelX * dt;
		mPosY += mVelY * dt;
	}

	public void resolveCollisionWithBounds(float mHorizontalBound,
			float mVerticalBound) {
		if (mPosX > mHorizontalBound) {
			mPosX = mHorizontalBound;
			mVelX = -mVelX * COR;
		} else if (mPosX < -mHorizontalBound) {
			mPosX = -mHorizontalBound;
			mVelX = -mVelX * COR;
		}
		if (mPosY > mVerticalBound) {
			mPosY = mVerticalBound;
			mVelY = -mVelY * COR;
		} else if (mPosY < -mVerticalBound) {
			mPosY = -mVerticalBound;
			mVelY = -mVelY * COR;
		}
	}

}
