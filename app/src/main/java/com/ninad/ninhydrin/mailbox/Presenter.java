package com.ninad.ninhydrin.mailbox;

import java.util.ArrayList;
import java.util.Locale;

class Presenter implements MVP.ViewToPresenter, MVP.ModelToPresenter {

    private MVP.PresenterToModel presenterToModel;
    private MVP.PresenterToView presenterToView;

    void attach(MVP.PresenterToView presenterToView) {
        this.presenterToView = presenterToView;
    }

    void detach() {
        presenterToView = null;
    }

    Presenter() {
        Model model = new Model(this);
        presenterToModel = model.getPresenterToModel();
    }

    @Override
    public void retrieveNewLetters(String RecipientID) {
        presenterToModel.retrieveNewLettersRequest(RecipientID);
    }

    @Override
    public void retrieveReadLetters(String RecipientID) {
        presenterToModel.retrieveReadLettersRequest(RecipientID);
    }

    @Override
    public void retrieveArchiveLetters(String RecipientID) {
        presenterToModel.retrieveArchiveLettersRequest(RecipientID);
    }

    @Override
    public void LettersShownSuccess() {
        presenterToModel.unSubscribeClient();
    }

    @Override
    public void ReadLetterShownSuccess() {
        presenterToModel.unSubscribeClient();
    }

    @Override
    public void ArchiveLetterShownSuccess() {
        presenterToModel.unSubscribeClient();
    }

    @Override
    public void LetterOpened(int index, int id) {
        presenterToModel.markLetterAsRead(index, id);
        presenterToView.hideFab();
    }

    @Override
    public void LetterSwiped(int index, int id) {
        presenterToModel.markLetterAsArchive(index, id);
    }

    @Override
    public void retrieveNewLettersFailed() {
        if (presenterToView != null)
            presenterToView.retrieveNewLettersFailed();
    }

    @Override
    public void retrieveReadLettersFailed() {
        if (presenterToView != null)
            presenterToView.retrieveReadLettersFailed();
    }

    @Override
    public void retrieveArchiveLettersFailed() {
        if (presenterToView != null)
            presenterToView.retrieveArchiveLettersFailed();
    }

    @Override
    public void retrieveNewLettersSuccess(ArrayList<Model.Letter> letters) {
        if (presenterToView == null)
            return;

        if (letters.size() == 0)
            presenterToView.retrieveNewLetterSuccess(letters, "No new letters today");
        else
            presenterToView.retrieveNewLetterSuccess
                    (letters, String.format(Locale.ENGLISH, "You have %1$d new letters", letters.size()));

    }

    @Override
    public void retrieveReadLettersSuccess(ArrayList<Model.Letter> readLetters) {
        if (presenterToView == null)
            return;

        if (readLetters.size() == 0)
            presenterToView.retrieveReadLettersSuccess(readLetters, "No read letters ");
        else
            presenterToView.retrieveReadLettersSuccess
                    (readLetters, String.format(Locale.ENGLISH,
                            "You have %1$d read letters", readLetters.size()));
    }

    @Override
    public void retrieveArchiveLettersSuccess(ArrayList<Model.Letter> archiveLetters) {

        if (presenterToView == null)
            return;

        if (archiveLetters.size() == 0)
            presenterToView.retrieveArchiveLettersSuccess(archiveLetters, "No archive letters ");
        else
            presenterToView.retrieveArchiveLettersSuccess
                    (archiveLetters, String.format(Locale.ENGLISH,
                            "You have %1$d archive letters", archiveLetters.size()));

    }

    MVP.ViewToPresenter getViewToPresenter() {
        return this;
    }

}
