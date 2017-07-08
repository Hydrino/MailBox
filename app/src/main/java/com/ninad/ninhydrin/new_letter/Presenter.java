package com.ninad.ninhydrin.new_letter;

class Presenter implements MVP.ViewToPresenter, MVP.ModelToPresenter {


    private MVP.PresenterToView presenterToView;
    private MVP.PresenterToModel presenterToModel;

    Presenter() {
        Model model = new Model(this);
        presenterToModel = model.getPresenterToModel();
    }

    void attach(MVP.PresenterToView presenterToView1) {
        presenterToView = presenterToView1;
    }

    void detach() {
        presenterToView = null;
    }

    @Override
    public void SendClicked(String SenderID, String ReceiverID, String message) {

        if (presenterToView == null)
            return;

        if (!SenderID.equals("") && !ReceiverID.equals("") && !message.equals(""))
            presenterToModel.sendLetterRequest(SenderID, ReceiverID, message);
        else
            presenterToView.showFailure("Field is empty");
    }

    @Override
    public void unSub() {
        presenterToModel.unSub();
    }

    @Override
    public void Success() {
        if (presenterToView != null)
            presenterToView.showSuccess();
    }

    @Override
    public void Failure() {
        if (presenterToView != null)
            presenterToView.showFailure("Something messed up,try again!");
    }

    MVP.ViewToPresenter getViewToPresenter() {
        return this;
    }
}
