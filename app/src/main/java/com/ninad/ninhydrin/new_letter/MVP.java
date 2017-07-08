package com.ninad.ninhydrin.new_letter;


public class MVP {

    interface ViewToPresenter {
        void SendClicked(String SenderID, String ReceiverID, String message);
        void unSub();
    }

    interface PresenterToModel {
        void sendLetterRequest(String SenderID, String ReceiverID, String message);
        void unSub();
    }

    interface ModelToPresenter {
        void Success();

        void Failure();
    }

    interface PresenterToView {
        void showSuccess();

        void showFailure(String message);
    }
}
