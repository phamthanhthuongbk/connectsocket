package Data;

import android.graphics.Bitmap;

public class ItemsInfo {
	int id;
	public String title = null;
	public String author = null;
	public Bitmap bitmap = null;
	public void setItemsInfo(int id, String title, String author)
	{
		this.id = id;
		this.author = author;
		this.title = title;
	}
	
	public ItemsInfo()
	{
		this.title = null;
		this.author = null;
		this.bitmap = null;
	}
}
