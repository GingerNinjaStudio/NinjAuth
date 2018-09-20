package me.gingerninja.authenticator.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import me.gingerninja.authenticator.R;
import me.gingerninja.authenticator.databinding.AccountListFragmentBinding;
import me.gingerninja.authenticator.ui.base.BaseFragment;

public class AccountListFragment extends BaseFragment<AccountListFragmentBinding> {
    private AccountListViewModel viewModel;

    @Override
    protected void onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState, View root, AccountListFragmentBinding viewDataBinding) {
        subscribeToUi(viewDataBinding);
    }

    private void subscribeToUi(AccountListFragmentBinding binding) {
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(AccountListViewModel.class);
        binding.setViewModel(viewModel);

        viewModel
                .getNavigationAction()
                .observe(this, rawEvent -> {
                    String event = rawEvent.getContentAndMarkHandled();
                    if (event != null) {
                        switch (event) {
                            case AccountListViewModel.NAV_ADD_ACCOUNT_FROM_CAMERA:
                                getNavController().navigate(R.id.addAccountFromCameraFragment);
                                break;
                        }
                    }
                });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.account_list_fragment;
    }
}