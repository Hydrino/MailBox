package com.ninad.ninhydrin.mailbox;


import java.util.ArrayList;

public class MVP {

    public interface ViewToPresenter {
        void retrieveNewLetters(String RecipientID);

        void retrieveReadLetters(String RecipientID);

        void retrieveArchiveLetters(String RecipientID);

        void LettersShownSuccess();

        void ReadLetterShownSuccess();

        void ArchiveLetterShownSuccess();

        void LetterOpened(int index, int id);

        void LetterSwiped(int index, int id);
    }

    interface PresenterToModel {
        void retrieveNewLettersRequest(String RecipientID);

        void retrieveReadLettersRequest(String RecipientID);

        void retrieveArchiveLettersRequest(String RecipientID);

        void unSubscribeClient();

        void markLetterAsRead(int index, int id);

        void markLetterAsArchive(int index, int id);
    }

    interface ModelToPresenter {
        void retrieveNewLettersFailed();

        void retrieveReadLettersFailed();

        void retrieveArchiveLettersFailed();

        void retrieveNewLettersSuccess(ArrayList<Model.Letter> letters);

        void retrieveReadLettersSuccess(ArrayList<Model.Letter> readLetters);

        void retrieveArchiveLettersSuccess(ArrayList<Model.Letter> archiveLetters);


    }

    interface PresenterToView {
        void retrieveNewLetterSuccess(ArrayList<Model.Letter> letters, String message);

        void retrieveNewLettersFailed();

        void retrieveReadLettersSuccess(ArrayList<Model.Letter> readLetters, String message);

        void retrieveArchiveLettersSuccess(ArrayList<Model.Letter> letters, String message);

        void retrieveReadLettersFailed();

        void retrieveArchiveLettersFailed();

        void hideFab();
    }
}
