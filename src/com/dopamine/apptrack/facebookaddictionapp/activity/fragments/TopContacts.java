package com.dopamine.apptrack.facebookaddictionapp.activity.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.Toast;

import com.dopamine.apptrack.facebookaddictionapp.R;
import com.dopamine.apptrack.facebookaddictionapp.activity.fragments.adapters.ContactView;

public class TopContacts extends Fragment{
	BaseAdapter contactView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    GridView gridview = (GridView) getActivity().findViewById(R.id.contact_grid);
	    contactView = new ContactView(getActivity());
	    gridview.setAdapter(contactView);

	    gridview.setOnItemClickListener(new OnItemClickListener() {
	        @Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	            Toast.makeText(getActivity(), "" + position, Toast.LENGTH_SHORT).show();
	        }
	    });
	    
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View rootView = inflater.inflate(R.layout.contact_grid, container, false);
		return rootView;
	}
	
	@Override
	public View getView(){
		GridView gridview = (GridView) getActivity().findViewById(R.id.contact_grid);
	    gridview.setAdapter(new ContactView(getActivity()));

	    gridview.setOnItemClickListener(new OnItemClickListener() {
	        @Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	            Toast.makeText( getActivity(), "" + position, Toast.LENGTH_SHORT).show();
	        }
	    });
	    
	    
	    return gridview;
	}
}
