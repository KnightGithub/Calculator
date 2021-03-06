package com.example.tiancai.calculator;

import android.annotation.SuppressLint;//Lint是一个静态检查器，它围绕Android项目的正确性、安全性、性能、可用性以及可访问性进行分析。它检查的对象包括XML资源、位图、ProGuard配置文件、源文件甚至编译后的字节码。
import android.app.Activity;  //创建Activity
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;  //打印Log信息
import android.view.View;  //引用View用到的
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import com.example.tiancai.calculator.InputItem.InputType;
import java.math.BigDecimal;  //用于高精度计算
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//当程序里面有很多的部件需要点击执行动作的时候可以使用implements OnClickListener
public class MainActivity extends Activity implements OnClickListener{
    //private 是私有的只能在当前类里使用
	private TextView mShowResultTv;  //显示计算结果
	private TextView mShowInputTv;   //显示输入的字符
	private Button mCBtn;  //清除键
	private Button mDelBtn;  //后退键
	private Button mAddBtn;  //加
	private Button mSubBtn;  //减
	private Button mMultiplyBtn;   //乘
	private Button mDividebtn;  //除
	private Button mZeroButton;  //0
	private Button mOnebtn;  //1
	private Button mTwoBtn;  //2
	private Button mThreeBtn;  //3
	private Button mFourBtn;  //4
	private Button mFiveBtn;  //5
	private Button mSixBtn;  //6
	private Button mSevenBtn;  //7
	private Button mEightBtn;  //8
	private Button mNineBtn;  //9
	private Button mPointtn;  //点
	private Button mEqualBtn;  //等于

	private Button mFactorial;//阶乘

	private HashMap<View,String> map;  //将View和String映射起来
	private List<InputItem> mInputList;  //定义记录每次输入的数
	private int mLastInputstatus = INPUT_NUMBER;  //记录上一次输入的状态

	public static final int INPUT_NUMBER = 1; 
	public static final int INPUT_POINT = 0;
	public static final int INPUT_OPERATOR = -1;
	public static final int END = -2;
	public static final int ERROR= -3;
	public static final int SHOW_RESULT_DATA = 1;
	public static final String nan = "无解";
	public static final String infinite = "无解";

    //在异步线程里面更新UI的时候使用handler
    //handler是android中为了处理异步线程更新UI的问题而出现的一个工具
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler(){

		public void handleMessage(Message msg) {  //覆盖handleMessage方法
			
			if(msg.what == SHOW_RESULT_DATA){
				mShowResultTv.setText(mShowInputTv.getText());//清除上次运算记录
				mShowInputTv.setText(mInputList.get(0).getInput());//保存你输入的数据
				clearScreen(mInputList.get(0));
			}
		}
	};

