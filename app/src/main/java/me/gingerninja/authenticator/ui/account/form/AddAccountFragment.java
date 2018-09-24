package me.gingerninja.authenticator.ui.account.form;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavOptions;
import me.gingerninja.authenticator.R;
import me.gingerninja.authenticator.databinding.AccountFormFragmentBinding;
import me.gingerninja.authenticator.ui.base.BaseFragment;

public class AddAccountFragment extends BaseFragment<AccountFormFragmentBinding> {

    @Override
    protected void onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState, View root, AccountFormFragmentBinding binding) {
        setupUi(binding, getArguments());
    }

    private void setupUi(AccountFormFragmentBinding binding, Bundle args) {
        AddAccountViewModel viewModel = ViewModelProviders.of(this, viewModelFactory).get(AddAccountViewModel.class);
        viewModel.init(args);
        binding.setViewModel(viewModel);

        viewModel.getNavigationAction().observe(this, singleEvent -> {
            String event = singleEvent.getContentAndMarkHandled();
            if (event != null) {
                switch (event) {
                    case AddAccountViewModel.NAV_ACTION_SAVE:
                        getNavController().navigate(R.id.accountListFragment, args, new NavOptions.Builder().setPopUpTo(R.id.accountListFragment, true).build());
                        break;
                }
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.account_form_fragment;
    }
}
