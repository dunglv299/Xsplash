package com.teusoft.facesplash.customcontrol;

public class ItemAbout {
	public ItemAbout(String title, int icon) {
		super();
		this.title = title;
		this.icon = icon;
	}

	private String title;
	private int icon;

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the icon
	 */
	public int getIcon() {
		return icon;
	}

	/**
	 * @param icon
	 *            the icon to set
	 */
	public void setIcon(int icon) {
		this.icon = icon;
	}

}
