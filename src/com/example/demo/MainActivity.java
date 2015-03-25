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
	//	--	1.找到XML文件中预定义的控件
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
	 * 给Spinner赋值
	 * 	
	 * 		通过适配器
	 * 	
	 */
	public void setDataForSpinner(){
	
		/**
		 * SpinnerAdapter  Interface
		 * 
		 * ArrayAdapter - BaseAdapter - SpinnerAdapter
		 * 
		 * <>  泛型 -- 类型参数化
		 * 
		 * @param Context  上下文  Activity/Service 继承Context
		 * @param Resource R.layout.XXX  布局文件..--资源ID
		 * @param TextViewResourceId  控件Id
		 * @param Object List/[] 数据源
		 * 
		 */
		
		mSpinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, getResources().getStringArray(R.array.servers)));
	}
	
	
	/**
	 * 设置下拉列表项选中事件
	 */
	public void setSpinnerItemSelected(){
		
		mSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			/**
			 * AdapterView  -- Spinner 
			 * View  		--  所选中的项
			 * int			--	下标
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
		
		//	--	1.获取EditText 中内容
		String playName = mEditText.getText().toString().trim();
		
		
		//	--	2.验证playName 是否合法
		if(TextUtils.isEmpty(playName)){
			Toast.makeText(this, "召唤师名称不可以为空", Toast.LENGTH_SHORT).show();
			return ;
		}
		
		
		//	--	3.获取选择的服务器名称
		
		Log.i(TAG,playName + "playName" );		
		Log.i(TAG,serverName + "serverName" );
		
		
		str_url = "http://lolbox.duowan.com/playerDetail.php?serverName=[SN]&playerName=[PN]";
		
		//--	4.编码转换并替换占位符
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
		
		Log.i(TAG, str_url + "替换后");
		
		new Thread(){
			
			@Override
			public void run() {			
				try{
					//	--	1-.构建URL对象
					URL url = new URL(str_url);
					//	--	2.打开连接
					URLConnection conn = url.openConnection();
					//	--	3.通过URLConnection对象打开连接
					conn.connect();
					//	--	4.获取流资源
					/**
					 * 
					 * BufferReader 包装流
					 * InputStreamReader 字节流转换成字字符流
					 * conn.getInputStream 获取字节流对象.
					 */
					BufferedReader br= new BufferedReader(new InputStreamReader(conn.getInputStream()));
					
					//	--	循环从流中读取内容
					while (true) {
						String temp = br.readLine();
						if (temp == null) {
							break;
						}
						if (temp.contains("<p><em><span")) {
							
							number = temp.substring(temp.indexOf("'>") + 2,temp.indexOf("</span>"));
							Log.i(TAG, temp + "");
							//	--	3.0之后不允许非UI线程更改UI界面.
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

