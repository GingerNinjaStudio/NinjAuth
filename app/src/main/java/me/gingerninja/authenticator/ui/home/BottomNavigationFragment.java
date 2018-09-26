package me.gingerninja.authenticator.ui.home;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.MenuRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import me.gingerninja.authenticator.R;

public class BottomNavigationFragment extends BottomSheetDialogFragment implements NavigationView.OnNavigationItemSelectedListener {
    private static final String ARG_MENU_RES_ID = "arg.menu_res_id";

    private BottomNavigationListener listener;

    public static BottomNavigationFragment create(@MenuRes int menuRes) {
        Bundle args = new Bundle(1);
        args.putInt(ARG_MENU_RES_ID, menuRes);

        BottomNavigationFragment fragment = new BottomNavigationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (getTargetFragment() instanceof BottomNavigationListener) {
            listener = (BottomNavigationListener) getTargetFragment();
        } else if (getParentFragment() instanceof BottomNavigationListener) {
            listener = (BottomNavigationListener) getParentFragment();
        } else if (context instanceof BottomNavigationListener) {
            listener = (BottomNavigationListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        listener = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.bottom_nav_fragment, container, false);
        NavigationView navigationView = root.findViewById(R.id.navigation_view);

        if (navigationView != null) {
            assert getArguments() != null;
            navigationView.getMenu().clear();
            navigationView.inflateMenu(getArguments().getInt(ARG_MENU_RES_ID));
            navigationView.setNavigationItemSelectedListener(this);
        }

        return root;
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        dialog.setOnShowListener(dialog1 -> {
            BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialog1;
            FrameLayout bottomSheet = bottomSheetDialog.findViewById(R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                BottomSheetBehavior<FrameLayout> behavior = BottomSheetBehavior.from(bottomSheet);
                behavior.setSkipCollapsed(true);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        return dialog;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        if (listener != null) {
            listener.onBottomNavigationSelected(getTag(), menuItem.getItemId());
        }

        dismiss();

        return false;
    }

    public interface BottomNavigationListener {
        void onBottomNavigationSelected(@Nullable String tag, int id);
    }
}
