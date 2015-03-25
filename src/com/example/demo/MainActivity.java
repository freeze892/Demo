package com.example.demo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity implements OnClickListener {
	
	private static final String TAG = "MainActivity";
	//	--	1.�ҵ�XML�ļ���Ԥ����Ŀؼ�
	private EditText mEditText;
	private Spinner mSpinner;
	private Button mButton;
	private TextView tv_ShowTitle;
	
	private String number;
	
	private String serverName;
	private String str_url;
	
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0x01:
				tv_ShowTitle.setText(number);
				
				break;

			default:
				break;
			}
		};
	};
	
	/**
	 * Find View By Id
	 */
	public void findView(){
		
		mButton = (Button) findViewById(R.id.btn_Query);
		mEditText = (EditText) findViewById(R.id.edit_entryPlayName);
		mSpinner = (Spinner) findViewById(R.id.sp_showServerName);
		tv_ShowTitle = (TextView) findViewById(R.id.tv_ShowTitle);
	}
	
	/**
	 * ��Spinner��ֵ
	 * 	
	 * 		ͨ��������
	 * 	
	 */
	public void setDataForSpinner(){
	
		/**
		 * SpinnerAdapter  Interface
		 * 
		 * ArrayAdapter - BaseAdapter - SpinnerAdapter
		 * 
		 * <>  ���� -- ���Ͳ�����
		 * 
		 * @param Context  ������  Activity/Service �̳�Context
		 * @param Resource R.layout.XXX  �����ļ�..--��ԴID
		 * @param TextViewResourceId  �ؼ�Id
		 * @param Object List/[] ����Դ
		 * 
		 */
		
		mSpinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, getResources().getStringArray(R.array.servers)));
	}
	
	
	/**
	 * ���������б���ѡ���¼�
	 */
	public void setSpinnerItemSelected(){
		
		mSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			/**
			 * AdapterView  -- Spinner 
			 * View  		--  ��ѡ�е���
			 * int			--	�±�
			 * long			--	ID
			 */
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				serverName = ((TextView)view).getText().toString();
				Toast.makeText(MainActivity.this, serverName, Toast.LENGTH_SHORT).show();
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	
	/**
	 * Called First
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		findView();
		setDataForSpinner();
		setSpinnerItemSelected();
		mButton.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		//	--	1.��ȡEditText ������
		String playName = mEditText.getText().toString().trim();
		
		
		//	--	2.��֤playName �Ƿ�Ϸ�
		if(TextUtils.isEmpty(playName)){
			Toast.makeText(this, "�ٻ�ʦ���Ʋ�����Ϊ��", Toast.LENGTH_SHORT).show();
			return ;
		}
		
		
		//	--	3.��ȡѡ��ķ���������
		
		Log.i(TAG,playName + "playName" );		
		Log.i(TAG,serverName + "serverName" );
		
		
		str_url = "http://lolbox.duowan.com/playerDetail.php?serverName=[SN]&playerName=[PN]";
		
		//--	4.����ת�����滻ռλ��
		String sName = null;
		String pName = null;
		try {
			sName = URLEncoder.encode(serverName,"UTF-8");
			pName = URLEncoder.encode(playName,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		str_url = str_url.replace("[SN]", sName);
		str_url = str_url.replace("[PN]", pName);
		
		Log.i(TAG, str_url + "�滻��");
		
		new Thread(){
			
			@Override
			public void run() {			
				try{
					//	--	1-.����URL����
					URL url = new URL(str_url);
					//	--	2.������
					URLConnection conn = url.openConnection();
					//	--	3.ͨ��URLConnection���������
					conn.connect();
					//	--	4.��ȡ����Դ
					/**
					 * 
					 * BufferReader ��װ��
					 * InputStreamReader �ֽ���ת�������ַ���
					 * conn.getInputStream ��ȡ�ֽ�������.
					 */
					BufferedReader br= new BufferedReader(new InputStreamReader(conn.getInputStream()));
					
					//	--	ѭ�������ж�ȡ����
					while (true) {
						String temp = br.readLine();
						if (temp == null) {
							break;
						}
						if (temp.contains("<p><em><span")) {
							
							number = temp.substring(temp.indexOf("'>") + 2,temp.indexOf("</span>"));
							Log.i(TAG, temp + "");
							//	--	3.0֮�������UI�̸߳���UI����.
							mHandler.sendEmptyMessage(0x01);
						}
					}
					br.close();
				}catch(Exception e){
					e.printStackTrace();
				}
			};
		}.start();
	}
}

