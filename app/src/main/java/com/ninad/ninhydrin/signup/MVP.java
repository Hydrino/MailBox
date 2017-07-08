package com.ninad.ninhydrin.signup;

class MVP {

    interface ViewToPresenter {
        void buttonClicked(String Name, String UserID);
    }

    interface PresenterToModel {
        void requestNewUser(String Name, String UserID);
    }

    interface ModelToPresenter {
        void signUpResult(String message, String Name, String ID);

        void signUpFailure(String message);
    }

    interface PresenterToView {
        void showSuccess(String Name, String ID);

        void showProgressBarAndDisableButton();

        void hideProgressBarAndEnableButton();

        void showError(String message);
    }
}
