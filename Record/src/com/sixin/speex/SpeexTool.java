package com.sixin.speex;

import java.io.File;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioManager;

public class SpeexTool {
	//¼��
	public static SpeexRecorder recorderInstance = null;
	// Speex��������
	public static SpeexPlayer mSpeexPlayer = null;

	/**
	 * ��ʼ¼��
	 * 
	 * @param name ��Ƶ���·��
	 */
	public static void start(String name) {
		// speex¼��
		if (recorderInstance != null) {
			recorderInstance.setRecording(false);
			recorderInstance = null;
		}
		if (recorderInstance == null) {
			recorderInstance = new SpeexRecorder(name);
			Thread th = new Thread(recorderInstance);
			th.start();
		}

		recorderInstance.setRecording(true);
	}

	/**
	 * ֹͣ¼�������Ƿ�ɾ���ļ�
	 * 
	 * @param del
	 * @param filename
	 */
	public static void stop(boolean del, final String filename) {
		// speex¼��
		stop();
		if (del) {
			new Thread((new Runnable() {
				@Override
				public void run() {
					if (filename != null && filename.length() > 0) {
						File file = new File(filename);
						if (file.exists()) {
							file.delete();
						}
					}
				}
			})).start();
		}
	}

	/**
	 * ֹͣ¼��
	 * 
	 */
	public static void stop() {
		// speex¼��
		if (recorderInstance != null) {
			recorderInstance.setRecording(false);
			recorderInstance = null;
		}
	}

	/**
	 * ��������
	 * 
	 * @param name
	 * @param chatmsgid
	 */
	public static void playMusic(Context context, String name) {
		try {
			// �����speex¼��
			if (name != null && name.endsWith(".spx")) {
				if (mSpeexPlayer != null && mSpeexPlayer.isPlay) {
					stopMusic(context);
				} else {
					muteAudioFocus(context, true);
					mSpeexPlayer = new SpeexPlayer(name, new OnSpeexCompletionListener() {
						@Override
						public void onError(Exception ex) {
							System.out.println("���Ŵ���");
						}

						@Override
						public void onCompletion(SpeexDecoder speexdecoder) {
							System.out.println("�������");
						}
					});
					mSpeexPlayer.startPlay();
				}
			} else {
				System.out.println("��Ƶ�ļ���ʽ����ȷ");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ֹͣ��������
	 */
	public static void stopMusic(Context context) {
		// ֹͣ����¼��
		if (mSpeexPlayer != null && mSpeexPlayer.isPlay) {
			mSpeexPlayer.stopPlay();
			mSpeexPlayer = null;
			muteAudioFocus(context, false);
		}
	}

	@TargetApi(8)
	public static boolean muteAudioFocus(Context context, boolean bMute) {
		if (context == null) {
			return false;
		}
		if (!isBeforeFroyo()) {
			// 2.1���µİ汾��֧�������API��requestAudioFocus��abandonAudioFocus

			System.out.println("ANDROID_LAB Android 2.1 and below can not stop music");
			return false;
		}
		boolean bool = false;
		AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		if (bMute) {
			int result = am.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
			bool = result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
		} else {
			int result = am.abandonAudioFocus(null);
			bool = result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
		}
		System.out.println("ANDROID_LAB pauseMusic bMute=" + bMute + " result=" + bool);
		return bool;
	}

	public static boolean isBeforeFroyo() {
		int sdkVersion = 0;
		try {
			sdkVersion = Integer.valueOf(android.os.Build.VERSION.SDK);
		} catch (NumberFormatException e) {
			sdkVersion = 0;
		}
		if (sdkVersion <= 8) {
			return true;
		}
		return true;
	}
}
