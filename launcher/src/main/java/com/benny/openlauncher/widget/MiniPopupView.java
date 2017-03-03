package com.benny.openlauncher.widget;

import android.content.Context;
import android.media.AudioManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.SeekBar;
import android.widget.TextView;

import com.benny.openlauncher.R;
import com.benny.openlauncher.util.LauncherAction;
import com.benny.openlauncher.util.Tool;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.codetail.widget.RevealFrameLayout;

/**
 * Created by BennyKok on 3/3/2017.
 */

public class MiniPopupView extends RevealFrameLayout {
    private boolean haveWidowDisplayed;

    public MiniPopupView(Context context) {
        super(context);
    }

    public MiniPopupView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MiniPopupView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (haveWidowDisplayed) {
            closeAllWindow();

            haveWidowDisplayed = false;

            return true;
        }
        return super.onTouchEvent(event);
    }

    public void showActionWindow(LauncherAction.Action action, float x, float y) {
        View window = null;
        switch (action) {
            case LockScreen:
                break;
            case ClearRam:
                window = LayoutInflater.from(getContext()).inflate(R.layout.window_clearam, this, false);
                ClearRamViewHolder clearRamViewHolder = new ClearRamViewHolder(window);
                clearRamViewHolder.availableRam.setText(clearRamViewHolder.availableRam.getText() + Tool.getFreeRAM(getContext()));
                clearRamViewHolder.availableStorage.setText(clearRamViewHolder.availableStorage.getText() + Tool.getFreeMemory());
                break;
            case SetWallpaper:
                break;
            case DeviceSettings:
                break;
            case LauncherSettings:
                break;
            case ThemePicker:
                break;
            case VolumeDialog:
                AudioManager audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
                window = LayoutInflater.from(getContext()).inflate(R.layout.widow_volume, this, false);
                VolumeDialogViewHolder volumeDialogViewHolder = new VolumeDialogViewHolder(window);
                volumeDialogViewHolder.sbRingtone.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_RING));
                volumeDialogViewHolder.sbNotification.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION));
                volumeDialogViewHolder.sbSystem.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM));
                volumeDialogViewHolder.sbMedia.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));

                volumeDialogViewHolder.sbRingtone.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_RING));
                volumeDialogViewHolder.sbNotification.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION));
                volumeDialogViewHolder.sbSystem.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM));
                volumeDialogViewHolder.sbMedia.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));

                volumeDialogViewHolder.sbRingtone.setOnSeekBarChangeListener(VolumeDialogViewHolder.getSoundChangeListener(audioManager, AudioManager.STREAM_RING));
                volumeDialogViewHolder.sbNotification.setOnSeekBarChangeListener(VolumeDialogViewHolder.getSoundChangeListener(audioManager, AudioManager.STREAM_NOTIFICATION));
                volumeDialogViewHolder.sbSystem.setOnSeekBarChangeListener(VolumeDialogViewHolder.getSoundChangeListener(audioManager, AudioManager.STREAM_SYSTEM));
                volumeDialogViewHolder.sbMedia.setOnSeekBarChangeListener(VolumeDialogViewHolder.getSoundChangeListener(audioManager, AudioManager.STREAM_MUSIC));
                break;
        }
        displayWindow(window, x, y);
    }

    public void closeAllWindow() {
        displayWindow(null, 0, 0);
    }

    private void displayWindow(final View window, final float x, final float y) {
        if (window == null) {
            removeAllViews();
            return;
        }

        window.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (window != null) {
                    window.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                    //window.setX(x);
                    if (y + window.getHeight() / 2 > MiniPopupView.this.getHeight()) {
                        window.setY(MiniPopupView.this.getHeight() - window.getHeight() - ((MarginLayoutParams) window.getLayoutParams()).bottomMargin);
                    } else {
                        float yy = y - window.getHeight() / 2;
                        window.setY(yy);
                    }
                }
            }
        });
        haveWidowDisplayed = true;
        addView(window);
    }

    static class VolumeDialogViewHolder {
        @InjectView(R.id.sb_ringtone)
        SeekBar sbRingtone;
        @InjectView(R.id.sb_notification)
        SeekBar sbNotification;
        @InjectView(R.id.sb_system)
        SeekBar sbSystem;
        @InjectView(R.id.sb_media)
        SeekBar sbMedia;

        VolumeDialogViewHolder(View view) {
            ButterKnife.inject(this, view);
        }

        private static SeekBar.OnSeekBarChangeListener getSoundChangeListener(final AudioManager audioManager, final int type) {
            return new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    audioManager.setStreamVolume(type, progress, AudioManager.FLAG_PLAY_SOUND);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            };
        }
    }

    static class ClearRamViewHolder {
        @InjectView(R.id.available_ram)
        TextView availableRam;
        @InjectView(R.id.available_storage)
        TextView availableStorage;

        ClearRamViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
