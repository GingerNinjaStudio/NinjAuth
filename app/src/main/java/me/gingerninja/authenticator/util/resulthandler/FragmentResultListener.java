package me.gingerninja.authenticator.util.resulthandler;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;

import timber.log.Timber;

public interface FragmentResultListener {
    int RESULT_OK = Activity.RESULT_OK;
    int RESULT_CANCELED = Activity.RESULT_CANCELED;

    static void registerNavController(FragmentActivity activity, NavController navController) {
        activity.getSupportFragmentManager().registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
            @Override
            public void onFragmentStarted(@NonNull FragmentManager fm, @NonNull Fragment f) {
                if (f instanceof FragmentResultListener) {
                    FragmentResultViewModel root = ResultViewModelProvider.of(f.getActivity()).get(FragmentResultViewModel.class);
                    FragmentInstanceViewModel vm = ResultViewModelProvider.of(f).getIfExists(FragmentInstanceViewModel.class);

                    Timber.v("Started fragment [%s], vm: %s", f.getClass().getSimpleName(), vm);

                    if (vm == null) {
                        return;
                    }

                    ResultStore.Result result = root.getResult(vm.who);

                    if (result != null) {
                        ((FragmentResultListener) f).onFragmentResult(result.getRequestCode(), result.getResultCode(), result.getData());
                    }
                }
            }
        }, true);

        /*navController.addOnDestinationChangedListener((controller, destination, arguments) -> {

        });*/
    }

    default ResultNavController navigateForResult(int requestCode) {
        Fragment source = getFragment();

        if (source.getActivity() == null) {
            throw new IllegalStateException("The fragment initiating navigation for result must be attached to an Activity");
        }

        FragmentResultViewModel rootViewModel = ResultViewModelProvider.of(source.getActivity()).get(FragmentResultViewModel.class);
        FragmentInstanceViewModel instanceViewModel = ResultViewModelProvider.of(source).get(FragmentInstanceViewModel.class);
        rootViewModel.addPendingResultRequest(instanceViewModel.who, requestCode);
        return ResultNavController.wrap(getNavController(), instanceViewModel.who, requestCode);
    }

    /**
     * Transfers the navigation result request to the next Fragment. The use case is the following:
     * <p>
     * {@code A -- navigateForResult --> B -- navigateForResultTransfer --> C -- setResultAndLeave(A) --> A }
     * </p>
     *
     * @return the {@link ResultNavController} used for navigating forward
     * @throws ResultNavController.ParentNotExpectingResultsException if the calling fragment is not expected to return a result
     */
    default ResultNavController navigateForResultTransfer() throws ResultNavController.ParentNotExpectingResultsException {
        Fragment source = getFragment();

        if (source.getActivity() == null) {
            throw new IllegalStateException("The fragment initiating navigation for result must be attached to an Activity");
        }

        return ResultNavController.wrap(getNavController(), source);
    }

    default void setResultAndLeave(int resultCode) {
        setResultAndLeave(resultCode, null);
    }

    default void setResultAndLeave(int resultCode, @Nullable Object data) {
        Fragment source = getFragment();
        if (source.getActivity() == null) {
            throw new IllegalStateException("The fragment initiating navigation for result must be attached to an Activity");
        }

        Bundle args = source.getArguments();
        if (args != null) {
            String who = args.getString(ResultNavController.EXTRA_KEY_TARGET);
            //int requestCode = args.getInt(ResultNavController.EXTRA_KEY_REQUEST_CODE);
            // TODO

            FragmentResultViewModel rootViewModel = ResultViewModelProvider.of(source.getActivity()).get(FragmentResultViewModel.class);
            rootViewModel.setResult(who, resultCode, data);
        }

        boolean navigated = getNavController().popBackStack();
        Timber.w("[NAV] Navigated: %s", navigated);
    }

    default void setResultAndLeave(int resultCode, @IdRes int destinationId, boolean inclusive) {
        setResultAndLeave(resultCode, null, destinationId, inclusive);
    }

    default void setResultAndLeave(int resultCode, @Nullable Object data, @IdRes int destinationId, boolean inclusive) {
        Fragment source = getFragment();
        if (source.getActivity() == null) {
            throw new IllegalStateException("The fragment initiating navigation for result must be attached to an Activity");
        }

        Bundle args = source.getArguments();
        if (args != null) {
            String who = args.getString(ResultNavController.EXTRA_KEY_TARGET);
            //int requestCode = args.getInt(ResultNavController.EXTRA_KEY_REQUEST_CODE);
            // TODO should we propagate the results to the destination?

            FragmentResultViewModel rootViewModel = ResultViewModelProvider.of(source.getActivity()).get(FragmentResultViewModel.class);
            rootViewModel.setResult(who, resultCode, data);
        }

        /*if (inclusive) {
            Timber.w("Cannot set result for unknown destination");
        } else {
            FragmentResultViewModel rootViewModel = ResultViewModelProvider.of(source.getActivity()).get(FragmentResultViewModel.class);
            rootViewModel.setResult(destinationId, resultCode, data);
        }*/

        boolean navigated = getNavController().popBackStack(destinationId, inclusive);
        Timber.w("[NAV] Navigated: %s", navigated);
    }

    default Fragment getFragment() {
        return (Fragment) this;
    }

    NavController getNavController();

    default void onFragmentResult(int requestCode, int resultCode, @Nullable Object data) {
    }
}
