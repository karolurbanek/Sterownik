package pl.kurbanek.sterownik2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class List_Custom extends BaseAdapter {
	private String[] data;
	private Context ctx;

	public List_Custom(Context ctx, String[] importeddata) {
		// TODO Auto-generated constructor stub
		this.ctx=ctx;
		this.data=importeddata;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	private class ViewHolderPattern{
		TextView text_w_layoucie;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolderPattern view_holder;
		
		if(convertView==null){
			LayoutInflater inflater=(LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView=inflater.inflate(R.layout.item_on_list, parent, false);

			view_holder=new ViewHolderPattern();
			view_holder.text_w_layoucie=(TextView)convertView.findViewById(R.id.nazwa_maszyny);
			convertView.setTag(view_holder);
		}else{
			view_holder=(ViewHolderPattern)convertView.getTag();
		}
		view_holder.text_w_layoucie.setText(data[position]);
		return convertView;
	}

}
