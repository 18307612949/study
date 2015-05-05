package com.yangyueyue.demo.luyin;

import java.io.File;
import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sixin.speex.SpeexTool;
import com.yangyueyue.demo.luyin.tools.GetSystemDateTime;
import com.yangyueyue.demo.luyin.tools.SDcardTools;
import com.yangyueyue.demo.luyin.tools.ShowDialog;
import com.yangyueyue.demo.luyin.tools.StringTools;

public class LuYinActivity extends Activity {
	private Button buttonStart; // ��ʼ��ť
	private Button buttonStop; // ֹͣ��ť
	private Button buttonDeleted; // ɾ����ť
	private TextView textViewLuYinState; // ¼��״̬
	private ListView listViewAudio; // ��ʾ¼���ļ���list
	private ArrayAdapter<String> adaperListAudio; // �б�

	private String fileAudioName; // �������Ƶ�ļ�������
	private MediaRecorder mediaRecorder; // ¼������
	private String filePath; // ��Ƶ������ļ�·��
	private List<String> listAudioFileName; // ��Ƶ�ļ��б�
	private boolean isLuYin; // �Ƿ���¼�� true �� false��
	private File fileAudio; // ¼���ļ�
	private File fileAudioList; //�б��е� ¼���ļ�
	File dir; //¼���ļ�
	private boolean isSpeex = true;
	private boolean isOpenEdit = false;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		// ��ʼ�����
		initView();
		// ��ʼ������
		initData();
		// �������
		setView();
		// �����¼�
		setEvent();

	}

	/* **********************************************************************
	 * 
	 * ��ʼ�����
	 */
	private void initView() {
		// ��ʼ
		buttonStart = (Button) findViewById(R.id.button_start);
		// ֹͣ
		buttonStop = (Button) findViewById(R.id.button_stop);
		// ɾ��
		buttonDeleted = (Button) findViewById(R.id.button_delete);
		// ¼��״̬
		textViewLuYinState = (TextView) findViewById(R.id.text_luyin_state);
		// ��ʾ¼���ļ����б�
		listViewAudio = (ListView) findViewById(R.id.listViewAudioFile);

	}

	/* ******************************************************************
	 * 
	 * ��ʼ������
	 */
	private void initData() {
		if (!SDcardTools.isHaveSDcard()) {
			Toast.makeText(LuYinActivity.this, "�����SD���Ա�洢¼��", Toast.LENGTH_LONG).show();
			return;
		}

		// Ҫ������ļ���·��
		filePath = SDcardTools.getSDPath() + "/" + "myAudio";
		// ʵ�����ļ���
		dir = new File(filePath);
		if (!dir.exists()) {
			// ����ļ��в����� �򴴽��ļ���
			dir.mkdir();
		}
		Log.i("test", "Ҫ�����¼�����ļ���Ϊ" + fileAudioName + "·��Ϊ" + filePath);
		if (isSpeex) {
			listAudioFileName = SDcardTools.getFileFormSDcard(dir, ".spx");
		} else {
			listAudioFileName = SDcardTools.getFileFormSDcard(dir, ".mp3");
		}
		adaperListAudio = new ArrayAdapter<String>(LuYinActivity.this, android.R.layout.simple_list_item_1, listAudioFileName);
	}

	/* **************************************************************
	 * 
	 * �������
	 */
	private void setView() {
		buttonStart.setEnabled(true);
		buttonStop.setEnabled(false);
		buttonDeleted.setEnabled(false);
		listViewAudio.setAdapter(adaperListAudio);

	}

	/* ***********************************************************************
	 * 
	 * �����¼�
	 */
	private void setEvent() {

		// ��ʼ��ť
		buttonStart.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isSpeex) {
					startSpeexAudio();
				} else {
					startAudio();
				}
			}
		});

		// ֹͣ��ť
		buttonStop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isSpeex) {
					stopSpeexAudio();
				} else {
					stopAudion();
				}
			}
		});

		// ɾ����ť
		buttonDeleted.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (fileAudio != null) {
					showDeleteAudioDialog("�Ƿ�ɾ��" + fileAudioName + "�ļ�", "��ɾ��", "ɾ��", false);
				} else {
					ShowDialog.showTheAlertDialog(LuYinActivity.this, "���ļ�������");
				}
			}
		});

		//�ļ��б����¼�
		listViewAudio.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				String fileAudioNameList = ((TextView) arg1).getText().toString();
				fileAudioList = new File(filePath + "/" + fileAudioNameList);
				String name = fileAudioList.getAbsolutePath();
				if (!isOpenEdit) {
					if (isSpeex) {
						SpeexTool.playMusic(LuYinActivity.this, name);
					} else {
						openFile(fileAudioList);
					}
				} else {
					try {
						Intent intent = new Intent(Intent.ACTION_EDIT, Uri.parse(name));
						//			            intent.putExtra("was_get_content_intent",
						//			                    mWasGetContentIntent);
						intent.setClassName("com.ringdroid", "com.ringdroid.RingdroidEditActivity");
						startActivityForResult(intent, 1);
					} catch (Exception e) {
						Log.e("Ringdroid", "Couldn't start editor");
					}
				}

			}
		});
		//�ļ��б�ĳ���ɾ���¼�
		listViewAudio.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				Log.i("test", "�����¼�ִ����");
				String fileAudioNameList = ((TextView) arg1).getText().toString();
				fileAudioList = new File(filePath + "/" + fileAudioNameList);
				openFile(fileAudioList);
				if (fileAudioList != null) {
					fileAudio = fileAudioList;
					fileAudioName = fileAudioNameList;
					showDeleteAudioDialog("�Ƿ�ɾ��" + fileAudioName + "�ļ�", "��ɾ��", "ɾ��", false);
				} else {
					ShowDialog.showTheAlertDialog(LuYinActivity.this, "���ļ�������");
				}
				return false;
			}
		});

	}

	/* ****************************************************************
	 * 
	 * ��ʼ¼��
	 */
	private void startAudio() {
		// ����¼��Ƶ�ļ�
		// ���ִ�����ʽ���ɵ��ļ����������
		fileAudioName = "audio" + GetSystemDateTime.now() + StringTools.getRandomString(2) + ".mp3";
		mediaRecorder = new MediaRecorder();
		//MediaRecorder.AudioSource.CAMCORDER �ⲿmic¼��
		// ����¼������ԴΪ��˷�
		mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
		mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
		mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
		mediaRecorder.setOutputFile(filePath + "/" + fileAudioName);
		try {
			mediaRecorder.prepare();
			mediaRecorder.start();
			textViewLuYinState.setText("¼���С�����");

			fileAudio = new File(filePath + "/" + fileAudioName);
			buttonStart.setEnabled(false);
			buttonStop.setEnabled(true);
			buttonDeleted.setEnabled(false);
			listViewAudio.setEnabled(false);
			isLuYin = true;
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/* ****************************************************************
	 * 
	 * ��ʼ¼��
	 */
	private void startSpeexAudio() {
		// ����¼��Ƶ�ļ�
		// ���ִ�����ʽ���ɵ��ļ����������
		fileAudioName = "audio" + GetSystemDateTime.now() + StringTools.getRandomString(2) + ".spx";
		String name = filePath + "/" + fileAudioName;
		try {
			SpeexTool.start(name);
			textViewLuYinState.setText("¼���С�����");
			buttonStart.setEnabled(false);
			buttonStop.setEnabled(true);
			buttonDeleted.setEnabled(false);
			listViewAudio.setEnabled(false);
			isLuYin = true;
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (null != adaperListAudio) {
			adaperListAudio.notifyDataSetChanged();
		}
	}

	/* ******************************************************
	 * 
	 * ֹͣ¼��
	 */
	private void stopAudion() {
		if (null != mediaRecorder) {
			// ֹͣ¼��
			mediaRecorder.stop();
			mediaRecorder.release();
			mediaRecorder = null;
			textViewLuYinState.setText("¼��ֹͣ��");

			// ��ʼ���ܹ�����
			buttonStart.setEnabled(true);
			buttonStop.setEnabled(false);
			listViewAudio.setEnabled(true);
			// ɾ�����ܰ���
			buttonDeleted.setEnabled(true);
			adaperListAudio.add(fileAudioName);

		}
	}

	/* ******************************************************
	 * 
	 * ֹͣ¼��
	 */
	private void stopSpeexAudio() {
		if (null != SpeexTool.recorderInstance) {
			// ֹͣ¼��
			SpeexTool.stop();
			textViewLuYinState.setText("¼��ֹͣ��");

			// ��ʼ���ܹ�����
			buttonStart.setEnabled(true);
			buttonStop.setEnabled(false);
			listViewAudio.setEnabled(true);
			// ɾ�����ܰ���
			buttonDeleted.setEnabled(true);
			adaperListAudio.add(fileAudioName);

		}
	}

	/*******************************************************************************************************
	 * 
	 * �Ƿ�ɾ��¼���ļ�
	 * 
	 * @param messageString
	 *            //�Ի������
	 * @param button1Title
	 *            //��һ����ť������
	 * @param button2Title
	 *            //�ڶ�����ť������
	 * @param isExit
	 *            //�Ƿ����˳�����
	 */
	public void showDeleteAudioDialog(String messageString, String button1Title, String button2Title, final boolean isExit) {
		AlertDialog dialog = new AlertDialog.Builder(LuYinActivity.this).create();
		dialog.setTitle("��ʾ");
		dialog.setMessage(messageString);
		dialog.setButton(button1Title, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				if (isExit) {
					dialog.dismiss();
					System.exit(0);
				}
			}
		});
		dialog.setButton2(button2Title, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				fileAudio.delete();
				adaperListAudio.remove(fileAudioName);
				fileAudio = null;
				buttonDeleted.setEnabled(false);

				if (isExit) {
					dialog.dismiss();
					System.exit(0);
				}
			}
		});

		dialog.show();
	}

	/*** *************************************************************************************
	 * 
	 * �򿪲���¼���ļ��ĳ���
	 * @param f
	 */
	private void openFile(File f) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		String type = getMIMEType(f);
		intent.setDataAndType(Uri.fromFile(f), type);
		startActivity(intent);
	}

	private String getMIMEType(File f) {
		String end = f.getName().substring(f.getName().lastIndexOf(".") + 1, f.getName().length()).toLowerCase();
		String type = "";
		if (end.equals("mp3") || end.equals("aac") || end.equals("aac") || end.equals("amr") || end.equals("mpeg") || end.equals("mp4")) {
			type = "audio";
		} else if (end.equals("jpg") || end.equals("gif") || end.equals("png") || end.equals("jpeg")) {
			type = "image";
		} else {
			type = "*";
		}
		type += "/*";
		return type;
	}

	/**
	 * ********************************************************
	 * 
	 * ������ֹͣ��ʱ��
	 */
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		if (null != mediaRecorder && isLuYin) {
			// ֹͣ¼��
			mediaRecorder.stop();
			mediaRecorder.release();
			mediaRecorder = null;

			buttonStart.setEnabled(true);
			buttonStop.setEnabled(false);
			listViewAudio.setEnabled(true);
			buttonDeleted.setEnabled(false);
		}
		super.onStop();
	}

	/**
	 * 
	 * 
	 *********************************************************************** 
	 * ����˳���ťʱ
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (null != mediaRecorder && isLuYin) {
				if (fileAudio != null) {
					showDeleteAudioDialog("�Ƿ񱣴�" + fileAudioName + "�ļ�", "����", "������", true);
				} else {
					ShowDialog.showTheAlertDialog(LuYinActivity.this, "���ļ�������");
				}
			} else {
				System.exit(0);
			}
		}
		return true;
	}
}