package com.sixin.speex;

/**
 * Speex��Ƶ������ɼ���
 * @author shidongxue
 *
 */
public interface OnSpeexCompletionListener {
    void onCompletion(SpeexDecoder speexdecoder);
    void onError(Exception ex);
}
