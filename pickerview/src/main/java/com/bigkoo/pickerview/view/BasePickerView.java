package com.bigkoo.pickerview.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import com.bigkoo.pickerview.R;
import com.bigkoo.pickerview.configure.PickerOptions;
import com.bigkoo.pickerview.listener.OnDismissListener;
import com.bigkoo.pickerview.utils.PickerViewAnimateUtil;

/**
 * Created by Sai on 15/11/22.
 * Fine imitation of iOSPickerViewController control
 */
public class BasePickerView {
    private Context context;
    protected ViewGroup contentContainer;
    private ViewGroup rootView;
    private ViewGroup dialogView;

    protected PickerOptions mPickerOptions;
    private OnDismissListener onDismissListener;
    private boolean dismissing;

    private Animation outAnim;
    private Animation inAnim;
    private boolean isShowing;

    protected int animGravity = Gravity.BOTTOM;

    private Dialog mDialog;
    protected View clickView;//Which View pops up through
    private boolean isAnim = true;

    public BasePickerView(Context context) {
        this.context = context;
    }

    protected void initViews() {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM);

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        if (isDialog()) {
            dialogView = (ViewGroup) layoutInflater.inflate(R.layout.layout_basepickerview, null, false);
            dialogView.setBackgroundColor(Color.TRANSPARENT);
            //This is the parent layout that actually loads the selector
            contentContainer = (ViewGroup) dialogView.findViewById(R.id.content_container);
            //Settings dialog box Default left and right spacing screen 30
            params.leftMargin = 30;
            params.rightMargin = 30;
            contentContainer.setLayoutParams(params);

            createDialog();
            //Set a click event for the background so that the interface will be closed when clicking outside the content
            dialogView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });
        } else {
            //If you just want to display it at the bottom of the screen
            if (mPickerOptions.decorView == null) {
                if (context instanceof Activity) {
                    mPickerOptions.decorView = (ViewGroup) ((Activity) context).getWindow().getDecorView();
                } else if (context instanceof ContextThemeWrapper && ((ContextThemeWrapper) context).getBaseContext() instanceof Activity) {
                    // When showing from inside an AppCompatDialog, context will be a ContextThemeWrapper
                    mPickerOptions.decorView = (ViewGroup) ((Activity) ((ContextThemeWrapper) context).getBaseContext()).getWindow().getDecorView();
                }
            }
            //Add controls to decorView
            rootView = (ViewGroup) layoutInflater.inflate(R.layout.layout_basepickerview, mPickerOptions.decorView, false);
            rootView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            if (mPickerOptions.outSideColor != -1) {
                rootView.setBackgroundColor(mPickerOptions.outSideColor);
            }
            //This is the parent layout that actually loads the time picker
            contentContainer = (ViewGroup) rootView.findViewById(R.id.content_container);
            contentContainer.setLayoutParams(params);
        }
        setKeyBackCancelable(true);
    }

    protected void initAnim() {
        inAnim = getInAnimation();
        outAnim = getOutAnimation();
    }

    protected void initEvents() {
    }

    /**
     * @param v      (Which View pops up through)
     * @param isAnim Whether to display animation effects
     */
    public void show(View v, boolean isAnim) {
        this.clickView = v;
        this.isAnim = isAnim;
        show();
    }

    public void show(boolean isAnim) {
        show(null, isAnim);
    }

    public void show(View v) {
        this.clickView = v;
        show();
    }

    /**
     * Add View to root view
     */
    public void show() {
        if (isDialog()) {
            showDialog();
        } else {
            if (isShowing()) {
                return;
            }
            isShowing = true;
            onAttached(rootView);
            rootView.requestFocus();
        }
    }

    /**
     * Called during show
     *
     * @param view the view
     */
    private void onAttached(View view) {
        mPickerOptions.decorView.addView(view);
        if (isAnim) {
            contentContainer.startAnimation(inAnim);
        }
    }

    /**
     * Check whether the View has been added to the root view
     *
     * @return This View returns true if the view already exists
     */
    public boolean isShowing() {
        if (isDialog()) {
            return false;
        } else {
            return rootView.getParent() != null || isShowing;
        }
    }

    public void dismiss() {
        if (isDialog()) {
            dismissDialog();
        } else {
            if (dismissing) {
                return;
            }

            if (isAnim) {
                //Disappear animation
                outAnim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        dismissImmediately();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
                contentContainer.startAnimation(outAnim);
            } else {
                dismissImmediately();
            }
            dismissing = true;
        }
    }

    public void dismissImmediately() {
        mPickerOptions.decorView.post(new Runnable() {
            @Override
            public void run() {
                //Remove from root view
                mPickerOptions.decorView.removeView(rootView);
                isShowing = false;
                dismissing = false;
                if (onDismissListener != null) {
                    onDismissListener.onDismiss(BasePickerView.this);
                }
            }
        });
    }

    private Animation getInAnimation() {
        int res = PickerViewAnimateUtil.getAnimationResource(this.animGravity, true);
        return AnimationUtils.loadAnimation(context, res);
    }

    private Animation getOutAnimation() {
        int res = PickerViewAnimateUtil.getAnimationResource(this.animGravity, false);
        return AnimationUtils.loadAnimation(context, res);
    }

    public BasePickerView setOnDismissListener(OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
        return this;
    }

    public void setKeyBackCancelable(boolean isCancelable) {
        ViewGroup View;
        if (isDialog()) {
            View = dialogView;
        } else {
            View = rootView;
        }

        View.setFocusable(isCancelable);
        View.setFocusableInTouchMode(isCancelable);
        if (isCancelable) {
            View.setOnKeyListener(onKeyBackListener);
        } else {
            View.setOnKeyListener(null);
        }
    }

    private View.OnKeyListener onKeyBackListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == MotionEvent.ACTION_DOWN && isShowing()) {
                dismiss();
                return true;
            }
            return false;
        }
    };

    protected BasePickerView setOutSideCancelable(boolean isCancelable) {
        if (rootView != null) {
            View view = rootView.findViewById(R.id.outmost_container);

            if (isCancelable) {
                view.setOnTouchListener(onCancelableTouchListener);
            } else {
                view.setOnTouchListener(null);
            }
        }

        return this;
    }

    /**
     * Set whether the dialog mode can be canceled by clicking outside
     */
    public void setDialogOutSideCancelable() {
        if (mDialog != null) {
            mDialog.setCancelable(mPickerOptions.cancelable);
        }
    }

    /**
     * Called when the user touch on black overlay, in order to dismiss the dialog.
     */
    private final View.OnTouchListener onCancelableTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                dismiss();
            }
            return false;
        }
    };

    public View findViewById(int id) {
        return contentContainer.findViewById(id);
    }

    public void createDialog() {
        if (dialogView != null) {
            mDialog = new Dialog(context, R.style.custom_dialog2);
            mDialog.setCancelable(mPickerOptions.cancelable);//You cannot click outside to cancel, nor can you click back to cancel.
            mDialog.setContentView(dialogView);

            Window dialogWindow = mDialog.getWindow();
            if (dialogWindow != null) {
                dialogWindow.setWindowAnimations(R.style.picker_view_scale_anim);
                dialogWindow.setGravity(Gravity.CENTER);//Can be changed to Bottom
            }

            mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    if (onDismissListener != null) {
                        onDismissListener.onDismiss(BasePickerView.this);
                    }
                }
            });
        }
    }

    private void showDialog() {
        if (mDialog != null) {
            mDialog.show();
        }
    }

    private void dismissDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    public ViewGroup getDialogContainerLayout() {
        return contentContainer;
    }

    public Dialog getDialog() {
        return mDialog;
    }

    public boolean isDialog() {
        return false;
    }
}
