package me.gingerninja.authenticator.ui.account.camera;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.PermissionChecker;
import me.gingerninja.authenticator.R;
import me.gingerninja.authenticator.data.db.entity.Account;
import me.gingerninja.authenticator.databinding.AccountFromCameraFragmentBinding;
import me.gingerninja.authenticator.ui.base.BaseFragment;
import me.gingerninja.authenticator.util.Parser;
import me.gingerninja.authenticator.util.RequestCodes;

public class AddAccountFromCameraFragment extends BaseFragment<AccountFromCameraFragmentBinding> implements Detector.Processor<Barcode> {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState, View root, AccountFromCameraFragmentBinding viewDataBinding) {
    }

    private void openCamera() {
        Context context = getContext();

        if (PermissionChecker.PERMISSION_GRANTED != PermissionChecker.checkSelfPermission(context, Manifest.permission.CAMERA)) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, RequestCodes.PERMISSION_CAMERA);
            return;
        }

        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(context)
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();
        barcodeDetector.setProcessor(this);

        if (barcodeDetector.isOperational()) {

            CameraSource cameraSource = new CameraSource.Builder(context, barcodeDetector)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(1024, 1024)
                    .setAutoFocusEnabled(true)
                    .build();
            try {
                getDataBinding().cameraPreview.start(cameraSource);
            } catch (IOException e) {
                // TODO handle exception
            }
        } else {
            // TODO wait until operational
        }
    }

    private void stopDetection() {
        getDataBinding().cameraPreview.stop();
    }

    @Override
    public void onStart() {
        super.onStart();
        openCamera();
    }

    @Override
    public void onStop() {
        super.onStop();
        getDataBinding().cameraPreview.stop();
    }

    @Override
    public void onDestroyView() {
        getDataBinding().cameraPreview.release();
        super.onDestroyView();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RequestCodes.PERMISSION_CAMERA:
                openCamera();
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.account_from_camera_fragment;
    }

    @Override
    public void release() {

    }

    @Override
    public void receiveDetections(Detector.Detections<Barcode> detections) {
        if (detections.detectorIsOperational()) {
            SparseArray<Barcode> items = detections.getDetectedItems();
            for (int i = 0; i < items.size(); i++) {
                Barcode barcode = items.valueAt(i);
                Log.d("AddAccountFromCamera", "barcode: " + barcode.rawValue + ", valueFormat: " + barcode.valueFormat);
                Account account = Parser.parseUrl(barcode.rawValue);

                if (account != null) {
                    Snackbar.make(getView(), account.getAccountName() + " by " + account.getIssuer() + " found", Snackbar.LENGTH_LONG).show();
                    // TODO found QR code
                    //stopDetection();
                    getNavController().navigateUp();
                }
            }

        } else {
            // TODO show wait for operational status
        }
    }
}