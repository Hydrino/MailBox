package com.ninad.ninhydrin.signup;

class Presenter implements MVP.ViewToPresenter, MVP.ModelToPresenter {

    private MVP.PresenterToView presenterToView;
    private MVP.PresenterToModel presenterToModel;

    Presenter() {
        Model model = new Model(this);
        presenterToModel = model.getPresenterToModel();
    }

    void attach(MVP.PresenterToView presenterToView) {
        this.presenterToView = presenterToView;
    }

    @Override
    public void buttonClicked(String Name, String UserID) {
        if (presenterToView == null)
            return;

        if (Name.equals("") || UserID.equals("")) {
            presenterToView.showError("Empty Field");
        } else {
            presenterToView.showProgressBarAndDisableButton();
            presenterToModel.requestNewUser(Name, UserID);
        }
    }

    @Override
    public void signUpResult(String message, String Name, String ID) {
        if (presenterToView == null)
            return;

        if (message.equals("success"))
            presenterToView.showSuccess(Name, ID);
        else if (message.startsWith("Duplicate entry"))
            presenterToView.showError("User ID already exists");
        else
            presenterToView.showError("Unexpected error, try again later");

        presenterToView.hideProgressBarAndEnableButton();

    }

    @Override
    public void signUpFailure(String message) {
        presenterToView.showError(message);
        presenterToView.hideProgressBarAndEnableButton();
    }

    MVP.ViewToPresenter getViewToPresenter() {
        return this;
    }

    void detach() {
        presenterToView = null;
    }
}