	@Override
	//onCreate方法用来实例化Activity对象
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); //调用父类的方法来实现对界面的绘制
		setContentView(R.layout.main); //加载布局
		initView(); 
		initData(); 
	}

	//初始化view
	private void initView() {
		//实现按钮的获取，通过findViewById方法取得布局文件中声明的文件
		mShowResultTv = (TextView) this.findViewById(R.id.show_result_tv);
		mShowInputTv = (TextView)this.findViewById(R.id.show_input_tv);
		mCBtn = (Button)this.findViewById(R.id.c_btn);
		mDelBtn= (Button)this.findViewById(R.id.del_btn);
		mAddBtn= (Button)this.findViewById(R.id.add_btn);
		mMultiplyBtn= (Button)this.findViewById(R.id.multiply_btn);
		mDividebtn= (Button)this.findViewById(R.id.divide_btn);
		mZeroButton = (Button)this.findViewById(R.id.zero_btn);
		mOnebtn= (Button)this.findViewById(R.id.one_btn);
		mTwoBtn= (Button)this.findViewById(R.id.two_btn);
		mThreeBtn= (Button)this.findViewById(R.id.three_btn);
		mFourBtn= (Button)this.findViewById(R.id.four_btn);
		mFiveBtn= (Button)this.findViewById(R.id.five_btn);
		mSixBtn= (Button)this.findViewById(R.id.six_btn);
		mSevenBtn= (Button)this.findViewById(R.id.seven_btn);
		mEightBtn= (Button)this.findViewById(R.id.eight_btn);
		mNineBtn= (Button)this.findViewById(R.id.nine_btn);
		mPointtn= (Button)this.findViewById(R.id.point_btn);
		mEqualBtn= (Button)this.findViewById(R.id.equal_btn);
		mSubBtn = (Button)this.findViewById(R.id.sub_btn);
		mFactorial = (Button)this.findViewById(R.id.factorial_btn);//阶乘
		setOnClickListener(); //调用监听事件
	}

	//初始化数据
	private void initData() {
		if(map == null)
			map = new HashMap<View, String>();
		map.put(mAddBtn,getResources().getString(R.string.add));
		map.put(mSubBtn, getResources().getString(R.string.sub));
		map.put(mMultiplyBtn,getResources().getString(R.string.multiply));
		map.put(mDividebtn,getResources().getString(R.string.divide));
		map.put(mZeroButton ,getResources().getString(R.string.zero));
		map.put(mOnebtn,getResources().getString(R.string.one));
		map.put(mTwoBtn,getResources().getString(R.string.two));
		map.put(mThreeBtn,getResources().getString(R.string.three));
		map.put(mFourBtn,getResources().getString(R.string.four));
		map.put(mFiveBtn,getResources().getString(R.string.five));
		map.put(mSixBtn,getResources().getString(R.string.six));
		map.put(mSevenBtn,getResources().getString(R.string.seven));
		map.put(mEightBtn,getResources().getString(R.string.eight));
		map.put(mNineBtn,getResources().getString(R.string.nine));
		map.put(mPointtn,getResources().getString(R.string.point));
		map.put(mEqualBtn,getResources().getString(R.string.equal));

		map.put(mFactorial,getResources().getString(R.string.factorial));//阶乘

		mInputList = new ArrayList<InputItem>();
		mShowResultTv.setText("");
		clearAllScreen();
	}

	//设置监听事件
	private void setOnClickListener() {
		mCBtn.setOnClickListener(this);
		mDelBtn.setOnClickListener(this);
		mAddBtn.setOnClickListener(this);
		mMultiplyBtn.setOnClickListener(this);
		mDividebtn.setOnClickListener(this);
		mSubBtn.setOnClickListener(this);
		mZeroButton.setOnClickListener(this);
		mOnebtn.setOnClickListener(this);
		mTwoBtn.setOnClickListener(this);
		mThreeBtn.setOnClickListener(this);
		mFourBtn.setOnClickListener(this);
		mFiveBtn.setOnClickListener(this);
		mSixBtn.setOnClickListener(this);
		mSevenBtn.setOnClickListener(this);
		mEightBtn.setOnClickListener(this);
		mNineBtn.setOnClickListener(this);
		mPointtn.setOnClickListener(this);
		mEqualBtn.setOnClickListener(this);
		mFactorial.setOnClickListener(this);//阶乘
	}


	//点击事件
	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.c_btn:
			clearAllScreen();
			break;
		case R.id.del_btn:
			back();
			break;
		case R.id.point_btn:
			inputPoint(arg0);
			break;
		case R.id.equal_btn:
			operator();
			break;
		//阶乘
		case R.id.factorial_btn:
			jiecheng();
			break;
		//加减乘除
		case R.id.add_btn:
		case R.id.sub_btn:
		case R.id.multiply_btn:
		case R.id.divide_btn:
			inputOperator(arg0);
			break;
		default:
			inputNumber(arg0);
			break;
		}
	}

	//点击之后的运算事件
	private void operator() {
		if(mLastInputstatus == END ||mLastInputstatus == ERROR || mLastInputstatus == INPUT_OPERATOR|| mInputList.size()==1){
			return;
		}

		mShowResultTv.setText(mShowResultTv.getText());

		startAnim();
		findHighOperator(0);
		if(mLastInputstatus != ERROR){
			findLowOperator(0);
		}
		mHandler.sendMessageDelayed(mHandler.obtainMessage(SHOW_RESULT_DATA), 300);
	}

	//动画
	private void startAnim(){
		mShowInputTv.setText(mShowInputTv.getText()+getResources().getString(R.string.equal));
		Animation anim = AnimationUtils.loadAnimation(this, R.anim.screen_anim);
		mShowInputTv.startAnimation(anim);
	}

	//阶乘功能
	private void jiecheng(){
        //使用mInputList来保存每次输入的数据
        //比如输入5，inputItem对象封装数据，保存了你按下的5的string值
		InputItem item = mInputList.get(mInputList.size() - 1);
		String input = item.getInput();
        //String转化为double运算
        //double型是为了弥补用整型数表示时精度不够的缺陷，占八个字节，提供15~16位有效数字
		double n = Double.valueOf(input);
		double result = 1;
		//循环连乘
		for (int i = 2; i <= n; i++) {
			result *= i;
		}
		if (result > Double.MAX_VALUE) {
			mShowInputTv.setText("数值太大无法计算");
		} else {
			mShowResultTv.setText(result + "");
		}
	}

	//输入点
	private void inputPoint(View view) {
		if(mLastInputstatus == INPUT_POINT){
			return;
		}
		if(mLastInputstatus == END || mLastInputstatus == ERROR){
			clearInputScreen();
		}
		String key = map.get(view);
		String input = mShowInputTv.getText().toString();
		if(mLastInputstatus == INPUT_OPERATOR){
			input = input+"0";
		} 
		mShowInputTv.setText(input+key);
		addInputList(INPUT_POINT, key);
	}

	//输入数字
	private void inputNumber(View view){
		if(mLastInputstatus == END || mLastInputstatus == ERROR){
			clearInputScreen();
		}
		String key = map.get(view);
		if("0".equals(mShowInputTv.getText().toString())){
			mShowInputTv.setText(key);
		}else{
		mShowInputTv.setText(mShowInputTv.getText() + key);
		}
		addInputList(INPUT_NUMBER, key);
	}

	//输入运算符
	private void inputOperator(View view) {
		if(mLastInputstatus == INPUT_OPERATOR || mLastInputstatus == ERROR){
			return;
		}
		if(mLastInputstatus == END){
			mLastInputstatus = INPUT_NUMBER;
		}

		String key = map.get(view);
		if("0".equals(mShowInputTv.getText().toString())){
			mShowInputTv.setText("0"+key);
			mInputList.set(0,new InputItem("0",InputItem.InputType.INT_TYPE));
		}else{
		mShowInputTv.setText(mShowInputTv.getText() + key);
		}
		addInputList(INPUT_OPERATOR, key);
	}

	//回退操作
	private void back() {
		if(mLastInputstatus == ERROR){
			clearInputScreen();
		}
		String str = mShowInputTv.getText().toString();
		if(str.length() != 1){
			mShowInputTv.setText(str.substring(0, str.length()-1));
			backList();
		}else{
			mShowInputTv.setText(getResources().getString(R.string.zero));
			clearScreen(new InputItem("",InputItem.InputType.INT_TYPE));
		}
	}

	//回退InputList操作
	private void backList() {
		InputItem item = mInputList.get(mInputList.size() - 1);
        Log.i("wpg","item的值:"+item);
		if (item.getType() == InputItem.InputType.INT_TYPE) {

			//获取到最后一个item,并去掉最后一个字符
			String input = item.getInput().substring(0,
					item.getInput().length() - 1);
            Log.i("wpg","最后一个item,并去掉最后一个字符input的值:"+input);

			//"".equals(input)判断input是否为空,返回TRUE或FALSE
			//如果截完了，则移除这个item，并将当前状态改为运算操作符
			if ("".equals(input)) {
				Log.i("wpg","判断是否截完了? input的值:"+input);
				mInputList.remove(item);
                Log.i("wpg","截完了item的值:"+item);
                mLastInputstatus = INPUT_OPERATOR;
                Log.i("wpg","mLastInputstatus的值:"+mLastInputstatus);
            } else {
				//否则设置item为截取完的字符串，并将当前状态改为number
				item.setInput(input);
				Log.i("wpg","否则设置item为截取完的字符串，并将当前状态改为number:"+input);
				mLastInputstatus = INPUT_NUMBER;
			}

			//如果item是运算操作符 则移除。
		} else if (item.getType() == InputItem.InputType.OPERATOR_TYPE) {
			Log.i("wpg","如果item是运算操作符 则移除:"+item);
			mInputList.remove(item);
			Log.i("wpg","如果item是运算操作符 则移除:"+item);
			if (mInputList.get(mInputList.size() - 1).getType() == InputItem.InputType.INT_TYPE) {
				mLastInputstatus = INPUT_NUMBER;
			} else {
				mLastInputstatus = INPUT_POINT;
			}

			//如果当前item是小数
		} else {
			String input = item.getInput().substring(0,
					item.getInput().length() - 1);
			if ("".equals(input)) {
				mInputList.remove(item);
				mLastInputstatus = INPUT_OPERATOR;
			} else {
				if (input.contains(".")) {
					item.setInput(input);
					mLastInputstatus = INPUT_POINT;
				} else {
					item.setInput(input);
					mLastInputstatus = INPUT_NUMBER;
				}
			}
		}
	}

	//清理屏
	private void clearAllScreen() {
		
		clearResultScreen();
		clearInputScreen();
		
	}

	//清理结果屏幕
	//点击mCBtn的时候生效
	private void clearResultScreen(){
		mShowResultTv.setText("");
	}

	//清理输入屏幕
	private void clearInputScreen() {
		mShowInputTv.setText(getResources().getString(R.string.zero));
		mLastInputstatus = INPUT_NUMBER;
		mInputList.clear();
		mInputList.add(new InputItem("", InputItem.InputType.INT_TYPE));
	}

	//计算完成
	private void clearScreen(InputItem item) {
		if(mLastInputstatus != ERROR){
			mLastInputstatus = END;
		}
		mInputList.clear();
		mInputList.add(item);
	}

	//实现高级运算
	public int findHighOperator(int index) {
		if (mInputList.size() > 1 && index >= 0 && index < mInputList.size())
			for (int i = index; i < mInputList.size(); i++) {
					InputItem item = mInputList.get(i);
				if (getResources().getString(R.string.divide).equals(item.getInput())
						|| getResources().getString(R.string.multiply).equals(item.getInput())) {
					int a,b; double c,d;
					if(mInputList.get(i - 1).getType() == InputItem.InputType.INT_TYPE){
						a = Integer.parseInt(mInputList.get(i - 1).getInput());
						if(mInputList.get(i + 1).getType() == InputItem.InputType.INT_TYPE){
							b = Integer.parseInt(mInputList.get(i + 1).getInput());
							if(getResources().getString(R.string.multiply).equals(item.getInput())){
								mInputList.set(i - 1,new InputItem( String.valueOf(a * b),InputItem.InputType.INT_TYPE));
							}else{
								if(b == 0){
									mLastInputstatus = ERROR;
									if(a==0){
										clearScreen(new InputItem(nan,InputType.ERROR));
									}else{
										clearScreen(new InputItem(infinite,InputType.ERROR));
									}
									return -1;
								}else if(a % b != 0){
									mInputList.set(i - 1,new InputItem(String.valueOf((double)a / b),InputItem.InputType.DOUBLE_TYPE));
								}else{
									mInputList.set(i - 1,new InputItem(String.valueOf((Integer)a / b),InputItem.InputType.INT_TYPE));
								}
							}
						}else{
							d = Double.parseDouble(mInputList.get(i + 1).getInput());
							if(getResources().getString(R.string.multiply).equals(item.getInput())){
								mInputList.set(i - 1,new InputItem( String.valueOf(a * d),InputItem.InputType.DOUBLE_TYPE));
							}else{
								if(d == 0){
									mLastInputstatus = ERROR;
									if(a==0){
										clearScreen(new InputItem(nan,InputType.ERROR));
									}else{
										clearScreen(new InputItem(infinite,InputType.ERROR));
									}
									return -1;
								}
								mInputList.set(i - 1,new InputItem(String.valueOf(a / d),InputItem.InputType.DOUBLE_TYPE));	
							}
						}
					}else{
						c = Double.parseDouble(mInputList.get(i-1).getInput());
						if(mInputList.get(i + 1).getType() == InputItem.InputType.INT_TYPE){
							b = Integer.parseInt(mInputList.get(i + 1).getInput());
							if(getResources().getString(R.string.multiply).equals(item.getInput())){
								mInputList.set(i - 1,new InputItem( String.valueOf(c* b),InputItem.InputType.DOUBLE_TYPE));
							}else{
								if(b== 0){
									mLastInputstatus = ERROR;
									if(c==0){
										clearScreen(new InputItem(nan,InputType.ERROR));
									}else{
										clearScreen(new InputItem(infinite,InputType.ERROR));
									}
									return -1;
								}
								mInputList.set(i - 1,new InputItem(String.valueOf(c / b),InputItem.InputType.DOUBLE_TYPE));	
							}
						}else{
							d = Double.parseDouble(mInputList.get(i + 1).getInput());
							if(getResources().getString(R.string.multiply).equals(item.getInput())){
								mInputList.set(i - 1,new InputItem( String.valueOf(mul(c,d)),InputItem.InputType.DOUBLE_TYPE));
							}else{
								if(d == 0){
									mLastInputstatus = ERROR;
									if(c==0){
										clearScreen(new InputItem(nan, InputType.ERROR));
									}else{
										clearScreen(new InputItem(infinite,InputType.ERROR));
									}
									return -1;
								}
								mInputList.set(i - 1,new InputItem(String.valueOf(div(c, d)),InputItem.InputType.DOUBLE_TYPE));	
							}
						}
					}
					mInputList.remove(i + 1);
					mInputList.remove(i);
					return findHighOperator(i);
				}
			}
		return -1;

	}
	
	public int findLowOperator(int index) {
		if (mInputList.size()>1 && index >= 0 && index < mInputList.size())
			for (int i = index; i < mInputList.size(); i++) {
					InputItem item = mInputList.get(i);
				if (getResources().getString(R.string.sub).equals(item.getInput())
						|| getResources().getString(R.string.add).equals(item.getInput())) {
					int a,b; double c,d;
					if(mInputList.get(i - 1).getType() == InputItem.InputType.INT_TYPE){
						a = Integer.parseInt(mInputList.get(i - 1).getInput());
						if(mInputList.get(i + 1).getType() == InputItem.InputType.INT_TYPE){
							b = Integer.parseInt(mInputList.get(i + 1).getInput());
							if(getResources().getString(R.string.add).equals(item.getInput())){
							mInputList.set(i - 1,new InputItem( String.valueOf(a + b),InputItem.InputType.INT_TYPE));
							}else{
							mInputList.set(i - 1,new InputItem(String.valueOf(a - b),InputItem.InputType.INT_TYPE));	
							}
						}else{
							d = Double.parseDouble(mInputList.get(i + 1).getInput());
							if(getResources().getString(R.string.add).equals(item.getInput())){
								mInputList.set(i - 1,new InputItem( String.valueOf(a + d),InputItem.InputType.DOUBLE_TYPE));
							}else{
								mInputList.set(i - 1,new InputItem(String.valueOf(a - d),InputItem.InputType.DOUBLE_TYPE));	
							}
						}
					}else{
						c = Double.parseDouble(mInputList.get(i-1).getInput());
						if(mInputList.get(i + 1).getType() == InputItem.InputType.INT_TYPE){
							b = Integer.parseInt(mInputList.get(i + 1).getInput());
							if(getResources().getString(R.string.add).equals(item.getInput())){
								mInputList.set(i - 1,new InputItem( String.valueOf(c + b),InputItem.InputType.DOUBLE_TYPE));
							}else{
								mInputList.set(i - 1,new InputItem(String.valueOf(c - b),InputItem.InputType.DOUBLE_TYPE));	
							}
						}else{
							d = Double.parseDouble(mInputList.get(i + 1).getInput());
							if(getResources().getString(R.string.add).equals(item.getInput())){
								mInputList.set(i - 1,new InputItem( String.valueOf(add(c, d)),InputItem.InputType.DOUBLE_TYPE));
							}else{
								mInputList.set(i - 1,new InputItem(String.valueOf(sub(c,d)),InputItem.InputType.DOUBLE_TYPE));	
							}
						}
					}
					mInputList.remove(i + 1);
					mInputList.remove(i);
					return findLowOperator(i);
				}
			}
		return -1;

	}

	//currentStatus 当前状态
	void addInputList(int currentStatus,String inputChar){
		switch (currentStatus) {
		case INPUT_NUMBER:
			if(mLastInputstatus == INPUT_NUMBER){
				InputItem item = (InputItem)mInputList.get(mInputList.size()-1);
				item.setInput(item.getInput()+inputChar);
				item.setType(InputItem.InputType.INT_TYPE);
				mLastInputstatus = INPUT_NUMBER;
			}else if(mLastInputstatus == INPUT_OPERATOR){
				InputItem item = new InputItem(inputChar, InputItem.InputType.INT_TYPE);
				mInputList.add(item);
				mLastInputstatus = INPUT_NUMBER;
			}else if(mLastInputstatus == INPUT_POINT){
				InputItem item = (InputItem)mInputList.get(mInputList.size()-1);
				item.setInput(item.getInput()+inputChar);
				item.setType(InputItem.InputType.DOUBLE_TYPE);
				mLastInputstatus = INPUT_POINT;
			}
			break;
		case INPUT_OPERATOR:
				InputItem item = new InputItem(inputChar, InputItem.InputType.OPERATOR_TYPE);
				mInputList.add(item);
				mLastInputstatus = INPUT_OPERATOR;
			break;
		case INPUT_POINT:
			 if(mLastInputstatus == INPUT_OPERATOR){
				 InputItem item1 =  new InputItem("0"+inputChar,InputItem.InputType.DOUBLE_TYPE);
				 mInputList.add(item1); 
				 mLastInputstatus = INPUT_POINT;
			}else{
				InputItem item1 = (InputItem)mInputList.get(mInputList.size()-1);
				item1.setInput(item1.getInput()+inputChar);
				item1.setType(InputItem.InputType.DOUBLE_TYPE);
				mLastInputstatus = INPUT_POINT;
			}
			break;
		}
	}
	
	   public static Double div(Double v1,Double v2){
	        BigDecimal b1 = new BigDecimal(v1.toString());
	        BigDecimal b2 = new BigDecimal(v2.toString());
	        return b1.divide(b2,10,BigDecimal.ROUND_HALF_UP).doubleValue();
	    }
	   
	   public static Double sub(Double v1,Double v2){
	        BigDecimal b1 = new BigDecimal(v1.toString());
	        BigDecimal b2 = new BigDecimal(v2.toString());
	        return b1.subtract(b2).doubleValue();
	    }
	   
	   public static Double add(Double v1,Double v2){
	        BigDecimal b1 = new BigDecimal(v1.toString());
	        BigDecimal b2 = new BigDecimal(v2.toString());
	        return b1.add(b2).doubleValue();
	    }
	   
	   public static Double mul(Double v1,Double v2){
	        BigDecimal b1 = new BigDecimal(v1.toString());
	        BigDecimal b2 = new BigDecimal(v2.toString());
	        return b1.multiply(b2).doubleValue();
	    }
}
