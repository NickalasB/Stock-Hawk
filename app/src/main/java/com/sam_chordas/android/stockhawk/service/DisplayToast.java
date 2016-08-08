package com.sam_chordas.android.stockhawk.service;

import android.content.Context;
import android.widget.Toast;

public class DisplayToast implements Runnable {
  private final Context mContext;
  private String mText;

  public DisplayToast(Context mContext, String text) {
    this.mContext = mContext;
    mText = text;
  }

  @Override
  public void run() {
    Toast.makeText(mContext, mText, Toast.LENGTH_LONG).show();
  }
}
