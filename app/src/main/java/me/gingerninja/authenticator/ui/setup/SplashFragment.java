package me.gingerninja.authenticator.ui.setup;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import me.gingerninja.authenticator.R;
import me.gingerninja.authenticator.databinding.SplashFragmentBinding;
import me.gingerninja.authenticator.ui.base.BaseFragment;
import me.gingerninja.authenticator.util.AppSettings;

public class SplashFragment extends BaseFragment<SplashFragmentBinding> implements SkipConfirmationBottomFragment.SkipDialogListener {
    @Inject
    AppSettings appSettings;

    private Runnable animationRunnable;

    @Override
    protected void onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState, View root, SplashFragmentBinding binding) {
        boolean isFirstTime = !appSettings.isFirstRunComplete();

        if (isFirstTime) {
            binding.btnSkipSetup.setOnClickListener(v -> {
                SkipConfirmationBottomFragment.show(getChildFragmentManager());
            });

            binding.btnNext.setOnClickListener(v -> {
                // TODO
            });

            if (savedInstanceState == null) {
                animationRunnable = binding.motionLayout::transitionToEnd;
                binding.motionLayout.postDelayed(animationRunnable, 750);
            } else {
                binding.motionLayout.setState(R.id.end, -1, -1);
            }
        } else {
            getNavController().navigate(R.id.skipSetupToAccountListAction);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (animationRunnable != null) {
            new Handler(Looper.getMainLooper()).removeCallbacks(animationRunnable);
            animationRunnable = null;
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.splash_fragment;
    }

    @Override
    public void onSkipSetup() {
        // TODO remove comment - appSettings.setFirstRunComplete();
        getNavController().navigate(R.id.skipSetupToAccountListAction);
    }
}
